package ru.otus.homework.message_system;

import ru.otus.homework.ms_client.ClientType;
import ru.otus.homework.ms_client.Message;

import java.util.UUID;

public interface MessageConsumer {

    UUID getId();

    ClientType getType();

    void sendMessage(Message message);

}
