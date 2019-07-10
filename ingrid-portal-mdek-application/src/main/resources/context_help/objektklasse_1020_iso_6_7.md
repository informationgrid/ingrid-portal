---
# ID des GUI Elements
guid: 1020
# title, used as window title
title: Objektklasse
---

# Objektklasse

Zuordnung des Objekts zu einer passenden Informationskategorie. Im Metadateneditor sind die verschiedenen Arten von Informationen in Informationskategorien eingeteilt (sog. Objektklassen). Die Objektklassen unterscheiden sich durch unterschiedliche Beschreibungsfelder in der Registerkarte Fachbezug. Es muss eine der folgenden Objektklasse ausgewählt werden: - Datensammlung/Datenbank: analoge oder digitale Sammlung von Daten. Beispiele: Messdaten, statistische Erhebungen, Modelldaten, Daten zu Anlagen - Dienst/Anwendung/Informationssystem: zentrale Auskunftssysteme, welche in der Regel auf eine oder mehrere Datenbanken zugreifen und diese zugänglich machen. - Dokument/Bericht/Literatur: Broschüren, Bücher, Aufsätze, Gutachten, etc. Von Interesse sind insbesondere Dokumente, welche nicht über den Buchhandel oder über Bibliotheken erhältlich sind ("graue Literatur") - Geo-Information/Karte: GIS-Daten, analoge Karten oder Kartenwerke - Organisationseinheit/Fachaufgabe: Zuständigkeiten von Institutionen oder Abteilungen, Fachgebiete, Fachaufgaben. - Vorhaben/Projekt/Programm: Forschungs- und Entwicklungsvorhaben, Projekte unter Beteiligung anderer Institutionen oder privater Unternehmen, Schutzprogramme; von besonderem Interesse sind Vorhaben/Projekte/Programme, in denen umweltrelevante Datenbestände entstehen.

# ISO Abbildung

Die Objektklasse hat Auswirkung auf die Abbildung auf das Element `gmd:hierarchyLevel` (Bereich, auf den sich die Metadaten beziehen, weitere Informationen zu Hierarchieebenen sind dem ISO 19115 - Anhang H  zu entnehmen. Domain 6) und `gmd:hierarchyLevelName` (Bezeichnung der Hierarchieebene, auf die sich die Metadaten beziehen. Domain 7) abgebildet.

| Objektklasse         | gmd:hierarchyLevel     | gmd:hierarchyLevelName   |
| -------------------- |:----------------------:| :-----------------------:|
| Fachaufgabe          | nonGeographicDataset   | job                      |
| Geodatensatz         | dataset/series         | <empty>/series           |
| Geodatendienst       | service                | service                  |
| Literatur            | nonGeographicDataset   | document                 |
| Projekt              | nonGeographicDataset   | project                  |
| Datensammlung        | nonGeographicDataset   | database                 |
| Informationssystem   | application            | application              |

