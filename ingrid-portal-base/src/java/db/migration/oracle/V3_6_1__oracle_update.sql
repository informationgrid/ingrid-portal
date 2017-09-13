--
-- Daten für Tabelle help_messages
--

UPDATE info SET value_name = '3.6.1' WHERE  info.key_name = 'version';

-- Updated stuff for InGrid 3.6.1
-- ==============================

DELETE FROM help_messages WHERE gui_id = 6010;
DELETE FROM help_messages WHERE gui_id = 10026;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(322, 0, 10026, -1, 'de', 'Nutzungsbedingungen', 'Einschränkungen, die die Eignung der Ressource oder Metadaten betreffen (ISO: useLimitation)', 'nicht für Navigationszwecke geeignet');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1322, 0, 10026, -1, 'en', 'Use Limitations', 'Limitation affecting the fitness for use of the resource or metadata (ISO: useLimitation)', 'not to be used for navigation');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1530, 0, 10027, -1, 'de', 'Nutzungsbeschränkungen', 'Einschränkungen zum Schutz der Privatsphäre oder des geistigen Eigentums sowie andere besondere Einschränkungen oder Warnungen bezüglich der Nutzung der Ressource oder der Metadaten (ISO: useConstraints)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1531, 0, 10027, -1, 'en', 'Use Constraints', 'Constraints applied to assure the protection of privacy or intellectual property, and any special restrictions or limitations or warnings on using the resource or metadata (ISO: useConstraints)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1410, 0, 6010, -1, 'de', 'Open Data', 'Diese Checkbox kennzeichnet dieses Objekt als "Open Data"-Objekt. Es sind dann zusätzlich folgende Angaben verpflichtend:<ul><li>In der (nur für Opendata) erscheinenden Tabelle "Kategorien" muß mindestens ein Wert eingetragen werden.</li><li>Unter Verweisen muß mindestens ein Verweis vom Typ "Datendownload" eingetragen werden.</li></ul>', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1411, 0, 6010, -1, 'en', 'Open Data', 'This checkbox defines this object as an "open data"-object. The following inputs are mandatory:<ul><li>in the appearing table "Categories", at least one value must be entered</li><li>in the table "Links To", at least one link of type download must exist</li></ul>', '');
