package br.com.totem.controller.request;

import br.com.totem.model.Configuracao;
import br.com.totem.model.Dispositivo;
import br.com.totem.model.constantes.TipoAgenda;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AgendaRequest {


    private UUID id;
    private String nome;
    private boolean ativo;
    private LocalDate inicio;
    private LocalDate termino;
    private ConfiguracaoRequest configuracao;
    private List<Dispositivo> dispositivos;
    private boolean todos;
}
