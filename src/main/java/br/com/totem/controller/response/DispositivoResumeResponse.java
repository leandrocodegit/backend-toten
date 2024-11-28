package br.com.totem.controller.response;

import br.com.totem.model.Temporizador;
import br.com.totem.model.constantes.StatusConexao;
import br.com.totem.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
public class DispositivoResumeResponse {

    private String mac;
    private String nome;
    private LocalDateTime ultimaAtualizacao;
    private StatusConexao status;
    private CorResponse cor;
    private boolean isTimer;
    private Temporizador temporizador;

    public boolean isTimer() {
        return TimeUtil.isTime(this);
    }
}
