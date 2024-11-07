package br.com.totem.controller;

import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.request.ParametroRequest;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.model.constantes.Topico;
import br.com.totem.service.ComandoService;
import br.com.totem.service.DispositivoService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comando")
public class ComandoController {

    @Autowired
    private ComandoService comandoService;
    @Autowired DispositivoService dispositivoService;

    @PostMapping()
    public ResponseEntity<TokenResponse> criar(@RequestBody ParametroRequest request) {
         return ResponseEntity.ok().build();
    }

    @GetMapping("/sincronizar/{forcaTeste}")
    public ResponseEntity<?> sincronizar(@PathVariable boolean forcaTeste) {
        comandoService.enviarComando(dispositivoService.listaTodosDispositivosPorFiltro(Filtro.ATIVO).stream().map(device -> device.getMac()).collect(Collectors.toList()), forcaTeste, true);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/teste/{mac}")
    public ResponseEntity<?> testar(@PathVariable String mac) {
        comandoService.enviardComandoTeste(mac);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sincronizar/{forcaTeste}")
    public ResponseEntity<?> sincronizar(@RequestBody List<String> macs, @PathVariable boolean forcaTeste) {
        comandoService.enviarComando(macs, forcaTeste, true);
        return ResponseEntity.ok().build();
    }
}
