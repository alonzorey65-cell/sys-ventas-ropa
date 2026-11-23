-- ============================================================
-- ARQUITECTURA SQLite - Sys.Ventas (equivalente lógica)
-- ============================================================
-- Nota: la BD activa del proyecto es PostgreSQL/Neon (schema_neon.sql).
-- Este archivo muestra la arquitectura equivalente en SQLite para
-- documentación/portabilidad académica.
--
-- Última actualización:
-- - Se alineó la documentación con los cambios recientes del comprobante:
--   el comprobante ahora muestra DNI o RUC según el documento del cliente
--   y también muestra la dirección del cliente.
-- - No se requirieron nuevas tablas ni columnas en BD porque el esquema
--   ya incluía cliente.dni_ruc y cliente.direccion.
-- - Se mantienen índices POS y relaciones clave para venta/comprobante.
--
-- Equivalencias usadas:
--   BIGSERIAL/IDENTITY -> INTEGER PRIMARY KEY AUTOINCREMENT
--   VARCHAR            -> TEXT
--   DOUBLE/DOUBLE PRECISION -> REAL
--   TIMESTAMP          -> TEXT (ISO-8601) / DATETIME
-- ============================================================

PRAGMA foreign_keys = ON;

-- Limpieza
DROP TABLE IF EXISTS venta_detalle;
DROP TABLE IF EXISTS venta;
DROP TABLE IF EXISTS vent_carrito;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS menu;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS unid_medida;
DROP TABLE IF EXISTS marca;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS perfil;

-- Catálogo de roles/perfiles
CREATE TABLE perfil (
    id_perfil INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL
);

-- Catálogo de categorías
CREATE TABLE categoria (
    id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL
);

-- Catálogo de marcas
CREATE TABLE marca (
    id_marca INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL
);

-- Catálogo de unidades de medida
CREATE TABLE unid_medida (
    id_unid_medida INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    abreviatura TEXT
);

-- Usuarios del sistema
CREATE TABLE usuario (
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    id_perfil INTEGER DEFAULT 1,
    FOREIGN KEY (id_perfil) REFERENCES perfil(id_perfil)
);

-- Menú dinámico por perfil
CREATE TABLE menu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    menu_nombre TEXT NOT NULL,
    menu_item_nombre TEXT,
    id_nombre_obj TEXT NOT NULL,
    perfil TEXT NOT NULL
);

-- Productos/inventario
CREATE TABLE producto (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    precio REAL NOT NULL DEFAULT 0,
    stock REAL NOT NULL DEFAULT 0,
    id_categoria INTEGER DEFAULT 1,
    id_marca INTEGER DEFAULT 1,
    id_unid_medida INTEGER DEFAULT 1,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    FOREIGN KEY (id_marca) REFERENCES marca(id_marca),
    FOREIGN KEY (id_unid_medida) REFERENCES unid_medida(id_unid_medida)
);

-- Clientes
-- dni_ruc: DNI (8) o RUC (11); el comprobante muestra la etiqueta según este valor.
-- direccion: se muestra en el comprobante (vista previa/PDF) junto con los datos del cliente.
CREATE TABLE cliente (
    id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,  -- Nombre o razón social
    dni_ruc TEXT,          -- Documento del cliente (DNI/RUC)
    direccion TEXT,        -- Dirección del cliente (usada en comprobante)
    telefono TEXT          -- Teléfono de contacto
);

-- Carrito temporal POS
CREATE TABLE vent_carrito (
    id_vent_carrito INTEGER PRIMARY KEY AUTOINCREMENT,
    id_producto INTEGER NOT NULL,
    nombre_producto TEXT NOT NULL,
    cantidad REAL NOT NULL,
    p_buffer REAL NOT NULL,
    subtotal REAL NOT NULL,
    id_usuario INTEGER,
    FOREIGN KEY (id_producto) REFERENCES producto(id),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Cabecera de venta/comprobante
CREATE TABLE venta (
    id_venta INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha TEXT NOT NULL,
    id_cliente INTEGER,
    id_usuario INTEGER,
    total REAL NOT NULL,
    tipo_comprobante TEXT,
    serie TEXT,
    nro_comprobante TEXT,
    estado TEXT DEFAULT 'ACTIVO',
    motivo_anulacion TEXT,
    fecha_anulacion TEXT,
    usuario_anulacion INTEGER,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Detalle de venta
CREATE TABLE venta_detalle (
    id_venta_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
    id_venta INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    cantidad REAL NOT NULL,
    precio REAL NOT NULL,
    subtotal REAL NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id)
);

-- Índices útiles para POS
CREATE INDEX IF NOT EXISTS idx_producto_nombre ON producto(nombre);
CREATE INDEX IF NOT EXISTS idx_cliente_dni ON cliente(dni_ruc);

-- Datos iniciales mínimos
INSERT INTO perfil (nombre) VALUES ('ADMINISTRADOR');
INSERT INTO perfil (nombre) VALUES ('VENDEDOR');

INSERT INTO categoria (nombre) VALUES ('General');
INSERT INTO marca (nombre) VALUES ('Genérica');
INSERT INTO unid_medida (nombre, abreviatura) VALUES ('Unidad', 'UND');

INSERT INTO usuario (username, password, id_perfil) VALUES ('admin', '123', 1);
INSERT INTO usuario (username, password, id_perfil) VALUES ('vendedor', '123', 2);

INSERT INTO cliente (nombre, dni_ruc, direccion, telefono) VALUES ('Público General', '00000000', 'S/D', '');
