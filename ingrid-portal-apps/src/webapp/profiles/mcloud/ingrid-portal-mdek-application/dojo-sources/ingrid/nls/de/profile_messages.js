/*-
 * **************************************************-
 * InGrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
define({
    "tree.nodeCut": "Datensätze/Adressen/Teilb&auml;ume ausschneiden",
    "tree.nodeCopySingle": "Datensätze/Adressen kopieren",
    "tree.newNodeName": "Neuer Datensatz",

    "mcloud.title": "mCloud Editor",
    "mcloud.tree.objectNode": "Datensätze",

    "help.mcloud.form.title.title": "Titel",
    "help.mcloud.form.title.text": "Angabe einer kurzen, prägnanten Bezeichnung des beschriebenen Datensatzes. Es sollte auf den ersten Blick erkennbar sein, welche Daten zu erwarten sind. Dies ist insbesondere dann wichtig, wenn zu erwarten ist, dass andere Datenbereitsteller einen ähnlichen Titel wählen könnten.<br /><br />Besser nicht: „Fahrzeugbestand“<br />Sondern: „NRW: Fahrzeugbestand 2018“<br /><br />Der Eintrag in dieses Feld ist obligatorisch.",
    "help.mcloud.form.title.example": "NRW: Fahrzeugbestand 2018",
    "help.mcloud.form.description.title": "Beschreibung",
    "help.mcloud.form.description.text": "Fachliche Inhaltsangabe über den beschriebenen Datensatz. Hier soll in knapper Form beschrieben werden, um welche Art von Information es sich handelt (Geo-Daten, Schnittstelle zu Daten, etc.). Ferner sollten die Hauptinhalte der beschriebenen Daten genannt werden. Auf Verständlichkeit für fachfremde Dritte ist zu achten. Wenn der Inhalt nicht selbsterklärend ist, sollte hier eine ausführlichere Beschreibung des Datensatzes, inkl. Hinweisen, die für die Nachnutzung erforderlich sind (z.B. Erläuterung kryptische Spaltennamen in Tabellen) angebracht werden.<br /><br/>Das Feld „Beschreibungen“ muss ausgefüllt werden, damit der Datensatz veröffentlicht werden kann.",
    "help.mcloud.form.addresses.title": "Adressen",
    "help.mcloud.form.addresses.text": "Hier soll der Datenbereitsteller (Institution, Firma, Behörde, etc.) angegeben werden, der die Metadaten einträgt und über die Veröffentlichung entschieden hat. Über den Link „Adresse hinzufügen“ wird aus im System registrierten Adressen ausgewählt. Die entsprechende Adresse muss zuvor im Verzeichnisbaum unter „Adressen“ &rarr; „Neu anlegen“ angelegt worden sein.<br /><br />Die Neueingabe von Adressen hier in diesem Feld ist nicht möglich.<br /><br />Das Feld „Adressen“ muss ausgefüllt sein, damit der Datensatz veröffentlicht werden kann.<br /><br />Die hier ausgewählte Adresse / Institution erscheint in der mCLOUD unter „Bereitgestellt durch“",
    "help.mcloud.form.crsSection.title": "Raumbezugssystem",
    "help.mcloud.form.crsSection.text": "Einordnung des Datenbestandes in eine geographische Region.",
    "help.mcloud.form.crs.title": "Raumbezugssystem",
    "help.mcloud.form.crs.text": "Für Geo-Daten: Angabe des Raumbezugssystems, in dem die Daten vorliegen (muss nicht mit den für die räumliche Suche in der mCLOUD erfassten Eckkoordinaten sein).",
    "help.mcloud.form.crs.example": "EPSG:4326 / WGS 84 / geographisch",
    "help.mcloud.form.geothesaurus.title": "Geothesaurus-Raumbezug",
    "help.mcloud.form.geothesaurus.text": "Über den Link „Geothesaurus-Navigator“ kann anhand eines Suchbegriffs eine geographische Region innerhalb Deutschlands ausgewählt werden, für die der Datensatz gilt. Die Eckkoordinaten (nordwestliche und südöstliche Ecke der Region) werden automatisch anhand der gewählten Region ermittelt.<br /><br />Der Geothesaurus ist nur für das Hoheitsgebiet Deutschlands gültig. Sie werden im Bezugssystem WGS84 ermittelt. Für Daten außerhalb Deutschlands oder für Gebiete, die nicht über den Geothesaurus gefunden werden, müssen die Koordinaten weiter unten unter „Freier Raumbezug“ erfasst werden.<br /><br />Die Einträge in dieses Feld sind optional.",
    "help.mcloud.form.freeBBox.title": "Freier Raumbezug",
    "help.mcloud.form.freeBBox.text": "Manuelle Eingabe von Eckkoordinaten (NO- und SW-Ecke des Bezugsgebietes) im Bezugssystem WGS84.",
    "help.mcloud.form.freeCoordsTransformed.title": "Umgerechnete Koordinaten",
    "help.mcloud.form.freeCoordsTransformed.text": "Umrechnung der unter „Freier Raumbezug“ ausgewählten Daten in die in der Auswahlbox zur Verfügung stehenden Koordinatensysteme",
    "help.mcloud.form.timeSection.title": "Zeitbezug",
    "help.mcloud.form.timeSection.text": "Angabe des Zeitraums oder des Zeitpunktes, für den die referenzierten Daten Gültigkeit haben.<br /><br />In der mCLOUD erscheint die 'abgedeckte Zeitspanne' in der Detailansicht als „Aktualität der Daten“.<br /><br />Die Einträge in die folgenden Felder sind optional.",
    "help.mcloud.form.resourceDate.title": "Zeitbezug der Ressource",
    "help.mcloud.form.resourceDate.text": "Angabe, wann die eigentlichen Daten erstellt und/oder zuletzt revidiert wurden. Die Erfassung dieser Information ist z.B. für eine spätere zeitliche Suche relevant.",
    "help.mcloud.form.resourceDate.example": "11.11.2017 Erstellung",
    "help.mcloud.form.timeCoverage.title": "Durch die Ressource abgedeckte Zeitspanne",
    "help.mcloud.form.timeCoverage.text": "Hier soll die Zeitspanne der Entstehung der eigentlichen Daten (z.B. Messdaten) eingetragen werden. Es stehen umfassende Möglichkeiten zur exakten Eingabe der abgedeckten Zeitspanne zur Verfügung, damit später einmal eine exakte zeitliche Suche möglich ist.<br /><br />In der mCLOUD erscheint dieser Eintrag in der Detailansicht als „Aktualität der Daten“.",
    "help.mcloud.form.periodicity.title": "Periodizität",
    "help.mcloud.form.periodicity.text": "Angabe des Zeitzyklus der Datenerhebung. Der Eintrag muss aus der Auswahlliste erfolgen, die über den Pfeil am Ende des Feldes geöffnet wird.",
    "help.mcloud.form.periodicity.example": "täglich",

    "mcloud.form.termsOfUse": "Nutzungshinweise",
    "mcloud.form.termsOfUse.helpMessage": "Wenn es in Bezug auf die Nachnutzung der Daten spezielle Hinweise gibt, sind diese hier einzutragen. Die Einträge erscheinen in der mCLOUD unterhalb der Links zu den Daten.<br /><br />Hier kann beispielsweise angegeben werden, wenn der Datensatz für die Nutzung in bestimmten Kontexten nicht geeignet ist oder aufgrund möglicherweise enthaltener Fehler keine Gewähr übernommen wird.<br /><br />Der Eintrag in dieses Feld ist optional.",
    "mcloud.form.dataHoldingPlace": "Datenhaltende Stelle",
    "mcloud.form.category": "Kategorie",
    "mcloud.form.category.helpMessage": "Die Daten in der mCLOUD sind den folgenden Kategorien zuzuordnen:<br /><ul><li>Bahn</li><li>Wasserstraßen und Gewässer</li><li>Infrastruktur</li><li>Klima und Wetter</li><li>Luft- und Raumfahrt</li><li>Straßen</li></ul>Eine Mehrfachauswahl ist möglich.<br /><br />Der Eintrag in dieses Feld ist obligatorisch.",
    "mcloud.form.downloads": "Downloads",
    "mcloud.form.downloads.helpMessage": "In diese Felder werden die Links auf die eigentlichen Daten eingetragen. Für jeden Link sind vier Felder auszufüllen:<br /><ul><li>Titel: Ein kurzer für den verlinkten Datensatz spezifischer Titel.</li><li>Beispiel: Reisezentrenliste der DB Vertrieb GmbH (Stand: 19.07.2018)</li><li>Link: Der Link auf die Datenquelle.</li><li>Typ: Angabe, um welche Art des Datenzugangs es sich handelt (z.B. Download, FTP, WMS, AtomFeed, etc.).</li><li>Datenformat: Dateiformat, in dem die Daten vorliegen. Hier ist auch eine freie Eingabe möglich.</li></ul>Die Einträge in dieses Feld sind obligatorisch (Link, Typ).",
    "mcloud.form.license": "Lizenz",
    "mcloud.form.license.helpMessage": "Um Nutzern Klarheit darüber zu verschaffen, was sie mit den Daten tun dürfen, ist die Verwendung einer Lizenz erforderlich.  Die mit den Daten zu verknüpfende Lizenz ist über ein Auswahlfeld auszuwählen, wobei auch eine freie Eingabe möglich ist. Häufig im Kontext von Open Data verwendete Lizenzen sind:<br /><ul><li>Datenlizenz Deutschland – Namensnennung – Version 2.0: verpflichtet den Datennutzer, den jeweiligen Datenbereitsteller zu nennen.</li><li>Datenlizenz Deutschland – Zero – Version 2.0: ermöglicht einschränkungslose Weiterverwendung.</li><li>Creative Commons – Namensnennung – Version 4.0: verpflichtet den Datennutzer, den jeweiligen Datenbereitsteller zu nennen.</li><li>Creative Commons – Namensnennung-Share Alike – Version 4.0: verpflichtet den Datennutzer, den jeweiligen Datenbereitsteller zu nennen und bei einer Weiterveröffentlichung die gleichen Bedingungen einzuhalten</li><li>Creative Commons – Public Domain Dedication – Version 1.0: Daten sind in die Gemeinfreiheit entlassen.</li></ul>Weitere empfohlene Open-Data-Lizenzen (siehe http://opendefinition.org/licenses/).<br /><br />Wenn eine der vorgenannten offenen Lizenzen nicht infrage kommt, nennen Sie hier die Nutzungsbedingungen, unter denen eine Weiterverwendung der Daten möglich ist.<br /><br />Die Datenlizenz Deutschland wird in aller Regel von behördlichen Datenbereitstellern genutzt, während die Creative Commons Lizenzen im privatwirtschaftlichen Umfeld verbreitet sind.",
    "mcloud.form.sourceNote": "Quellenvermerk",
    "mcloud.form.sourceNote.helpMessage": "Im Quellenvermerk wird angegeben, wie der Urheber bei einer Nachnutzung und Wiederveröffentlichung der Daten zu nennen ist. An dieser Stelle kann auch vermerkt werden, ob und in welcher Form den Daten ein Veränderungshinweis mitgegeben werden muss. Dies kann auch das Verlangen nach Löschung des Quellenvermerks nach Bearbeitung der Ursprungsdaten beinhalten.<br /><br />Beispiele:<br />Quelle: Deutscher Wetterdienst, 2016<br />Quelle: Gewässereinzugsgebiete Deutschland, www.wsv.de<br /><br />Veränderungshinweise können z.B. lauten:<br />Datenbasis: Deutscher Wetterdienst, eigene Elemente ergänzt.<br /><br />Die Einträge in dieses Feld sind optional.<br /><br />Der Eintrag erscheint in der mCLOUD als 'Quellenvermerk:' unter 'Nutzungshinweise'.",
    "mcloud.form.fileType": "Datentyp",
    "mcloud.form.availability": "Verfügbarkeit",
    "mcloud.form.fileFormat": "Datenformat",
    "mcloud.form.relevance": "Aktualität / Zeitraum",
    "mcloud.form.dateUpdated": "Aktualisierungsdatum",
    "mcloud.form.realTimeData": "Echtzeitdaten",
    "mcloud.form.mFundProject": "mFUND Projekt",
    "mcloud.form.mFundProject.helpMessage": "Dieses Feld ist nur von Datenbereitstellern auszufüllen, die aus der Förderinitiative mFUND des BMVI gefördert werden.<br /><br />Hier wird der Name des mFUND Projekts eingetragen.<br /><br />In der mCLOUD erscheint die mFUND Information unterhalb der Beschreibung.",
    "mcloud.form.mFundFKZ": "mFUND Förderkennzeichen",
    "mcloud.form.mFundFKZ.helpMessage": "Dieses Feld ist nur von Datenbereitstellern auszufüllen, die aus der Förderinitiative mFUND des BMVI gefördert werden.<br /><br />Das Förderkennzeichen wird nach diesem Muster eingetragen:<br />19F1030 oder<br />19F1030A<br /><br />In der mCLOUD erscheint die mFUND Information unterhalb der Beschreibung.",

    "mcloud.form.general": "Allgemein",
    "mcloud.form.general.tool,tip": "...",

    "mcloud.form.mcloudFields": "mCLOUD-Felder",
    "mcloud.form.mcloudFields.toolTip": "In den nachfolgenden Feldern werden Metadaten erfasst, die in der Detailbeschreibung der Datensätze in der mCLOUD angezeigt werden.<br /><br />Außerdem werden 'opendata'-Metadaten erfasst, die z.B. bei der Abgabe nach GovData obligatorisch sind.",

    "mcloud.form.dcatcategory": "DCAT Kategorie (GovData)",
    "mcloud.form.dcatcategory.helpMessage": "Hier können mehrere 'opendata'-Kategorien eingetragen werden. Bei der Abgabe nach GovData sind diese Kategorien bestimmend, werden in der mCLOUD allerdings nicht angezeigt.<br /><br />Es muss mindestens eine Kategorie eingetragen werden."
});

