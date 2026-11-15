package pe.edu.upeu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VentaDetalle {
    private Long idVentaDetalle;
    private Long idVenta;
    private Long idProducto;
    private Double cantidad;
    private Double precioVenta;
    private Double subtotal;
    private Double precio;
}