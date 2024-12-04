package br.com.totem.model;

import br.com.totem.model.constantes.ModoOperacao;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Operacao {

    @Id
    private String mac;
    private ModoOperacao modoOperacao;
    private Agenda agenda;
    private Cor corTemporizador;
    private LocalDateTime time;
}