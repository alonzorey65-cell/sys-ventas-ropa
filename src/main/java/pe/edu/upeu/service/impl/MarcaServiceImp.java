package pe.edu.upeu.service.impl;

import pe.edu.upeu.model.Marca;
import pe.edu.upeu.repository.MarcaRepository;
import pe.edu.upeu.service.IMarcaService;

import java.util.List;

public class MarcaServiceImp implements IMarcaService {

    private final MarcaRepository marcaRepository;

    public MarcaServiceImp(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    @Override
    public List<Marca> listarTodo() {
        return marcaRepository.listarTodos();
    }

    @Override
    public Marca buscarPorId(Long id) {
        return marcaRepository.buscarPorId(id).orElse(null);
    }

    @Override
    public Marca guardar(Marca entidad) {
        return marcaRepository.guardar(entidad);
    }

    @Override
    public Marca actualizar(Long id, Marca entidad) {
        entidad.setIdMarca(id);
        return marcaRepository.modificar(entidad);
    }

    @Override
    public boolean eliminar(Long id) {
        try {
            marcaRepository.eliminar(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Marca save(Marca marca) {
        return null;
    }

    @Override
    public Marca update(Long aLong, Marca marca) {
        return null;
    }

    @Override
    public List<Marca> findAll() {
        return List.of();
    }

    @Override
    public Marca findById(Long aLong) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}