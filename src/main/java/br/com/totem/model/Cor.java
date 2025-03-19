package br.com.totem.model;

import br.com.totem.model.constantes.Efeito;
import br.com.totem.model.constantes.TipoCor;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "cores")
public class Cor {

    @Id
    private UUID id;
    @DBRef
    private Cliente cliente;
    private String nome;
    private long time;
    private int quantidadePinos;
    private boolean rapida;
    private Boolean vibracao;
    private boolean exclusiva;
    private int velocidade;
    private List<Parametro> parametros;
    @Transient
    private boolean responder;

    public static Cor padrao(){
        return Cor.builder()
                .id(UUID.randomUUID())
                .nome("Padrão")
                .quantidadePinos(0)
                .rapida(false)
                .time(0)
                .velocidade(100)
                .vibracao(false)
                .exclusiva(true)
                .responder(false)
                .parametros(List.of(
                        Parametro.builder()
                                .cor(new int[] {255,0,0,0,255,0,0,0,255})
                                .correcao(new int[] {255,255,255})
                                .corHexa(List.of("red","green","blue"))
                                .pino(1)
                                .efeito(Efeito.COLORIDO)
                                .configuracao(Configuracao.builder()
                                        .tipoCor(TipoCor.RGB)
                                        .leds(10)
                                        .faixa(2)
                                        .intensidade(255)
                                        .ativo(true).build())
                                .build()
                ))
                .build();

    }

}
