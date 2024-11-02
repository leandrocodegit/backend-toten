package br.com.totem.controller;

import br.com.totem.controller.request.ConfiguracaoRequest;
import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.response.ConfiguracaoResponse;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.service.ConfiguracaoService;
import br.com.totem.service.DispositivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/configuracao")
public class ConfiguracaoController {

    @Autowired
    private ConfiguracaoService configuracaoService;


    @PostMapping("/duplicar")
    public ResponseEntity<TokenResponse> duplicar(@RequestBody ConfiguracaoRequest request) {
        configuracaoService.duplicarconfiguracao(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{principal}")
    public ResponseEntity<TokenResponse> salvar(@RequestBody ConfiguracaoRequest request, @PathVariable boolean principal) {
        configuracaoService.salvarconfiguracao(request, principal);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TokenResponse> remover(@PathVariable UUID id) {
        configuracaoService.removerConfiguracao(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("")
    public ResponseEntity<Page<ConfiguracaoResponse>> listaConexao(Pageable pageable) {
        return ResponseEntity.ok(configuracaoService.listaTodasConfiguracoes(pageable));
    }


}
