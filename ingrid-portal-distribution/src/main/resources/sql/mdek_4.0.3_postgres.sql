-- --------------------------------------------------------

UPDATE info SET value_name = '4.0.3' WHERE  info.key_name = 'version';

--
-- Daten für Tabelle 'help_messages'
--

INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1620, 0, 5170, -1, 'de', 'AdV-Produktgruppe', 'Zuordnung zu einer Produktgruppe bzw. Fachthema der AdV.', ''),
(1621, 0, 5170, -1, 'en', 'AdV-Product Group', 'Assignment to a product group or subject of the AdV.', ''),
(1630, 0, 6001, -1, 'de', 'INSPIRE-relevant konform', 'Geodatensatz wird an INSPIRE gemeldet und liegt im INSPIRE-DatenSchema vor. Der Grad der Konformität zur Spezifikation (VO 1089/2010) wird auf "true" gesetzt.', ''),
(1631, 0, 6001, -1, 'en', 'INSPIRE-relevant conform', 'Geo-Dataset is reported to INSPIRE and uses the INSPIRE-Data-Scheme. The degree of conformity to the specification (VO 1089/2010) will be set to "true".', ''),
(1632, 0, 6002, -1, 'de', 'INSPIRE-relevant nicht konform', 'Geodatensatz wird an INSPIRE gemeldet, liegt aber nicht im INSPIRE-DatenSchema vor. Der Grad der Konformität zur Spezifikation (VO 1089/2010) kann durch den Anwender nur auf "false" oder "nicht evaluiert" gesetzt werden.', ''),
(1633, 0, 6002, -1, 'en', 'INSPIRE-relevant not conform', 'Geo-Dataset is reported to INSPIRE but does not use the INSPIRE-Data-Scheme. The degree of conformity to the specification (VO 1089/2010) can only be set to "false" or "not evaluated".', ''),
(1634, 0, 6005, -1, 'de', 'AdV kompatibel', 'Beim Anhaken der Checkbox "AdV kompatibel" werden die Anforderungen des AdV-Metadatenprofils umgesetzt (z.B. Automatisiertes Setzen des Schlüsselwortes "AdVMIS" in der Datenbank).', ''),
(1635, 0, 6005, -1, 'en', 'AdV compatible', 'When checkbox "AdV compatible" is checked, then the requirements of the AdV-Metadata profiles are used (e.g. automatically set the keyword "AdVMIS" in the database).', ''),
(1636, 0, 10034, -1, 'de', 'Konformität', 'Hier muss angegeben werden, zu welcher Durchführungsbestimmung der INSPIRE-Richtlinie bzw. zu welcher anderweitigen Spezifikation die beschriebenen Daten konform sind. (INSPIRE-Pflichtfeld)<br/><br/>Dieses Feld wird bei Eintragungen in "INSPIRE-Themen" oder "Art des Dienstes" automatisch befüllt. Es muss dann nur der Grad der Konformität manuell eingetragen werden.<br/><br/>Bitte entsprechend den Empfehlungen des AdV-Metadatenprofils nur die Werte "konform" und "nicht konform" beim Feld "Grad der Konformität" verwenden.', ''),
(1637, 0, 10034, -1, 'en', 'Conformity', 'Specify the relevant specification (obligatory for INSPIRE-relevant metadata) and whether the dataset/service is conformant or not.<br/><br/>When inserting an entry in "INSPIRE-Topics" (Geodatensatz) or a "Type of Service" (Geodatendienst), the specification will be filled in automatically.<br/><br/>Please only use the values "conform" and "not conform" for the "Degree of Conformity"-field, according to the recommendations of the AdV-Metadata profiles.', '');
