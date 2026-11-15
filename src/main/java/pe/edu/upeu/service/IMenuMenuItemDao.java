package pe.edu.upeu.service;

import pe.edu.upeu.dto.MenuMenuItenTO;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IMenuMenuItemDao {
    List<MenuMenuItenTO> listaAccesos(String perfil, Properties resources);
    int[] contarMenuMunuItem(List<MenuMenuItenTO> lista);
    Map<String, String[]> accesosAutorizados(List<MenuMenuItenTO> lista);
}