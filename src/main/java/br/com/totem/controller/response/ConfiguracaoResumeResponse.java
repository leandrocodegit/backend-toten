package br.com.totem.controller.response;

import br.com.totem.model.constantes.Efeito;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ConfiguracaoResumeResponse {

    private UUID id;
    private Efeito efeito;
    private int[] cor;
    private String primaria;
    private String secundaria;
    private int leds;
    private int faixa;
    private int intensidade;
    private int[] correcao;
    private int velocidade;
}
