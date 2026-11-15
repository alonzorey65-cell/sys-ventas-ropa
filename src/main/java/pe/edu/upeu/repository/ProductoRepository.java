package pe.edu.upeu.repository;

import pe.edu.upeu.model.Producto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/*
 * ========================= POO: HERENCIA y POLIMORFISMO =========================
 * HERENCIA: ProductoRepository hereda (extends) de AbstractJpaRepository, reutilizando la
 *        lógica común de CRUD (listarTodos, buscarPorId, guardar, etc.).
 * POLIMORFISMO (sobrescritura): implementa los métodos abstractos de la clase padre
 *        (getTableName, getPkColumn, insert, updateRow, mapRow) con el comportamiento específico
 *        para la entidad Producto y la tabla "producto".
 * CLASE/OBJETOS: esta clase sirve para crear objetos repositorio; cada operación recibe/devuelve
 *        OBJETOS Producto (instancias de la clase modelo).
 * CONEXIÓN: ProductoServiceImp -> ProductoRepository -> AbstractJpaRepository -> Conexion -> Neon DB.
 */
public class ProductoRepository extends AbstractJpaRepository<Producto, Long> { // HERENCIA

    @Override
    protected String getTableName() {
        return "producto";
    }

    @Override
    protected String getPkColumn() {
        return "id"; // Si tu PK se llama distinto en tu BD, puedes cambiarlo aquí
    }

    @Override
    protected Producto insert(Connection connection, Producto entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO producto(nombre, precio, stock, id_categoria, id_marca, id_unid_medida) VALUES(?, ?, ?, ?, ?, ?)",
                entity.getNombre(),
                entity.getPrecio(),
                entity.getStock(),
                entity.getIdCategoria(),
                entity.getIdMarca(),
                entity.getIdUnidMedida());
        entity.setIdProducto(id);
        return entity;
    }

    @Override
    protected Producto updateRow(Connection connection, Producto entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE producto SET nombre = ?, precio = ?, stock = ?, id_categoria = ?, id_marca = ?, id_unid_medida = ? WHERE id = ?",
                entity.getNombre(),
                entity.getPrecio(),
                entity.getStock(),
                entity.getIdCategoria(),
                entity.getIdMarca(),
                entity.getIdUnidMedida(),
                entity.getIdProducto());
        return entity;
    }

    @Override
    protected Producto mapRow(ResultSet rs) throws SQLException {
        Producto producto = new Producto();

        // 1. Mapeo seguro para el ID (prueba varios nombres comunes o la columna 1)
        long idVal = 0L;
        try { idVal = rs.getLong("id"); } catch (Exception e1) {
            try { idVal = rs.getLong("id_producto"); } catch (Exception e2) {
                try { idVal = rs.getLong(1); } catch (Exception e3) { idVal = 0L; }
            }
        }
        producto.setIdProducto(idVal);

        // 2. Mapeo seguro para el Nombre
        try {
            producto.setNombre(rs.getString("nombre"));
        } catch (Exception e) {
            try { producto.setNombre(rs.getString(2)); } catch (Exception ex) { producto.setNombre("Sin Nombre"); }
        }

        // 3. Mapeo seguro para el Precio
        try {
            producto.setPrecio(rs.getDouble("precio"));
        } catch (Exception e) {
            try { producto.setPrecio(rs.getDouble("precio_venta")); } catch (Exception ex) { producto.setPrecio(0.0); }
        }

        // 4. Mapeo seguro para el Stock
        try {
            producto.setStock(rs.getDouble("stock"));
        } catch (Exception e) {
            producto.setStock(0.0);
        }

        // 5. Relaciones (Categoría, Marca, Unidad de Medida)
        try { producto.setIdCategoria(rs.getLong("id_categoria")); } catch (Exception e) { producto.setIdCategoria(1L); }
        try { producto.setIdMarca(rs.getLong("id_marca")); } catch (Exception e) { producto.setIdMarca(1L); }
        try { producto.setIdUnidMedida(rs.getLong("id_unid_medida")); } catch (Exception e) { producto.setIdUnidMedida(1L); }

        return producto;
    }

    @Override
    public Producto save(Producto entity) { return guardar(entity); }

    @Override
    public Producto update(Producto entity) { return modificar(entity); }

    @Override
    public Optional<Producto> findById(Long id) { return buscarPorId(id); }

    @Override
    public List<Producto> findAll() { return listarTodos(); }

    @Override
    public void deleteById(Long id) { eliminar(id); }

    @Override
    public boolean existsById(Long id) { return buscarPorId(id).isPresent(); }
}