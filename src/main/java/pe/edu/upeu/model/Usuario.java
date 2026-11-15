package pe.edu.upeu.model; // O tu paquete correspondiente (ej: pe.edu.upeu.sysventas.model)

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    private Long idUsuario;
    private String usuario;
    private String clave;
    private Long idPerfil;
}