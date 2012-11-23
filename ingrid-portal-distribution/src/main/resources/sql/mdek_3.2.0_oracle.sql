--
-- Daten für Tabelle help_messages
--

-- Create table for storing version info
CREATE TABLE info (
  key_name varchar2(255) default NULL,
  value_name varchar2(255) default NULL
);

INSERT INTO info (key_name, value_name) VALUES
('version', '3.2');

-- Fixes for InGrid 3.0.1
-- ======================

-- "Basisdaten" vice versa in different classes, see INGRID31-28
-- Dokument/Bericht/Literatur
DELETE FROM help_messages WHERE gui_id = 3210;
-- Geodatendienst
DELETE FROM help_messages WHERE gui_id = 3345;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(57, 0, 3210, 2, 'de', 'Basisdaten', 'Verweis auf zugrunde liegende Daten. Hier sollen Verweise zu anderen Objekten dieses Katalogs gelegt werden, die Auskunft über Herkunft und Art der zugrunde liegenden Daten geben. Es kann über den Link (Verweis anlegen/bearbeiten) ein neuer Verweis angelegt werden.', 'Deponieüberwachung Berlin-Tegel, Statistikauswertungen seit 1974');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1057, 0, 3210, 2, 'en', 'General', 'Reference to underlying base data. Here references to other objects in this catalogue should be given, which give information about the origin and the type of the underlying base data. Using the register card ', 'Berlin-Tegel Landfill Monitoring, Statistical evalustions since 1974');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(47, 0, 3345, 3, 'de', 'Basisdaten', 'Herkunft und Art der zugrundeliegenden Daten. Bei einem OGC Web Service können Verweise auf ein oder mehrere Objekte eingefügt werden, die mit dem Dienst eng verknüpft ("tightly coupled") sind. Im Allgemeinen sind dies die Datensätze, auf die der Dienst aufgesetzt ist. Bitte verwenden Sie hierzu möglichst einen "Verweis zu Dienst", der in dem die Basisdaten beschreibenden Objekt eingetragen wird (Klasse Geoinformation/Karte). Allgemein sollen die Herkunft oder die Ausgangsdaten der Daten beschrieben werden, die in dem Dienst benutzt, werden. Zusätzlich kann die Art der Daten (z. B. digital, automatisch ermittelt oder aus Umfrageergebnissen, Primärdaten, fehlerbereinigte Daten) angegeben werden. Der Eintrag kann hier direkt über die Auswahl der Registerkarte "Text" erfolgen oder es können Verweise eingetragen werden, indem der Link "Verweis anlegen/bearbeiten" angewählt wird.', 'Messdaten von Emissionen in bestimmten Betrieben bzw. Einfügen eines Verweises auf den "tightly coupled" OGC Web-Dienst');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1047, 0, 3345, 3, 'en', 'General', 'Origin and type of the underlying data. With an OGC web service, references to one or several objects can be inserted that are tightly coupled with this service. In general, these are the data sets on which the service is based. For this, if possible, please use a "reference to service", which is entered in the object describing the base data (class Geoinformation / Map). In general, the origin or the start data of the data are described, which are used in the service. In addition, the type of the data (e.g. digital, automatically produced or taken from survey results, primary data, error-adjusted data). The entry here can take place directly via the selection of the "text" register card or references can be entered by selecting the link "Add/Edit Link".', '"Readings for emissions in certain firms and/or the insertion of a reference to the ""tightly coupled"" OGC web service."');

-- add missing german help, got lost in 3.0.1 (added in 3.0.0)
DELETE FROM help_messages WHERE id = 368;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(368, 0, 3000, 6, 'de', 'Objektname', 'Angabe einer kurzen prägnanten Bezeichnung des beschriebenen Dienstes / der beschriebenen Anwendung / des beschriebenen Informationssystems. Der Objektname kann z.B. identisch mit dem Namen des Dienstes /der Anwendung /des Informationssystems sein, sofern dieser ausreichend kurz und aussagekräftig ist. Soweit ein gängiges Kürzel vorhanden ist, ist dieses Kürzel mit anzugeben. Der Eintrag in dieses Feld ist obligatorisch.', 'Emissions-Fernüberwachung EFÜ');
DELETE FROM help_messages WHERE id = 369;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(369, 0, 6000, -1, 'de', 'INSPIRE-relevanter Datensatz', 'Dieses Feld definiert, wenn aktiviert, dass ein Metadatensatz für das INSPIRE-Monitoring vorgesehen ist.', '');

-- add missing english help
DELETE FROM help_messages WHERE id = 1367;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1367, 0, 10156, -1, 'en', 'Allow Custom Entries', 'If this option is activated it is also possible to add a free entry to the code list. In the other case only an entry of the code list can be chosen. The entries can be done in different languages, so by changing the language in the InGrid-Editor the list entry is adapted.', '');
DELETE FROM help_messages WHERE id = 1369;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1369, 0, 6000, -1, 'en', 'INSPIRE-relevant Record', 'If clicked, this record is relevant for INSPIRE-Monitoring.', '');

-- repair, wrong help for gui_id 10100
DELETE FROM help_messages WHERE id = 346;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(346, 0, 10100, -1, 'de', 'Typ', 'Der Typ bestimmt die Art und Funktionsweise des Feldes. Es stehen zur Auswahl: Textfeld, Nummernfeld, Datum, Auswahlliste und Tabelle.', '');
DELETE FROM help_messages WHERE id = 1346;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1346, 0, 10100, -1, 'en', 'Type', 'The type of the field.', '');

-- repair, wrong help for gui_id 10153
DELETE FROM help_messages WHERE id = 1364;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1364, 0, 10153, -1, 'en', 'Entries', 'Here the entries for the selection lists are defined. The ID is created automatically. It is necessary to guarantee a correct link to the different language versions of a list entry. Furthermore it is possible to change the order of the entries by click into the first column of the table and pull the entry to the desired position.', '');

-- repair, wrong help for gui_id 10112
DELETE FROM help_messages WHERE id = 1358;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1358, 0, 10112, -1, 'en', 'Number of table rows', 'The number of displayed rows of the table.', '4');


-- Changes for InGrid 3.2
-- ======================

-- "Datenqualität (Pkt 6 d. Protokoll AK-IGE)" see INGRID32-48
-- DQ table "Datendefizit" removed, instead field "Datendefizit" (3565)
DELETE FROM help_messages WHERE gui_id = 7510;
-- DQ table "Absolute Positionsgenauigkeit" removed, instead fields "Höhengenauigkeit" (5069) and "Lagegenauigkeit" (3530)
DELETE FROM help_messages WHERE gui_id = 7517;

-- NEW Table "Datensammlung/Datenbank - Objektartenkatalog" see INGRID32-50
DELETE FROM help_messages WHERE gui_id = 3109;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1372, 0, 3109, 5, 'de', 'Objektartenkatalog', 'An dieser Stelle besteht die Möglichkeit, den Daten zugrunde liegende Klassifizierungsschlüssel zu benennen. Dabei ist die Eingabe mehrerer Kataloge mit zugehörigem Datum (Pflichteintrag) und Version (Optional) möglich.', 'Biotoptypenschlüssel, Datum 01.01.1998, Version 1.1');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1373, 0, 3109, 5, 'en', 'Key Catalogue', 'In this place there is the possibility of naming the classification keys upon which the data is based. In doing this, the input of several catalogues with the associated date (obligatory entry) and version (optional) is possible.', 'Type: Biotop type key; Date 01.01.1998; Version 1.1');

-- "Kontaktdaten (Pkt 4 d. Protokoll AK-IGE)" see INGRID32-46
-- "Verantwortlicher" (1030) new help
DELETE FROM help_messages WHERE gui_id = 1030;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(9, 0, 1030, -1, 'de', 'Verantwortlicher', 'Eintrag des Adressverweises zur Person oder Institution, welche für diesen Metadatensatz verantwortlich und auskunftsfähig ist.', 'Mustermann, Erika');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1009, 0, 1030, -1, 'en', 'Responsible user', 'Person or Institution that is responsible for this metadata record. The email address is used as metadata point of contact.', '"Sample, Erica"');
-- "Adressen" (1000) new help
DELETE FROM help_messages WHERE gui_id = 1000;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1, 0, 1000, -1, 'de', 'Adressen', 'Eintrag von Adressverweisen zu Personen oder Institutionen, welche weitergehende Informationen zum aktuellen Objekt/zu den beschriebenen Daten geben können. Bei Bedarf können diese Verweise verändert werden.<br/><br/>In der ersten Spalte wird jeweils die Art des Verweises eingetragen (=Rolle der Person oder Institution), z.B. Ansprechpartner, Verwalter, Eigentümer ect.<br/>Über den Link "Adresse hinzufügen" wird der Verweis selbst angelegt.<br/>Als Auswahlmöglichkeit stehen alle in der Adressverwaltung des aktuellen Kataloges bereits eingetragenen Personen zur Verfügung.<br/>Ein Eintrag in der ersten Zeile ist obligatorisch.', 'Verwalter / Robbe, Antje<br/>Ansprechpartner / Dr. Seehund, Siegfried');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1001, 0, 1000, -1, 'en', 'Addresses', 'Input of address references to persons or institutions, which can provide further information on the current object. If needed, these references can be amended.<br/>In the first column the type of reference is entered respectively (custodian, owner, publisher etc.).<br/>The reference is itself created via the "add address" link.<br/>All the persons already entered in the address manager of the current catalogue are available for selection.<br/>Making an entry in the first line is obligatory.', 'Custodian / Sam Sample<br/>Point of Contact / Jane Sample');

-- "Automatische Befüllung des Feldes 'Identifikator der Datenquelle'" see INGRID32-30
-- NEW Field "Katalogverwaltung - Katalogeinstellungen - Namensraum"
DELETE FROM help_messages WHERE gui_id = 8100;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1374, 0, 8100, -1, 'de', 'Namensraum des Katalogs', 'Der Namensraum des Katalogs, der bei der Abgabe der Daten verwendet wird (CSW, Open Search).', 'http://portalu.de/igc_ni');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1375, 0, 8100, -1, 'en', 'Catalogue Namespace', 'The namespace of the catalogue which is used when exporting data (CSW, Open Search).', 'http://portalu.de/igc_ni');
-- "Geo-Information/Karte - Fachbezug - Identifikator der Datenquelle" new Help
DELETE FROM help_messages WHERE gui_id = 10021;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(317, 0, 10021, -1, 'de', 'Identifikator der Datenquelle', 'Hier muss ein eindeutiger Name (Identifikator) für jede im Datenobjekt beschriebene Datenquelle (z.B. eine Karte) vergeben werden. (INSPIRE-Pflichtfeld).<br/><br/>Mit Hilfe des Buttons "Erzeuge ID" kann eine UUID als Identifikator in dieses Feld eingetragen werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1317, 0, 10021, -1, 'en', 'Identification', 'Here a unique identification has to be given for every data source described in the data object (e.g. a map). (INSPIRE-obligatory field)<br/><br/>Using the button "Create ID" a UUID can be generated and inserted into this form element.', '');

-- "Verbesserung der Ergonomie des Eintrags von Verweisen" see INGRID32-27
-- Dialog "Verweis anlegen":
-- "Verweis von", new Help
DELETE FROM help_messages WHERE gui_id = 7034;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(182, 0, 7034, -1, 'de', 'Verweis von', 'Bestehenden Verweis editieren oder neuen Verweis anlegen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1182, 0, 7034, -1, 'en', 'Link From', 'Edit existing reference or create a new reference.', '');
-- "Verweistyp" (former "Feldname/Bezeichnung der Verweisbeziehung"), new Help
DELETE FROM help_messages WHERE gui_id = 2000;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(25, 0, 2000, -1, 'de', 'Verweistyp', 'Angabe des fachlichen Bezuges, der zwischen dem aktuellen Objekt und dem Verweisobjekt (anderes Objekt, URL, OLE-Objekt) besteht. Wird der Verweis-Dialog in der Rubrik Verweise geöffnet, so kann über das Dropdown-Menu (ausklappbar über den Pfeil an der rechten Seite des Feldes) aus einer Auswahlliste ein Eintrag gewählt werden, es sind dann auch freie Einträge für den Verweistyp möglich. Wurde der Dialog von einem Feld in einer anderen Rubrik (z.B. das Feld Basisdaten/Verweise in der Rubrik Fachbezug) geöffnet, so wird automatisch der betreffende Feldname eingetragen und angezeigt (z.B. Basisdaten).', 'Methode / Datengrundlage');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1025, 0, 2000, -1, 'en', 'Link type', 'Statement of the technical relationship that exists between the current object and the reference object (other object, URL). A description has to be chosen from a drop down list. This field is only editable if a new reference is produced or an older one is called up. If the dialogue box is opened from a field in another register card then the relevant field name is automatically entered and displayed (e.g. Functional Base). ', 'Method / Base Data');
-- "Verweisziel" (former "Verweistyp"), new Help
DELETE FROM help_messages WHERE gui_id = 7037;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(184, 0, 7037, -1, 'de', 'Verweisziel', 'Auswahl, ob auf Objekte des Katalogs oder auf Webseiten (URLs) verwiesen werden soll.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1184, 0, 7037, -1, 'en', 'Link target', 'Selection whether the reference should be made to an object in the catalogue or to a website (URL).', '');
-- "URL", remove field "Datentyp"
DELETE FROM help_messages WHERE gui_id = 2240;
-- "URL", remove field "Datenvolumen"
DELETE FROM help_messages WHERE gui_id = 2220;
-- "URL", remove field "Icon-URL"
DELETE FROM help_messages WHERE gui_id = 2250;
-- "URL", remove field "Icon-Text"
DELETE FROM help_messages WHERE gui_id = 2230;

-- "Verbesserung der Ergonomie des Eintrags von Service-Objekten" see INGRID32-26
-- Dialog "Operation bearbeiten/hinzufügen"
-- "Parameter", new Help
DELETE FROM help_messages WHERE gui_id = 5205;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(145, 0, 5205, 3, 'de', 'Parameter', 'Mögliche Parameter, die bei einem Aufruf der Operation übergeben werden können:<br/>- Parametername und gegebenenfalls Zuweisung eines Wertes ( in der Form Name=Wert , siehe Beispiel unten)<br/>- Richtung des Datenflusses, der durch diesen Parameter erzeugt wird.<br/>- Textliche Beschreibung des Parameters.<br/>- Optionalität: Angabe, ob der Parameter angegeben werden muß oder nicht.<br/>- Angabe, ob eine Mehrfacheingabe des Parameters möglich ist.', 'Name: REQUEST=GetCapabilities<br/>Richtung:<br/>Beschreibung: Name of request<br/>Optional: Nein<br/>Mehrfacheingabe: Nein');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1145, 0, 5205, 3, 'en', 'Parameters', 'Possible parameters which can be passed when the operation is called up.', 'Name: REQUEST=GetCapabilities<br/>Direction:<br/>Description: Name of request<br/>Is optional: no<br/>multiple input: no');

-- "Nutzeradministration - Rolle", new Help, see INGRID-2088
DELETE FROM help_messages WHERE gui_id = 8014;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(235, 0, 8014, -1, 'de', 'Rolle', 'Die Rolle des Nutzers wird indirekt durch die Ebene in der Nutzerverwaltung bestimmt, auf welcher der Nutzer angelegt wurde. Der Nutzer, welcher an oberster Stelle im Hierarchiebaum steht, ist der Katalog-Administrator. Die darunter angelegten Nutzer sind alle Metadaten-Administratoren (mit QS Rechten), alle wiederum darunter befindlichen Nutzer haben die Rolle Metadaten-Autor. Die Rolle kann nicht verändert werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1235, 0, 8014, -1, 'en', 'Role', 'The role of the user is allocated via the Portal administration and can not be changed here. The possible ones are catalogue administrator (this is the user at top level), Metadata-Administrator (with QA rights, these are the users at the 2. level) and Metadata-Author (the users at the 3. level)', '');

-- NEW Address field "Daten nicht anzeigen" see INGRID32-37
DELETE FROM help_messages WHERE gui_id = 4320;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1376, 0, 4320, -1, 'de', 'Daten nicht anzeigen', 'Wird die Option "Daten nicht anzeigen" ausgewählt, so wird bei der Darstellung im Portal (auch CSW und Opensearch) bei allen Metadatensätzen, denen diese Adresse zugeordnet ist, statt dessen die Adresse der übergeordneten Institution verwendet.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1377, 0, 4320, -1, 'en', 'Do not display data', 'If the option "Do not display data" is selected, all metadata records use the address of the superordinate institution instead when displaying those metadata record in the portal (or when exporting via OpenSearch or CSW).', '');

-- Separation of User Addresses, Input of full user data in user administration, see INGRID32-36
-- External user system may not be portal, see INGRID32-35
DELETE FROM help_messages WHERE gui_id = 8011;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(232, 0, 8011, -1, 'de', 'Nutzeradministration', 'Hier können Nutzer administriert werden, die mit dem Metadaten-Editor arbeiten dürfen. Über das Hinzufügen einer Gruppe, erhält der Nutzer seine entsprechenden Rechte für das Arbeiten mit dem Katalog. Es können nur neue Nutzer hinzugefügt werden, die im Portal oder einer anderen externen Benutzerverwaltung schon angelegt wurden und bisher noch keine Rechte für den Editor haben. ', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1232, 0, 8011, -1, 'en', 'User Management', 'Here users, who may work with the metadata editor, can be administered. The user receives the relevant rights to work with the catalogue by the addition of a group. Only new users can be added who have already been set up in the Portal or another external user management system and so far still have no editor rights. ', '');
DELETE FROM help_messages WHERE gui_id = 8013;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(234, 0, 8013, -1, 'de', 'Login', 'Login-Name des entsprechenden Nutzers. Entspricht dem Namen welcher im Portal oder einer anderen externen Benutzerveraltung angegeben wurde.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1234, 0, 8013, -1, 'en', 'Login', 'Login-Name of the corresponding user. Equates to the name which was given in the Portal or another external user management system.', '');
-- Addressverweis becomes "Name"
DELETE FROM help_messages WHERE gui_id = 8015;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(236, 0, 8015, -1, 'de', 'Name', 'Angabe des Nachnamens des Benutzers.', 'vom Busche');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1236, 0, 8015, -1, 'en', 'Surname', 'Entry of the surname of the user.', 'vom Busche');
DELETE FROM help_messages WHERE gui_id = 8200;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1378, 0, 8200, -1, 'de', 'Vorname', 'Angabe des Vornamens des Benutzers.', 'Rosemarie');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1379, 0, 8200, -1, 'en', 'Forename', 'Entry of the first name of the user.', 'Rosemarie');
DELETE FROM help_messages WHERE gui_id = 8201;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1380, 0, 8201, -1, 'de', 'E-Mail Benutzer', 'Angabe der E-Mail des Benutzers.', 'rosemarie.busche@yourinstitution.de');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1381, 0, 8201, -1, 'en', 'E-Mail User', 'Entry of the e-mail of the user.', 'rosemarie.busche@yourinstitution.de');
DELETE FROM help_messages WHERE gui_id = 8202;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1382, 0, 8202, -1, 'de', 'E-Mail Metadatenauskunft', 'Angabe einer alternativen E-Mail für die Metadatenauskunft.<br/>Hintergrund: Der Nutzer ist als Verantwortlicher bei Metadatensätzen eingetragen. Dessen E-Mail-Adresse wird verwendet für die Metadatenauskunft. Um die Personenbezogenen Daten zu schützen, kann hier eine alternative E-Mail-Adresse angeben werden (als Funktionsadresse). Für die Angaben Auskunft Metadaten im Portal bzw Metadata Point of Contact über CSW wird dann die E-Mail Metadatenauskunft verwendet. Ist diese Email nicht angegeben, wird die Benutzer-E-Mail-Adresse verwendet.', 'pressestelle@mu.niedersachsen.de');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1383, 0, 8202, -1, 'en', 'E-Mail Point of Contact', 'Entry of an alternative e-mail for point of contact.<br/>If you enter an e-mail here this e-mail is used for Point of Contact in the portal and via CSW. If no entry the e-mail of the user is used. This way you can hide your user e-mail address.', 'pressestelle@mu.niedersachsen.de');
DELETE FROM help_messages WHERE gui_id = 8203;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1384, 0, 8203, -1, 'de', 'Institution', 'Angabe der Institution des Benutzers.', 'Ministerium für Umwelt und Klimaschutz');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1385, 0, 8203, -1, 'en', 'Institution', 'Entry of the institution of the user.', 'Ministry for  Environment and Climate Protection');
DELETE FROM help_messages WHERE gui_id = 8204;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1386, 0, 8204, -1, 'de', 'Straße/Hausnummer', 'Angabe der Hausadresse. Bei der Straße und Hausnummer der Person sollte es sich um die Hausadresse des Dienstgebäudes der Person handeln.', 'Blocksberg 17');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1387, 0, 8204, -1, 'en', 'Street', 'Entry of the address of the building. In giving the street and number of the person, this should be the address of the building where the person works.', '14 Smith Street');
DELETE FROM help_messages WHERE gui_id = 8205;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1388, 0, 8205, -1, 'de', 'PLZ', 'Postleitzahl der Hausadresse (Straße/Hausnummer).', '30169');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1389, 0, 8205, -1, 'en', 'Post Code', 'Post code of the building address (street/building number).', '30169');
DELETE FROM help_messages WHERE gui_id = 8206;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1390, 0, 8206, -1, 'de', 'Ort', 'Es ist der Ort anzugeben, wo sich das Dienstgebäude befindet, in dem die betreffende Person tätig ist.', 'Wolgast');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1391, 0, 8206, -1, 'en', 'City', 'The place is to be given where the office building is located in which the person concerned is employed.', 'London');
DELETE FROM help_messages WHERE gui_id = 8207;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1392, 0, 8207, -1, 'de', 'Telefon', 'Angabe der Telefonnummer des Benutzers.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1393, 0, 8207, -1, 'en', 'Telephone', 'Entry of the telephone number of the user.', '');

-- NEW Coupling type see INGRID32-81
DELETE FROM help_messages WHERE gui_id = 3221;
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1394, 0, 3221, 3, 'de', 'Kopplungstyp', 'Die Art der Kopplung vom Service zu den Daten. Der Typ "tight" bewirkt, dass ein Verweis zu einem Datensatz existieren muss.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1395, 0, 3221, 3, 'en', 'Coupling type', 'The type of the coupling to the data set. The type "tight" needs at least one link to a dataset.', '');
