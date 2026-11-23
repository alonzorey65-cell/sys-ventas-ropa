-- ============================================================
-- ÍNDICES ADICIONALES - Sys.Ventas
-- ============================================================
-- Este script agrega índices para optimizar búsquedas frecuentes
-- en el punto de venta (POS).
--
-- idx_producto_nombre: acelera búsquedas de productos por nombre
--   (por ejemplo, cuando el usuario escribe en el buscador del POS).
--
-- idx_cliente_dni: acelera búsquedas de clientes por DNI/RUC
--   (por ejemplo, al buscar un cliente antes de registrar la venta).
--
-- Nota: CREATE INDEX IF NOT EXISTS evita error si ya existen.
CREATE INDEX IF NOT EXISTS idx_producto_nombre ON producto(nombre);
CREATE INDEX IF NOT EXISTS idx_cliente_dni ON cliente(dni_ruc);
