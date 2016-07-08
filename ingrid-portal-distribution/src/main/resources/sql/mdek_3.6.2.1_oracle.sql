--
-- Daten für Tabelle help_messages
--

UPDATE info SET value_name = '3.6.2.1' WHERE  info.key_name = 'version';

-- Updated stuff for InGrid 3.6.2
-- ==============================

DELETE FROM help_messages WHERE gui_id = 8071;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(290, 0, 8071, -1, 'de', 'Allgemeiner Erfassungsassistent - Freier-Raumbezug', 'Auswahl, welche der vorgeschlagenen räumlichen Begriffe in den freien Raumbezug des Objekts übernommen werden sollen. Durch einen Klick in die erste Spalte der Tabelle, kann über eine Checkbox der zu übernehmende Begriff ausgewählt werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1290, 0, 8071, -1, 'en', 'Custom Location', 'Selection, which spatial term shall be used as custom location within the object. By clicking in the first column of the table you can activate a checkbox for selection of the chosen term.', '');
