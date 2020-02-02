package ru.otus.homework.database.handlers;

import ru.otus.homework.ms_client.model.User;
import ru.otus.homework.database.service.DBService;
import ru.otus.homework.ms_client.Message;
import ru.otus.homework.ms_client.MessageHandler;
import ru.otus.homework.ms_client.MessageType;
import ru.otus.homework.ms_client.common.Serializers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetAllUsersRequestHandler implements MessageHandler {

    private final DBService dbService;

    public GetAllUsersRequestHandler(DBService dbService) {
        this.dbService = dbService;
    }

    @Override
    public Optional<Message> handle(Message msg, UUID clientId) {
        List<User> users = dbService.getAllUsers();
        return Optional.of(new Message(msg.getTo(), msg.getFrom(), Optional.of(msg.getId()), clientId, MessageType.ALL_USERS_DATA, Serializers.serialize(users)));
    }
}
