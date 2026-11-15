package pe.edu.upeu.service.impl;

import pe.edu.upeu.model.Usuario;
import pe.edu.upeu.repository.UsuarioRepository;
import pe.edu.upeu.service.IUsuarioService;

import java.util.List;
import java.util.Optional;

public class UsuarioServiceImp implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImp(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<Usuario> loginUsuario(String usuario, String clave) {
        return usuarioRepository.findByUsuarioAndClave(usuario, clave);
    }

    @Override
    public List<Usuario> listarTodo() {
        return usuarioRepository.listarTodos();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id).orElse(null);
    }

    @Override
    public Usuario guardar(Usuario entidad) {
        return usuarioRepository.guardar(entidad);
    }

    @Override
    public Usuario actualizar(Long id, Usuario entidad) {
        entidad.setIdUsuario(id);
        return usuarioRepository.modificar(entidad);
    }

    @Override
    public boolean eliminar(Long id) {
        try {
            usuarioRepository.eliminar(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    @Override
    public Usuario save(Usuario usuario) {
        return null;
    }

    @Override
    public Usuario update(Long aLong, Usuario usuario) {
        return null;
    }

    @Override
    public List<Usuario> findAll() {
        return List.of();
    }

    @Override
    public Usuario findById(Long aLong) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}