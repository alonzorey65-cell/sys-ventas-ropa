package pe.edu.upeu;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.config.DatabaseConfig;
import pe.edu.upeu.config.Conexion;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class DbSmokeTest {

    @Test
    void databaseConnectsAndSchemaLoads() throws Exception {
        DatabaseConfig.init();
        try (Connection c = Conexion.obtenerConexion()) {
            assertNotNull(c);
            assertFalse(c.isClosed());

            // Check core tables exist and seed data loaded
            assertTrue(count(c, "SELECT COUNT(*) FROM usuario") >= 2);
            assertTrue(count(c, "SELECT COUNT(*) FROM menu") >= 7);
            assertTrue(count(c, "SELECT COUNT(*) FROM producto") >= 3);
            assertTrue(count(c, "SELECT COUNT(*) FROM cliente") >= 1);
            assertTrue(count(c, "SELECT COUNT(*) FROM perfil") >= 2);
            assertTrue(count(c, "SELECT COUNT(*) FROM categoria") >= 1);
            assertTrue(count(c, "SELECT COUNT(*) FROM marca") >= 1);
            assertTrue(count(c, "SELECT COUNT(*) FROM unid_medida") >= 1);

            // Check admin login data
            try (PreparedStatement ps = c.prepareStatement("SELECT username, id_perfil FROM usuario WHERE username=?")) {
                ps.setString(1, "admin");
                try (ResultSet rs = ps.executeQuery()) {
                    assertTrue(rs.next());
                    assertEquals(1L, rs.getLong("id_perfil"));
                }
            }
        } finally {
            DatabaseConfig.shutdown();
        }
    }

    private long count(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }
}
