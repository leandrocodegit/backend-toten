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

    public Page<ClienteResponse> pesquisarDispositivos(String token, String pesquisa, boolean ativo, Pageable pageable) {
        var user = authService.recuperarUsuarioLogado(token);
        if (authService.validaPermissao(token, Role.ROOT))
            return clienteRepository.findByIdAndNomeContaining(pesquisa, ativo, pageable).map(clienteMapper::toResponse);
        return clienteRepository.findByIdAndNomeContaining(user.getCliente().getId(), pesquisa, ativo, pageable).map(clienteMapper::toResponse);
    }

    public Page<ClienteResponse> listaClientes(String token, boolean ativo, Pageable pageable) {
        var user = authService.recuperarUsuarioLogado(token);
        if (authService.validaPermissao(token, Role.ROOT))
            return clienteRepository.findAllByAtivo(ativo, pageable).map(clienteMapper::toResponse);

        if (user.getCliente() == null)
            return Page.empty();
        var clientePai = clienteMapper.toResponse(user.getCliente());
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

    public ClienteResponse salvarCliente(String token, ClienteRequest request) {
        var cliente = clienteMapper.toEntity(request);
        var user = authService.recuperarUsuarioLogado(token);

        if (request.getId() == null) {
            if (authService.validaPermissao(token, Role.ROOT)) {
                cliente.setId(UUID.randomUUID());
                clienteRepository.save(cliente);
            } else {
                if (!authService.validaPermissaoCriarCliente(token, Role.ROOT, Role.ADMIN))
                    throw new ExceptionResponse("Usuário sem premissão para criar clientes");

                if (authService.validaPermissao(token, Role.ROOT)) {
                    cliente.setPrincipal(request.getPrincipal());
                }

                var clientePai = user.getCliente();
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
            if (authService.validaPermissao(user, Role.ROOT)) {
                cliente = clienteRepository.findById(request.getId()).orElseThrow(() -> {
                    throw new ExceptionResponse("Cliente não relacionado");
                });
            } else if(authService.validaPermissao(user, Role.ADMIN)) {
                var clienteOptional = buscarClinterelacionado(user.getCliente().getId(), request.getId());
                if(clienteOptional.isEmpty()){
                  cliente = clienteRepository.findByClientePrincipal(request.getId()).orElseThrow(() -> {
                        throw new ExceptionResponse("Cliente não relacionado");
                    });
                }else{
                    cliente = clienteOptional.get();
                }
            }else{
                throw new ExceptionResponse("Usuário sem pemissão");
            }
            if (authService.validaPermissao(user, Role.ROOT)) {
                cliente.setPrincipal(request.getPrincipal());
            }else {
                if (!cliente.isPrincipal()) {
                    cliente.setNome(request.getNome());
                    cliente.setAtivo(request.getAtivo());
                }
            }
            cliente.setEndereco(request.getEndereco());
            clienteRepository.save(cliente);
        }
        return clienteMapper.toResponse(cliente);
    }

    private Optional<Cliente> buscarClinterelacionado(UUID clienteId, UUID id) {
        var cliente = clienteRepository.findById(clienteId).orElse(null);
        if (cliente == null || cliente.getClientes() == null || cliente.getClientes().isEmpty())
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

    public ClienteResponse buscarClinte(String token, UUID id) {
        var user = authService.recuperarUsuarioLogado(token);
        if (authService.validaPermissao(user, Role.ROOT))
            return clienteMapper.toResponse(clienteRepository.findById(id).orElseThrow(() -> {
                throw new ExceptionResponse("Cliente não encontrado");
            }));

        if (user.getCliente().getId().toString().equals(id.toString()))
            return clienteMapper.toResponse(clienteRepository.findById(user.getCliente().getId()).orElseThrow(() -> {
                throw new ExceptionResponse("Cliente não encontrado");
            }));
        return clienteMapper.toResponse(clienteRepository.findByClienteAndId(user.getCliente().getId(), id).orElseThrow(() -> {
            throw new ExceptionResponse("Cliente não encontrado");
        }));
    }

    public void removerClinte(String token, UUID id) {
        buscarClinte(token, id);
        clienteRepository.deleteById(id);
    }

}
