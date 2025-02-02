package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.AgendaRequest;
import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.response.AgendaResponse;
import br.com.totem.mapper.AgendaMapper;
import br.com.totem.mapper.CorMapper;
import br.com.totem.mapper.DispositivoMapper;
import br.com.totem.model.Agenda;
import br.com.totem.model.Log;
import br.com.totem.model.constantes.Comando;
import br.com.totem.model.constantes.ModoOperacao;
import br.com.totem.repository.AgendaRepository;
import br.com.totem.repository.DispositivoRepository;
import br.com.totem.repository.LogRepository;
import br.com.totem.repository.OperacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public void criarAgenda(AgendaRequest request) {
        if (request.getId() == null || !agendaRepository.findById(request.getId()).isPresent()) {
            if (request.getCor() == null || request.getCor().getId() == null) {
                throw new ExceptionResponse("Configuração de cor é obrigatorio");
            }
            request.setId(UUID.randomUUID());

            Agenda agenda = agendaMapper.toEntity(request);
            agenda.setInicio(LocalDateTime.of(request.getInicio(), LocalTime.of(0,0,0)));
            agenda.setTermino(LocalDateTime.of(request.getInicio(), LocalTime.of(0,0,0)));
            agendaRepository.save(agenda);
            validarConflitos(agenda);
            logRepository.save(Log.builder()
                    .cor(null)
                    .mac(request.getId().toString())
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(request.toString())
                    .mensagem("Nova agenda criada")
                    .build());
        } else {
            throw new ExceptionResponse("Agenda já existe");
        }
    }

    public void alterarAgenda(AgendaRequest request, boolean removerConflitos) {
        Optional<Agenda> agendaOptional = agendaRepository.findById(request.getId());

        if (agendaOptional.isPresent()) {

            Agenda agenda = agendaOptional.get();
            agenda.setNome(request.getNome());
            agenda.setAtivo(request.isAtivo());
            agenda.setInicio(LocalDateTime.of(request.getInicio(), LocalTime.of(0,0,0)));
            agenda.setTermino(LocalDateTime.of(request.getTermino(), LocalTime.of(0,0,0)));
            agenda.setTodos(request.isTodos());
            agenda.setExecucao(null);
            if (Boolean.TRUE.equals(request.isTodos())) {
                agenda.setDispositivos(Collections.emptyList());
            }else {
                agenda.setDispositivos(request.getDispositivos());
            }

            if (removerConflitos) {
                for (int i = 0; i < agenda.getDispositivos().size(); i++) {
                    if (agendaDeviceService.possuiAgendaDipositivoPrevistaHoje(agenda, agenda.getDispositivos().get(i)) || verificarSeTemAgendaParaTodos(agenda)) {
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
                    .mac(agenda.getId().toString())
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(agenda.toString())
                    .mensagem("Agenda foi atualizada")
                    .build());
            verificaSeAgendaHoje(agenda);
            dashboardService.atualizarDashboardAgendas();
            comandoService.sincronizarTodos(false);
        } else {
            throw new ExceptionResponse("Agenda não existe");
        }
    }


    public void verificaSeAgendaHoje(Agenda agenda){
        var bool = agenda.getInicio().toLocalDate().equals(LocalDate.now()) || agenda.getInicio().toLocalDate().isBefore(LocalDate.now());
        if(bool)
            bool = agenda.getTermino().toLocalDate().equals(LocalDate.now()) || agenda.getTermino().toLocalDate().isAfter(LocalDate.now());;
        if(bool){
            var dispositivos = dispositivoRepository.findAllById(agenda.getDispositivos());
            if(agenda.isTodos())
                dispositivos = dispositivoRepository.findAll();
            dispositivos.forEach(device -> {
                if(agenda.isAtivo()){
                    if(!device.isIgnorarAgenda()) {
                        device.getOperacao().setModoOperacao(ModoOperacao.AGENDA);
                        device.getOperacao().setAgenda(agenda);
                        operacaoRepository.save(device.getOperacao());
                    }
                }
            });
        }
    }
    private void validarConflitos(Agenda agenda){
        agenda.getDispositivos().forEach(device -> {
            if(verificarSeTemAgendaParaTodos(agenda)){
                throw new ExceptionResponse("Conflito de datas");
            }
            if (agendaDeviceService.possuiAgendaDipositivoPrevistaHoje(agenda, device)) {
                throw new ExceptionResponse("Conflito de datas");
            }
        });
    }

    public void removerAgenda(UUID id) {
        comandoService.sincronizarTodos(false);
        agendaRepository.deleteById(id);
    }

    public List<AgendaResponse> agendasDoMesAtual(boolean ativo) {
        Sort sort = Sort.by(Sort.Order.asc("inicio"));
        return agendaRepository.findAllDoMesAtualInOrderByInicioDesc(LocalDate.now().getMonthValue(), ativo, sort).stream().map(agendaMapper::toResponse).toList();
    }

    public boolean verificarSeTemAgendaParaTodos(Agenda agenda) {
        return !agendaRepository.findAllAgendasByDataDentroDoIntervaloTodosDispositivos(agenda.getId(), agenda.getInicio().toLocalDate(), agenda.getTermino().toLocalDate()).isEmpty();
    }
}
