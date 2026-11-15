package pe.edu.upeu.service;

import pe.edu.upeu.model.Usuario;
import java.util.Optional;

public interface IUsuarioService extends ICrudGenericoService<Usuario, Long> {
    Optional<Usuario> loginUsuario(String usuario, String clave);
}