package pe.edu.upeu;


/**
 * POO - ABSTRACCIÓN / PUNTO DE ENTRADA:
 * - Clase simple con responsabilidad única: iniciar la aplicación.
 * - Abstracción de arranque: separa el lanzador del ciclo JavaFX para permitir empaquetado fat JAR.
 * - Reutiliza App.main(), evitando duplicar lógica.
 */
public class SysVentas {
    public static void main(String[] args) {
        App.main(args);
    }
}
