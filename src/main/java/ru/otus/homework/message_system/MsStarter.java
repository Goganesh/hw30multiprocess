package ru.otus.homework.message_system;

public class MsStarter {

    private static final int MS_SERVER_PORT = 8081;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        MessageSystem messageSystem = new MessageSystemImpl();
        new MsServer(MS_SERVER_PORT, HOST, messageSystem).start();
    }

}