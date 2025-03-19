package br.com.totem.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracaoRequest {

    private long id;
    private Float sensibilidadeVibracao;
    private ConexaoRequest conexao;
    private UUID corVibracao;


}
