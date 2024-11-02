package br.com.totem.model;

import br.com.totem.model.constantes.Comando;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "logs")
public class Log {

    private LocalDateTime data;
    private String descricao;
    private Comando comando;
    private String usuario;
    private String mensagem;
    private Configuracao configuracao;

}
