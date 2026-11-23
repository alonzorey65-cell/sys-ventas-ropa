-- ============================================================
-- ESQUEMA DE BASE DE DATOS - Sys.Ventas (PostgreSQL / Neon)
-- ============================================================
-- Este archivo define la estructura completa de la base de datos
-- del sistema de ventas de escritorio "Sys.Ventas".
--
-- ¿Qué hace esta base de datos en general?
-- - Guarda la configuración base del negocio: perfiles, categorías,
--   marcas y unidades de medida.
-- - Controla el acceso al sistema mediante usuarios y perfiles.
-- - Define el menú dinámico que ve cada perfil en la aplicación.
-- - Administra el inventario de productos (precio, stock y catálogos).
-- - Registra clientes con su documento (DNI/RUC), dirección y teléfono.
-- - Soporta el carrito/POS temporal antes de confirmar una venta.
-- - Registra ventas (cabecera/comprobante) y su detalle (ítems vendidos).
-- - Conserva estado de venta (ACTIVO/ANULADO) y datos de anulación.
--
-- Nota: en application.properties se usa db.ddl.script=schema_neon.sql.
-- Si db.ddl.auto=false, este script no se ejecuta automáticamente y
-- los datos existentes se conservan.
-- ============================================================

-- ------------------------------------------------------------
-- LIMPIEZA DE TABLAS (reinicio del esquema)
-- ------------------------------------------------------------
-- Elimina tablas si existen para poder recrearlas desde cero.
-- CASCADE elimina también dependencias relacionadas.
-- El orden importa: primero tablas hijas/dependientes, luego padres.
DROP TABLE IF EXISTS venta_detalle CASCADE; -- Detalle de ventas (depende de venta)
DROP TABLE IF EXISTS venta CASCADE;         -- Cabecera de ventas/comprobantes
DROP TABLE IF EXISTS vent_carrito CASCADE;  -- Carrito temporal del POS
DROP TABLE IF EXISTS cliente CASCADE;       -- Clientes/compradores
DROP TABLE IF EXISTS producto CASCADE;      -- Inventario de productos
DROP TABLE IF EXISTS menu CASCADE;          -- Menús dinámicos por perfil
DROP TABLE IF EXISTS usuario CASCADE;       -- Usuarios del sistema
DROP TABLE IF EXISTS unid_medida CASCADE;   -- Catálogo de unidades de medida
DROP TABLE IF EXISTS marca CASCADE;         -- Catálogo de marcas
DROP TABLE IF EXISTS categoria CASCADE;     -- Catálogo de categorías
DROP TABLE IF EXISTS perfil CASCADE;        -- Catálogo de perfiles/roles

-- ------------------------------------------------------------
-- TABLA: perfil
-- ------------------------------------------------------------
-- Catálogo de roles/perfiles del sistema.
-- Se usa para clasificar usuarios (ej. ADMINISTRADOR, VENDEDOR).
CREATE TABLE perfil (
    id_perfil BIGSERIAL PRIMARY KEY, -- ID autoincremental del perfil
    nombre VARCHAR(50) NOT NULL      -- Nombre del perfil/rol
);

-- ------------------------------------------------------------
-- TABLA: categoria
-- ------------------------------------------------------------
-- Catálogo de categorías de producto (ej. General, Ropa Hombre).
-- Permite clasificar productos en el inventario.
CREATE TABLE categoria (
    id_categoria BIGSERIAL PRIMARY KEY, -- ID autoincremental de categoría
    nombre VARCHAR(100) NOT NULL         -- Nombre de la categoría
);

-- ------------------------------------------------------------
-- TABLA: marca
-- ------------------------------------------------------------
-- Catálogo de marcas de producto (ej. Genérica, Nike, Adidas).
CREATE TABLE marca (
    id_marca BIGSERIAL PRIMARY KEY, -- ID autoincremental de marca
    nombre VARCHAR(100) NOT NULL     -- Nombre de la marca
);

-- ------------------------------------------------------------
-- TABLA: unid_medida
-- ------------------------------------------------------------
-- Catálogo de unidades de medida para productos (UND, CJ, PQT, etc.).
CREATE TABLE unid_medida (
    id_unid_medida BIGSERIAL PRIMARY KEY, -- ID autoincremental de unidad
    nombre VARCHAR(100) NOT NULL,         -- Nombre completo (Unidad, Caja...)
    abreviatura VARCHAR(20)               -- Abreviatura visible (UND, CJ...)
);

-- ------------------------------------------------------------
-- TABLA: usuario
-- ------------------------------------------------------------
-- Usuarios que pueden iniciar sesión en el sistema.
-- El campo id_perfil indica el rol del usuario (1=ADMIN, 2=VENDEDOR
-- según los datos iniciales).
CREATE TABLE usuario (
    id_usuario BIGSERIAL PRIMARY KEY,      -- ID autoincremental del usuario
    username VARCHAR(50) NOT NULL UNIQUE,  -- Nombre de usuario para login (único)
    password VARCHAR(100) NOT NULL,        -- Contraseña (en este esquema base se guarda simple)
    id_perfil BIGINT DEFAULT 1             -- Perfil/rol asociado (por defecto 1)
);

-- ------------------------------------------------------------
-- TABLA: menu
-- ------------------------------------------------------------
-- Define el menú dinámico de la aplicación según el perfil.
-- La app lee estos registros para mostrar opciones disponibles.
CREATE TABLE menu (
    id BIGSERIAL PRIMARY KEY,          -- ID autoincremental del ítem de menú
    menu_nombre VARCHAR(50) NOT NULL,  -- Nombre del grupo/menú (Almacén, Ventas...)
    menu_item_nombre VARCHAR(50),      -- Nombre de la opción visible
    id_nombre_obj VARCHAR(50) NOT NULL,-- Identificador interno usado por la app para abrir la vista/acción
    perfil VARCHAR(30) NOT NULL        -- Perfil al que pertenece el acceso (se guarda como texto: '1', '2')
);

-- ------------------------------------------------------------
-- TABLA: producto
-- ------------------------------------------------------------
-- Inventario de productos disponibles para la venta.
-- Guarda precio, stock y referencias a catálogos.
CREATE TABLE producto (
    id BIGSERIAL PRIMARY KEY,                       -- ID autoincremental del producto
    nombre VARCHAR(100) NOT NULL,                   -- Nombre/descripcion del producto
    precio DOUBLE PRECISION NOT NULL DEFAULT 0,     -- Precio unitario de venta
    stock DOUBLE PRECISION NOT NULL DEFAULT 0,      -- Stock disponible actual
    id_categoria BIGINT DEFAULT 1,                  -- FK lógica a categoria.id_categoria
    id_marca BIGINT DEFAULT 1,                      -- FK lógica a marca.id_marca
    id_unid_medida BIGINT DEFAULT 1                 -- FK lógica a unid_medida.id_unid_medida
);

-- ------------------------------------------------------------
-- TABLA: cliente
-- ------------------------------------------------------------
-- Registro de clientes/compradores.
-- dni_ruc guarda DNI (8 dígitos) o RUC (11 dígitos).
CREATE TABLE cliente (
    id_cliente BIGSERIAL PRIMARY KEY, -- ID autoincremental del cliente
    nombre VARCHAR(100) NOT NULL,     -- Nombre o razón social
    dni_ruc VARCHAR(20),              -- Documento del cliente: DNI o RUC
    direccion VARCHAR(150),           -- Dirección del cliente
    telefono VARCHAR(30)              -- Teléfono de contacto
);

-- ------------------------------------------------------------
-- TABLA: vent_carrito
-- ------------------------------------------------------------
-- Carrito temporal del POS (punto de venta).
-- Permite almacenar ítems antes de confirmar/registrar la venta.
-- p_buffer representa el precio unitario capturado en el momento.
CREATE TABLE vent_carrito (
    id_vent_carrito BIGSERIAL PRIMARY KEY,        -- ID autoincremental del ítem en carrito
    id_producto BIGINT NOT NULL,                  -- Producto agregado al carrito
    nombre_producto VARCHAR(150) NOT NULL,        -- Nombre del producto (snapshot en carrito)
    cantidad DOUBLE PRECISION NOT NULL,           -- Cantidad seleccionada
    p_buffer DOUBLE PRECISION NOT NULL,           -- Precio unitario en el momento de agregar
    subtotal DOUBLE PRECISION NOT NULL,           -- cantidad * p_buffer
    id_usuario BIGINT                             -- Usuario que maneja el carrito/POS
);

-- ------------------------------------------------------------
-- TABLA: venta
-- ------------------------------------------------------------
-- Cabecera de la venta/comprobante.
-- Representa una venta realizada, su total, comprobante y estado.
-- El estado puede ser ACTIVO o ANULADO; si se anula, se guardan
-- motivo, fecha y usuario de anulación.
CREATE TABLE venta (
    id_venta BIGSERIAL PRIMARY KEY,           -- ID autoincremental de la venta
    fecha TIMESTAMP NOT NULL,                 -- Fecha y hora de la venta
    id_cliente BIGINT,                        -- Cliente asociado (referencia a cliente.id_cliente)
    id_usuario BIGINT,                        -- Usuario que registró la venta
    total DOUBLE PRECISION NOT NULL,          -- Total final pagado/registrado
    tipo_comprobante VARCHAR(30),             -- Tipo de comprobante (ej. BOLETA)
    serie VARCHAR(10),                        -- Serie del comprobante (ej. B001)
    nro_comprobante VARCHAR(30),              -- Número/correlativo del comprobante
    estado VARCHAR(20) DEFAULT 'ACTIVO',      -- Estado de la venta: ACTIVO o ANULADO
    motivo_anulacion VARCHAR(255),            -- Motivo si la venta fue anulada
    fecha_anulacion TIMESTAMP,                -- Fecha/hora de anulación
    usuario_anulacion BIGINT                  -- Usuario que realizó la anulación
);

-- ------------------------------------------------------------
-- TABLA: venta_detalle
-- ------------------------------------------------------------
-- Detalle/ítems de cada venta.
-- Cada fila representa un producto vendido dentro de una venta.
-- Tiene FK real a venta con ON DELETE CASCADE: si se elimina la venta,
-- sus detalles se eliminan automáticamente.
CREATE TABLE venta_detalle (
    id_venta_detalle BIGSERIAL PRIMARY KEY,      -- ID autoincremental del detalle
    id_venta BIGINT NOT NULL,                    -- FK a venta.id_venta
    id_producto BIGINT NOT NULL,                 -- Producto vendido (referencia a producto.id)
    cantidad DOUBLE PRECISION NOT NULL,          -- Cantidad vendida
    precio DOUBLE PRECISION NOT NULL,            -- Precio unitario al momento de venta
    subtotal DOUBLE PRECISION NOT NULL,          -- cantidad * precio
    CONSTRAINT fk_venta_detalle_venta FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE
);

-- ============================================================
-- DATOS INICIALES (SEED DATA)
-- ============================================================
-- Estos datos permiten que el sistema arranque con configuración mínima.

-- Perfiles/roles base del sistema.
INSERT INTO perfil (nombre) VALUES ('ADMINISTRADOR'); -- Perfil 1: acceso completo
INSERT INTO perfil (nombre) VALUES ('VENDEDOR');      -- Perfil 2: acceso de ventas/comprobantes

-- Categorías iniciales para clasificar productos.
INSERT INTO categoria (nombre) VALUES ('General');
INSERT INTO categoria (nombre) VALUES ('Ropa Hombre');
INSERT INTO categoria (nombre) VALUES ('Ropa Mujer');

-- Marcas iniciales.
INSERT INTO marca (nombre) VALUES ('Genérica');
INSERT INTO marca (nombre) VALUES ('Nike');
INSERT INTO marca (nombre) VALUES ('Adidas');

-- Unidades de medida iniciales.
INSERT INTO unid_medida (nombre, abreviatura) VALUES ('Unidad', 'UND');
INSERT INTO unid_medida (nombre, abreviatura) VALUES ('Caja', 'CJ');
INSERT INTO unid_medida (nombre, abreviatura) VALUES ('Paquete', 'PQT');

-- Usuarios iniciales para pruebas/acceso.
-- IMPORTANTE: contraseñas en texto plano solo para entorno de desarrollo/demo.
INSERT INTO usuario (username, password, id_perfil) VALUES ('admin', '123', 1);    -- Admin
INSERT INTO usuario (username, password, id_perfil) VALUES ('vendedor', '123', 2); -- Vendedor

-- ------------------------------------------------------------
-- Menús por perfil
-- ------------------------------------------------------------
-- id_nombre_obj es el identificador interno que la aplicación usa
-- para saber qué vista/acción abrir.

-- Menús ADMINISTRADOR (perfil '1')
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Inicio', 'Panel Administrador', '11', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Almacén', 'Producto', '1', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Almacén', 'Categoría', '2', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Almacén', 'Marca', '6', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Almacén', 'Unidad Medida', '7', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Ventas', 'Registrar Venta / POS', '3', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Ventas', 'Clientes', '8', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Reportes', 'Comprobantes', '4', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Administración', 'Usuarios', '9', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Sesión', 'Cerrar Sesión', '5', '1');

-- Menús VENDEDOR (perfil '2')
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Inicio', 'Panel Vendedor', '10', '2');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Ventas', 'Registrar Venta / POS', '3', '2');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Reportes', 'Comprobantes', '4', '2');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Sesión', 'Cerrar Sesión', '5', '2');

-- Productos de ejemplo para probar el POS/inventario.
INSERT INTO producto (nombre, precio, stock, id_categoria, id_marca, id_unid_medida) VALUES
('Polo Casual Oversize Algodón', 45.00, 25, 1, 1, 1),
('Pantalón Jean Slim Fit Hombre', 89.90, 15, 1, 1, 1),
('Camisa Formal Slim Fit', 65.00, 20, 1, 1, 1);

-- Cliente por defecto usado cuando no se identifica uno específico.
INSERT INTO cliente (nombre, dni_ruc, direccion, telefono) VALUES
('Público General', '00000000', 'S/D', '');
