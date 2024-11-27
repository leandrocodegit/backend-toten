package br.com.totem.mapper;

import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.response.IntegracaoResponse;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.model.Integracao;
import br.com.totem.model.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface IntegracaoMapper {

    IntegracaoResponse toResponse(Integracao entity);

}
