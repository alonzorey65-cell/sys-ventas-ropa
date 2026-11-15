package pe.edu.upeu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Emisor {
    private Long idEmisor;
    private String ruc;
    private String razonSocial;
    private String direccion;
    private String telefono;
    private String correo;
}