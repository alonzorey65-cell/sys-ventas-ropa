package pe.edu.upeu.service;

import pe.edu.upeu.model.Producto;

/*
 * POO - ABSTRACCIÓN, HERENCIA DE INTERFACES y POLIMORFISMO:
 * - ABSTRACCIÓN: define el contrato específico de servicio para Producto.
 * - HERENCIA (de interfaces): extiende ICrudGenericoService reutilizando operaciones CRUD comunes.
 * - POLIMORFISMO: puede ser implementada por diferentes clases; el controlador depende de esta interfaz.
 * CONEXIÓN: ProductoController -> IProductoService -> ProductoServiceImp -> ProductoRepository.
 */
public interface IProductoService extends ICrudGenericoService<Producto, Long> { // ABSTRACCIÓN específica
    // Aquí puedes agregar métodos personalizados para productos en el futuro si los necesitas
}