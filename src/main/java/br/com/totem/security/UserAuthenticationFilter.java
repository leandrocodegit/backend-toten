package br.com.totem.security;

import br.com.totem.Exception.ExceptionAuthorization;
import br.com.totem.model.User;
import br.com.totem.model.constantes.TipoToken;
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
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            TipoToken tipoToken = TipoToken.ACCESS;
            if (isPublicEndpoint(request)) {
                String token = recoveryToken(request);
                String requestURI = request.getRequestURI();
                String httpMethod = request.getMethod();
                System.out.println(requestURI);

                if (httpMethod.equals("GET") && requestURI.equals("/totem/auth/ws")) {
                    tipoToken = TipoToken.SOCKET;
                }

                if (token != null) {
                    System.out.println("############################ " + token);
                    String subject = jwtTokenProvider.getSubjectFromToken(token, tipoToken);
                    System.out.println("############################ " + subject);
                    User user = userRepository.findByEmail(subject).get();

//                   user.setEmail("admin");
//                   user.setPassword("$2a$10$Ra/VAlbHDFTC0r6wJ6k76uZsIdBvbthCpZHUEdtlvCYJMps/Ntygy");
//                   user.setRoles(Arrays.asList(Role.ADMIN, Role.USER));
                    UserDetailsImpl userDetails = new UserDetailsImpl(user);

                    System.out.println("############################ detalis" + userDetails.getUser().getRoles());
                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());

                    //if(!tipoToken.equals(TipoToken.SOCKET))
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Context");
                } else {
                    System.out.println("Erro");
                    throw new ExceptionAuthorization("O token está ausente.");
                }
            }
          //  if(!tipoToken.equals(TipoToken.SOCKET))
                filterChain.doFilter(request, response);
            System.out.println("chain");
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