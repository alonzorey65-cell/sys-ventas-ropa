package pe.edu.upeu.dto;

public class SessionManager {
    private static SessionManager instance;
    private Long userId;       // <-- Agregado para almacenar el ID del usuario logueado
    private String userPerfil;
    private String userName;

    private SessionManager() {
        this.userPerfil = "1"; // Valor por defecto: Administrador
    }

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