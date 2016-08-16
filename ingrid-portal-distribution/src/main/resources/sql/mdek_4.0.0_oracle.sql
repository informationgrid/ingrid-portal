--
-- Daten für Tabelle `help_messages`
--

UPDATE info SET value_name = '4.0.0' WHERE  info.key_name = 'version';

-- Updated stuff for InGrid 3.6.1
-- ==============================

INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1540, 0, 4450, -1, 'de', 'Servicezeiten', 'Zeitraum inklusive der Zeitzone, wann die verantwortliche Person oder Organisation erreicht werden kann.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1541, 0, 4450, -1, 'en', 'Hours Of Service', 'Time period (including time zone) when individuals can contact the organization or individual.', '');
