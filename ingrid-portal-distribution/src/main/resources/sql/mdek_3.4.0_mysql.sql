--
-- Daten für Tabelle `help_messages`
--

UPDATE `info` SET `value_name` = '3.4' WHERE  `info`.`key_name` = 'version';

-- Uodated stuff for InGrid 3.4
-- ============================

DELETE FROM help_messages WHERE gui_id = 8061;
INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(281, 0, 8061, -1, 'de', 'GetCapabilities Assistent - Dienst-URL', 'Eintrag der Dienst-URL des entsprechenden Dienstes. Die Parameter REQUEST und SERVICE werden automatisch ergänzt, können aber auch explizit mit angegeben werden.', 'http://www.portalu.de/csw202?SERVICE=CSW'),
(1281, 0, 8061, -1, 'en', 'Service URL', 'Enter the getCapability-URL of the corresponding service. The parameters REQUEST and SERVICE are optional and will be added automatically. However it is possible to set them explicitly.', 'http://www.portalu.de/csw202?SERVICE=CSW');
