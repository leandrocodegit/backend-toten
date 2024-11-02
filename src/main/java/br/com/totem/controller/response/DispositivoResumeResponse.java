package br.com.totem.controller.response;

import br.com.totem.model.Configuracao;
import br.com.totem.model.constantes.Comando;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DispositivoResumeResponse {


    private String mac;
    private String nome;

}
