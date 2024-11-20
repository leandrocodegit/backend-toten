package br.com.totem.mapper;

import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.controller.response.DispositivoResumeResponse;
import br.com.totem.model.Dispositivo;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface DispositivoMapper {


    DispositivoResponse toResponse(Dispositivo entity);
    DispositivoResumeResponse toResume(Dispositivo entity);

}
