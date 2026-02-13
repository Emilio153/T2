-- 2. Inserción de Usuarios (Todos con password: 1234)
-- Hash BCrypt de "1234": $2a$10$clYvYI6fR/U0J7.O1l7Kue.mK6/0L6B.vJ.vI8BWBW9x1B.N8.yW
INSERT INTO usuario (username, password, email, rol) VALUES
('admin01', '$2a$10$uAX0Uz1sETSDQlsI3E.G8evSBvWOt04jtG3cXxhnkvN5Z4ljp6Tfy', 'admin@empresa.com', 'ADMIN'),
('juan85', '$2a$10$uAX0Uz1sETSDQlsI3E.G8evSBvWOt04jtG3cXxhnkvN5Z4ljp6Tfy', 'juan@correo.com', 'USER'),
('maria_dev', '$2a$10$uAX0Uz1sETSDQlsI3E.G8evSBvWOt04jtG3cXxhnkvN5Z4ljp6Tfy', 'maria@correo.com', 'USER'),
('pablo_qa', '$2a$10$uAX0Uz1sETSDQlsI3E.G8evSBvWOt04jtG3cXxhnkvN5Z4ljp6Tfy', 'pablo@correo.com', 'USER');

-- 3. Inserción de 10 Tareas Variadas
INSERT INTO tarea (titulo, descripcion, fecha_creacion, fecha_vencimiento, estado, id_usuario) VALUES
-- Tareas del Admin (ID 1)
('Configuración Inicial', 'Configurar los parámetros del sistema', '2024-02-01', '2024-02-05', 'COMPLETADA', 1),
('Auditoría de Seguridad', 'Revisar logs de acceso sospechosos', '2024-02-10', '2024-02-15', 'PENDIENTE', 1),

-- Tareas de Juan (ID 2)
('Maquetación Frontend', 'Crear la vista de login con CSS', '2024-02-02', '2024-02-10', 'EN_PROGRESO', 2),
('Refactorización', 'Limpiar código muerto en el controlador', '2024-02-05', '2024-02-07', 'COMPLETADA', 2),
('Subir a Producción', 'Desplegar la versión 1.2.0', '2024-02-20', '2024-02-20', 'PENDIENTE', 2),

-- Tareas de Maria (ID 3)
('Diseño de Base de Datos', 'Crear diagrama ER de la nueva entidad', '2024-02-01', '2024-02-03', 'COMPLETADA', 3),
('Testing Unitario', 'Alcanzar el 80% de cobertura en servicios', '2024-02-12', '2024-02-28', 'EN_PROGRESO', 3),
('Revisión de Pull Requests', 'Aprobar cambios de la rama develop', '2024-02-15', '2024-02-16', 'PENDIENTE', 3),

-- Tareas de Pablo (ID 4)
('QA de Regresión', 'Probar que no se hayan roto funciones antiguas', '2024-02-18', '2024-02-22', 'PENDIENTE', 4),
('Automatización de Tests', 'Crear scripts de Selenium para el Home', '2024-02-05', '2024-02-15', 'COMPLETADA', 4);