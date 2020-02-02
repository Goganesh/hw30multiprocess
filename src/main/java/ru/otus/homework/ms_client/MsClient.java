package ru.otus.homework.ms_client;

import java.util.UUID;

public interface MsClient {

    UUID getId();

    void addHandler(MessageType type, MessageHandler requestHandler);

    void sendMessage(Message msg);

    void handle(Message msg);

    ClientType getType();

    <T> Message produceMessage(ClientType to, T data, MessageType msgType);

}
