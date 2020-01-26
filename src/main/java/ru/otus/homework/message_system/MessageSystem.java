package ru.otus.homework.message_system;

import ru.otus.homework.ms_client.Message;
import ru.otus.homework.ms_client.MsClient;

public interface MessageSystem {

    void addClient(MsClient msClient);

    void addMessageConsumer(MessageConsumer consumer);

    void removeClient(MsClient msClient);

    boolean newMessage(Message msg);

    void dispose() throws InterruptedException;
}