package br.com.totem.model;

import br.com.totem.model.constantes.Efeito;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "configuracoes")
public class Configuracao {


    @Id
    private UUID id;
    private String nome;
    private Efeito efeito;
    private int[] cor;
    private String primaria;
    private String secundaria;
    private int leds;
    private int faixa;
    private int intensidade;
    private int[] correcao;
    private int velocidade;
    private long time;
    private boolean rapida;
    @Transient
    private boolean responder;

}
