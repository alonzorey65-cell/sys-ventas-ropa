package pe.edu.upeu.dto;

public class ComboBoxOption {
    private int id;
    private String nombre;

    public ComboBoxOption(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        // Esto permite que el ComboBox muestre directamente el nombre en la interfaz
        return nombre;
    }
}