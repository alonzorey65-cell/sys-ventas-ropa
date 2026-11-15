package pe.edu.upeu.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ========================= POO y CONEXIÓN a Base de Datos =========================
 * CLASE: Conexion actúa como fachada (punto de acceso simple) para obtener conexiones.
 * ABSTRACCIÓN/ENCAPSULAMIENTO: oculta los detalles del pool y configuración delegando en DatabaseConfig;
 *        las demás capas piden una conexión sin conocer cómo se crea internamente.
 * MÉTODO: obtenerConexion() expone el comportamiento necesario para la capa repositorio.
 * CONEXIÓN: Repositorios -> Conexion.obtenerConexion() -> DatabaseConfig/HikariCP -> Neon PostgreSQL.
 */
public class Conexion { // CLASE fachada de conexión (capa infraestructura)

    private Conexion() {
    }

    public static Connection obtenerConexion() throws SQLException {
        return DatabaseConfig.getConnection();
    }
}