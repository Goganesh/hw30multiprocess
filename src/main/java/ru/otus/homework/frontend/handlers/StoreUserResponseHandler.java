package ru.otus.homework.frontend.handlers;

import ru.otus.homework.frontend.service.FrontendService;
import ru.otus.homework.ms_client.Message;
import ru.otus.homework.ms_client.MessageHandler;
import ru.otus.homework.ms_client.common.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import java.util.UUID;


public class StoreUserResponseHandler implements MessageHandler {
    private static Logger logger = LoggerFactory.getLogger(StoreUserResponseHandler.class);

    private final FrontendService frontendService;

    public StoreUserResponseHandler(FrontendService frontendService) {

        this.frontendService = frontendService;
    }

    @Override
    public Optional<Message> handle(Message msg, UUID clientId) {
        logger.info("new message:{}", msg);
        try {
            String infoMessage = Serializers.deserialize(msg.getPayload(), String.class);
            UUID sourceMessageId = msg.getSourceMessageId().orElseThrow(() -> new RuntimeException("Not found sourceMsg for message:" + msg.getId()));
            frontendService.takeConsumer(sourceMessageId, String.class).ifPresent(consumer -> consumer.accept(infoMessage));

        } catch (Exception ex) {
            logger.error("msg:" + msg, ex);
        }
        return Optional.empty();
    }

}
