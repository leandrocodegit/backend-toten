package br.com.totem.config;

import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.model.Agenda;
import br.com.totem.model.Dispositivo;
import br.com.totem.model.Log;
import br.com.totem.model.constantes.Comando;
import br.com.totem.repository.LogRepository;
import br.com.totem.service.AgendaDeviceService;
import br.com.totem.service.ComandoService;
import br.com.totem.service.DispositivoService;
import br.com.totem.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
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
    @Autowired
    private LogRepository logRepository;

    @Scheduled(fixedRate = 5000)
    public void executarTarefaAgendada() {
        List<Agenda> agendas = agendaDeviceService.listaTodosAgendasPrevistaHoje();


        if(!agendas.isEmpty()){
            System.out.println("# " + agendas.size());
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

    @Scheduled(fixedRate = 360000)
    public void checkarDipositivosOffile() {
        dispositivoService.dispositivosQueFicaramOffilne().forEach(device -> {
            logRepository.save(Log.builder()
                    .data(LocalDateTime.now())
                    .usuario("Sistema")
                    .mensagem(device.getMac())
                    .configuracao(null)
                    .comando(Comando.OFFLINE)
                    .descricao(String.format(Comando.OFFLINE.value(), device.getMac()))
                    .mac(device.getMac())
                    .build());
            dispositivoService.salvarDispositivoComoOffline(device);
            System.out.println("Dispositivo offline " + device.getMac());
        });
    }
}
