package br.com.totem.controller.request;

import br.com.totem.controller.request.validacoes.ConfiguracaoUpdate;
import br.com.totem.model.Configuracao;
import br.com.totem.model.Dispositivo;
import br.com.totem.model.constantes.TipoAgenda;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Validated
public class AgendaRequest {

    private UUID id;
    @NotEmpty(message = "Nome não pode ser vazio")
    private String nome;
    private boolean ativo;
    @NotNull(message = "Data inicio não pode ser vazio")
    private LocalDate inicio;
    @NotNull(message = "Data termino não pode ser vazio")
    private LocalDate termino;
    @NotNull
    private ConfiguracaoRequest configuracao;
    private List<Dispositivo> dispositivos;
    private boolean todos;
}
