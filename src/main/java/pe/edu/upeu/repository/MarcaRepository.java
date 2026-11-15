package pe.edu.upeu.repository;

import pe.edu.upeu.model.Marca;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MarcaRepository extends AbstractJpaRepository<Marca, Long> {

    @Override
    protected String getTableName() {
        return "marca";
    }

    @Override
    protected String getPkColumn() {
        return "id_marca";
    }

    @Override
    protected Marca insert(Connection connection, Marca entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO marca(nombre) VALUES(?)",
                entity.getNombre());
        entity.setIdMarca(id);
        return entity;
    }

    @Override
    protected Marca updateRow(Connection connection, Marca entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE marca SET nombre = ? WHERE id_marca = ?",
                entity.getNombre(),
                entity.getIdMarca());
        return entity;
    }

    @Override
    protected Marca mapRow(ResultSet rs) throws SQLException {
        return Marca.builder()
                .idMarca(rs.getLong("id_marca"))
                .nombre(rs.getString("nombre"))
                .build();
    }

    @Override
    public Marca save(Marca entity) {
        return null;
    }

    @Override
    public Marca update(Marca entity) {
        return null;
    }

    @Override
    public Optional<Marca> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Marca> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }
}