--
-- Daten für Tabelle `help_messages`
--

UPDATE `info` SET `value_name` = '3.4' WHERE  `info`.`key_name` = 'version';

-- Uodated stuff for InGrid 3.4
-- ============================

DELETE FROM help_messages WHERE gui_id = 8061;
DELETE FROM help_messages WHERE gui_id = 5064;
DELETE FROM help_messages WHERE gui_id = 10024;
DELETE FROM help_messages WHERE gui_id = 1315;
DELETE FROM help_messages WHERE gui_id = 3220;
INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(281, 0, 8061, -1, 'de', 'GetCapabilities Assistent - Dienst-URL', 'Eintrag der Dienst-URL des entsprechenden Dienstes. Die Parameter REQUEST und SERVICE werden automatisch ergänzt, können aber auch explizit mit angegeben werden.', 'http://www.portalu.de/csw202?SERVICE=CSW'),
(1281, 0, 8061, -1, 'en', 'Service URL', 'Enter the getCapability-URL of the corresponding service. The parameters REQUEST and SERVICE are optional and will be added automatically. However it is possible to set them explicitly.', 'http://www.portalu.de/csw202?SERVICE=CSW'),
(137, 0, 5064, -1, 'de', 'INSPIRE-Themen', 'Auswahl eines INSPIRE Themengebiets zur Verschlagwortung des Datensatzes (INSPIRE-Pflichtfeld).<br/><br/>Bei Eintragung oder Löschen eines INSPIRE-Themas werden in den Pflichtfeldern Konformität/Spezifikation und Kodierungsschema automatisch Einträge vorgenommen bzw. entfernt. ', 'Boden (automatischer Eintrag von "INSPIRE Data Specification on Soil - Draft Technical Guidelines" in Konformität/Spezifikation)'),
(1137, 0, 5064, -1, 'en', 'INSPIRE-Topics', 'Selection of an INSPIRE subject area for providing keywords from the data set (INSPIRE-obligatory field).<br/><br/>Inserting or deleting an entry here will cause an automatic insertion or deletion of a corresponding entry in "Conformity/Specification of Conformity".', 'Soil (automatic insertion of "INSPIRE Data Specification on Soil - Draft Technical Guidelines" in Conformity/Specification of Conformity)'),
(319, 0, 10024, -1, 'de', 'Konformität', 'Hier muss angegeben werden, zu welcher Durchführungsbestimmung der INSPIRE-Richtlinie bzw. zu welcher anderweitigen Spezifikation die beschriebenen Daten konform sind. (INSPIRE-Pflichtfeld)<br/><br/>Dieses Feld wird bei Eintragungen in "INSPIRE-Themen" oder "Art des Dienstes" automatisch befüllt. Es muss dann nur der Grad der Konformität manuell eingetragen werden.', ''),
(1319, 0, 10024, -1, 'en', 'Conformity', 'Specify the relevant specification (obligatory for INSPIRE-relevant metadata) and whether the dataset/service is conformant or not.<br/><br/>When inserting an entry in "INSPIRE-Topics" (Geodatensatz) or a "Type of Service" (Geodatendienst), the specification will be filled in automatically.', ''),
(345, 0, 1315, -1, 'de', 'Kodierungsschema der geographischen Daten', 'Kodierung: Beschreibung des Datenformats (zum Beispiel eine Markup-Sprache wie GML), in welchem die beschriebenen Daten kodiert sind.<br/><br/>Bei Eintragung oder Löschung in "INSPIRE-Themen" (Geodatensatz) werden in diesem Feld korrespondierende Einträge automatisch vorgenommen oder entfernt.', 'Hydrography GML application schema (automatischer Eintrag bei Auswahl des INSPIRE-Themas Gewäsernetz)'),
(1424, 0, 1315, -1, 'en', 'Encoding scheme geographical data', 'Specify the dataformat of the data. Inserting or deleting an entry in "INSPIRE-Topics" (Geodatensatz) will cause an automatic insertion or deletion of an corresponding entry here.', 'Hydrography GML application schema (automatic insertion when selecting INSPIRE-Topic "Hydrography")'),
(48, 0, 3220, 3, 'de', 'Art des Dienstes', 'In diesem Pflichtfeld kann die Art des Dienstes ausgewählt werden. Über das Feld werden die zur weiteren Befüllung auszuwählenden Angaben zu Operationen gesteuert.<br/><br/>Bei Eintragungen bzw. Änderungen dieses Feldes werden in der Tabelle Konformität die Einträge für die zugehörige Spezifikation automatisch gesetzt (gilt nicht für alle Dienstarten).', 'Darstellungsdienst (automatischer Eintrag "Technical Guidance for the implementation of INSPIRE View Services" in Konformität/Spezifikation)'),
(1048, 0, 3220, 3, 'en', 'Type of Service', 'This mandatory field specifies the type of the service. The input of this field controls the information that has to be provided for the operations of the service.<br/><br/>Inserting or deleting an entry here will cause an automatic insertion or deletion of an corresponding entry in "Conformity/Specification of Conformity" (not for all types of services).', 'View Service (automatic insertion of "Technical Guidance for the implementation of INSPIRE View Services" in Conformity/Specification of Conformit).');