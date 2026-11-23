-- ============================================================
-- ESQUEMA SIMPLIFICADO / LEGACY - Sys.Ventas (H2/MySQL-like)
-- ============================================================
-- Este archivo parece una versión anterior o simplificada del esquema.
-- NO es el esquema principal usado por la configuración actual
-- (application.properties apunta a schema_neon.sql para PostgreSQL/Neon).
--
-- Se conserva comentado para entender la evolución del modelo.
-- Diferencias principales frente al esquema actual:
-- - cliente usa dni_ruc como PRIMARY KEY (en el actual usa id_cliente).
-- - venta usa cliente_dni_ruc como FK a cliente(dni_ruc).
-- - venta_detalle guarda descripcion directamente (no id_producto).
-- - No incluye tablas de catálogos: perfil, categoria, marca, unid_medida.
-- - No incluye vent_carrito ni campos de anulación en venta.
-- ============================================================

-- ------------------------------------------------------------
-- LIMPIEZA: elimina tablas en orden inverso por dependencias FK.
-- ------------------------------------------------------------
DROP TABLE IF EXISTS venta_detalle; -- Detalle depende de venta
DROP TABLE IF EXISTS venta;         -- Venta depende de cliente
DROP TABLE IF EXISTS cliente;       -- Cliente
DROP TABLE IF EXISTS producto;      -- Producto
DROP TABLE IF EXISTS usuario;       -- Usuario

-- ------------------------------------------------------------
-- TABLA: usuario
-- ------------------------------------------------------------
-- Usuarios para login básico del sistema.
CREATE TABLE usuario (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY, -- ID autoincremental
                         username VARCHAR(50) NOT NULL UNIQUE, -- Usuario único
                         password VARCHAR(100) NOT NULL,       -- Contraseña
                         rol VARCHAR(30) NOT NULL              -- Rol como texto (ej. ADMINISTRADOR)
);

-- ------------------------------------------------------------
-- TABLA: producto
-- ------------------------------------------------------------
-- Inventario simple de productos.
CREATE TABLE producto (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY, -- ID autoincremental
                          nombre VARCHAR(100) NOT NULL,         -- Nombre del producto
                          categoria VARCHAR(50),                -- Categoría como texto (sin tabla catálogo)
                          precio DOUBLE NOT NULL,               -- Precio unitario
                          stock INT NOT NULL                    -- Stock disponible
);

-- ------------------------------------------------------------
-- TABLA: cliente
-- ------------------------------------------------------------
-- Clientes identificados por DNI/RUC como clave primaria.
CREATE TABLE cliente (
                         dni_ruc VARCHAR(15) PRIMARY KEY, -- DNI/RUC como PK
                         nombre VARCHAR(100) NOT NULL,    -- Nombre
                         apellido_paterno VARCHAR(50),    -- Apellido paterno
                         apellido_materno VARCHAR(50),    -- Apellido materno
                         direccion VARCHAR(150)           -- Dirección
);

-- ------------------------------------------------------------
-- TABLA: venta
-- ------------------------------------------------------------
-- Cabecera del comprobante/venta.
CREATE TABLE venta (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,                    -- ID autoincremental
                       serie VARCHAR(10) NOT NULL,                              -- Serie comprobante
                       numero VARCHAR(20) NOT NULL,                             -- Número comprobante
                       fecha VARCHAR(30) NOT NULL,                              -- Fecha (como texto en este esquema legacy)
                       cliente_dni_ruc VARCHAR(15),                             -- FK al cliente por DNI/RUC
                       total DOUBLE NOT NULL,                                   -- Total de venta
                       CONSTRAINT fk_cliente FOREIGN KEY (cliente_dni_ruc) REFERENCES cliente(dni_ruc)
);

-- ------------------------------------------------------------
-- TABLA: venta_detalle
-- ------------------------------------------------------------
-- Ítems del comprobante; guarda descripción textual del producto.
CREATE TABLE venta_detalle (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,              -- ID autoincremental
                               venta_id BIGINT NOT NULL,                          -- FK a venta.id
                               descripcion VARCHAR(150) NOT NULL,                 -- Descripción del ítem
                               cantidad INT NOT NULL,                             -- Cantidad
                               precio_unitario DOUBLE NOT NULL,                   -- Precio unitario
                               subtotal DOUBLE NOT NULL,                          -- Subtotal
                               CONSTRAINT fk_venta FOREIGN KEY (venta_id) REFERENCES venta(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- TABLA: menu
-- ------------------------------------------------------------
-- Menú dinámico por perfil (versión simple/legacy).
CREATE TABLE IF NOT EXISTS menu (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- ID autoincremental
                                    menu_nombre VARCHAR(50) NOT NULL,     -- Grupo de menú
    menu_item_nombre VARCHAR(50),                                        -- Opción visible
    id_nombre_obj VARCHAR(50) NOT NULL,                                  -- ID interno de vista/acción
    perfil VARCHAR(30) NOT NULL                                          -- Perfil como texto
    );

-- Datos iniciales de menú para ADMINISTRADOR (legacy).
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Almacén', 'Producto', '1', 'ADMINISTRADOR');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Almacén', 'Categoría', '2', 'ADMINISTRADOR');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Ventas', 'Registrar Venta / POS', '3', 'ADMINISTRADOR');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Configuración', 'Cerrar Sesión', '4', 'ADMINISTRADOR');
