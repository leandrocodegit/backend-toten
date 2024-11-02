package br.com.totem.model;

import br.com.totem.model.constantes.Efeito;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

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
    @Transient
    private boolean responder;

//    public static Configuracao configuracaoPadrao(){
//        return Configuracao.builder()
//                .id(UUID.randomUUID())
//                .nome("Sem nome")
//                .efeito(Efeito.COLORIDO)
//                .cor(new int[]{0,0,255,255,0,0})
//                .primaria("red")
//                .secundaria("blue")
//                .leds(14)
//                .faixa(2)
//                .intensidade(255)
//                .correcao(new int[]{255,255,255})
//                .velocidade(100)
//                .responder(true)
//                .build();
//    }
}
