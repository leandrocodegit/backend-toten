package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.mapper.UserMapper;
import br.com.totem.model.User;
import br.com.totem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public void criarUsuario(UserCreateRequest userRequest){

        if(!userRepository.findByEmail(userRequest.getEmail()).isPresent()){
            User user = userMapper.toEntity(userRequest);
            user.setId(UUID.randomUUID());
            user.setStatus(true);
            userRepository.save(user);
        }else{
            throw new ExceptionResponse("Usuario já existe");
        }
    }

    public Page<UserResponse> pesquisarUsuarios(String pesquisa, Pageable pageable){
        return userRepository.findByNomeAndEmailContaining(pesquisa, pageable).map(userMapper::toResponse);
    }
    public Page<UserResponse> listaTodosUsuarios(Pageable pageable){
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    public void removerUsuario(UUID id){
        userRepository.deleteById(id);
    }

    public void mudarStatusUsuario(UUID id){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setStatus(!user.getStatus());
            userRepository.save(user);
        }else{
            throw new ExceptionResponse("Usuario já existe");
        }

    }
}
