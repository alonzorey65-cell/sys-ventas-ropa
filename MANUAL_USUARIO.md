# Manual de Usuario - SysVentas

## 1. Presentación
**SysVentas** es un sistema de ventas de escritorio desarrollado en **JavaFX**. Permite gestionar productos, registrar ventas (POS), generar comprobantes y controlar el acceso por **roles/perfiles**.

Este manual está diseñado de forma didáctica para que cualquier usuario pueda aprender a usar el sistema paso a paso.

---

## 2. Requisitos para ejecutar el sistema

### Opción A: Ejecutar el archivo JAR
1. Tener instalado **Java 21** o superior.
2. Ubicar el archivo ejecutable: `SysVentas-1.0-SNAPSHOT-all.jar`.
3. Doble clic sobre el JAR, o ejecutar en consola:

```bash
java -jar SysVentas-1.0-SNAPSHOT-all.jar
```

### Opción B: Ejecutar el archivo EXE (Windows)
1. Requiere generar el instalador con JDK que incluya `jpackage` y WiX Toolset (para `.exe`).
2. Ubicar el instalador/ejecutable generado en `target/installer/`.
3. Doble clic en `SysVentas.exe` o ejecutar el instalador.
4. Si no dispone de WiX/jpackage, use la opción JAR ejecutable.

### Opción C: Ejecutar desde código fuente (desarrollador)
```bash
mvn clean javafx:run
```

---

## 3. Roles / Perfiles del sistema

El sistema maneja acceso por perfiles. El menú y las funcionalidades visibles dependen del rol del usuario.

| Rol / Perfil | Usuario de prueba | Contraseña | Funcionalidades principales |
|---|---|---:|---|
| **Administrador (Perfil 1)** | `admin` | `123` | Gestión de productos, registro de ventas, comprobantes, reportes y cierre de sesión. |
| **Vendedor (Perfil 2)** | `vendedor` | `123` | Registro de ventas, consulta/generación de comprobantes y cierre de sesión. |

> **Nota:** El Administrador tiene control completo del catálogo. El Vendedor se enfoca en la operación de venta.

---

## 4. Inicio de sesión

1. Abra el sistema SysVentas.
2. En la pantalla **Login SysVentas**, ingrese su **Usuario**.
3. Ingrese su **Clave**.
4. Presione el botón **Ingresar**.
5. Si los datos son correctos, el sistema mostrará un mensaje de bienvenida y abrirá la ventana principal.
6. Si falta un campo o las credenciales son incorrectas, el sistema mostrará un mensaje de validación.

**Botón Cerrar:** finaliza la aplicación.

---

## 5. Pantalla principal

Después de iniciar sesión, verá:
- **Barra de menú:** contiene las opciones disponibles según su rol.
- **Pestañas:** cada módulo se abre en una pestaña.
- **Cambio de estilo/idioma:** permite personalizar la apariencia.

### Menús según rol

#### Administrador
- Almacén / Productos
- Ventas / Registrar Venta POS
- Reportes / Comprobantes
- Sesión / Cerrar Sesión

#### Vendedor
- Ventas / Registrar Venta POS
- Reportes / Comprobantes
- Sesión / Cerrar Sesión

---

## 6. Módulo de Productos (Administrador)

**Ruta:** Menú Almacén → Producto

### 6.1 Registrar un producto
1. Complete el formulario:
   - **Nombre:** nombre del producto (obligatorio, mínimo 3 caracteres).
   - **Precio:** valor numérico mayor a 0.
   - **Stock:** cantidad numérica mayor o igual a 0.
   - **Categoría, Marca, Unidad:** seleccione una opción.
2. Presione **Guardar**.
3. El sistema mostrará un mensaje: "Producto registrado correctamente".

### 6.2 Editar un producto
1. En la tabla, haga **doble clic** sobre el producto.
2. Los datos se cargarán en el formulario.
3. Modifique los campos necesarios.
4. Presione **Guardar**.
5. El sistema mostrará: "Producto actualizado correctamente".

### 6.3 Eliminar un producto
1. Seleccione el producto con doble clic.
2. Presione **Eliminar**.
3. Confirme la acción en el mensaje.
4. El sistema mostrará el resultado de la eliminación.

### 6.4 Buscar / filtrar productos
- Escriba en el campo **Filtrar Producto** para buscar por nombre.
- Presione **Refrescar** para recargar la lista.

### Validaciones del formulario de producto
- No permite guardar sin nombre, precio o stock.
- Precio debe ser numérico y mayor a 0.
- Stock debe ser numérico y no negativo.
- Muestra mensajes de éxito o error en pantalla.

---

## 7. Módulo de Ventas / POS (Administrador y Vendedor)

**Ruta:** Menú Ventas → Registrar Venta / POS

### 7.1 Buscar o registrar cliente
1. Ingrese DNI (8 dígitos) o RUC (11 dígitos).
2. Presione **Buscar** o Enter.
3. Si el cliente existe, se cargan sus datos.
4. Si no existe, complete Nombre/Razón Social y Dirección.
5. Presione **Add** para guardar el cliente.
6. El sistema valida DNI/RUC y nombre antes de guardar.

### 7.2 Agregar productos al carrito
1. Busque el producto por nombre o ID.
2. Verifique el producto seleccionado, stock, cantidad y precio unitario.
3. Ingrese la **Cantidad**.
4. Presione **Add** para agregar al carrito.
5. El sistema valida:
   - Producto seleccionado.
   - Cantidad válida.
   - Stock suficiente.

### 7.3 Revisar totales
- El sistema calcula automáticamente:
  - **Base imponible**
  - **IGV**
  - **Descuento**
  - **Importe total S/**

### 7.4 Registrar venta
1. Verifique cliente y productos del carrito.
2. Presione **Registrar Venta**.
3. El sistema guarda la venta, guarda el detalle, actualiza stock y genera comprobante.
4. Se muestra un mensaje de confirmación.

### 7.5 Generar/visualizar comprobante
- Presione **Comprobante** para ver la vista previa.
- Después de registrar una venta, el comprobante se muestra automáticamente.

---

## 8. Módulo de Comprobantes (Administrador y Vendedor)

**Ruta:** Menú Reportes → Comprobantes

### Funciones disponibles
- **Refrescar:** recarga las ventas registradas.
- **Ver comprobante:** muestra la vista previa de la venta seleccionada.
- **Eliminar venta:** elimina la venta seleccionada y sus detalles (solicita confirmación).
- **Exportar PDF:** guarda el comprobante en PDF.
- **Imprimir:** envía el comprobante a la impresora.

### Recomendación
Antes de eliminar una venta, verifique que sea la correcta. La eliminación solicita confirmación.

---

## 9. Mensajes y validaciones

El sistema muestra mensajes para ayudar al usuario:
- **Mensajes verdes:** acción realizada correctamente.
- **Mensajes rojos:** error, dato faltante o validación no cumplida.
- **Campos resaltados:** indican qué dato debe corregirse.

Ejemplos:
- "Ingrese su usuario."
- "Credenciales incorrectas. Verifique usuario y contraseña."
- "Producto registrado correctamente."
- "DNI debe tener 8 dígitos o RUC 11 dígitos."
- "Stock insuficiente..."

---

## 10. Cerrar sesión / salir

- **Cerrar Sesión:** desde el menú Sesión, regresa a la pantalla de login.
- **Cerrar:** desde el login o ventana, cierra la aplicación.

---

## 11. Empaquetado del sistema

### Generar JAR ejecutable
```bash
mvn clean package
```
Archivo generado:
- `target/SysVentas-1.0-SNAPSHOT-all.jar`

### Generar EXE (Windows, requiere JDK con jpackage)
```bash
mvn clean package
mvn -Pexe jpackage:jpackage
```
Salida:
- `target/installer/`

> Si no se cuenta con WiX Toolset o configuración de jpackage, se puede entregar el JAR ejecutable como alternativa válida.

---

## 12. Solución de problemas comunes

| Problema | Posible causa | Solución |
|---|---|---|
| No abre el JAR | Java no instalado o versión antigua | Instalar Java 21+ y ejecutar con `java -jar ...` |
| No inicia sesión | Usuario/clave incorrectos | Usar credenciales de prueba o verificar datos |
| No guarda producto | Campos inválidos | Revisar nombre, precio > 0 y stock >= 0 |
| No registra venta | Carrito vacío o cliente inválido | Agregar productos y seleccionar/registrar cliente |
| No muestra comprobante | No hay venta seleccionada | Seleccionar una venta o registrar una nueva |

---

## 13. Créditos
Proyecto académico **SysVentas** - JavaFX, Maven, arquitectura por capas (Repositorio, Servicio, Controlador).
