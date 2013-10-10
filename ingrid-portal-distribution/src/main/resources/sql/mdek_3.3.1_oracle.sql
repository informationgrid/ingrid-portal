--
-- Daten für Tabelle help_messages
--

UPDATE info SET value_name = '3.3.1' WHERE  info.key_name = 'version';

-- Uodated stuff for InGrid 3.3.1
-- ==============================

DELETE FROM help_messages WHERE gui_id = 5070;
DELETE FROM help_messages WHERE gui_id = 3535;
DELETE FROM help_messages WHERE gui_id = 1500;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(140, 0, 5070, 1, 'de', 'Sachdaten / Attributinformation', 'Angabe der mit der Geo-Information/Karte verbundenen Sachdaten. Bei Bedarf kann hier eine Auflistung der Attribute des Datenbestandes erfolgen. Die hauptsächliche Nutzung dieses Feldes ist für digitale Geo-Informationen vorgesehen.<br/><strong>Achtung:</strong> Mit einem Eintrag unter Sachdaten/Attributinformation wird die Tabelle Schlüsselkatalog zum Pflichtfeld. Bitte geben Sie dort den Schlüsselkatalog an, welcher das eingetragene Attribut verzeichnet.', 'Baumkartei');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1140, 0, 5070, 1, 'en', 'Attributes', 'Details of the factual data connected with the geo-information/map. If needed, a listing of the attributes of the data inventory can be done here. The main use of this field is envisaged for digital geo-information.<br/><strong>Attention:</strong> The table Key Catalog will be mandatory if this table has an entry. Please enter the key catalogue which contains the entered attribute.', 'Tree index');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(74, 0, 3535, 1, 'de', 'Schlüsselkatalog', 'An dieser Stelle besteht die Möglichkeit, den Daten zugrunde liegende Klassifizierungsschlüssel zu benennen. Dabei ist die Eingabe mehrerer Kataloge mit zugehörigem Datum (Pflichteintrag) und Version (Optional) möglich.<br/>Das Feld Schüsselkatalog wird zum Pflichtfeld, wenn in der Tabelle Sachdaten/Attributinformationen ein Eintrag vorgenommen wurde.', 'Biotoptypenschlüssel, Datum 01.01.1998, Version 1.1');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1074, 0, 3535, 1, 'en', 'Key Catalogue', 'In this place there is the possibility of naming the classification keys upon which the data are based. In doing this, the input of several catalogues with the associated date (obligatory entry) and version (optional) is possible.<br/>The field Key Catalogue will be mandatory if the tabel Attributes contains an entry.', 'Type: Biotop type key; Date 01.01.1998; Version 1.1');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(32, 0, 2240, -1, 'de', 'Dateiformat', 'Angabe des Dateiformats - es sind beliebige Einträge möglich z.B. JPG, HTML, XLS oder TXT', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1032, 0, 2240, -1, 'en', 'File Format', 'Information on the data format ', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(23, 0, 1500, -1, 'de', 'Verweis zu', 'Es gibt die Möglichkeit, Verweise von einem Objekt zu einem anderen Objekt oder zu einer WWW-Adresse (URL) zu erstellen. In dieser Tabelle werden alle Verweise zusammenfassend aufgeführt, welche im aktuellen Objekt angelegt wurden. Über dem Link "Verweise anlegen/bearbeiten" öffnet sich ein Dialog, mit dem weitere Einzelheiten zu den Verweisen eingesehen und editiert werden können. Es ist ferner möglich, weitere Verweise über diesen Dialog hinzuzufügen. Wenn Open-Data ausgewählt ist, muss mindestens ein Verweis vom Typ "Datendownload" vorhanden sein, bevor das Objekt veröffentlicht werden kann!', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1023, 0, 1500, -1, 'en', 'Links To', 'There is the possibility of creating references from one object to another object or to a www-address (URL). In this table all references are listed that were created in the current object as a summary. A dialogue box opens via the link "create/edit references" with which further details on the references can be viewed and edited. Additionally, it is possible to add further references via this dialogue box. When this is an open-data object, then there must be at least on reference of type "Download of data" to be able to publish the object.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1410, 0, 6010, -1, 'de', 'Open Data', 'Diese Checkbox definiert dieses Objekt als "Open Data"-Objekt. Ist diese aktiviert, muss in der erscheinenden Tabelle "Kategorien" mindestens ein Wert eingetragen werden, bevor das Objekt veröffentlicht werden kann. Des Weiteren wird in den Nutzungsbedingungen die Codeliste für Lizenzen verwendet.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1411, 0, 6010, -1, 'en', 'Open Data', 'This checkbox defines this object as an "open data"-object. When activated, the appearing table "Categories" must contain at least one value before the object can be published.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1412, 0, 6020, -1, 'de', 'Kategorien', 'Die Kategorien enthalten eine Auswahl, die das Open Data - Objekt näher bestimmen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1413, 0, 6020, -1, 'en', 'Categories', 'The categories contain a choice, which are used to describe the open data object more detailed', '');