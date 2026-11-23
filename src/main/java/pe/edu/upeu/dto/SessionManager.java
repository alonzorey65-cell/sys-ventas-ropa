package pe.edu.upeu.dto;

/**
 * POO / PATRÓN SINGLETON:
 * - SINGLETON: garantiza una única instancia de sesión en toda la app.
 *   Se logra con: atributo estático `instance`, constructor privado y método estático `getInstance()`.
 * - ENCAPSULAMIENTO: atributos privados (userId, userPerfil, userName) con getters/setters controlados.
 * - ABSTRACCIÓN: otros componentes piden la sesión sin saber cómo se almacena internamente.
 * - COHESIÓN: esta clase solo se encarga del estado de sesión del usuario autenticado.
 */
public class SessionManager {
    // SINGLETON: instancia única compartida
    private static SessionManager instance;
    // ENCAPSULAMIENTO: estado de sesión oculto
    private Long userId;       // ID del usuario logueado
    private String userPerfil; // Perfil/rol (1=ADMIN, 2=VENDEDOR)
    private String userName;

    // SINGLETON: constructor privado evita `new SessionManager()` desde fuera
    private SessionManager() {
        this.userPerfil = "1"; // Valor por defecto: Administrador
    }

    // SINGLETON: punto global de acceso; synchronized evita problemas en concurrencia
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // ==========================================
    // Métodos para el ID del usuario (userId)
    // ==========================================
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // ==========================================
    // Métodos para el perfil / rol
    // ==========================================
    public String getUserPerfil() {
        return userPerfil;
    }

    public void setUserPerfil(String userPerfil) {
        this.userPerfil = userPerfil;
    }

    // Compatibilidad por si en el login usas setRol / getRol
    public String getRol() {
        return userPerfil;
    }

    public void setRol(String rol) {
        this.userPerfil = rol;
    }

    // ==========================================
    // Métodos para el nombre de usuario
    // ==========================================
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}