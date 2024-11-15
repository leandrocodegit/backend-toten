package br.com.totem.controller;

import br.com.totem.controller.request.DispositivoRequest;
import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.service.DispositivoService;
import br.com.totem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dispositivo")
public class DispositivoController {

    @Autowired
    private DispositivoService dispositivoService;


    @GetMapping("/{mac}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<DispositivoResponse> buscar(@PathVariable String mac) {        ;
        return ResponseEntity.ok(dispositivoService.buscarPorMac(mac));
    }

    @GetMapping("/pesquisar/{pesquisa}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> pesquisar(@PathVariable String pesquisa, Pageable pageable) {        ;
        return ResponseEntity.ok(dispositivoService.pesquisarDispositivos(pesquisa, pageable));
    }

    @PatchMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<TokenResponse> atualizarNome(@RequestBody DispositivoRequest request) {
        dispositivoService.atualizarNomeDispositivo(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/lista")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> lista(Pageable pageable) {
        return ResponseEntity.ok(dispositivoService.listaTodosDispositivos(pageable));
    }

    @GetMapping("/filtro/{filtro}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> listaAtivos(@PathVariable Filtro filtro, Pageable pageable, @RequestParam(required = false) boolean unpaged) {
        if(unpaged){
            return ResponseEntity.ok(dispositivoService.listaTodosDispositivosPorFiltro(filtro));
        }
        return ResponseEntity.ok(dispositivoService.listaTodosDispositivosPorFiltro(filtro, pageable));
    }

    @GetMapping("/ativar/{mac}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<DispositivoResponse>> Ativar(@PathVariable String mac) {
        dispositivoService.ativarDispositivos(mac);
        return ResponseEntity.accepted().build();
    }

}
