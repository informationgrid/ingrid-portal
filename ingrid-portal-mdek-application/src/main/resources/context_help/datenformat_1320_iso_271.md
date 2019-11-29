---
# ID des GUI Elements
guid: 1320
# title, used as window title
title: Datenformat
---

# Datenformat

Angabe des Formats der Daten in DV-technischer Hinsicht, in welchem diese verfügbar sind. Das Format wird durch 4 unterschiedliche Eingaben spezifiziert. Der Name muss ausgefüllt werden.
* Name: Angabe des Formatnamens, z.B. "DXF" 
* Version: Version der verfügbaren Daten (z.B. "2.0" oder "Version vom 8.11.2001")
  *  Verpflichtend bei Format "GML" mit der Version "2.1","3.0","3.1","3.2","3.3" usw. wobei die Angabe nur bis zur 2. Stelle genügen soll
* Kompressionstechnik: Kompression, in welcher die Daten geliefert werden (z.B. "WinZip", "keine")
* Bildpunkttiefe: BitsPerSample.

## Beispiel:

Formatkürzel: tif | Version: 6.0 | Kompression: LZW | Bildpunkttiefe: 8 Bit

Für INSPIRE-konforme Datensätze ist das Datenformat GML und dessen Version anzugeben:

Formatkürzel: GML | Version: (nur 2-stellig), z.B. "3.2"

# ISO Abbildung

Abgabeformat: Beschreibung des Formats, in dem die Daten bereitgestellt werden

Domain: 271 (gmd:distributionFormat)
