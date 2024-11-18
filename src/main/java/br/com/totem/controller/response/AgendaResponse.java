package br.com.totem.controller.response;

import br.com.totem.model.constantes.TipoAgenda;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AgendaResponse {


    private UUID id;
    private String nome;
    private boolean ativo;
    private String status;
    private TipoAgenda tipoAgenda;
    private LocalDate execucao;
    private LocalDate inicio;
    private LocalDate termino;
    private ConfiguracaoResumeResponse configuracao;
    private List<DispositivoResumeResponse> dispositivos;
    private boolean todos;

    public String getStatus() {
        if (execucao != null && LocalDate.now().compareTo(execucao) >= 0 && Boolean.TRUE.equals(ativo))
            return "Executada";

        if (Boolean.FALSE.equals(ativo))
            return "Parada";

        if (Boolean.TRUE.equals(ativo)) {
            if (termino != null && termino.compareTo(LocalDate.now()) < 0)
                return "Expirada";
            return "Aguardando";
        }
        return status;
    }
}
