package br.com.totem.service;

import br.com.totem.controller.request.ConfiguracaoRequest;
import br.com.totem.controller.request.DispositivoRequest;
import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.mapper.DispositivoMapper;
import br.com.totem.model.*;
import br.com.totem.model.constantes.Comando;
import br.com.totem.repository.DispositivoRepository;
import br.com.totem.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DispositivoService {

    @Autowired
    private DispositivoRepository dispositivoRepository;
    @Autowired
    private DispositivoMapper dispositivoMapper;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private ConfiguracaoService configuracaoService;
    @Autowired
    private ComandoService comandoService;
    @Autowired
    private AgendaDeviceService agendaDeviceService;


    public void salvarDispositivoComoOffline(Dispositivo dispositivo) {
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(dispositivo.getMac());
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivoDB = dispositivoOptional.get();
            dispositivoDB.setComando(Comando.OFFLINE);
            dispositivoRepository.save(dispositivo);
        }
    }

    public void atualizarNomeDispositivo(DispositivoRequest request) {
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(request.getMac());
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setNome(request.getNome());
            dispositivo.setLatitude(request.getLatitude());
            dispositivo.setLongitude(request.getLongitude());
            dispositivoRepository.save(dispositivo);
        }
    }

    public void atualizarDispositivo(Mensagem mensagem) {
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(mensagem.getId());
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();

            System.out.println(mensagem.getId().substring(mensagem.getId().length() - 5, mensagem.getId().length()));
            dispositivo.setUltimaAtualizacao(LocalDateTime.now().atZone(ZoneOffset.UTC).toLocalDateTime());
            dispositivo.setIp(mensagem.getIp());
            dispositivo.setMemoria(mensagem.getMemoria());
            dispositivo.setComando(Comando.ONLINE);
            dispositivo.setVersao(mensagem.getVersao());
            dispositivo.setBrokerId(mensagem.getBrockerId());
            if (dispositivo.getConfiguracao() != null && (mensagem.getComando().equals(Comando.CONFIGURACAO) || mensagem.getComando().equals(Comando.CONCLUIDO))) {
                logRepository.save(Log.builder()
                        .data(LocalDateTime.now())
                        .usuario("Leandro")
                        .mensagem(mensagem.getId())
                        .configuracao(dispositivo.getConfiguracao())
                        .comando(mensagem.getComando())
                        .descricao(mensagem.getComando().equals(Comando.ONLINE) ? String.format(mensagem.getComando().value(), mensagem.getId()) : mensagem.getComando().value())
                        .build());
            }
            if (mensagem.getComando().equals(Comando.ACEITO) || mensagem.getComando().equals(Comando.ONLINE)) {
                logRepository.save(Log.builder()
                        .data(LocalDateTime.now())
                        .usuario("Leandro")
                        .mensagem(mensagem.getId())
                        .configuracao(dispositivo.getConfiguracao())
                        .comando(mensagem.getComando())
                        .descricao(mensagem.getComando().equals(Comando.ONLINE) ? String.format(mensagem.getComando().value(), mensagem.getId()) : mensagem.getComando().value())
                        .build());
                webSocketService.sendMessageDashboard(dashboardService.gerarDash());
            } else if (mensagem.getComando().equals(Comando.ONLINE)) {
//                logRepository.save(Log.builder()
//                        .data(LocalDateTime.now())
//                        .usuario("Leandro")
//                        .mensagem(mensagem.getId())
//                        .configuracao(dispositivo.getConfiguracao())
//                        .comando(mensagem.getComando())
//                        .descricao(mensagem.getComando().value())
//                        .build());
            }
            dispositivoRepository.save(dispositivo);
            if (mensagem.getComando().equals(Comando.CONFIGURACAO) || mensagem.getComando().equals(Comando.CONCLUIDO)) {
                comandoService.enviardComando(dispositivo);
            }
        } else {
            dispositivoRepository.save(
                    Dispositivo.builder()
                            .ultimaAtualizacao(LocalDateTime.now().atZone(ZoneOffset.UTC).toLocalDateTime())
                            .mac(mensagem.getId())
                            .versao("")
                            .ignorarAgenda(false)
                            .memoria(0)
                            .ativo(false)
                            .nome(mensagem.getId().substring(mensagem.getId().length() - 5, mensagem.getId().length()))
                            .comando(Comando.ONLINE)
                            .build()
            );
        }
    }

    private Instant creationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
    }

    public void ativarDispositivos(String mac) {
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(mac);
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setAtivo(!dispositivo.isAtivo());
            dispositivoRepository.save(dispositivo);
        }
    }

    public DispositivoResponse buscarPorMac(String mac) {
        return dispositivoMapper.toResponse(dispositivoRepository.findById(mac).orElseThrow());
    }

    public Page<DispositivoResponse> pesquisarDispositivos(String pesquisa, Pageable pageable) {
        return dispositivoRepository.findByMacAndNomeContaining(pesquisa, pageable).map(dispositivoMapper::toResponse);
    }

    public Page<DispositivoResponse> listaTodosDispositivos(Pageable pageable) {
        return dispositivoRepository.findAll(pageable).map(dispositivoMapper::toResponse);
    }

    public Page<DispositivoResponse> listaTodosDispositivosPorFiltro(Filtro filtro, Pageable pageable) {
        return listaTodosEntidadeDispositivosPorFiltro(filtro, pageable).map(dispositivoMapper::toResponse);
    }

    public List<DispositivoResponse> listaTodosDispositivosPorFiltro(Filtro filtro) {
        return listaTodosEntidadeDispositivosPorFiltro(filtro).stream().map(dispositivoMapper::toResponse).collect(Collectors.toList());
    }

    public Page<Dispositivo> listaTodosEntidadeDispositivosPorFiltro(Filtro filtro, Pageable pageable) {
        switch (filtro) {
            case TODOS -> {
                return dispositivoRepository.findAll(pageable);
            }
            case ATIVO -> {
                return dispositivoRepository.findAllByAtivo(true, pageable);
            }
            case INATIVO -> {
                return dispositivoRepository.findAllByAtivo(false, pageable);
            }
            case OFFLINE -> {
                return buscarDispositivosAtivosTempo(5, pageable);
            }
            case NAO_CONFIGURADO -> {
                return dispositivoRepository.findDispositivosSemConfiguracao(pageable);
            }

        }
        return Page.empty();
    }

    public List<Dispositivo> listaTodosEntidadeDispositivosPorFiltro(Filtro filtro) {
        switch (filtro) {
            case TODOS -> {
                return dispositivoRepository.findAll();
            }
            case ATIVO -> {
                return dispositivoRepository.findAllByAtivo(true);
            }
            case INATIVO -> {
                return dispositivoRepository.findAllByAtivo(false);
            }
            case OFFLINE -> {
                return buscarDispositivosAtivosTempo(5);
            }
            case CORDENADAS -> {
                return buscarDispositivosAtivosComAgendaPesquisada();
            }
            case NAO_CONFIGURADO -> {
                return dispositivoRepository.findDispositivosSemConfiguracao();
            }

        }
        return Collections.emptyList();
    }


    public Page<DispositivoResponse> buscarDispositivosAtivosComMaisDe5Minutos(Pageable pageable) {
        return buscarDispositivosAtivosTempo(5, pageable).map(dispositivoMapper::toResponse);
    }

    public List<Dispositivo> buscarDispositivosAtivosTempo(long minutos) {
        LocalDateTime cincoMinutosAtras = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(minutos);
        Date dataLimite = Date.from(cincoMinutosAtras.atZone(ZoneOffset.UTC).toInstant());
        return dispositivoRepository.findAllAtivosComUltimaAtualizacaoAntes(dataLimite);
    }

    public Page<Dispositivo> buscarDispositivosAtivosTempo(long minutos, Pageable pageable) {
        LocalDateTime cincoMinutosAtras = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(minutos);
        Date dataLimite = Date.from(cincoMinutosAtras.atZone(ZoneOffset.UTC).toInstant());
        return dispositivoRepository.findAllAtivosComUltimaAtualizacaoAntes(dataLimite, pageable);
    }

    public List<Dispositivo> buscarDispositivosAtivosComAgendaPesquisada() {
        List<Dispositivo> dispositivos = dispositivoRepository.findAllByAtivo(true);
        if (!dispositivos.isEmpty()) {
            dispositivos.forEach(device -> {
                Agenda agenda = agendaDeviceService.buscarAgendaDipositivoPrevistaHoje(device.getMac());
                if (agenda != null && agenda.getConfiguracao() != null) {
                    device.setConfiguracao(agenda.getConfiguracao());
                }
            });
        }
        return dispositivos;
    }

    public List<Dispositivo> dispositivosQueFicaramOffilne() {
        LocalDateTime cincoMinutosAtras = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(6);
        Date dataLimite = Date.from(cincoMinutosAtras.atZone(ZoneOffset.UTC).toInstant());
        return dispositivoRepository.findAllAtivosComUltimaAtualizacaoAntesQueEstavaoOnline(dataLimite);
    }
}
