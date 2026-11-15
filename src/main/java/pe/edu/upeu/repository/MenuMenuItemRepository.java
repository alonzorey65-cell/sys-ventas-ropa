package pe.edu.upeu.repository;

import pe.edu.upeu.config.Conexion;
import pe.edu.upeu.dto.MenuMenuItenTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuMenuItemRepository {

    public List<MenuMenuItenTO> listarPorPerfil(String perfil) {
        List<MenuMenuItenTO> lista = new ArrayList<>();
        String sql = "SELECT menu_nombre, menu_item_nombre, id_nombre_obj FROM menu WHERE perfil = ?";

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, perfil);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MenuMenuItenTO item = new MenuMenuItenTO();
                    item.setMenunombre(rs.getString("menu_nombre"));
                    item.setMenuitemnombre(rs.getString("menu_item_nombre"));
                    item.setIdNombreObj(rs.getString("id_nombre_obj"));
                    lista.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar los menús desde la BD: " + e.getMessage());
        }
        return lista;
    }
}