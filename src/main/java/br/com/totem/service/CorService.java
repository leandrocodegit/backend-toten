package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.CorRequest;
import br.com.totem.controller.response.CorResponse;
import br.com.totem.mapper.CorMapper;
import br.com.totem.model.Cliente;
import br.com.totem.model.Cor;
import br.com.totem.model.Dispositivo;
import br.com.totem.model.Parametro;
import br.com.totem.model.constantes.Efeito;
import br.com.totem.model.constantes.Role;
import br.com.totem.model.constantes.TipoConfiguracao;
import br.com.totem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CorService {

    private final CorRepository corRepository;
    private final DispositivoRepository dispositivoRepository;
    private final AgendaRepository agendaRepository;
    private final CorMapper corMapper;
    private final ComandoService comandoService;
    private final ParametroRepository parametroRepository;
    private final ConexaoService conexaoService;
    private final OperacaoRepository operacaoRepository;
    private final AuthService authService;


    public Page<CorResponse> listaTodasCores(String token, UUID clienteId, boolean rapida, boolean vibracao, boolean exclusiva, Pageable pageable) {
        var isRoot = authService.validaPermissao(token, Role.ROOT);
        if (!rapida && !vibracao && !exclusiva) {
            if (isRoot)
                return corRepository.findAllByVibracaoAndExclusiva(vibracao, exclusiva, pageable).map(corMapper::toResponse);
            else
                return corRepository.findAllByVibracaoAndExclusiva(clienteId, vibracao, exclusiva, pageable).map(corMapper::toResponse);
        } else if (isRoot)
            return corRepository.findAllByRapidaAndVibracaoAndExclusiva(rapida, vibracao, exclusiva, pageable).map(corMapper::toResponse);
        else
            return corRepository.findAllByRapidaAndVibracaoAndExclusiva(clienteId, rapida, vibracao, exclusiva, pageable).map(corMapper::toResponse);
    }

    public void removerConfiguracao(String token, UUID clienteId, UUID id) {
        Optional<Cor> cor = Optional.empty();
        if (authService.validaPermissao(token, Role.ROOT))
            cor = corRepository.findById(id);
        else cor = corRepository.findByClienteAndId(clienteId, id);

        if (cor.isPresent()) {

            var devices = dispositivoRepository.findDispositivosPorCor(id);
            if (!dispositivoRepository.findDispositivosPorCor(id).isEmpty())
                throw new ExceptionResponse("Essa cor está sendo usada por " + devices.size() + " dispositivos");
            var operacoes = operacaoRepository.findDispositivosPorCorTemporizada(id);
            if (!operacoes.isEmpty())
                throw new ExceptionResponse("Essa cor está sendo usada por " + operacoes.size() + " seleção rápida");
            operacoes = operacaoRepository.findDispositivosPorCorVibracao(id);
            if (!operacoes.isEmpty())
                throw new ExceptionResponse("Essa cor está sendo usada por " + operacoes.size() + " como cor de vibração");
            var agendas = agendaRepository.findAgendasPorCorVibracao(id);
            if (!agendas.isEmpty())
                throw new ExceptionResponse("Essa cor está sendo usada por " + agendas.size() + " agendas");
            corRepository.deleteById(id);
        } else {
            throw new ExceptionResponse("Cor não existe mais");
        }
    }

    public CorResponse salvarCor(String token, UUID clienteId, CorRequest request, boolean principal) {
        Cor cor = corMapper.toEntity(request);

        if (clienteId == null)
            throw new ExceptionResponse("É necessário um cliente válido");
        var isRoot = authService.validaPermissao(token, Role.ROOT);

        if (request.getId() == null) {
            cor.setId(UUID.randomUUID());
            if (cor.getParametros() == null || cor.getParametros().isEmpty()) {
                cor.setParametros(List.of(Parametro.builder()
                        .cor(new int[]{255, 0, 0, 0, 255, 0, 0, 255})
                        .correcao(new int[]{255, 255, 255})
                        .efeito(Efeito.COLORIDO)
                        .corHexa(List.of("red", "green", "blue"))
                        .pino(1)
                        .build()));
            }

        } else {
            Optional<Cor> corDB = Optional.empty();
            if (isRoot)
                corDB = corRepository.findByIdAndExclusiva(request.getId(), true);
            else corDB = corRepository.findByIdAndExclusiva(clienteId, request.getId(), true);
            if (corDB.isPresent()) {
                cor.setExclusiva(true);
                cor.setRapida(false);
                cor.setVibracao(false);
            }
            cor.setParametros(cor.getParametros().stream().limit(4).sorted(Comparator.comparing(Parametro::getPino)).toList());
        }

        cor.setCliente(Cliente.builder().id(clienteId).principal(false).build());
        var corSalve = corRepository.save(cor);
        if (principal) {
            salvarCorDisposisito(token, clienteId, cor, request.getDeviceId(), principal);
            System.out.println("Chamando atualizacao dashboard");
            //  conexaoService.atualizarDashboar();
        }

        if (cor.getVibracao())
            comandoService.sincronizarVibracao(cor.getId());
        return corMapper.toResponse(corSalve);
    }

    public void salvarCorVibracao(String token, UUID clienteId, CorRequest request, boolean principal) {
        Cor cor = null;
        Optional<Dispositivo> dispositivoOptional = Optional.empty();
        if (authService.validaPermissao(token, Role.ROOT)) {
            cor = corRepository.findById(request.getId()).orElseThrow(() -> new ExceptionResponse("Cor não encontrada"));
            dispositivoOptional = dispositivoRepository.findById(request.getDeviceId());
        } else {
            cor = corRepository.findByClienteAndId(clienteId, request.getId()).orElseThrow(() -> new ExceptionResponse("Cor não encontrada"));
            dispositivoOptional = dispositivoRepository.findByClienteAndId(clienteId, request.getDeviceId());
        }

        if (dispositivoOptional.isPresent()) {
            dispositivoOptional.get().getOperacao().setCorVibracao(cor);
            operacaoRepository.save(dispositivoOptional.get().getOperacao());
        } else {
            throw new ExceptionResponse("Falha ao definir a cor de evento");
        }
    }

    public CorResponse buscaCor(String token, UUID clienteId, UUID id) {
        Cor cor = null;
        if(authService.validaPermissao(token, Role.ROOT))
         cor = corRepository.findById(id).orElseThrow(() -> new ExceptionResponse("Cor não encontrada"));
        else cor = corRepository.findByClienteAndId(clienteId, id).orElseThrow(() -> new ExceptionResponse("Cor não encontrada"));
        return corMapper.toResponse(cor);
    }

    public void duplicarCor(String token, UUID clienteId, CorRequest request) {

        if(clienteId == null)
            throw new ExceptionResponse("Essa ação requer um cliente logado");
        Cor cor = corMapper.toEntity(request);
        cor.setId(UUID.randomUUID());
        corRepository.save(cor);
        Dispositivo dispositivo = salvarCorDisposisito(token, clienteId, cor, request.getDeviceId(), true);
        if (dispositivo != null) {
            comandoService.sincronizar(dispositivo.getId());
        }
    }

    public Cor parametricarCorDispositivo(Cor cor, Dispositivo dispositivo) {
        var corDispositivo = dispositivo.getCor();
        corDispositivo.setNome(cor.getNome());
        for (int i = 0; i < corDispositivo.getParametros().size(); i++) {
            var parametroCor = cor.getParametros().get(i);
            var parametroDispositivo = corDispositivo.getParametros().get(i);

            parametroDispositivo.setCor(parametroCor.getCor());
            parametroDispositivo.setCorrecao(parametroCor.getCorrecao());
            parametroDispositivo.setEfeito(parametroCor.getEfeito());

            if (cor.getParametros().size() <= i)
                break;
        }
        return corDispositivo;
    }

    public Dispositivo salvarCorDisposisito(String token, UUID clienteId, Cor cor, long id, boolean principal) {
        Optional<Dispositivo> dispositivoOptional = Optional.empty();
        if(authService.validaPermissao(token, Role.ROOT))
          dispositivoOptional = dispositivoRepository.findById(id);
        else dispositivoOptional = dispositivoRepository.findByClienteAndId(clienteId, id);
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            if (principal) {
                parametricarCorDispositivo(cor, dispositivo);
                corRepository.save(dispositivo.getCor());
                comandoService.sincronizar(dispositivo.getId());
            }
            return dispositivo;
        }
        return null;
    }

    public void criarCor(Cor cor) {

        if (!corRepository.findById(cor.getId()).isPresent()) {
            corRepository.save(cor);
        } else {
            throw new ExceptionResponse("Configuração já existe");
        }
    }
}
