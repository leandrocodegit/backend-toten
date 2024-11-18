package br.com.totem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableFeignClients
public class TotemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TotemApplication.class, args);



		System.out.println("Mensagem retida enviada para o broker MQTT.");
	}

}
