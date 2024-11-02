package br.com.totem.mapper;

import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.model.Dispositivo;
import br.com.totem.model.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface DispositivoMapper {


    DispositivoResponse toResponse(Dispositivo entity);

}
