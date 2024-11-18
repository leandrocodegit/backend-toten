package br.com.totem.service;

import br.com.totem.controller.response.AgendaResponse;
import br.com.totem.mapper.AgendaMapper;
import br.com.totem.model.Agenda;
import br.com.totem.repository.AgendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaDeviceService {


    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;

    public Page<AgendaResponse> listaTodosAgendas(Pageable pageable) {
        return agendaRepository.findAll(pageable).map(agendaMapper::toResponse);
    }

    public List<AgendaResponse> listaTodosAgendasPorDispositivo(String mac) {
        return agendaRepository.findAgendasByDispositivoId(mac).stream().map(agendaMapper::toResponse).collect(Collectors.toList());
    }

    public List<AgendaResponse> listaTodosAgendasPorCor(UUID ID) {
        return agendaRepository.findAgendasByCorId(ID).stream().map(agendaMapper::toResponse).collect(Collectors.toList());
    }

    public List<Agenda> listaTodosAgendasPrevistaHoje() {
        LocalDate data = LocalDateTime.now().plusHours(3).toLocalDate();
        return agendaRepository.findAgendasByDataDentroDoIntervalo(data);
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
