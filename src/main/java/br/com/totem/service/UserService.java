package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.request.UserUpdateRequest;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.mapper.UserMapper;
import br.com.totem.model.User;
import br.com.totem.model.constantes.TipoToken;
import br.com.totem.repository.UserRepository;
import br.com.totem.security.SecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityConfig securityConfiguration;
    private final AuthService authService;

    public UserResponse buscarPorEmail(String email){
        return userMapper.toResponse(userRepository.findByEmail(email).orElseThrow());
    }
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

    public void atualizarUsuario(UserUpdateRequest userRequest, String token){

        Optional<User> userOptional = userRepository.findById(userRequest.getId());
        if(userOptional.isPresent()){

            AuthService.isStrongPassword(userRequest.getPassword(), userRequest.getConfirmPassword());
            authService.validaPermissaoTrocaSenha(userOptional.get(), token, TipoToken.ACCESS);
            User user = userOptional.get();
            user.setEmail(userRequest.getEmail());
            user.setPassword(securityConfiguration.passwordEncoder().encode(userRequest.getPassword()));
            user.setStatus(true);
            userRepository.save(user);
        }else{
            throw new ExceptionResponse("Operação não permitida");
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
