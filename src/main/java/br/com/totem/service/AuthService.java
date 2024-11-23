package br.com.totem.service;


import br.com.totem.Exception.ExceptionAuthorization;
import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.AuthUserRequest;
import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.request.UserUpdateRequest;
import br.com.totem.model.Log;
import br.com.totem.model.MessageError;
import br.com.totem.model.User;
import br.com.totem.model.constantes.Comando;
import br.com.totem.model.constantes.Role;
import br.com.totem.model.constantes.TipoToken;
import br.com.totem.repository.LogRepository;
import br.com.totem.repository.UserRepository;
import br.com.totem.security.JWTTokenProvider;
import br.com.totem.security.SecurityConfig;
import br.com.totem.security.UserDetailsImpl;
import br.com.totem.controller.request.AuthenticationRequest;
import br.com.totem.controller.response.TokenResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;
    private final SecurityConfig securityConfiguration;
    private final UserRepository userRepository;
    private final LogRepository logRepository;

    public TokenResponse authenticateUser(AuthUserRequest loginUserDto) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(), loginUserDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new TokenResponse(
                jwtTokenProvider.generateToken(userDetails, TipoToken.ACCESS),
                jwtTokenProvider.generateToken(userDetails, TipoToken.REFRESH), "bearer", 0);
    }

    public TokenResponse refreshtoken(String refreshToken) {

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new ExceptionAuthorization("Token inválido ou expirado.");
        }
        try {
            String subject = jwtTokenProvider.getSubjectFromToken(refreshToken, TipoToken.REFRESH);
            User user = userRepository.findByEmail(subject).get();
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            return new TokenResponse(
                    jwtTokenProvider.generateToken(userDetails, TipoToken.ACCESS),
                    jwtTokenProvider.generateToken(userDetails, TipoToken.REFRESH), "bearer", 0);

        } catch (Exception err) {

        }
        return null;
    }

    public void criarUsuario(UserCreateRequest request) {
        if (!userRepository.findByEmail(request.getEmail()).isPresent()) {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .nome(request.getNome())
                    .email(request.getEmail())
                    .password(securityConfiguration.passwordEncoder().encode(request.getPassword()))
                    .roles(request.getRoles())
                    .status(true)
                    .build();
            userRepository.save(user);
            logRepository.save(Log.builder()
                    .cor(null)
                    .mac(user.getEmail())
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(user.toString())
                    .mensagem( "Novo usuário adicionado")
                    .build());
        } else {
            throw new ExceptionResponse("Usuário já existe");
        }
    }

    public void alterarSenha(UserUpdateRequest request, String token) {

        String subject = jwtTokenProvider.getSubjectFromToken(token, TipoToken.REFRESH);
        Optional<User> userOptional = userRepository.findByEmail(subject);

        if (userOptional.isPresent()) {
            validaPermissaoTrocaSenha(userOptional.get(), token, TipoToken.REFRESH);
            User user = userOptional.get();
            user.setPassword(securityConfiguration.passwordEncoder().encode(request.getPassword()));
            userRepository.save(user);
            logRepository.save(Log.builder()
                    .cor(null)
                    .mac(user.getId().toString())
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(user.toString())
                    .mensagem( "Usuário alterou " + user.getEmail() + " a senha")
                    .build());
        } else {
            throw new ExceptionResponse("Operação não permitida");
        }
    }

    public void validaPermissaoTrocaSenha(User user, String token, TipoToken tipoToken) {

        String subject = jwtTokenProvider.getSubjectFromToken(token.replace("Bearer ", ""), tipoToken);
        Optional<User> userOptional = userRepository.findByEmail(subject);
        if (!userOptional.isPresent() || !subject.equals(user.getEmail()) && userOptional.get().getRoles().stream().noneMatch(role -> role.equals(Role.ADMIN))) {
            throw new ExceptionResponse("Operação não permitida");
        }
    }

    public static void isStrongPassword(String password, String confirmPassword) {

        List<MessageError> messages = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            messages.add(new MessageError("info", "Senha não poder ser vazia"));
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            messages.add(new MessageError("info", "Confirme a senha"));
        }

        if (!password.equals(confirmPassword)) {
            messages.add(new MessageError("error", "As senhas são diferentes"));
        }
        if (password.length() < 8) {
            messages.add(new MessageError("warn", "A senha deve ter pelo menos 8 caracteres."));
        }
        if (!password.matches(".*[a-z].*")) {
            messages.add(new MessageError("warn", "A senha deve conter pelo menos uma letra minúscula"));
        }
        if (!password.matches(".*[A-Z].*")) {
            messages.add(new MessageError("warn", "A senha deve conter pelo menos uma letra maiúscula"));
        }
        if (!password.matches(".*\\d.*")) {
            messages.add(new MessageError("warn", "A senha deve conter pelo menos um dígito"));
        }
        if (!password.matches(".*[@$!%*?&].*")) {
            messages.add(new MessageError("warn", "detail: A senha deve conter pelo menos um caractere especial (@, $, !, %, *, ?, &)"));
        }

        if (!messages.isEmpty()) {
            throw new ExceptionResponse(new Gson().toJson(messages));
        }
    }
}
