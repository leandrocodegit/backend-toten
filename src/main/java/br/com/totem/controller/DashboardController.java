package br.com.totem.controller;

import br.com.totem.controller.response.DashboardResponse;
import br.com.totem.model.Dashboard;
import br.com.totem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping()
    public ResponseEntity<Dashboard> buscar(@RequestHeader UUID clienteId) {
        return ResponseEntity.ok(dashboardService.buscarDashboard(clienteId));
    }

    @CrossOrigin(
            origins = "http://conexao:8084",
            methods = {RequestMethod.GET},
            allowCredentials = "true"
    )
    @GetMapping("/gerar")
    public ResponseEntity<Dashboard> gerarDash(@RequestHeader(required = false) UUID clienteId) {
        if (clienteId == null)
            return ResponseEntity.ok().build();
        return ResponseEntity.ok(dashboardService.gerarDash(clienteId));
    }

    @GetMapping("/atualizar/conexoes")
    public ResponseEntity<Dashboard> atualizarConexoes(@RequestHeader(required = false) UUID clienteId) {
        if (clienteId != null)
            dashboardService.atualizarConexoes(clienteId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/atualizar/agendas")
    public ResponseEntity<Dashboard> atualizarAgendas(@RequestHeader(required = false) UUID clienteId) {
        if (clienteId != null)
            dashboardService.atualizarConexoes(clienteId);
        return ResponseEntity.ok().build();
    }


}
