-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: onlinecoursedb
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `instructor_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK649nhrgf5mod1gvtnvonccapi` (`instructor_id`),
  CONSTRAINT `FK649nhrgf5mod1gvtnvonccapi` FOREIGN KEY (`instructor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'2026-01-15 02:15:57.944437','2026-01-15 02:15:57.944437','Intro to Java','Java Fundamentals',2),(2,'2026-01-15 02:15:57.954435','2026-01-15 02:15:57.954435','Build secure REST APIs','Spring Boot',2),(3,'2026-01-15 02:15:57.957441','2026-01-15 02:15:57.957441','Nederlands oefenen','Nederlands',3);
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollments`
--

DROP TABLE IF EXISTS `enrollments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `enrollment_date` datetime(6) NOT NULL,
  `course_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKi0g6mfijtuh199nj653nva6j5` (`student_id`,`course_id`),
  KEY `FKm6ptklbuk36d0q5nb8vpwnj48` (`course_id`),
  CONSTRAINT `FK2lha5vwilci2yi3vu5akusx4a` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKm6ptklbuk36d0q5nb8vpwnj48` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollments`
--

LOCK TABLES `enrollments` WRITE;
/*!40000 ALTER TABLE `enrollments` DISABLE KEYS */;
INSERT INTO `enrollments` VALUES (1,'2026-01-15 02:15:57.962440','2026-01-15 02:15:57.962440','2026-01-15 02:15:57.960435',1,4),(2,'2026-01-15 02:15:57.969433','2026-01-15 02:15:57.969433','2026-01-15 02:15:57.960435',2,4),(3,'2026-01-15 02:15:57.973436','2026-01-15 02:15:57.973436','2026-01-15 02:15:57.960435',1,5),(4,'2026-01-15 02:15:57.977434','2026-01-15 02:15:57.977434','2026-01-15 02:15:57.960435',3,6);
/*!40000 ALTER TABLE `enrollments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','INSTRUCTOR','STUDENT') NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-01-15 02:15:57.829257','2026-01-15 02:15:57.829257','admin@test.com','$2a$10$K6RgPVFV3nvsPYFbxzxbruOsFuZlUL6dcCaLO1n3oWDU7nuvefWFG','ADMIN','admin'),(2,'2026-01-15 02:15:57.922477','2026-01-15 02:15:57.922477','hilal@test.com','$2a$10$k6OYxdUM5R/Md4R5Ut/jTen7seMlHOhzJm7xKTN9FFjAKg5E67Y36','INSTRUCTOR','hilal'),(3,'2026-01-15 02:15:57.927438','2026-01-15 02:15:57.927438','teodora@test.com','$2a$10$sUbbOm1CwOvaLfGp7sq4MuysHsptv2.eEmTC0I5lQF2JlPJ2e07P.','INSTRUCTOR','teodora'),(4,'2026-01-15 02:15:57.931434','2026-01-15 02:15:57.931434','tamara@test.com','$2a$10$ztL7t6WIRxIgR0B3B6tOvu/oxMxfX8xdAJs8dxwJyQI1AycETUwRS','STUDENT','tamara'),(5,'2026-01-15 02:15:57.935485','2026-01-15 02:15:57.935485','eva@test.com','$2a$10$W9ZX05l7O8FcJp0nAGER1uBGwdL0jrYC9x8OrwSZiMb2g3O8gvBM2','STUDENT','eva'),(6,'2026-01-15 02:15:57.939442','2026-01-15 02:15:57.939442','vika@test.com','$2a$10$/p27hl29zGd37mnIXpW0OudgSzynT70MjNnozx5LtwK43nNitY8a.','STUDENT','vika');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-15  3:16:10
