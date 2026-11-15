-- Insertar usuario administrador inicial (Usuario: admin, Password: 123)
INSERT INTO usuario (username, password, rol)
VALUES ('admin', '123', 'ADMINISTRADOR');

-- Insertar productos iniciales (Ropa)
INSERT INTO producto (nombre, categoria, precio, stock)
VALUES ('Polo Casual Oversize Algodón', 'Polos', 45.00, 25);

INSERT INTO producto (nombre, categoria, precio, stock)
VALUES ('Pantalón Jean Slim Fit Hombre', 'Pantalones', 89.90, 15);

INSERT INTO producto (nombre, categoria, precio, stock)
VALUES ('Camisa Formal Slim Fit', 'Camisas', 65.00, 20);

-- Insertar cliente de prueba por defecto (Público General)
INSERT INTO cliente (dni_ruc, nombre, apellido_paterno, apellido_materno, direccion)
VALUES ('00000000', 'Público', 'General', '', 'S/D');