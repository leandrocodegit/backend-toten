package br.com.totem.controller.response;

import br.com.totem.model.constantes.Efeito;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class ParametroResponse {

    private UUID id;
    private int pino;
    private Efeito efeito;
    private int[] cor;
    private String primaria;
    private String secundaria;
    private int[] correcao;
    private int velocidade;
}
