package ru.otus.homework.frontend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.ms_client.ClientType;
import ru.otus.homework.ms_client.Message;
import ru.otus.homework.ms_client.MessageType;
import ru.otus.homework.ms_client.MsClient;
import ru.otus.homework.ms_client.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class FrontendServiceImpl implements FrontendService {
    private static Logger logger = LoggerFactory.getLogger(FrontendServiceImpl.class);
    private final Map<UUID, Consumer<?>> consumerMap = new ConcurrentHashMap<>();
    private final MsClient msClient;
    private final ClientType databaseClient;

    public FrontendServiceImpl(MsClient msClient, ClientType databaseClient) {
        this.msClient = msClient;
        this.databaseClient = databaseClient;

        Message registerMsg = msClient.produceMessage(null, null, MessageType.REGISTER_CLIENT);
        msClient.sendMessage(registerMsg);
    }

    @Override
    public <T> Optional<Consumer<T>> takeConsumer(UUID sourceMessageId, Class<T> tClass) {
        Consumer<T> consumer = (Consumer<T>) consumerMap.remove(sourceMessageId);
        if (consumer == null) {
            logger.warn("consumer not found for:{}", sourceMessageId);
            return Optional.empty();
        }
        return Optional.of(consumer);
    }

    @Override
    public void storeUser(User user, Consumer<String> dataConsumer) {
        Message outMsg = msClient.produceMessage(databaseClient, user, MessageType.STORE_USER);
        consumerMap.put(outMsg.getId(), dataConsumer);
        msClient.sendMessage(outMsg);
    }

    @Override
    public void getAllUsers(Consumer<List<User>> dataConsumer) {
        Message outMsg = msClient.produceMessage(databaseClient, null, MessageType.ALL_USERS_DATA);
        consumerMap.put(outMsg.getId(), dataConsumer);
        msClient.sendMessage(outMsg);
    }
}
