package br.com.totem.controller.request;

import br.com.totem.model.Dispositivo;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
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
    private CorRequest cor;
    private List<Dispositivo> dispositivos;
    private boolean todos;
}
