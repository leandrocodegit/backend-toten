package br.com.totem.service;

import br.com.totem.model.Log;
import br.com.totem.model.constantes.Role;
import br.com.totem.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final AuthService authService;

    public Page<Log> listaLogsPorTipo(String token, List<String> tipos, Pageable pageable){
        return logRepository.findAllByClienteAndComandoInOrderByDataDesc(authService.getCliente(token), tipos, pageable);
    }

    public Page<Log> listaLogs(String token, Pageable pageable){
        var user = authService.recuperarUsuarioLogado(token);
        if(authService.validaPermissao(user, Role.ROOT))
            logRepository.findAll(pageable);
        return logRepository.findAllByClienteOrderByDataDesc(authService.getCliente(token), pageable);
    }
}
