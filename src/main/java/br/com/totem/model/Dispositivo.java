package br.com.totem.model;

import br.com.totem.model.constantes.Comando;
import lombok.Builder;
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
@Builder
@Document(collection = "dispositivos")
public class Dispositivo {

    @Id
    private String mac;
    private String nome;
    private String ip;
    private Integer memoria;
    private String versao;
    private boolean ignorarAgenda;
    private LocalDateTime ultimaAtualizacao;
    private boolean ativo;
    private Comando comando;
    private String latitude;
    private String longitude;
    private UUID brokerId;
    @DBRef
    private Configuracao configuracao;
    @DBRef
    private Agenda agenda;

}
