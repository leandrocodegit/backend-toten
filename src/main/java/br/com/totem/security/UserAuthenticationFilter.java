package br.com.totem.security;

import br.com.totem.Exception.ExceptionAuthorization;
import br.com.totem.model.User;
import br.com.totem.model.constantes.Role;
import br.com.totem.model.constantes.TipoToken;
import br.com.totem.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isPublicEndpoint(request)) {

            TipoToken tipoToken = TipoToken.ACCESS;

            String token = recoveryToken(request);
            String requestURI = request.getRequestURI();
            String httpMethod = request.getMethod();

           if(httpMethod.equals("GET") && requestURI.equals("/ws")) {
               Map<String, String[]> queryParams = request.getParameterMap();
               for (Map.Entry<String, String[]> entry : queryParams.entrySet()) {
                   String[] paramValues = entry.getValue();
                   if (!String.join(", ", paramValues).isEmpty()){
                       token = String.join(", ", paramValues);
                       tipoToken = TipoToken.SOCKET;
                       break;
                   }
               }
           }

               if (token != null) {
                   String subject = jwtTokenProvider.getSubjectFromToken(token, tipoToken);
                   User user = userRepository.findByEmail(subject).get();

//                   user.setEmail("admin");
//                   user.setPassword("$2a$10$Ra/VAlbHDFTC0r6wJ6k76uZsIdBvbthCpZHUEdtlvCYJMps/Ntygy");
//                   user.setRoles(Arrays.asList(Role.ADMIN, Role.USER));
                   UserDetailsImpl userDetails = new UserDetailsImpl(user);

                   Authentication authentication =
                           new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());

                   SecurityContextHolder.getContext().setAuthentication(authentication);
               } else {
                   throw new ExceptionAuthorization("O token está ausente.");
               }
           }
        filterChain.doFilter(request, response); // Continua o processamento da requisição
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