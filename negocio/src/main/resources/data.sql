-- TABLA USUARIO (Singular, coincidiendo con @Table(name="usuario"))
INSERT INTO usuario (nombre, apellido, correo, contrasena, role, created_at) 
VALUES ('Pablo', 'Test', 'admin@test.com', '1234', 'INQUILINO', CURRENT_TIMESTAMP);