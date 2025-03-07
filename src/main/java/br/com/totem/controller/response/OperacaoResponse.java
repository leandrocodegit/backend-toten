package br.com.totem.controller.response;

import br.com.totem.model.constantes.ModoOperacao;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperacaoResponse {


    private ModoOperacao modoOperacao;
    private AgendaResponse agenda;
    private CorResponse corTemporizador;
    private CorResponse corVibracao;
    private LocalDateTime time;
}