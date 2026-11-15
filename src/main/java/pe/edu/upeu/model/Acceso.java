package pe.edu.upeu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Acceso {
    private Long idAcceso;
    private String menuNombre;
    private String menuItemNombre;
    private String idNombreObj;
    private String perfil;
}