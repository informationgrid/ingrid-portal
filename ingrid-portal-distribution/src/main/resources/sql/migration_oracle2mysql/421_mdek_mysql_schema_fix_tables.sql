-- Vor Migration Oracle nach MySQL das MySQL Schema auf den gleichen Stand wie Oracle bringen, damit Daten korrekt überführt werden (Spaltenreihenfolge ...)

DROP TABLE IF EXISTS `user_data`;
CREATE TABLE `user_data` (
  `id` bigint(20) NOT NULL,
  `version` int(11) NOT NULL DEFAULT '0',
  `addr_uuid` varchar(255) DEFAULT NULL,
  `portal_login` varchar(255) DEFAULT NULL,
  `plug_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
