package br.com.totem.mapper;

import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface UserMapper {


    UserCreateRequest toRequest(User entity);

    User toEntity(UserCreateRequest request);
    @Mappings({
            @Mapping(target = "clienteId", expression = "java(entity.getCliente() != null ? entity.getCliente().getId() : null)"),
            @Mapping(target = "nomeCliente", expression = "java(entity.getCliente() != null ? entity.getCliente().getNome() : null)")
    })
    UserResponse toResponse(User entity);

}
