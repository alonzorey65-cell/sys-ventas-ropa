package pe.edu.upeu.service.impl;

import pe.edu.upeu.model.VentaDetalle;
import pe.edu.upeu.repository.VentaDetalleRepository;
import pe.edu.upeu.service.IVentaDetalleService;

import java.util.List;
import java.util.stream.Collectors;

public class VentaDetalleServiceImp implements IVentaDetalleService {

    private final VentaDetalleRepository ventaDetalleRepository;

    public VentaDetalleServiceImp(VentaDetalleRepository ventaDetalleRepository) {
        this.ventaDetalleRepository = ventaDetalleRepository;
    }

    @Override
    public List<VentaDetalle> listarTodo() {
        return ventaDetalleRepository.listarTodos();
    }

    @Override
    public VentaDetalle buscarPorId(Long id) {
        return ventaDetalleRepository.buscarPorId(id).orElse(null);
    }

    @Override
    public VentaDetalle guardar(VentaDetalle entidad) {
        return ventaDetalleRepository.guardar(entidad);
    }

    @Override
    public VentaDetalle actualizar(Long id, VentaDetalle entidad) {
        entidad.setIdVentaDetalle(id);
        return ventaDetalleRepository.modificar(entidad);
    }

    @Override
    public boolean eliminar(Long id) {
        try {
            ventaDetalleRepository.eliminar(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public VentaDetalle save(VentaDetalle ventaDetalle) { return guardar(ventaDetalle); }

    @Override
    public VentaDetalle update(Long id, VentaDetalle ventaDetalle) { return actualizar(id, ventaDetalle); }

    @Override
    public List<VentaDetalle> findAll() { return listarTodo(); }

    @Override
    public VentaDetalle findById(Long id) { return buscarPorId(id); }

    @Override
    public void delete(Long id) { eliminar(id); }

    @Override
    public List<VentaDetalle> listarPorVenta(Long idVenta) {
        return ventaDetalleRepository.listarTodos().stream()
                .filter(detalle -> idVenta != null && idVenta.equals(detalle.getIdVenta()))
                .collect(Collectors.toList());
    }
}
