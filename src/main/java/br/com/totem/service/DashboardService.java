package br.com.totem.service;

import br.com.totem.controller.response.DispositivoDashResponse;
import br.com.totem.mapper.DispositivoMapper;
import br.com.totem.model.*;
import br.com.totem.model.constantes.Comando;
import br.com.totem.model.constantes.Role;
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
    private final AuthService authService;

    public Dashboard buscarDashboard(String token) {
        var userLogado = authService.recuperarUsuarioLogado(token);
        var dash = dashBoardrepository.findById(userLogado.getCliente().getId());
        if (dash.isPresent()) {
            if (dash.get().getUltimaAtualizacao() != null && dash.get().getUltimaAtualizacao().isAfter(LocalDateTime.now()))
                return dash.get();
        }
        return gerarDash(userLogado);
    }


    public Dashboard gerarDash(User user) {
        var clienteId = user.getCliente().getId();
       return gerarDash(clienteId, authService.validaPermissao(user, Role.ROOT));
    }

    public Dashboard gerarDash(UUID clienteId, boolean checarPermissao) {

        Dashboard dashboard = new Dashboard();
        dashboard.setId(clienteId);
        dashboard.setAtualizacao(LocalDateTime.now());
        if (checarPermissao) {
            dashboard.setUsuariosAtivos(userRepository.countByStatus(true));
            dashboard.setUsuariosInativos(userRepository.countByStatus(false));
        } else {
            dashboard.setUsuariosAtivos(userRepository.countByClienteAndStatus(Cliente.builder().id(clienteId).principal(false).build(), true));
            dashboard.setUsuariosInativos(userRepository.countByClienteAndStatus(Cliente.builder().id(clienteId).principal(false).build(), false));
        }

        dashboard.setUltimaAtualizacao(LocalDateTime.now().plusMinutes(10));
        dashboard.setDispositivos(new DispositivoDashResponse());
        List<LogConexao> l = logRepository.findLogsGroupedByCommandAndHour(clienteId);
        dashboard.setLogsConexao(l);
        dashBoardrepository.save(dashboard);
        atualizarConexoes(clienteId, checarPermissao);
        atualizarDashboardAgendas(clienteId, checarPermissao);
        return dashBoardrepository.findById(clienteId).get();
    }


    public void atualizarConexoes(User user) {
        var clienteId = user.getCliente().getId();
        atualizarConexoes(clienteId, authService.validaPermissao(user, Role.ROOT));
    }
    public void atualizarConexoes(UUID clienteId, boolean checarPermissao) {
        List<Dispositivo>  dispositivos = new ArrayList<Dispositivo>();

        Optional<Dashboard> optionalDashboard = dashBoardrepository.findById(clienteId);
        Map<String, DispositivoPorCor> cores = new HashMap<>();

        if (checarPermissao) {
            dispositivos =  dispositivoRepository.findAllByAtivo(true);
        } else {
            dispositivos = dispositivoRepository.findAllByAtivo(clienteId, true);
        }

        if (optionalDashboard.isPresent()) {
            optionalDashboard.get().setDispositivos(new DispositivoDashResponse());
            dispositivos.stream().map(dispositivoMapper::toResume).toList().forEach(device -> {
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
            optionalDashboard.get().getDispositivos().setTotal(optionalDashboard.get().getDispositivos().getOffline() + optionalDashboard.get().getDispositivos().getOnline());

            optionalDashboard.get().setCores(cores.values().stream().toList());
            dashBoardrepository.save(optionalDashboard.get());
        }
    }

    public void atualizarDashboardAgendas(User user) {
        var clienteId = user.getCliente().getId();
        atualizarDashboardAgendas(clienteId, authService.validaPermissao(user, Role.ROOT));
    }
    public void atualizarDashboardAgendas(UUID clienteId, boolean checarPermissao) {

        List<Agenda>  agendasList = new ArrayList<Agenda>();
        List<Agenda>  agendasAtivas = new ArrayList<Agenda>();
        Optional<Dashboard> optionalDashboard = dashBoardrepository.findById(clienteId);
        if (optionalDashboard.isPresent()) {

            if (checarPermissao) {
                agendasList = agendaRepository.findAllAgendasByDataDentroDoIntervalo(LocalDate.now());
                agendasAtivas = agendaRepository.findAllByAtivo(true);
            } else {
                agendasList = agendaRepository.findAllAgendasByDataDentroDoIntervalo(clienteId, LocalDate.now());
                agendasAtivas = agendaRepository.findAllByAtivo(clienteId, true);
            }

            Map<String, DispositivoPorCor> agendas = new HashMap<>();
            if (agendasAtivas.isEmpty()) {
                agendas.put("grey", new DispositivoPorCor("transparent", 1));
            } else {
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
            agendasList.forEach(agenda -> {
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
