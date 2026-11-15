package pe.edu.upeu.service;

import pe.edu.upeu.model.Producto;
import java.util.List;

public interface ProductoService {
    List<Producto> listarProductos();
    boolean registrarProducto(Producto producto);
}