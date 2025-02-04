package br.com.totem.controller.request;

import br.com.totem.model.Parametro;
import br.com.totem.model.constantes.Efeito;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CorRequest {

    private UUID id;
    private String nome;
    private long time;
    private int quantidadePinos;
    private boolean rapida;
    private List<Parametro> parametros;
    @Transient
    private boolean responder;
    private String mac;

}
