package br.com.totem.security;

import br.com.totem.Exception.ExceptionAuthorization;
import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.model.Integracao;
import br.com.totem.model.User;
import br.com.totem.model.constantes.Role;
import br.com.totem.model.constantes.TipoToken;
import br.com.totem.repository.IntegracaoRepository;
import br.com.totem.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final IntegracaoRepository integracaoRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            TipoToken tipoToken = TipoToken.ACCESS;
            if (isPublicEndpoint(request)) {
                String token = recoveryToken(request);

                String requestURI = request.getRequestURI();
                String httpMethod = request.getMethod();

                if (httpMethod.equals("GET") && requestURI.equals("/totem/auth/valid")) {
                    tipoToken = TipoToken.COMANDO;
                }

                if (token != null) {

                    UserDetailsImpl userDetails = null;
                    String subject = null;
                    if(requestURI.equals("/totem/auth/valid/integracao")){
                         subject = jwtTokenProvider.getSubjectFromToken(token, TipoToken.COMANDO);
                        Integracao integracao = integracaoRepository.findByClientId(subject)
                                .orElseThrow(() -> new ExceptionResponse("Usuário não encontrado"));

                        User user = User.builder()
                                .email(integracao.getClientId())
                                .roles(Collections.singletonList(Role.INTEGRACAO))
                                .build();

                        userDetails = new UserDetailsImpl(user);
                    }else {
                        subject = jwtTokenProvider.getSubjectFromToken(token, tipoToken);
                        User user = userRepository.findByEmail(subject).get();
                        userDetails = new UserDetailsImpl(user);
                    }

                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    System.out.println("Erro");
                    throw new ExceptionAuthorization("O token está ausente.");
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception err) {
            err.printStackTrace();
            throw new ExceptionAuthorization("O token inválido");
        }
    }

    private String recoveryToken(HttpServletRequest request) {

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return !Arrays.stream(SecurityConfig.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED).anyMatch(path -> {
            return requestURI.equals(path) ||
                    requestURI.matches(path + "/.*");
        });
    }

}