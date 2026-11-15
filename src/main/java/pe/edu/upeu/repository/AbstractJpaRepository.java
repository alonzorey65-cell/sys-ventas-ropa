package pe.edu.upeu.repository;

import pe.edu.upeu.config.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * ========================= POO: ABSTRACCIÓN, HERENCIA y POLIMORFISMO =========================
 * CLASE ABSTRACTA (ABSTRACCIÓN): define comportamiento común de repositorios y declara métodos
 *        abstractos que las subclases deben implementar (getTableName, mapRow, insert, etc.).
 * HERENCIA: clases como ProductoRepository, ClienteRepository, UsuarioRepository, etc.
 *        "extends AbstractJpaRepository" y heredan su lógica común (guardar, listar, eliminar...).
 * POLIMORFISMO:
 *        - Sobrescritura (@Override): cada repositorio concreto implementa mapRow/insert/updateRow
 *          con su propio comportamiento.
 *        - Uso polimórfico: se puede tratar a un ProductoRepository como ICrudGenericoRepository/AbstractJpaRepository.
 * ENCAPSULAMIENTO: detalles de SQL/conexión quedan encapsulados en esta capa; el servicio no maneja JDBC directo.
 * CONEXIÓN: Servicio -> Repositorio (hereda de AbstractJpaRepository) -> Conexion/DatabaseConfig -> Neon(PostgreSQL).
 */
public abstract class AbstractJpaRepository<T, ID> implements ICrudGenericoRepository<T, ID> { // HERENCIA base + ABSTRACCIÓN

    protected abstract String getTableName();
    protected abstract String getPkColumn();

    protected abstract T insert(Connection connection, T entity) throws SQLException;
    protected abstract T updateRow(Connection connection, T entity) throws SQLException;
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    @Override
    public T guardar(T entidad) {
        try (Connection conn = Conexion.obtenerConexion()) {
            return insert(conn, entidad);
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar en " + getTableName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public T modificar(T entidad) {
        try (Connection conn = Conexion.obtenerConexion()) {
            return updateRow(conn, entidad);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar en " + getTableName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<T> listarTodos() {
        List<T> lista = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();
        try (Connection conn = Conexion.obtenerConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar " + getTableName() + ": " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Optional<T> buscarPorId(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getPkColumn() + " = ?";
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParam(ps, 1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por ID en " + getTableName() + ": " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void eliminar(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getPkColumn() + " = ?";
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParam(ps, 1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar en " + getTableName() + ": " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================

    protected Connection openConnection() throws SQLException {
        return Conexion.obtenerConexion();
    }

    protected int executeUpdateStandalone(String sql, Object... params) {
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error en executeUpdateStandalone: " + e.getMessage(), e);
        }
    }

    protected void executeUpdate(Connection connection, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParams(ps, params);
            ps.executeUpdate();
        }
    }

    protected long executeInsertGetKey(Connection connection, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, params);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener la llave generada.");
    }

    protected boolean executeExists(String sql, Object... params) {
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en executeExists: " + e.getMessage(), e);
        }
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            setParam(ps, i + 1, params[i]);
        }
    }

    private void setParam(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value instanceof Long) {
            ps.setLong(index, (Long) value);
        } else if (value instanceof Integer) {
            ps.setInt(index, (Integer) value);
        } else if (value instanceof String) {
            ps.setString(index, (String) value);
        } else if (value instanceof Double) {
            ps.setDouble(index, (Double) value);
        } else if (value instanceof Boolean) {
            ps.setBoolean(index, (Boolean) value);
        } else {
            ps.setObject(index, value);
        }
    }
}