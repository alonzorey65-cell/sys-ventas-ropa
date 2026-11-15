-- Eliminar tablas si ya existen (orden inverso por las llaves foráneas)
DROP TABLE IF EXISTS venta_detalle;
DROP TABLE IF EXISTS venta;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS usuario;

-- 1. Tabla Usuario (Para el Login)
CREATE TABLE usuario (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         username VARCHAR(50) NOT NULL UNIQUE,
                         password VARCHAR(100) NOT NULL,
                         rol VARCHAR(30) NOT NULL
);

-- 2. Tabla Producto (Inventario de ropa)
CREATE TABLE producto (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          categoria VARCHAR(50),
                          precio DOUBLE NOT NULL,
                          stock INT NOT NULL
);

-- 3. Tabla Cliente (Gestión de compradores / DNI)
CREATE TABLE cliente (
                         dni_ruc VARCHAR(15) PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         apellido_paterno VARCHAR(50),
                         apellido_materno VARCHAR(50),
                         direccion VARCHAR(150)
);

-- 4. Tabla Venta (Cabecera del comprobante)
CREATE TABLE venta (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       serie VARCHAR(10) NOT NULL,
                       numero VARCHAR(20) NOT NULL,
                       fecha VARCHAR(30) NOT NULL,
                       cliente_dni_ruc VARCHAR(15),
                       total DOUBLE NOT NULL,
                       CONSTRAINT fk_cliente FOREIGN KEY (cliente_dni_ruc) REFERENCES cliente(dni_ruc)
);

-- 5. Tabla Venta Detalle (Items del comprobante)
CREATE TABLE venta_detalle (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               venta_id BIGINT NOT NULL,
                               descripcion VARCHAR(150) NOT NULL,
                               cantidad INT NOT NULL,
                               precio_unitario DOUBLE NOT NULL,
                               subtotal DOUBLE NOT NULL,
                               CONSTRAINT fk_venta FOREIGN KEY (venta_id) REFERENCES venta(id) ON DELETE CASCADE
);

-- Tabla para los menús dinámicos
CREATE TABLE IF NOT EXISTS menu (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    menu_nombre VARCHAR(50) NOT NULL,
    menu_item_nombre VARCHAR(50),
    id_nombre_obj VARCHAR(50) NOT NULL,
    perfil VARCHAR(30) NOT NULL
    );

-- Insertar accesos para el Administrador
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Almacén', 'Producto', '1', 'ADMINISTRADOR');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Almacén', 'Categoría', '2', 'ADMINISTRADOR');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Ventas', 'Registrar Venta / POS', '3', 'ADMINISTRADOR');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Configuración', 'Cerrar Sesión', '4', 'ADMINISTRADOR');