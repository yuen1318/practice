package io.toro.pairprogramming.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker( MessageBrokerRegistry config ) {
        //every route that has /listen can get message
        config.enableSimpleBroker( "/listen");
        //every route that has /emit can post message
        config.setApplicationDestinationPrefixes( "/emit" );
    }

    @Override
    public void registerStompEndpoints( StompEndpointRegistry registry ) {
        registry.addEndpoint("/server").withSockJS();
    }

    @EventListener
    public void onSocketConnected(SessionConnectedEvent event) throws JsonProcessingException {
//        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        //System.out.println("[Connected] " + stompHeaderAccessor.getSessionId());
//        webSocketService.addUser(stompHeaderAccessor.getSessionId());
//        System.out.println(webSocketService.getUsers());
//        webSocketService.updateUsers();
    }

    @EventListener
    public void onSocketDisconnected(SessionDisconnectEvent event) throws JsonProcessingException {
//        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        //System.out.println("[Disonnected] " + stompHeaderAccessor.getSessionId());
//        webSocketService.removeUser(stompHeaderAccessor.getSessionId());
//        System.out.println(webSocketService.getUsers());
//        webSocketService.updateUsers();
    }
}

