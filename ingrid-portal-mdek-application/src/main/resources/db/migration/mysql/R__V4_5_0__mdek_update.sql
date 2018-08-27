UPDATE info SET value_name = '4.5.0' WHERE  info.key_name = 'version';

UPDATE help_messages
SET help_text = 'Angabe des Formats der Daten in DV-technischer Hinsicht, in welchem diese verfügbar sind. Das Format wird durch 4 unterschiedliche Eingaben spezifiziert. Der Name muss ausgefüllt werden. Name: Angabe des Formatnamens, wie z.B. "Date" Version: Version der verfügbaren Daten (z.B. "Version 8" oder "Version vom 8.11.2001") Kompressionstechnik: Kompression, in welcher die Daten geliefert werden (z.B. "WinZip", "keine") Bildpunkttiefe: BitsPerSample.'
WHERE gui_id = 1320 AND language = 'de';

