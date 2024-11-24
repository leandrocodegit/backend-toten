package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.CorRequest;
import br.com.totem.controller.response.CorResponse;
import br.com.totem.mapper.CorMapper;
import br.com.totem.model.Cor;
import br.com.totem.model.Dispositivo;
import br.com.totem.repository.CorRepository;
import br.com.totem.repository.DispositivoRepository;
import br.com.totem.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CorService {

    private final CorRepository corRepository;
    private final DispositivoRepository dispositivoRepository;
    private final CorMapper corMapper;
    private final ComandoService comandoService;
    private final LogRepository logRepository;


    public Page<CorResponse> listaTodasCores(Pageable pageable) {
        return corRepository.findAll(pageable).map(corMapper::toResponse);
    }

    public List<CorResponse> listaTodasCoresRapidas() {
        return corRepository.findByRapida(true).stream().map(corMapper::toResponse).toList();
    }
    public void removerConfiguracao(UUID id) {
        if (corRepository.existsById(id)) {
            corRepository.deleteById(id);
        } else {
            throw new ExceptionResponse("Configuração não existe mais");
        }
    }

    public void salvarCor(CorRequest request, boolean principal) {
        Cor cor = corMapper.toEntity(request);
        if (request.getId() == null) {
            cor.setId(UUID.randomUUID());
        }
        corRepository.save(cor);
        if (principal) {
            salvarCorDisposisito(cor, request.getMac());
        }
    }

    public Optional<Cor> buscaCor(UUID id){
        return corRepository.findById(id);
    }
    public void duplicarCor(CorRequest request) {
        Cor cor = corMapper.toEntity(request);
        cor.setId(UUID.randomUUID());
        corRepository.save(cor);
        Dispositivo dispositivo = salvarCorDisposisito(cor, request.getMac());
        if (dispositivo != null) {
            comandoService.sincronizar(dispositivo.getMac());
        }
    }

    public Dispositivo salvarCorDisposisito(Cor cor, String mac) {
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(mac);
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setCor(cor);
            dispositivoRepository.save(dispositivo);
            comandoService.sincronizar(dispositivo.getMac());
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
