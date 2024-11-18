package br.com.totem.controller;

import br.com.totem.controller.response.DashboardResponse;
import br.com.totem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public  class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<DashboardResponse> gerarDash() {
         return ResponseEntity.ok(dashboardService.gerarDash());
    }
}
