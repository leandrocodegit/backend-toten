package br.com.totem.controller.response;

import br.com.totem.model.constantes.Efeito;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class ComandoRequest {

    private Efeito efeito;
    private int[] cor;
    private int leds;
    private int faixa;
    private int intensidade;
    private int[] correcao;
    private int velocidade;
    private boolean responder;
}
