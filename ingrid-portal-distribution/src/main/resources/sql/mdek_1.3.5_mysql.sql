--
-- Daten für Tabelle `help_messages`
--

UPDATE `help_messages` SET `name` = 'Zugangsbeschränkungen',
`help_text` = 'Das Feld „Zugangsbeschränkungen“ beschreibt, warum eine Zugriffsbeschränkung vorliegt. Bei frei nutzbaren Daten bzw. Services soll der Erfasser „keine“ auswählen. Sind die Beschränkungen unbekannt, soll vom Erfasser „unbekannt" eingetragen werden.' WHERE `id` = 320;

INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(322, 0, 10026, -1, 'de', 'Nutzungsbedingungen', 'Das Feld „Nutzungsbedingungen“ soll die Bedingungen zur Nutzung des beschriebenen Datensatzes bzw. des Services enthalten. Es sollen z.B. die Kosten für die Nutzung der Daten angegeben werden. Bei frei nutzbaren Daten bzw. Services soll der Erfasser „keine Einschränkungen“ laut Durchführungsbestimmungen“ eintragen. Sind die Bedingungen unbekannt, soll vom Erfasser „unbekannte Nutzungsbedingungen“ eingetragen werden.', '');
