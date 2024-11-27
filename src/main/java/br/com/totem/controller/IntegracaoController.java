package br.com.totem.controller;

import br.com.totem.controller.request.IntegracaoRequest;
import br.com.totem.controller.response.IntegracaoResponse;
import br.com.totem.security.JWTTokenProvider;
import br.com.totem.service.IntegracaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/integracao")
@RequiredArgsConstructor
public class IntegracaoController {

    private final IntegracaoService integracaoService;
    private final JWTTokenProvider jwtTokenProvider;


    @GetMapping
    public ResponseEntity<Page<IntegracaoResponse>> lista(Pageable pageable) {
        return ResponseEntity.ok(integracaoService.listaIntegracoes(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> criarIntegracao(@RequestBody IntegracaoRequest request) {
        integracaoService.criarIntegracao(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{nome}")
    public ResponseEntity<IntegracaoResponse> remover(@PathVariable String nome) {
        integracaoService.remover(nome);
        return ResponseEntity.ok().build();
    }
}
