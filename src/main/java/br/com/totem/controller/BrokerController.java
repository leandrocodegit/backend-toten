package br.com.totem.controller;

import br.com.totem.controller.request.ConfiguracaoSendRequest;
import br.com.totem.service.ComandoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class BrokerController {

    @Autowired
    private ComandoService comandoService;
    private final SimpMessagingTemplate messagingTemplate;

    public BrokerController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/device/configuracao")
    @SendTo("/topic/dashboard")
    public String handleMessage(@Payload String message) {
        // Aqui você pode processar a mensagem recebida
        System.out.println("Mensagem recebida: " + message);

        // Retorne a mensagem ou alguma resposta
        return "Recebido: " + message;
    }

    @MessageMapping("/device")
    public String handleMessageDevice(@Payload ConfiguracaoSendRequest request) {
        // Aqui você pode processar a mensagem recebida
        System.out.println("Mensagem recebida: " + request);

        request.getCor().setResponder(false);
        //if(request.getDevice() != null && !request.getDevice().isEmpty())
        //mqttService.sendRetainedMessage(Topico.DEVICE_RECEIVE + request.getDevice(),request.getCor());
        return "Recebido: " + request;
    }


}
