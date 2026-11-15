package pe.edu.upeu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VentCarrito {
    private Long idVentCarrito;
    private Long idProducto;
    private String nombreProducto;
    private Double cantidad;
    private Double pBuffer;
    private Double subtotal;
    private Long idUsuario;
}