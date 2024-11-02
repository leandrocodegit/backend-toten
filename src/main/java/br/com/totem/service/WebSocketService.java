package br.com.totem.service;

import br.com.totem.controller.response.DashboardResponse;
import br.com.totem.controller.response.DispositivoResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessageDashboard(DashboardResponse message) {
        messagingTemplate.convertAndSend("/topic/dashboard", message);
    }
    public void sendMessageDipositivos(List<DispositivoResponse> message) {
        messagingTemplate.convertAndSend("/topic/dispositivos", message);
    }
}
