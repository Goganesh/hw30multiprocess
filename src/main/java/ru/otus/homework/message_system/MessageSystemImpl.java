package ru.otus.homework.message_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.ms_client.ClientType;
import ru.otus.homework.ms_client.Message;
import ru.otus.homework.ms_client.MsClient;
import lombok.SneakyThrows;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class MessageSystemImpl implements MessageSystem {
    private static Logger logger = LoggerFactory.getLogger(MessageSystemImpl.class);
    private static final int MESSAGE_QUEUE_SIZE = 1_000;
    private static final int MSG_HANDLER_THREAD_LIMIT = 2;

    private final AtomicBoolean runFlag = new AtomicBoolean(true);

    private final Map<UUID, MsClient> clientMap = new ConcurrentHashMap<>();
    private final Map<UUID, MessageConsumer> msgConsumerMap = new ConcurrentHashMap<>();

    private final BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(MESSAGE_QUEUE_SIZE);

    private final ExecutorService msgProcessor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("msg-processor-thread");
        return thread;
    });

    private final ExecutorService msgHandler = Executors.newFixedThreadPool(MSG_HANDLER_THREAD_LIMIT, new ThreadFactory() {
        private final AtomicInteger threadNameSeq = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("msg-handler-thread-" + threadNameSeq.incrementAndGet());
            return thread;
        }
    });

    public MessageSystemImpl() {
        msgProcessor.submit(this::msgProcessor);
    }

    private void msgProcessor() {
        logger.info("msgProcessor started");
        while (runFlag.get()) {
            try {
                Message msg = messageQueue.take();
                if (msg == Message.VOID_MESSAGE) {
                    logger.info("received the stop message");
                } else {
                    clientMap.values().stream()
                            .filter(client -> client.getType() == msg.getTo())
                            .findAny()
                            .ifPresent(
                                    clientTo -> msgHandler.submit(() -> handleMessage(clientTo, msg)));
                }
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        msgHandler.submit(this::messageHandlerShutdown);
        logger.info("msgProcessor finished");
    }

    private void messageHandlerShutdown() {
        msgHandler.shutdown();
        logger.info("msgHandler has been shut down");
    }

    private void handleMessage(MsClient msClient, Message message) {
        try {
            //if a message to be sent to DB, only one (any) server must get it (otherwise duplicate data is stored)
            if (msClient.getType() == ClientType.DATABASE_SERVICE) {
                msgConsumerMap.values()
                        .stream()
                        .filter(server -> server.getType() == msClient.getType())
                        .findAny()
                        .ifPresent(
                                server -> server.sendMessage(message));

                //if a message to be sent to Frontend, both servers must get it, as only one of them has the needed dataConsumer for this message.
                //Currently it's not known which one.
            } else if (msClient.getType() == ClientType.FRONTEND_SERVICE) {
                msgConsumerMap.values()
                        .stream()
                        .filter(server -> server.getType() == msClient.getType())
                        .forEach(server -> server.sendMessage(message));
            } else {
                logger.warn("Server with type {} not found", msClient.getType());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            logger.error("message:{}", msClient);
        }
    }

    private void insertStopMessage() throws InterruptedException {
        boolean result = messageQueue.offer(Message.VOID_MESSAGE);
        while (!result) {
            Thread.sleep(100);
            result = messageQueue.offer(Message.VOID_MESSAGE);
        }
    }

    @Override
    public void addClient(MsClient msClient) {
        logger.info("new client: {}", msClient);
        if (clientMap.containsKey(msClient.getId())) {
            throw new IllegalArgumentException(msClient + " already exists");
        }
        clientMap.put(msClient.getId(), msClient);
    }

    @SneakyThrows
    @Override
    public void addMessageConsumer(MessageConsumer consumer) {
        logger.info("new server: {}", consumer);
        msgConsumerMap.put(consumer.getId(), consumer);
    }

    @SneakyThrows
    @Override
    public void removeClient(MsClient msClient) {
        MsClient removedClient = clientMap.remove(msClient.getId());
        if (removedClient == null) {
            logger.warn("client not found: {}", msClient);
        } else {
            logger.info("removed client: {}", removedClient);
        }
    }

    @Override
    public boolean newMessage(Message msg) {
        if (runFlag.get()) {
            logger.info("Adding message to queue: {}", msg);
            return messageQueue.offer(msg);
        } else {
            logger.warn("MS is being shutting down... rejected:{}", msg);
            return false;
        }
    }

    @Override
    public void dispose() throws InterruptedException {
        runFlag.set(false);
        insertStopMessage();
        msgProcessor.shutdown();
        msgHandler.awaitTermination(60, TimeUnit.SECONDS);
    }
}