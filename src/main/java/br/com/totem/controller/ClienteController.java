package br.com.totem.controller;

import br.com.totem.controller.request.ClienteRequest;
import br.com.totem.controller.response.ClienteResponse;
import br.com.totem.controller.response.ClienteResume;
import br.com.totem.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/detalhes")
    public ResponseEntity<ClienteResume> buscar(@RequestHeader(required = false) UUID clienteId) {
        if(clienteId == null)
            return ResponseEntity.ok().build();
        return ResponseEntity.ok(clienteService.buscarDetalhesClinte(clienteId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<ClienteResponse> buscar(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.buscarClinte(token, clienteId, id));
    }

    @GetMapping("/ativo/{ativo}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<Page<ClienteResponse>> listaCleintes(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @PathVariable boolean ativo, Pageable pageable) {
        return ResponseEntity.ok(clienteService.listaClientes(token, clienteId, ativo, pageable));
    }

    @GetMapping("/pesquisar/{ativo}/{texto}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<Page<ClienteResponse>> pesquisar(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId,  @PathVariable boolean ativo, @PathVariable String texto, Pageable pageable) {
        return ResponseEntity.ok(clienteService.pesquisarDispositivos(token, clienteId, texto, ativo, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<ClienteResponse> criarCleinte(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @RequestBody ClienteRequest request) {       ;
        return ResponseEntity.ok(clienteService.salvarCliente(token, clienteId, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<ClienteResponse> removerCleinte(@RequestHeader UUID clienteId, @PathVariable UUID id) {
        clienteService.removerClinte(id);
        return ResponseEntity.ok().build();
    }

}
