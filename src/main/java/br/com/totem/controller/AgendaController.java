package br.com.totem.controller;

import br.com.totem.controller.request.AgendaRequest;
import br.com.totem.controller.response.AgendaResponse;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.mapper.AgendaMapper;
import br.com.totem.service.AgendaDeviceService;
import br.com.totem.service.AgendaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agenda")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;
    @Autowired
    private AgendaDeviceService agendaDeviceService;
    @Autowired
    private AgendaMapper agendaMapper;


    @GetMapping
    public ResponseEntity<Page<AgendaResponse>> listaTodasAgenda(Pageable pageable) {
        return ResponseEntity.ok(agendaDeviceService.listaTodosAgendas(pageable));
    }

    @GetMapping("/mes")
    public ResponseEntity<?> listaTodasAgendaMesAtual(Pageable pageable) {
        return ResponseEntity.ok(agendaService.agendasDoMesAtual(true));
    }

    @GetMapping("/hoje")
    public ResponseEntity<List<AgendaResponse>> listaTodasAgendaHoje() {
        return ResponseEntity.ok(agendaDeviceService.listaTodosAgendasPrevistaHoje().stream().map(agendaMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/dispositivo/{mac}")
    public ResponseEntity<List<AgendaResponse>> listaTodasAgendaPorDispositivo(@PathVariable String mac) {
        return ResponseEntity.ok(agendaDeviceService.listaTodosAgendasPorDispositivo(mac));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<AgendaResponse>> removerAgenda(@PathVariable UUID id) {
        agendaService.removerAgenda(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/configuracao/{id}")
    public ResponseEntity<List<AgendaResponse>> listaTodasAgendaPorConfiguracao(@PathVariable UUID id) {
        return ResponseEntity.ok(agendaDeviceService.listaTodosAgendasPorCor(id));
    }

    @PatchMapping("/{removerConflitos}")
    public ResponseEntity<TokenResponse> atualizarAgenda(@RequestBody AgendaRequest request, @PathVariable boolean removerConflitos) {
        agendaService.alterarAgenda(request, removerConflitos);
        return ResponseEntity.ok().build();
    }


    @PostMapping
    public ResponseEntity<TokenResponse> criarAgenda(@RequestBody @Valid AgendaRequest request) {
        agendaService.criarAgenda(request);
        return ResponseEntity.ok().build();
    }





}
