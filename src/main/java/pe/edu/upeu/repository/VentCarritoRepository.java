package pe.edu.upeu.repository;

import pe.edu.upeu.model.VentCarrito;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class VentCarritoRepository extends AbstractJpaRepository<VentCarrito, Long> {

    @Override
    protected String getTableName() {
        return "vent_carrito";
    }

    @Override
    protected String getPkColumn() {
        return "id_vent_carrito";
    }

    @Override
    protected VentCarrito insert(Connection connection, VentCarrito entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO vent_carrito(id_producto, nombre_producto, cantidad, p_buffer, subtotal, id_usuario) VALUES(?, ?, ?, ?, ?, ?)",
                entity.getIdProducto(),
                entity.getNombreProducto(),
                entity.getCantidad(),
                entity.getPBuffer(),
                entity.getSubtotal(),
                entity.getIdUsuario());
        entity.setIdVentCarrito(id);
        return entity;
    }

    @Override
    protected VentCarrito updateRow(Connection connection, VentCarrito entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE vent_carrito SET id_producto = ?, nombre_producto = ?, cantidad = ?, p_buffer = ?, subtotal = ?, id_usuario = ? WHERE id_vent_carrito = ?",
                entity.getIdProducto(),
                entity.getNombreProducto(),
                entity.getCantidad(),
                entity.getPBuffer(),
                entity.getSubtotal(),
                entity.getIdUsuario(),
                entity.getIdVentCarrito());
        return entity;
    }

    @Override
    protected VentCarrito mapRow(ResultSet rs) throws SQLException {
        return VentCarrito.builder()
                .idVentCarrito(rs.getLong("id_vent_carrito"))
                .idProducto(rs.getLong("id_producto"))
                .nombreProducto(rs.getString("nombre_producto"))
                .cantidad(rs.getDouble("cantidad"))
                .pBuffer(rs.getDouble("p_buffer"))
                .subtotal(rs.getDouble("subtotal"))
                .idUsuario(rs.getLong("id_usuario"))
                .build();
    }

    @Override
    public VentCarrito save(VentCarrito entity) {
        return null;
    }

    @Override
    public VentCarrito update(VentCarrito entity) {
        return null;
    }

    @Override
    public Optional<VentCarrito> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<VentCarrito> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    // Métodos requeridos por la interfaz genérica (si no los hereda correctamente de AbstractJpaRepository)
    @Override
    public VentCarrito guardar(VentCarrito entity) {
        return super.guardar(entity);
    }

    @Override
    public VentCarrito modificar(VentCarrito entity) {
        return super.modificar(entity);
    }

    @Override
    public Optional<VentCarrito> buscarPorId(Long id) {
        return super.buscarPorId(id);
    }

    @Override
    public List<VentCarrito> listarTodos() {
        return super.listarTodos();
    }

    @Override
    public void eliminar(Long id) {
        super.eliminar(id);
    }
}