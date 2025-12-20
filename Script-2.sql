-- ==========================================
-- 1. LIMPIEZA TOTAL Y REINICIO (RESET)
-- ==========================================

-- Borramos primero los hijos (inmuebles) para no violar la Foreign Key
DELETE FROM inmueble;
-- Borramos los padres (usuarios)
DELETE FROM usuario;

-- ¡TRUCO! Reiniciamos los contadores de ID a 1.
-- Así Paqui volverá a ser ID 1 y Pepe ID 2.
ALTER TABLE usuario ALTER COLUMN id RESTART WITH 1;
ALTER TABLE inmueble ALTER COLUMN id RESTART WITH 1;

-- ==========================================
-- 2. INSERTAR USUARIOS
-- ==========================================

-- Usuario 1: La Dueña (Paqui) -> Asumimos que obtendrá ID 1
INSERT INTO usuario (correo, contrasena, nombre, apellido, role, created_at, updated_at)
VALUES ('paqui@inmo.com', '{noop}1234', 'Paqui', 'Dueña', 'PROPIETARIO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Usuario 2: El Inquilino (Pepe) -> Asumimos que obtendrá ID 2
INSERT INTO usuario (correo, contrasena, nombre, apellido, role, created_at, updated_at)
VALUES ('pepe@viajero.com', '{noop}1234', 'Pepe', 'Viajero', 'INQUILINO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==========================================
-- 3. INSERTAR INMUEBLES
-- Nota: id_anfitrion = 1 (Paqui)
-- He usado 'foto_url' (snake_case) que suele ser el defecto de Hibernate
-- ==========================================

-- Alojamiento 1: Cuenca
INSERT INTO inmueble (id_anfitrion, nombre, ciudad, direccion, descripcion, tipo, capacidad, precio, foto_url, distancia_centro, politica_cancelacion, created_at, is_active)
VALUES (1, 'Cabaña Rústica', 'Cuenca', 'Sierra Alta 45', 'Ideal para desconectar en plena naturaleza.', 'Casa', 4, 120.50, 'https://picsum.photos/id/1018/400/300', 12.5, 'FLEXIBLE', CURRENT_TIMESTAMP, true);

-- Alojamiento 2: Toledo
INSERT INTO inmueble (id_anfitrion, nombre, ciudad, direccion, descripcion, tipo, capacidad, precio, foto_url, distancia_centro, politica_cancelacion, created_at, is_active)
VALUES (1, 'Loft Histórico', 'Toledo', 'Calle del Comercio 12', 'Apartamento moderno en pleno casco antiguo.', 'Apartamento', 2, 85.00, 'https://picsum.photos/id/1020/400/300', 0.2, 'ESTRICTA', CURRENT_TIMESTAMP, true);

-- Alojamiento 3: Albacete
INSERT INTO inmueble (id_anfitrion, nombre, ciudad, direccion, descripcion, tipo, capacidad, precio, foto_url, distancia_centro, politica_cancelacion, created_at, is_active)
VALUES (1, 'Piso Céntrico', 'Albacete', 'Calle Ancha 5', 'Amplio piso cerca de la zona de tiendas y feria.', 'Piso', 6, 60.00, 'https://picsum.photos/id/1033/400/300', 0.5, 'MODERADA', CURRENT_TIMESTAMP, true);

-- Alojamiento 4: Ciudad Real
INSERT INTO inmueble (id_anfitrion, nombre, ciudad, direccion, descripcion, tipo, capacidad, precio, foto_url, distancia_centro, politica_cancelacion, created_at, is_active)
VALUES (1, 'El Cortijo Manchego', 'Ciudad Real', 'Camino del Vino s/n', 'Gran casa con piscina y barbacoa para grupos.', 'Casa Rural', 10, 250.00, 'https://picsum.photos/id/1040/400/300', 5.0, 'FLEXIBLE', CURRENT_TIMESTAMP, true);

-- Alojamiento 5: Madrid
INSERT INTO inmueble (id_anfitrion, nombre, ciudad, direccion, descripcion, tipo, capacidad, precio, foto_url, distancia_centro, politica_cancelacion, created_at, is_active)
VALUES (1, 'Estudio Malasaña', 'Madrid', 'Calle Pez 8', 'Pequeño pero acogedor, ideal para parejas jóvenes.', 'Estudio', 2, 110.00, 'https://picsum.photos/id/1050/400/300', 1.0, 'ESTRICTA', CURRENT_TIMESTAMP, true);