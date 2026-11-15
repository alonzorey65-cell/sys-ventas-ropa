package pe.edu.upeu.service.impl;

import pe.edu.upeu.model.Cliente;
import pe.edu.upeu.repository.ClienteRepository;
import pe.edu.upeu.service.IClienteService;

import java.util.List;
import java.util.Optional;

public class ClienteServiceImp implements IClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImp(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<Cliente> listarTodo() {
        return clienteRepository.listarTodos();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepository.buscarPorId(id).orElse(null);
    }

    @Override
    public Cliente guardar(Cliente entidad) {
        return clienteRepository.guardar(entidad);
    }

    @Override
    public Cliente actualizar(Long id, Cliente entidad) {
        entidad.setIdCliente(id);
        return clienteRepository.modificar(entidad);
    }

    @Override
    public boolean eliminar(Long id) {
        try {
            clienteRepository.eliminar(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Cliente save(Cliente cliente) { return guardar(cliente); }

    @Override
    public Cliente update(Long id, Cliente cliente) { return actualizar(id, cliente); }

    @Override
    public List<Cliente> findAll() { return listarTodo(); }

    @Override
    public Cliente findById(Long id) { return buscarPorId(id); }

    @Override
    public void delete(Long id) { eliminar(id); }

    @Override
    public Cliente buscarPorDniRuc(String dniRuc) {
        if (dniRuc == null || dniRuc.isBlank()) return null;
        return clienteRepository.buscarPorDniRuc(dniRuc.trim()).orElse(null);
    }
}
