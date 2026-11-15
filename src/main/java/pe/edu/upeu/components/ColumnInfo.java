package pe.edu.upeu.components;

public class ColumnInfo {
    private String titulo;
    private String propiedad;
    private double ancho;

    public ColumnInfo(String titulo, String propiedad, double ancho) {
        this.titulo = titulo;
        this.propiedad = propiedad;
        this.ancho = ancho;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(String propiedad) {
        this.propiedad = propiedad;
    }

    public double getAncho() {
        return ancho;
    }

    public void setAncho(double ancho) {
        this.ancho = ancho;
    }
}