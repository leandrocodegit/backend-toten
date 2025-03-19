package br.com.totem.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(value = "conexao", url = "${conexao-url}")
public interface ConexaoService {

    @GetMapping
    public void atualizarDashboar(@RequestHeader(required = false) UUID clienteId);
}
