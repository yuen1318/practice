package io.toro.pairprogramming.controllers.websocket;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import io.toro.pairprogramming.models.Message;

@Controller
public class MessageSocketController {
    @SendTo("/listen/project/{projectId}/yuen")//emit and broadcast
    public Message getProjectMessage(Message message)  {
        return message;
    }
}
