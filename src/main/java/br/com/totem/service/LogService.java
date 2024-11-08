package br.com.totem.service;

import br.com.totem.model.Log;
import br.com.totem.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public Page<Log> listaLogsMac(String mac){
    return null;
    }
}
