package br.com.totem.model;

import br.com.totem.controller.response.DispositivoDashResponse;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.controller.response.DispositivoResumeResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "dashboard")
public class Dashboard {

    @Id
    private UUID id;
    @DBRef
    private Cliente cliente;
    private LocalDateTime atualizacao;
    private long usuariosAtivos;
    private long usuariosInativos;
    private DispositivoDashResponse dispositivos;
    private List<DispositivoPorCor> agendas;
    private List<DispositivoPorCor> agendasExecucao;
    private List<DispositivoPorCor> cores;
    private List<LogConexao> logsConexao;
}