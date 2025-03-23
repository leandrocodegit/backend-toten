package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.ConfiguracaoRequest;
import br.com.totem.controller.request.DispositivoRequest;
import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.mapper.ClienteMapper;
import br.com.totem.mapper.DispositivoMapper;
import br.com.totem.model.*;
import br.com.totem.model.constantes.*;
import br.com.totem.repository.*;
import br.com.totem.security.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DispositivoService {

    @Value("${quantidade-clientes}")
    private int quantidadeClientes;
    private final DispositivoRepository dispositivoRepository;
    private final DispositivoMapper dispositivoMapper;
    private final LogRepository logRepository;
    private final ComandoService comandoService;
    private final AgendaDeviceService agendaDeviceService;
    private final ConexaoRepository conexaoRepository;
    private final JWTTokenProvider jwtTokenProvider;
    private final CorRepository corRepository;
    private final OperacaoRepository operacaoRepository;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final AuthService authService;


    public void atualizarNomeDispositivo(String token, UUID clienteId, DispositivoRequest request) {
        Optional<Dispositivo> dispositivoOptional = Optional.empty();
        if (authService.validaPermissao(token, Role.ROOT))
         dispositivoOptional = dispositivoRepository.findById(request.getId());
        else dispositivoRepository.findByClienteAndId(clienteId, request.getId());
        var user = jwtTokenProvider.getSubjectFromToken(token, TipoToken.ACCESS);
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setNome(request.getNome());
            if(request.getConexao() != null){
                dispositivo.getConexao().setLatitude(request.getConexao().getLatitude() != null ? request.getConexao().getLatitude() : "");
                dispositivo.getConexao().setLongitude(request.getConexao().getLongitude() != null ? request.getConexao().getLongitude() : "");
                conexaoRepository.save(dispositivo.getConexao());
            }
            dispositivo.setEndereco(request.getEndereco());
            dispositivo.setIgnorarAgenda(request.isIgnorarAgenda());
            dispositivo.setPermiteComando(request.isPermiteComando());
            dispositivo.setCliente(clienteMapper.toEntity(request.getCliente()));
            dispositivoRepository.save(dispositivo);
            logRepository.save(Log.builder()
                    .cor(null)
                    .usuario(user)
                    .id(String.valueOf(request.getId()))
                    .tipoLog(TipoLog.DEVICE)
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao("Dispositivo foi atualizado")
                    .mensagem("Dispositivo foi atualizado")
                    .build());

            if (dispositivo.getCor() != null && dispositivo.getCor().getCliente() == null) {
                dispositivo.getCor().setCliente(dispositivo.getCliente());
                corRepository.save(dispositivo.getCor());
            }
        }
    }

    public void atualizarConfiguracaoDispositivo(String token, ConfiguracaoRequest request) {
        var userLogado = authService.recuperarUsuarioLogado(token);
        Optional<Dispositivo> dispositivoOptional = Optional.empty();
        if (authService.validaPermissao(token, Role.ROOT))
            dispositivoOptional = dispositivoRepository.findById(request.getId());
        else dispositivoRepository.findByClienteAndId(userLogado.getCliente().getId(), request.getId());
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setSensibilidadeVibracao(request.getSensibilidadeVibracao());

            dispositivo.getConexao().setModoLora(request.getConexao().getModoLora());
            dispositivo.getConexao().setClasse(request.getConexao().getClasse());
            dispositivo.getConexao().setHabilitarLoraWan(request.getConexao().isHabilitarLoraWan());
            dispositivo.getConexao().setHabilitarWifi(request.getConexao().getHabilitarWifi());
            dispositivo.getConexao().setSenha(request.getConexao().getSenha());
            dispositivo.getConexao().setSsid(request.getConexao().getSsid());
            dispositivo.getConexao().setNwkSKey(request.getConexao().getNwkSKey());
            dispositivo.getConexao().setAppSKey(request.getConexao().getAppSKey());
            dispositivo.getConexao().setDevAddr(request.getConexao().getDevAddr());
            dispositivo.getConexao().setAppEui(request.getConexao().getAppEui());
            dispositivo.getConexao().setAppKey(request.getConexao().getAppKey());
            dispositivo.getConexao().setDevEui(request.getConexao().getDevEui());
            dispositivo.getConexao().setTxPower(request.getConexao().getTxPower());
            dispositivo.getConexao().setDataRate(request.getConexao().getDataRate());
            dispositivo.getConexao().setAutoJoin(request.getConexao().isAutoJoin());
            dispositivo.getConexao().setFracionarMensagem(request.getConexao().isFracionarMensagem());
            dispositivo.getConexao().setAdr(request.getConexao().isAdr());
            dispositivo.getConexao().setTempoAtividade(request.getConexao().getTempoAtividade());

            if (request.getCorVibracao() != null) {
                var cor = corRepository.findById(request.getCorVibracao());
                if (cor.isPresent()) {
                    dispositivo.getOperacao().setCorVibracao(cor.get());
                    dispositivo.setCorVibracao(cor.get().getId().toString());
                    operacaoRepository.save(dispositivo.getOperacao());
                }
            }

            if (dispositivo.getCor() != null && dispositivo.getCor().getCliente() == null) {
                dispositivo.getCor().setCliente(dispositivo.getCliente());
                corRepository.save(dispositivo.getCor());
            }

            conexaoRepository.save(dispositivo.getConexao());
            dispositivoRepository.save(dispositivo);
            //      comandoService.sincronizar(dispositivo.getMac());
            logRepository.save(Log.builder()
                    .cor(null)
                    .id(String.valueOf(request.getId()))
                    .tipoLog(TipoLog.DEVICE)
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .mensagem("Dispositivo foi alterado a configuracao")
                    .build());
        }
    }

    public void ativarDispositivos(String token, long id) {
        Optional<Dispositivo> dispositivoOptional = Optional.empty();
        var user = authService.recuperarUsuarioLogado(token);
        if (authService.validaPermissao(token, Role.ROOT))
            dispositivoOptional = dispositivoRepository.findById(id);
        else dispositivoRepository.findByClienteAndId(user.getCliente().getId(), id);
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setAtivo(!dispositivo.isAtivo());

            if (dispositivo.isAtivo() && dispositivoRepository.countByAtivo(true) >= quantidadeClientes) {
                throw new ExceptionResponse("O limite de dispositivos ativos foi excedido em " + quantidadeClientes);
            }

            if (!dispositivo.isAtivo()) {
                dispositivo.getConexao().setStatus(StatusConexao.Offline);
                conexaoRepository.save(dispositivo.getConexao());
            }

            dispositivoRepository.save(dispositivo);
            logRepository.save(Log.builder()
                    .cor(null)
                    .id(String.valueOf(id))
                    .tipoLog(TipoLog.DEVICE)
                    .data(LocalDateTime.now())
                    .mensagem("Dispositivo foi " + (dispositivo.isAtivo() ? "ativado" : "desativado"))
                    .build());
        }
    }

    public DispositivoResponse buscarPorMac(String token, UUID clienteId, long id) {
        if (authService.validaPermissao(token, Role.ROOT))
            return dispositivoMapper.toResponse(dispositivoRepository.findById(id).orElseThrow());
        return dispositivoMapper.toResponse(dispositivoRepository.findByClienteAndId(clienteId, id).orElseThrow());
    }

    public Page<DispositivoResponse> pesquisarDispositivos(String token, String pesquisa, Pageable pageable) {
        var user = authService.recuperarUsuarioLogado(token);
        if (authService.validaPermissao(user, Role.ROOT))
            return dispositivoRepository.findByIdAndNomeContaining(pesquisa, pageable).map(dispositivoMapper::toResponse);
        return dispositivoRepository.findByIdAndNomeContainingAndClienteId(user.getCliente().getId(), pesquisa, pageable).map(dispositivoMapper::toResponse);
    }

    public Page<DispositivoResponse> listaTodosDispositivos(String token, Pageable pageable) {
        var user = authService.recuperarUsuarioLogado(token);
        if (authService.validaPermissao(user, Role.ROOT))
            return dispositivoRepository.findAll(pageable).map(dispositivoMapper::toResponse);
        return dispositivoRepository.findAllByCliente(user.getCliente().getId(), pageable).map(dispositivoMapper::toResponse);
    }

    public Page<DispositivoResponse> listaTodosDispositivosPorFiltro(String token, UUID clienteId, Filtro filtro, Pageable pageable) {
        return listaTodosEntidadeDispositivosPorFiltro(clienteId, token, filtro, pageable).map(dispositivoMapper::toResponse);
    }

    public List<DispositivoResponse> listaTodosDispositivosPorFiltro(String token, Filtro filtro) {
        var user = authService.recuperarUsuarioLogado(token);
        if (authService.validaPermissao(user, Role.ROOT))
            filtro = Filtro.TODOS;
        return listaTodosEntidadeDispositivosPorFiltro(user.getCliente().getId(), filtro).stream().map(dispositivoMapper::toResponse).collect(Collectors.toList());
    }

    public Page<Dispositivo> listaTodosEntidadeDispositivosPorFiltro(UUID clienteId, String token, Filtro filtro, Pageable pageable) {

       var isRoot = (authService.validaPermissao(token, Role.ROOT));

        switch (filtro) {
            case TODOS -> {
                return dispositivoRepository.findAll(pageable);
            }
            case ATIVO -> {
                if(isRoot)
                    return dispositivoRepository.findAllByAtivo(true, pageable);
                return dispositivoRepository.findAllByAtivo(clienteId, true, pageable);
            }
            case INATIVO -> {
                if(isRoot)
                    return dispositivoRepository.findAllByAtivo(false, pageable);
                return dispositivoRepository.findAllByAtivo(clienteId, false, pageable);
            }
            case OFFLINE -> {
                if(isRoot)
                    return buscarDispositivosAtivosTempo( 5, pageable);
                return buscarDispositivosAtivosTempo(clienteId, 5, pageable);
            }

        }
        return Page.empty();
    }

    public List<Dispositivo> listaTodosEntidadeDispositivosPorFiltro(UUID clienteId, Filtro filtro) {
        switch (filtro) {
            case TODOS -> {
                return dispositivoRepository.findAll();
            }
            case ATIVO -> {
                return dispositivoRepository.findAllByAtivo(clienteId, true);
            }
            case INATIVO -> {
                return dispositivoRepository.findAllByAtivo(clienteId, false);
            }
            case OFFLINE -> {
                return buscarDispositivosAtivosTempo(clienteId, 5);
            }
            case CORDENADAS -> {
                return buscarDispositivosAtivosComAgendaPesquisada(clienteId);
            }
        }
        return Collections.emptyList();
    }


    public Page<DispositivoResponse> buscarDispositivosAtivosComMaisDe5Minutos(UUID clienteId, Pageable pageable) {
        return buscarDispositivosAtivosTempo(clienteId, 5, pageable).map(dispositivoMapper::toResponse);
    }

    public List<Dispositivo> buscarDispositivosAtivosTempo(UUID clienteId, long minutos) {
        LocalDateTime cincoMinutosAtras = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(minutos);
        Date dataLimite = Date.from(cincoMinutosAtras.atZone(ZoneOffset.UTC).toInstant());
        return dispositivoRepository.findAllAtivosComUltimaAtualizacaoAntes(clienteId, dataLimite);
    }

    private Page<Dispositivo> buscarDispositivosAtivosTempo(long minutos, Pageable pageable) {
        LocalDateTime cincoMinutosAtras = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(minutos);
        Date dataLimite = Date.from(cincoMinutosAtras.atZone(ZoneOffset.UTC).toInstant());
        return dispositivoRepository.findAllAtivosComUltimaAtualizacaoAntes(dataLimite, pageable);
    }
    public Page<Dispositivo> buscarDispositivosAtivosTempo(UUID clienteId, long minutos, Pageable pageable) {
        LocalDateTime cincoMinutosAtras = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(minutos);
        Date dataLimite = Date.from(cincoMinutosAtras.atZone(ZoneOffset.UTC).toInstant());
        return dispositivoRepository.findAllAtivosComUltimaAtualizacaoAntes(clienteId, dataLimite, pageable);
    }

    public List<Dispositivo> buscarDispositivosAtivosComAgendaPesquisada(UUID clienteId) {
        List<Dispositivo> dispositivos = dispositivoRepository.findAllByAtivo(clienteId, true);
        if (!dispositivos.isEmpty()) {
            dispositivos.forEach(device -> {
                Agenda agenda = agendaDeviceService.buscarAgendaDipositivoPrevistaHoje(clienteId, device.getId());
                if (agenda != null && agenda.getCor() != null) {
                    device.setCor(agenda.getCor());
                }
            });
        }
        return dispositivos;
    }

}
