--
-- Daten für Tabelle `help_messages`
--

UPDATE `info` SET `value_name` = '4.0.3' WHERE  `info`.`key_name` = 'version';

-- Updated stuff for InGrid 4.0.3
-- ==============================

INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(1620, 0, 5170, -1, 'de', 'AdV-Produktgruppe', '', ''),
(1621, 0, 5170, -1, 'en', 'AdV-Product Group', '', '');
(1630, 0, 6001, -1, 'de', 'INSPIRE-relevant konform', 'Geodatensatz wird an INSPIRE gemeldet und liegt im INSPIRE-DatenSchema vor. Der Grad der Konformität zur Spezifikation (VO 1089/2010) wird auf "true" gesetzt.', ''),
(1631, 0, 6001, -1, 'en', 'INSPIRE-relevant conform', 'Geo-Dataset is reported to INSPIRE and uses the INSPIRE-Data-Scheme. The degree of conformity to the specification (VO 1089/2010) will be set to "true".', ''),
(1632, 0, 6002, -1, 'de', 'INSPIRE-relevant nicht konform', 'Geodatensatz wird an INSPIRE gemeldet, liegt aber nicht im INSPIRE-DatenSchema vor. Der Grad der Konformität zur Spezifikation (VO 1089/2010) kann durch den Anwender nur auf "false" oder "nicht evaluiert" gesetzt werden.', ''),
(1633, 0, 6002, -1, 'en', 'INSPIRE-relevant not conform', 'Geo-Dataset is reported to INSPIRE but does not use the INSPIRE-Data-Scheme. The degree of conformity to the specification (VO 1089/2010) can only be set to "false" or "not evaluated".', '');
