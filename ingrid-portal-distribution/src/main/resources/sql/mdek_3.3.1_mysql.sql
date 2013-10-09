--
-- Daten für Tabelle `help_messages`
--

UPDATE `info` SET `value_name` = '3.3.1' WHERE  `info`.`key_name` = 'version';

-- Uodated stuff for InGrid 3.3.1
-- ==============================

DELETE FROM help_messages WHERE gui_id = 5070;
DELETE FROM help_messages WHERE gui_id = 3535;
INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(140, 0, 5070, 1, 'de', 'Sachdaten / Attributinformation', 'Angabe der mit der Geo-Information/Karte verbundenen Sachdaten. Bei Bedarf kann hier eine Auflistung der Attribute des Datenbestandes erfolgen. Die hauptsächliche Nutzung dieses Feldes ist für digitale Geo-Informationen vorgesehen.<br/><strong>Achtung:</strong> Mit einem Eintrag unter Sachdaten/Attributinformation wird die Tabelle Schlüsselkatalog zum Pflichtfeld. Bitte geben Sie dort den Schlüsselkatalog an, welcher das eingetragene Attribut verzeichnet.', 'Baumkartei'),
(1140, 0, 5070, 1, 'en', 'Attributes', 'Details of the factual data connected with the geo-information/map. If needed, a listing of the attributes of the data inventory can be done here. The main use of this field is envisaged for digital geo-information.<br/><strong>Attention:</strong> The table Key Catalog will be mandatory if this table has an entry. Please enter the key catalogue which contains the entered attribute.', 'Tree index'),
(74, 0, 3535, 1, 'de', 'Schlüsselkatalog', 'An dieser Stelle besteht die Möglichkeit, den Daten zugrunde liegende Klassifizierungsschlüssel zu benennen. Dabei ist die Eingabe mehrerer Kataloge mit zugehörigem Datum (Pflichteintrag) und Version (Optional) möglich.<br/>Das Feld Schüsselkatalog wird zum Pflichtfeld, wenn in der Tabelle Sachdaten/Attributinformationen ein Eintrag vorgenommen wurde.', 'Biotoptypenschlüssel, Datum 01.01.1998, Version 1.1'),
(1074, 0, 3535, 1, 'en', 'Key Catalogue', 'In this place there is the possibility of naming the classification keys upon which the data are based. In doing this, the input of several catalogues with the associated date (obligatory entry) and version (optional) is possible.<br/>The field Key Catalogue will be mandatory if the tabel Attributes contains an entry.', 'Type: Biotop type key; Date 01.01.1998; Version 1.1'),
(32, 0, 2240, -1, 'de', 'Dateiformat', 'Angabe des Dateiformats - es sind beliebige Einträge möglich z.B. JPG, HTML, XLS oder TXT', ''),
(1032, 0, 2240, -1, 'en', 'File Format', 'Information on the data format ', '');
