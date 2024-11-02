package br.com.totem.mapper;

import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.model.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {


    UserCreateRequest toRequest(User entity);

    User toEntity(UserCreateRequest request);
    UserResponse toResponse(User entity);

}
