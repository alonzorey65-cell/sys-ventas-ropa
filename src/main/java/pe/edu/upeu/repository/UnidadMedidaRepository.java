package pe.edu.upeu.repository;

import pe.edu.upeu.model.UnidadMedida;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UnidadMedidaRepository extends AbstractJpaRepository<UnidadMedida, Long> {

    @Override
    protected String getTableName() {
        return "unid_medida";
    }

    @Override
    protected String getPkColumn() {
        return "id_unid_medida";
    }

    @Override
    protected UnidadMedida insert(Connection connection, UnidadMedida entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO unid_medida(nombre, abreviatura) VALUES(?, ?)",
                entity.getNombre(),
                entity.getAbreviatura());
        entity.setIdUnidMedida(id);
        return entity;
    }

    @Override
    protected UnidadMedida updateRow(Connection connection, UnidadMedida entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE unid_medida SET nombre = ?, abreviatura = ? WHERE id_unid_medida = ?",
                entity.getNombre(),
                entity.getAbreviatura(),
                entity.getIdUnidMedida());
        return entity;
    }

    @Override
    protected UnidadMedida mapRow(ResultSet rs) throws SQLException {
        return UnidadMedida.builder()
                .idUnidMedida(rs.getLong("id_unid_medida"))
                .nombre(rs.getString("nombre"))
                .abreviatura(rs.getString("abreviatura"))
                .build();
    }

    @Override
    public UnidadMedida save(UnidadMedida entity) {
        return null;
    }

    @Override
    public UnidadMedida update(UnidadMedida entity) {
        return null;
    }

    @Override
    public Optional<UnidadMedida> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<UnidadMedida> findAll() {
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