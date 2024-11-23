package br.com.totem.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "comando", url = "http://broker-container:8082/comando")
public interface ComandoService {

    @GetMapping("/{mac}")
    public void sincronizar(@PathVariable("mac") String mac);
    @GetMapping("/true")
    public void sincronizarTodos();
}
