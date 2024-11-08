package br.com.totem.handler;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@MessageEndpoint
public class MqttProcessor {

    @ServiceActivator(inputChannel = "inputChannel", outputChannel = "outputChannel")
    public String processMessage(Message<?> message) {
        System.out.println("Mensagem recebida: " + message.getPayload());
        return "Mensagem Processada";  // A mensagem resultante será enviada para o canal de saída
    }
}
