--
-- Daten für Tabelle `help_messages`
--

UPDATE `info` SET `value_name` = '3.3.2' WHERE  `info`.`key_name` = 'version';

-- Uodated stuff for InGrid 3.3.2
-- ==============================
INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(1420, 0, 8091, -1, 'de', 'ATOM-Downloadservice-URL', 'Die hier eingetragene URL wird im Portal verwendet, um einen Metadatensatz als ATOM-Feed herunterzuladen. Dieser Metadatensatz muss hierfür explizit für den Download freigeschaltet sein, wofür die Checkbox "Als ATOM-Download Dienst bereitstellen" zuständig ist!', 'http://portalu.de/dls/'),
(1421, 0, 8091, -1, 'en', 'ATOM-Downloadservice-URL', 'This URL is used in the portal to download metadata as an ATOM-feed. For this functionality this metadata has to be explicitly marked with "Provide as ATOM-download service!', 'http://portalu.de/dls/'),
(1422, 0, 3225, -1, 'de', 'Als ATOM-Download Dienst bereitstellen', 'Bei aktivierter Option, wird dieser Datensatz im Portal als Download angeboten.', ''),
(1423, 0, 3225, -1, 'en', 'Provide as ATOM-download service', 'If checked, this metadata will be available as a download in the portal.', '');