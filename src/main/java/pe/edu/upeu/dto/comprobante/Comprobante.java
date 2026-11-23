package pe.edu.upeu.dto.comprobante;

import java.util.List;

/**
 * DTO Comprobante: objeto de transporte de datos para mostrar/exportar un comprobante.
 * No es tabla BD; se arma en memoria desde datos de Venta, Cliente y VentaDetalle.
 *
 * Flujo:
 * - VentaController / MainComprobanteController -> crean Comprobante (new Comprobante(...))
 * - ComprobanteView.construir() -> lee este DTO y lo renderiza en pantalla
 * - ComprobantePdfService.exportar() -> lee este DTO y genera el PDF
 */
public class Comprobante {
    private String serie;              // Serie del comprobante (ej. B001), viene de Venta/registro
    private String numero;             // Número/correlativo, viene de Venta/registro
    private String fecha;              // Fecha formateada para mostrar, viene de Venta.fecha
    private String clienteNombre;      // Nombre del cliente, viene de Cliente.nombre
    private String clienteDniRuc;      // DNI(8) o RUC(11), viene de Cliente.dniRuc
    private String clienteDireccion;   // Dirección del cliente, viene de Cliente.direccion
    private double total;              // Total de la venta, viene de Venta.total / cálculo POS
    private List<DetalleItem> items;   // Ítems del comprobante, vienen de VentaDetalle/carrito
    private String estado;             // Estado ACTIVO/ANULADO, viene de Venta.estado

    public Comprobante() {} // Constructor vacío: útil para frameworks/creación gradual

    // Constructor de compatibilidad (7 args): delega al constructor completo con dirección vacía.
    public Comprobante(String serie, String numero, String fecha, String clienteNombre, String clienteDniRuc, double total, List<DetalleItem> items) {
        this(serie, numero, fecha, clienteNombre, clienteDniRuc, "", total, items); // manda al constructor completo
    }

    // Constructor completo: recibe datos ya preparados por controlador y los guarda en el DTO.
    public Comprobante(String serie, String numero, String fecha, String clienteNombre, String clienteDniRuc, String clienteDireccion, double total, List<DetalleItem> items) {
        this.serie = serie;                       // guarda serie para View/PDF
        this.numero = numero;                     // guarda número para View/PDF
        this.fecha = fecha;                       // guarda fecha para View/PDF
        this.clienteNombre = clienteNombre;       // guarda nombre cliente para View/PDF
        this.clienteDniRuc = clienteDniRuc;       // guarda DNI/RUC para View/PDF
        this.clienteDireccion = clienteDireccion; // guarda dirección para View/PDF
        this.total = total;                       // guarda total para View/PDF
        this.items = items;                       // guarda detalle de ítems para View/PDF
    }

    // Getters/setters: encapsulan acceso al estado del DTO.
    public String getSerie() { return serie; } // leído por ComprobanteView/ComprobantePdfService
    public void setSerie(String serie) { this.serie = serie; }

    public String getNumero() { return numero; } // leído por View/PDF
    public void setNumero(String numero) { this.numero = numero; }

    public String getFecha() { return fecha; } // leído por View/PDF
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getClienteNombre() { return clienteNombre; } // leído por View/PDF
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteDniRuc() { return clienteDniRuc; } // leído por View/PDF para mostrar DNI o RUC
    public void setClienteDniRuc(String clienteDniRuc) { this.clienteDniRuc = clienteDniRuc; }

    public String getClienteDireccion() { return clienteDireccion; } // leído por View/PDF
    public void setClienteDireccion(String clienteDireccion) { this.clienteDireccion = clienteDireccion; }

    public double getTotal() { return total; } // leído por View/PDF para totales
    public void setTotal(double total) { this.total = total; }

    public List<DetalleItem> getItems() { return items; } // leído por View/PDF para listar detalle
    public void setItems(List<DetalleItem> items) { this.items = items; }

    public String getEstado() { return estado; } // leído por View/PDF para estado ACTIVO/ANULADO
    public void setEstado(String estado) { this.estado = estado; }

    /**
     * Item interno del comprobante.
     * Composición: un Comprobante "tiene" varios DetalleItem.
     * Los datos llegan desde carrito/VentaDetalle.
     */
    public static class DetalleItem {
        private String descripcion;    // nombre/descripción del producto
        private int cantidad;          // cantidad vendida
        private double precioUnitario; // precio unitario
        private double subtotal;       // cantidad * precioUnitario

        public DetalleItem(String descripcion, int cantidad, double precioUnitario, double subtotal) {
            this.descripcion = descripcion;       // guarda descripción para View/PDF
            this.cantidad = cantidad;             // guarda cantidad para View/PDF
            this.precioUnitario = precioUnitario; // guarda precio unitario para View/PDF
            this.subtotal = subtotal;             // guarda subtotal para View/PDF
        }

        public String getDescripcion() { return descripcion; } // usado por View/PDF
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public int getCantidad() { return cantidad; } // usado por View/PDF
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public double getPrecioUnitario() { return precioUnitario; } // usado por View/PDF
        public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
        public double getSubtotal() { return subtotal; } // usado por View/PDF
        public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    }
}
