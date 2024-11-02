package br.com.totem.config;

import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.model.Agenda;
import br.com.totem.model.Dispositivo;
import br.com.totem.service.AgendaDeviceService;
import br.com.totem.service.ComandoService;
import br.com.totem.service.DispositivoService;
import br.com.totem.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
public class ScheduleConfig {

    @Autowired
    private AgendaDeviceService agendaDeviceService;
    @Autowired
    private ComandoService comandoService;
    @Autowired
    private DispositivoService dispositivoService;
    @Autowired
    private WebSocketService webSocketService;

    @Scheduled(fixedRate = 5000)
    public void executarTarefaAgendada() {
        List<Agenda> agendas = agendaDeviceService.listaTodosAgendasPrevistaHoje();

        System.out.println("# " + agendas.size());
        if(!agendas.isEmpty()){
            agendas.forEach(agenda -> {
                System.out.println(agenda.getDispositivos().toString());
                comandoService.enviarComando(agenda);
                agendaDeviceService.atualizarDataExecucao(agenda);
            });
            System.out.println("Tarefa executada a cada 5 segundos: " + System.currentTimeMillis());
            webSocketService.sendMessageDipositivos(dispositivoService.listaTodosDispositivosPorFiltro(Filtro.CORDENADAS));
        }
    }

    @Scheduled(fixedRate = 60000)
    public void executarTarefaDispositivos() {
        webSocketService.sendMessageDipositivos(dispositivoService.listaTodosDispositivosPorFiltro(Filtro.CORDENADAS));
    }
}
