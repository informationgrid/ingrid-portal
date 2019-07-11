---
# ID des GUI Elements
guid: 3000
# optional ID der Objektklasse
oid: 5
# title, used as window title
title: Objektname
---

# Objektname

Angabe einer kurzen prägnanten Bezeichnung der beschriebenen Datensammlung / Datenbank. Der Objektname kann z.B. identisch mit dem Namen der Datenbank bzw. der Datensammlung sein, sofern dieser ausreichend kurz und aussagekräftig ist. Soweit ein gängiges Kürzel vorhanden ist, ist dieses Kürzel mit anzugeben. Der Eintrag in dieses Feld ist obligatorisch.

## Beispiel:

Anionenkonzentration in Abwasser

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
