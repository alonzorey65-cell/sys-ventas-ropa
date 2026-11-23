-- Esquema base funcional para Sys.Ventas (PostgreSQL / Neon)
DROP TABLE IF EXISTS venta_detalle CASCADE;
DROP TABLE IF EXISTS venta CASCADE;
DROP TABLE IF EXISTS vent_carrito CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS producto CASCADE;
DROP TABLE IF EXISTS menu CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;
DROP TABLE IF EXISTS unid_medida CASCADE;
DROP TABLE IF EXISTS marca CASCADE;
DROP TABLE IF EXISTS categoria CASCADE;
DROP TABLE IF EXISTS perfil CASCADE;

CREATE TABLE perfil (
    id_perfil BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE categoria (
    id_categoria BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE marca (
    id_marca BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE unid_medida (
    id_unid_medida BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    abreviatura VARCHAR(20)
);

CREATE TABLE usuario (
    id_usuario BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    id_perfil BIGINT DEFAULT 1
);

CREATE TABLE menu (
    id BIGSERIAL PRIMARY KEY,
    menu_nombre VARCHAR(50) NOT NULL,
    menu_item_nombre VARCHAR(50),
    id_nombre_obj VARCHAR(50) NOT NULL,
    perfil VARCHAR(30) NOT NULL
);

CREATE TABLE producto (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DOUBLE PRECISION NOT NULL DEFAULT 0,
    stock DOUBLE PRECISION NOT NULL DEFAULT 0,
    id_categoria BIGINT DEFAULT 1,
    id_marca BIGINT DEFAULT 1,
    id_unid_medida BIGINT DEFAULT 1
);

CREATE TABLE cliente (
    id_cliente BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dni_ruc VARCHAR(20),
    direccion VARCHAR(150),
    telefono VARCHAR(30)
);

CREATE TABLE vent_carrito (
    id_vent_carrito BIGSERIAL PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    nombre_producto VARCHAR(150) NOT NULL,
    cantidad DOUBLE PRECISION NOT NULL,
    p_buffer DOUBLE PRECISION NOT NULL,
    subtotal DOUBLE PRECISION NOT NULL,
    id_usuario BIGINT
);

CREATE TABLE venta (
    id_venta BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL,
    id_cliente BIGINT,
    id_usuario BIGINT,
    total DOUBLE PRECISION NOT NULL,
    tipo_comprobante VARCHAR(30),
    serie VARCHAR(10),
    nro_comprobante VARCHAR(30)
);

CREATE TABLE venta_detalle (
    id_venta_detalle BIGSERIAL PRIMARY KEY,
    id_venta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad DOUBLE PRECISION NOT NULL,
    precio DOUBLE PRECISION NOT NULL,
    subtotal DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_venta_detalle_venta FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE
);

-- Datos iniciales
INSERT INTO perfil (nombre) VALUES ('ADMINISTRADOR');
INSERT INTO perfil (nombre) VALUES ('VENDEDOR');

INSERT INTO categoria (nombre) VALUES ('General');
INSERT INTO categoria (nombre) VALUES ('Ropa Hombre');
INSERT INTO categoria (nombre) VALUES ('Ropa Mujer');

INSERT INTO marca (nombre) VALUES ('Genérica');
INSERT INTO marca (nombre) VALUES ('Nike');
INSERT INTO marca (nombre) VALUES ('Adidas');

INSERT INTO unid_medida (nombre, abreviatura) VALUES ('Unidad', 'UND');
INSERT INTO unid_medida (nombre, abreviatura) VALUES ('Caja', 'CJ');
INSERT INTO unid_medida (nombre, abreviatura) VALUES ('Paquete', 'PQT');

INSERT INTO usuario (username, password, id_perfil) VALUES ('admin', '123', 1);
INSERT INTO usuario (username, password, id_perfil) VALUES ('vendedor', '123', 2);

-- Menús ADMINISTRADOR (perfil 1)
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Almacén', 'Producto', '1', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Ventas', 'Registrar Venta / POS', '3', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Reportes', 'Comprobantes', '4', '1');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Sesión', 'Cerrar Sesión', '5', '1');

-- Menús VENDEDOR (perfil 2)
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Ventas', 'Registrar Venta / POS', '3', '2');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Reportes', 'Comprobantes', '4', '2');
INSERT INTO menu (menu_nombre, menu_item_nombre, id_nombre_obj, perfil) VALUES ('Sesión', 'Cerrar Sesión', '5', '2');

INSERT INTO producto (nombre, precio, stock, id_categoria, id_marca, id_unid_medida) VALUES
('Polo Casual Oversize Algodón', 45.00, 25, 1, 1, 1),
('Pantalón Jean Slim Fit Hombre', 89.90, 15, 1, 1, 1),
('Camisa Formal Slim Fit', 65.00, 20, 1, 1, 1);

INSERT INTO cliente (nombre, dni_ruc, direccion, telefono) VALUES
('Público General', '00000000', 'S/D', '');
