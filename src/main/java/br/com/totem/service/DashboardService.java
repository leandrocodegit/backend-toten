package br.com.totem.service;

import br.com.totem.controller.response.DispositivoDashResponse;
import br.com.totem.mapper.DispositivoMapper;
import br.com.totem.model.Cliente;
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

    public Dashboard buscarDashboard(UUID clienteId) {
        var dash = dashBoardrepository.findById(clienteId);
        if (dash.isPresent()) {
            return dash.get();
        }
        return gerarDash(clienteId);
    }

    public Dashboard gerarDash(UUID clienteId) {


        Dashboard dashboard = new Dashboard();
        dashboard.setId(clienteId);
        dashboard.setAtualizacao(LocalDateTime.now());
        dashboard.setUsuariosAtivos(userRepository.countByClienteAndStatus(Cliente.builder().id(clienteId).principal(false).build(),true));
        dashboard.setUsuariosInativos(userRepository.countByClienteAndStatus(Cliente.builder().id(clienteId).principal(false).build(),false));


        dashboard.setDispositivos(new DispositivoDashResponse());

        Map<String, DispositivoPorCor> cores = new HashMap<>();

        dispositivoRepository.findAllByAtivo(clienteId, true).stream().map(dispositivoMapper::toResume).toList().forEach(device -> {
            if (device.getConexao().getStatus() != null && device.getConexao().getStatus().equals(StatusConexao.Online)) {
                dashboard.getDispositivos().setOnline(dashboard.getDispositivos().getOnline() + 1);
            } else {
                dashboard.getDispositivos().setOffline(dashboard.getDispositivos().getOffline() + 1);
            }

            if (device.getCor() != null) {
               device.getCor().getParametros().forEach(parametro -> {
                    if (cores.containsKey(parametro.getCorHexa().get(0))) {
                       DispositivoPorCor cor = cores.get(parametro.getCorHexa().get(0));
                       cor.setQuantidade(cor.getQuantidade() + 1);
                    } else {
                        cores.put(parametro.getCorHexa().get(0), new DispositivoPorCor(parametro.getCorHexa().get(0), 1));
                    }
                });
            }
        });
        dashboard.setCores(cores.values().stream().toList());
        dashboard.getDispositivos().setTotal(dashboard.getDispositivos().getOffline() + dashboard.getDispositivos().getOnline());


        List<LogConexao> l = logRepository.findLogsGroupedByCommandAndHour(clienteId);
        dashboard.setLogsConexao(l);


        dashBoardrepository.save(dashboard);
        atualizarConexoes(clienteId);
        atualizarDashboardAgendas(clienteId);
        return dashBoardrepository.findById(clienteId).get();
    }

    public void atualizarConexoes(UUID clienteId) {
        Optional<Dashboard> optionalDashboard = dashBoardrepository.findById(clienteId);
        Map<String, DispositivoPorCor> cores = new HashMap<>();


        if (optionalDashboard.isPresent()) {
            optionalDashboard.get().setDispositivos(new DispositivoDashResponse());
            dispositivoRepository.findAllByAtivo(clienteId, true).stream().map(dispositivoMapper::toResume).toList().forEach(device -> {
                if (device.getConexao().getStatus() != null && device.getConexao().getStatus().equals(StatusConexao.Online)) {
                    optionalDashboard.get().getDispositivos().setOnline(optionalDashboard.get().getDispositivos().getOnline() + 1);
                } else {
                    optionalDashboard.get().getDispositivos().setOffline(optionalDashboard.get().getDispositivos().getOffline() + 1);
                }
                device.getCor().getParametros().forEach(parametro -> {
                    if (cores.containsKey(parametro.getCorHexa().get(0))) {
                        DispositivoPorCor cor = cores.get(parametro.getCorHexa().get(0));
                        cor.setQuantidade(cor.getQuantidade() + 1);
                    } else {
                        cores.put(parametro.getCorHexa().get(0), new DispositivoPorCor(parametro.getCorHexa().get(0), 1));
                    }
                });
            });
            optionalDashboard.get().setCores(cores.values().stream().toList());
            dashBoardrepository.save(optionalDashboard.get());
        }
    }

    public void atualizarDashboardAgendas(UUID clienteId) {

        Optional<Dashboard> optionalDashboard = dashBoardrepository.findById(clienteId);
        if (optionalDashboard.isPresent()) {
            Map<String, DispositivoPorCor> agendas = new HashMap<>();
            var agendasAtivas = agendaRepository.findAllByAtivo(clienteId, true);
            if(agendasAtivas.isEmpty()){
                agendas.put("grey", new DispositivoPorCor("transparent", 1));
            }else {
                agendasAtivas.forEach(device -> {
                    if (device.getCor() != null) {
                        device.getCor().getParametros().forEach(parametro -> {
                            if (agendas.containsKey(parametro.getCorHexa().get(0))) {
                                DispositivoPorCor cor = agendas.get(parametro.getCorHexa().get(0));
                                cor.setQuantidade(cor.getQuantidade() + 1);
                            } else {
                                agendas.put(parametro.getCorHexa().get(0), new DispositivoPorCor(parametro.getCorHexa().get(0), 1));
                            }
                        });
                    }
                });
            }
            Map<String, DispositivoPorCor> agendasExecucao = new HashMap<>();
            agendaRepository.findAllAgendasByDataDentroDoIntervalo(clienteId, LocalDate.now()).forEach(agenda -> {
                if (agenda.getCor() != null && agenda.isAtivo() && agenda.getDispositivos().size() > 0) {
                    agenda.getCor().getParametros().forEach(parametro -> {
                        if (agendasExecucao.containsKey(parametro.getCorHexa().get(0))) {
                            DispositivoPorCor cor = agendasExecucao.get(parametro.getCorHexa().get(0));
                            cor.setQuantidade(cor.getQuantidade() + 1);
                        } else {
                            agendasExecucao.put(parametro.getCorHexa().get(0), new DispositivoPorCor(parametro.getCorHexa().get(0), 1));
                        }
                    });
                }
            });

            optionalDashboard.get().setAgendasExecucao(agendasExecucao.values().stream().toList());
            optionalDashboard.get().setAgendas(agendas.values().stream().toList());
            dashBoardrepository.save(optionalDashboard.get());
        }
    }

}
