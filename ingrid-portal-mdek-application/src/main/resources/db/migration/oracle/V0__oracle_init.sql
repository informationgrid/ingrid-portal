-- -------------------- HELP_MESSAGES ------------------------------------

-- 
-- Tabellenstruktur für Tabelle `help_messages`
-- 
CREATE TABLE help_messages (
  id NUMBER(24,0) NOT NULL,
  version NUMBER(10,0) DEFAULT '0' NOT NULL,
  gui_id NUMBER(10,0),
  entity_class NUMBER(10,0),
  language VARCHAR2(2 CHAR),
  name VARCHAR2(255 CHAR),
  help_text CLOB,
  sample CLOB);

-- primary key
ALTER TABLE help_messages ADD CONSTRAINT PRIMARY_HelpMessages PRIMARY KEY ( id ) ENABLE;

-- 
-- Daten für Tabelle `help_messages`
-- 
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1, 0, 1000, -1, 'de', 'Adressen', 'Eintrag von Adressverweise zu Personen oder Institutionen, die weitergehende Informationen zum aktuellen Objekt geben können. Bei Bedarf können diese Verweise verändert werden. In der ersten Spalte wird jeweils die Art des Verweises eingetragen ( Auskunft, Datenhalter, etc.). Über den Link "Adresse hinzufügen" wird der Verweis selbst angelegt. Als Auswahlmöglichkeit stehen alle in der Adressverwaltung des aktuellen Kataloges bereits eingetragenen Personen zur Verfügung. Der Eintrag in der ersten Zeile (Auskunft) ist obligatorisch. Weiterhin ist hier ein Eintrag für Datenverantwortung verpflichtend, wenn ein INSPIRE-Thema ausgewählt wurde.', 'Auskunft / Robbe, Antje Datenhalter / Dr. Seehund, Siegfried');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(2, 0, 1010, 3, 'de', 'Beschreibung', 'Fachliche Inhaltsangabe des Geodatendienstes. Hier sollen in knapper Form die Art des Dienstes sowie die fachlichen Informationsgehalte beschrieben werden. Auf Verständlichkeit für fachfremde Dritte ist zu achten. DV-technische Einzelheiten sollten auf zentrale, kennzeichnende Aspekte beschränkt werden. Das Feld Beschreibungen muss ausgefüllt werden, damit das Objekt abgespeichert werden kann.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(3, 0, 1010, 0, 'de', 'Beschreibung', 'Fachliche Beschreibung der Organisationseinheit/Fachaufgabe. Im Falle einer Organisationseinheit sind die wesentlichen Zuständigkeitsbereiche/Fachaufgaben aufzuführen und ggf. kurz zu erläutern. Hierbei sollten die umweltbezogenen Fachaufgaben der Organisationseinheit im Vordergrund stehen. Ist das Objekt zur Beschreibung einer einzelnen Fachaufgabe angelegt worden, so ist diese Fachaufgabe näher zu erläutern (rechtliche Grundlage, organisatorische Rahmenbedingungen, Zielsetzung, ggf. Überschneidungen mit anderen Fachaufgaben). Auf Verständlichkeit für fachfremde Dritte ist zu achten. Das Feld Beschreibungen muss ausgefüllt werden, damit das Objekt abgespeichert werden kann.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(4, 0, 1010, 4, 'de', 'Beschreibung', 'Fachliche Inhaltsangabe des Vorhabens / Projektes / Programms. Hier sollen in knapper Form die Zielsetzungen und wichtigsten Rahmenbedingungen des Vorhabens / Projektes / Programms beschrieben werden. Auf Verständlichkeit für fachfremde Dritte ist zu achten. Für Detailinformationen (Name des Projektleiters, Projektbeteiligte, etc.) stehen gesonderte Eingabefelder zur Verfügung. Das Feld Beschreibungen muss ausgefüllt werden, damit das Objekt abgespeichert werden kann.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(5, 0, 1010, 5, 'de', 'Beschreibung', 'Fachliche Beschreibung der Datensammlung / Datenbank. Hier sollen in knapper Form die Hauptinhalte Datensammlung / Datenbank beschrieben werden. Hierbei sollte sich auf Parametergruppen beschränkt werden (z.B. Schwermetalle). Detaillierte Parameter können in einem gesonderten Feld eingetragen werden (siehe Fachbezug). Ferner sind ggf. die organisatorischen Rahmenbedingungen der Datensammlung / Datenbank von Interesse (Historie, Pflege, Nutzerkreis, Kooperationen). Auf Verständlichkeit für fachfremde Dritte ist zu achten. Das Feld Beschreibungen muss ausgefüllt werden, damit das Objekt abgespeichert werden kann.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(6, 0, 1010, 2, 'de', 'Beschreibung', 'Inhaltsangabe zum Dokument / Bericht / Literatur. Es ist eine kurze, aber Aussagekräfte Inhaltsangabe (Abstract) einzugeben. Diese Inhaltsangabe kann z.B. den Ausgangspunkt einer Untersuchung, eventuelle methodische Vorgehensweisen sowie die Hauptergebnisse enthalten. Auf Verständlichkeit für fachfremde Dritte ist zu achten. Für Detailinformationen (Autor, Erscheinungsjahr, etc.) stehen gesonderte Eingabefelder zur Verfügung. Das Feld Beschreibungen muss ausgefüllt werden, damit das Objekt abgespeichert werden kann.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(7, 0, 1010, 1, 'de', 'Beschreibung', 'Fachliche Inhaltsangabe der Geo-Information/Karte. Hier soll in knapper Form beschrieben werden, um welche Art von Geo-Information es sich handelt: - GIS-Daten, analoge Karte, Kartenwerk/Atlas - topographische Karte, politische Karte, fachbezogene Karte, etc. Ferner sollten die Hauptinhalte der dargestellten Sachinformationen genannt werden (z.B.: Pegelmessstellen, Kohlekraftwerke). Auf Verständlichkeit für fachfremde Dritte ist zu achten. Für Detailinformationen (Maßstäbe, etc.) stehen gesonderte Eingabefelder zur Verfügung. Das Feld Beschreibungen muss ausgefüllt werden, damit das Objekt abgespeichert werden kann.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(8, 0, 1020, -1, 'de', 'Objektklasse', 'Zuordnung des Objekts zu einer passenden Informationskategorie. Im Metadateneditor sind die verschiedenen Arten von Informationen in Informationskategorien eingeteilt (sog. Objektklassen). Die Objektklassen unterscheiden sich durch unterschiedliche Beschreibungsfelder in der Registerkarte Fachbezug. Es muss eine der folgenden Objektklasse ausgewählt werden: - Datensammlung/Datenbank: analoge oder digitale Sammlung von Daten. Beispiele: Messdaten, statistische Erhebungen, Modelldaten, Daten zu Anlagen - Dienst/Anwendung/Informationssystem: zentrale Auskunftssysteme, welche in der Regel auf eine oder mehrere Datenbanken zugreifen und diese zugänglich machen. - Dokument/Bericht/Literatur: Broschüren, Bücher, Aufsätze, Gutachten, etc. Von Interesse sind insbesondere Dokumente, welche nicht über den Buchhandel oder über Bibliotheken erhältlich sind ("graue Literatur") - Geo-Information/Karte: GIS-Daten, analoge Karten oder Kartenwerke - Organisationseinheit/Fachaufgabe: Zuständigkeiten von Institutionen oder Abteilungen, Fachgebiete, Fachaufgaben. - Vorhaben/Projekt/Programm: Forschungs- und Entwicklungsvorhaben, Projekte unter Beteiligung anderer Institutionen oder privater Unternehmen, Schutzprogramme; von besonderem Interesse sind Vorhaben/Projekte/Programme, in denen umweltrelevante Datenbestände entstehen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(9, 0, 1030, 0, 'de', 'Verantwortlicher', 'Eintrag von Adressverweise zu Personen oder Institutionen, die für dieses Objekt verantwortlich sind.', 'Mustermann, Erika');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(10, 0, 1130, -1, 'de', 'Minimum', 'Angabe der Werte für die Höhe über einem punkt (siehe Pegel) eingegeben. Ist eine vertikale Ausdehnung vorhanden, so kann für das Maximum ein größerer Wert eingegeben werden. Sollte dies nicht der Fall sein, so ist die Eingabe eines Minimalwerts ausreichend, dieser Wert wird dann automatisch ebenso für den Maximalwert übernommen.', 'Minimum 100, Maximum 110');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(11, 0, 1140, -1, 'de', 'Erläuterung', 'Zusätzliche Angaben zum Raumbezug.', 'Die Koordinaten für die Fachliche Gebietseinheit sind ungefähre Angaben.');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(12, 0, 1220, -1, 'de', 'Status', 'Stand der Ausführung des Projektes, der Messung etc. Der Editor nimmt alle bekannten Umweltdaten auf, diese können sich in unterschiedlichen Stadien ihrer Lebenszeit befinden, d.h. Projekte, Programme oder Messungen können in konkreter Planung sein, derzeit durchgeführt werden oder schon abgeschlossen sein.', 'abgeschlossen');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(13, 0, 1230, -1, 'de', 'Intervall', 'Angabe des zeitlichen Abstands (Frequenz) der Datenerhebung. Erfolgt die Datenerhebung kontinuierlich oder periodisch (siehe Feld Periodizität), so soll diese Angabe hier präzisiert werden. Es stehen Felder für den freien Eintrag einer Ziffer und eine Auswahlliste zur Verfügung, die zeitliche Intervalle vorgibt. Der Eintrag von 10 und Tage bedeutet: Die beschriebenen Daten werden bzw. wurden alle 10 Tage erhoben.', 'Alle 6 Monate');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(14, 0, 1240, -1, 'de', 'Periodizität', 'Angabe des Zeitzyklus der Datenerhebung. Der Eintrag muss aus der Auswahlliste erfolgen, die über den Pfeil am Ende des Feldes geöffnet wird. Wichtig: Der Eintrag "unbekannt" sollte nicht mehr verwendet und falls noch in Altdaten vorhanden durch sinnvolle Einträge ersetzt werden. Er stellt eine nicht ISO-konforme Erweiterung der Auswahlliste dar.', 'täglich');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(15, 0, 1250, -1, 'de', 'Erläuterung', 'Zusätzliche Angaben zum Zeitbezug. Hier können z.B. die Angaben der Periodizität eingeschränkt, weitere Zeitangaben gemacht oder Unregelmäßigkeiten erklärt werden. Im Zusammenhang mit dem Eintrag im Feld Periodizität können hier Abstände, Perioden und Intervalle eingetragen werden, die sich nicht aus dem Zusammenhang der anderen Felder des Zeitbezuges erklären, z.B. Jahreszeiten, Dekaden, Tageszeiten.', 'Die Messungen erfolgten nur tagsüber.');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(16, 0, 1310, -1, 'de', 'Medienoption', 'Angabe, auf welchen Medien die Daten zur Verfügung gestellt werden können. Hier können elektronische Datenträger als auch Medien in Papierform angegeben werden, auf denen die im Objekt beschriebenen Daten dem Nutzer zur Verfügung stehen. Es können mehrere Medien eingetragen werden. Medium: Angabe der Medien, auf denen der Datensatz bereit gestellt werden kann (ISO-Auswahlliste) Datenvolumen: Umfang des Datenvolumens in MB (Fließkommazahl) Speicherort: Ort der Datenspeicherung im Intranet/Internet, Angabe als Verweis', 'Medium: CD-ROM Datenvolumen: 700 MB Speicherort: Explorer Z:/Bereich_51/Metainformation/20040423_Hilfetexte.doc');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(17, 0, 1320, -1, 'de', 'Datenformat', 'Angabe des Formats der Daten in DV-technischer Hinsicht, in welchem diese verfügbar sind. Das Format wird durch 4 unterschiedliche Eingaben spezifiziert. Wenn die erste Spalte befüllt wird, müssen auch die anderen Eintragungen gemacht werden. Name: Angabe des Formatnamens, wie z.B. "Date" Version: Version der verfügbaren Daten (z.B. "Version 8" oder "Version vom 8.11.2001") Kompressionstechnik: Kompression, in welcher die Daten geliefert werden (z.B. "WinZip", "keine") Bildpunkttiefe: BitsPerSample.', 'Formatkürzel: tif Version: 6.0 Kompression: LZW Bildpunkttiefe: 8 Bit');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(18, 0, 1350, -1, 'de', 'Rechtliche Grundlage', 'Angabe der rechtlichen Grundlage, die die Erhebung der beschriebenen Daten veranlasst hat. Hier können Kürzel von Gesetzen, Erlassen, Verordnungen usw. eingetragen werden, in denen z. B. die Methode oder die Form der Erhebung der im Objekt beschriebenen Daten festgelegt oder beschrieben wird. Es ist bei Bedarf der Eintrag mehrerer Angaben möglich.', 'Niedersächsisches Naturschutzgesetz');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(19, 0, 1360, -1, 'de', 'Erläuterung', 'Angabe, warum die Daten erhoben werden.', 'Topographische Karten werden erstellt für den Nachweis des Landesgebietes.');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(20, 0, 1370, -1, 'de', 'XML-Export-Kriterium', 'Eintrag eines Selektionskriteriums zur Steuerung des Exports der Daten. Um eine Teilmenge von Objekten exportieren zu können, kann in diesem Feld ein diese Teilmenge identifizierendes Schlagwort eingegeben werden. In der Exportfunktion kann dann eines der Schlagworte aus diesem Feld angegeben werden und alle Objekte exportiert werden, für die in diesem Feld das entsprechende Schlagwort vergeben wurde. Die Eingabe mehrerer Schlagworte ist möglich. Die Schlagworte können frei eingegeben werden. Zur Verhinderung von Schreibfehlern sollte jedoch der Eintrag aus der Auswahlliste vorgezogen werden.', 'CDS');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(21, 0, 1410, -1, 'de', 'Freie Suchbegriffe', 'Eingabe von Schlagworten, über die das Objekt bei einer Schlagwortsuche möglichst schnell zu finden ist und die nicht im Thesaurus vorhanden sind. Hier sollen prägnante Begriffe und Termini, die in engem Zusammenhang mit dem Objekt stehen und die nicht im Thesaurus vorhanden sind, eingetragen werden. Dies können spezielle Fachgebiete, (Mess-Methoden, Bestandteile o.ä. sein. Die Freien Suchbegriffe sind ergänzend zu den Thesaurus-Suchbegriffen anzugeben. Wenn Sie hier einen Thesaurusbegriff eingeben, so wird automatisch vorgeschlagen, diesen in das Feld für die Thesaurusbegriffe zu übernehmen. Wird ein Synonym zu einem Thesaurusbegriff eingegeben, so wird die zusätzliche automatische Übernahme des zugehörigen Thesaurusbegriffs vorgeschlagen.', 'Bestandsaufnahme Rasterkarte Nachtfalter');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(22, 0, 1420, -1, 'de', 'Thesaurus-Suchbegriffe', 'Eingabe von mindestens drei Schlagworten (Pflicht), die im Thesaurus verzeichnet sind. Die Verschlagwortung dient dem themenbezogenen Wiederauffinden (Retrieval) der Objekte über den Thesaurus-Navigator. Dazu müssen Schlagworte aus dem Thesaurus ausgewählt werden, die das Objekt so genau wie möglich, aber auch so allgemein wie nötig beschreiben. So sollte mindestens ein Schlagwort in der Thesaurushierarchie einen relativ allgemeinen Aspekt des Objektes beschreiben und mindestens ein Schlagwort das Objekt so speziell wie möglich beschreiben. Die Auswahl kann über den "Verschlagwortungsassistenten" oder den "Thesaurus-Navigator" vorgenommen werden - siehe Verlinkung.', 'Naturschutz Schmetterling Kartierung Artenschutz');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(23, 0, 1500, -1, 'de', 'Verweis zu', 'Es gibt die Möglichkeit, Verweise von einem Objekt zu einem anderen Objekt, zu einer WWW-Adresse (URL) oder zu einem OLE-Objekt (WinWord, Excel, usw.) zu erstellen. In dieser Tabelle werden alle Verweise zusammenfassend aufgeführt, welche im aktuellen Objekt angelegt wurden. Über dem Link "Verweise anlegen/bearbeiten" öffnet sich ein Dialog, mit dem weitere Einzelheiten zu den Verweisen eingesehen und editiert werden können. Es ist ferner möglich, weitere Verweise über diesen Dialog hinzuzufügen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(24, 0, 1510, -1, 'de', 'Verweis von', 'In dieser Tabelle werden alle Verweise von denjenigen Objekten aufgeführt, welche auf das aktuelle Objekt verweisen. Das Editieren oder Hinzufügen ist nicht möglich. Sollen die Verweise geändert oder ergänzt werden, so muss zu dem entsprechenden Objekt gewechselt werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(25, 0, 2000, -1, 'de', 'Feldname / Bezeichnung der Verweisbeziehung', 'Angabe des fachlichen Bezuges, der zwischen dem aktuellen Objekt und dem Verweisobjekt (anderes Objekt, URL, OLE-Objekt) besteht. Die Beziehung des erstellten Verweises soll hier kurz und prägnant beschrieben werden (z. B. Datensammlung zum Gutachten, Literaturhinweis zur Datenerhebung). Über den Pfeil an der rechten Seite des Feldes kann aus einer Auswahlliste eine schon benutzte Beschreibung übernommen werden. Dieses Feld ist nur editierbar, wenn ein neuer Verweis erzeugt oder ein älterer aufgerufen wird. Einzutragen ist ein sprechender Name für den Verweis. Wurde der Dialog von einem Feld in einer anderen Registerkarte geöffnet, so wird automatisch der betreffende Feldname eingetragen und angezeigt (z.B. technische Dokumentation).', 'Literaturhinweis zum V-Modell');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(26, 0, 2100, -1, 'de', 'Objektname', 'Objektname des Objekts, auf das verwiesen werden soll. Der Verweis auf ein Objekt kann unabhängig von Strukturbaum und Hierarchie aus fachlichen Gründen gebildet werden. Die Eingabe erfolgt über eine Auswahl aus dem Strukturbaum, der über den Link "Objekt auswählen" geöffnet werden kann.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(27, 0, 2110, -1, 'de', 'Erläuterung', 'Anmerkungen zu dem erstellten Verweis. Hier können weitergehende Informationen zum Objekt eingegeben werden, auf das verwiesen wird. Es können auch Erläuterungen zu der Beziehung zwischen den beschriebenen Daten des aktuellen Objektes und den Daten des Verweis-Objektes gegeben werden.', 'Das Objekt, auf das verwiesen wird, enthält weiterführende Literaturhinweise zum aktuellen Objekt.');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(28, 0, 2200, -1, 'de', 'Internet-Adresse (URL)', 'Angabe einer Internet-Adresse. Hier sollen Adressen eingegeben werden, bei denen im Internet Daten bzw. eine Anwendung zu finden sind, die mit den beschriebenen Daten in fachlichem Zusammenhang stehen oder bei der man die beschriebenen Daten im Internet ansehen kann.', 'http://www.landesbehörde.bundesland.de/ozondaten.html');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(29, 0, 2210, -1, 'de', 'Bezeichnung des Verweises', 'Eintrag einer aussagekräftigen Bezeichnung für den Verweis. Beispielsweise kann der sprechende Name der Webseite eingetragen werden.', 'Technische Dokumentation zum Luftüberwachungssystem Niedersachsen (LÜN)');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(30, 0, 2220, -1, 'de', 'Datenvolumen', 'Angabe über die Menge der Daten, die sich hinter der URL verbergen. Hier soll eine Angabe über die ungefähre Menge der Daten erfolgen, die bei Aufruf der URL zu übertragen sind. Der Eintrag soll in MB oder KB erfolgen.', '120 MB');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(31, 0, 2230, -1, 'de', 'Icon_Text', 'Text, der bei entsprechender Browser-Einstellung anstelle eines die Anwendung bzw. die Daten repräsentierenden Icons dargestellt wird.', 'NUMIS');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(32, 0, 2240, -1, 'de', 'Datentyp (Link Dialog)', 'Angabe des Dateiformats - es sind beliebige Einträge möglich z.B. JPG, HTML, XLS oder TXT', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(33, 0, 2250, -1, 'de', 'Icon_URL', 'Angabe einer URL, die ein Icon vorhält, das die verwiesenen Daten repräsentiert. Einer Anwendung oder Daten wird häufig ein signifikantes Icon zugeordnet. In diesem Feld soll die Internet-Adresse angegeben werden, wo dieses Icon zu finden ist.', 'http://www.landesbehörde.bundesland.de/flagge.gif');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(34, 0, 2251, -1, 'de', 'URL-Typ', 'Bei URL-Verweisen für ein Objekt wird unterschieden ob diese URL für das Internet oder für das Intranet gilt.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(35, 0, 2260, -1, 'de', 'Erläuterung', 'Anmerkungen zu dem erstellten Verweis. Hier können weitergehende Informationen zu der URL eingegeben werden, auf die verwiesen wird. Es können auch Erläuterungen zu der Beziehung zwischen den beschriebenen Daten des aktuellen Objektes und den Daten des Verweis-Objektes gegeben werden.', 'Es werden Java-Applets geladen');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(36, 0, 2300, -1, 'de', 'Bezeichnung des Verweises', 'Eintrag einer aussagekräftigen Bezeichnung für den Verweis. Wird beispielsweise ein Textdokument als OLE-Objekt eingebunden, so kann der Titel dieses Dokumentes eingetragen werden.', 'Technische Dokumentation zum Lüftüberwachungssystem Niedersachsen (LÜN)');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(37, 0, 3000, 3, 'de', 'Objektname', 'Angabe einer kurzen prägnanten Bezeichnung des beschriebenen Dienstes / der beschriebenen Anwendung / des beschriebenen Informationssystems. Der Objektname kann z.B. identisch mit dem Namen des Dienstes /der Anwendung /des Informationssystems sein, sofern dieser ausreichend kurz und aussagekräftig ist. Soweit ein gängiges Kürzel vorhanden ist, ist dieses Kürzel mit anzugeben. Der Eintrag in dieses Feld ist obligatorisch.', 'Emissions-Fernüberwachung EFÜ');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(38, 0, 3000, 0, 'de', 'Objektname', 'Angabe einer kurzen, prägnanten Bezeichnung der beschriebenen Organisationseinheit oder Fachaufgabe.', 'Jahresstatistik zum Stand der Altlastenbearbeitung');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(39, 0, 3000, 4, 'de', 'Objektname', 'Angabe einer kurzen prägnanten Bezeichnung des beschriebenen Vorhabens / Projekts / Programms. Der Objektname kann z.B. identisch mit dem Titel des Vorhabens / Projekts / Programms sein, sofern dieser ausreichend kurz und aussagekräftig ist. Soweit ein gängiges Kürzel vorhanden ist, ist dieses Kürzel mit anzugeben. Der Eintrag in dieses Feld ist obligatorisch.', 'Moorschutzprogramm');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(40, 0, 3000, 5, 'de', 'Objektname', 'Angabe einer kurzen prägnanten Bezeichnung der beschriebenen Datensammlung / Datenbank. Der Objektname kann z.B. identisch mit dem Namen der Datenbank bzw. der Datensammlung sein, sofern dieser ausreichend kurz und aussagekräftig ist. Soweit ein gängiges Kürzel vorhanden ist, ist dieses Kürzel mit anzugeben. Der Eintrag in dieses Feld ist obligatorisch.', 'Anionenkonzentration in Abwasser');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(41, 0, 3000, 2, 'de', 'Objektname', 'Angabe einer kurzen prägnanten Bezeichnung des beschriebenen Dokumentes / Berichtes / der beschriebenen Literatur. Der Objektname kann z.B. identisch mit dem Titel des Dokumentes / des Berichtes / der Literatur sein, sofern dieser ausreichend kurz und aussagekräftig ist. Soweit ein gängiges Kürzel vorhanden ist, ist dieses Kürzel mit anzugeben. Der Eintrag in dieses Feld ist obligatorisch.', 'Schwermetallbelastung des Bodens im Umkreis von Deponien vor 1950.');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(42, 0, 3000, 1, 'de', 'Objektname', 'Angabe einer kurzen, prägnanten Bezeichnung der beschriebenen Karte oder Geo-Information. Der Objektname kann z.B. identisch mit dem Namen der Karte oder der Geo-Information sein, sofern dieser ausreichend kurz und aussagekräftig ist. Soweit ein gängiges Kürzel vorhanden ist, ist dieses Kürzel mit anzugeben. Der Eintrag in dieses Feld ist obligatorisch.', 'Biotoptypenkarte');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(43, 0, 3100, 5, 'de', 'Methode / Datengrundlage', 'Angabe der verwendeten Methoden und der Datenherkunft. Hier sollen die angewandten Methoden der Datengewinnung (z.B. Meßmethode, Erhebungsmethode) benannt und beschrieben werden. Außerdem können Angaben z. B. zu Qualität oder Umfang der Datengrundlage eingetragen werden. Der Eintrag kann direkt erfolgen, wenn die Registerkarte A gewählt wurde, oder als Verweis über die Registerkarte mit dem Pfeil.', 'Ionenchromatographie nach DIN 38405-D20 (Sept. 91)');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(44, 0, 3110, 5, 'de', 'Inhalte der Datensammlung', 'Angabe der wichtigsten Parameter der Datenbank bzw. der Datensammlung. Um einen qualifizierten Einblick in die beschriebene Datensammlung bzw. Datenbank zu bekommen, sollen hier die aussagekräftigen Parameter der vorgehaltenen Daten genannt werden. In der linken Spalte werden diese Parameter eingetragen. Beispiele für Parameter: im Falle von Messdaten werden die wichtigsten Messparameter (z.B. NOx, SO2, Windgeschwindigkeit, pH-Wert), im Falle von statistischen Erhebungen werden die Erhebungsgrößen (z.B. Wasserverbrauch pro Kopf, Bevölkerungsdichte), im Falle von Modelldaten werden die Modellparameter (z.B. Meeresspiegel, CO2-Gehalt der Luft, Welttemperatur) angegeben In der rechten Spalte können pro Parameter ergänzende Angaben gemacht werden. Beispielsweise kommen Angaben zur Maßeinheit, Genauigkeit, Nachweisgrenze, Probematrix oder parameterspezifische Angaben zur Meßmethode in Frage.', 'Blei / in Trinkwasser, Nachweisgrenze: 10 ppb Cadmium / in Schlacke, Nachweisgrenze: 3 ppm');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(45, 0, 3120, 5, 'de', 'Erläuterung', 'zusätzliche Angaben zur Datensammlung bzw. zur Datenbank', 'Die angegebenen Inhalte der Datenbank stellen nur eine Auswahl aller gemessenen Parameter dar (insgesamt ca. 300).');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(46, 0, 3200, 3, 'de', 'Systemumgebung', 'Angaben zum Betriebssystem und der Software, ggf. auch Hardware, die zur Implementierung des Dienstes eingesetzt wird.', 'UMN Mapserver 4.0.2 auf Linux 2.6.0 Für Echtzeit- und near-online-GPS-Anwendungen: Korrekturdatenempfänger Für postprocessing-Anwendungen:GPS-Auswertesoftware');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(47, 0, 3210, 3, 'de', 'Basisdaten', 'Verweis auf zugrunde liegende Daten. Hier sollen Verweise zu anderen Objekten dieses Katalogs gelegt werden, die Auskunft über Herkunft und Art der zugrunde liegenden Daten geben. Es kann über den Link (Verweis anlegen/bearbeiten) ein neuer Verweis angelegt werden.', 'Messdaten von Emissionen in bestimmten Betrieben bzw. Einfügen eines Verweises auf den "tightly coupled" OGC Web-Dienst');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(48, 0, 3220, 3, 'de', 'Art des Dienstes', 'In diesem Pflichtfeld kann die Art des Dienstes ausgewählt werden. Über das Feld werden die zur weiteren Befüllung auszuwählenden Angaben zu Operationen gesteuert.', 'Positionierungsdienst SAPOS® WMS WFS');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(49, 0, 3230, 3, 'de', 'Version', 'Angaben zu Version der dem Dienst zugrunde liegenden Spezifikation. Bitte alle Versionen eintragen, die vom Dienst unterstützt werden.', '1.1.1 2.0');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(50, 0, 3240, 3, 'de', 'Historie', 'OGC Web Service: Angaben zur Implementierungsgeschichte des Dienstes. Bei anderen Systemen Angabe zur Entwicklungsgeschichte. Hier können Vorläufer und Folgedienste bzw. -anwendungen oder -systeme genannt werden. Ebenso sind Angaben zu initiierenden Forschungsvorhaben oder -programmen von Interesse.', '11.12.03: Installation des UMN Mapserver 3.0 auf Linux 2.2.005.04.04: Upgrade Linux 2.2.0 auf Linux 2.6.0 Modellversuch beim Gewerbeaufsichtsamt Osnabrück 1991; Einführung 1993');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(51, 0, 3250, 3, 'de', 'Erläuterung', 'OGC Web Services: Beschreibung der Basisdaten. Allgemein: Zusätzliche Anmerkungen zu dem beschriebenen Dienst, der Anwendung oder dem Informationssystem. Hier können weitergehende Angaben z. B. technischer Art gemacht werden, die zum Verständnis des Dienstes, der Anwendung, des Informationssystems notwendig sind.', 'Der Datensatz ist eine Shape-Datei, die alle Grundwassermessstellen in Hessen mit Lage und Kennung beinhaltet.');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(52, 0, 3300, 2, 'de', 'Erscheinungsjahr', 'Angabe der Jahreszahl der Publikation der Literatur. Das Erscheinungsjahr ist vor allem für regelmäßig erscheinende Literatur wie z.B. jährliche Tagungsbände äußerst wichtig zur Identifikation. Das Erscheinungsjahr kann sich von den entsprechenden Angaben im Zeitbezug des Objektes unterscheiden, die sich auf den Inhalt der Literatur beziehen und nicht auf die Literatur selbst.', '1996');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(53, 0, 3310, 2, 'de', 'Erscheinungsort', 'Angabe des Publikationsortes der Literatur. Diese Angabe bezieht sich auf die Literatur und nicht auf die Inhalte der Literatur. Die räumliche Zuordnung der Inhalte der Literatur erfolgt in den Angaben zum Raumbezug des aktuellen Objektes.', 'Hamburg');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(54, 0, 3320, 2, 'de', 'Seiten', 'Angabe der Anzahl der Seiten der Literatur. Hier ist die Anzahl der Seiten anzugeben, wenn es sich um ein Buch handelt. Bei einem Artikel, der in einer Zeitschrift erschienen ist, sollen die Seitenzahlen des Artikelanfangs und des Endes eingegeben werden.', '345 256-268');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(55, 0, 3330, 2, 'de', 'Band, Heft', 'Angabe der Zählung des betreffenden Bandes einer Reihe. Zeitschriften und Sammelwerke bzw. Reihen haben eine durchgängige Zählung seit ihrem Erscheinen oder pro Jahr. Hier ist die Zählung des Bandes anzugeben, in dem der Artikel bzw. der Bericht erschienen ist.', 'Band IV');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(56, 0, 3340, 2, 'de', 'Erschienen in', 'Angabe des Sammelwerkes, in dem ein Aufsatz erschienen ist. Aufsätze und andere nicht selbständige Literatur sind häufig als Teil einer Zeitschrift oder eines Buches erschienen oder als gedruckte Version eines Vortrages im Rahmen einer Tagung. Hier ist der Titel der Zeitschrift bzw. des Sammelwerkes (Tagungsband (Proceedings), Jahresberichte etc.) anzugeben, in der bzw. in dem die beschriebene Literatur erschienen ist. Unter diesem Titel kann ein Artikel beim Herausgeber bezogen werden.', 'Jahresberichte zur Abfallwirtschaft');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(57, 0, 3345, 2, 'de', 'Basisdaten', 'Herkunft und Art der zugrundeliegenden Daten. Bei einem OGC Web Service können Verweise auf ein oder mehrere Objekte eingefügt werden, die mit dem Dienst eng verknüpft ("tightly coupled") sind. Im Allgemeinen sind dies die Datensätze, auf die der Dienst aufgesetzt ist. Bitte verwenden Sie hierzu möglichst einen "Verweis zu Dienst", der in dem die Basisdaten beschreibenden Objekt eingetragen wird (Klasse Geoinformation/Karte). Allgemein sollen die Herkunft oder die Ausgangsdaten der Daten beschrieben werden, die in dem Dienst / der Anwendung bzw. dem Informationssystem benutzt, gespeichert, angezeigt oder weiterverarbeitet werden. Zusätzlich kann die Art der Daten (z. B. digital, automatisch ermittelt oder aus Umfrageergebnissen, Primärdaten, fehlerbereinigte Daten) angegeben werden. Der Eintrag kann hier direkt über die Auswahl der Registerkarte "Text" erfolgen oder es können Verweise eingetragen werden, indem der Link "Verweis anlegen/bearbeiten" angewählt wird.', 'Deponieüberwachung Berlin-Tegel, Statistikauswertungen seit 1974');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(58, 0, 3350, 2, 'de', 'Herausgeber', 'Angabe des Herausgebers. Der Herausgeber ist z. B. die Institution, in der ein Autor arbeitet und in deren Auftrag er geschrieben hat. Es kann auch ein Verlag, ein Verein oder eine andere Körperschaft sein, der/die Beiträge zu einem Thema sammelt und als Buch erscheinen lässt bzw. Bücher zu einem Thema als Reihe herausgibt.', 'Umweltbundesamt');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(59, 0, 3355, 2, 'de', 'Autor/Verfasser', 'Angabe des Autors bzw. des Verfassers der Literatur. Der Eintrag mehrerer Personen ist durch Semikolon zu trennen.', 'Angelika Müller; Hans Meier');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(60, 0, 3360, 2, 'de', 'Standort', 'Angabe des Aufbewahrungsortes und evtl. Bezugsort der Literatur, für den Fall, dass ein Bezug auf üblichem Wege (Handel, Bibliotheken) nicht möglich ist. Der Eintrag kann direkt über die Auswahl der Registerkarte "Text" erfolgen oder es können Adreßverweise eingetragen werden, indem die Registerkarte "Verweise" aktiviert und der Link "Adresse hinzufügen" betätigt werden. Es können Adressen nach Vorname, Nachname oder Name der Einheit/Institution des aktuellen Kataloges gesucht werden. Alternativ kann der Eintrag über den Hierarchiebaum erfolgen.', 'Bibliothek Umweltbundesamt');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(61, 0, 3365, 2, 'de', 'ISBN-Nr.', 'Angabe der 10-stelligen Identifikationsnummer der Literatur.', '3-456-7889-X');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(62, 0, 3370, 2, 'de', 'Verlag', 'Angabe des Verlages, in dem die Literatur erschienen ist.', 'econ');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(63, 0, 3375, 2, 'de', 'Erläuterung', 'Zusätzliche Anmerkungen zur beschriebenen Literatur.', 'Der Artikel beruht auf der Diplomarbeit des Autors aus dem Jahr 1995 an der Universität');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(64, 0, 3380, 2, 'de', 'Weitere bibliographische Angaben', 'Hier können bibliographische Angaben gemacht werden, für die kein Feld explizit vorgesehen ist. Dies können z.B. Angaben zu Abbildungen oder zum Format sein. Wichtig ist auch ein Hinweis, wenn dem Dokument eine Diskette oder eine CD-ROM beiliegt bzw. es identisch auf CD-ROM erschienen ist.', 'Das Kartenwerk ist im DIN A3-Format erschienen.');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(65, 0, 3385, 2, 'de', 'Dokumenttyp', 'Angabe der Art des Dokumentes. Es ist eine Kurzcharakteristik über die Art der Literatur anzugeben. Der Eintrag kann direkt erfolgen oder mit Hilfe einer Auswahlliste, die über den Pfeil am rechten Ende des Feldes geöffnet werden kann.', 'Zeitschriftenartikel');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(66, 0, 3400, 4, 'de', 'Projektleiter', 'Angabe des Projektleiters. Der Eintrag kann direkt über die Auswahl der Registerkarte "Text" erfolgen oder es können Adreßverweise ausgewählt werden, indem die Registerkarte "Verweise" aktiviert wird. Über den Link "Adresse hinzufügen" kann nach Adressen gesucht werden oder es können diese mit Hilfe des Hierachiebaums ausgewählt werden. ', 'Dr. Antje Robbe');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(67, 0, 3410, 4, 'de', 'Beteiligte', 'Angabe von Personen oder Institutionen, die am Projekt bzw. am Programm oder Vorhaben beteiligt sind. Der Eintrag soll Hinweise auf wichtige Institutionen oder Personen geben, die beteiligt waren bzw. sind und über die evtl. weitere genauere Informationen zu erfahren sind. Der Eintrag kann direkt über die Auswahl der Registerkarte "Text" erfolgen oder es können Adreßverweise eingetragen werden, indem die Registerkarte "Verweise" aktiviert wird. Über "Adresse hinzufügen" kann nach Adressen und Institutionen gesucht werden oder man kann diese über den Hierarchiebaum ausgewählten.', 'BfN, BMU');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(68, 0, 3420, 4, 'de', 'Erläuterungen', 'Weitere Angaben zum Projekt bzw. Programm oder Vorhaben. Hier können zusätzliche wichtige Angaben eingetragen werden, z.B. Finanzierung, Förderkennzeichen, Bearbeitungsstatus.', 'Finanzierung über BMU, EU');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(69, 0, 3500, 1, 'de', 'Raumbezugssystem', 'Angabe der Bezugssysteme (Lage, Höhe, Schwere), die der Erarbeitung des zu beschreibenden Datenbestandes oder der fertigen Karte zugrunde gelegt wurden. Die Angaben können sich gegenüber der Bounding Box unterscheiden. Hier werden die dem Datenobjekt zugrunde liegenden Parameter angegeben.', 'EPSG:4326 / WGS 84 / geographisch');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(70, 0, 3515, 1, 'de', 'Herstellungsprozess', 'Angabe der Methode, die zur Erstellung des Datenobjektes geführt hat. Der Eintrag kann in Textform erfolgen, indem die Registerkarte "Text" ausgewählt wird. Außerdem kann durch Auswahl der Registerkarte "Verweise" ein Verweis erstellt werden.', 'Feldkartierung');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(71, 0, 3520, 1, 'de', 'Fachliche Grundlage', 'Angabe der Dokumente, die Grundlage der fachlichen Inhalte der Karte oder Datensammlung sind. Außerdem können Regeln für die Erfassung (Geo-Information) bzw. Darstellung (Karte) angegeben werden. Dieses Dokument kann eine Erläuterung der gesetzlichen Grundlagen darstellen, jedoch auch selbständigen Charakters sein. Der Eintrag kann in Textform erfolgen, indem die Karteikarte "Text" ausgewählt wird. Außerdem kann durch Auswahl der Registerkarte "Verweise" ein Verweis zu einem anderen Objekt im aktuellen Katalog erstellt werden. (INSPIRE-Pflichtfeld für Datasets und Data series)', 'Zeichenvorschrift');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(72, 0, 3525, 1, 'de', 'Erstellungsmaßstab', 'Angabe des Erstellungsmaßstabes, der sich auf die erstellte Karte und/oder Digitalisiergrundlage bei Geodaten bezieht. Maßstab: Maßstab der Karte, z.B 1:12 Bodenauflösung: Einheit geteilt durch Auflösung multipliziert mit dem Maßstab (Angabe in Meter, Fließkommazahl) Scanauflösung: Auflösung z.B. einer eingescannten Karte, z.B. 120dpi (Angabe in dpi, Integerzahl) (optionales INSPIRE-Feld)', 'Bodenauflösung: Auflösungseinheit in Linien/cm; Einheit: z.B. 1 cm geteilt durch 400 Linien multipliziert mit dem Maßstab 1:25.000 ergibt 62,5 cm als Bodenauflösung');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(73, 0, 3530, 1, 'de', 'Lagegenauigkeit', 'Angabe über die Genauigkeit z.B. in einer Karte', '0,5 m');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(74, 0, 3535, 1, 'de', 'Schlüsselkatalog', 'An dieser Stelle besteht die Möglichkeit, den Daten zugrunde liegende Klassifizierungsschlüssel zu benennen. Dabei ist die Eingabe mehrerer Kataloge mit zugehörigem Datum (Pflichteintrag) und Version (Optional) möglich.', 'Biotoptypenschlüssel, Datum 01.01.1998, Version 1.1');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(75, 0, 3555, 1, 'de', 'Symbolkatalog', 'Für die Präsentation genormter Objekte und Sachverhalte können für die Nutzer der Daten zur Herstellung von Karten abgestimmte Symbole vorgegeben werden. Die Angabe eines oder mehrerer analoger oder digitaler Symbolpaletten mit zugehörigem Datum (Pflichteintrag) und Version (Optional) ist hier möglich.', 'Planzeichenverordnung, Datum 01.01.1998, Version 1.0');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(76, 0, 3565, 1, 'de', 'Erfassungsgrad', 'Eingabe einer Prozentangabe zum Stand der Erfassung des Datenobjektes. Diese kann sich auf die Anzahl der Kartenblätter aber auch auf den Erfassungsgrad einer Gesamtkarte beziehen.', '55');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(77, 0, 3570, 1, 'de', 'Datengrundlage', 'Angabe der Unterlagen (Luftbilder, Karten, Datensammlungen), die bei der Erstellung der Karte oder der Geo-Information (des digitalen Datenbestandes) Verwendung finden. Der Eintrag kann in Textform erfolgen, indem die Karteikarte "Text" ausgewählt wird. Außerdem kann durch Auswahl der Registerkarte "Verweise" ein Verweis zu einem anderen Objekt im aktuellen Katalog erstellt werden.', 'Kartieroriginale der Pflanzenerfassung');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(78, 0, 3571, -1, 'de', 'Veröffentlichung', 'Das Feld Veröffentlichung gibt an, welche Veröffentlichungsmöglichkeiten für das Objekt freigegeben sind. Die Liste der Möglichkeiten ist nach Freigabestufen hierarchisch geordnet. Wird einem Objekt eine niedrigere Freigabestufe zugeordnet (z.B. von Internet auf Intranet), werden automatisch auch alle untergeordneten Objekte dieser Stufe zugeordnet. Soll einem Objekt eine höhere Freigabestufe zugeordnet werden als die des übergeordneten Objektes, wird die Zuordnung verweigert. Wird einem Objekt eine höhere Freigabestufe zugeordnet (z.B. von Amtsintern auf Intranet), kann auch allen untergeordneten Objekten die höhere Freigabestufe zugeordnet werden.', 'Die Einstellung haben folgende Bedeutung: Internet: Das Objekt darf überall veröffentlicht werden Intranet: Das Objekt darf nur im Intranet veröffentlicht werden, aber nicht im Internet Amtsintern: Das Objekt darf nur in der erfassenden Instanz aber weder im Internet noch im Intranet veröffentlicht werden  Nicht freigegeben: Das Objekt ist nur für den Erfasser sichtbar');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(79, 0, 4000, 3, 'de', 'Name', 'Name einer Person (Pflichtfeld), für die eine freie Adresse eingetragen werden soll. Hinweis: Diese Maske dient dem Eintrag sog. freier Adressen von Personen. Hierunter werden Adressen verstanden, die nicht hierarchisch aufgebaut sind (Institution/Einheit/Person). Der Eintrag freier Adressen kann dann sinnvoll sein, wenn eine Person keiner Institution zuzuordnen ist oder wenn man die Adresse von nur einem Mitarbeiter einer Institution eintragen will.', 'vom Busche');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(80, 0, 4005, 3, 'de', 'Vorname', 'Angabe des Vornamens der eingetragenen Person. Um Verwechslungen von Trägern gleicher Nachnamen zu vermeiden, soll hier der Vorname der eingetragenen Person genannt werden. Auf Vollständigkeit der Vornamen wird dabei kein Wert gelegt.', 'Rosemarie');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(81, 0, 4010, 3, 'de', 'Institution', 'Name der Institution, der die Person angehört. Falls die Dienst- oder Geschäftsadresse der Person angelegt werden soll, so ist hier der Name der Institution einzutragen, der die Person angehört. Evtl. kann in einer weiteren Zeile auch die betreffende Einheit (Abteilung, Referat, Dezernat, etc.) eingetragen werden.', 'Landesanstalt für Umweltschutz');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(82, 0, 4015, 3, 'de', 'Anrede', 'Angabe der Anrede für die eingetragene Person. Um eine korrekte Korrespondenz mit der eingetragenen Person zu ermöglichen, soll diese Angabe falsche Anreden der eingetragenen Person vermeiden. Die Standardeinträge (Frau, Herr) sind in der Auswahlliste enthalten. Freie Einträge sind jedoch auch möglich (z.B. Mrs, Mr).', 'Frau');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(83, 0, 4020, 3, 'de', 'Titel', 'Angabe des akademischen Titels der eingetragenen Person. Hier sollen die dem Namen vorangestellten akademischen Titel angegeben werden. Amtstitel (z.B. MinD, OAR) sollen aus Datenschutzgründen nicht eingetragen werden.', 'Prof. Dr.');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(84, 0, 4100, 0, 'de', 'Institution', 'Name der Institution. Hier soll die vollständige, offizielle Bezeichnung der Institution eingetragen werden. Eine Institution ist eine offizielle, selbständige organisatorische Einheit. Dies kann z. B. eine Behörde, ein Amt oder eine Firma sein. Um die zuständigen Personen zu präzisieren, können der Institution Einheiten und Personen zugeordnet werden, die dann detaillierte Angaben zu der Einheit und der Person enthalten. Einer Institution kann auch eine Person direkt zugeordnet werden. Eine Institution kann auch alleine, ohne zugeordnete Personen oder Einheiten in den Adressverweisen der Objekte eingetragen werden.', 'Landesanstalt für Umweltschutz');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(85, 0, 4200, 1, 'de', 'Einheit', 'Name der Einheit. Hier soll die vollständige, offizielle Bezeichnung der Einheit eingetragen werden. Eine Einheit stellt eine organisatorische Untergliederung einer übergeordneten Institution dar. Es ist möglich, eine Einheit unter einer anderen Einheit anzuordnen (z.B. Referat/Dezernat XY unter Abteilung Z).', 'Referat 606 (DV-Organisation, Umweltinformationssysteme)');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(86, 0, 4210, -1, 'de', 'Institution/Übergeordnete Einheiten)', 'Es werden die übergeordnete Institution und die übergeordneten Einheiten angezeigt.', 'Ministerium für Umwelt und Klimaschutz');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(87, 0, 4300, 2, 'de', 'Anrede', 'Angabe der Anrede für die eingetragene Person. Um eine korrekte Korrespondenz mit der eingetragenen Person zu ermöglichen, soll diese Angabe falsche Anreden der eingetragenen Person vermeiden. Die Standardeinträge (Frau, Herr) sind in der Auswahlliste enthalten. Freie Einträge sind jedoch auch möglich (z.B. Mrs, Mr).', 'Frau');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(88, 0, 4305, 2, 'de', 'Titel', 'Angabe des akademischen Titels der eingetragenen Person. Hier sollen die dem Namen vorangestellten akademischen Titel angegeben werden. Amtstitel (z.B. MinD, OAR) sollen aus Datenschutzgründen nicht eingetragen werden.', 'Prof. Dr.');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(89, 0, 4310, 2, 'de', 'Vorname', 'Angabe des Vornamens der eingetragenen Person. Um Verwechslungen von Trägern gleicher Nachnamen zu vermeiden, soll hier der Vorname der eingetragenen Person genannt werden. Auf Vollständigkeit der Vornamen wird dabei kein Wert gelegt.', 'Rosemarie');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(90, 0, 4315, 2, 'de', 'Name', 'Angabe des Nachnamens der eingetragenen Person. Hier soll der vollständige Nachname einer Person eingetragen werden.', 'vom Busche');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(91, 0, 4400, 0, 'de', 'Straße/Hausnummer', 'Angabe der Hausadresse. Bei der Straße und Hausnummer der Institution sollte es sich um die Adresse der Zentrale bzw. des Hauptgebäudes handeln. Für die zuzuordnenden Einheiten und Personen können eigene Adressen eingetragen werden. Die Angabe der Hausadresse ist Pflicht, sofern kein Postfach angegeben wird.', 'Meyersgrund 14');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(92, 0, 4400, 2, 'de', 'Straße/Hausnummer', 'Angabe der Hausadresse. Bei der Straße und Hausnummer der Person sollte es sich um die Hausadresse des Dienstgebäudes der Person handeln. Die Angabe einer Hausadresse ist Pflicht, sofern kein Postfach angegeben wird.', 'Blocksberg 17');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(93, 0, 4400, 3, 'de', 'Straße/Hausnummer', 'Angabe der Hausadresse. Bei der Straße und Hausnummer der Person sollte es sich um die Hausadresse des Dienstgebäudes der Person handeln. Fehlt der Eintrag für die Institution, ist hier i.d.R. die Privatanschrift einzutragen. Die Angabe einer Hausadresse ist Pflicht, sofern kein Postfach angegeben wird.', 'Blocksberg 17');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(94, 0, 4400, 1, 'de', 'Straße/Hausnummer', 'Angabe der Hausadresse. Bei der Straße und Hausnummer der Einheit sollte es sich um die Adresse des Dienstgebäudes dieser Einheit handeln. Für die zuzuordnenden Personen können eigene Adressen eingetragen werden. Die Angabe einer Hausadresse ist Pflicht, sofern kein Postfach angegeben wird.', 'Vogelfangtrift 15');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(95, 0, 4405, 0, 'de', 'Staat', 'Angabe des postalischen Staatenkürzels. Das international gültige postalische Staatenkürzel ist katalogspezifisch voreingestellt. Es kann bei Bedarf jedoch durch einen freien Eintrag oder durch Auswahl eines anderen Kürzels aus der Auswahlliste überschrieben werden.', 'D');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(96, 0, 4410, -1, 'de', 'PLZ', 'Postleitzahl der Hausadresse (Straße/Hausnummer). Hier ist die Postleitzahl einzugeben, die sich auf die Hausadresse (Straße/Hausnummer) bezieht. Für die Postleitzahl zur Postdresse (Postfach) ist ein eigenes Feld vorgesehen.', '30169');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(97, 0, 4415, 0, 'de', 'Ort', 'Es ist der Ort anzugeben, wo sich die Zentrale bzw. das Hauptgebäude der Institution befindet. Für die zuzuordnenden Einheiten und Personen können eigene Adressen bzw. Ortsangaben eingetragen werden. Für den Fall, dass der Ort für Hausadresse (Straße/Hausnummer) und Postadresse (Postfach) verschieden ist, kann nur eine vollständige Adresse eingetragen werden (Haus- oder Postadresse).', 'Bruchsal');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(98, 0, 4415, 2, 'de', 'Ort', 'Es ist der Ort anzugeben, wo sich das Dienstgebäude befindet, in dem die betreffende Person tätig ist. Für die zugeordnete Institution/Einheiten können eigene Adressen bzw. Ortsangaben eingetragen werden. Für den Fall, dass der Ort für Hausadresse (Straße/Hausnummer) und Postadresse (Postfach) verschieden ist, kann nur eine vollständige Adresse eingetragen werden (Haus- oder Postadresse).', 'Wolgast');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(99, 0, 4415, 3, 'de', 'Ort', 'Es ist der Ort anzugeben, wo sich das Dienstgebäude befindet, in dem die betreffende Person tätig ist. Soll die Privatadresse der betreffenden Person angegeben werden, ist hier der Wohnort anzugeben. Für den Fall, dass der Ort für Hausadresse (Straße/Hausnummer) und Postadresse (Postfach) verschieden ist, kann nur eine vollständige Adresse eingetragen werden (Haus- oder Postadresse).', 'Wolgast');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(100, 0, 4415, 1, 'de', 'Ort', 'Es ist der Ort anzugeben, wo sich das Dienstgebäude der Einheit befindet. Für die zuzuordnenden weiteren Einheiten und Personen können eigene Adressen bzw. Ortsangaben eingetragen werden. Für den Fall, dass der Ort für Hausadresse (Straße/Hausnummer) und Postadresse (Postfach) verschieden ist, kann nur eine vollständige Adresse eingetragen werden (Haus- oder Postadresse).', 'Wolgast');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(101, 0, 4420, -1, 'de', 'Postfach', 'Angabe der Nummer des Postfaches. In Ergänzung zur Hausadresse (Straße/Hausnummer) kann hier ein Postfach angegeben werden. Ist keine Hausadresse angegeben, so ist die Eingabe eines Postfaches zwingend notwendig, um die Adresse speichern zu können. Für den seltenen Fall, dass keine Hausadresse existiert und die Postleitzahl bereits die Nummer des Postfachs darstellt (also keine gesonderte Postfachnummer existiert), ist hier ein Leerzeichen einzutragen.', '30151');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(102, 0, 4425, -1, 'de', 'PLZ (Postfach)', 'Postleitzahl des Postfaches. Hier ist die Postleitzahl einzugeben, die sich auf das Postfach bezieht. Für die Postleitzahl zur Hausadresse (Straße/Hausnummer) ist ein eigenes Feld vorgesehen.', '8977');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(103, 0, 4430, 0, 'de', 'Kommunikation', 'Angabe von Telefonnummern, Faxnummer, E-Mail, URL-Adresse usw. In dieser Tabelle sollen alle Kommunikationsmittel (außer normaler Post) eingetragen werden, mit denen die Institution erreicht werden kann.  Die Bezeichnung kann über eine Auswahlliste eingegeben werden. In der rechten Spalte wird die zugehörige Nummer für Telefon oder Fax bzw. Adresse für E-Mail-Kommunikation eingegeben. Wichtig: Die Angabe der E-Mail Adresse ist Pflicht! (INSPIRE-Pflichtfeld)', 'E-Mail : pressestelle@mu.niedersachsen.de');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(104, 0, 4430, 2, 'de', 'Kommunikation', 'Angabe von Telefonnummern, Faxnummer, E-Mail, URL-Adresse usw. In dieser Tabelle sollen alle Kommunikationsmittel (außer normaler Post) eingetragen werden, mit denen die Institution erreicht werden kann.  Die Bezeichnung kann über eine Auswahlliste eingegeben werden. In der rechten Spalte wird die zugehörige Nummer für Telefon oder Fax bzw. Adresse für E-Mail-Kommunikation eingegeben. Wichtig: Die Angabe der E-Mail Adresse ist Pflicht! (INSPIRE-Pflichtfeld)', 'antje.robbe@mu.niedersachsen.de');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(105, 0, 4430, 3, 'de', 'Kommunikation', 'Angabe von Telefonnummern, Faxnummer, E-Mail, URL-Adresse usw. In dieser Tabelle sollen alle Kommunikationsmittel (außer normaler Post) eingetragen werden, mit denen die Institution erreicht werden kann.  Die Bezeichnung kann über eine Auswahlliste eingegeben werden. In der rechten Spalte wird die zugehörige Nummer für Telefon oder Fax bzw. Adresse für E-Mail-Kommunikation eingegeben. Wichtig: Die Angabe der E-Mail Adresse ist Pflicht! (INSPIRE-Pflichtfeld)', 'antje.robbe@mu.niedersachsen.de');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(106, 0, 4430, 1, 'de', 'Kommunikation', 'Angabe von Telefonnummern, Faxnummer, E-Mail, URL-Adresse usw. In dieser Tabelle sollen alle Kommunikationsmittel (außer normaler Post) eingetragen werden, mit denen die Institution erreicht werden kann.  Die Bezeichnung kann über eine Auswahlliste eingegeben werden. In der rechten Spalte wird die zugehörige Nummer für Telefon oder Fax bzw. Adresse für E-Mail-Kommunikation eingegeben. Wichtig: Die Angabe der E-Mail Adresse ist Pflicht! (INSPIRE-Pflichtfeld)', 'pressestelle@mu.niedersachsen.de');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(107, 0, 4435, 0, 'de', 'Notizen', 'Weitere Angaben zur Institution. Hier können für die Datenauskunft zusätzlich relevante Information eingegeben werden. Dies können z. B. Öffnungszeiten oder Beschreibungen des Anfahrtsweges sein.', 'Besucherverkehr: MO-FR 9-12 Uhr; DO 14-19 Uhr');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(108, 0, 4435, 2, 'de', 'Notizen', 'Weitere Angaben zur Personenadresse. Hier können für die Datenauskunft zusätzlich relevante Information eingegeben werden. Dies können z. B. Öffnungszeiten oder Angaben zur zeitlichen Erreichbarkeit sein.', 'vormittags erreichbar');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(109, 0, 4435, 3, 'de', 'Notizen', 'Weitere Angaben zur Personenadresse. Hier können für die Datenauskunft zusätzlich relevante Information eingegeben werden. Dies können z. B. Öffnungszeiten oder Angaben zur zeitlichen Erreichbarkeit sein.', 'vormittags erreichbar');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(110, 0, 4435, 1, 'de', 'Notizen', 'Weitere Angaben zur Einheit bzw. Abteilung. Hier können für die Datenauskunft zusätzlich relevante Information eingegeben werden. Dies können z. B. Öffnungszeiten oder Beschreibungen des Anfahrtsweges sein.', 'Besucherverkehr: MO-FR 9-12 Uhr; DO 14-19 Uhr');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(111, 0, 4440, 0, 'de', 'Aufgaben', 'Angabe der Zuständigkeiten und Aufgaben einer Institution. In knapper, aussagekräftiger Form sollen die Aufgaben und Zuständigkeiten der eingetragenen Institution dargestellt werden.', 'Erarbeitung von fachlichen Grundlagen für Entscheidungen der Bezirksregierungen im Bereich des Naturschutzes sowie Erstellung von Gutachten in besonderen Fällen gemäß NNatG');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(112, 0, 4440, 2, 'de', 'Aufgaben', 'Angabe der Zuständigkeiten und Aufgaben einer Person. In knapper, aussagekräftiger Form sollen die Aufgaben und Zuständigkeiten der eingetragenen Person dargestellt werden.', 'Vorbereitung von Ausweisungen von Naturschutzgebieten; Erarbeitung von Abgrenzungen der Gebiete; Verordnungsentwurf');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(113, 0, 4440, 3, 'de', 'Aufgaben', 'Angabe der Zuständigkeiten und Aufgaben einer Person. In knapper, aussagekräftiger Form sollen die Aufgaben und Zuständigkeiten der eingetragenen Person dargestellt werden.', 'Vorbereitung von Ausweisungen von Naturschutzgebieten; Erarbeitung von Abgrenzungen der Gebiete; Verordnungsentwurf');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(114, 0, 4440, 1, 'de', 'Aufgaben', 'Angabe der Zuständigkeiten und Aufgaben einer Einheit. In knapper, aussagekräftiger Form sollen die Aufgaben und Zuständigkeiten der eingetragenen Einheit bzw. Abteilung dargestellt werden.', 'Erarbeitung von fachlichen Grundlagen für Ausweisung von Naturschutzgebieten sowie Erstellung von Gutachten gemäß NNatG.');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(115, 0, 4500, 0, 'de', 'Freie Suchbegriffe', 'Eingabe von Schlagworten, über die die Institution bei einer Schlagwortsuche möglichst schnell zu finden ist und die nicht im Thesaurus vorhanden sind. Hier sollen prägnante Begriffe und Termini, die in engem Zusammenhang mit dem Adresse stehen und die nicht im Thesaurus vorhanden sind, eingetragen werden. Dies können Fachgebiete, Zuständigkeiten o.ä. sein. Die Freien Suchbegriffe sind ergänzend zu den Thesaurus-Suchbegriffen anzugeben. Wenn Sie hier einen Thesaurusbegriff eingeben, so wird automatisch vorgeschlagen, diesen in das Feld für die Thesaurusbegriffe zu übernehmen. Wird ein Synonym zu einem Thesaurusbegriff eingegeben, so wird die zusätzliche automatische Übernahme des zugehörigen Thesaurusbegriffs vorgeschlagen.', 'Naturschutz Artenschutz Erfassung');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(116, 0, 4500, 2, 'de', 'Freie Suchbegriffe', 'Eingabe von Schlagworten, über die die Person bei einer Schlagwortsuche möglichst schnell zu finden ist und die nicht im Thesaurus vorhanden sind. Hier sollen prägnante Begriffe und Termini, die in engem Zusammenhang mit dem Adresse stehen und die nicht im Thesaurus vorhanden sind, eingetragen werden. Dies können Fachgebiete, Zuständigkeiten o.ä. sein. Die Freien Suchbegriffe sind ergänzend zu den Thesaurus-Suchbegriffen anzugeben. Wenn Sie hier einen Thesaurusbegriff eingeben, so wird automatisch vorgeschlagen, diesen in das Feld für die Thesaurusbegriffe zu übernehmen. Wird ein Synonym zu einem Thesaurusbegriff eingegeben, so wird die zusätzliche automatische Übernahme des zugehörigen Thesaurusbegriffs vorgeschlagen.', 'Falter Kartierung');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(117, 0, 4500, 3, 'de', 'Freie Suchbegriffe', 'Eingabe von Schlagworten, über die die Person bei einer Schlagwortsuche möglichst schnell zu finden ist und die nicht im Thesaurus vorhanden sind. Hier sollen prägnante Begriffe und Termini, die in engem Zusammenhang mit dem Adresse stehen und die nicht im Thesaurus vorhanden sind, eingetragen werden. Dies können Fachgebiete, Zuständigkeiten o.ä. sein. Die Freien Suchbegriffe sind ergänzend zu den Thesaurus-Suchbegriffen anzugeben. Wenn Sie hier einen Thesaurusbegriff eingeben, so wird automatisch vorgeschlagen, diesen in das Feld für die Thesaurusbegriffe zu übernehmen. Wird ein Synonym zu einem Thesaurusbegriff eingegeben, so wird die zusätzliche automatische Übernahme des zugehörigen Thesaurusbegriffs vorgeschlagen.', 'Falter Kartierung');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(118, 0, 4500, 1, 'de', 'Freie Suchbegriffe', 'Eingabe von Schlagworten, über die die Einheit bei einer Schlagwortsuche möglichst schnell zu finden ist und die nicht im Thesaurus vorhanden sind. Hier sollen prägnante Begriffe und Termini, die in engem Zusammenhang mit dem Adresse stehen und die nicht im Thesaurus vorhanden sind, eingetragen werden. Dies können Fachgebiete, Zuständigkeiten o.ä. sein. Die Freien Suchbegriffe sind ergänzend zu den Thesaurus-Suchbegriffen anzugeben. Wenn Sie hier einen Thesaurusbegriff eingeben, so wird automatisch vorgeschlagen, diesen in das Feld für die Thesaurusbegriffe zu übernehmen. Wird ein Synonym zu einem Thesaurusbegriff eingegeben, so wird die zusätzliche automatische Übernahme des zugehörigen Thesaurusbegriffs vorgeschlagen.', 'Artenschutz Kartierung');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(119, 0, 4510, 0, 'de', 'Thesaurus-Suchbegriffe', 'Eingabe von möglichst mindestens drei Schlagworten, die im Thesaurus verzeichnet sind. Die Verschlagwortung dient dem themenbezogenen Wiederauffinden (Retrieval) der Adresse. Dazu müssen Schlagworte aus dem Thesaurus ausgewählt werden, die die Adresse so genau wie möglich, aber auch so allgemein wie nötig beschreiben. So sollte mindestens ein Schlagwort in der Thesaurushierarchie einen relativ allgemeinen Aspekt der Adresse beschreiben und mindestens ein Schlagwort die Adresse so speziell wie möglich beschreiben. Die Schlagworte können mit Hilfe des Verschlagwortungsassistenten (es werden Schlagworte vorgeschlagen) oder dem Thesaurus-Navigator ausgewählt werden.', 'Naturschutz');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(120, 0, 4510, 2, 'de', 'Thesaurus-Suchbegriffe', 'Eingabe von möglichst mindestens drei Schlagworten, die im Thesaurus verzeichnet sind. Die Verschlagwortung dient dem themenbezogenen Wiederauffinden (Retrieval) der Adresse. Dazu müssen Schlagworte aus dem Thesaurus ausgewählt werden, die die Adresse so genau wie möglich, aber auch so allgemein wie nötig beschreiben. So sollte mindestens ein Schlagwort in der Thesaurushierarchie einen relativ allgemeinen Aspekt der Adresse beschreiben und mindestens ein Schlagwort die Adresse so speziell wie möglich beschreiben. Die Schlagworte können mit Hilfe des Verschlagwortungsassistenten (es werden Schlagworte vorgeschlagen) oder dem Thesaurus-Navigator ausgewählt werden.', 'Naturschutz Schmetterling Kartierung Artenschutz');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(121, 0, 4510, 3, 'de', 'Thesaurus-Suchbegriffe', 'Eingabe von möglichst mindestens drei Schlagworten, die im Thesaurus verzeichnet sind. Die Verschlagwortung dient dem themenbezogenen Wiederauffinden (Retrieval) der Adresse. Dazu müssen Schlagworte aus dem Thesaurus ausgewählt werden, die die Adresse so genau wie möglich, aber auch so allgemein wie nötig beschreiben. So sollte mindestens ein Schlagwort in der Thesaurushierarchie einen relativ allgemeinen Aspekt der Adresse beschreiben und mindestens ein Schlagwort die Adresse so speziell wie möglich beschreiben. Die Schlagworte können mit Hilfe des Verschlagwortungsassistenten (es werden Schlagworte vorgeschlagen) oder dem Thesaurus-Navigator ausgewählt werden.', 'Naturschutz Schmetterling Kartierung Artenschutz');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(122, 0, 4510, -1, 'de', 'Thesaurus-Suchbegriffe', 'Eingabe von möglichst mindestens drei Schlagworten, die im Thesaurus verzeichnet sind. Die Verschlagwortung dient dem themenbezogenen Wiederauffinden (Retrieval) der Adresse. Dazu müssen Schlagworte aus dem Thesaurus ausgewählt werden, die die Adresse so genau wie möglich, aber auch so allgemein wie nötig beschreiben. So sollte mindestens ein Schlagwort in der Thesaurushierarchie einen relativ allgemeinen Aspekt der Adresse beschreiben und mindestens ein Schlagwort die Adresse so speziell wie möglich beschreiben. Die Schlagworte können mit Hilfe des Verschlagwortungsassistenten (es werden Schlagworte vorgeschlagen) oder dem Thesaurus-Navigator ausgewählt werden.', 'Naturschutz Schmetterling Kartierung Artenschutz');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(123, 0, 4520, -1, 'de', 'Höhe', 'Angabe der Werte für die Höhe über einem punkt (siehe Pegel) eingegeben. Ist eine vertikale Ausdehnung vorhanden, so kann für das Maximum ein größerer Wert eingegeben werden. Sollte dies nicht der Fall sein, so ist die Eingabe eines Minimums ausreichend, dieser Wert wird dann automatisch ebenso für den maximalen übernommen.', '100 m');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(124, 0, 5000, 0, 'de', 'Kurzbezeichnung', 'Angabe einer Kurzbezeichnung für ein Objekt. (Wird insbes. von GeoMIS.Bund unterstützt)', 'DTK25 digitale topographische Karte GK25 - Grundkarte');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(125, 0, 5020, -1, 'de', 'Maximum', 'Angabe der Werte für die Höhe über einem punkt (siehe Pegel) eingegeben. Ist eine vertikale Ausdehnung vorhanden, so kann für das Maximum ein größerer Wert eingegeben werden. Sollte dies nicht der Fall sein, so ist die Eingabe eines Minimums ausreichend, dieser Wert wird dann automatisch ebenso für den maximalen übernommen.', 'Minimum 100, Maximum 110');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(126, 0, 5021, -1, 'de', 'Maßeinheit', 'Angabe der Maßeinheit, in der die Höhe gemessen wird.', 'Meter');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(127, 0, 5022, -1, 'de', 'Vertikal Datum', 'Angabe des Referenzpegels, zu dem die Höhe relativ gemessen wird. In Deutschland ist dies i.A. der Pegel Amsterdam.', 'Pegel Amsterdam');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(128, 0, 5030, -1, 'de', 'Zeitbezug des Datensatzes', 'Angabe, wann der Datensatz erstellt, revidiert und/oder publiziert wurde.', '11.11.2003 Erstellung');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(129, 0, 5040, -1, 'de', 'Eignung/Nutzung', 'Angaben über die Verwendungsmöglichkeiten, die diese Daten in Verbindung mit weiteren Informationen erfüllen können.', 'Präsentation des Raumordnungsprogramms auf Basis der topografischen Kartenwerke');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(130, 0, 5041, -1, 'de', 'Sprache des Metadatensatzes', 'Angabe der Sprache des Metadatensatzes.', 'Deutsch');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(131, 0, 5042, -1, 'de', 'Sprache des Datensatzes', 'Angabe der Sprache des Datensatzes.', 'Deutsch');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(132, 0, 5052, -1, 'de', 'Bestellinformation', 'Angabe von generellen Informationen wie Bedingungen oder Konditionen zur Bestellung.', 'Lieferzeit beträgt 3 Wochen');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(133, 0, 5060, 1, 'de', 'Themenkategorie', 'Angabe der Hauptthemen, welche die Metadaten beschreiben. Die Auswahl erfolgt über die vorgegebene Auswahlliste.', 'Umwelt');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(134, 0, 5061, 1, 'de', 'Datensatz/Datenserie', 'Bei den beschriebenen Daten der Klasse Geo-Information/Karte handelt es sich um einen einzelnen Datensatz mit bestimmtem räumlichen Bezug oder um eine Datenserie mit einheitlichem thematischen Bezug und mehreren Datensätzen mit unterschiedlichem räumlichen Bezug (z.B. Datenserie: DTK25)', 'Datensatz');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(135, 0, 5062, 1, 'de', 'Digitale Repräsentation', 'Angabe der Methode, räumliche Daten zu präsentieren. Die Auswahl erfolgt über eine vorgegebene Liste.', 'Vektor');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(136, 0, 5063, 1, 'de', 'Topologieinfo', 'Achtung: Nur aktiv nach Auswahl von "Vektor" bei "Digitale Repräsentation". Es erscheint hier dann eine Auswahlliste mit Begriffen.', 'geometrisch');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(137, 0, 5064, 1, 'de', 'INSPIRE-Themen', 'Auswahl eines INSPIRE Themengebiets zur Verschlagwortung des Datensatzes (INSPIRE-Pflichtfeld)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(138, 0, 5066, 1, 'de', 'Verweis zu Dienst', 'Georeferenzierte Daten, die Basisdaten eines OGC Web-Dienstes sind, können über dieses Feld einen Verweis auf einen beschriebenen OGC Web-Dienst erhalten. Diese Geodaten sind in der Regel eng mit dem Dienst verknüpft ("tightly coupled") und über den verknüpften OGC Web Service direkt erreichbar.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(139, 0, 5069, 1, 'de', 'Höhengenauigkeit', 'Angabe über die Genauigkeit der Höhe z.B. in einem Geländemodell.', '2');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(140, 0, 5070, 1, 'de', 'Sachdaten / Attributinformation', 'Angabe der mit der Geo-Information/Karte verbundenen Sachdaten. Bei Bedarf kann hier eine Auflistung der Attribute des Datenbestandes erfolgen. Die hauptsächliche Nutzung dieses Feldes ist für digitale Geo-Informationen vorgesehen.', 'Baumkartei');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(141, 0, 5201, 3, 'de', 'Name der Operation', 'Name der von einem Dienst bereitgestellten Funktion/Operation. Hier muss ein eindeutiger Bezeichner für die beschriebene Operation eingegeben werden.', 'getMap getCapabilities getFeatureInfo');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(142, 0, 5202, 3, 'de', 'Beschreibung', 'Textliche Beschreibung der Funktionalität der Operation.', 'Die getMap Operation des WMS gibt eine Raster-Repräsentation der in "Basisdaten" beschriebenen digitalen Karte zurück.');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(143, 0, 5203, 3, 'de', 'Unterstütze Plattformen', 'Angaben zur Art der Plattform bzw. Schnittstelle über die der Dienst angesprochen werden kann.', 'Java SQL HTTPGet HTTPSoap');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(144, 0, 5204, 3, 'de', 'Aufruf', 'Eindeutiger Funktionsname über den die Operation aufgerufen werden kann. Bei OGC Web-Diensten sind die jeweiligen spezifizierten REQUEST-Aufrufe zu verwenden.', 'getMap getCapabilities getFeatureInfo');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(145, 0, 5205, 3, 'de', 'Parameter', 'Mögliche Parameter, die bei einem Aufruf der Operation übergeben werden können. - Name/Bezeichner des Parameters. - Richtung des Datenflusses, der durch diesen Parameter erzeugt wird. - Textliche Beschreibung des Parameters. - Ist Optional: Angabe, ob der Parameter optional ist oder nicht. - Angabe, ob eine Mehrfacheingabe des Parameters möglich bzw. notwendig ist.', 'Name: WIDTH Richtung: Eingabe Beschreibung: "Der Parameter WIDTH definiert die Breite des vom WMS erzeugten Kartenbildes" Ist Optional: Ja Mehrfacheingabe: Nein');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(146, 0, 5206, 3, 'de', 'Zugriffsadresse', 'Eindeutige URL über die die Operation aufgerufen werden kann.', 'http://my.host.com/cgi-bin/mapserv?map=mywms.map&');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(147, 0, 5207, 3, 'de', 'Abhängigkeiten', 'Die Namen der Operationen, die vor dem Ausführen der aktuellen Operation ausgeführt werden müssen wenn die Operation als Teil einer Service Chain genutzt werden soll.', 'Für die "getMap" Operation: "getCapabilities"');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(148, 0, 7000, -1, 'de', 'Fachbezug', 'Die Objektklassen unterscheiden sich durch unterschiedliche Beschreibungsfelder in der Registerkarte Fachbezug:\n- Organisationseinheit/Fachaufgabe: Zuständigkeiten von Institutionen oder Abteilungen, Fachgebiete, Fachaufgaben.\n- Geo-Information/Karte: GIS-Daten, analoge Karten oder Kartenwerke.\n- Dokument/Bericht/Literatur: Broschüren, Bücher, Aufsätze, Gutachten, etc. Von Interesse sind insbesondere Dokumente, welche nicht über den Buchhandel oder über Bibliotheken erhältlich sind (""graue Literatur"").\n- Geodatendienst: Dienste die raumbezogene Daten zur Verfügung stellen, insbesondere Dienste im Rahmen von INSPIRE, der GDI-DE oder der GDIs der Länder.\n- Vorhaben/Projekt/Programm: Forschungs- und Entwicklungsvorhaben, Projekte unter Beteiligung anderer Institutionen oder privater Unternehmen, Schutzprogramme; von besonderem Interesse sind Vorhaben/Projekte/Programme, in denen umweltrelevante Datenbestände entstehen.\n- Datensammlung/Datenbank: Analoge oder digitale Sammlung von Daten. Beispiele: Messdaten, statistische Erhebungen, Modelldaten, Daten zu Anlagen.\n- Dienst/Anwendung/Informationssystem: zentrale Auskunftssysteme, welche in der Regel auf eine oder mehrere Datenbanken zugreifen und diese zugänglich machen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(149, 0, 7001, -1, 'de', 'Raumbezugssystem', 'Angabe der Bezugssysteme (Lage, Höhe, Schwere), die der Erarbeitung des zu beschreibenden Datenbestandes oder der fertigen Karte zugrunde gelegt wurden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(150, 0, 7002, -1, 'de', 'Zeitbezug', 'Angabe des Zeitbezugs des Datensatzes und des Dateninhalts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(151, 0, 7003, -1, 'de', 'Zusatzinformation', 'Angabe von allgemeinen Daten wie Sprache und Ort der Veröffentlichung des Datensatzes', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(152, 0, 7004, -1, 'de', 'Verfügbarkeit', 'Angabe, ob und in welcher Form es Zugangs- oder Nutzungsbeschränkungen gibt', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(153, 0, 7005, -1, 'de', 'Verschlagwortung', 'Angabe von Schlagworten, um die Suche nach Daten zu verbessern', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(154, 0, 7006, -1, 'de', 'Als Katalogseite anzeigen', 'Auswahl, ob Datenobjekt als Katalogseite in PortalU bzw. ingrid angezeigt werden soll', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(155, 0, 7007, -1, 'de', 'Verweise', 'Anzeige von Verweisen auf andere Objekte/URLs und Möglichkeit der Neueingabe bzw. Bearbeitung von Verweisen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(156, 0, 7008, -1, 'de', 'Adresse und Aufgaben - Sektionsüberschrift', 'Angabe von Adressdaten zur Kommunikation mit entsprechender Kontaktstelle, sowie Zusatzinformationen (Notizen und Aufgaben) zu dieser', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(157, 0, 7009, -1, 'de', 'Verschlagwortung - Sektionsüberschrift', 'Angabe von Schlagworten, um die Suche nach den Adressdaten zu vereinfachen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(158, 0, 7010, -1, 'de', 'Zugeordnete Objekte', 'In dieser Tabelle werden alle Verweise von denjenigen Objekten aufgeführt, welche auf die aktuelle Adresse verweisen. Das Editieren oder Hinzufügen ist nicht möglich. Sollen die Verweise geändert oder ergänzt werden, so muss zu dem entsprechenden Objekt gewechselt werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(159, 0, 7011, -1, 'de', 'Allgemeines', 'Eintrag von allgemeinen Informationen zum Objekt (Beschreibung, Kontaktinformation)', 'Mustermann, Erika');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(160, 0, 7012, -1, 'de', 'i-Info Umgerechnete Koordinaten', 'Umrechnung der unter Geothesaurus-Raumbezug ausgewählten Daten in die in der Auswahlbox zur Verfügung stehenden Koordinatensysteme', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(161, 0, 7013, -1, 'de', 'i-Info Umgerechnete Koordinaten - freie Raumbezüge', 'Umrechnung der unter freier Raumbezug ausgewählten Daten in die in der Auswahlbox zur Verfügung stehenden Koordinatensysteme', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(162, 0, 7014, -1, 'de', 'Vektorformat', 'Achtung: Nur aktiv nach Auswahl von "Vektor" bei "Digitale Repräsentation". Es können hier Topologieinformationen, Geometrietyp (Angabe der geometrischen Objekte, zur Beschreibung der geometrischen Lage) und Elementanzahl (Angaben der Anzahl der Punkt- oder Vektortypelemente) angegeben werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(163, 0, 7015, -1, 'de', 'Operationen', 'Angabe von Operationen bezüglich Webdiensten wie getMap, getCapabilities und getFeatureInfo', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(164, 0, 7016, -1, 'de', 'Objekte anlegen/bearbeiten', 'Formular zum Anlegen und Bearbeiten von Metadatenobjekten der 6 verschiedenen Objektklassen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(165, 0, 7017, -1, 'de', 'Einheit/Institution', 'Angabe des Namens der Einheit bzw. Institution, nach der gesucht werden soll', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(166, 0, 7018, -1, 'de', 'Nachname', 'Angabe des Namens der Person, nach der gesucht werden soll', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(167, 0, 7019, -1, 'de', 'Vorname', 'Angabe des Vornamens der Person, nach der gesucht werden soll', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(168, 0, 7020, -1, 'de', 'Geothesaurus-Navigator - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(169, 0, 7021, -1, 'de', 'Geothesaurus-Navigator - Räumliche Einheit festlegen', 'Eingabe der Räumlichen Einheit, deren Koordinaten gesucht werden sollen z.B. Berlin', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(170, 0, 7022, -1, 'de', 'Raumbezug hinzufügen', 'Hier können die Koordinaten einer Bounding Box angegeben werden, die das Objekt umschließt. Bei einer punktförmigen Ortsangabe wird zweimal die gleiche Koordinate angegeben: Länge1=Länge2 und Breite1=Breite2', ' ');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(171, 0, 7023, -1, 'de', 'Freier Raumbezug (Eingabefeld)', 'Angabe eines frei wählbaren Namens für den neuen Raumbezug.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(172, 0, 7024, -1, 'de', 'Unten links (Rechtswert/Länge)', 'Angabe des x-Werts der unteren linken Koordinate der Bounding Box', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(173, 0, 7025, -1, 'de', 'Unten links (Hochwert/Breite)', 'Angabe des y-Werts der unteren linken Koordinate der Bounding Box', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(174, 0, 7026, -1, 'de', 'Oben rechts (Rechtswert/Länge)', 'Angabe des x-Werts der oberen rechten Koordinate der Bounding Box', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(175, 0, 7027, -1, 'de', 'Oben Rechts (Hochwert/Breite)', 'Angabe des y-Werts der oberen rechten Koordinate der Bounding Box', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(176, 0, 7028, -1, 'de', 'Koordinatensystem', 'Auswahl des für die Koordinatenangabe verwendeten Koordinatensystems aus einer Liste.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(177, 0, 7029, -1, 'de', 'Verschlagwortungsassistent', 'Hier werden Schlagworte auf Basis der bereits ausgefüllten Textfelder automatisch vorgeschlagen und können bei Bedarf übernommen werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(178, 0, 7030, -1, 'de', 'Thesaurus-Navigator', 'Hier kann nach Diskriptoren oder Ordnungsbegriffen im Thesaurus gesucht werden oder es können Schlagworte direkt im Hierarchiebaum ausgewählt werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(179, 0, 7031, -1, 'de', 'Suche nach Deskriptoren und Ordnungsbegriffen', 'Suche nach Diskriptoren oder Ordnungsbegriffen im Thesaurus. Die Ergebnisse der Suche werden auf dem Karteiblatt "Ergebnisliste" angezeigt und können von dort aus übernommen werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(180, 0, 7032, -1, 'de', 'Liste der Deskriptoren', 'Anzeige der aus dem Hierarchiebaum oder der Such-Ergebnisliste hinzugefügten Deskriptoren. Über den Button "Übernehmen" werden diese bei den Thesaurus-Suchbegriffen ergänzt.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(181, 0, 7033, -1, 'de', 'Verweise anlegen/bearbeiten', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(182, 0, 7034, -1, 'de', 'Verweis von', 'Auf der rechten Seite unter "Verweisliste" werden alle vorhandenen Verweise zu anderen Objekten oder URLs aufgelistet. Durch die Anwahl dieser können die Verweise editiert werden. Über den Button "Neu" können neue Verweise hinzugefügt werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(183, 0, 7035, -1, 'de', 'Objektname', 'Anzeige des Namens des aktuell in Bearbeitung befindlichen Objekts.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(184, 0, 7037, -1, 'de', 'Verweistyp', 'Auswahl, ob auf Objekte des Katalogs oder auf Webseiten (URLs) verwiesen werden soll.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(185, 0, 7038, -1, 'de', 'Verweis auf', 'Anzeige des Objekts oder der URL auf die der Verweis zielt. ', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(186, 0, 7040, -1, 'de', 'Objektklasse', 'Hier wird die Klasse des Objekts angezeigt, auf welche der Verweis zielt.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(187, 0, 7041, -1, 'de', 'Operation hinzufügen /bearbeiten', 'Angabe von Operationen bezüglich Webdiensten wie getMap, getCapabilities und getFeatureInfo', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(188, 0, 7042, -1, 'de', 'Kommentare ansehen/hinzufügen - Allgemeine Hilfe', 'Angabe von Operationen bezüglich Webdiensten wie getMap, getCapabilities und getFeatureInfo', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(189, 0, 7043, -1, 'de', 'Kommentare ansehen/hinzufügen - Neuen Kommentar verfassen', 'Angabe von Operationen bezüglich Webdiensten wie getMap, getCapabilities und getFeatureInfo', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(190, 0, 7044, -1, 'de', 'Adresstitel - Formularkopf', 'Angabe eines frei wählbaren Namens für das Adressobjekt', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(191, 0, 7045, -1, 'de', 'Adresstyp - Formularkopf', 'Angabe des Typs des Adressobjekts - Institution, Einheit oder Person', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(192, 0, 7046, -1, 'de', 'Erweiterte Suche Objekte - Thema - Suchmodus', 'Entscheidung, ob nach ganzen Suchwörtern oder nach Teilzeichenketten gesucht werden soll', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(193, 0, 7047, -1, 'de', 'Erweiterte Suche Objekte - Thema - Fachwörterbuch - Bitte geben Sie die Thesaurus-Begriffe an, nach denen gesucht werden soll', 'Eingabe eines (Thesaurus-) Begriff, zu dem ähnliche Begriffe gesucht werden sollen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(194, 0, 7048, -1, 'de', 'Erweiterte Suche Objekte - Thema - Fachwörterbuch - Suchbegriffe', 'aus Thesaurus-Suche übernommene Suchbegriffe, mit denen ein Datenobjekt gefunden werden soll', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(195, 0, 7049, -1, 'de', 'Erweiterte Suche Objekte - Raum - Geothesaurus-Raumbezug - Bitte geben Sie die Raumbezüge an, nach denen gesucht werden soll', 'Eingabe von räumlichen Begriffen, mit denen im Geothesaurus gesucht werden soll z.B. Berlin', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(196, 0, 7050, -1, 'de', 'Erweiterte Suche Objekte - Raum - Geothesaurus-Raumbezug - Suchbegriffe', 'aus Geothesaurus-Suche übernommene Begriffe, mit ', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(197, 0, 7051, -1, 'de', 'Erweiterte Suche Objekte - Raum - Freier Raumbezug', 'Auswahl eines im Katalog verwendeten freien Raumbezugs, zur Suche nach Datenobjekten', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(198, 0, 7052, -1, 'de', 'Erweiterte Suche Objekte - Zeit - Zeitbezug', 'Angabe eines Zeitpunkts oder Zeitraums zur Lokalisierung eines Objekts (Suche nach "Zeitbezug des Dateninhaltes")', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(199, 0, 7053, -1, 'de', 'Erweiterte Suche Objekte - Zeit - Erweiterung des Zeitbezuges', 'Angabe, ob der Zeitpunkt/Zeitraum des gesuchten Objekts innerhalb oder außerhalb des oben angegeben Zeitbezugs liegt', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(200, 0, 7054, -1, 'de', 'Erweiterte Suche Adressen - Thema - Suchmodus', 'Entscheidung, ob nach ganzen Suchwörtern oder nach Teilzeichenketten gesucht werden soll', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(201, 0, 7055, -1, 'de', 'Erweiterte Suche Adressen - Thema - Suche in folgenden Feldern', 'Entscheidung, ob in allen Textfeldern eines Datenobjekts oder nur in bestimmten gesucht werden soll', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(202, 0, 7056, -1, 'de', 'Erweiterte Suche Adressen - Raum - Fügen Sie hier Ihrer Suchanfrage konkrete Adressinformationen zu', 'Suche von Adressobjekten nach Adressinformationen wie Strasse, PLZ oder Ort', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(203, 0, 7057, -1, 'de', 'Erweiterte Suche Adressen - Raum - Straße', 'Suche nach Straßennamen in einem Adressobjekt', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(204, 0, 7058, -1, 'de', 'Erweiterte Suche Adressen - Raum - PLZ', 'Suche nach Postleitzahlen in einem Adressobjekt', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(205, 0, 7059, -1, 'de', 'Erweiterte Suche Adressen - Raum - Ort', 'Suche nach Ortsnamen in einem Adressobjekt', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(206, 0, 7060, -1, 'de', 'Suche - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(207, 0, 7061, -1, 'de', 'Thesaurusnavigator - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(208, 0, 7062, -1, 'de', 'Thesaurusnavigator - Einstieg in die Thesaurus-Hierarchie', 'Suche nach Thesaurusbegriffen und deren Position im Thesaurus-Hierarchiebaum', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(209, 0, 7063, -1, 'de', 'Datenbank-Suche - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(210, 0, 7064, -1, 'de', 'Datenbank-Suche - Datenbank-Suche', 'Suche nach Objekten mit Hilfe der Hibernate Query Language-Syntax (HQL)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(211, 0, 7065, -1, 'de', 'Qualitätssicherung - Bearbeitung/Verantwortlich - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(212, 0, 7066, -1, 'de', 'Qualitätssicherung - Bearbeitung/Verantwortlich - Objekte / Adressen deren Verfallszeitspanne abgelaufen ist', 'Anzeige von Objekten und Adressen, die überprüft werden müssen, da Verfallszeitspanne abgelaufen ist', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(213, 0, 7067, -1, 'de', 'Qualitätssicherung - Bearbeitung/Verantwortlich - Objekte / Adressen die sich in von Ihnen derzeit in Bearbeitung befinden', 'Anzeige von Objekten und Adressen die sich im Bearbeitungszustand befinden d.h. nicht veröffentlicht sind', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(214, 0, 7068, -1, 'de', 'Qualitätssicherung - Bearbeitung/Verantwortlich - Objekte / Adressen die an die Qualitätssicherung überwiesen bzw. von der Qualitätssicherung rücküberweisen wurden', 'Anzeige von Objekten, die sich im Qualitätssicherungszyklus befinden d.h. sind nicht veröffentlicht', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(215, 0, 7069, -1, 'de', 'Qualitätssicherung - Qualitätssicherung - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(216, 0, 7070, -1, 'de', 'Qualitätssicherung - Qualitätssicherung - Objekte / Adressen die Ihnen als Qualitätssichernder zugewiesen wurden', 'Anzeige von Objekten/Adressen, die die Qualitätssicherung überprüfen muss', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(217, 0, 7071, -1, 'de', 'Qualitätssicherung - Qualitätssicherung - Objekte / Adressen die sich in Bearbeitung befinden, für die Sie Qualitätssichernder sind', 'Anzeige von Objekten/Adressen, die die Qualitätssicherung überprüfen muss', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(218, 0, 7072, -1, 'de', 'Qualitätssicherung - Qualitätssicherung - Objekte / Adressen deren Verfallszeitspanne abgelaufen ist, für die Sie Qualitätssichernder sind', 'Anzeige von Objekten/Adressen, für die Sie als QS-Beauftragter zuständig sind u. überarbeitet werden', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(219, 0, 7073, -1, 'de', 'Qualitätssicherung - Qualitätssicherung - Objekte deren Raumbezug abgelaufen ist, für die Sie Qualitätssichernder sind', 'Anzeige von Objekten/Adressen, deren Raumbezug abgelaufen ist und für die Sie als QS zuständig sind - müssen von Ihnen überprüft werden', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(220, 0, 7074, -1, 'de', 'Objekt auswählen', 'Mit Hilfe des Strukturbaums kann ein Objekt ausgewählt werden, auf welches verwiesen werden soll. Nach der Auswahl ist noch der Button "Zuweisen" zu betätigen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(221, 0, 8000, -1, 'de', 'Katalogeinstellungen', 'Angabe von Informationen zum Katalog wie z.B. Name des Katalogs, Anbieter oder Sprache des Katalogs', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(222, 0, 8001, -1, 'de', 'Name des Katalogs', 'Der Name des Katalogs sollte den Inhalt des Katalogs widerspiegeln.', 'UDK Niedersachsen');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(223, 0, 8002, -1, 'de', 'Name des Partners', 'Angabe des Namens des Partners', 'Bund');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(224, 0, 8003, -1, 'de', 'Name des Anbieters', 'Angabe des Namen des Anbieters', 'Ministerium für Landwirtschaft und Umwelt Sachsen-Anhalt');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(225, 0, 8004, -1, 'de', 'Staat', 'Angabe des Staates, in dem der Katalog basiert', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(226, 0, 8005, -1, 'de', 'Katalogsprache', 'Angabe der Sprache des Katalogs', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(227, 0, 8006, -1, 'de', 'Raumbezug', 'Hier kann über die Schaltfläche "Raumbezug auswählen" die Räumliche Einheit ausgewählt werden', 'Minden-Lübbecke');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(228, 0, 8007, -1, 'de', 'Raumbezug auswählen', 'Hier kann nach eine räumliche Einheit für den Katalog ausgewählt werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(229, 0, 8008, -1, 'de', 'Räumliche Einheit festlegen', 'Hier kann ein Ort angegeben werden, zu dem die im Geothesaurus vorhandenen räumlichen Einheiten ausgegeben und ausgewählt werden können.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(230, 0, 8009, -1, 'de', 'Feldeinstellungen', 'Hier kann ausgewählt werden, ob ein Feld beim ersten Öffnen eines Objekt sichtbar ist oder man es erste ausklappen muss.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(231, 0, 8010, -1, 'de', 'Folgende Felder auch in der Standard-Ansicht anzeigen', 'Liste aller Felder des Katalogs mit der entsprechenden Auswahl der Ansicht (sofort sichtbar oder erst nach dem Ausklappen)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(232, 0, 8011, -1, 'de', 'Nutzeradministration', 'Hier können Nutzer administriert werden, die mit dem Metadaten-Editor arbeiten dürfen. Über das Hinzufügen einer Gruppe, erhält der Nutzer seine entsprechenden Rechte für das Arbeiten mit dem Katalog. Es können nur neue Nutzer hinzugefügt werden, die im Portal schon angelegt wurden und bisher noch keine Rechte für den Editor haben. ', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(233, 0, 8012, -1, 'de', 'Nutzerdaten', 'Hier können Details zum entsprechenden Nutzer geändert oder geprüft werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(234, 0, 8013, -1, 'de', 'Login-Name des entsprechenden Nutzers. Entspricht dem Namen welcher im Portal angegeben wurde.', '', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(235, 0, 8014, -1, 'de', 'Rolle', 'Die Rolle des Nutzers wird über die Portaladministration vergeben und hier nur angezeigt. Möglich sind Katalogadministrator, Metadaten-Administrator (mit QS Rechten) und (Keine Vorschläge)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(236, 0, 8015, -1, 'de', 'Adressverweise', 'Zuweisung einer Adresse aus dem Katalog - über den Button "Adresse suchen" kann eine Adresse aus dem Katalogbestand ausgewählt werden', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(237, 0, 8016, -1, 'de', 'Gruppe', 'Über die Gruppe werden die Rechte für die Bearbeitung von Objekten oder Adressen in bestimmten Teilbäumen des Katalogs vergeben.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(238, 0, 8017, -1, 'de', 'Gruppenadministration', 'Über die Gruppe werden die Rechte für die Bearbeitung von Objekten oder Adressen in bestimmten Teilbäumen des Katalogs vergeben. Außerdem kann ausgewählt werden ob der Benutzer Objekte und Adressen freigeben darf (Qualitätssichernder).', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(239, 0, 8018, -1, 'de', 'Gruppendaten', 'Eingabe eines Namens für eine neue Gruppe - sollte deren Verantwortung/Funktion erklären', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(240, 0, 8019, -1, 'de', 'Gruppennamen', 'Name der Gruppe - sollte deren Funktion erklären', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(241, 0, 8020, -1, 'de', 'Robot-Objekte und - Adressen anlegen', 'Diese Funktion erlaubt es neue Teilbäume auf der gleichen Ebene wie das freigegebene Objekt/Objektbaum bzw. Adressen zu erstellen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(242, 0, 8021, -1, 'de', 'Qualitätssichernder', 'Ein Qualitätssichernder hat das Recht geänderte Objekte freizugeben, so dass sie anschließen über die Portalsuche zu finden sind.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(243, 0, 8022, -1, 'de', 'Berechtigungen für Objekte', 'Hier können Objekte ausgewählt werden, die die Gruppe bearbeiten darf. Teilbaum bedeutet, dass der gesamt Teilbaum unterhalb des ausgewählten Objekts bearbeitet werden kann.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(244, 0, 8023, -1, 'de', 'Berechtigungen für Adressen', 'Hier können Adressen ausgewählt werden, die die Gruppe bearbeiten darf. Teilbaum bedeutet, dass der gesamt Teilbaum unterhalb der ausgewählten Adresse bearbeitet werden kann.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(245, 0, 8024, -1, 'de', 'Berechtigungsübersicht', 'Hier wird angezeigt, welcher Nutzer ein ausgewähltes Objekt bearbeiten darf, welche Rolle er besitzt, ob er den darunterliegenden Teilbaum bearbeiten darf und ob er QS-Rechte besitzt.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(246, 0, 8025, -1, 'de', 'Berechtigungen für Objekte', 'Auswahl eines Objekts, für das alle Nutzer angezeigt werden sollen, die dieses bearbeiten dürfen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(247, 0, 8026, -1, 'de', 'Berechtigungen für Adressen', 'Auswahl einer Adresse, für die alle Nutzer angezeigt werden sollen, die diese bearbeiten dürfen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(248, 0, 8028, -1, 'de', 'Analyse - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(249, 0, 8029, -1, 'de', 'Dubletten - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(250, 0, 8030, -1, 'de', 'Dubletten - Objektname', 'Anzeige des Namens eines in der linken Spalte ausgewählten Objekts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(251, 0, 8031, -1, 'de', 'Dubletten - Klasse', 'Anzeige der Objekt-Klasse eines in der linken Spalte ausgewählten Objekts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(252, 0, 8032, -1, 'de', 'Dubletten - Objektbeschreibung', 'Anzeige der Objektbeschreibung eines in der linken Spalte ausgewählten Objekts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(253, 0, 8033, -1, 'de', 'URL-Pflege - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(254, 0, 8034, -1, 'de', 'URL-Pflege - Markierte URLs durch folgende URL ersetzen', 'Eingabe einer neuen URL, die die in der Tabellen markierten URLs ersetzen soll', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(255, 0, 8035, -1, 'de', 'Auswahl-/ISO-Codelistenpflege - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(256, 0, 8036, -1, 'de', 'Auswahlliste - Auswahllistenpflege', 'Auswahl der zu prüfenden Auswahl- oder ISO-Codeliste', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(257, 0, 8037, -1, 'de', 'Defaultwert einstellbar', 'Auswahl, ob und welcher Eintrag der Liste per Default angezeigt werden soll d.h. als erster in der Auswahlbox sichtbar ist', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(258, 0, 8038, -1, 'de', 'Auswahlliste - Rechtliche Grundlagen', 'Bearbeitung der Auswahlliste "rechtliche Grundlage" - keine Auswahl einer anderen Liste möglich', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(259, 0, 8039, -1, 'de', 'Zusätzliche Felder - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(260, 0, 8040, -1, 'de', 'Zusätzliche Felder - Feldname', 'Eingabe eines Namens für ein neues, zusätzliches Feld', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(261, 0, 8041, -1, 'de', 'Zusätzliche Felder - Länge', 'Angabe der Länge des Textfeldes - auch bei Auswahllisten kann Nutzer zus. Einträge vornehmen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(262, 0, 8042, -1, 'de', 'Zusätzliche Felder - Typ', 'Auswahl des Typ des neuen Zusatzfelds  - einfaches Textfeld oder Auswahl-Liste mit mehreren Einträgen möglich', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(263, 0, 8043, -1, 'de', 'Adresse löschen - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(264, 0, 8044, -1, 'de', 'Adresse löschen - Titel', 'Anzeige des Namens des im Hierarchiebaum ausgewählten Adressobjekts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(265, 0, 8045, -1, 'de', 'Adresse löschen - Erstellt am', 'Anzeige des Erstellungsdatums des im Hierarchiebaum ausgewählten Adressobjekts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(266, 0, 8046, -1, 'de', 'Adresse löschen - Verantwortlicher', 'Anzeige des Verantwortlichen des im Hierarchiebaum ausgewählten Adressobjekts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(267, 0, 8047, -1, 'de', 'Adresse löschen - ID', 'Anzeige der Objekt-ID des im Hierarchiebaum ausgewählten Adressobjekts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(268, 0, 8048, -1, 'de', 'Adresse löschen - Qualitätssichernder', 'Anzeige des QS-Beauftragten für das ausgewählte Adressobjekt', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(269, 0, 8049, -1, 'de', 'Adresse löschen - Neue Adresse -Titel ', 'Anzeige des Namens des im Hierarchiebaum ausgewählten Adressobjekts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(270, 0, 8050, -1, 'de', 'Adresse löschen - Neue Adresse - Erstellt am', 'Anzeige des Erstellungsdatums des im Hierarchiebaum ausgewählten Adressobjekts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(271, 0, 8051, -1, 'de', 'Adresse löschen - Neue Adresse - Verantwortlicher', 'Anzeige des Verantwortlichen des im Hierarchiebaum ausgewählten Adressobjekts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(272, 0, 8052, -1, 'de', 'Adresse löschen - Neue Adresse - ID', 'Anzeige der Objekt-ID des im Hierarchiebaum ausgewählten Adressobjekts', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(273, 0, 8053, -1, 'de', 'Adresse löschen - Neue Adresse - Qualitätssichernder', 'Anzeige des QS-Beauftragten für das ausgewählte Adressobjekt', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(274, 0, 8054, -1, 'de', 'Suchbegriffe - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(275, 0, 8055, -1, 'de', 'Suchbegriffe - Aktualisierungs-Datensatz auswählen', 'Auswahl einer lokalen Datei mit aktualisierten Thesaurusdaten', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(276, 0, 8056, -1, 'de', 'Raumbezüge - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(277, 0, 8057, -1, 'de', 'Raumbezüge - Aktualisierungs-Datensatz auswählen', 'Auswahl einer lokalen Datei mit aktualisierten Raumbezugsdaten', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(278, 0, 8058, -1, 'de', 'Allgemeine Einstellungen - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(279, 0, 8059, -1, 'de', 'Allgemeine Einstellungen - Allgemeine Einstellungen', 'Verhalten für automatisches Speichern und für automatische Session-Erneuerung einstellen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(280, 0, 8060, -1, 'de', 'File Dialog - Datei auswählen', 'Auswahl der lokalen XML-Importdatei (Import aller Codelisten)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(281, 0, 8061, -1, 'de', 'GetCapabilities Assistent - Capabilities URL', 'Eintrag der getCapability-URL des entsprechenden Dienstes', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(282, 0, 8063, -1, 'de', 'Allgemeiner Erfassungsassistent - URL der Internetseite', 'Eintrag der URL, der zu analysierenden Internetseite', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(283, 0, 8064, -1, 'de', 'Allgemeiner Erfassungsassistent - Anzahl der zu analysierenden Wörter', 'Angabe der max. Anzahl der zu analysierenden Wörter - Ändert die Dauer der Analyse', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(284, 0, 8065, -1, 'de', 'Allgemeiner Erfassungsassistent - Zeige die Beschreibung der übergebenen Webseite an', 'Optionales Analyse der Beschreibung der Webseite aus dem Meta-Tag "description"', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(285, 0, 8066, -1, 'de', 'Allgemeiner Erfassungsassistent - Titel übernehmen', 'Auswahl, ob Titel für neues Datenobjekt übernommen werden soll (Meta-Tag "title")', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(286, 0, 8067, -1, 'de', 'Allgemeiner Erfassungsassistent - Beschreibung übernehmen', 'Auswahl, ob Beschreibung für neues Datenobjekt übernommen werden soll (Meta-Tag "description")', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(287, 0, 8068, -1, 'de', 'Allgemeiner Erfassungsassistent - Inhalt der Webseite übernehmen', 'Auswahl, ob Begriffe der SNS-Analyse der Webseiteninhalte übernommen werden sollen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(288, 0, 8069, -1, 'de', 'Allgemeiner Erfassungsassistent - Deskriptoren', 'Auswahl, ob Descriptoren zur Verschlagwortung des Objekts aus der SNS-Analyse der Webseite übernommen werden sollen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(289, 0, 8070, -1, 'de', 'Allgemeiner Erfassungsassistent - Alle Deskriptoren auswählen', 'Auswahl, ob alle angegebenen Descriptoren übernommen werden sollen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(290, 0, 8071, -1, 'de', 'Allgemeiner Erfassungsassistent - Geothesaurus-Raumbezug', 'Liste möglicher Raumbezüge aus der SNS-Analyse der Webseite', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(291, 0, 8072, -1, 'de', 'Allgemeiner Erfassungsassistent - Alle Raumbezüge auswählen', 'Auswahl, ob alle o.g.  Raumbezüge verwendet werden sollen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(292, 0, 8073, -1, 'de', 'Allgemeiner Erfassungsassistent - Zeitbezug', 'Auswahl, ob vorgeschlagener Zeitbezug für Objekt genutzt werden soll', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(293, 0, 8074, -1, 'de', 'Import - Import-Datei', 'Auswahl einer lokalen Datei mit zu importierenden Datensätzen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(294, 0, 8075, -1, 'de', 'Import - Importierte Datensätze veröffentlichen', 'Auswahl, ob neue Datensätze direkt veröffentlicht werden sollen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(295, 0, 8076, -1, 'de', 'Import - Importierte Datensätze ausschließlich unter dem gewählten Importknoten anlegen', 'Auswahl, ob importierte Datensätze auch an evtl. vorhandener ehemaliger Position eingefügt werden sollen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(296, 0, 8077, -1, 'de', 'Import - Ausgewähltes übergeordnetes Objekt', 'Auswahl eines Eltern-Objekt, unter das die import. Objekte eingehängt werden sollen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(297, 0, 8078, -1, 'de', 'Import - Ausgewählte übergeordnete Adresse', 'Auswahl eines Eltern-Objekt, unter das die import. Objekte eingehängt werden sollen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(298, 0, 8079, -1, 'de', 'Import - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(299, 0, 8080, -1, 'de', 'Export - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(300, 0, 8081, -1, 'de', 'Export - Teilexport', 'Export von Datensätzen, bei denen der ausgewählten Begriff im Feld "xml-Export-Kriterium" steht', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(301, 0, 8082, -1, 'de', 'Export - Teilbaumexport', 'Export aller Datensätze des ausgewählten Teilbaums', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(302, 0, 8083, -1, 'de', 'Export - Nur der ausgewählte Datensatz', 'Export nur des ausgewählten Datensatzes', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(303, 0, 8084, -1, 'de', 'Zusätzliche Felder - Auswahlliste bearbeiten', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(304, 0, 8085, -1, 'de', 'Import - Teilbaumimport', 'Auswahl der Eltern-Objekte, unter das die Importobjekte gehängt werden sollen', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(305, 0, 8086, -1, 'de', 'Vergleichsansicht Adressen - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(306, 0, 8087, -1, 'de', 'Vergleichsansicht Objekte - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(307, 0, 8088, -1, 'de', 'Allgemeiner Erfassungsassistent - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(308, 0, 8089, -1, 'de', 'GetCapabilities Assistent - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(309, 0, 8090, -1, 'de', 'File Dialog - Allgemeine Hilfe', '[?]', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(310, 0, 10006, -1, 'de', 'Geothesaurus-Raumbezug', 'Informationen über die räumliche Zuordnung des in dem Objekt beschriebenen Datenbestand. Über den Geothesaurus-Navigator kann nach den Koordinaten der räumlichen Einheit gesucht werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(311, 0, 10008, -1, 'de', 'Freier Raumbezug ', 'Informationen über die räumliche Zuordnung des in dem Objekt beschriebenen Datenbestand. Es können frei wählbare Raumbezugs-Koordinaten hinzugefügt werden.', 'EPSG:4326 / WGS 84 / geographisch');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(312, 0, 10011, -1, 'de', 'Zeitbezug des Dateninhalts', 'Hier sollte der Entstehung der eigentlichen (Umwelt-Daten (z.B. Messdaten) eingetragen werden (Erstellungsdatum, Publikation, letzte Änderung)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(313, 0, 10013, -1, 'de', 'Herstellungszweck', 'Grund für die Datenerhebung', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(314, 0, 10014, -1, 'de', 'Umweltthemen', 'Die Verschlagwortung mit den Begriffen der Umweltbereiche soll dazu dienen, eine Zuordnung der Objekte zu grob eingeteilten Umweltthemen und Kategorien herzustellen. Diese Themen und Kategorien sind in den Auswahllisten enthalten. Es können mehrere Einträge vorgenommen werden. Über das Auswahlfeld "Als Katalogseite anzeigen" kann entschieden werden, ob das Objekt als Katalogseite verwendet werden soll.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(315, 0, 10015, -1, 'de', 'Themen', 'Hier müssen zum Objekt passende  Themenbereiche aus einer Liste ausgewählt werden. Es muss mind. ein Themenbereich ausgewählt werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(316, 0, 10016, -1, 'de', 'Kategorie', 'Hier müssen zum Objekt passende Kategorien aus einer Liste ausgewählt werden. Es muss mind. eine Kategorie ausgewählt werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(317, 0, 10021, -1, 'de', 'Identifikator der Datenquelle', 'Hier muss ein eindeutiger Name (Identifikator) für jede im Datenobjekt beschriebene Datenquelle (z.B. eine Karte) vergeben werden. (INSPIRE-Pflichtfeld)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(318, 0, 10022, 3, 'de', 'Klassifikation des Dienstes', 'Auswahl der Beschreibung des Dienstes. Dieses Feld dient in erster Linie der Identifikation eines Dienstes durch den recherchierenden Nutzer', 'z.B. Katalogdienst, Dienst für geografische Visualisierung, usw. ');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(319, 0, 10024, -1, 'de', 'Konformität', 'Hier muss angegeben werden, zu welcher Durchführungsbestimmung der INSPIRE-Richtlinie bzw. zu welcher anderweitigen Spezifikation die beschriebenen Daten konform sind. (INSPIRE-Pflichtfeld)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(320, 0, 10025, -1, 'de', 'Zugangsbeschränkungen', 'Das Feld „Zugangsbeschränkungen“ beschreibt, die Art der Zugriffsbeschränkung. Bei frei nutzbaren Daten bzw. Services soll der Eintrag „keine“ ausgewählt werden. Sind die Beschränkungen unbekannt, soll der Eintrag „unbekannt" eingetragen werden.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(321, 0, 7075, -1, 'de', 'Zusatzfelder', 'Die Zusatzfelder gelten nur katalogweit und werden vom jeweiligen Katalogadministrator zusätzlich zu den Standardfelder des InGridCatalog hinzugefügt.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(322, 0, 10026, -1, 'de', 'Nutzungsbedingungen', 'In das Feld „Nutzungsbedingungen“ sollen die Bedingungen zur Nutzung des beschriebenen Datensatzes bzw. des Services eingetragen werden. Es sollen z.B. die Kosten für die Nutzung der Daten angegeben werden. Bei frei nutzbaren Daten bzw. Services soll „keine Einschränkungen“ eintragen werden. Sind die Bedingungen unbekannt, ist „unbekannte Nutzungsbedingungen“ einzutragen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(323, 0, 3260, 3, 'de', 'Zugang geschützt', 'Das Kontrollkästchen „Zugang geschützt“ soll aktiviert werden, wenn der Zugang zu dem Dienst z.B. durch ein Passwort geschützt ist. Bei aktiviertem Kontrollkästchen wird kein direkter Link („Zeige Karte“) aus dem Portal zu dem Dienst generiert.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(324, 0, 1010, 6, 'de', 'Beschreibung', 'Fachliche Inhaltsangabe des Dienstes, der Anwendung oder des Informationssystems. Hier sollen in knapper Form die Art des Dienstes, der Anwendung oder des Informationssystems sowie die fachlichen Informationsgehalte beschrieben werden. Auf Verständlichkeit für fachfremde Dritte ist zu achten. DV-technische Einzelheiten sollten auf zentrale, kennzeichnende Aspekte beschränkt werden. Das Feld Beschreibungen muss ausgefüllt werden, damit das Objekt abgespeichert werden kann.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(325, 0, 3620, 6, 'de', 'Art des Dienstes', 'In diesem Feld muss die Art des Dienstes ausgewählt werden. Es stehen folgende Einstellungen zur Verfügung: „Informationssystem", „nicht geographischer Dienst" und „Anwendung". Sollte es sich bei dIhrem Dienst um einen geographischen Dienst handeln, wählen Sie bitte in dem dafür vorgesehenen Feld „Objektklasse“ die Einstellung  „Geodatenklasse“ aus.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(326, 0, 3630, 6, 'de', 'Version', 'Angaben zu Version des beschriebenen Dienstes (Bitte alle Versionen eintragen, die vom Dienst unterstützt werden).', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(327, 0, 3600, 6, 'de', 'Systemumgebung', 'Angaben zum Betriebssystem und der Software, ggf. auch Hardware, die zur Implementierung des Dienstes eingesetzt wird.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(328, 0, 3640, 6, 'de', 'Historie', 'Angabe zur Entwicklungsgeschichte. Hier können Vorläufer und Folgedienste bzw. -anwendungen oder -systeme genannt werden. Ebenso sind Angaben zu initiierenden Forschungsvorhaben oder -programmen von Interesse.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(329, 0, 3645, 6, 'de', 'Basisdaten', 'Herkunft und Art der zugrundeliegenden Daten. Im Allgemeinen sind dies die Datensätze, auf die der Dienst aufgesetzt ist. Allgemein sollen die Herkunft oder die Ausgangsdaten der Daten beschrieben werden, die in dem Dienst / der Anwendung bzw. dem Informationssystem benutzt, gespeichert, angezeigt oder weiterverarbeitet werden. Zusätzlich kann die Art der Daten (z. B. digital, automatisch ermittelt oder aus Umfrageergebnissen, Primärdaten, fehlerbereinigte Daten) angegeben werden. Der Eintrag kann hier direkt über die Auswahl der Registerkarte "Text" erfolgen oder es können Verweise eingetragen werden, indem der Link "Verweis anlegen/bearbeiten" angewählt wird.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(330, 0, 3650, 6, 'de', 'Erläuterungen', 'Zusätzliche Anmerkungen zu dem beschriebenen Dienst, der Anwendung oder dem Informationssystem. Hier können weitergehende Angaben z. B. technischer Art gemacht werden, die zum Verständnis des Dienstes, der Anwendung, des Informationssystems notwendig sind.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(331, 0, 3670, 6, 'de', 'Service-Urls', 'Angaben zu der Zugriffsadresse auf das Informationssystem, den Dienst oder der Anwendung. Es soll er Name und der URL sowie eine kurzen Erläuterung zu der Adresse angegeben werden (Beispiel: Name: PortalU; URL: http://www.portalu.de; Erläuterung: Umweltportal Deutschland).', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(332, 0, 5043, -1, 'de', 'Zeichensatz des Datensatzes', 'Angaben zu dem im beschriebenen Datensatz benutzten Zeichensatz z.B. UTF-8', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(333, 0, 7500, -1, 'de', 'Datenqualität', 'Angaben zur Datenqualität des beschriebenen Datensatzes', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(334, 0, 7509, -1, 'de', 'Datenüberschuss', 'Angaben zu den überschüssigen Features, Attributen oder ihren Relationen; Bsp.: Anzahl der überflüssigen Elemente zur Anzahl der gesamten Elemente: 11,2% (ACHTUNG: es wird nur eine Zahl angegeben; KEIN %-Zeichen)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(335, 0, 7510, -1, 'de', 'Datendefizit', 'Angaben zu den fehlenden Elementen; Bsp.: Anzahl der fehlenden Elemente im Datensatz zu der Anzahl der Elemente, die vorhanden sein sollten: 5,3% (ACHTUNG: es wird nur eine Zahl angegeben; KEIN %-Zeichen)', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(336, 0, 7512, -1, 'de', 'Konzeptionelle Konsistenz', 'Angaben zu Fehlern bezüglich der Verletzung der Regeln des konzeptionellen Schemas; Bsp.: Anzahl der überlappenden Oberflächen innerhalb des Datensatzes: 23', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(337, 0, 7513, -1, 'de', 'Konsistenz des Wertebereichs', 'Angaben zur Übereinstimmung des Wertebereichs; Angegeben wird die Anzahl der Übereinstimmungen im Verhältnis zur Gesamtmenge der Elemente', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(338, 0, 7514, -1, 'de', 'Formatkonsistenz', 'Angaben darüber, wie viele Elemente sich im Konflikt zu der physikalischen Struktur des Datensatzes befinden', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(339, 0, 7515, -1, 'de', 'Topologische Konsistenz', 'Angaben zu topologischen Fehlern, die zwischen verschiedenen Unterelementen des Datensatzes auftreten; Bsp.: Anzahl  fehlender Verbindungen zwischen Unterelementen aufgrund von Undershoots/Overshoots.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(340, 0, 7517, -1, 'de', 'Absolute Positionsgenauigkeit', 'Angabe der Abweichung des Mittelwertes der räumlichen Position der Elemente des Datensatzes zum angenommenen tatsächlichen Wert.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(341, 0, 7520, -1, 'de', 'Zeitliche Genauigkeit', 'Angabe der Anzahl der zeitlich korrekt zugeordneten Elemente zur Gesamtzahl der Elemente', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(342, 0, 7525, -1, 'de', 'Korrektheit der thematischen Klassifizierung', 'Angabe der Anzahl der thematisch falsch klassifizierten Elemente zur Gesamtanzahl der Elemente', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(343, 0, 7526, -1, 'de', 'Genauigkeit nicht-quantitativer Attribute', 'Angabe der Anzahl der inkorrekten nicht-quantitativen Attributwerte im Verhältnis zur Gesamtzahl der Attribute ', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(344, 0, 7527, -1, 'de', 'Genauigkeit quantitativer Attribute', 'Angabe der Anzahl der quantitativen Attribute, die inkorrekt sind; Bsp.: Anzahl aller quantitativen Werte,  die nicht mit 95% Wahrscheinlichkeit dem wahren Wert entsprechen.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(345, 0, 1315, -1, 'de', 'Kodierungsschema der geographischen Daten', 'Kodierung: Beschreibung des Programmiersprachenkonstrukts (zum Beispiel eine Markup-Sprache wie GML), das die Darstellung eines Datenobjekts in einem Datensatz, in einer Datei, einer Nachricht, einem Speichermedium oder einem Übertragungskanal bestimmt.', 'Geographic Markup Language (GML)');

-- -------------------- HIBERNATE_UNIQUE_KEY ------------------------------------

-- 
-- Tabellenstruktur für Tabelle `hibernate_unique_key`
-- 
CREATE TABLE hibernate_unique_key (
  next_hi NUMBER(24,0));

-- 
-- Daten für Tabelle `hibernate_unique_key`
-- 
INSERT INTO hibernate_unique_key (next_hi) VALUES 
(78);

-- -------------------- USER_DATA ------------------------------------

-- 
-- Tabellenstruktur für Tabelle `user_data`
-- 
CREATE TABLE user_data (
  id NUMBER(24,0) NOT NULL,
  version NUMBER(10,0) DEFAULT '0' NOT NULL,
  portal_login VARCHAR2(255 CHAR),
  plug_id VARCHAR2(255 CHAR),
  addr_uuid VARCHAR2(255 CHAR));

-- primary key
ALTER TABLE user_data ADD CONSTRAINT PRIMARY_UserData PRIMARY KEY ( id ) ENABLE;
