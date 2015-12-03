--
-- Daten für Tabelle `help_messages`
--

UPDATE `info` SET `value_name` = '3.6.2' WHERE  `info`.`key_name` = 'version';

-- Updated stuff for InGrid 3.6.1
-- ==============================

DELETE FROM help_messages WHERE gui_id = 3225;
INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(1422, 0, 3225, -1, 'de', 'Als ATOM-Download Dienst bereitstellen', 'Bei aktivierter Option, wird dieser Datensatz im Portal als Download angeboten. Zusätzlich wird die in den Katalogeinstellungen hinterlegte "ATOM-Downloadservice-URL" automatisch ins ISO-Format unter "distributionInfo/*/linkage" abgebildet.', ''),
(1423, 0, 3225, -1, 'en', 'Provide as ATOM-download service', 'If checked, this metadata will be available as a download in the portal. Additionally the "ATOM-Downloadservice-URL" from the catalog settings will be added automatically to the ISO-format under "distributionInfo/*/linkage".', '');