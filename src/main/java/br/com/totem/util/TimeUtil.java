package br.com.totem.util;

import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.controller.response.DispositivoResumeResponse;
import br.com.totem.model.Dispositivo;
import br.com.totem.model.constantes.ModoOperacao;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TimeUtil {

    private TimeUtil(){}

    public static Map<String, Dispositivo> timers = new HashMap<>();
    public static boolean isTime(Dispositivo dispositivo) {
        if (dispositivo == null || dispositivo.getOperacao().getTime() == null || !dispositivo.getOperacao().getModoOperacao().equals(ModoOperacao.TEMPORIZADOR)) {
            return false;
        }
        long differenceInMinutes = Duration.between(dispositivo.getOperacao().getTime(), LocalDateTime.now()).toMinutes();
        return differenceInMinutes <= 0;
    }

    public static boolean isTime(DispositivoResponse dispositivo) {
        if (dispositivo == null || dispositivo.getOperacao().getTime() == null || !dispositivo.getOperacao().getModoOperacao().equals(ModoOperacao.TEMPORIZADOR)) {
            return false;
        }
        long differenceInMinutes = Duration.between(dispositivo.getOperacao().getTime(), LocalDateTime.now()).toMinutes();
        return differenceInMinutes <= 0;
    }

    public static boolean isTime(DispositivoResumeResponse dispositivo) {
        if (dispositivo == null || dispositivo.getOperacao().getTime() == null || !dispositivo.getOperacao().getModoOperacao().equals(ModoOperacao.TEMPORIZADOR)) {
            return false;
        }
        long differenceInMinutes = Duration.between(dispositivo.getOperacao().getTime(), LocalDateTime.now()).toMinutes();
        return differenceInMinutes <= 0;
    }

    public String getConexao(LocalDateTime ultimaAtualizacao) {
        long differenceInMinutes = Duration.between(ultimaAtualizacao, LocalDateTime.now()).toMinutes();
        if (differenceInMinutes >= 5)
            return "Offline";
        return "Online";
    }
}
