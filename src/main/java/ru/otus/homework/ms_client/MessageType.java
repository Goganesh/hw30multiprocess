package ru.otus.homework.ms_client;

public enum MessageType {

    STORE_USER("StoreUser"),

    ALL_USERS_DATA("AllUsersData"),

    VOID_TECHNICAL_MESSAGE("voidTechnicalMessage"),

    REGISTER_CLIENT("registerClient"),

    REGISTER_MESSAGE_CONSUMER("registerMessageConsumer"),

    REMOVE_CLIENT("removeClient");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
