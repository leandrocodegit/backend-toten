package br.com.totem.service;

import br.com.totem.controller.response.DashboardResponse;
import br.com.totem.controller.response.LogConexaoResponse;
import br.com.totem.mapper.DispositivoMapper;
import br.com.totem.model.DispositivoPorCor;
import br.com.totem.repository.AgendaRepository;
import br.com.totem.repository.DispositivoRepository;
import br.com.totem.repository.LogRepository;
import br.com.totem.repository.UserRepository;
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
public class DashboardService {

    @Autowired
    private DispositivoRepository dispositivoRepository;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private AgendaRepository agendaRepository;
    @Autowired
    private DispositivoMapper dispositivoMapper;
    @Autowired
    private UserRepository userRepository;


    public DashboardResponse gerarDash() {

        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setUsuariosAtivos(userRepository.countByStatus(true));
        dashboardResponse.setUsuariosInativos(userRepository.countByStatus(false));

        dashboardResponse.setDispositivos(dispositivoRepository.findAll().stream().map(dispositivoMapper::toResponse).collect(Collectors.toList()));

        Map<String, DispositivoPorCor> cores = new HashMap<>();

        dashboardResponse.getDispositivos().forEach(device -> {
            if (device.getConfiguracao() != null) {
                if (cores.containsKey(device.getConfiguracao().getPrimaria())) {
                    DispositivoPorCor cor = cores.get(device.getConfiguracao().getPrimaria());
                    cor.setQuantidade(cor.getQuantidade() + 1);
                } else {
                    cores.put(device.getConfiguracao().getPrimaria(), new DispositivoPorCor(device.getConfiguracao().getPrimaria(), 1));
                }
            }
        });
        dashboardResponse.setCores(cores.values().stream().toList());


        Map<String, DispositivoPorCor> agendas = new HashMap<>();
        agendaRepository.findAllAgendasByDataDentroDoIntervalo(LocalDate.now()).forEach(device -> {
            if (device.getConfiguracao() != null) {
                if (agendas.containsKey(device.getConfiguracao().getPrimaria())) {
                    DispositivoPorCor cor = agendas.get(device.getConfiguracao().getPrimaria());
                    cor.setQuantidade(cor.getQuantidade() + 1);
                } else {
                    agendas.put(device.getConfiguracao().getPrimaria(), new DispositivoPorCor(device.getConfiguracao().getPrimaria(), 1));
                }
            }
        });
        Pageable pageable = PageRequest.of(0, 100);

        dashboardResponse.setAgendas(agendas.values().stream().toList());
        dashboardResponse.setLogs(logRepository.findAllByComandoInOrderByDataDesc(List.of("ENVIADO", "CONCLUIDO", "SINCRONIZAR", "SISTEMA", "NENHUM_DEVICE", "OFFLINE"),pageable).getContent());
        List<LogConexaoResponse> l = logRepository.findLogsGroupedByCommandAndHour();
        dashboardResponse.setLogsConexao(l);
        return dashboardResponse;
    }

}
