package br.com.totem.security;

import br.com.totem.Exception.ExceptionAuthorization;
import br.com.totem.model.constantes.TipoToken;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider {

    private static final String SECRET_KEY = "4Z^XrroxR@dWxqf$mTTKwW$!@#qGr4P";

    private static final String ISSUER = "pizzurg-api"; // Emissor do token

    public String generateToken(UserDetails user, TipoToken tipoToken) {
        try {

            List<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(creationDate())
                    .withExpiresAt(expirationDate(getTime(tipoToken)))
                    .withSubject(user.getUsername())
                    .withClaim("roles", roles)
                    .sign(getAlgorithm(tipoToken));
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Erro ao gerar token.", exception);
        }
    }

    public String getSubjectFromToken(String token, TipoToken tipo) {
        try {
            return JWT.require(getAlgorithm(tipo))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token inválido ou expirado.");
        }catch (Exception exception) {
            throw new JWTVerificationException("Token inválido ou expirado.");
        }
    }

    public Algorithm getAlgorithm(TipoToken tipoToken) {
        switch (tipoToken) {
            case ACCESS -> {
                return Algorithm.HMAC256(SECRET_KEY);
            }
            case REFRESH -> {
                return Algorithm.HMAC512(SECRET_KEY);
            }
            case SOCKET -> {
                return Algorithm.HMAC384(SECRET_KEY);
            }
        }
        return null;
    }

    private long getTime(TipoToken tipoToken) {
        switch (tipoToken) {
            case ACCESS -> {
                return 15;
            }
            default -> {
                return  60;
            }
        }
    }

    private Instant creationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
    }

    private Instant expirationDate(long minutos) {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusMinutes(minutos).toInstant();
    }

    public static Map<String, Claim> getClaims(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);

        return jwt.getClaims();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);

            if (jwt.getExpiresAt() != null && jwt.getExpiresAt().before(new Date())) {
                return false;
            }
            return true;
        } catch (JWTDecodeException exception) {
            throw new ExceptionAuthorization("Erro ao decodificar o token.");
        } catch (JWTVerificationException exception) {
            throw new ExceptionAuthorization("Token inválido.");
        }
    }

}