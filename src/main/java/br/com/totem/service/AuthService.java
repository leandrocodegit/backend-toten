package br.com.totem.service;


import br.com.totem.Exception.ExceptionAuthorization;
import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.AuthUserRequest;
import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.model.User;
import br.com.totem.model.constantes.Role;
import br.com.totem.model.constantes.TipoToken;
import br.com.totem.repository.UserRepository;
import br.com.totem.security.JWTTokenProvider;
import br.com.totem.security.SecurityConfig;
import br.com.totem.security.UserDetailsImpl;
import br.com.totem.controller.request.AuthenticationRequest;
import br.com.totem.controller.response.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private SecurityConfig securityConfiguration;
    @Autowired
    private UserRepository userRepository;

    public TokenResponse authenticateUser(AuthUserRequest loginUserDto) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(), loginUserDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new TokenResponse(
                jwtTokenProvider.generateToken(userDetails.getUser().getEmail(), TipoToken.ACCESS),
                jwtTokenProvider.generateToken(userDetails.getUser().getEmail(), TipoToken.REFRESH),
                jwtTokenProvider.generateToken(userDetails.getUser().getEmail(), TipoToken.SOCKET), "bearer", 0);    }

    public TokenResponse refreshtoken(String refreshToken) {

        if(!jwtTokenProvider.validateRefreshToken(refreshToken)){
            throw new ExceptionAuthorization("Token inválido ou expirado.");
        }
        return new TokenResponse(
                jwtTokenProvider.generateToken("admin", TipoToken.ACCESS),
                jwtTokenProvider.generateToken("admin", TipoToken.REFRESH),
                jwtTokenProvider.generateToken("admin", TipoToken.SOCKET), "bearer", 0);
    }

    public void criarUsuario(UserCreateRequest request) {
        if(!userRepository.findByEmail(request.getEmail()).isPresent()){
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .nome(request.getNome())
                    .email(request.getEmail())
                    .password(securityConfiguration.passwordEncoder().encode(request.getEmail()))
                    .roles(request.getRoles())
                    .status(true)
                    .build();
            userRepository.save(user);
        }else{
            throw new ExceptionResponse("Usuário já existe");
        }
    }
}
