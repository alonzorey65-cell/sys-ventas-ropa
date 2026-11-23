package pe.edu.upeu;

/**
 * Punto de entrada alternativo para empaquetado ejecutable (fat JAR).
 * Evita problemas de arranque de JavaFX cuando se ejecuta desde un JAR sombreado.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
