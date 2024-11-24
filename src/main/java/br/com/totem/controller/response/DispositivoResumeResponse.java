package br.com.totem.controller.response;

import br.com.totem.model.Temporizador;
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
    private String conexao;
    private CorResponse cor;
    private boolean isTimer;
    private Temporizador temporizador;

    public String getConexao() {
        long differenceInMinutes = Duration.between(ultimaAtualizacao, LocalDateTime.now()).toMinutes();
        if (differenceInMinutes >= 5)
            return "Offline";
        return "Online";
    }

    public boolean isTimer() {
        return TimeUtil.isTime(this);
    }
}
