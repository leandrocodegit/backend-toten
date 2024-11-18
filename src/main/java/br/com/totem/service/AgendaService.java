package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.AgendaRequest;
import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.response.AgendaResponse;
import br.com.totem.mapper.AgendaMapper;
import br.com.totem.mapper.CorMapper;
import br.com.totem.model.Agenda;
import br.com.totem.model.Log;
import br.com.totem.model.constantes.Comando;
import br.com.totem.repository.AgendaRepository;
import br.com.totem.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;
    private final CorMapper configuracaoMapper;
    private final DispositivoService dispositivoService;
    private final AgendaDeviceService agendaDeviceService;
    private final ComandoService comandoService;
    private final LogRepository logRepository;

    public void criarAgenda(AgendaRequest request) {
        if (request.getId() == null || !agendaRepository.findById(request.getId()).isPresent()) {
            if(request.getCor() == null || request.getCor().getId() == null){
                throw new ExceptionResponse("Configuração de cor é obrigatorio");
            }
            request.setId(UUID.randomUUID());
            agendaRepository.save(agendaMapper.toEntity(request));
            logRepository.save(Log.builder()
                    .cor(null)
                    .mac(request.getId().toString())
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(request.toString())
                    .mensagem( "Nova agenda criada")
                    .build());
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
            agenda.setExecucao(null);
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
            if (request.getCor() != null && request.getCor().getId() != null)
                agenda.setCor(configuracaoMapper.toEntity(request.getCor()));
            agendaRepository.save(agenda);
            logRepository.save(Log.builder()
                    .cor(null)
                    .mac(agenda.getId().toString())
                    .data(LocalDateTime.now())
                    .comando(Comando.CONFIGURACAO)
                    .descricao(agenda.toString())
                    .mensagem( "Agenda foi atualizada")
                    .build());
        } else {
            throw new ExceptionResponse("Agenda não existe");
        }
    }

    public void removerAgenda(UUID id) {
        comandoService.sincronizarTodos();
        agendaRepository.deleteById(id);
    }

    public List<AgendaResponse> agendasDoMesAtual(boolean ativo){
        Sort sort = Sort.by(Sort.Order.asc("inicio"));
       return agendaRepository.findAllDoMesAtualInOrderByInicioDesc(LocalDate.now().getMonthValue(),ativo, sort).stream().map(agendaMapper::toResponse).toList();
    }
}
