UPDATE info SET value_name = '4.3.0' WHERE  info.key_name = 'version';

DELETE FROM help_messages WHERE gui_id = 10028;

INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(1670, 0, 10028, -1, 'de', 'Konformität (freie Eingabe)', 'Hier können frei vom Benutzer definierbar Konformitätsangaben eingegeben werden. Beispiele sind Konformitätsangaben, die nicht in INSPIRE definiert sind.', '');
INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(1671, 0, 10028, -1, 'en', 'Conformity (free entry)', 'Freely definable conformity statements can be entered in this field. Examples are conformity statements that are not defined in INSPIRE.', '');
