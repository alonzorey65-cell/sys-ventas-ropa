
package pe.edu.upeu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * POO - CLASE, OBJETO, ENCAPSULAMIENTO, ABSTRACCIÓN, MÉTODOS:
 * - CLASE: Cliente es una plantilla que modela a un cliente del negocio.
 * - OBJETO: cada cliente registrado en memoria es una instancia (objeto) de esta clase.
 * - ENCAPSULAMIENTO: atributos private y acceso controlado por getters/setters (Lombok @Data).
 * - ABSTRACCIÓN: representa solo datos necesarios del cliente (dni/ruc, nombre, dirección...).
 * - MÉTODOS: constructores, getters/setters, builder, etc. generados automáticamente.
 * CONEXIÓN: objetos Cliente se usan en VentaController/servicios/repositorios y se persisten en Neon.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cliente { // CLASE de dominio (entidad Cliente)
    private Long idCliente;
    private String nombre;
    private String dniRuc;  // <-- Esto genera automáticamente getDniRuc() y setDniRuc()
    private String direccion;
    private String telefono;
}