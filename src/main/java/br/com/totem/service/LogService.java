package br.com.totem.service;

import br.com.totem.model.Log;
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

    public Page<Log> listaLogsPorTipo(List<String> tipos, Pageable pageable){
        return logRepository.findAllByComandoInOrderByDataDesc(tipos, pageable);
    }
}
