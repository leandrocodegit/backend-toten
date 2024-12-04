package br.com.totem.controller.response;

import br.com.totem.model.constantes.ModoOperacao;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OperacaoResponse {


    private ModoOperacao modoOperacao;
    private AgendaResponse agenda;
    private CorResponse corTemporizador;
    private LocalDateTime time;
}