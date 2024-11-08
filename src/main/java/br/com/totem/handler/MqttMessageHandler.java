package br.com.totem.handler;

import br.com.totem.model.Mensagem;
import br.com.totem.model.constantes.Topico;
import br.com.totem.service.DispositivoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MqttMessageHandler implements MessageHandler {

    @Autowired
    private DispositivoService dispositivoService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, String> clientMap = new ConcurrentHashMap<>();

    @Override
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        UUID clientId = (UUID) message.getHeaders().get("id");
        String topico = (String) message.getHeaders().get("mqtt_receivedTopic");


        if (topico.startsWith(Topico.DEVICE_SEND)) {

            try {
                byte[] bytes = (byte[]) message.getPayload();
                Mensagem payload = objectMapper.readValue(bytes, Mensagem.class);
                payload.setBrockerId(clientId.toString());
                dispositivoService.atualizarDispositivo(payload);
            } catch (Exception erro) {
                System.out.println("Erro ao capturar id");
                erro.printStackTrace();
            }
        }

        System.out.println("Mensagem recebida do cliente " + clientId + ": " + message.getPayload().toString());
    }

    // Método para obter informações de um cliente
    public String getClientInfo(String clientId) {
        return clientMap.get(clientId);
    }
}
