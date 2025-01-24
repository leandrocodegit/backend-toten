package br.com.totem.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "comando", url = "${conexao-url}")
public interface ConexaoService {

    @GetMapping
    public void atualizarDashboar();
}
