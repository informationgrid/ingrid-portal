---
# ID des GUI Elements
guid: 3000
# optional ID der Objektklasse
oid: 0
# title, used as window title
title: Objektname
---

# Objektname

Angabe einer kurzen, prägnanten Bezeichnung der beschriebenen Organisationseinheit oder Fachaufgabe.

Bei gewünschter Mehrsprachigkeit muss dieses Feld nach dem Schema "Deutscher Text#locale-eng:English text" gefüllt werden. Beispiel: Apfelbaum#locale-eng:apple tree

## Beispiel:

Jahresstatistik zum Stand der Altlastenbearbeitung

# ISO Abbildung

Bezeichnung, unter der die Ressource bekannt ist

Domain: 360 (gmd:title)

## Abbildung ISO 19139 XML

```XML
<MD_Metadata>
  <identificationInfo>
    <MD_DataIdentification>
      <citation>
        <CI_Citation>
          <title>
            <gco:CharacterString>TITLE</gco:CharacterString>
          </title>
        </CI_Citation>
      </citation>
    </MD_DataIdentification>
  </identificationInfo>
</MD_Metadata>
```
