package br.com.totem.utils;

import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.model.Dispositivo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeUtil {

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
        return differenceInMinutes <= 0;
    }
}
