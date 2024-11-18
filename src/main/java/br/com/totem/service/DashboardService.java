package br.com.totem.service;

import br.com.totem.controller.response.DashboardResponse;
import br.com.totem.controller.response.LogConexaoResponse;
import br.com.totem.mapper.AgendaMapper;
import br.com.totem.mapper.DispositivoMapper;
import br.com.totem.model.DispositivoPorCor;
import br.com.totem.repository.AgendaRepository;
import br.com.totem.repository.DispositivoRepository;
import br.com.totem.repository.LogRepository;
import br.com.totem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DispositivoRepository dispositivoRepository;
    private final LogRepository logRepository;
    private final AgendaRepository agendaRepository;
    private final DispositivoMapper dispositivoMapper;
    private final UserRepository userRepository;

    public DashboardResponse gerarDash() {

        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setUsuariosAtivos(userRepository.countByStatus(true));
        dashboardResponse.setUsuariosInativos(userRepository.countByStatus(false));

        dashboardResponse.setDispositivos(dispositivoRepository.findAll().stream().map(dispositivoMapper::toResponse).collect(Collectors.toList()));

        Map<String, DispositivoPorCor> cores = new HashMap<>();

        dashboardResponse.getDispositivos().forEach(device -> {
            if (device.getCor() != null) {
                if (cores.containsKey(device.getCor().getPrimaria())) {
                    DispositivoPorCor cor = cores.get(device.getCor().getPrimaria());
                    cor.setQuantidade(cor.getQuantidade() + 1);
                } else {
                    cores.put(device.getCor().getPrimaria(), new DispositivoPorCor(device.getCor().getPrimaria(), 1));
                }
            }
        });
        dashboardResponse.setCores(cores.values().stream().toList());

        Map<String, DispositivoPorCor> agendas = new HashMap<>();
        agendaRepository.findAllByAtivo(true).forEach(device -> {
            if (device.getCor() != null) {
                if (agendas.containsKey(device.getCor().getPrimaria())) {
                    DispositivoPorCor cor = agendas.get(device.getCor().getPrimaria());
                    cor.setQuantidade(cor.getQuantidade() + 1);
                } else {
                    agendas.put(device.getCor().getPrimaria(), new DispositivoPorCor(device.getCor().getPrimaria(), 1));
                }
            }
        });

        Map<String, DispositivoPorCor> agendasExecucao = new HashMap<>();
        agendaRepository.findAllAgendasByDataDentroDoIntervalo(LocalDate.now()).forEach(device -> {
            if (device.getCor() != null) {
                if (agendasExecucao.containsKey(device.getCor().getPrimaria())) {
                    DispositivoPorCor cor = agendasExecucao.get(device.getCor().getPrimaria());
                    cor.setQuantidade(cor.getQuantidade() + 1);
                } else {
                    agendasExecucao.put(device.getCor().getPrimaria(), new DispositivoPorCor(device.getCor().getPrimaria(), 1));
                }
            }
        });


        Pageable pageable = PageRequest.of(0, 100);

        dashboardResponse.setAgendas(agendas.values().stream().toList());
        dashboardResponse.setAgendasExecucao(agendasExecucao.values().stream().toList());
        dashboardResponse.setLogs(logRepository.findAllByComandoInOrderByDataDesc(List.of("ENVIADO", "CONCLUIDO", "SINCRONIZAR", "SISTEMA", "NENHUM_DEVICE", "OFFLINE"),pageable).getContent());
        List<LogConexaoResponse> l = logRepository.findLogsGroupedByCommandAndHour();
        dashboardResponse.setLogsConexao(l);
        return dashboardResponse;
    }

}
