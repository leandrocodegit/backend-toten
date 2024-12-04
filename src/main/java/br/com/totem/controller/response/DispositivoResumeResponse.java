package br.com.totem.controller.response;

import br.com.totem.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispositivoResumeResponse {

    private String mac;
    private String nome;
    private ConexaoResponse conexao;
    private CorResponse cor;
    private boolean isTimer;
    private OperacaoResponse operacao;

    public boolean isTimer() {
        return TimeUtil.isTime(this);
    }
}
