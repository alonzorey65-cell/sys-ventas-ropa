package pe.edu.upeu.dto.comprobante;

import java.util.List;

public class Comprobante {
    private String serie;
    private String numero;
    private String fecha;
    private String clienteNombre;
    private String clienteDniRuc;
    private String clienteDireccion;
    private double total;
    private List<DetalleItem> items;
    private String estado;

    public Comprobante() {}

    public Comprobante(String serie, String numero, String fecha, String clienteNombre, String clienteDniRuc, double total, List<DetalleItem> items) {
        this(serie, numero, fecha, clienteNombre, clienteDniRuc, "", total, items);
    }

    public Comprobante(String serie, String numero, String fecha, String clienteNombre, String clienteDniRuc, String clienteDireccion, double total, List<DetalleItem> items) {
        this.serie = serie;
        this.numero = numero;
        this.fecha = fecha;
        this.clienteNombre = clienteNombre;
        this.clienteDniRuc = clienteDniRuc;
        this.clienteDireccion = clienteDireccion;
        this.total = total;
        this.items = items;
    }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteDniRuc() { return clienteDniRuc; }
    public void setClienteDniRuc(String clienteDniRuc) { this.clienteDniRuc = clienteDniRuc; }

    public String getClienteDireccion() { return clienteDireccion; }
    public void setClienteDireccion(String clienteDireccion) { this.clienteDireccion = clienteDireccion; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public List<DetalleItem> getItems() { return items; }
    public void setItems(List<DetalleItem> items) { this.items = items; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public static class DetalleItem {
        private String descripcion;
        private int cantidad;
        private double precioUnitario;
        private double subtotal;

        public DetalleItem(String descripcion, int cantidad, double precioUnitario, double subtotal) {
            this.descripcion = descripcion;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = subtotal;
        }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
        public double getSubtotal() { return subtotal; }
        public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    }
}