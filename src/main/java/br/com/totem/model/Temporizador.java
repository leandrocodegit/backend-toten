package br.com.totem.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Temporizador {

    private UUID idConfiguracao;
    private LocalDateTime time;
}
