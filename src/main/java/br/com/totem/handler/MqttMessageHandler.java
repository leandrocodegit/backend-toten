package br.com.totem.handler;

import br.com.totem.model.Mensagem;
import br.com.totem.model.constantes.Topico;
import br.com.totem.service.DispositivoService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MqttMessageHandler implements MessageHandler {

   @Autowired
   private DispositivoService dispositivoService;
    private final ConcurrentHashMap<String, String> clientMap = new ConcurrentHashMap<>();

    @Override
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        UUID clientId = (UUID) message.getHeaders().get("id");
        String topico = (String) message.getHeaders().get("mqtt_receivedTopic");

        Mensagem payload = new Gson().fromJson(message.getPayload().toString(), Mensagem.class);

        if (topico.startsWith(Topico.DEVICE_SEND)){
            dispositivoService.atualizarDispositivo(payload);
        }

        System.out.println("Mensagem recebida do cliente " + clientId + ": " + payload);
    }

    // Método para obter informações de um cliente
    public String getClientInfo(String clientId) {
        return clientMap.get(clientId);
    }
}
