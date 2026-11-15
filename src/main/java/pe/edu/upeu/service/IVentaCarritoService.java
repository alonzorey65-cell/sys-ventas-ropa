package pe.edu.upeu.service;

import pe.edu.upeu.model.Producto;
import pe.edu.upeu.model.VentCarrito;

import java.util.List;

public interface IVentaCarritoService {
    List<VentCarrito> listarTodo();

    VentCarrito buscarPorId(Long id);

    VentCarrito guardar(VentCarrito entidad);

    VentCarrito actualizar(Long id, VentCarrito entidad);

    boolean eliminar(Long id);

    void agregarItem(Producto producto, int cantidad);
    void removerItem(Long productoId);
    void limpiarCarrito();
    double calcularTotalGeneral();
    List<Object> obtenerItemsCarrito(); // O una clase DetalleCarrito
}