package br.com.totem.controller;

import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.request.UserUpdateRequest;
import br.com.totem.controller.response.TokenResponse;
import br.com.totem.model.constantes.Comando;
import br.com.totem.service.AuthService;
import br.com.totem.service.DashboardService;
import br.com.totem.service.LogService;
import br.com.totem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;
    private final DashboardService dashboardService;

    @GetMapping("/{tipo}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_OPERADOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> pesquisarPorEmail(@PathVariable String tipo, Pageable pageable) {
        if(tipo.contains("TIME"))
            return ResponseEntity.ok(logService.listaLogsPorTipo(Arrays.asList(Comando.TIMER_CONCLUIDO.name(), Comando.TIMER_CRIADO.name(), Comando.TIMER_CANCELADO.name()), pageable));
        return ResponseEntity.ok(logService.listaLogsPorTipo(Arrays.asList(tipo), pageable));
    }
}
