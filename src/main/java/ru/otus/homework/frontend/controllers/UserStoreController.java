package ru.otus.homework.frontend.controllers;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.frontend.service.FrontendService;
import ru.otus.homework.ms_client.model.User;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
public class UserStoreController {
    private static Logger logger = LoggerFactory.getLogger(UserStoreController.class);
    private final FrontendService frontendService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Gson gson = new Gson();

    public UserStoreController(FrontendService frontendService,
                               SimpMessagingTemplate simpMessagingTemplate) {
        this.frontendService = frontendService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/userStore")
    public void userStore(WSMessage message) {
        logger.info("Got message: {}", message);
        User user = gson.fromJson(message.getUsersDataContent(), User.class);

        frontendService.storeUser(user, infoMessage -> {
            WSMessage messageToSend = new WSMessage(
                    infoMessage,  //infoContent
                    ""  //userDataContent
            );
            logger.info("Response message: {}", messageToSend);
            simpMessagingTemplate.convertAndSend("/infoMessage/response", messageToSend);
        });
    }
}
