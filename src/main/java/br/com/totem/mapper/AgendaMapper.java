package br.com.totem.mapper;

import br.com.totem.controller.request.AgendaRequest;
import br.com.totem.controller.request.ConfiguracaoRequest;
import br.com.totem.controller.response.AgendaResponse;
import br.com.totem.model.Agenda;
import br.com.totem.model.Configuracao;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AgendaMapper {


    Agenda toEntity(AgendaRequest request);
    AgendaResponse toResponse(Agenda entity);

}
