package pe.edu.upeu.repository;

import pe.edu.upeu.model.VentaDetalle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class VentaDetalleRepository extends AbstractJpaRepository<VentaDetalle, Long> {

    @Override
    protected String getTableName() {
        return "venta_detalle";
    }

    @Override
    protected String getPkColumn() {
        return "id_venta_detalle";
    }

    @Override
    protected VentaDetalle insert(Connection connection, VentaDetalle entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO venta_detalle(id_venta, id_producto, cantidad, precio, subtotal) VALUES(?, ?, ?, ?, ?)",
                entity.getIdVenta(),
                entity.getIdProducto(),
                entity.getCantidad(),
                entity.getPrecio(),
                entity.getSubtotal());
        entity.setIdVentaDetalle(id);
        return entity;
    }

    @Override
    protected VentaDetalle updateRow(Connection connection, VentaDetalle entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE venta_detalle SET id_venta = ?, id_producto = ?, cantidad = ?, precio = ?, subtotal = ? WHERE id_venta_detalle = ?",
                entity.getIdVenta(),
                entity.getIdProducto(),
                entity.getCantidad(),
                entity.getPrecio(),
                entity.getSubtotal(),
                entity.getIdVentaDetalle());
        return entity;
    }

    @Override
    protected VentaDetalle mapRow(ResultSet rs) throws SQLException {
        return VentaDetalle.builder()
                .idVentaDetalle(rs.getLong("id_venta_detalle"))
                .idVenta(rs.getLong("id_venta"))
                .idProducto(rs.getLong("id_producto"))
                .cantidad(rs.getDouble("cantidad"))
                .precio(rs.getDouble("precio"))
                .subtotal(rs.getDouble("subtotal"))
                .build();
    }

    @Override
    public VentaDetalle save(VentaDetalle entity) { return guardar(entity); }

    @Override
    public VentaDetalle update(VentaDetalle entity) { return modificar(entity); }

    @Override
    public Optional<VentaDetalle> findById(Long id) { return buscarPorId(id); }

    @Override
    public List<VentaDetalle> findAll() { return listarTodos(); }

    @Override
    public void deleteById(Long id) { eliminar(id); }

    @Override
    public boolean existsById(Long id) { return buscarPorId(id).isPresent(); }

    // Métodos requeridos por la interfaz genérica
    @Override
    public VentaDetalle guardar(VentaDetalle entity) {
        return super.guardar(entity);
    }

    @Override
    public VentaDetalle modificar(VentaDetalle entity) {
        return super.modificar(entity);
    }

    @Override
    public Optional<VentaDetalle> buscarPorId(Long id) {
        return super.buscarPorId(id);
    }

    @Override
    public List<VentaDetalle> listarTodos() {
        return super.listarTodos();
    }

    @Override
    public void eliminar(Long id) {
        super.eliminar(id);
    }
}