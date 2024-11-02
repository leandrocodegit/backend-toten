package br.com.totem;

import br.com.totem.service.MqttService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TotemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TotemApplication.class, args);



		System.out.println("Mensagem retida enviada para o broker MQTT.");
	}

}
