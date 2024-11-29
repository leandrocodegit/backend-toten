package br.com.totem.model;

import br.com.totem.model.constantes.StatusConexao;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "conexoes")
public class Conexao {

    @Id
    private String mac;
    private LocalDateTime ultimaAtualizacao;
    private StatusConexao status;

}