package br.com.totem.controller;

import br.com.totem.controller.request.CorRequest;
import br.com.totem.controller.request.TemporizadorRequest;
import br.com.totem.controller.response.CorResponse;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.service.CorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cor")
@RequiredArgsConstructor
public class CorController {

    private final CorService corService;


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO','ROLE_ADMIN', 'ROLE_INTEGRACAO', 'ROLE_USER')")
    public ResponseEntity<CorResponse> buscarCor(@RequestHeader("Authorization") String token, @RequestHeader UUID clienteId,@PathVariable UUID id) {
        return ResponseEntity.ok(corService.buscaCor(token, clienteId, id));
    }
    @PostMapping("/duplicar")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> duplicar(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @RequestBody CorRequest request) {
        corService.duplicarCor(token, clienteId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{principal}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<CorResponse> salvar(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @RequestBody @Valid CorRequest request, @PathVariable boolean principal) {        ;
        return ResponseEntity.ok(corService.salvarCor(token, clienteId, request, principal));
    }

    @PostMapping("/vibracao")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<?> salvarVibracaoDispositivo(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @RequestBody @Valid CorRequest request, @PathVariable boolean principal) {
        corService.salvarCorVibracao(token, clienteId,request, principal);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO','ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> remover(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @PathVariable UUID id) {
        corService.removerConfiguracao(token, clienteId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{rapida}/{vibracao}/{exclusiva}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_AVANCADO','ROLE_ADMIN', 'ROLE_INTEGRACAO', 'ROLE_USER')")
    public ResponseEntity<Page<CorResponse>> listaCores(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @PathVariable boolean rapida,@PathVariable boolean vibracao,@PathVariable boolean exclusiva, Pageable pageable) {
        return ResponseEntity.ok(corService.listaTodasCores(token, clienteId, rapida, vibracao, exclusiva, pageable));
    }

}
