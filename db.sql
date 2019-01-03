-- MySQL dump 10.13  Distrib 8.0.12, for osx10.13 (x86_64)
--
-- Host: localhost    Database: forgotten
-- ------------------------------------------------------
-- Server version	8.0.12

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8mb4 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `attendance` (
  `date` date NOT NULL COMMENT '日付',
  `user_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ユーザID',
  `weekday` int(11) NOT NULL COMMENT '曜日',
  `summary` varchar(100) DEFAULT NULL COMMENT '勤務内容',
  `holiday` tinyint(4) NOT NULL COMMENT '0：休日でない 1:休日',
  `start_time` varchar(10) DEFAULT NULL COMMENT '始業時間',
  `end_time` varchar(10) DEFAULT NULL COMMENT '終業時間',
  `rest_time` varchar(100) DEFAULT NULL COMMENT '休憩',
  `work_time` varchar(100) DEFAULT NULL COMMENT '労働時間',
  PRIMARY KEY (`date`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='勤怠表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance_master`
--

DROP TABLE IF EXISTS `attendance_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `attendance_master` (
  `user_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `summary` varchar(100) DEFAULT NULL,
  `start_time` varchar(100) DEFAULT NULL,
  `end_time` varchar(100) DEFAULT NULL,
  `rest_time` varchar(100) DEFAULT NULL,
  `work_time` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_master`
--

LOCK TABLES `attendance_master` WRITE;
/*!40000 ALTER TABLE `attendance_master` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendance_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fare`
--

DROP TABLE IF EXISTS `fare`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `fare` (
  `fare_id` varchar(100) NOT NULL COMMENT '交通費ID',
  `date` varchar(100) NOT NULL COMMENT '日付',
  `user_id` varchar(100) NOT NULL COMMENT 'ユーザID',
  `purpose` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目的地',
  `transportation` varchar(100) NOT NULL COMMENT '交通手段',
  `departure` varchar(100) NOT NULL COMMENT '出発地',
  `arrival` varchar(100) NOT NULL COMMENT '到着',
  `round_trip` bit(1) DEFAULT NULL COMMENT '往復かどうかのフラグ 0:片道 1:往復',
  `fare` int(11) NOT NULL COMMENT '運賃',
  PRIMARY KEY (`fare_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交通費';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fare`
--

LOCK TABLES `fare` WRITE;
/*!40000 ALTER TABLE `fare` DISABLE KEYS */;
/*!40000 ALTER TABLE `fare` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fare_master`
--

DROP TABLE IF EXISTS `fare_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `fare_master` (
  `fare_master_id` varchar(100) NOT NULL,
  `user_id` varchar(100) NOT NULL,
  `purpose` varchar(100) NOT NULL,
  `transportation` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `departure` varchar(100) NOT NULL,
  `arrival` varchar(100) NOT NULL,
  `round_trip` varchar(100) NOT NULL,
  `fare` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fare_master`
--

LOCK TABLES `fare_master` WRITE;
/*!40000 ALTER TABLE `fare_master` DISABLE KEYS */;
/*!40000 ALTER TABLE `fare_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-12-24 14:26:50
