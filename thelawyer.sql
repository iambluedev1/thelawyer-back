-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3307
-- Généré le :  mar. 17 avr. 2018 à 20:20
-- Version du serveur :  10.2.8-MariaDB
-- Version de PHP :  5.6.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `thelawyer`
--

-- --------------------------------------------------------

--
-- Structure de la table `bans`
--

DROP TABLE IF EXISTS `bans`;
CREATE TABLE IF NOT EXISTS `bans` (
  `ban_id` int(7) NOT NULL AUTO_INCREMENT,
  `hotlink_id` int(7) NOT NULL,
  `banned_name` varchar(45) NOT NULL,
  `ban_reason` text NOT NULL,
  `banned_by` varchar(45) NOT NULL,
  `ban_at` int(11) NOT NULL,
  PRIMARY KEY (`ban_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35010 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `hotlinks`
--

DROP TABLE IF EXISTS `hotlinks`;
CREATE TABLE IF NOT EXISTS `hotlinks` (
  `hotlink_id` int(7) NOT NULL AUTO_INCREMENT,
  `hotlink_name` varchar(45) NOT NULL,
  `hotlink_banlist_url` text NOT NULL,
  `hotlink_main_url` text NOT NULL,
  `hotlink_type` varchar(14) NOT NULL,
  PRIMARY KEY (`hotlink_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `hotlinks`
--

INSERT INTO `hotlinks` (`hotlink_id`, `hotlink_name`, `hotlink_banlist_url`, `hotlink_main_url`, `hotlink_type`) VALUES
(1, 'kryptonia', 'https://kryptonia.fr/core/cache/sanctions_ban.json', 'https://kryptonia.fr', 'json'),
(2, 'senacraft', 'https://senacraft.fr/sanctions', 'https://senacraft.fr', 'personal'),
(3, 'bloodsymphony', 'https://bloodsymphony.com/banlist', 'https://bloodsymphony.com', 'litebans'),
(4, 'energyfight', 'https://energygaming.fr/banlist/energyfight', 'https://energygaming.fr', 'litebans'),
(5, 'energycheat', 'https://energygaming.fr/banlist/energycheat', 'https://energygaming.fr', 'litebans'),
(6, 'energyplay', 'https://energygaming.fr/banlist/energyplay', 'https://energygaming.fr', 'litebans'),
(7, 'energywild', 'https://energygaming.fr/banlist/energywild', 'https://energygaming.fr', 'litebans'),
(8, 'survivalworld', 'https://ban.survivalworld.fr', 'https://survivalworld.fr', 'fork_litebans');

-- --------------------------------------------------------

--
-- Structure de la table `kicks`
--

DROP TABLE IF EXISTS `kicks`;
CREATE TABLE IF NOT EXISTS `kicks` (
  `kick_id` int(7) NOT NULL AUTO_INCREMENT,
  `hotlink_id` int(7) NOT NULL,
  `kicked_name` varchar(45) NOT NULL,
  `kick_reason` text NOT NULL,
  `kicked_by` varchar(45) NOT NULL,
  `kick_at` int(11) NOT NULL,
  PRIMARY KEY (`kick_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3242 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `mutes`
--

DROP TABLE IF EXISTS `mutes`;
CREATE TABLE IF NOT EXISTS `mutes` (
  `mute_id` int(7) NOT NULL AUTO_INCREMENT,
  `hotlink_id` int(7) NOT NULL,
  `muted_name` varchar(45) NOT NULL,
  `mute_reason` text NOT NULL,
  `muted_by` varchar(45) NOT NULL,
  `mute_at` int(11) NOT NULL,
  PRIMARY KEY (`mute_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32282 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `warnings`
--

DROP TABLE IF EXISTS `warnings`;
CREATE TABLE IF NOT EXISTS `warnings` (
  `warn_id` int(7) NOT NULL AUTO_INCREMENT,
  `hotlink_id` int(7) NOT NULL,
  `warned_name` varchar(45) NOT NULL,
  `warn_reason` text NOT NULL,
  `warned_by` varchar(45) NOT NULL,
  `warn_at` int(11) NOT NULL,
  PRIMARY KEY (`warn_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11232 DEFAULT CHARSET=latin1;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
