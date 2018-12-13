-- Vor Migration Oracle nach MySQL das MySQL Schema auf den gleichen Stand wie Oracle bringen, damit Daten korrekt überführt werden (Spaltenreihenfolge ...)

DROP TABLE IF EXISTS `ingrid_principal_pref`;
CREATE TABLE `ingrid_principal_pref` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `principal_name` varchar(251) NOT NULL DEFAULT '',
  `pref_name` varchar(251) NOT NULL DEFAULT '',
  `modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `pref_value` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ingrid_rss_store`;
CREATE TABLE `ingrid_rss_store` (
  `author` varchar(1023) DEFAULT NULL,
  `categories` varchar(255) DEFAULT NULL,
  `copyright` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `published_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `title` varchar(1023) DEFAULT NULL,
  `description` mediumtext,
  `link` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`link`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

