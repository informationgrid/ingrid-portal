--
-- Daten für Tabelle `help_messages`
--

UPDATE info SET value_name = '4.0.1' WHERE  info.key_name = 'version';

-- Updated stuff for InGrid 4.0.1
-- ==============================

INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1600, 0, 5105, -1, 'de', 'Dateibeschreibung', 'Textliche Beschreibung des Inhalts der Grafik.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1601, 0, 5105, -1, 'en', 'File Description', 'Text description of the illustration.', '');