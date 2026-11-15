package pe.edu.upeu.service;

import java.util.List;

/*
 * ========================= POO: ABSTRACCIÓN y POLIMORFISMO =========================
 * INTERFAZ (ABSTRACCIÓN): define el "contrato" de operaciones CRUD sin implementar cómo se hacen.
 *        Oculta los detalles de implementación y expone qué puede hacer un servicio genérico.
 * POLIMORFISMO: distintas clases pueden implementar esta interfaz (ProductoServiceImp,
 *        ClienteServiceImp, etc.) y ser usadas a través del mismo tipo ICrudGenericoService/IProductoService.
 *        El controlador puede invocar métodos de la interfaz sin depender de la clase concreta.
 * MÉTODOS: son las acciones/comportamientos que deben ofrecer las implementaciones.
 * CONEXIÓN: Controlador -> (usa interfaz Servicio) -> Implementación Servicio -> Repositorio -> BD.
 */
public interface ICrudGenericoService<T, ID> { // ABSTRACCIÓN: contrato genérico reutilizable
    // MÉTODOS (comportamientos abstractos del servicio)
    List<T> listarTodo();
    T buscarPorId(ID id);
    T guardar(T entidad);
    T actualizar(ID id, T entidad);
    boolean eliminar(ID id);

    T save(T t);

    T update(ID id, T t);

    List<T> findAll();

    T findById(ID id);

    void delete(ID id);
}