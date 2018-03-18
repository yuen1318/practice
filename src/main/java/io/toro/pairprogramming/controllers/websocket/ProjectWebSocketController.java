package io.toro.pairprogramming.controllers.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.toro.pairprogramming.models.Message;
import java.io.IOException;
import java.util.Map;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.socket.WebSocketHttpHeaders;

@Controller
public class ProjectWebSocketController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("/edit")
    @SendTo("/listen/edit")
    public String edit(String content) throws InterruptedException, IOException {
        //Thread.sleep(1000);
        Map<String, Object> jsonMap = objectMapper.readValue(content,
            new TypeReference<Map<String,Object>>(){});
        return objectMapper.writeValueAsString(jsonMap);
    }

}
