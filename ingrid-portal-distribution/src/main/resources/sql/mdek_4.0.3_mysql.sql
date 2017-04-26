--
-- Daten für Tabelle `help_messages`
--

UPDATE `info` SET `value_name` = '4.1.0' WHERE  `info`.`key_name` = 'version';

-- Updated stuff for InGrid 3.6.1
-- ==============================

INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(1620, 0, 5170, -1, 'de', 'AdV-Produktgruppe', '', ''),
(1621, 0, 5170, -1, 'en', 'AdV-Product Group', '', '');
