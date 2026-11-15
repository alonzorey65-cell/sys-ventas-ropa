package pe.edu.upeu.repository;

import pe.edu.upeu.config.Conexion;
import pe.edu.upeu.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository extends AbstractJpaRepository<Usuario, Long> {

    @Override
    protected String getTableName() {
        return "usuario";
    }

    @Override
    protected String getPkColumn() {
        return "id_usuario";
    }

    @Override
    protected Usuario insert(Connection connection, Usuario entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO usuario(username, password, id_perfil) VALUES(?, ?, ?)",
                entity.getUsuario(),
                entity.getClave(),
                entity.getIdPerfil() == null ? 1L : entity.getIdPerfil());
        entity.setIdUsuario(id);
        return entity;
    }

    @Override
    protected Usuario updateRow(Connection connection, Usuario entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE usuario SET username = ?, password = ?, id_perfil = ? WHERE id_usuario = ?",
                entity.getUsuario(),
                entity.getClave(),
                entity.getIdPerfil() == null ? 1L : entity.getIdPerfil(),
                entity.getIdUsuario());
        return entity;
    }

    @Override
    protected Usuario mapRow(ResultSet rs) throws SQLException {
        return Usuario.builder()
                .idUsuario(rs.getLong("id_usuario"))
                .usuario(rs.getString("username"))
                .clave(rs.getString("password"))
                .idPerfil(rs.getLong("id_perfil"))
                .build();
    }

    public Optional<Usuario> findByUsuarioAndClave(String user, String pass) {
        String sql = "SELECT * FROM usuario WHERE username = ? AND password = ?";
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user != null ? user.trim() : "");
            pstmt.setString(2, pass != null ? pass.trim() : "");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por credenciales: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Usuario save(Usuario entity) { return guardar(entity); }
    @Override
    public Usuario update(Usuario entity) { return modificar(entity); }
    @Override
    public Optional<Usuario> findById(Long id) { return buscarPorId(id); }
    @Override
    public List<Usuario> findAll() { return listarTodos(); }
    @Override
    public void deleteById(Long id) { eliminar(id); }
    @Override
    public boolean existsById(Long id) { return buscarPorId(id).isPresent(); }
}