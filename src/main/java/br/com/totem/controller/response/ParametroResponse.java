package br.com.totem.controller.response;

import br.com.totem.model.Configuracao;
import br.com.totem.model.constantes.Efeito;
import br.com.totem.model.constantes.TipoCor;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ParametroResponse {

    private int pino;
    private Efeito efeito;
    private int[] cor;
    private List<String> corHexa;
    private int[] correcao;
    private Configuracao configuracao;
}
