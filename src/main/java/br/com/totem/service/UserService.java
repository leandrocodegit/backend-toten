package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.request.UserUpdateRequest;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.mapper.ClienteMapper;
import br.com.totem.mapper.UserMapper;
import br.com.totem.model.Cliente;
import br.com.totem.model.Log;
import br.com.totem.model.User;
import br.com.totem.model.constantes.Comando;
import br.com.totem.model.constantes.Role;
import br.com.totem.model.constantes.TipoToken;
import br.com.totem.repository.LogRepository;
import br.com.totem.repository.UserRepository;
import br.com.totem.security.SecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityConfig securityConfiguration;
    private final AuthService authService;
    private final LogRepository logRepository;
    private final ClienteMapper clienteMapper;

    public UserResponse buscarPorEmail(String token, UUID clienteId, String email){
        if(authService.validaPermissao(token, Role.ROOT)){
            return userMapper.toResponse(userRepository.buscarPorEmail(email).orElseThrow(() -> new ExceptionResponse("Não encontrado")));
        }else{
            return userMapper.toResponse(userRepository.buscarPorEmail(clienteId, email).orElseThrow(() -> new ExceptionResponse("Não encontrado")));
        }
    }

    public void atualizarUsuario(UUID clienteId, UserUpdateRequest userRequest, String token){

        if(userRequest.getClienteId() == null){
            throw new ExceptionResponse("Necessário informar um cliente");
        }

        Optional<User> userOptional = Optional.empty();
        if(authService.validaPermissao(token, Role.ROOT)){
           userOptional = userRepository.findById(userRequest.getId());
        }else{
            userOptional = userRepository.findByClienteAndId(clienteId, userRequest.getId());
        }

        if(userOptional.isPresent()){

            if(userOptional.get().getEmail().equals("master")){
                throw new ExceptionResponse("Falha ao atualizar usuário");
            }

            authService.validaPermissaoTrocaSenha(userOptional.get(), token, TipoToken.ACCESS);
            User user = userOptional.get();
            user.setEmail(userRequest.getEmail());
            user.setStatus(true);
            if(authService.validaPermissao(token, Role.ADMIN))
                user.setRoles(userRequest.getRoles());
            if(userRequest.getClienteId() == null)
                userRequest.setClienteId(clienteId);

            if(authService.validaPermissaoCriarCliente(token, Role.ROOT)){
                user.setBusiness(userRequest.getBusiness());
            }

            user.setCliente(Cliente.builder().id(userRequest.getClienteId()).principal(false).build());
            userRepository.save(user);
            logRepository.save(Log.builder()
                    .cor(null)
                    .id(user.getId().toString())
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(user.toString())
                    .mensagem( "Usuário " + user.getEmail() + " foi atualizado")
                    .build());
        }else{
            throw new ExceptionResponse("Operação não permitida");
        }
    }

    public void atualizarSenhaUsuario(UUID clienteId, UserUpdateRequest userRequest, String token){
        Optional<User> userOptional = Optional.empty();
        if(authService.validaPermissao(token, Role.ROOT)){
            userOptional = userRepository.findById(userRequest.getId());
        }else{
            userOptional = userRepository.findByClienteAndId(clienteId, userRequest.getId());
        }

        if(userOptional.isPresent()){

            if(userOptional.get().getEmail().equals("master")){
                throw new ExceptionResponse("Falha ao atualizar usuário");
            }

            AuthService.isStrongPassword(userRequest.getPassword(), userRequest.getConfirmPassword());
            authService.validaPermissaoTrocaSenha(userOptional.get(), token, TipoToken.ACCESS);
            User user = userOptional.get();
            user.setPassword(securityConfiguration.passwordEncoder().encode(userRequest.getPassword()));
            userRepository.save(user);
            logRepository.save(Log.builder()
                    .cor(null)
                    .id(user.getId().toString())
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(user.toString())
                    .mensagem( "Usuário " + user.getEmail() + " foi atualizado")
                    .build());
        }else{
            throw new ExceptionResponse("Operação não permitida");
        }
    }

    public Page<UserResponse> pesquisarUsuarios(UUID clienteId, String pesquisa, Pageable pageable){
        return userRepository.findByNomeAndEmailContaining(clienteId, pesquisa, pageable).map(userMapper::toResponse);
    }
    public Page<UserResponse> listaTodosUsuarios(String token,UUID clienteId, Pageable pageable){
        if(authService.validaPermissao(token, Role.ROOT))
            return userRepository.findAll(pageable).map(userMapper::toResponse);
        return userRepository.listaUsuarios(clienteId, pageable).map(userMapper::toResponse);
    }

    public void removerUsuario(String token, UUID clienteId, UUID id){
        Optional<User> user = Optional.empty();
        if(authService.validaPermissao(token, Role.ROOT))
            user = userRepository.findById(id);
        else user = userRepository.findByClienteAndId(clienteId, id);

        if(!user.isPresent()){
            throw new ExceptionResponse("Usuário não encontrado");
        }

        if(user.get().getEmail().equals("master")){
            throw new ExceptionResponse("Falha ao remover usuário");
        }
        userRepository.deleteById(id);
        logRepository.save(Log.builder()
                .cor(null)
                .id(id.toString())
                .data(LocalDateTime.now())
                .comando(Comando.CONFIGURACAO)
                .descricao("")
                .mensagem( "Usuário " + id.toString() + " foi removido")
                .build());
    }

    public void mudarStatusUsuario(UUID id){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setStatus(!user.getStatus());
            userRepository.save(user);
            logRepository.save(Log.builder()
                    .cor(null)
                    .id(user.getEmail())
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(user.toString())
                    .mensagem( "Usuário " + user.getEmail() + " foi " + (user.getStatus() ? "ativado" : "desativado"))
                    .build());
        }else{
            throw new ExceptionResponse("Usuario já existe");
        }

    }
}
