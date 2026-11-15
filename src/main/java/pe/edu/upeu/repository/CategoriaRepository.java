package pe.edu.upeu.repository;

import pe.edu.upeu.model.Categoria;
import pe.edu.upeu.model.Marca;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CategoriaRepository extends AbstractJpaRepository<Categoria, Long>{
    @Override
    protected String getTableName() {
        return "categoria";
    }

    @Override
    protected String getPkColumn() {
        return "id_categoria";
    }

    @Override
    protected Categoria insert(Connection connection, Categoria entity) throws SQLException {
        long id=executeInsertGetKey(connection,
                " insert into categoria(nombre) values(?) "
                , entity.getNombre());
        entity.setIdCategoria(id);
        return entity;
    }

    @Override
    protected Categoria updateRow(Connection connection, Categoria entity) throws SQLException {
        executeUpdate(connection,
                "update categoria set nombre=? where id_categoria=?",
                entity.getNombre(),
                entity.getIdCategoria());

        return entity;
    }

    @Override
    protected Categoria mapRow(ResultSet rs) throws SQLException {
        return Categoria.builder()
                .idCategoria(rs.getLong("id_categoria"))
                .nombre(rs.getString("nombre"))
                .build();
    }

    @Override
    public Categoria save(Categoria entity) {
        return null;
    }

    @Override
    public Categoria update(Categoria entity) {
        return null;
    }

    @Override
    public Optional<Categoria> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Categoria> findAll() {
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
