package pe.edu.upeu.repository;

import pe.edu.upeu.model.Cliente;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClienteRepository extends AbstractJpaRepository<Cliente, Long> {

    @Override
    protected String getTableName() {
        return "cliente";
    }

    @Override
    protected String getPkColumn() {
        return "id_cliente";
    }

    @Override
    protected Cliente insert(Connection connection, Cliente entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO cliente(nombre, dni_ruc, direccion, telefono) VALUES(?, ?, ?, ?)",
                entity.getNombre(),
                entity.getDniRuc(),
                entity.getDireccion(),
                entity.getTelefono());
        entity.setIdCliente(id);
        return entity;
    }

    @Override
    protected Cliente updateRow(Connection connection, Cliente entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE cliente SET nombre = ?, dni_ruc = ?, direccion = ?, telefono = ? WHERE id_cliente = ?",
                entity.getNombre(),
                entity.getDniRuc(),
                entity.getDireccion(),
                entity.getTelefono(),
                entity.getIdCliente());
        return entity;
    }

    @Override
    protected Cliente mapRow(ResultSet rs) throws SQLException {
        return Cliente.builder()
                .idCliente(rs.getLong("id_cliente"))
                .nombre(rs.getString("nombre"))
                .dniRuc(rs.getString("dni_ruc"))
                .direccion(rs.getString("direccion"))
                .telefono(rs.getString("telefono"))
                .build();
    }

    public Optional<Cliente> buscarPorDniRuc(String dniRuc) {
        String sql = "SELECT * FROM cliente WHERE dni_ruc = ?";
        try (Connection conn = pe.edu.upeu.config.Conexion.obtenerConexion();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dniRuc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por DNI/RUC: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Cliente save(Cliente entity) { return guardar(entity); }
    @Override
    public Cliente update(Cliente entity) { return modificar(entity); }
    @Override
    public Optional<Cliente> findById(Long id) { return buscarPorId(id); }
    @Override
    public List<Cliente> findAll() { return listarTodos(); }
    @Override
    public void deleteById(Long id) { eliminar(id); }
    @Override
    public boolean existsById(Long id) { return buscarPorId(id).isPresent(); }
}