package pe.edu.upeu.service.impl;

import pe.edu.upeu.dto.MenuMenuItenTO;
import pe.edu.upeu.repository.MenuMenuItemRepository;
import pe.edu.upeu.service.IMenuMenuItemDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MenuMenuItemDaoImp implements IMenuMenuItemDao {

    private final MenuMenuItemRepository menuRepository = new MenuMenuItemRepository();

    @Override
    public List<MenuMenuItenTO> listaAccesos(String perfil, Properties resources) {
        // Consulta dinámica directamente desde la base de datos H2 según el rol/perfil
        return menuRepository.listarPorPerfil(perfil);
    }

    @Override
    public int[] contarMenuMunuItem(List<MenuMenuItenTO> lista) {
        int menusUnicos = 0;
        int totalItems = lista.size();
        String ultimoMenu = "";

        for (MenuMenuItenTO item : lista) {
            if (!item.getMenunombre().equals(ultimoMenu)) {
                menusUnicos++;
                ultimoMenu = item.getMenunombre();
            }
        }
        return new int[]{menusUnicos, totalItems};
    }

    @Override
    public Map<String, String[]> accesosAutorizados(List<MenuMenuItenTO> lista) {
        Map<String, String[]> map = new HashMap<>();

        // Mapeo de rutas FXML vinculadas a los IDs de objetos devueltos por la BD
        map.put("mi1", new String[]{"/view/main_producto.fxml", "Productos", ""});
        map.put("mi2", new String[]{"/view/main_categoria.fxml", "Categorías", ""});
        map.put("mi3", new String[]{"/view/main_venta.fxml", "Ventas / POS", ""});
        map.put("mi4", new String[]{"/view/main_comprobante.fxml", "Comprobantes", ""});
        map.put("mi5", new String[]{"/view/login.fxml", "Cerrar Sesión", "C"});
        map.put("mi6", new String[]{"/view/main_marca.fxml", "Marcas", ""});
        map.put("mi7", new String[]{"/view/main_unidadmedida.fxml", "Unidades de Medida", ""});
        map.put("mi8", new String[]{"/view/main_cliente.fxml", "Clientes", ""});
        map.put("mi9", new String[]{"/view/main_usuario.fxml", "Usuarios", ""});
        map.put("mi10", new String[]{"/view/main_vendedor.fxml", "Panel Vendedor", ""});
        map.put("mi11", new String[]{"/view/main_admin.fxml", "Panel Administrador", ""});
        map.put("mi12", new String[]{"/view/main_almacen.fxml", "Panel Almacén", ""});

        return map;
    }
}