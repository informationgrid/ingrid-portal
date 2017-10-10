UPDATE info SET value_name = '4.0.4' WHERE  info.key_name = 'version';

DELETE FROM help_messages WHERE gui_id = 10025;

INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(320, 0, 10025, -1, 'de', 'Zugriffsbeschränkungen', 'Das Feld „Zugriffsbeschränkungen“ beschreibt, die Art der Zugriffsbeschränkung. Bei frei nutzbaren Daten bzw. Services soll der Eintrag „keine“ ausgewählt werden (ISO: accessConstraints).', '');