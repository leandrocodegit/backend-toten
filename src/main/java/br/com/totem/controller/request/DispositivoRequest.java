package br.com.totem.controller.request;

import br.com.totem.model.Configuracao;
import br.com.totem.model.constantes.Comando;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
public class DispositivoRequest {

    private String mac;
    private String nome;
    private String latitude;
    private String longitude;
}
