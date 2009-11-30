# Sequel Pro dump
# Version 1191
# http://code.google.com/p/sequel-pro
#
# Host: localhost (MySQL 5.0.83)
# Database: darpa
# Generation Time: 2009-11-30 13:15:49 -0700
# ************************************************************

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table balloon_results
# ------------------------------------------------------------

DROP TABLE IF EXISTS `balloon_results`;

CREATE TABLE `balloon_results` (
  `id` int(11) NOT NULL auto_increment,
  `balloon_id` int(11) default NULL,
  `search_result_id` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;



# Dump of table balloons
# ------------------------------------------------------------

DROP TABLE IF EXISTS `balloons`;

CREATE TABLE `balloons` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `status` varchar(255) default NULL,
  `lat` float(20,15) default NULL,
  `lng` float(20,15) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;



# Dump of table images
# ------------------------------------------------------------

DROP TABLE IF EXISTS `images`;

CREATE TABLE `images` (
  `id` int(11) unsigned NOT NULL auto_increment,
  `search_result_id` int(10) unsigned NOT NULL,
  `url` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_images_results` (`search_result_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table search_results
# ------------------------------------------------------------

DROP TABLE IF EXISTS `search_results`;

CREATE TABLE `search_results` (
  `id` int(11) unsigned NOT NULL auto_increment,
  `source` varchar(50) NOT NULL,
  `url` varchar(255) NOT NULL,
  `search_phrase` varchar(140) NOT NULL,
  `surrounding_text` text,
  `location` varchar(50) default NULL,
  `timestamp` datetime default NULL,
  `unique_id` varchar(255) NOT NULL,
  `status` varchar(255) default NULL,
  `contact` varchar(255) default NULL,
  `notes` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `source_unique_id` (`unique_id`,`source`),
  KEY `search_result_source` (`source`),
  KEY `status` (`status`)
) ENGINE=MyISAM AUTO_INCREMENT=5286 DEFAULT CHARSET=latin1;






/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
