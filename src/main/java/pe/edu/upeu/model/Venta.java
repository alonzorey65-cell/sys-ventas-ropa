package pe.edu.upeu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Venta {
    private Long idVenta;
    private LocalDateTime fecha;
    private Long idCliente;
    private Long idUsuario;
    private Double total;
    private String tipoComprobante;
    private String serie;
    private String nroComprobante;
}