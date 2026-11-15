package pe.edu.upeu.service.impl;

import pe.edu.upeu.model.Categoria; // (O la entidad que corresponda)
import pe.edu.upeu.repository.CategoriaRepository;
import pe.edu.upeu.service.ICategoriaService;

import java.util.List;

public class CategoriaServiceImp implements ICategoriaService {

    private final CategoriaRepository repository;

    public CategoriaServiceImp(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Categoria> listarTodo() {
        return repository.listarTodos();
    }

    @Override
    public Categoria buscarPorId(Long id) {
        return repository.buscarPorId(id).orElse(null);
    }

    @Override
    public Categoria guardar(Categoria entidad) {
        return repository.guardar(entidad);
    }

    @Override
    public Categoria actualizar(Long id, Categoria entidad) {
        // Asegúrate de setear el ID antes de modificar en la BD
        return repository.modificar(entidad);
    }

    @Override
    public boolean eliminar(Long id) {
        try {
            repository.eliminar(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Categoria save(Categoria categoria) {
        return null;
    }

    @Override
    public Categoria update(Long aLong, Categoria categoria) {
        return null;
    }

    @Override
    public List<Categoria> findAll() {
        return List.of();
    }

    @Override
    public Categoria findById(Long aLong) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}