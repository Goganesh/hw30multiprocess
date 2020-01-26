package ru.otus.homework.database.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.database.service.DBService;
import ru.otus.homework.ms_client.model.User;
import ru.otus.homework.ms_client.Message;
import ru.otus.homework.ms_client.MessageHandler;
import ru.otus.homework.ms_client.MessageType;
import ru.otus.homework.ms_client.common.Serializers;
import java.util.Optional;
import java.util.UUID;

public class StoreUserRequestHandler implements MessageHandler {

    private static Logger logger = LoggerFactory.getLogger(StoreUserRequestHandler.class);
    private final DBService dbService;

    public StoreUserRequestHandler(DBService dbService) {
        this.dbService = dbService;
    }

    @Override
    public Optional<Message> handle(Message msg, UUID clientId) {
        User user = Serializers.deserialize(msg.getPayload(), User.class);
        String info = storeUser(user);
        return Optional.of(new Message(msg.getTo(), msg.getFrom(), Optional.of(msg.getId()), clientId, MessageType.STORE_USER, Serializers.serialize(info)));
    }

    private String storeUser(User user) {
        String message;
        try {
            long id = dbService.saveUser(user);

            message = "User is saved with id = " + id;
        } catch (Exception ex) {
            logger.error("Error: " + ex);
            message = "User is not saved. \n Error: " + ex.getCause();
        }
        return message;
    }
}