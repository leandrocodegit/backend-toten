package br.com.totem.controller.response;

import br.com.totem.model.DispositivoPorCor;
import br.com.totem.model.Log;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardResponse {

    private long usuariosAtivos;
    private long usuariosInativos;
    private DispositivoDashResponse dispositivos;
    private List<DispositivoPorCor> agendas;
    private List<DispositivoPorCor> agendasExecucao;
    private List<DispositivoPorCor> cores;
    private List<Log> logs;
    private List<LogConexaoResponse> logsConexao;
}
