package br.com.totem.mapper;

import br.com.totem.controller.request.ConfiguracaoRequest;
import br.com.totem.controller.response.ConfiguracaoResponse;
import br.com.totem.controller.response.ConfiguracaoResumeResponse;
import br.com.totem.model.Configuracao;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ConfiguracaoMapper {


    Configuracao toEntity(ConfiguracaoRequest request);
    ConfiguracaoResumeResponse toResume(Configuracao entity);
    ConfiguracaoResponse toResponse(Configuracao entity);

}
