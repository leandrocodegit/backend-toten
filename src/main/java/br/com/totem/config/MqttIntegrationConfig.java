package br.com.totem.config;

import br.com.totem.handler.MessageSmart;
import br.com.totem.model.constantes.Topico;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.Mqttv5PahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.converter.SimpleMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.SmartMessageConverter;

import java.util.Arrays;

@Configuration
@EnableIntegration
public class MqttIntegrationConfig {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    private String topicos[] = {Topico.DEVICE_SEND + "#",
            Topico.DEVICE_RECEIVE + "#"};
    @Bean
    public Mqttv5PahoMessageDrivenChannelAdapter mqttInbound() {
        Mqttv5PahoMessageDrivenChannelAdapter adapter =
                new Mqttv5PahoMessageDrivenChannelAdapter(connectionOptions(), clientId, topicos);
        adapter.setMessageConverter(new MessageSmart());
        adapter.setQos(1);
        adapter.setCompletionTimeout(5000);
        adapter.setOutputChannel(mqttInputChannel());

        return adapter;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageHandler mqttOutbound() {
        Mqttv5PahoMessageHandler messageHandler =
                new Mqttv5PahoMessageHandler(connectionOptions(), "mqttOutbound");
        messageHandler.setAsync(true);
        return messageHandler;
    }

    @Bean
    public MessageChannel outputChannel() {
        return new DirectChannel();  // Canal de saída
    }


    private MqttConnectionOptions connectionOptions() {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setServerURIs(new String[]{brokerUrl});
        options.setUserName("broker");
        options.setPassword("pass2020".getBytes());
        options.setMaxReconnectDelay(5000);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);

        return options;
    }
}
