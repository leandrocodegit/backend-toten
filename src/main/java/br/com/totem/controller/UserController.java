package br.com.totem.controller;

import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.service.AuthService;
import br.com.totem.service.MqttService;
import br.com.totem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @PostMapping()
    public ResponseEntity<TokenResponse> criar(@RequestBody UserCreateRequest request) {
        authService.criarUsuario(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pesquisar/{pesquisa}")
    public ResponseEntity<?> pesquisar(@PathVariable String pesquisa, Pageable pageable) {        ;
        return ResponseEntity.ok(userService.pesquisarUsuarios(pesquisa, pageable));
    }
    @GetMapping()
    public ResponseEntity<?> listaTodosUsuarios(Pageable pageable) {        ;
        return ResponseEntity.ok(userService.listaTodosUsuarios(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerUsuario(@PathVariable UUID id) {
        userService.removerUsuario(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> mudarStatusUsuario(@PathVariable UUID id) {
        userService.mudarStatusUsuario(id);
        return ResponseEntity.accepted().build();
    }
}
