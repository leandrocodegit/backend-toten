package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.ConfiguracaoRequest;
import br.com.totem.controller.request.TemporizadorRequest;
import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.response.ConfiguracaoResponse;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.mapper.ConfiguracaoMapper;
import br.com.totem.mapper.UserMapper;
import br.com.totem.model.*;
import br.com.totem.model.constantes.Comando;
import br.com.totem.repository.ConfiguracaoRepository;
import br.com.totem.repository.DispositivoRepository;
import br.com.totem.repository.LogRepository;
import br.com.totem.repository.UserRepository;
import br.com.totem.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConfiguracaoService {

    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    @Autowired
    private DispositivoRepository dispositivoRepository;
    @Autowired
    private ConfiguracaoMapper configuracaoMapper;
    @Autowired
    private ComandoService comandoService;
    @Autowired
    private LogRepository logRepository;


    public Page<ConfiguracaoResponse> listaTodasConfiguracoes(Pageable pageable) {
        return configuracaoRepository.findAll(pageable).map(configuracaoMapper::toResponse);
    }

    public List<ConfiguracaoResponse> listaTodasConfiguracoesRapidas() {
        return configuracaoRepository.findByRapida(true).stream().map(configuracaoMapper::toResponse).toList();
    }
    public void removerConfiguracao(UUID id) {
        if (configuracaoRepository.existsById(id)) {
            configuracaoRepository.deleteById(id);
        } else {
            throw new ExceptionResponse("Configuração não existe mais");
        }
    }

    public void salvarconfiguracao(ConfiguracaoRequest request, boolean principal) {
        Configuracao configuracao = configuracaoMapper.toEntity(request);
        if (request.getId() == null) {
            configuracao.setId(UUID.randomUUID());
        }
        configuracaoRepository.save(configuracao);
        if (principal) {
            salvarConfiguracaoDisposisito(configuracao, request.getMac());
        }
    }

    public void salvarconfiguracaoTemporizada(TemporizadorRequest request) {

        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(request.getMac());

        if(request.isCancelar() && dispositivoOptional.isPresent()){
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setTemporizador(Temporizador.builder()
                    .idConfiguracao(request.getIdConfiguracao())
                    .time(LocalDateTime.now().plusMinutes(-1))
                    .build());

            dispositivoRepository.save(dispositivo);
            comandoService.enviardComando(dispositivo, false);
        }
        else{
        Optional<Configuracao> configuracaoOptional = configuracaoRepository.findById(request.getIdConfiguracao());
        if (dispositivoOptional.isPresent() && configuracaoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();

            dispositivo.setTemporizador(Temporizador.builder()
                    .idConfiguracao(request.getIdConfiguracao())
                    .time(LocalDateTime.now().plusMinutes(configuracaoOptional.get().getTime()))
                    .build());

            dispositivoRepository.save(dispositivo);
            dispositivo.setConfiguracao(configuracaoOptional.get());
            comandoService.enviardComando(dispositivo, false);
            TimeUtil.timers.put(dispositivo.getMac(), dispositivo);

            logRepository.save(Log.builder()
                    .data(LocalDateTime.now())
                    .usuario("Leandro")
                    .mensagem(String.format(Comando.TIMER_CRIADO.value(), dispositivo.getMac()))
                    .configuracao(null)
                    .comando(Comando.TIMER_CRIADO)
                    .descricao(String.format(Comando.TIMER_CRIADO.value(), dispositivo.getMac()))
                    .mac(dispositivo.getMac())
                    .build());
        }
        }
    }

    public Optional<Configuracao> buscaConfiguracao(UUID id){
        return configuracaoRepository.findById(id);
    }
    public void duplicarconfiguracao(ConfiguracaoRequest request) {
        Configuracao configuracao = configuracaoMapper.toEntity(request);
        configuracao.setId(UUID.randomUUID());
        configuracaoRepository.save(configuracao);
        Dispositivo dispositivo = salvarConfiguracaoDisposisito(configuracao, request.getMac());
        if (dispositivo != null) {
            comandoService.enviardComando(dispositivo, false);
        }
    }

    public Dispositivo salvarConfiguracaoDisposisito(Configuracao configuracao, String mac) {
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(mac);
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setConfiguracao(configuracao);
            dispositivoRepository.save(dispositivo);
            return dispositivo;
        }
        return null;
    }

    public void criarConfiguracao(Configuracao configuracao) {

        if (!configuracaoRepository.findById(configuracao.getId()).isPresent()) {
            configuracaoRepository.save(configuracao);
        } else {
            throw new ExceptionResponse("Configuração já existe");
        }
    }
}
