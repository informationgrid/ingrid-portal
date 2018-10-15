UPDATE info SET value_name = '4.5.0' WHERE  info.key_name = 'version';

UPDATE help_messages
SET help_text = 'Angabe des Formats der Daten in DV-technischer Hinsicht, in welchem diese verfügbar sind. Das Format wird durch 4 unterschiedliche Eingaben spezifiziert. Der Name muss ausgefüllt werden. Name: Angabe des Formatnamens, wie z.B. "Date" Version: Version der verfügbaren Daten (z.B. "Version 8" oder "Version vom 8.11.2001") Kompressionstechnik: Kompression, in welcher die Daten geliefert werden (z.B. "WinZip", "keine") Bildpunkttiefe: BitsPerSample.'
WHERE gui_id = 1320 AND language = 'de';

INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1700, 0, 1001, -1, 'de', 'Identifikator des übergeordneten Metadatensatzes', 'Für Datensätze in der obersten Ebene oder direkt unter einem Ordner, kann eine zusätzliche Referenz auf einen übergeordneten Metadatensatz vergeben werden. Dadurch ist es möglich, auch auf externe Datensätze zu verweisen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1701, 0, 1001, -1, 'en', 'Parent Identifier', 'A separate parent identifier can be given to datasets that are on the root level or directly inside a folder. So it is possible to reference external datasets as a parent.', '');