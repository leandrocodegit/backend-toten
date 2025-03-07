package br.com.totem.model;

import br.com.totem.model.constantes.ModoOperacao;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Builder
@Document(collection = "agendas")
public class Agenda {


    @Id
    private UUID id;
    @DBRef
    private Cliente cliente;
    private String nome;
    private boolean ativo;
    private String status;
    private LocalDateTime execucao;
    private LocalDateTime inicio;
    private LocalDateTime termino;
    @DBRef
    private Cor cor;
    private List<Long> dispositivos;
    private boolean todos;
}

