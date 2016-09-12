--
-- Daten für Tabelle `help_messages`
--

UPDATE info SET value_name = '4.1.0' WHERE  info.key_name = 'version';

-- Updated stuff for InGrid 3.6.1
-- ==============================

INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1550, 0, 6005, -1, 'de', 'AdV Kompatibilität', 'Bei Aktivierung wird automatisch das Schlüsselwort "AdVMIS" hinzugefügt. Außerdem wird beim Veröffentlichen überprüft, dass die Ansprechpartner ein Verwaltungsgebiet gesetzt haben.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1551, 0, 6005, -1, 'en', 'AdV compatibility', 'When activated the keyword "AdVMIS" is automatically added. Moreover during publishing, the "Point of Contact" must have an administrative area set.', '');
