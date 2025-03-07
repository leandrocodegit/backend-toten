package br.com.totem.service;

import br.com.totem.model.constantes.TipoConfiguracao;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(value = "comando", url = "${comando-url}")
public interface ComandoService {

    @GetMapping("/interno/{mac}")
    public void sincronizar(@PathVariable("mac") long id);
    @GetMapping("/interno/sincronizar/{cor}")
    public void sincronizarVibracao(@PathVariable("cor") UUID id);
    @GetMapping("/interno/sincronizar/{user}/{responder}")
    public void sincronizarTodos(@PathVariable String  user, @PathVariable boolean responder);

}
