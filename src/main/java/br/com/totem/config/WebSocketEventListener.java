package br.com.totem.config;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketEventListener {


    private static Set<WebSocketSession> sessions = new HashSet<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent  event) {

        System.out.println();
    }
}
