package br.com.totem.controller;

import br.com.totem.controller.request.MensagemEmailRequest;
import br.com.totem.util.EnviarEmailHTML;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("email")
public class EmailController {

    @PostMapping
    public void enviarEmail(@RequestBody MensagemEmailRequest request){
        EnviarEmailHTML.enviar(request);
    }
}
