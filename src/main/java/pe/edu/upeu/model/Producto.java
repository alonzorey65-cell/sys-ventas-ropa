package pe.edu.upeu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * ========================= POO (Programación Orientada a Objetos) =========================
 * CLASE: "Producto" es una CLASE, es decir, una plantilla/molde que define la estructura
 *        y características de un producto del sistema.
 * OBJETO: cuando se crea una instancia, por ejemplo:
 *         Producto p = new Producto();  o  Producto.builder()...build();
 *         esa variable "p" referencia un OBJETO (instancia concreta de la clase).
 * ENCAPSULAMIENTO: los atributos son "private" (idProducto, nombre, precio, etc.), por lo que
 *        no se accede directamente desde otras clases; se accede mediante métodos públicos
 *        getters/setters (generados por Lombok @Data), controlando el acceso a los datos.
 * ABSTRACCIÓN: esta clase representa el concepto real "Producto" del negocio, mostrando solo
 *        los datos relevantes para el sistema (nombre, precio, stock...) y ocultando detalles
 *        internos de almacenamiento/implementación.
 * CONEXIÓN EN EL PROYECTO:
 *        - Vista/Controlador (ProductoController, FXML) crean/usan objetos Producto.
 *        - Servicio (IProductoService/ProductoServiceImp) aplica reglas y orquesta operaciones.
 *        - Repositorio (ProductoRepository) convierte objetos Producto <-> registros en BD (Neon).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Producto { // CLASE (modelo/entidad del dominio)
    // ENCAPSULAMIENTO: atributos privados, accesibles vía getters/setters
    private Long idProducto;
    private String nombre;
    private Double precio;
    private Double stock;
    private Long idCategoria;
    private Long idMarca;
    private Long idUnidMedida;

    // MÉTODOS: Lombok genera constructores, getters, setters, toString, equals, hashCode, builder.
    // Ejemplos de uso (métodos):
    //   producto.getNombre();  producto.setPrecio(10.0);  Producto.builder().nombre("X").build();
}