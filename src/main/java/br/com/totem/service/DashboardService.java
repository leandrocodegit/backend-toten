package br.com.totem.service;

import br.com.totem.controller.response.DispositivoDashResponse;
import br.com.totem.mapper.DispositivoMapper;
import br.com.totem.model.Dashboard;
import br.com.totem.model.DispositivoPorCor;
import br.com.totem.model.LogConexao;
import br.com.totem.model.constantes.Comando;
import br.com.totem.model.constantes.StatusConexao;
import br.com.totem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DispositivoRepository dispositivoRepository;
    private final LogRepository logRepository;
    private final AgendaRepository agendaRepository;
    private final DispositivoMapper dispositivoMapper;
    private final UserRepository userRepository;
    private final DashBoardrepository dashBoardrepository;
    private final UUID id = UUID.fromString("dba60104-517a-4190-9d1d-77cf1e6d1442");

    public Dashboard buscarDashboard() {
        var dash = dashBoardrepository.findById(id);
        if (dash.isPresent()) {
            return dash.get();
        }
        return gerarDash();
    }

    public Dashboard gerarDash() {


        Dashboard dashboard = new Dashboard();
        dashboard.setId(id);
        dashboard.setAtualizacao(LocalDateTime.now());
        dashboard.setUsuariosAtivos(userRepository.countByStatus(true));
        dashboard.setUsuariosInativos(userRepository.countByStatus(false));


        dashboard.setDispositivos(new DispositivoDashResponse());

        Map<String, DispositivoPorCor> cores = new HashMap<>();

        dispositivoRepository.findAllByAtivo(true).stream().map(dispositivoMapper::toResume).toList().forEach(device -> {
            if(device.getConexao().getStatus() != null && device.getConexao().getStatus().equals(StatusConexao.Online)){
                dashboard.getDispositivos().setOnline(dashboard.getDispositivos().getOnline() + 1);
            }else{
                dashboard.getDispositivos().setOffline(dashboard.getDispositivos().getOffline() + 1);
            }

            if (device.getCor() != null) {
                if (cores.containsKey(device.getCor().getPrimaria())) {
                    DispositivoPorCor cor = cores.get(device.getCor().getPrimaria());
                    cor.setQuantidade(cor.getQuantidade() + 1);
                } else {
                    cores.put(device.getCor().getPrimaria(), new DispositivoPorCor(device.getCor().getPrimaria(), 1));
                }
            }
        });
        dashboard.setCores(cores.values().stream().toList());
        dashboard.getDispositivos().setTotal(dashboard.getDispositivos().getOffline() + dashboard.getDispositivos().getOnline());


        List<LogConexao> l = logRepository.findLogsGroupedByCommandAndHour();
        dashboard.setLogsConexao(l);


        dashBoardrepository.save(dashboard);
        atualizarConexoes();
        atualizarDashboardAgendas();
        return dashboard;
    }

    public void atualizarConexoes(){
            Optional<Dashboard> optionalDashboard = dashBoardrepository.findById(id);
            Map<String, DispositivoPorCor> cores = new HashMap<>();
            optionalDashboard.get().setDispositivos(new DispositivoDashResponse());

            if(optionalDashboard.isPresent()) {
                dispositivoRepository.findAllByAtivo(true).stream().map(dispositivoMapper::toResume).toList().forEach(device -> {
                    if (device.getConexao().getStatus() != null && device.getConexao().getStatus().equals(StatusConexao.Online)) {
                        optionalDashboard.get().getDispositivos().setOnline(optionalDashboard.get().getDispositivos().getOnline() + 1);
                    } else {
                        optionalDashboard.get().getDispositivos().setOffline(optionalDashboard.get().getDispositivos().getOffline() + 1);
                    }

                    if (device.getCor() != null) {
                        if (cores.containsKey(device.getCor().getPrimaria())) {
                            DispositivoPorCor cor = cores.get(device.getCor().getPrimaria());
                            cor.setQuantidade(cor.getQuantidade() + 1);
                        } else {
                            cores.put(device.getCor().getPrimaria(), new DispositivoPorCor(device.getCor().getPrimaria(), 1));
                        }
                    }
                });
                optionalDashboard.get().setCores(cores.values().stream().toList());
                dashBoardrepository.save(optionalDashboard.get());
        }
    }
    public void atualizarDashboardAgendas(){

        Optional<Dashboard> optionalDashboard = dashBoardrepository.findById(id);
        if(optionalDashboard.isPresent()) {
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

            optionalDashboard.get().setAgendasExecucao(agendasExecucao.values().stream().toList());
            optionalDashboard.get().setAgendas(agendas.values().stream().toList());
            dashBoardrepository.save(optionalDashboard.get());
        }
    }

}
