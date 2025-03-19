package br.com.totem.controller;

import br.com.totem.controller.request.AgendaRequest;
import br.com.totem.controller.response.AgendaResponse;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.mapper.AgendaMapper;
import br.com.totem.model.constantes.TipoToken;
import br.com.totem.security.JWTTokenProvider;
import br.com.totem.service.AgendaDeviceService;
import br.com.totem.service.AgendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;
    private final AgendaDeviceService agendaDeviceService;
    private final AgendaMapper agendaMapper;
    private final JWTTokenProvider jwtTokenProvider;


    @GetMapping
    public ResponseEntity<Page<AgendaResponse>> listaTodasAgenda(@RequestHeader("Authorization") String token, Pageable pageable) {
        return ResponseEntity.ok(agendaDeviceService.listaTodosAgendas(token, pageable));
    }

    @GetMapping("/mes")
    public ResponseEntity<?> listaTodasAgendaMesAtual(@RequestHeader("Authorization") String token, Pageable pageable) {
        return ResponseEntity.ok(agendaService.agendasDoMesAtual(token, true));
    }

    @GetMapping("/hoje")
    public ResponseEntity<List<AgendaResponse>> listaTodasAgendaHoje() {
        return ResponseEntity.ok(agendaDeviceService.listaTodosAgendasPrevistaHoje().stream().map(agendaMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/dispositivo/{mac}")
    public ResponseEntity<List<AgendaResponse>> listaTodasAgendaPorDispositivo(@PathVariable long id) {
        return ResponseEntity.ok(agendaDeviceService.listaTodosAgendasPorDispositivo(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<AgendaResponse>> removerAgenda(@PathVariable UUID id, @RequestHeader String authorization) {
        var user = jwtTokenProvider.getSubjectFromToken(authorization, TipoToken.ACCESS);
        agendaService.removerAgenda(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/configuracao/{id}")
    public ResponseEntity<List<AgendaResponse>> listaTodasAgendaPorConfiguracao(@PathVariable UUID id) {
        return ResponseEntity.ok(agendaDeviceService.listaTodosAgendasPorCor(id));
    }

    @PatchMapping("/{removerConflitos}")
    public ResponseEntity<TokenResponse> atualizarAgenda(@RequestHeader("Authorization") String token, @RequestBody AgendaRequest request, @PathVariable boolean removerConflitos, @RequestHeader String authorization) {
        agendaService.alterarAgenda(token, request, removerConflitos);
        return ResponseEntity.ok().build();
    }


    @PostMapping
    public ResponseEntity<TokenResponse> criarAgenda(@RequestHeader("Authorization") String token, @RequestBody @Valid AgendaRequest request) {
        agendaService.criarAgenda(token, request);
        return ResponseEntity.ok().build();
    }





}
