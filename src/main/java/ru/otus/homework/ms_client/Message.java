package ru.otus.homework.ms_client;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Message implements Serializable {

    public static final Message VOID_MESSAGE = new Message();

    private final UUID id = UUID.randomUUID();
    private final UUID fromClientId;
    private final ClientType from;
    private final ClientType to;
    private final Optional<UUID> sourceMessageId;
    private final MessageType messageType;
    private final int payloadLength;
    private final byte[] payload;

    private Message() {
        this.from = null;
        this.to = null;
        this.sourceMessageId = Optional.empty();
        this.fromClientId = null;
        this.messageType = MessageType.VOID_TECHNICAL_MESSAGE;
        this.payload = new byte[1];
        this.payloadLength = payload.length;
    }

    public Message(ClientType from, ClientType to, Optional<UUID> sourceMessageId,
                   UUID fromClientId, MessageType messageType, byte[] payload) {
        this.from = from;
        this.to = to;
        this.sourceMessageId = sourceMessageId;
        this.fromClientId = fromClientId;
        this.messageType = messageType;
        this.payloadLength = payload.length;
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", sourceMessageId=" + sourceMessageId +
                ", type='" + messageType + '\'' +
                ", payloadLength=" + payloadLength +
                '}';
    }

    public UUID getId() {
        return id;
    }

    public ClientType getFrom() {
        return from;
    }

    public ClientType getTo() {
        return to;
    }

    public UUID getFromClientId() {
        return fromClientId;
    }

    public MessageType getType() {
        return messageType;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public Optional<UUID> getSourceMessageId() {
        return sourceMessageId;
    }
}