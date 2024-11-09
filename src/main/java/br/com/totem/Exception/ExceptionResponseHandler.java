package br.com.totem.Exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mapping.MappingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;


@ControllerAdvice
public class ExceptionResponseHandler {


    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ResponseError> handlerJWTVerificationException(JWTVerificationException ex) {
        return new ResponseEntity<>(ResponseError.builder()
                .message(ex.getMessage())
                .codigo("401")
                .build(), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExceptionSocket.class)
    public ResponseEntity<Error> handleExceptionSocket(ExceptionSocket exception, WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.UNAUTHORIZED, request, exception.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Error> handleJwtException(JwtException exception, WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.UNAUTHORIZED, request, exception.getMessage());
    }
    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<Error> handleJWTDecodeException(JWTDecodeException exception, WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.UNAUTHORIZED, request, exception.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ExceptionAuthorization.class)
    public ResponseEntity<Error> handleExceptionAuthorization(ExceptionAuthorization exception, WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.UNAUTHORIZED, request, exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Error> handleRuntimeException(RuntimeException exception, WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, request, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, request, exception.getMessage());
    }
    @ExceptionHandler(ExceptionResponse.class)
    public ResponseEntity<Error> handleExceptionResponse(ExceptionResponse exception, WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, request, exception.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MappingException.class)
    public ResponseEntity<Error> handleMappingException(ExceptionResponse exception, WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, request, exception.getMessage());
    }

    private ResponseEntity<Error> buildErrorResponse(Exception exception, HttpStatus status, WebRequest request, String message) {
        Error errorDto = new Error(status.value(), message);
        return ResponseEntity.status(status).body(errorDto);
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Error {
        private final int status;
        private final String message;
    }
}
