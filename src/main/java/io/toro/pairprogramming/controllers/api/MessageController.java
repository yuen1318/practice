package io.toro.pairprogramming.controllers.api;

import java.net.URI;
import java.util.List;

import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.toro.pairprogramming.models.Message;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.services.AuthService;
import io.toro.pairprogramming.services.MessageService;



@CrossOrigin
@RestController
@RequestMapping("api/v1/projects")
public class MessageController {

    private MessageService messageService;
    private AuthService authService;
    private SimpMessagingTemplate template;

    @Autowired
    public MessageController( MessageService messageService, AuthService authService,
            SimpMessagingTemplate template ) {
        this.messageService = messageService;
        this.authService = authService;
        this.template = template;
    }

    @PostMapping(value = "/{projectId}/messages")
    public ResponseEntity sendMessage(@PathVariable(value = "projectId") Long projectId, @RequestBody String message, HttpServletRequest request ) {
        User user = extractUserInfoFromToken( request );
        Message result = messageService.sendMessage( projectId, message ,user );
        URI createdMessageUrl = URI.create( request.getServerName() + ":" + request.getServerPort() + "/api/v1/project/" + result.getProject().getId() + "/message" );
        template.convertAndSend("/listen/projects/" + projectId +"/yuen" ,result);
        return ResponseEntity.created(createdMessageUrl).body( result );
    }

    private User extractUserInfoFromToken( HttpServletRequest request ) {
        String token = authService.getTokenFromHeader(request.getHeader( "Authorization" ));
        return authService.getUserFromToken(token);
    }

    @GetMapping(value = "/{projectId}/messages")
    public ResponseEntity getMessageByProjectId(@PathVariable(value = "projectId") Long projectId){
        List<Message> result = messageService.getAllMessageByProjectId(projectId);
        return ResponseEntity.ok( result );
    }
}
