package br.com.totem.util;

import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.controller.response.DispositivoResumeResponse;
import br.com.totem.model.Dispositivo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TimeUtil {

    private TimeUtil(){}

    public static Map<String, Dispositivo> timers = new HashMap<>();
    public static boolean isTime(Dispositivo dispositivo) {
        if (dispositivo == null || dispositivo.getTemporizador() == null) {
            return false;
        }
        long differenceInMinutes = Duration.between(dispositivo.getTemporizador().getTime(), LocalDateTime.now()).toMinutes();
        return differenceInMinutes <= 0;
    }

    public static boolean isTime(DispositivoResponse dispositivo) {
        if (dispositivo == null || dispositivo.getTemporizador() == null) {
            return false;
        }
        long differenceInMinutes = Duration.between(dispositivo.getTemporizador().getTime(), LocalDateTime.now()).toMinutes();
        System.out.println("Temporizador: " + differenceInMinutes);
        return differenceInMinutes <= 0;
    }

    public static boolean isTime(DispositivoResumeResponse dispositivo) {
        if (dispositivo == null || dispositivo.getTemporizador() == null) {
            return false;
        }
        long differenceInMinutes = Duration.between(dispositivo.getTemporizador().getTime(), LocalDateTime.now()).toMinutes();
        return differenceInMinutes <= 0;
    }
}
