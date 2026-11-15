package pe.edu.upeu.service.impl;

import pe.edu.upeu.model.UnidadMedida;
import pe.edu.upeu.repository.UnidadMedidaRepository;
import pe.edu.upeu.service.IUnidadMedidaService;

import java.util.List;
import java.util.Optional;

public class UnidadMedidaServiceImp implements IUnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;

    public UnidadMedidaServiceImp(UnidadMedidaRepository unidadMedidaRepository) {
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    @Override
    public List<UnidadMedida> listarTodo() {
        return unidadMedidaRepository.listarTodos();
    }

    @Override
    public UnidadMedida buscarPorId(Long id) {
        return unidadMedidaRepository.buscarPorId(id).orElse(null);
    }

    @Override
    public UnidadMedida guardar(UnidadMedida entidad) {
        return unidadMedidaRepository.guardar(entidad);
    }

    @Override
    public UnidadMedida actualizar(Long id, UnidadMedida entidad) {
        entidad.setIdUnidMedida(id); // Asegúrate de que el setter del ID coincida con tu modelo
        return unidadMedidaRepository.modificar(entidad);
    }

    @Override
    public boolean eliminar(Long id) {
        try {
            unidadMedidaRepository.eliminar(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public UnidadMedida save(UnidadMedida unidadMedida) {
        return null;
    }

    @Override
    public UnidadMedida update(Long aLong, UnidadMedida unidadMedida) {
        return null;
    }

    @Override
    public List<UnidadMedida> findAll() {
        return List.of();
    }

    @Override
    public UnidadMedida findById(Long aLong) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}