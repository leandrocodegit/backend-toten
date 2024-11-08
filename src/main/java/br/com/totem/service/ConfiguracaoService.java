package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.ConfiguracaoRequest;
import br.com.totem.controller.request.UserCreateRequest;
import br.com.totem.controller.response.ConfiguracaoResponse;
import br.com.totem.controller.response.UserResponse;
import br.com.totem.mapper.ConfiguracaoMapper;
import br.com.totem.mapper.UserMapper;
import br.com.totem.model.Configuracao;
import br.com.totem.model.Dispositivo;
import br.com.totem.model.User;
import br.com.totem.repository.ConfiguracaoRepository;
import br.com.totem.repository.DispositivoRepository;
import br.com.totem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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


    public Page<ConfiguracaoResponse> listaTodasConfiguracoes(Pageable pageable){
        return configuracaoRepository.findAll(pageable).map(configuracaoMapper::toResponse);
    }

    public void removerConfiguracao(UUID id){
        if(configuracaoRepository.existsById(id)){
            configuracaoRepository.deleteById(id);
        }else{
            throw new ExceptionResponse("Configuração não existe mais");
        }
    }

    public void salvarconfiguracao(ConfiguracaoRequest request, boolean principal){
        Configuracao configuracao = configuracaoMapper.toEntity(request);
        if(request.getId() == null){
            configuracao.setId(UUID.randomUUID());
        }
        configuracaoRepository.save(configuracao);
        if(principal){
            salvarConfiguracaoDisposisito(configuracao, request.getMac());
        }
    }

    public void duplicarconfiguracao(ConfiguracaoRequest request){
        Configuracao configuracao = configuracaoMapper.toEntity(request);
        configuracao.setId(UUID.randomUUID());
        configuracaoRepository.save(configuracao);
        Dispositivo dispositivo = salvarConfiguracaoDisposisito(configuracao, request.getMac());
        if(dispositivo != null){
            comandoService.enviardComando(dispositivo);
        }
    }

    public Dispositivo salvarConfiguracaoDisposisito(Configuracao configuracao, String mac){
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(mac);
        if(dispositivoOptional.isPresent()){
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setConfiguracao(configuracao);
            dispositivoRepository.save(dispositivo);
            return dispositivo;
        }
        return null;
    }
    public void criarConfiguracao(Configuracao configuracao){

        if(!configuracaoRepository.findById(configuracao.getId()).isPresent()){
            configuracaoRepository.save(configuracao);
        }else{
            throw new ExceptionResponse("Configuração já existe");
        }
    }
}
