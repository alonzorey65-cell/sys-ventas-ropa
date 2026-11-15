package pe.edu.upeu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class UtilsX {

    public Properties detectLanguage(String lang) {
        String code = (lang == null || lang.isBlank()) ? "es" : lang.trim().toLowerCase(Locale.ROOT);
        Properties properties = new Properties();

        // 1) Intentar desde classpath: src/main/resources/lenguaje/mensajes_*.properties
        try (InputStream in = UtilsX.class.getResourceAsStream("/lenguaje/mensajes_" + code + ".properties")) {
            if (in != null) {
                try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                    return properties;
                }
            }
        } catch (Exception ignored) { }

        // 2) Intentar ResourceBundle
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("lenguaje.mensajes", new Locale(code));
            for (String key : bundle.keySet()) {
                properties.setProperty(key, bundle.getString(key));
            }
            if (!properties.isEmpty()) return properties;
        } catch (Exception ignored) { }

        // 3) Intentar desde carpeta "lenguaje" en el directorio de trabajo/proyecto
        try {
            File f = new File("lenguaje/mensajes_" + code + ".properties");
            if (f.exists()) {
                try (InputStream in = new FileInputStream(f);
                     InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                    return properties;
                }
            }
        } catch (Exception ignored) { }

        // fallback mínimo
        properties.setProperty("app.titulo", "Sistema de Ventas");
        properties.setProperty("menu.inventario", "Productos");
        properties.setProperty("menu.ventas", "Ventas");
        properties.setProperty("label.bienvenido", "Bienvenido");
        return properties;
    }
}
