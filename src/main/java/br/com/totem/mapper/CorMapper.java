package br.com.totem.mapper;

import br.com.totem.controller.request.CorRequest;
import br.com.totem.controller.response.CorResponse;
import br.com.totem.model.Cor;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CorMapper {


    Cor toEntity(CorRequest request);
    CorResponse toResume(Cor entity);
    CorResponse toResponse(Cor entity);

}
