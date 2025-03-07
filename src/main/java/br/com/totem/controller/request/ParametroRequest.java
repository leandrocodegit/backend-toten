package br.com.totem.controller.request;

import br.com.totem.model.Configuracao;
import br.com.totem.model.constantes.Efeito;
import br.com.totem.model.constantes.TipoCor;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ParametroRequest {

    private int pino;
    private Efeito efeito;
    private int[] cor;
    private List<String> corHexa;
    private int[] correcao;
    private Configuracao configuracao;
}
