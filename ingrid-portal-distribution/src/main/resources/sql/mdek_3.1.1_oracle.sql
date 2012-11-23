--
-- Daten für Tabelle help_messages
--

-- Field 'environment categories' removed with InGrid 3.1.1, also remove help
DELETE FROM help_messages WHERE gui_id = 10016;

-- Update help of enclosing "frame"
DELETE FROM help_messages WHERE gui_id = 10014;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(314, 0, 10014, -1, 'de', 'Umweltthemen', 'Die Verschlagwortung mit den Begriffen der Umweltbereiche soll dazu dienen, eine Zuordnung der Objekte zu grob eingeteilten Umweltthemen herzustellen. Diese Themen sind in den Auswahllisten enthalten. Es können mehrere Einträge vorgenommen werden. Über das Auswahlfeld "Als Katalogseite anzeigen" kann entschieden werden, ob das Objekt als Katalogseite verwendet werden soll.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1314, 0, 10014, -1, 'en', 'Environmental', '"The use of environmental keywords is to generate an allocation of the objects to roughly allocated topics. These topics are contained in the drop down lists. Multiple entries can be made. Via the ""show as catalogue page"" it can be decided whether the object is to be used as a catalogue page in the portal."', '');
