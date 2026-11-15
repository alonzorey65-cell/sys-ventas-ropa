package pe.edu.upeu.service.impl;

import pe.edu.upeu.dto.comprobante.Comprobante;
import pe.edu.upeu.model.Venta;
import pe.edu.upeu.repository.VentaRepository;
import pe.edu.upeu.service.IVentaService;

import java.util.List;
import java.util.Optional;

public class VentaServiceImp implements IVentaService {

    private final VentaRepository ventaRepository;

    public VentaServiceImp(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Override
    public boolean registrarVentaCompleta(Comprobante comprobante) {
        return false;
    }

    @Override
    public List<Venta> listarTodo() {
        return ventaRepository.listarTodos();
    }

    @Override
    public Venta buscarPorId(Long id) {
        return ventaRepository.buscarPorId(id).orElse(null);
    }

    @Override
    public Venta guardar(Venta entidad) {
        return ventaRepository.guardar(entidad);
    }

    @Override
    public Venta actualizar(Long id, Venta entidad) {
        entidad.setIdVenta(id);
        return ventaRepository.modificar(entidad);
    }

    @Override
    public boolean eliminar(Long id) {
        try {
            ventaRepository.eliminar(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // =========================================================================
    // Métodos obligatorios de la interfaz genérica (en inglés)
    // =========================================================================

    @Override
    public Venta save(Venta entity) {
        return guardar(entity);
    }

    @Override
    public Venta update(Venta entity) {
        return actualizar(entity.getIdVenta(), entity);
    }

    @Override
    public Optional<Venta> findById(Long id) {
        return ventaRepository.buscarPorId(id);
    }

    @Override
    public List<Venta> findAll() {
        return listarTodo();
    }

    @Override
    public void deleteById(Long id) {
        eliminar(id);
    }

    @Override
    public boolean existsById(Long id) {
        return buscarPorId(id) != null;
    }
}