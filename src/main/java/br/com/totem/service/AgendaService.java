package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.AgendaRequest;
import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.response.AgendaResponse;
import br.com.totem.mapper.AgendaMapper;
import br.com.totem.mapper.ConfiguracaoMapper;
import br.com.totem.model.Agenda;
import br.com.totem.model.Dispositivo;
import br.com.totem.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;
    @Autowired
    private AgendaMapper agendaMapper;
    @Autowired
    private ConfiguracaoMapper configuracaoMapper;
    @Autowired
    private DispositivoService dispositivoService;
    @Autowired
    private AgendaDeviceService agendaDeviceService;
    @Autowired
    private ComandoService comandoService;
    public void criarAgenda(AgendaRequest request) {
        if (request.getId() == null || !agendaRepository.findById(request.getId()).isPresent()) {
            if(request.getConfiguracao() == null || request.getConfiguracao().getId() == null){
                throw new ExceptionResponse("Configuração de cor é obrigatorio");
            }
            request.setId(UUID.randomUUID());
            agendaRepository.save(agendaMapper.toEntity(request));
        } else {
            throw new ExceptionResponse("Agenda já existe");
        }
    }

    public void alterarAgenda(AgendaRequest request, boolean removerConflitos) {
        Optional<Agenda> agendaOptional = agendaRepository.findById(request.getId());

        if (agendaOptional.isPresent()) {
            Agenda agenda = agendaOptional.get();
            agenda.setAtivo(request.isAtivo());
            agenda.setInicio(request.getInicio());
            agenda.setTermino(request.getTermino());
            agenda.setTodos(request.isTodos());
            agenda.setExecucao(LocalDate.now().minusDays(3));
            if(request.isTodos()){
                agenda.setDispositivos(dispositivoService.listaTodosEntidadeDispositivosPorFiltro(Filtro.ATIVO));
            }else{
                agenda.setDispositivos(request.getDispositivos());
            }

            if(removerConflitos){
            for (int i = 0; i < agenda.getDispositivos().size(); i++) {
                if(agendaDeviceService.possuiAgendaDipositivoPrevistaHoje(agenda, agenda.getDispositivos().get(i).getMac())){
                    agenda.getDispositivos().remove(agenda.getDispositivos().get(i));
                }
            }}else {
                agenda.getDispositivos().forEach(device -> {
                    if(agendaDeviceService.possuiAgendaDipositivoPrevistaHoje(agenda, device.getMac())){
                        throw new ExceptionResponse("Conflito de datas");
                    }
                });
            }
            if (request.getConfiguracao() != null && request.getConfiguracao().getId() != null)
                agenda.setConfiguracao(configuracaoMapper.toEntity(request.getConfiguracao()));
            agendaRepository.save(agenda);
        } else {
            throw new ExceptionResponse("Agenda não existe");
        }
    }

    public void removerAgenda(UUID id) {
        comandoService.enviarComando(dispositivoService.listaTodosDispositivosPorFiltro(Filtro.ATIVO).stream().map(device -> device.getMac()).collect(Collectors.toList()), false, false);
        agendaRepository.deleteById(id);
    }

    public void atualizarDataExecucao(Agenda agenda) {
        agenda.setExecucao(LocalDate.now());
        agendaRepository.save(agenda);
    }

    public List<AgendaResponse> agendasDoMesAtual(boolean ativo){
        Sort sort = Sort.by(Sort.Order.asc("inicio"));
       return agendaRepository.findAllDoMesAtualInOrderByInicioDesc(ativo, sort).stream().map(agendaMapper::toResponse).toList();
    }
}
