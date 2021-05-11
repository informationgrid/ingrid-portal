---
# ID des GUI Elements
guid: 1020
# title, used as window title
title: Objektklasse
---

# Objektklasse

Zuordnung des Objekts zu einer passenden Informationskategorie. Im Metadateneditor sind die verschiedenen Arten von Informationen in Informationskategorien eingeteilt (sog. Objektklassen). Die Objektklassen unterscheiden sich durch unterschiedliche Beschreibungsfelder in der Registerkarte Fachbezug. Es muss eine der folgenden Objektklasse ausgewählt werden: 
* Geo-Information/Karte: GIS-Daten, analoge Karten oder Kartenwerke 
* Geodatendienst

# ISO Abbildung

Die Objektklasse hat Auswirkung auf die Abbildung auf das Element `gmd:hierarchyLevel` (Bereich, auf den sich die Metadaten beziehen, weitere Informationen zu Hierarchieebenen sind dem ISO 19115 - Anhang H  zu entnehmen. Domain 6) und `gmd:hierarchyLevelName` (Bezeichnung der Hierarchieebene, auf die sich die Metadaten beziehen. Domain 7) abgebildet.

| Objektklasse         | gmd:hierarchyLevel     | gmd:hierarchyLevelName   |
| -------------------- |:----------------------:| :-----------------------:|
| Geodatensatz         | dataset/series         | <empty>/series           |
| Geodatendienst       | service                | service                  |

