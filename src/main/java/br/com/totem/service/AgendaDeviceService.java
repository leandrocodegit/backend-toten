package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.AgendaRequest;
import br.com.totem.controller.request.Filtro;
import br.com.totem.controller.response.AgendaResponse;
import br.com.totem.mapper.AgendaMapper;
import br.com.totem.mapper.ConfiguracaoMapper;
import br.com.totem.model.Agenda;
import br.com.totem.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgendaDeviceService {

    @Autowired
    private AgendaRepository agendaRepository;
    @Autowired
    private AgendaMapper agendaMapper;

    public Page<AgendaResponse> listaTodosAgendas(Pageable pageable) {
        return agendaRepository.findAll(pageable).map(agendaMapper::toResponse);
    }

    public List<AgendaResponse> listaTodosAgendasPorDispositivo(String mac) {
        return agendaRepository.findAgendasByDispositivoId(mac).stream().map(agendaMapper::toResponse).collect(Collectors.toList());
    }

    public List<AgendaResponse> listaTodosAgendasPorConfiguracao(UUID ID) {
        return agendaRepository.findAgendasByConfiguracaoId(ID).stream().map(agendaMapper::toResponse).collect(Collectors.toList());
    }

    public List<Agenda> listaTodosAgendasPrevistaHoje() {
        return agendaRepository.findAgendasByDataDentroDoIntervalo(LocalDate.now());
    }
    public Agenda buscarAgendaDipositivoPrevistaHoje(String mac) {
        List<Agenda> agendaList = agendaRepository.findFirstByDataAndDispositivo(LocalDate.now(), LocalDate.now(), mac, UUID.randomUUID());
        if(!agendaList.isEmpty()){
            return agendaList.get(0);
        }
        return null;
    }

    public boolean possuiAgendaDipositivoPrevistaHoje(Agenda agenda, String mac) {
        return !agendaRepository.findFirstByDataAndDispositivo(agenda.getInicio(), agenda.getTermino(), mac, agenda.getId()).isEmpty();
    }

    public void atualizarDataExecucao(Agenda agenda) {
        agenda.setExecucao(LocalDate.now());
        agendaRepository.save(agenda);
    }
}
