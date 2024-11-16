package br.com.totem.service;

import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.CorRequest;
import br.com.totem.controller.request.TemporizadorRequest;
import br.com.totem.controller.response.CorResponse;
import br.com.totem.mapper.CorMapper;
import br.com.totem.model.*;
import br.com.totem.model.constantes.Comando;
import br.com.totem.repository.CorRepository;
import br.com.totem.repository.DispositivoRepository;
import br.com.totem.repository.LogRepository;
import br.com.totem.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CorService {

    @Autowired
    private CorRepository corRepository;
    @Autowired
    private DispositivoRepository dispositivoRepository;
    @Autowired
    private CorMapper corMapper;
    @Autowired
    private ComandoService comandoService;
    @Autowired
    private LogRepository logRepository;


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

    public void salvarCorTemporizada(TemporizadorRequest request) {

        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(request.getMac());

        if(request.isCancelar() && dispositivoOptional.isPresent()){
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setTemporizador(Temporizador.builder()
                    .idCor(request.getIdCor())
                    .time(LocalDateTime.now().plusMinutes(-1))
                    .build());

            dispositivoRepository.save(dispositivo);
            comandoService.enviardComando(dispositivo, true);
        }
        else{
        Optional<Cor> corOptional = corRepository.findById(request.getIdCor());
        if (dispositivoOptional.isPresent() && corOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();

            dispositivo.setTemporizador(Temporizador.builder()
                    .idCor(request.getIdCor())
                    .time(LocalDateTime.now().plusMinutes(corOptional.get().getTime()))
                    .build());

            dispositivoRepository.save(dispositivo);
            dispositivo.setCor(corOptional.get());
            comandoService.enviardComando(dispositivo, false);
            TimeUtil.timers.put(dispositivo.getMac(), dispositivo);

            logRepository.save(Log.builder()
                    .data(LocalDateTime.now())
                    .usuario("Leandro")
                    .mensagem(String.format(Comando.TIMER_CRIADO.value(), dispositivo.getMac()))
                    .cor(null)
                    .comando(Comando.TIMER_CRIADO)
                    .descricao(String.format(Comando.TIMER_CRIADO.value(), dispositivo.getMac()))
                    .mac(dispositivo.getMac())
                    .build());
        }
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
            comandoService.enviardComando(dispositivo, false);
        }
    }

    public Dispositivo salvarCorDisposisito(Cor cor, String mac) {
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(mac);
        if (dispositivoOptional.isPresent()) {
            Dispositivo dispositivo = dispositivoOptional.get();
            dispositivo.setCor(cor);
            dispositivoRepository.save(dispositivo);
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
