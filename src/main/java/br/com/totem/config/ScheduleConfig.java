package br.com.totem.config;

import br.com.totem.controller.request.Filtro;
import br.com.totem.repository.LogRepository;
import br.com.totem.service.AgendaDeviceService;
import br.com.totem.service.ComandoService;
import br.com.totem.service.DispositivoService;
import br.com.totem.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleConfig {

    private final AgendaDeviceService agendaDeviceService;
    private final ComandoService comandoService;
    private final DispositivoService dispositivoService;
    private final WebSocketService webSocketService;
    private final LogRepository logRepository;

    @Scheduled(fixedRate = 60000)
    public void executarTarefaDispositivos() {
        webSocketService.sendMessageDipositivos(dispositivoService.listaTodosDispositivosPorFiltro(Filtro.CORDENADAS));
    }

}
