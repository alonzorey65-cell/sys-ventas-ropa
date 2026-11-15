package pe.edu.upeu.service.impl;

import pe.edu.upeu.model.Producto;
import pe.edu.upeu.repository.ProductoRepository;
import pe.edu.upeu.service.IProductoService;

import java.util.List;

/*
 * ========================= POO: ABSTRACCIÓN, POLIMORFISMO y ENCAPSULAMIENTO =========================
 * CLASE de implementación de servicio: contiene la lógica de negocio/orquestación para Producto.
 * ABSTRACCIÓN/POLIMORFISMO: "implements IProductoService" cumple el contrato de la interfaz;
 *        puede usarse como IProductoService (tipo abstracto) aunque el objeto real sea ProductoServiceImp.
 * ENCAPSULAMIENTO: el atributo "productoRepository" es private final y se accede a través de la
 *        clase; los detalles de acceso a datos quedan encapsulados en el repositorio.
 * OBJETOS: recibe y retorna objetos Producto; internamente colabora con un objeto repositorio.
 * MÉTODOS: listarTodo, guardar, actualizar, eliminar... definen comportamientos del servicio.
 * CONEXIÓN: ProductoController (UI) -> IProductoService/ProductoServiceImp -> ProductoRepository -> BD.
 */
public class ProductoServiceImp implements IProductoService { // POLIMORFISMO: implementa interfaz

    private final ProductoRepository productoRepository;

    public ProductoServiceImp(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> listarTodo() {
        return productoRepository.listarTodos();
    }

    @Override
    public Producto buscarPorId(Long id) {
        return productoRepository.buscarPorId(id).orElse(null);
    }

    @Override
    public Producto guardar(Producto entidad) {
        return productoRepository.guardar(entidad);
    }

    @Override
    public Producto actualizar(Long id, Producto entidad) {
        entidad.setIdProducto(id);
        return productoRepository.modificar(entidad);
    }

    @Override
    public boolean eliminar(Long id) {
        try {
            productoRepository.eliminar(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Producto save(Producto producto) { return guardar(producto); }

    @Override
    public Producto update(Long id, Producto producto) { return actualizar(id, producto); }

    @Override
    public List<Producto> findAll() { return listarTodo(); }

    @Override
    public Producto findById(Long id) { return buscarPorId(id); }

    @Override
    public void delete(Long id) { eliminar(id); }
}
