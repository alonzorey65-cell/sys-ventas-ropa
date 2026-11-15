package pe.edu.upeu.service;

import pe.edu.upeu.model.VentaDetalle;
import java.util.List;

public interface IVentaDetalleService extends ICrudGenericoService<VentaDetalle, Long> {
    // Método personalizado útil para filtrar los detalles asociados a una venta específica
    List<VentaDetalle> listarPorVenta(Long idVenta);
}