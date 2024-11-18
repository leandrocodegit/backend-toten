package br.com.totem.controller;

import br.com.totem.controller.request.ConfiguracaoRequest;
import br.com.totem.controller.request.validacoes.ConfiguracaoCreate;
import br.com.totem.controller.response.ConfiguracaoResponse;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.service.ConfiguracaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/configuracao")
@RequiredArgsConstructor
public class ConfiguracaoController {

    private final ConfiguracaoService configuracaoService;

    @PostMapping("/duplicar")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> duplicar(@RequestBody ConfiguracaoRequest request) {
        configuracaoService.duplicarconfiguracao(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{principal}")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> salvar(@RequestBody @Validated({ConfiguracaoCreate.class}) ConfiguracaoRequest request, @PathVariable boolean principal) {
        configuracaoService.salvarconfiguracao(request, principal);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> remover(@PathVariable UUID id) {
        configuracaoService.removerConfiguracao(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<Page<ConfiguracaoResponse>> listaConfiguracoes(Pageable pageable) {
        return ResponseEntity.ok(configuracaoService.listaTodasConfiguracoes(pageable));
    }

    @GetMapping("/rapidas")
    @PreAuthorize("hasAnyAuthority('ROLE_OPERADOR','ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<List<ConfiguracaoResponse>> listaConfiguracoesRapidas() {
        return ResponseEntity.ok(configuracaoService.listaTodasConfiguracoesRapidas());
    }


}
