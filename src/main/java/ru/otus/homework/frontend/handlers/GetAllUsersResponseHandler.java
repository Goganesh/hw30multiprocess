package ru.otus.homework.frontend.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.frontend.service.FrontendService;
import ru.otus.homework.ms_client.Message;
import ru.otus.homework.ms_client.MessageHandler;
import ru.otus.homework.ms_client.common.Serializers;
import ru.otus.homework.ms_client.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class GetAllUsersResponseHandler implements MessageHandler {
    private static Logger logger = LoggerFactory.getLogger(GetAllUsersResponseHandler.class);
    private final FrontendService frontendService;

    public GetAllUsersResponseHandler(FrontendService frontendService) {

        this.frontendService = frontendService;
    }

    @Override
    public Optional<Message> handle(Message msg, UUID clientId) {
        logger.info("new message:{}", msg);
        try {
            List<User> users = Serializers.deserialize(msg.getPayload(), List.class);
            UUID sourceMessageId = msg.getSourceMessageId().orElseThrow(() -> new RuntimeException("Not found sourceMsg for message:" + msg.getId()));
            frontendService.takeConsumer(sourceMessageId, List.class).ifPresent(consumer -> consumer.accept(users));

        } catch (Exception ex) {
            logger.error("msg:" + msg, ex);
        }
        return Optional.empty();
    }
}
