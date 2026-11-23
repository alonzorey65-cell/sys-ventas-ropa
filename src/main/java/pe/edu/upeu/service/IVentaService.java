package pe.edu.upeu.service;

import pe.edu.upeu.dto.comprobante.Comprobante;
import pe.edu.upeu.model.Venta;

import java.util.List;
import java.util.Optional;

public interface IVentaService {
    boolean registrarVentaCompleta(Comprobante comprobante);

    List<Venta> listarTodo();

    Venta buscarPorId(Long id);

    Venta guardar(Venta entidad);

    Venta actualizar(Long id, Venta entidad);

    boolean eliminar(Long id);

    boolean anular(Long id, String motivo, Long usuarioId);

    Venta save(Venta entity);

    Venta update(Venta entity);

    Optional<Venta> findById(Long id);

    List<Venta> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}