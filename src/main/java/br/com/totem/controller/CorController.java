package br.com.totem.controller;

import br.com.totem.controller.request.CorRequest;
import br.com.totem.controller.request.TemporizadorRequest;
import br.com.totem.controller.response.CorResponse;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.service.CorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cor")
public class CorController {

    @Autowired
    private CorService corService;


    @PostMapping("/duplicar")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> duplicar(@RequestBody CorRequest request) {
        corService.duplicarCor(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{principal}")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<?> salvar(@RequestBody @Valid CorRequest request, @PathVariable boolean principal) {
        corService.salvarCor(request, principal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/temporizar")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<?> salvar(@RequestBody TemporizadorRequest request) {
        corService.salvarCorTemporizada(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> remover(@PathVariable UUID id) {
        corService.removerConfiguracao(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<Page<CorResponse>> listaCores(Pageable pageable) {
        return ResponseEntity.ok(corService.listaTodasCores(pageable));
    }

    @GetMapping("/rapidas")
    @PreAuthorize("hasAnyAuthority('ROLE_OPERADOR','ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<List<CorResponse>> listaCoresRapeidas() {
        return ResponseEntity.ok(corService.listaTodasCoresRapidas());
    }


}
