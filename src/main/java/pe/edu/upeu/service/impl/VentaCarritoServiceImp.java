package pe.edu.upeu.service.impl;

import pe.edu.upeu.model.Producto;
import pe.edu.upeu.model.VentCarrito;
import pe.edu.upeu.repository.VentCarritoRepository;
import pe.edu.upeu.service.IVentaCarritoService;

import java.util.List;
import java.util.Optional;

public class VentaCarritoServiceImp implements IVentaCarritoService {

    private final VentCarritoRepository ventCarritoRepository;

    public VentaCarritoServiceImp(VentCarritoRepository ventCarritoRepository) {
        this.ventCarritoRepository = ventCarritoRepository;
    }

    @Override
    public List<VentCarrito> listarTodo() {
        return ventCarritoRepository.listarTodos();
    }

    @Override
    public VentCarrito buscarPorId(Long id) {
        return ventCarritoRepository.buscarPorId(id).orElse(null);
    }

    @Override
    public VentCarrito guardar(VentCarrito entidad) {
        return ventCarritoRepository.guardar(entidad);
    }

    @Override
    public VentCarrito actualizar(Long id, VentCarrito entidad) {
        entidad.setIdVentCarrito(id); // Asegúrate de que el setter del ID coincida con tu modelo
        return ventCarritoRepository.modificar(entidad);
    }

    @Override
    public boolean eliminar(Long id) {
        try {
            ventCarritoRepository.eliminar(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void agregarItem(Producto producto, int cantidad) {

    }

    @Override
    public void removerItem(Long productoId) {

    }

    @Override
    public void limpiarCarrito() {

    }

    @Override
    public double calcularTotalGeneral() {
        return 0;
    }

    @Override
    public List<Object> obtenerItemsCarrito() {
        return List.of();
    }
}