package br.com.totem.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ComandoService {

    @GetMapping("/{mac}")
    public void sincronizar(@PathVariable("mac") String mac);
    @GetMapping("/true")
    public void sincronizarTodos();
}
