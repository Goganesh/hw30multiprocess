package ru.otus.homework.message_system;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.ms_client.Message;
import ru.otus.homework.ms_client.MessageType;
import ru.otus.homework.ms_client.MsClient;
import ru.otus.homework.ms_client.MsClientImpl;
import ru.otus.homework.ms_client.common.Serializers;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MsServer {
    private static Logger logger = LoggerFactory.getLogger(MsServer.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Gson gson = new Gson();
    private final int serverPort;
    private final String host;
    private final MessageSystem messageSystem;

    MsServer(int serverPort, String host, MessageSystem messageSystem) {
        this.serverPort = serverPort;
        this.host = host;
        this.messageSystem = messageSystem;
    }

    void start() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            while (!Thread.currentThread().isInterrupted()) {
                logger.info("waiting for client connection");
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> clientHandler(clientSocket));
            }
        } catch (Exception ex) {
            logger.error("error", ex);
        }
        executor.shutdown();
    }

    private void clientHandler(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            MsClient msClient = null;

            while (true) {
                String input = in.readLine();
                if (input != null) {
                    try {
                        Message message = gson.fromJson(input, Message.class);
                        if (message.getType() == MessageType.REGISTER_CLIENT) {
                            msClient = new MsClientImpl(message.getFromClientId(), clientSocket, message.getFrom());
                            messageSystem.addClient(msClient);

                        } else if (message.getType() == MessageType.REGISTER_MESSAGE_CONSUMER) {
                            int serverPort = Serializers.deserialize(message.getPayload(), Integer.class);
                            MessageConsumerImpl messageConsumer = new MessageConsumerImpl(message.getFrom(), new Socket(host, serverPort));
                            messageSystem.addMessageConsumer(messageConsumer);

                        } else if (message.getType() == MessageType.REMOVE_CLIENT) {
                            messageSystem.removeClient(msClient);
                            break;
                        } else {
                            messageSystem.newMessage(message);
                        }
                    } catch (Exception ex) {
                        logger.error("error", ex);
                    }
                }
            }
            clientSocket.close();
        } catch (Exception ex) {
            logger.error("error", ex);
        }
    }
}
