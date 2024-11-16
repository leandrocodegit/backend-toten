package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.ParametroRequest;
import br.com.totem.controller.response.ComandoRequest;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.mapper.DispositivoMapper;
import br.com.totem.model.*;
import br.com.totem.model.constantes.Comando;
import br.com.totem.model.constantes.Efeito;
import br.com.totem.model.constantes.Topico;
import br.com.totem.repository.CorRepository;
import br.com.totem.repository.DispositivoRepository;
import br.com.totem.repository.LogRepository;
import br.com.totem.utils.ConfiguracaoUtil;
import br.com.totem.utils.TimeUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ComandoService {
    @Autowired
    private MqttService mqttService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private DispositivoRepository dispositivoRepository;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private DispositivoMapper dispositivoMapper;
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private AgendaDeviceService agendaDeviceService;
    @Autowired
    private CorRepository corRepository;


    public void enviardComandoTeste(String mac) {
        Dispositivo dispositivo = buscarPorMac(mac);

        if (dispositivo != null && dispositivo.getConfiguracao() != null) {
            mqttService.sendRetainedMessage(Topico.DEVICE_RECEIVE + dispositivo.getMac(), ConfiguracaoUtil.gerarComandoTeste(dispositivo.getConfiguracao()));
        }
    }

    public void enviardComando(String mac) {
        Dispositivo dispositivo = buscarPorMac(mac);

        if (dispositivo.isAtivo() && dispositivo.getConfiguracao() != null) {
            mqttService.sendRetainedMessage(Topico.DEVICE_RECEIVE + dispositivo.getMac(), ConfiguracaoUtil.gerarComando(dispositivo));

        }
    }

    public void enviardComando(Dispositivo dispositivo, boolean forceConsulta) {

        if (forceConsulta) {
            dispositivo.setCor(buscarPorMac(dispositivo.getMac()).getCor());
        }

        if (dispositivo.isAtivo() && dispositivo.getConfiguracao() != null && dispositivo.getCor() != null) {
            mqttService.sendRetainedMessage(Topico.DEVICE_RECEIVE + dispositivo.getMac(), ConfiguracaoUtil.gerarComando(dispositivo));
        }
    }


    public void enviarComando(List<String> macs, boolean forcaTeste, boolean sincronizar) {

        List<Dispositivo> dispositivos = todosDispositivosAtivos(macs, true);

        if (!dispositivos.isEmpty()) {

            if (sincronizar) {
                logRepository.save(Log.builder()
                        .data(LocalDateTime.now())
                        .usuario("Leandro")
                        .mensagem(forcaTeste ? "Especifico" : "Todos")
                        .cor(null)
                        .comando(Comando.SINCRONIZAR)
                        .descricao(Comando.SINCRONIZAR.value())
                        .mac(macs.toString())
                        .build());
            }


            dispositivos.forEach(device -> {

                boolean salvarLog = true;

                if (device.isAtivo() && device.getConfiguracao() != null) {
                    if (!forcaTeste) {
                        device.setCor(getCor(device));
                    }
                    mqttService.sendRetainedMessage(Topico.DEVICE_RECEIVE + device.getMac(), ConfiguracaoUtil.gerarComando(device));
                    System.out.println(new Gson().toJson(device.getConfiguracao()));
                }

                if (forcaTeste) {
                    logRepository.save(Log.builder()
                            .data(LocalDateTime.now())
                            .usuario("Leandro")
                            .mensagem("Tesde configuração enviado")
                            .cor(null)
                            .comando(Comando.TESTE)
                            .descricao(Comando.TESTE.value())
                            .mac(device.getMac())
                            .build());
                }
            });
            logRepository.save(Log.builder()
                    .data(LocalDateTime.now())
                    .usuario("Leandro")
                    .mensagem("Enviado para todos")
                    .cor(null)
                    .comando(Comando.ENVIAR)
                    .mac(macs.toString())
                    .descricao(Comando.ENVIAR.value())
                    .build());
            webSocketService.sendMessageDashboard(dashboardService.gerarDash());
        } else {
            logRepository.save(Log.builder()
                    .data(LocalDateTime.now())
                    .usuario("Leandro")
                    .cor(null)
                    .mensagem(macs.toString())
                    .comando(Comando.NENHUM_DEVICE)
                    .descricao(Comando.NENHUM_DEVICE.value())
                    .build());
            webSocketService.sendMessageDashboard(dashboardService.gerarDash());
        }
        webSocketService.sendMessageDipositivos(buscarDispositivosAtivosComAgendaPesquisada().stream().map(dispositivoMapper::toResponse).collect(Collectors.toList()));
    }

    private Cor getCor(Dispositivo dispositivo) {
        Agenda agenda = null;

        if (TimeUtil.isTime(dispositivo)) {
            Optional<Cor> corOptional = buscaCor(dispositivo.getTemporizador().getIdCor());
            if (corOptional.isPresent()) {
                return corOptional.get();
            }
        }
        if (Boolean.FALSE.equals(dispositivo.isIgnorarAgenda())) {
            agenda = agendaDeviceService.buscarAgendaDipositivoPrevistaHoje(dispositivo.getMac());
        }
        if (agenda != null && agenda.getCor() != null) {
            return agenda.getCor();
        }
        return dispositivo.getCor();
    }

    public void enviarComando(Agenda agenda) {

        List<Dispositivo> dispositivos = Collections.EMPTY_LIST;

        if (agenda.isTodos()) {
            dispositivos = agenda.getDispositivos();
        } else {
            dispositivos = agenda.getDispositivos().stream().filter(device -> device.isAtivo() && device.getConfiguracao() != null).collect(Collectors.toList());
        }

        if (!dispositivos.isEmpty()) {
            dispositivos.forEach(device -> {

                if (device.isAtivo() && device.getConfiguracao() != null) {
                    if (Boolean.FALSE.equals(device.isIgnorarAgenda()) && !TimeUtil.isTime(device)) {
                        //    ConfiguraçãoUtil.parametrizarConfiguracao(agenda.getConfiguracao(), device.getConfiguracao());
                        mqttService.sendRetainedMessage(Topico.DEVICE_RECEIVE + device.getMac(),
                                new Gson().toJson(device.getConfiguracao()), false);
                    }
                }
            });
            logRepository.save(Log.builder()
                    .data(LocalDateTime.now())
                    .usuario(Comando.SISTEMA.value())
                    .mensagem("Tarefa agenda executada")
                    .cor(agenda.getCor())
                    .comando(Comando.SISTEMA)
                    .descricao("Tarefa agenda executada")
                    .mac(agenda.getDispositivos().stream().map(mac -> mac.getMac()).collect(Collectors.toList()).toString())
                    .build());
            webSocketService.sendMessageDashboard(dashboardService.gerarDash());
        }
    }

    public void atualizarDispositivo(ParametroRequest parametro, String mac) {
        Dispositivo dispositivo = buscarPorMac(mac);
        dispositivo.setComando(Comando.ENVIADO);

    }

    public Optional<Cor> buscaCor(UUID id) {
        return corRepository.findById(id);
    }

    private Dispositivo buscarPorMac(String mac) {
        return dispositivoRepository.findById(mac)
                .orElseThrow(() -> new ExceptionResponse("Dispositivo não encontrado"));
    }

    private List<DispositivoResponse> listaTodosDispositivos() {
        return dispositivoRepository.findAll().stream().map(dispositivoMapper::toResponse).collect(Collectors.toList());
    }

    private List<DispositivoResponse> listaTodosDispositivos(List<String> macs) {
        return dispositivoRepository.findAllById(macs).stream().map(dispositivoMapper::toResponse).collect(Collectors.toList());
    }

    private List<Dispositivo> todosDispositivos(List<String> macs) {
        return dispositivoRepository.findAllById(macs);
    }

    private List<Dispositivo> todosDispositivosAtivos(List<String> macs, boolean ativo) {
        return dispositivoRepository.findAllByMacInAndAtivo(macs, ativo);
    }

    public List<Dispositivo> buscarDispositivosAtivosComAgendaPesquisada() {
        List<Dispositivo> dispositivos = dispositivoRepository.findAllByAtivo(true);
        if (!dispositivos.isEmpty()) {
            dispositivos.forEach(device -> {
                Agenda agenda = agendaDeviceService.buscarAgendaDipositivoPrevistaHoje(device.getMac());
                if (agenda != null && agenda.getCor() != null) {
                    device.setCor(agenda.getCor());
                }
            });
        }
        return dispositivos;
    }
}
