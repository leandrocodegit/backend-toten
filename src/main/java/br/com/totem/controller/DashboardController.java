package br.com.totem.controller;

import br.com.totem.controller.response.DashboardResponse;
import br.com.totem.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping()
    public ResponseEntity<DashboardResponse> gerarDash() {
         return ResponseEntity.ok(dashboardService.gerarDash());
    }
}
