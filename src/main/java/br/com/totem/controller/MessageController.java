package br.com.totem.controller;

import br.com.totem.controller.request.AuthenticationRequest;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.service.MqttService;
import br.com.totem.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mensagem")
public class MessageController {

    @Autowired
    private MqttService mqttService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @GetMapping()
    public ResponseEntity<TokenResponse> login() {
        messagingTemplate.convertAndSend("/topic", "message");
        mqttService.sendRetainedMessage("test/comandos", "{\n" +
                "  \"comando\": \"CONTADOR\",\n" +
                "  \"cor\": [7,0,223,255,255,0,20],\n" +
                "  \"intensidade\": 255,\n" +
                "  \"leds\" : 14,\n" +
                "  \"velocidade\": 600\n" +
                "}");
        return ResponseEntity.ok().build();
    }



}
