package br.com.totem.config;

//import br.com.totem.handler.MqttProcessor;
//import br.com.totem.model.constantes.Topico;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.annotation.IntegrationComponentScan;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.config.EnableIntegration;
//import org.springframework.integration.dsl.IntegrationFlow;
//import org.springframework.integration.handler.MethodInvokingMessageProcessor;
//import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
//import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
//import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
//import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHandler;
//import org.springframework.integration.dsl.IntegrationFlows;


public class MqttConfig {

//    @Value("${mqtt.broker.url}")
//    private String brokerUrl;
//
//    @Value("${mqtt.client.id}")
//    private String clientId;
//
//
//    @Bean
//    public MessageChannel mqttInputChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    public DefaultMqttPahoClientFactory mqttClientFactory() {
//        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
//        factory.setConnectionOptions(connectionOptions());
//        return factory;
//    }
//
//    @Bean
//    public MessageHandler mqttOutbound() {
//        MqttPahoMessageHandler messageHandler =
//                new MqttPahoMessageHandler("mqttOutbound", mqttClientFactory());
//        messageHandler.setAsync(true);
//        return messageHandler;
//    }
//
//    @Bean
//    public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
//
//        MqttPahoMessageDrivenChannelAdapter adapter =
//                new MqttPahoMessageDrivenChannelAdapter(
//                        brokerUrl,
//                        "mqttInbound",
//                        mqttClientFactory(),
//                        Topico.DEVICE_SEND + "#",
//                        Topico.DEVICE_RECEIVE + "#");
//        adapter.setCompletionTimeout(5000);
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(1);
//        adapter.setOutputChannel(mqttInputChannel());
//
//        return adapter;
//    }
//
//
//
//    @Bean
//    public IntegrationFlow mqttInboundFlow() {
//        return IntegrationFlows.from(mqttInbound())
//                .handle(message -> {
//                    System.out.println("Mensagem recebida: " + message.getPayload());
//                })
//                .get();
//    }
//
//    private MqttConnectOptions connectionOptions() {
//        MqttConnectOptions options = new MqttConnectOptions();
//        options.setServerURIs(new String[]{brokerUrl});
//        options.setUserName("broker");
//        options.setPassword("pass2020".toCharArray());
//        options.setMaxReconnectDelay(5000);
//        options.setKeepAliveInterval(60);
//        options.setAutomaticReconnect(true);
//
//        return options;
//    }
}
