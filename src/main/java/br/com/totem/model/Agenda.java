package br.com.totem.model;

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
@Builder
@Document(collection = "agendas")
public class Agenda {

    @Id
    private UUID id;
    private String nome;
    private boolean ativo;
    private String status;
    private LocalDate execucao;
    private LocalDate inicio;
    private LocalDate termino;
    @DBRef
    private Configuracao configuracao;
    @DBRef
    private List<Dispositivo> dispositivos;
    private boolean todos;
}
