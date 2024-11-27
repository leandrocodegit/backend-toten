package br.com.totem.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@Builder
@Document(collection = "integracoes")
public class Integracao {

    @Id
    private String nome;
    private String clientId;
    private String secret;
    private boolean status;
}
