# POO y Patrones aplicados en SysVentas

Este documento resume **dónde se aplica Programación Orientada a Objetos (POO)** y el **patrón Singleton** en el proyecto.

## 1. Conceptos de POO aplicados

### Clase y Objeto
- **Clase**: plantilla que define atributos y métodos. Ejemplos: `Producto`, `Venta`, `Cliente`, `Usuario`, `ProductoController`, `VentaServiceImp`.
- **Objeto**: instancia creada con `new` o por el contenedor. Ejemplos: `new ProductoRepository()`, `new VentaServiceImp(...)`, objetos `Producto`/`Venta` cargados desde BD o formularios.
- **Ubicación**: todo el paquete `pe.edu.upeu.model` (entidades), `controller`, `service`, `repository`.

### Encapsulamiento
- Atributos privados/protegidos y acceso controlado por métodos (getters/setters, Lombok `@Data`, métodos de negocio).
- Ejemplos:
  - `SessionManager`: `userId`, `userPerfil`, `userName` privados; acceso por `get/set`.
  - `DatabaseConfig`: `dataSource` privado; acceso por `init()`, `getConnection()`.
  - `AppContext`: `contenedor` Map privado; acceso por `getBean()`/registro interno.
  - Controladores: campos `@FXML` privados.

### Abstracción
- Se exponen comportamientos esenciales y se ocultan detalles internos.
- Ejemplos:
  - `Conexion.obtenerConexion()` oculta HikariCP/driver/propiedades.
  - `ICrudGenericoService<T,ID>` y `IProductoService` definen qué se puede hacer sin exponer cómo.
  - `AbstractJpaRepository<T,ID>` abstrae operaciones JDBC comunes.

### Herencia
- Una clase hija reutiliza/extiende una clase base.
- Ejemplos:
  - `App extends Application` (JavaFX) — hereda comportamiento de aplicación gráfica.
  - `ProductoRepository extends AbstractJpaRepository<Producto, Long>`.
  - `CategoriaRepository`, `ClienteRepository`, `VentaRepository`, etc. extienden `AbstractJpaRepository`.
  - `ProductoServiceImp implements IProductoService` / servicios implementan interfaces CRUD genéricas.

### Polimorfismo
- **Sobrescritura (@Override)**: `App.start(...)`, métodos `insert/updateRow/mapRow/getTableName` en repositorios, métodos CRUD en servicios/controladores.
- **Polimorfismo por interfaz**: controladores dependen de `IProductoService`, `IVentaService`, etc.; en runtime se inyecta la implementación concreta (`ProductoServiceImp`, `VentaServiceImp`).
- **Sobrecarga**: constructores con distinta firma en `MainComprobanteController`.

### Interfaces
- Contratos de comportamiento: `IProductoService`, `IVentaService`, `ICrudGenericoService`, `IClienteService`, etc.
- Permiten desacoplar capas y facilitar pruebas/cambios de implementación.

### Genéricos
- `ICrudGenericoService<T, ID>` y `AbstractJpaRepository<T, ID>` reutilizan lógica para distintas entidades.

### Composición / Agregación / Inyección de dependencias
- `AppContext` compone el sistema creando e inyectando repositorios, servicios y controladores.
- Controladores reciben servicios por constructor (inyección por constructor).
- `Venta` se relaciona con `VentaDetalle`/Cliente/Usuario mediante IDs (agregación en modelo relacional).

### MVC (arquitectura aplicada con POO)
- **Modelo**: `model/*`, `dto/*`.
- **Vista**: `resources/view/*.fxml`, CSS.
- **Controlador**: `controller/*`.
- **Servicio**: lógica de negocio (`service/*`).
- **Repositorio**: acceso a datos (`repository/*`).

## 2. Patrón Singleton (dónde se aplica)

### 2.1 `SessionManager` (Singleton clásico)
- **Archivo**: `src/main/java/pe/edu/upeu/dto/SessionManager.java`
- **Cómo se aplica**:
  - `private static SessionManager instance;` — instancia única.
  - `private SessionManager()` — evita `new` externo.
  - `public static synchronized SessionManager getInstance()` — acceso global controlado.
- **Uso**: guarda usuario, id y perfil logueado. Usado en `LoginController`, `MainGuiController`, `VentaController`, `VendedorController`, `AdminController`, etc.

### 2.2 `AppContext` (Singleton + Contenedor IoC/DI simple)
- **Archivo**: `src/main/java/pe/edu/upeu/config/AppContext.java`
- **Cómo se aplica**:
  - `private static AppContext instance;`
  - Constructor privado `private AppContext()`.
  - `getInstance()` sincronizado.
- **Responsabilidad extra**: crea y registra beans (repositorios, servicios, controladores) y los entrega con `getBean(...)`.
- **Uso**: `StageManager`, `MainGuiController`, `VentaController`, paneles, login.

### 2.3 `DatabaseConfig` (recurso único compartido / Singleton de recurso)
- **Archivo**: `src/main/java/pe/edu/upeu/config/DatabaseConfig.java`
- **Cómo se aplica**:
  - `private static HikariDataSource dataSource;` — pool único.
  - `init()` sincronizado: si ya existe, reutiliza.
  - `shutdown()` cierra el recurso.
- **Uso**: `Conexion.obtenerConexion()` y pruebas/migraciones obtienen conexiones del mismo pool.

## 3. Otros patrones relacionados
- **Facade**: `Conexion` simplifica el acceso a la conexión.
- **Repository/DAO**: `repository/*` encapsula acceso JDBC.
- **Service Layer**: `service/*` contiene reglas de negocio.
- **Factory/ControllerFactory JavaFX**: `FXMLLoader.setControllerFactory(AppContext.getInstance()::getBean)`.
- **DTO**: `dto/*` para transportar datos (sesión, comprobante, combos).

## 4. Conexión entre capas (flujo POO)
`FXML (Vista)` → `Controller` → `Service (interfaz/impl)` → `Repository` → `Conexion/DatabaseConfig` → `BD`

Este flujo aplica encapsulamiento por capas, abstracción por interfaces, polimorfismo en implementaciones y reutilización por herencia/genéricos.
