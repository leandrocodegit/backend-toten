package br.com.totem.mapper;

import br.com.totem.controller.request.DispositivoRequest;
import br.com.totem.controller.response.ClienteResponse;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.controller.response.DispositivoResumeResponse;
import br.com.totem.model.Cliente;
import br.com.totem.model.Dispositivo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface DispositivoMapper {


    @Mapping(target = "clienteId", expression = "java(entity.getCliente() != null ? entity.getCliente().getId() : null)")
    DispositivoResponse toResponse(Dispositivo entity);

    Dispositivo toEntity(DispositivoRequest request);
    DispositivoResumeResponse toResume(Dispositivo entity);

}
