/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.11.16-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: alethana_books
-- ------------------------------------------------------
-- Server version	10.11.16-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `alethana_books`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `alethana_books` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `alethana_books`;

--
-- Table structure for table `administrador`
--

DROP TABLE IF EXISTS `administrador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrador` (
  `id_usuario` int(11) NOT NULL,
  `rol` varchar(5) NOT NULL DEFAULT 'ADMIN',
  PRIMARY KEY (`id_usuario`),
  KEY `fk_administrador_usuario` (`id_usuario`,`rol`),
  CONSTRAINT `fk_administrador_usuario` FOREIGN KEY (`id_usuario`, `rol`) REFERENCES `usuario` (`id_usuario`, `rol`),
  CONSTRAINT `ck_administrador_rol` CHECK (`rol` = 'ADMIN')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrador`
--

LOCK TABLES `administrador` WRITE;
/*!40000 ALTER TABLE `administrador` DISABLE KEYS */;
INSERT INTO `administrador` VALUES
(4,'ADMIN');
/*!40000 ALTER TABLE `administrador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `id_categoria` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `uq_categoria_nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES
(10,'Arte'),
(8,'Autoayuda'),
(2,'Ciencia'),
(6,'Fantasía'),
(3,'Historia'),
(5,'Infantil'),
(9,'Negocios'),
(1,'Novela'),
(7,'Poesía'),
(4,'Tecnología');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id_usuario` int(11) NOT NULL,
  `rol` varchar(5) NOT NULL DEFAULT 'USER',
  PRIMARY KEY (`id_usuario`),
  KEY `fk_cliente_usuario` (`id_usuario`,`rol`),
  CONSTRAINT `fk_cliente_usuario` FOREIGN KEY (`id_usuario`, `rol`) REFERENCES `usuario` (`id_usuario`, `rol`),
  CONSTRAINT `ck_cliente_rol` CHECK (`rol` = 'USER')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES
(1,'USER'),
(3,'USER'),
(5,'USER'),
(6,'USER'),
(7,'USER'),
(8,'USER'),
(9,'USER'),
(10,'USER'),
(11,'USER'),
(12,'USER'),
(13,'USER'),
(14,'USER'),
(15,'USER');
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `codigo_descuento`
--

DROP TABLE IF EXISTS `codigo_descuento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `codigo_descuento` (
  `codigo` varchar(20) NOT NULL,
  `porcentaje` int(11) NOT NULL,
  PRIMARY KEY (`codigo`),
  CONSTRAINT `ck_codigo_porcentaje` CHECK (`porcentaje` between 1 and 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `codigo_descuento`
--

LOCK TABLES `codigo_descuento` WRITE;
/*!40000 ALTER TABLE `codigo_descuento` DISABLE KEYS */;
INSERT INTO `codigo_descuento` VALUES
('BIENVENIDA5',5),
('CYBER20',20),
('LECTOR15',15),
('NAVIDAD25',25),
('PROMO10',10);
/*!40000 ALTER TABLE `codigo_descuento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datos_envio`
--

DROP TABLE IF EXISTS `datos_envio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `datos_envio` (
  `id_envio` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `nombre_destinatario` varchar(100) NOT NULL,
  `direccion` varchar(150) NOT NULL,
  `ciudad` varchar(60) NOT NULL,
  `telefono` varchar(15) NOT NULL,
  PRIMARY KEY (`id_envio`),
  UNIQUE KEY `uq_datos_envio_usuario` (`id_usuario`),
  CONSTRAINT `fk_datos_envio_cliente` FOREIGN KEY (`id_usuario`) REFERENCES `cliente` (`id_usuario`) ON DELETE CASCADE,
  CONSTRAINT `ck_datos_envio_telefono` CHECK (`telefono` regexp '^[0-9]{7,15}$')
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datos_envio`
--

LOCK TABLES `datos_envio` WRITE;
/*!40000 ALTER TABLE `datos_envio` DISABLE KEYS */;
INSERT INTO `datos_envio` VALUES
(1,1,'Otra','Calle 1','Lorica','3001234567'),
(2,3,'Prueba Final','Carrera 5 #20-15','Montería','3014567890'),
(3,5,'Carlos Ruiz','Calle 29 #8-40 Barrio Centro','Montería','3105551234'),
(4,6,'Sofía Martínez','Carrera 12 #14-22','Sahagún','3126547890'),
(5,7,'Juan Torres','Calle 18 #6-10 Barrio Las Mercedes','Planeta Rica','3157894561'),
(6,8,'Valentina Ortega','Transversal 9 #41-30 La Castellana','Montería','3001112233'),
(7,9,'Laura Mendoza','Calle 3 #15-27','Lorica','3209876543'),
(8,10,'Pedro Salcedo','Carrera 10 #11-05','Cereté','3183456789'),
(9,11,'Camila Herrera','Calle 44 #2-18 Barrio Recreo','Montería','3046789012'),
(10,12,'Daniel Vargas','Carrera 7 #9-33','Sahagún','3112223344'),
(11,13,'Isabella Pastrana','Circular 2 #36-14 El Prado','Montería','3168889900'),
(12,14,'Miguel Durango','Calle 1 #4-50','Lorica','7814455'),
(13,15,'Jose Andres Ortega Fdz','Calle 1 cn 2','Mi casa','3123456');
/*!40000 ALTER TABLE `datos_envio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_venta`
--

DROP TABLE IF EXISTS `detalle_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_venta` (
  `id_venta` int(11) NOT NULL,
  `id_libro` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(12,2) NOT NULL,
  `subtotal_linea` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id_venta`,`id_libro`),
  KEY `fk_detalle_venta_libro` (`id_libro`),
  CONSTRAINT `fk_detalle_venta_libro` FOREIGN KEY (`id_libro`) REFERENCES `libro` (`id_libro`),
  CONSTRAINT `fk_detalle_venta_venta` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`) ON DELETE CASCADE,
  CONSTRAINT `ck_detalle_venta` CHECK (`subtotal_linea` = `cantidad` * `precio_unitario`),
  CONSTRAINT `ck_detalle_venta_cantidad` CHECK (`cantidad` > 0),
  CONSTRAINT `ck_detalle_venta_precio_unitario` CHECK (`precio_unitario` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_venta`
--

LOCK TABLES `detalle_venta` WRITE;
/*!40000 ALTER TABLE `detalle_venta` DISABLE KEYS */;
INSERT INTO `detalle_venta` VALUES
(1,1,2,55000.00,110000.00),
(2,4,1,52000.00,52000.00),
(2,7,1,48000.00,48000.00),
(3,8,1,65000.00,65000.00),
(3,9,1,72000.00,72000.00),
(4,12,2,28000.00,56000.00),
(4,19,1,85000.00,85000.00),
(5,13,1,59000.00,59000.00),
(5,14,1,54000.00,54000.00),
(6,10,1,69000.00,69000.00),
(7,6,1,30000.00,30000.00),
(7,15,1,32000.00,32000.00),
(7,16,1,62000.00,62000.00),
(8,20,1,110000.00,110000.00),
(8,21,1,95000.00,95000.00),
(9,5,2,38000.00,76000.00),
(10,3,1,90000.00,90000.00),
(10,11,1,45000.00,45000.00),
(11,22,1,18000.00,18000.00),
(11,23,1,22000.00,22000.00),
(11,24,1,15000.00,15000.00),
(12,1,1,55000.00,55000.00),
(12,12,1,28000.00,28000.00),
(13,13,1,59000.00,59000.00),
(14,7,1,48000.00,48000.00),
(14,19,1,85000.00,85000.00),
(14,25,1,16000.00,16000.00),
(15,12,1,28000.00,28000.00),
(15,14,1,54000.00,54000.00),
(16,9,1,72000.00,72000.00),
(16,10,1,69000.00,69000.00),
(17,7,1,48000.00,48000.00);
/*!40000 ALTER TABLE `detalle_venta` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_detalle_venta_bi
BEFORE INSERT ON detalle_venta
FOR EACH ROW
BEGIN
DECLARE v_stock INT;
DECLARE v_activo TINYINT(1);

SELECT stock, activo INTO v_stock, v_activo
FROM libro
WHERE id_libro = NEW.id_libro;

IF v_activo = 0 THEN
SIGNAL SQLSTATE "45000" SET MESSAGE_TEXT = "El libro esta inactivo y no se puede vender";
END IF;

IF NEW.cantidad > v_stock THEN 
SIGNAL SQLSTATE "45000" SET MESSAGE_TEXT = "Stock insuficiente para cantidad solicitada";
END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_detalle_venta_ai
AFTER INSERT ON detalle_venta
FOR EACH ROW
BEGIN
  UPDATE libro
     SET stock = stock - NEW.cantidad
   WHERE id_libro = NEW.id_libro;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `libro`
--

DROP TABLE IF EXISTS `libro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `libro` (
  `id_libro` int(11) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(150) NOT NULL,
  `autor` varchar(100) NOT NULL,
  `id_categoria` int(11) NOT NULL,
  `precio` decimal(12,2) NOT NULL,
  `stock` int(11) NOT NULL,
  `ruta_imagen` varchar(255) DEFAULT NULL,
  `fecha_registro` datetime NOT NULL DEFAULT current_timestamp(),
  `tipo` varchar(7) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_libro`),
  UNIQUE KEY `uq_libro_id_tipo` (`id_libro`,`tipo`),
  KEY `fk_categoria` (`id_categoria`),
  KEY `ix_libro_titulo` (`titulo`),
  KEY `ix_libro_autor` (`autor`),
  CONSTRAINT `fk_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`),
  CONSTRAINT `ck_libro_precio` CHECK (`precio` >= 0),
  CONSTRAINT `ck_libro_stock` CHECK (`stock` >= 0),
  CONSTRAINT `ck_libro_tipo` CHECK (`tipo` in ('FISICO','DIGITAL')),
  CONSTRAINT `ck_libro_activo` CHECK (`activo` in (0,1))
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `libro`
--

LOCK TABLES `libro` WRITE;
/*!40000 ALTER TABLE `libro` DISABLE KEYS */;
INSERT INTO `libro` VALUES
(1,'Cien años de soledad','Gabriel García Márquez',1,55000.00,7,'515535837_122109753908919262_6853263663749249898_n.jpg','2026-10-31 17:23:27','FISICO',1),
(3,'Clean Code','Robert C. Martin',4,90000.00,98,'portadas/clean_code.jpg','2026-10-31 17:45:16','DIGITAL',1),
(4,'El amor en los tiempos del cólera','Gabriel García Márquez',1,52000.00,11,'portadas/amor_colera.jpg','2026-11-01 09:10:00','FISICO',1),
(5,'La vorágine','José Eustasio Rivera',1,38000.00,8,'portadas/la_voragine.jpg','2026-11-01 09:25:00','FISICO',1),
(6,'María','Jorge Isaacs',1,30000.00,14,'portadas/maria.jpg','2026-11-01 10:00:00','FISICO',1),
(7,'1984','George Orwell',1,48000.00,6,'portadas/1984.jpg','2026-11-02 11:15:00','FISICO',1),
(8,'Breve historia del tiempo','Stephen Hawking',2,65000.00,6,'portadas/breve_historia_tiempo.jpg','2026-11-02 15:40:00','FISICO',1),
(9,'Cosmos','Carl Sagan',2,72000.00,4,'portadas/cosmos.jpg','2026-11-03 08:30:00','FISICO',1),
(10,'Sapiens: de animales a dioses','Yuval Noah Harari',3,69000.00,9,'portadas/sapiens.jpg','2026-11-03 14:05:00','FISICO',1),
(11,'Historia mínima de Colombia','Jorge Orlando Melo',3,45000.00,7,'portadas/historia_minima_colombia.jpg','2026-11-04 10:20:00','FISICO',1),
(12,'El principito','Antoine de Saint-Exupéry',5,28000.00,16,'portadas/el_principito.jpg','2026-11-04 16:45:00','FISICO',1),
(13,'Harry Potter y la piedra filosofal','J. K. Rowling',6,59000.00,12,'portadas/hp_piedra_filosofal.jpg','2026-11-05 09:00:00','FISICO',1),
(14,'El Hobbit','J. R. R. Tolkien',6,54000.00,8,'portadas/el_hobbit.jpg','2026-11-05 13:30:00','FISICO',1),
(15,'Veinte poemas de amor y una canción desesperada','Pablo Neruda',7,32000.00,8,'portadas/veinte_poemas.jpg','2026-11-06 10:50:00','FISICO',1),
(16,'Hábitos atómicos','James Clear',8,62000.00,12,'portadas/habitos_atomicos.jpg','2026-11-07 12:00:00','FISICO',1),
(17,'Padre rico, padre pobre','Robert T. Kiyosaki',9,49000.00,0,'portadas/padre_rico.jpg','2026-11-07 17:20:00','FISICO',1),
(18,'Historia del arte','Ernst H. Gombrich',10,120000.00,3,'portadas/historia_arte.jpg','2026-11-08 09:45:00','FISICO',0),
(19,'The Pragmatic Programmer','David Thomas y Andrew Hunt',4,85000.00,97,'portadas/pragmatic_programmer.jpg','2026-11-08 14:10:00','DIGITAL',1),
(20,'Introducción a los algoritmos','Thomas H. Cormen',4,110000.00,98,'portadas/intro_algoritmos.jpg','2026-11-09 10:00:00','DIGITAL',1),
(21,'Fundamentos de bases de datos','Abraham Silberschatz',4,95000.00,98,'portadas/fundamentos_bd.jpg','2026-11-10 11:30:00','DIGITAL',1),
(22,'Orgullo y prejuicio','Jane Austen',1,18000.00,98,'portadas/orgullo_prejuicio.jpg','2026-11-11 08:15:00','DIGITAL',1),
(23,'El origen de las especies','Charles Darwin',2,22000.00,98,'portadas/origen_especies.jpg','2026-11-12 15:00:00','DIGITAL',1),
(24,'Cuentos de la selva','Horacio Quiroga',5,15000.00,98,'portadas/cuentos_selva.jpg','2026-11-13 09:40:00','DIGITAL',1),
(25,'El arte de la guerra','Sun Tzu',9,16000.00,98,'portadas/arte_guerra.jpg','2026-11-14 18:25:00','DIGITAL',1),
(26,'Roba como un artista','Austin Kleon',10,45000.00,12,'portadas/roba_como_artista.jpg','2026-11-22 16:17:09','FISICO',1),
(27,'Modos de ver','John Berger',10,52000.00,8,'portadas/modos_de_ver.jpg','2026-11-22 16:17:09','FISICO',1),
(28,'Cartas a Theo','Vincent van Gogh',10,20000.00,99,'portadas/cartas_theo.jpg','2026-11-22 16:17:09','DIGITAL',1),
(29,'El poder del ahora','Eckhart Tolle',8,58000.00,10,'portadas/poder_del_ahora.jpg','2026-11-22 16:17:09','FISICO',1),
(30,'Los 7 hábitos de la gente altamente efectiva','Stephen R. Covey',8,25000.00,99,'portadas/siete_habitos.jpg','2026-11-22 16:17:09','DIGITAL',1),
(31,'Cien sonetos de amor','Pablo Neruda',7,35000.00,9,'portadas/cien_sonetos.jpg','2026-11-22 16:17:09','FISICO',1),
(32,'Hojas de hierba','Walt Whitman',7,17000.00,99,'portadas/hojas_de_hierba.jpg','2026-11-22 16:17:09','DIGITAL',1),
(33,'El león, la bruja y el ropero','C. S. Lewis',6,47000.00,11,'portadas/leon_bruja_ropero.jpg','2026-11-22 16:17:09','FISICO',1),
(34,'Alicia en el país de las maravillas','Lewis Carroll',5,33000.00,14,'portadas/alicia.jpg','2026-11-22 16:17:09','FISICO',1),
(35,'Las venas abiertas de América Latina','Eduardo Galeano',3,56000.00,7,'portadas/venas_abiertas.jpg','2026-11-22 16:17:09','FISICO',1),
(36,'Armas, gérmenes y acero','Jared Diamond',3,74000.00,5,'portadas/armas_germenes_acero.jpg','2026-11-22 16:17:09','FISICO',1),
(37,'Piense y hágase rico','Napoleon Hill',9,42000.00,10,'portadas/piense_rico.jpg','2026-11-22 16:17:09','FISICO',1),
(38,'El gen egoísta','Richard Dawkins',2,68000.00,6,'portadas/gen_egoista.jpg','2026-11-22 16:17:09','FISICO',1);
/*!40000 ALTER TABLE `libro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `libro_digital`
--

DROP TABLE IF EXISTS `libro_digital`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `libro_digital` (
  `id_libro` int(11) NOT NULL,
  `formato` varchar(4) NOT NULL,
  `ruta_archivo` varchar(255) NOT NULL,
  `tipo` varchar(7) NOT NULL DEFAULT 'DIGITAL',
  PRIMARY KEY (`id_libro`),
  KEY `fk_libro_digital_libro` (`id_libro`,`tipo`),
  CONSTRAINT `fk_libro_digital_libro` FOREIGN KEY (`id_libro`, `tipo`) REFERENCES `libro` (`id_libro`, `tipo`),
  CONSTRAINT `ck_libro_digital_formato` CHECK (`formato` in ('PDF','EPUB','MOBI')),
  CONSTRAINT `ck_libro_digital_tipo` CHECK (`tipo` = 'DIGITAL')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `libro_digital`
--

LOCK TABLES `libro_digital` WRITE;
/*!40000 ALTER TABLE `libro_digital` DISABLE KEYS */;
INSERT INTO `libro_digital` VALUES
(3,'PDF','archivos/clean_code.pdf','DIGITAL'),
(19,'PDF','archivos/pragmatic_programmer.pdf','DIGITAL'),
(20,'PDF','archivos/intro_algoritmos.pdf','DIGITAL'),
(21,'EPUB','archivos/fundamentos_bd.epub','DIGITAL'),
(22,'EPUB','archivos/orgullo_prejuicio.epub','DIGITAL'),
(23,'MOBI','archivos/origen_especies.mobi','DIGITAL'),
(24,'PDF','archivos/cuentos_selva.pdf','DIGITAL'),
(25,'MOBI','archivos/arte_guerra.mobi','DIGITAL'),
(28,'PDF','archivos/cartas_theo.pdf','DIGITAL'),
(30,'EPUB','archivos/siete_habitos.epub','DIGITAL'),
(32,'EPUB','archivos/hojas_de_hierba.epub','DIGITAL');
/*!40000 ALTER TABLE `libro_digital` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `libro_fisico`
--

DROP TABLE IF EXISTS `libro_fisico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `libro_fisico` (
  `id_libro` int(11) NOT NULL,
  `peso` decimal(6,3) NOT NULL,
  `bodega` varchar(50) NOT NULL,
  `origen` varchar(10) NOT NULL,
  `tipo` varchar(7) NOT NULL DEFAULT 'FISICO',
  PRIMARY KEY (`id_libro`),
  KEY `fk_libro_fisico_libro` (`id_libro`,`tipo`),
  CONSTRAINT `fk_libro_fisico_libro` FOREIGN KEY (`id_libro`, `tipo`) REFERENCES `libro` (`id_libro`, `tipo`),
  CONSTRAINT `ck_libro_fisico_peso` CHECK (`peso` > 0),
  CONSTRAINT `ck_libro_fisico_origen` CHECK (`origen` in ('NACIONAL','IMPORTADO')),
  CONSTRAINT `ck_libro_fisico_tipo` CHECK (`tipo` = 'FISICO')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `libro_fisico`
--

LOCK TABLES `libro_fisico` WRITE;
/*!40000 ALTER TABLE `libro_fisico` DISABLE KEYS */;
INSERT INTO `libro_fisico` VALUES
(1,0.450,'Bodega Central','NACIONAL','FISICO'),
(4,0.520,'Bodega Central','NACIONAL','FISICO'),
(5,0.380,'Bodega Central','NACIONAL','FISICO'),
(6,0.340,'Bodega Norte','NACIONAL','FISICO'),
(7,0.410,'Bodega Central','IMPORTADO','FISICO'),
(8,0.450,'Bodega Norte','IMPORTADO','FISICO'),
(9,0.700,'Bodega Central','IMPORTADO','FISICO'),
(10,0.650,'Bodega Central','IMPORTADO','FISICO'),
(11,0.390,'Bodega Norte','NACIONAL','FISICO'),
(12,0.250,'Bodega Central','IMPORTADO','FISICO'),
(13,0.480,'Bodega Central','IMPORTADO','FISICO'),
(14,0.460,'Bodega Norte','IMPORTADO','FISICO'),
(15,0.200,'Bodega Central','IMPORTADO','FISICO'),
(16,0.420,'Bodega Central','IMPORTADO','FISICO'),
(17,0.330,'Bodega Norte','IMPORTADO','FISICO'),
(18,1.900,'Bodega Norte','IMPORTADO','FISICO'),
(26,0.300,'Bodega Central','IMPORTADO','FISICO'),
(27,0.280,'Bodega Norte','IMPORTADO','FISICO'),
(29,0.350,'Bodega Central','IMPORTADO','FISICO'),
(31,0.220,'Bodega Central','NACIONAL','FISICO'),
(33,0.400,'Bodega Norte','IMPORTADO','FISICO'),
(34,0.310,'Bodega Central','NACIONAL','FISICO'),
(35,0.520,'Bodega Central','NACIONAL','FISICO'),
(36,0.750,'Bodega Norte','IMPORTADO','FISICO'),
(37,0.380,'Bodega Central','IMPORTADO','FISICO'),
(38,0.480,'Bodega Norte','IMPORTADO','FISICO');
/*!40000 ALTER TABLE `libro_fisico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opinion`
--

DROP TABLE IF EXISTS `opinion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `opinion` (
  `id_opinion` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `texto` text NOT NULL,
  `calificacion` tinyint(4) NOT NULL,
  `fecha_hora` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_opinion`),
  KEY `fk_id_usuario` (`id_usuario`),
  CONSTRAINT `fk_id_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `cliente` (`id_usuario`) ON DELETE CASCADE,
  CONSTRAINT `ck_opinion_texto` CHECK (char_length(`texto`) >= 10),
  CONSTRAINT `ck_opinion_calificacion` CHECK (`calificacion` between 1 and 5)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opinion`
--

LOCK TABLES `opinion` WRITE;
/*!40000 ALTER TABLE `opinion` DISABLE KEYS */;
INSERT INTO `opinion` VALUES
(1,1,'Excelente libro, muy recomendado.',5,'2026-10-31 20:25:44'),
(2,5,'Muy buena atención, el libro llegó en perfecto estado y antes de lo esperado.',5,'2026-11-03 08:15:00'),
(3,6,'El descuento CYBER20 funcionó sin problemas. Volveré a comprar.',4,'2026-11-04 12:40:00'),
(4,8,'Descargué el PDF al instante, aunque me gustaría tener más títulos en EPUB.',4,'2026-11-05 10:05:00'),
(5,9,'El envío a Lorica tardó un poco más de lo indicado.',3,'2026-11-07 16:20:00'),
(6,11,'Excelente catálogo de autoayuda y poesía, precios justos.',5,'2026-11-08 09:30:00'),
(7,13,'Los libros digitales de programación son de muy buena calidad.',5,'2026-11-09 21:00:00'),
(8,7,'La pasarela de pago por Efecty fue confusa la primera vez.',2,'2026-11-10 11:11:00'),
(9,10,'Buen servicio, pero el buscador por autor podría mejorar.',4,'2026-11-11 18:45:00'),
(10,12,'Compré tres libros digitales y todos abrieron bien en mi lector.',5,'2026-11-12 07:50:00'),
(11,14,'Me llegó el pedido incompleto, aunque lo solucionaron rápido.',3,'2026-11-13 14:25:00'),
(12,3,'Página fácil de usar, el carrito valida el stock al momento.',4,'2026-11-14 19:10:00'),
(13,15,'Me guta muuuchooo',5,'2026-11-22 16:22:55');
/*!40000 ALTER TABLE `opinion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `correo` varchar(100) NOT NULL,
  `apodo` varchar(50) NOT NULL,
  `contrasena_hash` varchar(100) NOT NULL,
  `rol` varchar(5) NOT NULL DEFAULT 'USER',
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `uq_usuario_correo` (`correo`),
  UNIQUE KEY `uq_usuario_id_rol` (`id_usuario`,`rol`),
  CONSTRAINT `ck_usuario_correo` CHECK (`correo` like '%@%.%'),
  CONSTRAINT `ck_usuario_rol` CHECK (`rol` in ('USER','ADMIN'))
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES
(1,'ana@correo.com','Ana','hash1','USER'),
(3,'prueba_final@correo.com','PruebaFinal','hash','USER'),
(4,'admin@alethana.com','Administrador','3','ADMIN'),
(5,'carlos.ruiz@correo.com','CarlosR','d9804c97ea66f11d01db2411f696894a9b860f50ce1270a286c2b2af45dbd0d1','USER'),
(6,'sofia.martinez@correo.com','SofiM','9dc43a6c4ae9f454e61a0380502ded4ca583ab6d5973eaa5e8bac35ba950da0a','USER'),
(7,'juan.torres@correo.com','JuanT','b3b64dacc27ac1a3ff81e4bcf0123883a996a5275882475a4c56a8f599c103c6','USER'),
(8,'valentina.ortega@correo.com','ValeO','0e7f5d2acc21b326d77e199456796bb0318dca2ba737500cf7debb6c143d0ff7','USER'),
(9,'laura.mendoza@correo.com','LauMen','80bbed475746cfa3edb86fb0195fe8ca67ef2c51c093affa4e266f213b8c485f','USER'),
(10,'pedro.salcedo@correo.com','PedroS','febabaf3f4d81c1bab89ad603c771bb1b7d1a7867ba098fa5e8d53672719a151','USER'),
(11,'camila.herrera@correo.com','CamiH','4e460f9d042e7da0f5fc0bfc14fa0dc4eea22d377893f66a3a1af73160e6bbe9','USER'),
(12,'daniel.vargas@correo.com','DaniV','7d01e819a9dc3d2c8d3ae72bd306fefc9e7cb2cedd20aada4fb882f7216a9f41','USER'),
(13,'isabella.pastrana@correo.com','IsaP','57ccf31d0ea626cdc51e63c00ce921dd1150f55ff92f1e71488cba5bbb57c3e1','USER'),
(14,'miguel.durango@correo.com','MigueD','c9b34672e6c8b0743483b9724516bcea7858bcb28b5fc98c126ad6144850c2d8','USER'),
(15,'joseandres@correo.com','Jose Andres Ortega Fdz','f6ff5adab5180a3d10ffb611f2caddd0a2b0922bde398ad186e946480bec3943','USER');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_usuario_ai
AFTER INSERT On usuario
FOR EACH ROW 
BEGIN 
IF NEW.rol = "ADMIN" THEN
INSERT INTO administrador (id_usuario) VALUES (NEW.id_usuario);
END IF;
IF NEW.rol = "USER" THEN 
INSERT INTO cliente (id_usuario) VALUES(NEW.id_usuario);
END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Temporary table structure for view `v_historial_compras`
--

DROP TABLE IF EXISTS `v_historial_compras`;
/*!50001 DROP VIEW IF EXISTS `v_historial_compras`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `v_historial_compras` AS SELECT
 1 AS `id_usuario`,
  1 AS `id_venta`,
  1 AS `fecha`,
  1 AS `metodo_pago`,
  1 AS `tipo_descuento`,
  1 AS `titulo`,
  1 AS `cantidad`,
  1 AS `precio_unitario`,
  1 AS `subtotal_linea`,
  1 AS `subtotal`,
  1 AS `descuento`,
  1 AS `total` */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_libros_importados`
--

DROP TABLE IF EXISTS `v_libros_importados`;
/*!50001 DROP VIEW IF EXISTS `v_libros_importados`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `v_libros_importados` AS SELECT
 1 AS `id_libro`,
  1 AS `titulo`,
  1 AS `autor`,
  1 AS `categoria`,
  1 AS `precio`,
  1 AS `stock`,
  1 AS `origen` */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_pedidos_admin`
--

DROP TABLE IF EXISTS `v_pedidos_admin`;
/*!50001 DROP VIEW IF EXISTS `v_pedidos_admin`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `v_pedidos_admin` AS SELECT
 1 AS `id_venta`,
  1 AS `fecha`,
  1 AS `correo`,
  1 AS `apodo`,
  1 AS `metodo_pago`,
  1 AS `tipo_descuento`,
  1 AS `subtotal`,
  1 AS `descuento`,
  1 AS `total`,
  1 AS `envio_nombre`,
  1 AS `envio_ciudad` */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_resenas`
--

DROP TABLE IF EXISTS `v_resenas`;
/*!50001 DROP VIEW IF EXISTS `v_resenas`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `v_resenas` AS SELECT
 1 AS `id_opinion`,
  1 AS `apodo`,
  1 AS `calificacion`,
  1 AS `texto`,
  1 AS `fecha_hora` */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_resumen_dia`
--

DROP TABLE IF EXISTS `v_resumen_dia`;
/*!50001 DROP VIEW IF EXISTS `v_resumen_dia`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `v_resumen_dia` AS SELECT
 1 AS `ventas_hoy`,
  1 AS `ingresos_hoy` */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_ultimos_libros`
--

DROP TABLE IF EXISTS `v_ultimos_libros`;
/*!50001 DROP VIEW IF EXISTS `v_ultimos_libros`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `v_ultimos_libros` AS SELECT
 1 AS `id_libro`,
  1 AS `titulo`,
  1 AS `autor`,
  1 AS `categoria`,
  1 AS `precio`,
  1 AS `fecha_registro` */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `venta`
--

DROP TABLE IF EXISTS `venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `venta` (
  `id_venta` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `fecha` datetime NOT NULL DEFAULT current_timestamp(),
  `metodo_pago` varchar(8) NOT NULL,
  `tipo_descuento` varchar(11) NOT NULL,
  `codigo` varchar(20) DEFAULT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `descuento` decimal(12,2) NOT NULL DEFAULT 0.00,
  `total` decimal(12,2) NOT NULL,
  `envio_nombre` varchar(100) NOT NULL,
  `envio_direccion` varchar(150) NOT NULL,
  `envio_ciudad` varchar(60) NOT NULL,
  `envio_telefono` varchar(15) NOT NULL,
  PRIMARY KEY (`id_venta`),
  KEY `fk_venta_cliente` (`id_usuario`),
  KEY `fk_venta_codigo` (`codigo`),
  CONSTRAINT `fk_venta_cliente` FOREIGN KEY (`id_usuario`) REFERENCES `cliente` (`id_usuario`),
  CONSTRAINT `fk_venta_codigo` FOREIGN KEY (`codigo`) REFERENCES `codigo_descuento` (`codigo`),
  CONSTRAINT `ck_venta_metodo_pago` CHECK (`metodo_pago` in ('TARJETA','PSE','EFECTY')),
  CONSTRAINT `ck_venta_tipo_descuento` CHECK (`tipo_descuento` in ('NORMAL','FIJO','PROMOCIONAL','DIGITAL')),
  CONSTRAINT `ck_venta_montos` CHECK (`subtotal` >= 0 and `descuento` >= 0 and `descuento` <= `subtotal` and `total` = `subtotal` - `descuento`),
  CONSTRAINT `ck_venta_envio_telefono` CHECK (`envio_telefono` regexp '^[0-9]{7,15}$')
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `venta`
--

LOCK TABLES `venta` WRITE;
/*!40000 ALTER TABLE `venta` DISABLE KEYS */;
INSERT INTO `venta` VALUES
(1,1,'2026-10-31 18:18:47','TARJETA','NORMAL',NULL,110000.00,0.00,110000.00,'Otra','Calle 1','Lorica','3001234567'),
(2,5,'2026-11-02 10:12:33','TARJETA','NORMAL',NULL,100000.00,0.00,100000.00,'Carlos Ruiz','Calle 29 #8-40 Barrio Centro','Montería','3105551234'),
(3,6,'2026-11-03 16:45:10','PSE','PROMOCIONAL','CYBER20',137000.00,27400.00,109600.00,'Sofía Martínez','Carrera 12 #14-22','Sahagún','3126547890'),
(4,8,'2026-11-04 09:05:47','EFECTY','DIGITAL',NULL,141000.00,8500.00,132500.00,'Valentina Ortega','Transversal 9 #41-30 La Castellana','Montería','3001112233'),
(5,9,'2026-11-05 19:30:02','TARJETA','FIJO',NULL,113000.00,10000.00,103000.00,'Laura Mendoza','Calle 3 #15-27','Lorica','3209876543'),
(6,1,'2026-11-06 11:20:15','PSE','NORMAL',NULL,69000.00,0.00,69000.00,'Otra','Calle 1','Lorica','3001234567'),
(7,11,'2026-11-07 14:55:40','TARJETA','PROMOCIONAL','LECTOR15',124000.00,18600.00,105400.00,'Camila Herrera','Calle 44 #2-18 Barrio Recreo','Montería','3046789012'),
(8,13,'2026-11-08 20:10:09','TARJETA','DIGITAL',NULL,205000.00,20500.00,184500.00,'Isabella Pastrana','Circular 2 #36-14 El Prado','Montería','3168889900'),
(9,7,'2026-11-09 08:40:51','EFECTY','NORMAL',NULL,76000.00,0.00,76000.00,'Juan Torres','Calle 18 #6-10 Barrio Las Mercedes','Planeta Rica','3157894561'),
(10,10,'2026-11-10 17:25:30','PSE','PROMOCIONAL','PROMO10',135000.00,13500.00,121500.00,'Pedro Salcedo','Carrera 10 #11-05','Cereté','3183456789'),
(11,12,'2026-11-11 12:00:00','TARJETA','FIJO',NULL,55000.00,5000.00,50000.00,'Daniel Vargas','Carrera 7 #9-33','Sahagún','3112223344'),
(12,14,'2026-11-12 10:33:21','EFECTY','NORMAL',NULL,83000.00,0.00,83000.00,'Miguel Durango','Calle 1 #4-50','Lorica','7814455'),
(13,3,'2026-11-13 15:48:05','TARJETA','PROMOCIONAL','BIENVENIDA5',59000.00,2950.00,56050.00,'Prueba Final','Carrera 5 #20-15','Montería','3014567890'),
(14,5,'2026-11-14 18:22:44','PSE','DIGITAL',NULL,149000.00,10100.00,138900.00,'Carlos Ruiz','Calle 29 #8-40 Barrio Centro','Montería','3105551234'),
(15,6,'2026-11-15 09:15:12','TARJETA','NORMAL',NULL,82000.00,0.00,82000.00,'Sofía Martínez','Carrera 12 #14-22','Sahagún','3126547890'),
(16,8,'2026-11-15 11:42:37','PSE','PROMOCIONAL','NAVIDAD25',141000.00,35250.00,105750.00,'Valentina Ortega','Transversal 9 #41-30 La Castellana','Montería','3001112233'),
(17,15,'2026-11-22 16:29:03','TARJETA','PROMOCIONAL','CYBER20',48000.00,9600.00,38400.00,'Jose Andres Ortega Fdz','Calle 1 cn 2','Mi casa','3123456');
/*!40000 ALTER TABLE `venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'alethana_books'
--

--
-- Current Database: `alethana_books`
--

USE `alethana_books`;

--
-- Final view structure for view `v_historial_compras`
--

/*!50001 DROP VIEW IF EXISTS `v_historial_compras`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_historial_compras` AS select `v`.`id_usuario` AS `id_usuario`,`v`.`id_venta` AS `id_venta`,`v`.`fecha` AS `fecha`,`v`.`metodo_pago` AS `metodo_pago`,`v`.`tipo_descuento` AS `tipo_descuento`,`l`.`titulo` AS `titulo`,`d`.`cantidad` AS `cantidad`,`d`.`precio_unitario` AS `precio_unitario`,`d`.`subtotal_linea` AS `subtotal_linea`,`v`.`subtotal` AS `subtotal`,`v`.`descuento` AS `descuento`,`v`.`total` AS `total` from ((`venta` `v` join `detalle_venta` `d` on(`d`.`id_venta` = `v`.`id_venta`)) join `libro` `l` on(`l`.`id_libro` = `d`.`id_libro`)) order by `v`.`fecha` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_libros_importados`
--

/*!50001 DROP VIEW IF EXISTS `v_libros_importados`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_libros_importados` AS select `l`.`id_libro` AS `id_libro`,`l`.`titulo` AS `titulo`,`l`.`autor` AS `autor`,`c`.`nombre` AS `categoria`,`l`.`precio` AS `precio`,`l`.`stock` AS `stock`,`f`.`origen` AS `origen` from ((`libro` `l` join `libro_fisico` `f` on(`f`.`id_libro` = `l`.`id_libro`)) join `categoria` `c` on(`c`.`id_categoria` = `l`.`id_categoria`)) where `l`.`activo` = 1 and `f`.`origen` = 'IMPORTADO' */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_pedidos_admin`
--

/*!50001 DROP VIEW IF EXISTS `v_pedidos_admin`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_pedidos_admin` AS select `v`.`id_venta` AS `id_venta`,`v`.`fecha` AS `fecha`,`u`.`correo` AS `correo`,`u`.`apodo` AS `apodo`,`v`.`metodo_pago` AS `metodo_pago`,`v`.`tipo_descuento` AS `tipo_descuento`,`v`.`subtotal` AS `subtotal`,`v`.`descuento` AS `descuento`,`v`.`total` AS `total`,`v`.`envio_nombre` AS `envio_nombre`,`v`.`envio_ciudad` AS `envio_ciudad` from (`venta` `v` join `usuario` `u` on(`u`.`id_usuario` = `v`.`id_usuario`)) order by `v`.`fecha` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_resenas`
--

/*!50001 DROP VIEW IF EXISTS `v_resenas`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_resenas` AS select `o`.`id_opinion` AS `id_opinion`,`u`.`apodo` AS `apodo`,`o`.`calificacion` AS `calificacion`,`o`.`texto` AS `texto`,`o`.`fecha_hora` AS `fecha_hora` from (`opinion` `o` join `usuario` `u` on(`u`.`id_usuario` = `o`.`id_usuario`)) order by `o`.`fecha_hora` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_resumen_dia`
--

/*!50001 DROP VIEW IF EXISTS `v_resumen_dia`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_resumen_dia` AS select count(0) AS `ventas_hoy`,coalesce(sum(`venta`.`total`),0) AS `ingresos_hoy` from `venta` where cast(`venta`.`fecha` as date) = curdate() */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_ultimos_libros`
--

/*!50001 DROP VIEW IF EXISTS `v_ultimos_libros`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_ultimos_libros` AS select `l`.`id_libro` AS `id_libro`,`l`.`titulo` AS `titulo`,`l`.`autor` AS `autor`,`c`.`nombre` AS `categoria`,`l`.`precio` AS `precio`,`l`.`fecha_registro` AS `fecha_registro` from (`libro` `l` join `categoria` `c` on(`c`.`id_categoria` = `l`.`id_categoria`)) where `l`.`activo` = 1 order by `l`.`fecha_registro` desc limit 10 */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-22 16:34:15
