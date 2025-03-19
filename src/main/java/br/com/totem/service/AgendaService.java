package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.AgendaRequest;
import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.response.AgendaResponse;
import br.com.totem.mapper.AgendaMapper;
import br.com.totem.mapper.CorMapper;
import br.com.totem.mapper.DispositivoMapper;
import br.com.totem.model.Agenda;
import br.com.totem.model.Cliente;
import br.com.totem.model.Log;
import br.com.totem.model.constantes.*;
import br.com.totem.repository.AgendaRepository;
import br.com.totem.repository.DispositivoRepository;
import br.com.totem.repository.LogRepository;
import br.com.totem.repository.OperacaoRepository;
import br.com.totem.security.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;
    private final CorMapper configuracaoMapper;
    private final AgendaDeviceService agendaDeviceService;
    private final ComandoService comandoService;
    private final LogRepository logRepository;
    private final DashboardService dashboardService;
    private final DispositivoRepository dispositivoRepository;
    private final OperacaoRepository operacaoRepository;
    private final JWTTokenProvider jwtTokenProvider;
    private final AuthService authService;


    public void criarAgenda(String token, AgendaRequest request) {
        var user = authService.recuperarUsuarioLogado(token);
        var agendaOptional = agendaRepository.findByClienteAndId(user.getCliente().getId(), request.getId());
        if (request.getId() == null || !agendaOptional.isPresent()) {
            if (request.getCor() == null || request.getCor().getId() == null) {
                throw new ExceptionResponse("Configuração de cor é obrigatorio");
            }


            Agenda agenda = agendaMapper.toEntity(request);
            validarConflitos(user.getCliente().getId(), agenda);
            agenda.setInicio(LocalDateTime.of(request.getInicio(), LocalTime.of(0, 0, 0)));
            agenda.setTermino(LocalDateTime.of(request.getInicio(), LocalTime.of(0, 0, 0)));
            agenda.setId(UUID.randomUUID());
            agenda.setCliente(Cliente.builder().id(user.getCliente().getId()).principal(false).build());
            agendaRepository.save(agenda);
            logRepository.save(Log.builder()
                    .cor(null)
                    .id(agenda.getId().toString())
                    .tipoLog(TipoLog.AGENDA)
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(agenda.getNome())
                    .mensagem("Nova agenda criada")
                    .build());
        } else {
            throw new ExceptionResponse("Agenda já existe");
        }
    }

    public void alterarAgenda(String token, AgendaRequest request, boolean removerConflitos) {
        Optional<Agenda> agendaOptional = Optional.empty();
        var user = authService.recuperarUsuarioLogado(token);
        if (authService.validaPermissao(token, Role.ROOT))
            agendaOptional = agendaRepository.findById(request.getId());
        else agendaOptional = agendaRepository.findByClienteAndId(user.getCliente().getId(), request.getId());

        if (agendaOptional.isPresent()) {
            validarConflitos(user.getCliente().getId(), agendaMapper.toEntity(request));
            Agenda agenda = agendaOptional.get();

            agenda.setNome(request.getNome());
            agenda.setAtivo(request.isAtivo());
            agenda.setInicio(LocalDateTime.of(request.getInicio(), LocalTime.of(0, 0, 0)));
            agenda.setTermino(LocalDateTime.of(request.getTermino(), LocalTime.of(0, 0, 0)));
            agenda.setTodos(request.isTodos());
            agenda.setExecucao(null);
            if (Boolean.TRUE.equals(request.isTodos())) {
                agenda.setDispositivos(Collections.emptyList());
            } else {
                agenda.setDispositivos(request.getDispositivos());
            }

            if (removerConflitos) {
                for (int i = 0; i < agenda.getDispositivos().size(); i++) {
                    if (agendaDeviceService.possuiAgendaDipositivoPrevistaHoje(user.getCliente().getId(), agenda, agenda.getDispositivos().get(i)) || verificarSeTemAgendaParaTodos(agenda)) {
                        agenda.getDispositivos().remove(agenda.getDispositivos().get(i));
                    }
                }
            } else {

            }
            if (request.getCor() != null && request.getCor().getId() != null)
                agenda.setCor(configuracaoMapper.toEntity(request.getCor()));
            agendaRepository.save(agenda);
            logRepository.save(Log.builder()
                    .cor(null)
                    .id(agenda.getId().toString())
                    .tipoLog(TipoLog.AGENDA)
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(agenda.getNome())
                    .mensagem("Agenda foi atualizada")
                    .build());
            verificaSeAgendaHoje(agenda);
            dashboardService.atualizarDashboardAgendas(user.getCliente().getId());
            comandoService.sincronizarTodos(user.getEmail(), false);
        } else {
            throw new ExceptionResponse("Agenda não existe");
        }
    }


    public void verificaSeAgendaHoje(Agenda agenda) {
        var bool = agenda.getInicio().toLocalDate().equals(LocalDate.now()) || agenda.getInicio().toLocalDate().isBefore(LocalDate.now());
        if (bool)
            bool = agenda.getTermino().toLocalDate().equals(LocalDate.now()) || agenda.getTermino().toLocalDate().isAfter(LocalDate.now());
        ;
        if (bool) {
            var dispositivos = dispositivoRepository.findAllById(agenda.getDispositivos());
            if (agenda.isTodos())
                dispositivos = dispositivoRepository.findAll();
            dispositivos.forEach(device -> {
                if (agenda.isAtivo()) {
                    if (!device.isIgnorarAgenda()) {
                        device.getOperacao().setModoOperacao(ModoOperacao.AGENDA);
                        device.getOperacao().setAgenda(agenda);
                        operacaoRepository.save(device.getOperacao());
                    }
                }
            });
        }
    }

    private void validarConflitos(UUID clienteId, Agenda agenda) {
        agenda.getDispositivos().forEach(device -> {
            if (verificarSeTemAgendaParaTodos(agenda)) {
                throw new ExceptionResponse("Conflito de datas");
            }
            if (agendaDeviceService.possuiAgendaDipositivoPrevistaHoje(clienteId, agenda, device)) {
                throw new ExceptionResponse("Conflito de datas");
            }
        });
    }

    public void removerAgenda(UUID id, String user) {
        agendaRepository.deleteById(id);
        comandoService.sincronizarTodos(user, false);
    }

    public List<AgendaResponse> agendasDoMesAtual(String token, boolean ativo) {
        var user = authService.recuperarUsuarioLogado(token);
        Sort sort = Sort.by(Sort.Order.asc("inicio"));
        if (authService.validaPermissao(token, Role.ROOT))
            return agendaRepository.findAllDoMesAtualInOrderByInicioDesc(LocalDate.now().getMonthValue(), ativo, sort).stream().map(agendaMapper::toResponse).toList();
        return agendaRepository.findAllDoMesAtualInOrderByInicioDesc(user.getCliente().getId(), LocalDate.now().getMonthValue(), ativo, sort).stream().map(agendaMapper::toResponse).toList();
    }

    public boolean verificarSeTemAgendaParaTodos(Agenda agenda) {
        List<Agenda> agendas = new ArrayList<>();
        if (agenda.getId() == null)
            agendas = agendaRepository.findByDispositivosOuTodosAtivos(agenda.getDispositivos());
        else agendas = agendaRepository.findByDispositivosOuTodosAtivos(agenda.getId(), agenda.getDispositivos());

        var retorno = agendas.stream().anyMatch(ag -> {
            var inicio = ag.getInicio().getDayOfMonth() == agenda.getInicio().getDayOfMonth() && ag.getInicio().getMonth() == agenda.getInicio().getMonth();
            var termino = ag.getTermino().getDayOfMonth() == agenda.getTermino().getDayOfMonth() && ag.getTermino().getMonth() == agenda.getTermino().getMonth();
            if ((inicio || ag.getInicio().isBefore(agenda.getInicio())) &&
                    (termino || ag.getTermino().isAfter(agenda.getTermino())))
                return true;
            return false;
        });
        return retorno;
    }
}
