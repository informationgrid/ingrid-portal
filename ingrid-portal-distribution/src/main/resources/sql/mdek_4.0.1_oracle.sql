--
-- Daten für Tabelle `help_messages`
--

UPDATE info SET value_name = '4.0.1' WHERE  info.key_name = 'version';

-- Updated stuff for InGrid 4.0.1
-- ==============================

DELETE FROM help_messages WHERE gui_id = 6010;
DELETE FROM help_messages WHERE gui_id = 3230;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1600, 0, 5105, -1, 'de', 'Dateibeschreibung', 'Textliche Beschreibung des Inhalts der Grafik.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1601, 0, 5105, -1, 'en', 'File Description', 'Text description of the illustration.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1410, 0, 6010, -1, 'de', 'Open Data', 'Diese Checkbox kennzeichnet dieses Objekt als "Open Data"-Objekt. Es sind dann zusätzlich folgende Angaben verpflichtend:<ul><li>In der (nur für Opendata) erscheinenden Tabelle "Kategorien" muß mindestens ein Wert eingetragen werden.</li><li>Unter Verweisen muß mindestens ein Verweis vom Typ "Datendownload" eingetragen werden.</li><li>Unter "Nutzungsbedingung" muß mindestens ein Eintrag vorhanden sein.</li></ul>', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1411, 0, 6010, -1, 'en', 'Open Data', 'This checkbox defines this object as an "open data"-object. The following inputs are mandatory:<ul><li>in the appearing table "Categories", at least one value must be entered</li><li>in the table "Links To", at least one link of type download must exist</li><li>in "Use Constraints", at least one entry must exist</li></ul>', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(49, 0, 3230, 3, 'de', 'Version', 'Angaben zu Version der dem Dienst zugrunde liegenden Spezifikation. Bitte alle Versionen eintragen, die vom Dienst unterstützt werden.', '"OGC:WMS 1.1.1", "OGC:WMS 1.3.0"');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1049, 0, 3230, 3, 'en', 'Service Version', 'Details on the version of the underlying base specification of the service. Please enter all versions supported by the service.', '"OGC:WMS 1.1.1", "OGC:WMS 1.3.0"');