package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.IntegracaoRequest;
import br.com.totem.controller.response.IntegracaoResponse;
import br.com.totem.mapper.IntegracaoMapper;
import br.com.totem.model.Integracao;
import br.com.totem.repository.IntegracaoRepository;
import br.com.totem.security.SecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegracaoService {

    private final IntegracaoRepository integracaoRepository;
    private final IntegracaoMapper integracaoMapper;
    private final SecurityConfig securityConfiguration;

    public void criarIntegracao(IntegracaoRequest request){

        if(integracaoRepository.existsByNome(request.getNome())){
            throw new ExceptionResponse("Esse nome já existe");
        }else{
            integracaoRepository.save(Integracao.builder()
                            .nome(request.getNome())
                            .clientId(UUID.randomUUID().toString())
                            .secret(securityConfiguration.passwordEncoder().encode(UUID.randomUUID().toString()))
                            .status(true)
                    .build());
        }
    }

    public Page<IntegracaoResponse> listaIntegracoes(Pageable pageable){
        return integracaoRepository.findAll(pageable).map(integracaoMapper::toResponse);
    }

    public void remover(String nome){
        integracaoRepository.deleteById(nome);
    }

}
