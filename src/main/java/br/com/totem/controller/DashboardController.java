package br.com.totem.controller;

import br.com.totem.controller.response.DashboardResponse;
import br.com.totem.model.Dashboard;
import br.com.totem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public  class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<Dashboard> buscar() {
        return ResponseEntity.ok(dashboardService.buscarDashboard());
    }

    @CrossOrigin(
            origins = "http://conexao:8084",
            methods = {RequestMethod.GET},
            allowCredentials = "true"
    )
    @GetMapping("/gerar")
    public ResponseEntity<Dashboard> gerarDash() {
        return ResponseEntity.ok(dashboardService.gerarDash());
    }
}
