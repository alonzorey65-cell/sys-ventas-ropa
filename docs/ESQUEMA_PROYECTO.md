# Esquema de base de datos – Sys.Ventas

Este directorio contiene el esquema del proyecto en formato **SQL Power Architect**:

- **`SysVentas.architect`**: proyecto lógico con tablas, columnas, claves primarias y relaciones inferidas a partir del código y los scripts SQL del repositorio.

## Origen de la información

Se consolidó a partir de:

- Scripts SQL: `src/main/resources/schema.sql`, `schema_neon.sql`, `schema_sv.sql`, `crear.sql`, `sql_merge.sql`
- Modelos Java: `src/main/java/pe/edu/upeu/model/*.java`

## Tablas incluidas

| Tabla | Descripción | Origen principal |
|---|---|---|
| `usuario` | Usuarios del sistema (login/perfil) | `schema.sql`, `Usuario` |
| `perfil` | Perfiles/roles | Modelo `Perfil` (referenciado por `usuario.id_perfil`) |
| `menu` | Opciones de menú/accesos por perfil | `schema.sql`, modelo `Acceso` |
| `categoria` | Categorías de producto | Modelo `Categoria` (referenciado por `producto`) |
| `marca` | Marcas de producto | Modelo `Marca` |
| `unidad_medida` | Unidades de medida | Modelo `UnidadMedida` |
| `producto` | Catálogo/inventario | `schema.sql`, `Producto` |
| `cliente` | Clientes | `schema.sql`, `Cliente` |
| `vent_carrito` | Ítems temporales del carrito/POS | `schema.sql`, `VentCarrito` |
| `venta` | Cabecera de venta/comprobante | `schema.sql`, `Venta` |
| `venta_detalle` | Detalle de venta | `schema.sql`, `VentaDetalle` |
| `emisor` | Datos del emisor/comprobante | Modelo `Emisor` |
| `documento_comercial` | Series y numeración de documentos | Modelo `DocumentoComercial` |

## Relaciones modeladas

- `usuario.id_perfil` → `perfil.id_perfil`
- `producto.id_categoria` → `categoria.id_categoria`
- `producto.id_marca` → `marca.id_marca`
- `producto.id_unid_medida` → `unidad_medida.id_unid_medida`
- `vent_carrito.id_producto` → `producto.id`
- `vent_carrito.id_usuario` → `usuario.id_usuario`
- `venta.id_cliente` → `cliente.id_cliente`
- `venta.id_usuario` → `usuario.id_usuario`
- `venta_detalle.id_venta` → `venta.id_venta` (identificada en el diagrama; en `schema.sql` con `ON DELETE CASCADE`)
- `venta_detalle.id_producto` → `producto.id`

## Notas

- El proyecto tiene **variantes de esquema** (`schema.sql` H2, `schema_neon.sql` PostgreSQL/Neon, `crear.sql` con estructura alternativa). El archivo `.architect` representa un **esquema lógico unificado** basado en los modelos y el esquema funcional principal.
- Algunas tablas (`perfil`, `categoria`, `marca`, `unidad_medida`, `emisor`, `documento_comercial`) están presentes como modelos/clases pero no tienen `CREATE TABLE` explícito en los scripts revisados; se incluyen para reflejar la estructura completa del dominio del proyecto.
- Índices definidos en scripts: `idx_producto_nombre` sobre `producto(nombre)` y `idx_cliente_dni` sobre `cliente(dni_ruc)` (`schema_sv.sql`).
- Para ajustar tipos/longitudes exactos al motor (H2/PostgreSQL), edita el archivo `.architect` en SQL Power Architect y regenera el DDL según el destino.
