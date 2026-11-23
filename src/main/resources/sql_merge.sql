-- ============================================================
-- DATOS INICIALES / SEED (LEGACY) - Sys.Ventas
-- ============================================================
-- Script auxiliar con datos de ejemplo para el esquema legacy
-- (crear.sql). No corresponde al esquema principal actual
-- (schema_neon.sql), porque usa columnas/tablas diferentes.
-- ============================================================

-- Inserta un usuario administrador inicial.
-- Usuario: admin, Contraseña: 123, Rol: ADMINISTRADOR.
INSERT INTO usuario (username, password, rol)
VALUES ('admin', '123', 'ADMINISTRADOR');

-- Inserta productos iniciales de ejemplo (ropa).
INSERT INTO producto (nombre, categoria, precio, stock)
VALUES ('Polo Casual Oversize Algodón', 'Polos', 45.00, 25);

INSERT INTO producto (nombre, categoria, precio, stock)
VALUES ('Pantalón Jean Slim Fit Hombre', 'Pantalones', 89.90, 15);

INSERT INTO producto (nombre, categoria, precio, stock)
VALUES ('Camisa Formal Slim Fit', 'Camisas', 65.00, 20);

-- Inserta cliente por defecto para ventas sin cliente identificado.
INSERT INTO cliente (dni_ruc, nombre, apellido_paterno, apellido_materno, direccion)
VALUES ('00000000', 'Público', 'General', '', 'S/D');
