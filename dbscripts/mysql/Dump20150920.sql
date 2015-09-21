CREATE DATABASE  IF NOT EXISTS `webtest_db` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `webtest_db`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: localhost    Database: webtest_db
-- ------------------------------------------------------
-- Server version	5.6.21-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `gateways`
--

DROP TABLE IF EXISTS `gateways`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gateways` (
  `id_gateway` int(11) NOT NULL AUTO_INCREMENT,
  `ip_gateway` varchar(45) DEFAULT NULL,
  `descripcion` varchar(100) DEFAULT NULL,
  `feha_comunicacion` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id_gateway`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gateways`
--

LOCK TABLES `gateways` WRITE;
/*!40000 ALTER TABLE `gateways` DISABLE KEYS */;
INSERT INTO `gateways` VALUES (1,'127.0.0.1','Gateway Bloque Central Comuna 3','2015-03-20 17:30:25');
/*!40000 ALTER TABLE `gateways` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medidores`
--

DROP TABLE IF EXISTS `medidores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medidores` (
  `id_medidor` int(11) NOT NULL AUTO_INCREMENT,
  `serial` varchar(45) DEFAULT NULL,
  `id_gateway` int(11) DEFAULT NULL,
  `id_suscriptor` int(11) DEFAULT NULL,
  `fecha_instalacion` timestamp NULL DEFAULT NULL,
  `lectura_inicial` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id_medidor`),
  KEY `fk_med_suscript_idx` (`id_suscriptor`),
  KEY `fk_med_gateway_idx` (`id_gateway`),
  CONSTRAINT `fk_med_gateway` FOREIGN KEY (`id_gateway`) REFERENCES `gateways` (`id_gateway`),
  CONSTRAINT `fk_med_suscript` FOREIGN KEY (`id_suscriptor`) REFERENCES `suscriptores` (`id_suscriptor`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medidores`
--

LOCK TABLES `medidores` WRITE;
/*!40000 ALTER TABLE `medidores` DISABLE KEYS */;
INSERT INTO `medidores` VALUES (1,'1001',1,1,'2015-03-20 17:30:25',23.65),(2,'1002',1,2,'2015-03-20 17:30:25',45.55),(3,'1003',1,3,'2015-03-20 17:30:25',51.43),(4,'45600',1,1,'2015-09-20 05:00:00',46.00);
/*!40000 ALTER TABLE `medidores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recursos`
--

DROP TABLE IF EXISTS `recursos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recursos` (
  `id_recurso` int(11) NOT NULL,
  `recurso` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id_recurso`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recursos`
--

LOCK TABLES `recursos` WRITE;
/*!40000 ALTER TABLE `recursos` DISABLE KEYS */;
/*!40000 ALTER TABLE `recursos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recursos_x_roles`
--

DROP TABLE IF EXISTS `recursos_x_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recursos_x_roles` (
  `id_rol` int(11) NOT NULL,
  `id_recurso` int(11) NOT NULL,
  PRIMARY KEY (`id_rol`,`id_recurso`),
  KEY `fk_resorcerole_rolid_idx` (`id_recurso`),
  CONSTRAINT `fk_resorcerole_resid` FOREIGN KEY (`id_recurso`) REFERENCES `recursos` (`id_recurso`) ON UPDATE NO ACTION,
  CONSTRAINT `fk_resorcerole_rolid` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`) ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recursos_x_roles`
--

LOCK TABLES `recursos_x_roles` WRITE;
/*!40000 ALTER TABLE `recursos_x_roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `recursos_x_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `id_rol` int(11) NOT NULL,
  `nombre_rol` varchar(45) DEFAULT NULL,
  `descripcion_rol` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre_rol_UNIQUE` (`nombre_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles_x_usuarios`
--

DROP TABLE IF EXISTS `roles_x_usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles_x_usuarios` (
  `id_usuario` int(11) NOT NULL,
  `id_rol` int(11) NOT NULL,
  PRIMARY KEY (`id_usuario`,`id_rol`),
  KEY `fk_userroles_rolid_idx` (`id_rol`),
  CONSTRAINT `fk_userroles_rolid` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`) ON UPDATE NO ACTION,
  CONSTRAINT `fk_userroles_userid` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles_x_usuarios`
--

LOCK TABLES `roles_x_usuarios` WRITE;
/*!40000 ALTER TABLE `roles_x_usuarios` DISABLE KEYS */;
/*!40000 ALTER TABLE `roles_x_usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suscriptores`
--

DROP TABLE IF EXISTS `suscriptores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `suscriptores` (
  `id_suscriptor` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `identificacion` varchar(15) DEFAULT NULL,
  `tipo_identificacion` varchar(5) DEFAULT NULL,
  `fecha_nacimiento` timestamp NULL DEFAULT NULL,
  `activo` varchar(1) NOT NULL DEFAULT 'S',
  PRIMARY KEY (`id_suscriptor`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suscriptores`
--

LOCK TABLES `suscriptores` WRITE;
/*!40000 ALTER TABLE `suscriptores` DISABLE KEYS */;
INSERT INTO `suscriptores` VALUES (1,'JUAN ESTEBAN SANCHEZ','1000001','CC','1987-04-30 05:00:00','N'),(2,'ANDRES ALEJANDRO ACEVEDO','2000000','TI','1986-12-30 05:00:00','S'),(3,'YUIRI VIVIANA CORREA','3000000','RC','1999-12-31 05:00:00','S'),(4,'JULIO CESAR PAEZ CASTRO','76430','RC','1985-04-12 05:00:00','N');
/*!40000 ALTER TABLE `suscriptores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL,
  `nombre_usuario` varchar(45) DEFAULT NULL,
  `password` varchar(512) DEFAULT NULL,
  `password_salt` varchar(256) DEFAULT NULL COMMENT 'Salt usado durante la generacion del password ofuscado, necesario para ofuscar los password usados por los usuarios para autenticarse',
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `nombre_usuario_UNIQUE` (`nombre_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Tabla que almacena informacion de los usuarios, que no necesariamente representan personas. Pueden referirse a cualquier entidad que haga uso de la app.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'admin123','ae454a608ba2bbcea43da001c640633a0ce14fc672de35b1c409f9a2164688f6','53d3eb556755fc2642d3659195c3b1c9');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-09-20 19:22:06
