package br.com.totem.service;

import br.com.totem.controller.response.AgendaResponse;
import br.com.totem.mapper.AgendaMapper;
import br.com.totem.model.Agenda;
import br.com.totem.model.constantes.Role;
import br.com.totem.repository.AgendaRepository;
import lombok.RequiredArgsConstructor;
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
    private final AuthService authService;

    public Page<AgendaResponse> listaTodosAgendas(String token, Pageable pageable) {
        var user = authService.recuperarUsuarioLogado(token);
        if (authService.validaPermissao(user, Role.ROOT))
            return agendaRepository.findAll(pageable).map(agendaMapper::toResponse);
        return agendaRepository.findAllByCliente(user.getCliente().getId(), pageable).map(agendaMapper::toResponse);
    }

    public List<AgendaResponse> listaTodosAgendasPorDispositivo(long id) {
        return agendaRepository.findAgendasByDispositivoId(id).stream().map(agendaMapper::toResponse).collect(Collectors.toList());
    }

    public List<AgendaResponse> listaTodosAgendasPorCor(UUID ID) {
        return agendaRepository.findAgendasByCorId(ID).stream().map(agendaMapper::toResponse).collect(Collectors.toList());
    }

    public List<Agenda> listaTodosAgendasPrevistaHoje() {
        LocalDate data = LocalDateTime.now().plusHours(3).toLocalDate();
        return agendaRepository.findAgendasByDataDentroDoIntervalo(data);
    }

    public Agenda buscarAgendaDipositivoPrevistaHoje(UUID clienteId, long id) {
        List<Agenda> agendaList = agendaRepository.findFirstByDataAndDispositivo(clienteId, LocalDate.now(), LocalDate.now(), id, UUID.randomUUID());
        if (!agendaList.isEmpty()) {
            return agendaList.get(0);
        }
        return null;
    }

    public boolean possuiAgendaDipositivoPrevistaHoje(UUID clienteId, Agenda agenda, long id) {
        return !agendaRepository.findFirstByDataAndDispositivo(clienteId, agenda.getInicio().toLocalDate(), agenda.getTermino().toLocalDate(), id, agenda.getId()).isEmpty();
    }

}
