package pe.edu.upeu.repository;

import pe.edu.upeu.model.Venta; // Ajusta el import si tu modelo está en pe.edu.upeu.model

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class VentaRepository extends AbstractJpaRepository<Venta, Long> {

    @Override
    protected String getTableName() {
        return "venta";
    }

    @Override
    protected String getPkColumn() {
        return "id_venta";
    }

    @Override
    protected Venta insert(Connection connection, Venta entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO venta(fecha, id_cliente, id_usuario, total, tipo_comprobante, serie, nro_comprobante) VALUES(?, ?, ?, ?, ?, ?, ?)",
                entity.getFecha() != null ? Timestamp.valueOf(entity.getFecha()) : new Timestamp(System.currentTimeMillis()),
                entity.getIdCliente(),
                entity.getIdUsuario(),
                entity.getTotal(),
                entity.getTipoComprobante(),
                entity.getSerie(),
                entity.getNroComprobante());
        entity.setIdVenta(id);
        return entity;
    }

    @Override
    protected Venta updateRow(Connection connection, Venta entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE venta SET fecha = ?, id_cliente = ?, id_usuario = ?, total = ?, tipo_comprobante = ?, serie = ?, nro_comprobante = ? WHERE id_venta = ?",
                entity.getFecha() != null ? Timestamp.valueOf(entity.getFecha()) : new Timestamp(System.currentTimeMillis()),
                entity.getIdCliente(),
                entity.getIdUsuario(),
                entity.getTotal(),
                entity.getTipoComprobante(),
                entity.getSerie(),
                entity.getNroComprobante(),
                entity.getIdVenta());
        return entity;
    }

    @Override
    protected Venta mapRow(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("fecha");
        return Venta.builder()
                .idVenta(rs.getLong("id_venta"))
                .fecha(timestamp != null ? timestamp.toLocalDateTime() : null)
                .idCliente(rs.getLong("id_cliente"))
                .idUsuario(rs.getLong("id_usuario"))
                .total(rs.getDouble("total"))
                .tipoComprobante(rs.getString("tipo_comprobante"))
                .serie(rs.getString("serie"))
                .nroComprobante(rs.getString("nro_comprobante"))
                .build();
    }

    // =========================================================================
    // Métodos obligatorios de la interfaz genérica
    // =========================================================================

    @Override
    public Venta save(Venta entity) {
        return guardar(entity);
    }

    @Override
    public Venta update(Venta entity) {
        return modificar(entity);
    }

    @Override
    public Optional<Venta> findById(Long id) {
        return buscarPorId(id);
    }

    @Override
    public List<Venta> findAll() {
        return listarTodos();
    }

    @Override
    public void deleteById(Long id) {
        eliminar(id);
    }

    @Override
    public boolean existsById(Long id) {
        return buscarPorId(id).isPresent();
    }
}