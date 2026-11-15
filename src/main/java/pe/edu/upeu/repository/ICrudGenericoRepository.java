package pe.edu.upeu.repository;

import java.util.List;
import java.util.Optional;

/*
 * ========================= POO: ABSTRACCIÓN =========================
 * INTERFAZ (ABSTRACCIÓN): contrato para acceso a datos (repositorio) desacoplado de la BD concreta.
 * Permite cambiar/ajustar la implementación sin cambiar quién la usa (servicios).
 * POLIMORFISMO: múltiples repositorios concretos implementan esta interfaz.
 * CONEXIÓN: Servicio -> ICrudGenericoRepository (abstracción) -> AbstractJpaRepository/Repo concreto -> Conexion/DB.
 */
public interface ICrudGenericoRepository<T,ID> { // ABSTRACCIÓN del acceso a datos
    // MÉTODOS abstractos (qué operaciones de persistencia existen)
    T save(T entity);
    T update(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);

    T guardar(T entidad);

    T modificar(T entidad);

    List<T> listarTodos();

    Optional<T> buscarPorId(ID id);

    void eliminar(ID id);
}
