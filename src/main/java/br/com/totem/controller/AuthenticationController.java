package br.com.totem.controller;

import br.com.totem.controller.request.AuthUserRequest;
import br.com.totem.controller.request.UserUpdateRequest;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.security.JWTTokenProvider;
import br.com.totem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService authService;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody AuthUserRequest request) {
        TokenResponse tokenResponse = authService.authenticateUser(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/refresh")
    public  ResponseEntity<TokenResponse> refresh(@RequestParam("token") String refreshToken) {
        TokenResponse tokenResponse = authService.refreshtoken(refreshToken);
        return ResponseEntity.ok(tokenResponse);

    }

    @PostMapping("/status")
    public ResponseEntity<String> status(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO','ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<String> alteraSenha(@RequestHeader("Authorization") String token, @RequestBody UserUpdateRequest userUpdateRequest) {
        authService.alterarSenha(userUpdateRequest, token);
        return ResponseEntity.ok().build();
    }

    private String generateToken(UserDetails userDetails) {
        return "token"; // Retornar o token gerado
    }
}
