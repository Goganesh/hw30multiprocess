package ru.otus.homework.frontend.controllers;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.frontend.service.FrontendService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class UsersDataController {
    private static Logger logger = LoggerFactory.getLogger(UsersDataController.class);

    private final FrontendService frontendService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Gson gson = new Gson();

    public UsersDataController(FrontendService frontendService,
                               SimpMessagingTemplate simpMessagingTemplate) {
        this.frontendService = frontendService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/usersData")
    public void userData() {
        frontendService.getAllUsers(data -> {
            WSMessage messageToSend = new WSMessage(
                    "",  //infoContent
                    gson.toJson(data)  //userDataContent
            );
            logger.info("Response message: {}", messageToSend);
            simpMessagingTemplate.convertAndSend("/usersDataContent/response", messageToSend);
        });
    }
}
