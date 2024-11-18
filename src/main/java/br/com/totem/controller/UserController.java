package br.com.totem.controller;

import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.request.UserUpdateRequest;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.service.AuthService;
import br.com.totem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;


    @GetMapping("/{email}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> pesquisarPorEmail(@PathVariable String email) {        ;
        return ResponseEntity.ok(userService.buscarPorEmail(email));
    }
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> criar(@RequestBody UserCreateRequest request) {
        authService.criarUsuario(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO','ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> atualizar(@RequestHeader("Authorization") String token, @RequestBody UserUpdateRequest request) {
        userService.atualizarUsuario(request, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pesquisar/{pesquisa}")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO', 'ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> pesquisar(@PathVariable String pesquisa, Pageable pageable) {        ;
        return ResponseEntity.ok(userService.pesquisarUsuarios(pesquisa, pageable));
    }
    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO', 'ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> listaTodosUsuarios(Pageable pageable) {        ;
        return ResponseEntity.ok(userService.listaTodosUsuarios(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> removerUsuario(@PathVariable UUID id) {
        userService.removerUsuario(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO', 'ROLE_ADMIN')")
    public ResponseEntity<?> mudarStatusUsuario(@PathVariable UUID id) {
        userService.mudarStatusUsuario(id);
        return ResponseEntity.accepted().build();
    }
}
