package pe.edu.upeu.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class IdiomaConfig {

    /**
     * Obtiene el ResourceBundle según el idioma seleccionado.
     * @param codigoIdioma Código del idioma ("es" para español, "en" para inglés, "fr" para francés)
     * @return ResourceBundle con los textos correspondientes
     */
    public static ResourceBundle obtenerBundle(String codigoIdioma) {
        Locale locale = new Locale(codigoIdioma);
        // "lenguaje.mensajes" busca en la carpeta o paquete 'lenguaje' el archivo base 'mensajes'
        return ResourceBundle.getBundle("lenguaje.mensajes", locale);
    }
}