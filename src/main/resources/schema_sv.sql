-- Índices para optimizar búsquedas rápidas en el POS de ropa
CREATE INDEX IF NOT EXISTS idx_producto_nombre ON producto(nombre);
CREATE INDEX IF NOT EXISTS idx_cliente_dni ON cliente(dni_ruc);