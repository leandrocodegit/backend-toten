package br.com.totem.controller;

import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.request.UserUpdateRequest;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.service.AuthService;
import br.com.totem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/{email}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO','ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> pesquisarPorEmail(@RequestHeader("Authorization") String token, @PathVariable String email) {        ;
        return ResponseEntity.ok(userService.buscarPorEmail(token, email));
    }
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> criar(@RequestHeader("Authorization") String token, @RequestBody UserCreateRequest request) {
        authService.criarUsuario(token, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO','ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> atualizar(@RequestHeader("Authorization") String token, @RequestBody UserUpdateRequest request) {
        userService.atualizarUsuario(token, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO','ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> alterarSenha(@RequestHeader("Authorization") String token, @RequestBody UserUpdateRequest request) {
        userService.atualizarSenhaUsuario(token, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pesquisar/{pesquisa}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO', 'ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> pesquisar(@RequestHeader("Authorization") String token, @PathVariable String pesquisa, Pageable pageable) {        ;
        return ResponseEntity.ok(userService.pesquisarUsuarios(token, pesquisa, pageable));
    }
    @GetMapping("/lista/{business}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO', 'ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> listaTodosUsuarios(@RequestHeader("Authorization") String token,  @PathVariable boolean business, Pageable pageable) {        ;
        return ResponseEntity.ok(userService.listaTodosUsuarios(token, business, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<?> removerUsuario(@RequestHeader("Authorization") String token, @PathVariable UUID id) {
        userService.removerUsuario(token, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO', 'ROLE_ADMIN')")
    public ResponseEntity<?> mudarStatusUsuario(@PathVariable(required = false) UUID id) {
        userService.mudarStatusUsuario(id);
        return ResponseEntity.accepted().build();
    }
}
