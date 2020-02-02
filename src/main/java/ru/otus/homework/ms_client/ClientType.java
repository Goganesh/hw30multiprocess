package ru.otus.homework.ms_client;

public enum ClientType {

    FRONTEND_SERVICE("frontendService"),

    DATABASE_SERVICE("databaseService");

    private final String value;

    ClientType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
