package pe.edu.upeu.service;

import pe.edu.upeu.model.Categoria;

public interface ICategoriaService extends ICrudGenericoService<Categoria, Long> {
    // Aquí puedes agregar métodos específicos para Categoria si los necesitas en el futuro,
    // por ejemplo: List<Categoria> buscarPorNombre(String filtro);
}