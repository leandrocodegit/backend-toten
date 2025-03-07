package br.com.totem.mapper;

import br.com.totem.controller.request.ClienteRequest;
import br.com.totem.controller.response.ClienteResponse;
import br.com.totem.controller.response.ClienteResume;
import br.com.totem.model.Cliente;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClienteMapper {


    Cliente toEntity(ClienteRequest request);

    ClienteResponse toResponse(Cliente entity);

    ClienteResume toResume(Cliente entity);

}
