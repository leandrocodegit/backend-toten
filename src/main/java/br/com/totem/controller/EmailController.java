package br.com.totem.controller;

import br.com.totem.controller.request.MensagemEmailRequest;
import br.com.totem.util.EnviarEmailHTML;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("email")
public class EmailController {

    private final EnviarEmailHTML enviarEmailHTML;

    public EmailController(EnviarEmailHTML enviarEmailHTML) {
        this.enviarEmailHTML = enviarEmailHTML;
    }

    @PostMapping
    public ResponseEntity<String> enviarEmail(@RequestBody MensagemEmailRequest request){
        enviarEmailHTML.enviar(request);
        ResponseEntity.ok().build();
    }
}
