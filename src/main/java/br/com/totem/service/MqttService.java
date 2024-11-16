package br.com.totem.service;

import br.com.totem.controller.response.ComandoRequest;
import br.com.totem.model.Configuracao;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MqttService {
    @Autowired
    private MessageHandler mqttOutbound;

    public void sendRetainedMessage(String topic, String message, boolean reter) {
        message = message.replaceAll("#", "");
        Message<String> mqttMessage = MessageBuilder.withPayload(message)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.RETAINED, reter)
                .build();

        System.out.println("Comando enviado para: " + message);
        mqttOutbound.handleMessage(mqttMessage);
    }

    public void sendRetainedMessage(String topic, ComandoRequest comandoRequest) {

       String message = new Gson().toJson(comandoRequest);
        message = message.replaceAll("#", "");
        Message<String> mqttMessage = MessageBuilder.withPayload(message)
                .setHeader(MqttHeaders.TOPIC, topic)

                .build();

        System.out.println("Comando enviado para: " + message);
        mqttOutbound.handleMessage(mqttMessage);
    }

    public void removeRetainedMessage(String topic) {
        Message<String> emptyMessage = MessageBuilder.withPayload("") // Mensagem vazia
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.RETAINED, true) // Define a mensagem como retida
                .build();

        mqttOutbound.handleMessage(emptyMessage);
    }
}
