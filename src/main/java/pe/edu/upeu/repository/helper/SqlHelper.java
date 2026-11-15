package pe.edu.upeu.repository.helper;

import pe.edu.upeu.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlHelper {

    /**
     * Método genérico para ejecutar operaciones INSERT, UPDATE o DELETE.
     * @param sql Consulta SQL con signos de interrogación (?)
     * @param params Parámetros dinámicos para los comodines de la consulta
     * @return true si la operación afectó al menos una fila, false en caso contrario
     */
    public static boolean ejecutar(String sql, Object... params) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setParameters(ps, params);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Método auxiliar privado para asignar los parámetros dinámicos al PreparedStatement
     */
    private static void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
    }
}