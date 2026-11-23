# SysVentas

Sistema de ventas de escritorio desarrollado con **Java 21 + JavaFX + Maven**, con arquitectura por capas (**Repository, Service, Controller**).

## Cumplimiento de rúbrica

1. **Estructura avanzada (Repositorio, Servicio y Controllers)**
   - `src/main/java/pe/edu/upeu/repository`: acceso a datos (JPA/JDBC).
   - `src/main/java/pe/edu/upeu/service` e `impl`: lógica de negocio.
   - `src/main/java/pe/edu/upeu/controller`: controladores JavaFX.
   - `src/main/java/pe/edu/upeu/model`, `dto`, `config`, `components`: soporte de dominio e infraestructura.

2. **Manual de Usuario (didáctico, por roles/perfiles)**
   - Ver [`MANUAL_USUARIO.md`](./MANUAL_USUARIO.md).
   - Incluye roles **Administrador** y **Vendedor**, pasos de uso, validaciones y solución de problemas.

3. **Empaquetado en .exe o .jar**
   - **JAR ejecutable (fat JAR):**
     ```bash
     mvn clean package
     java -jar target/SysVentas-1.0-SNAPSHOT-all.jar
     ```
   - **EXE (Windows, requiere JDK con jpackage):**
     ```bash
     mvn clean package
     mvn -Pexe jpackage:jpackage
     ```
     Salida: `target/installer/`

4. **Interfaz gráfica en JavaFX**
   - Vistas FXML en `src/main/resources/view`.
   - Login, gestión de productos, ventas/POS y comprobantes.

5. **Validación de campos y mensajes de acción**
   - Validaciones en Login, Productos y Ventas (campos obligatorios, formatos, rangos, stock).
   - Mensajes de éxito/error mediante Toast/Alert y resaltado de campos inválidos.

## Configuración

- Por defecto usa **H2 local** (`data/sysventas`) con `schema.sql`.
- Para PostgreSQL/Neon, use `application-example.properties` como base y configure variables de entorno: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER`, `DB_DDL_SCRIPT`, `APISPERU_TOKEN`.
- `src/main/resources/application.properties` está saneado (sin credenciales) y se puede mantener localmente.

## Ejecución en desarrollo

```bash
mvn clean javafx:run
```

## Usuarios de prueba

| Rol | Usuario | Contraseña |
|---|---|---|
| Administrador | `admin` | `123` |
| Vendedor | `vendedor` | `123` |
