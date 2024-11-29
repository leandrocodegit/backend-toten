package br.com.totem.service;

import br.com.totem.controller.request.DispositivoRequest;
import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.mapper.DispositivoMapper;
import br.com.totem.model.*;
import br.com.totem.model.constantes.Comando;
import br.com.totem.repository.DispositivoRepository;
import br.com.totem.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DispositivoService {

    private final DispositivoRepository dispositivoRepository;
    private final DispositivoMapper dispositivoMapper;
    private final LogRepository logRepository;
    private final ComandoService comandoService;
    private final AgendaDeviceService agendaDeviceService;

    public void atualizarNomeDispositivo(DispositivoRequest request) {
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(request.getMac());
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setNome(request.getNome());
            dispositivo.setLatitude(request.getLatitude());
            dispositivo.setLongitude(request.getLongitude());
            dispositivo.setEndereco(request.getEndereco());
            dispositivo.setEnderecoCompleto(request.getEndereco().toString());
            dispositivo.setIgnorarAgenda(request.isIgnorarAgenda());
            dispositivoRepository.save(dispositivo);
            logRepository.save(Log.builder()
                    .cor(null)
                    .mac(request.getMac())
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(dispositivo.getConfiguracao().toString())
                    .mensagem( "Dispositivo foi atualizado")
                    .build());
        }
    }

    public void atualizarConfiguracaoDispositivo(DispositivoRequest request) {
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(request.getMac());
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setConfiguracao(
                    Configuracao.builder()
                            .intensidade(request.getConfiguracao().getIntensidade())
                            .leds(request.getConfiguracao().getLeds())
                            .faixa(request.getConfiguracao().getFaixa())
                            .tipoCor(request.getConfiguracao().getTipoCor())
                            .build()
            );
            dispositivoRepository.save(dispositivo);
            comandoService.sincronizar(dispositivo.getMac());
            logRepository.save(Log.builder()
                    .cor(null)
                    .mac(request.getMac())
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(dispositivo.getConfiguracao().toString())
                    .mensagem( "Dispositivo foi alterado a configuracao")
                    .build());
        }
    }

    public void ativarDispositivos(String mac) {
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(mac);
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setAtivo(!dispositivo.isAtivo());
            dispositivoRepository.save(dispositivo);
            logRepository.save(Log.builder()
                    .cor(null)
                    .mac(mac)
                    .data(LocalDateTime.now())
                    .mensagem( "Dispositivo foi " +  (dispositivo.isAtivo() ? "ativado" : "desativado"))
                    .build());
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
                if (agenda != null && agenda.getCor() != null) {
                    device.setCor(agenda.getCor());
                }
            });
        }
        return dispositivos;
    }

}
