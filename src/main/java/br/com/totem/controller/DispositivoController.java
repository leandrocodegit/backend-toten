package br.com.totem.controller;

import br.com.totem.controller.request.ConfiguracaoRequest;
import br.com.totem.controller.request.DispositivoRequest;
import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.service.DispositivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dispositivo")
@RequiredArgsConstructor
public class DispositivoController {

    private final DispositivoService dispositivoService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_USER', 'ROLE_AVANCADO', 'ROLE_ADMIN')")
    public ResponseEntity<DispositivoResponse> buscar(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @PathVariable long id) {        ;
        return ResponseEntity.ok(dispositivoService.buscarPorMac(token, clienteId, id));
    }

    @GetMapping("/pesquisar/{pesquisa}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_USER','ROLE_AVANCADO', 'ROLE_ADMIN')")
    public ResponseEntity<?> pesquisar(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @PathVariable String pesquisa, Pageable pageable) {        ;
        return ResponseEntity.ok(dispositivoService.pesquisarDispositivos(token, clienteId, pesquisa, pageable));
    }

    @PatchMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> atualizarNome(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @RequestBody DispositivoRequest request) {
        dispositivoService.atualizarNomeDispositivo(token, clienteId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/configuracao")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> atualizarConfiguracao(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @RequestBody ConfiguracaoRequest request) {
        dispositivoService.atualizarConfiguracaoDispositivo(token, clienteId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/lista")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_USER','ROLE_AVANCADO', 'ROLE_ADMIN')")
    public ResponseEntity<?> lista(@RequestHeader(required = false) UUID clienteId,  @RequestHeader("Authorization") String token, Pageable pageable) {
        return ResponseEntity.ok(dispositivoService.listaTodosDispositivos(token, clienteId, pageable));
    }

    @GetMapping("/filtro/{filtro}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_USER','ROLE_AVANCADO', 'ROLE_ADMIN')")
    public ResponseEntity<?> listaAtivos(@RequestHeader(required = false) UUID clienteId,  @RequestHeader("Authorization") String token, @PathVariable Filtro filtro, Pageable pageable, @RequestParam(required = false) boolean unpaged) {
        if(unpaged){
            return ResponseEntity.ok(dispositivoService.listaTodosDispositivosPorFiltro(token, clienteId, filtro));
        }
        var r = dispositivoService.listaTodosDispositivosPorFiltro(token,clienteId, filtro, pageable);
        return ResponseEntity.ok(dispositivoService.listaTodosDispositivosPorFiltro(token,clienteId, filtro, pageable));
    }

    @GetMapping("/ativar/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT','ROLE_ADMIN','ROLE_AVANCADO')")
    public ResponseEntity<List<DispositivoResponse>> Ativar(@RequestHeader("Authorization") String token, @RequestHeader(required = false) UUID clienteId, @PathVariable long id) {
        dispositivoService.ativarDispositivos(token, clienteId, id);
        return ResponseEntity.accepted().build();
    }

}
