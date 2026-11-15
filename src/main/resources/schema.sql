-- Esquema base funcional para Sys.Ventas (H2)
DROP TABLE IF EXISTS venta_detalle;
DROP TABLE IF EXISTS venta;
DROP TABLE IF EXISTS vent_carrito;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS menu;
DROP TABLE IF EXISTS usuario;

CREATE TABLE usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    id_perfil BIGINT DEFAULT 1
);

CREATE TABLE menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_nombre VARCHAR(50) NOT NULL,
    menu_item_nombre VARCHAR(50),
    id_nombre_obj VARCHAR(50) NOT NULL,
    perfil VARCHAR(30) NOT NULL
);

CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DOUBLE NOT NULL DEFAULT 0,
    stock DOUBLE NOT NULL DEFAULT 0,
    id_categoria BIGINT DEFAULT 1,
    id_marca BIGINT DEFAULT 1,
    id_unid_medida BIGINT DEFAULT 1
);

CREATE TABLE cliente (
    id_cliente BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dni_ruc VARCHAR(20),
    direccion VARCHAR(150),
    telefono VARCHAR(30)
);

CREATE TABLE vent_carrito (
    id_vent_carrito BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    nombre_producto VARCHAR(150) NOT NULL,
    cantidad DOUBLE NOT NULL,
    p_buffer DOUBLE NOT NULL,
    subtotal DOUBLE NOT NULL,
    id_usuario BIGINT
);

CREATE TABLE venta (
    id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha TIMESTAMP NOT NULL,
    id_cliente BIGINT,
    id_usuario BIGINT,
    total DOUBLE NOT NULL,
    tipo_comprobante VARCHAR(30),
    serie VARCHAR(10),
    nro_comprobante VARCHAR(30)
);

CREATE TABLE venta_detalle (
    id_venta_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_venta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad DOUBLE NOT NULL,
    precio DOUBLE NOT NULL,
    subtotal DOUBLE NOT NULL,
    CONSTRAINT fk_venta_detalle_venta FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE
);

-- Datos iniciales
INSERT INTO usuario (username, password, id_perfil) VALUES ('admin', '123', 1);

INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Almacén', 'Producto', '1', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Ventas', 'Registrar Venta / POS', '3', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Reportes', 'Comprobantes', '4', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Sesión', 'Cerrar Sesión', '5', '1');

INSERT INTO producto (nombre, precio, stock, id_categoria, id_marca, id_unid_medida) VALUES
('Polo Casual Oversize Algodón', 45.00, 25, 1, 1, 1),
('Pantalón Jean Slim Fit Hombre', 89.90, 15, 1, 1, 1),
('Camisa Formal Slim Fit', 65.00, 20, 1, 1, 1);

INSERT INTO cliente (nombre, dni_ruc, direccion, telefono) VALUES
('Público General', '00000000', 'S/D', '');
