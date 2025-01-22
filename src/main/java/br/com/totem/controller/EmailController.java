package br.com.totem.controller;

import br.com.totem.controller.request.MensagemEmailRequest;
import br.com.totem.util.EnviarEmailHTML;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("email")
public class EmailController {

    @GetMapping
    public void enviarEmail(@RequestBody MensagemEmailRequest request){
        EnviarEmailHTML.enviar(request);
    }
}
