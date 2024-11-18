package br.com.totem.controller.response;

import br.com.totem.model.constantes.Efeito;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracaoResponse {


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


}
