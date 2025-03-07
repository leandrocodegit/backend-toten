package br.com.totem.model;

import br.com.totem.model.constantes.Comando;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@Builder
@Document(collection = "dispositivos")
public class Dispositivo {

    @Id
    private long id;
    @DBRef
    private Cliente cliente;
    private String nome;
    private String ip;
    private Integer memoria;
    private String versao;
    private boolean ignorarAgenda;
    private boolean ativo;
    private boolean permiteComando;
    private Comando comando;
    private String latitude;
    private String longitude;
    private String brokerId;
    private Endereco endereco;
    private String enderecoCompleto;
    private Float sensibilidadeVibracao;
    private Integer tempoAtividade;
    private String corVibracao;
    @DBRef
    private Operacao operacao;
    @DBRef
    private Conexao conexao;
    @DBRef
    private Cor cor;

}
