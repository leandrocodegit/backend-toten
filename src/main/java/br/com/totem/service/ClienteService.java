package br.com.totem.service;


import br.com.totem.Exception.ExceptionResponse;
import br.com.totem.controller.request.ClienteRequest;
import br.com.totem.controller.response.ClienteResponse;
import br.com.totem.controller.response.ClienteResume;
import br.com.totem.controller.response.DispositivoResponse;
import br.com.totem.mapper.ClienteMapper;
import br.com.totem.model.Cliente;
import br.com.totem.model.constantes.Role;
import br.com.totem.repository.ClienteRepository;
import br.com.totem.repository.LogRepository;
import br.com.totem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final ClienteMapper clienteMapper;
    private final ClienteRepository clienteRepository;
    private final AuthService authService;

    public Page<ClienteResponse> pesquisarDispositivos(String token, UUID clienteId, String pesquisa, boolean ativo, Pageable pageable) {
        if (authService.validaPermissao(token, Role.ROOT))
            return clienteRepository.findByIdAndNomeContaining(pesquisa, ativo, pageable).map(clienteMapper::toResponse);
        return clienteRepository.findByIdAndNomeContaining(clienteId, pesquisa, ativo, pageable).map(clienteMapper::toResponse);
    }

    public Page<ClienteResponse> listaClientes(String token, UUID clienteId, boolean ativo, Pageable pageable) {

        if (authService.validaPermissao(token, Role.ROOT))
            return clienteRepository.findAllByAtivo(ativo, pageable).map(clienteMapper::toResponse);

        if (clienteId == null)
            return Page.empty();
        var clientePai = buscarClinte(clienteId);
        if (clientePai.getClientes() == null)
            clientePai.setClientes(new ArrayList<>());
        List<ClienteResponse> clientes = clientePai.getClientes();
        clientePai.setClientes(null);
        clientes.add(clientePai);
        clientes = clientes.stream().filter(cliente -> cliente.getAtivo().booleanValue() == ativo).toList();
        if (clientes.isEmpty())
            return Page.empty();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), clientes.size());
        return new PageImpl<>(clientes.subList(start, end), pageable, clientes.size());
    }

    public ClienteResponse salvarCliente(String token, UUID clienteId, ClienteRequest request) {
        var cliente = clienteMapper.toEntity(request);

        if (request.getId() == null) {
            if (authService.validaPermissao(token, Role.ROOT)) {
                cliente.setId(UUID.randomUUID());
                clienteRepository.save(cliente);
            } else {
                if (!authService.validaPermissaoCriarCliente(token, Role.ROOT, Role.ADMIN))
                    throw new ExceptionResponse("Usuário sem premissão para criar clientes");

                var clientePai = clienteRepository.findById(clienteId).orElseThrow(() -> {
                    throw new ExceptionResponse("Cliente não encontrado");
                });
                cliente.setId(UUID.randomUUID());
                if (clientePai.getClientes() == null)
                    clientePai.setClientes(new ArrayList<>());
                clientePai.getClientes().add(cliente);
                if (cliente.getEndereco() != null)
                    cliente.setEnderecoCompleto(cliente.getEndereco().toString());
                clienteRepository.save(cliente);
                clienteRepository.save(clientePai);
            }
        } else {
            if (authService.validaPermissao(token, Role.ROOT)) {
                cliente = clienteRepository.findById(request.getId()).orElseThrow(() -> {
                    throw new ExceptionResponse("Cliente não relacionado");
                });
            } else {
                cliente = buscarClinterelacionado(clienteId, request.getId()).orElseThrow(() -> {
                    throw new ExceptionResponse("Cliente não relacionado");
                });
            }
            cliente.setAtivo(request.getAtivo());
            cliente.setNome(request.getNome());
            cliente.setEndereco(request.getEndereco());
            clienteRepository.save(cliente);
        }
        return clienteMapper.toResponse(cliente);
    }

    public Optional<Cliente> buscarClinterelacionado(UUID clienteId, UUID id) {
        var cliente = clienteRepository.findById(clienteId).orElseThrow(() -> {
            throw new ExceptionResponse("Cliente não encontrado");
        });
        if (cliente.getClientes() == null || cliente.getClientes().isEmpty())
            return Optional.empty();
        return cliente.getClientes().stream().filter(it -> it.getId().toString().equals(id.toString())).findFirst();
    }


    public ClienteResume buscarDetalhesClinte(UUID id) {
        return clienteMapper.toResume(clienteRepository.findById(id).orElseThrow(() -> {
            throw new ExceptionResponse("Cliente não encontrado");
        }));
    }

    private ClienteResponse buscarClinte(UUID id) {
        return clienteMapper.toResponse(clienteRepository.findById(id).orElseThrow(() -> {
            throw new ExceptionResponse("Cliente não encontrado");
        }));
    }

    public ClienteResponse buscarClinte(String token, UUID clienteId, UUID id) {
        if (authService.validaPermissao(token, Role.ROOT))
            return clienteMapper.toResponse(clienteRepository.findById(id).orElseThrow(() -> {
                throw new ExceptionResponse("Cliente não encontrado");
            }));

        return clienteMapper.toResponse(clienteRepository.findByClienteAndId(clienteId, id).orElseThrow(() -> {
            throw new ExceptionResponse("Cliente não encontrado");
        }));
    }

    public void removerClinte(UUID id) {
        buscarClinte(id);
        clienteRepository.deleteById(id);
    }

}
