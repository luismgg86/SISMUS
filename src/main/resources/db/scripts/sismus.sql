-- ============================================================
-- DATABASE: sismus
-- ============================================================

CREATE DATABASE IF NOT EXISTS sismus
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE sismus;

-- ============================================================
-- LIMPIEZA (para recrear la BD de forma segura)
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS 
  lista_cancion,
  usuario_cancion,
  usuario_rol,
  lista,
  cancion,
  genero,
  artista,
  usuario,
  rol;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- TABLE: rol
-- ============================================================
CREATE TABLE rol (
  rol_id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL,
  PRIMARY KEY (rol_id),
  UNIQUE KEY uq_rol_nombre (nombre)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- ============================================================
-- TABLE: usuario
-- ============================================================
CREATE TABLE usuario (
  usuario_id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50),
  ap_paterno VARCHAR(50),
  ap_materno VARCHAR(50),
  correo VARCHAR(100),
  nickname VARCHAR(30),
  password VARCHAR(100),
  ultimo_acceso DATETIME,
  activo TINYINT(1) DEFAULT 1,
  PRIMARY KEY (usuario_id),
  UNIQUE KEY uq_usuario_correo (correo),
  UNIQUE KEY uq_usuario_nickname (nickname)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- ============================================================
-- TABLE: artista
-- ============================================================
CREATE TABLE artista (
  artista_id INT NOT NULL AUTO_INCREMENT,
  clave VARCHAR(5),
  nombre VARCHAR(50),
  activo TINYINT(1) DEFAULT 1,
  PRIMARY KEY (artista_id),
  UNIQUE KEY uq_artista_clave (clave)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- ============================================================
-- TABLE: genero
-- ============================================================
CREATE TABLE genero (
  genero_id INT NOT NULL AUTO_INCREMENT,
  clave VARCHAR(5),
  nombre VARCHAR(50),
  PRIMARY KEY (genero_id),
  UNIQUE KEY uq_genero_clave (clave)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- ============================================================
-- TABLE: cancion
-- ============================================================
CREATE TABLE cancion (
  cancion_id INT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(50),
  audio VARCHAR(200),
  duracion INT,
  fecha_alta DATE,
  artista_id INT,
  genero_id INT,
  activo TINYINT(1) DEFAULT 1,
  PRIMARY KEY (cancion_id),
  KEY idx_cancion_artista (artista_id),
  KEY idx_cancion_genero (genero_id),
  CONSTRAINT fk_cancion_artista FOREIGN KEY (artista_id) REFERENCES artista (artista_id),
  CONSTRAINT fk_cancion_genero FOREIGN KEY (genero_id) REFERENCES genero (genero_id)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- ============================================================
-- TABLE: lista
-- ============================================================
CREATE TABLE lista (
  lista_id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50),
  fecha_creacion DATE,
  usuario_id INT,
  PRIMARY KEY (lista_id),
  KEY idx_lista_usuario (usuario_id),
  CONSTRAINT fk_lista_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (usuario_id)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- ============================================================
-- TABLE: usuario_rol
-- ============================================================
CREATE TABLE usuario_rol (
  usuario_id INT NOT NULL,
  rol_id INT NOT NULL,
  PRIMARY KEY (usuario_id, rol_id),
  KEY idx_usuario_rol_rol (rol_id),
  CONSTRAINT fk_usuario_rol_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (usuario_id),
  CONSTRAINT fk_usuario_rol_rol FOREIGN KEY (rol_id) REFERENCES rol (rol_id)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- ============================================================
-- TABLE: usuario_cancion
-- ============================================================
CREATE TABLE usuario_cancion (
  usuario_cancion_id INT NOT NULL AUTO_INCREMENT,
  usuario_id INT NOT NULL,
  cancion_id INT NOT NULL,
  fecha_descarga DATE,
  PRIMARY KEY (usuario_cancion_id),
  KEY idx_usuario_cancion_usuario (usuario_id),
  KEY idx_usuario_cancion_cancion (cancion_id),
  CONSTRAINT fk_usuario_cancion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (usuario_id),
  CONSTRAINT fk_usuario_cancion_cancion FOREIGN KEY (cancion_id) REFERENCES cancion (cancion_id)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- ============================================================
-- TABLE: lista_cancion
-- ============================================================
CREATE TABLE lista_cancion (
  lista_cancion_id INT NOT NULL AUTO_INCREMENT,
  lista_id INT NOT NULL,
  cancion_id INT NOT NULL,
  fecha_agregada DATE,
  PRIMARY KEY (lista_cancion_id),
  UNIQUE KEY uq_lista_cancion (lista_id, cancion_id),
  KEY idx_lista_cancion_cancion (cancion_id),
  CONSTRAINT fk_lista_cancion_lista FOREIGN KEY (lista_id) REFERENCES lista (lista_id) ON DELETE CASCADE,
  CONSTRAINT fk_lista_cancion_cancion FOREIGN KEY (cancion_id) REFERENCES cancion (cancion_id) ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- ============================================================
-- CARGA DE DATOS
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Roles base
INSERT INTO rol (nombre) VALUES
  ('ADMIN'),
  ('USER');

-- Usuarios
INSERT INTO usuario (nombre, ap_paterno, ap_materno, correo, nickname, password, ultimo_acceso, activo) VALUES
  ('luis','gonzález',NULL,'luis@example.com','luismgg','{bcrypt}$2a$10$9QFiiT5F3LWvzpGNtauX8eKtieHjO1L/W3UqjbcbXVZyvaklwydY.','2025-10-26 20:39:30',1),
  ('ana','martínez',NULL,'ana@example.com','anamlo','{bcrypt}$2a$10$DzEWhlgcAFdjpnbKinAsr.Z4AG7CV1SZaa8JEUEJycCd.f31a2L0q','2025-10-26 13:56:43',1),
  ('carlos','ramírez',NULL,'carlos@example.com','carlitos','{bcrypt}$2a$10$E8Isj.szI3ocdXRo8NC6yuz6CJQJpHVcozXp7WKv9R4A/PF3Mpy6C','2025-10-25 20:28:33',1),
  ('Pedro','hernandez',NULL,'pedropz@gmail.com','pedropz','{bcrypt}$2a$10$V9XLRIY5XmnPQU3md0sR/e.xIzPgK8ZQfaSURuEr054Wjsfw.f7rW','2025-10-26 13:49:43',1),
  ('pepito','perez',NULL,'pepito@gmail.com','pepito123','{bcrypt}$2a$10$dfGqMDtV1xyHxe5m1YCJHO6NXMtJ1bm/r7gUxJ7ydGLLjrDjsxekq','2025-10-26 20:14:26',1),
  ('pepe','perez',NULL,'pepe@example.com','pepe1','{bcrypt}$2a$10$4UeNqDzSwUvd0O9hPQlxN.t/5eUGsQXPNK.EU/qaVFXH1InWhX36.','2025-10-26 20:39:47',1);

-- Artistas
INSERT INTO artista (clave, nombre, activo) VALUES
  ('a001','zoé',1),
  ('a002','natalia lafourcade',1),
  ('a003','tame impala',1),
  ('a004','the weeknd',1),
  ('a005','little jesus',1),
  ('a006','mgmt',0),
  ('a007','jamie xx',1),
  ('a023','Interpol',1),
  ('a034','The cure',1),
  ('a026','Queen',1),
  ('a025','Depeche mode',1),
  ('a045','Gorillaz',1);

-- Géneros
INSERT INTO genero (clave, nombre) VALUES
  ('g001','pop alternativo'),
  ('g002','rock indie'),
  ('g003','psychedelic'),
  ('g004','folk latino'),
  ('g005','pop rock'),
  ('g006','Synth pop');

-- Canciones
INSERT INTO cancion (titulo, audio, duracion, fecha_alta, artista_id, genero_id, activo) VALUES
  ('Labios rotos','/audios/71b10d1a-7036-4988-a05d-6b469448db92.mp3',130,'2025-10-25',1,5,0),
  ('Miel','/audios/79e7bcbe-4fe6-40b3-b35a-35eb63f1dedf.mp3',754,'2025-10-25',1,4,1),
  ('Let it happen','/audios/c32c698c-e9bc-4e77-be85-d0f96b1e5c3c.mp3',280,'2025-10-25',3,3,1),
  ('Blinding lights','/audios/068277f9-f181-467b-9756-35d2b9d87960.mp3',200,'2025-10-25',4,1,1),
  ('La magia','/audios/34f25bf3-ef46-4b17-b416-23d66a63fd96.mp3',220,'2025-10-25',5,4,1),
  ('Electric feel','/audios/f5eabf4d-ef54-4be3-a69d-03b9e2fa0d76.mp3',210,'2025-10-25',6,6,1),
  ('Kids','/audios/4c213bd2-68a9-4f4a-a5ca-4dcdc7428be3.mp3',215,'2025-10-25',6,6,1),
  ('Stranger in a room','/audios/dc802bbb-1df5-444d-88e2-639ab71c6b17.mp3',195,'2025-10-25',7,6,1),
  ('Químicos','/audios/d146ce09-9151-4ab9-8542-a840eeb0ce39.mp3',190,'2025-10-26',5,4,1),
  ('The less I know the better','/audios/a802f400-5949-4f8c-bf80-5f9528dea517.mp3',215,'2025-10-25',3,3,1),
  ('Love','/audios/aaec51e2-4843-4504-8a06-c0861c7f1a96.mp3',203,'2025-10-25',1,5,1),
  ('Gosh','/audios/c448588f-122d-498c-aa99-c837a2711e3d.mp3',263,'2025-10-25',7,6,1),
  ('Soñé','/audios/c6d542dd-1c57-4273-9e86-2a2c06140601.mp3',225,'2025-10-21',1,4,1),
  ('Enjoy the silence','/audios/7198763a-d01b-45a2-a417-3b01413913e6.mp3',258,'2025-10-25',11,6,1),
  ('Bohemian Rhapsody','/audios/afc5a314-bda5-4b01-910b-9c765fdfd506.mp3',350,'2025-10-26',10,3,1);

-- Listas
INSERT INTO lista (nombre, fecha_creacion, usuario_id) VALUES
  ('Viaje psicodélico','2025-04-16',2),
  ('Lo mejor del 2025','2025-04-17',3),
  ('Favoritos de Pedro','2025-10-18',4),
  ('Canciones para probar','2025-10-19',4),
  ('Playlist personal de Pedro','2025-10-19',4),
  ('Música para hacer ejercicio','2025-10-26',1),
  ('Música para estudiar','2025-10-26',1),
  ('Música para trabajar','2025-10-26',1),
  ('Música para correr','2025-10-26',1),
  ('Relax diario','2025-10-26',1),
  ('Descubrimientos recientes','2025-10-26',1),
  ('Favoritos del mes','2025-10-26',1),
  ('Música para concentrarse','2025-10-26',6);

-- Usuario - Rol
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
  (1,1),
  (1,2),
  (2,1),
  (2,2),
  (3,1),
  (4,1),
  (5,1),
  (6,1),
  (6,2);

-- Usuario - Canción
INSERT INTO usuario_cancion (usuario_id, cancion_id, fecha_descarga) VALUES
  (1,1,'2025-04-20'),
  (1,3,'2025-04-21'),
  (2,4,'2025-04-21'),
  (2,6,'2025-04-22'),
  (3,2,'2025-04-22'),
  (3,7,'2025-04-23'),
  (3,9,'2025-04-23'),
  (1,10,'2025-10-15'),
  (1,4,'2025-10-15'),
  (1,2,'2025-10-16'),
  (1,1,'2025-10-18'),
  (1,6,'2025-10-18'),
  (4,1,'2025-10-18'),
  (1,13,'2025-10-19'),
  (1,3,'2025-10-23'),
  (1,15,'2025-10-25'),
  (1,2,'2025-10-25'),
  (1,3,'2025-10-25'),
  (1,4,'2025-10-25'),
  (1,5,'2025-10-25'),
  (1,6,'2025-10-25'),
  (1,7,'2025-10-25'),
  (1,8,'2025-10-25'),
  (1,9,'2025-10-25'),
  (1,10,'2025-10-26'),
  (1,11,'2025-10-26'),
  (1,12,'2025-10-26'),
  (1,13,'2025-10-26'),
  (1,14,'2025-10-26'),
  (1,15,'2025-10-26');

-- Lista - Canción
INSERT INTO lista_cancion (lista_id, cancion_id, fecha_agregada) VALUES
  (1,5,'2025-10-18'),
  (1,9,'2025-10-18'),
  (1,6,'2025-10-19'),
  (2,2,'2025-10-19'),
  (2,5,'2025-10-19'),
  (3,6,'2025-10-19'),
  (3,7,'2025-10-19'),
  (3,8,'2025-10-19'),
  (2,3,'2025-10-19'),
  (1,10,'2025-10-19'),
  (2,1,'2025-10-19'),
  (1,4,'2025-10-19'),
  (6,6,'2025-10-26'),
  (6,7,'2025-10-26'),
  (6,13,'2025-10-26'),
  (7,9,'2025-10-26'),
  (7,15,'2025-10-26'),
  (7,3,'2025-10-26'),
  (7,1,'2025-10-26'),
  (7,5,'2025-10-26'),
  (6,2,'2025-10-26'),
  (7,2,'2025-10-26'),
  (6,4,'2025-10-26'),
  (7,8,'2025-10-26'),
  (6,15,'2025-10-26'),
  (7,14,'2025-10-26'),
  (8,7,'2025-10-26'),
  (8,8,'2025-10-26'),
  (8,15,'2025-10-26'),
  (8,10,'2025-10-26'),
  (8,9,'2025-10-26');

SET FOREIGN_KEY_CHECKS = 1;
