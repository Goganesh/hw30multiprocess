package ru.otus.homework.ms_client;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.ms_client.common.Serializers;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MsClientImpl implements MsClient {

    private static Logger logger = LoggerFactory.getLogger(MsClientImpl.class);
    private final UUID clientId;

    private final ClientType clientType;
    private final Map<MessageType, MessageHandler> handlers = new ConcurrentHashMap<>();
    private final Socket clientSocket;
    private final Gson gson = new Gson();

    public MsClientImpl(Socket clientSocket, ClientType clientType) {
        this.clientId = UUID.randomUUID();
        this.clientSocket = clientSocket;
        this.clientType = clientType;
    }

    public MsClientImpl(UUID clientId, Socket clientSocket, ClientType clientType) {
        this.clientId = clientId;
        this.clientSocket = clientSocket;
        this.clientType = clientType;
    }

    public UUID getId() {
        return clientId;
    }

    @Override
    public void addHandler(MessageType type, MessageHandler requestHandler) {
        this.handlers.put(type, requestHandler);
    }

    @Override
    public ClientType getType() {
        return clientType;
    }

    @Override
    public void sendMessage(Message message) {
        try {
            logger.info("Sending message: {}", message);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            out.println(gson.toJson(message));
            out.flush();
        } catch (Exception ex) {
            logger.error("error", ex);
        }
    }

    @Override
    public void handle(Message msg) {
        logger.info("new message:{}", msg);
        try {
            MessageHandler requestHandler = handlers.get(msg.getType());
            if (requestHandler != null) {
                requestHandler.handle(msg, clientId).ifPresent(this::sendMessage);
            } else {
                logger.error("handler not found for the message type:{}", msg.getType());
            }
        } catch (Exception ex) {
            logger.error("msg:" + msg, ex);
        }
    }

    @Override
    public <T> Message produceMessage(ClientType to, T data, MessageType msgType) {
        return new Message(clientType, to, null, clientId, msgType, Serializers.serialize(data));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MsClientImpl msClient = (MsClientImpl) o;
        return Objects.equals(clientType, msClient.clientType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientType);
    }

    @Override
    public String toString() {
        return "MsClientImpl{" +
                "clientId=" + clientId +
                ", clientType=" + clientType +
                '}';
    }
}
