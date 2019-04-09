---
# ID des GUI Elements
guid: 3000
# optional ID der Objektklasse
oid: 3
# title, used as window title
title: Objektname
---

# Objektname

Angabe einer kurzen prägnanten Bezeichnung des beschriebenen Geodatendienstes. Der Objektname kann z.B. identisch mit dem Namen des Geodatendienstes sein, sofern dieser ausreichend kurz und aussagekräftig ist. Soweit ein gängiges Kürzel vorhanden ist, ist dieses Kürzel mit anzugeben. Der Eintrag in dieses Feld ist obligatorisch.

## Beispiel:

Emissions-Fernüberwachung EFÜ

# ISO Abbildung

Bezeichnung, unter der die Ressource bekannt ist

Domain: 360

## Abbildung ISO 19139 XML

```XML
<MD_Metadata>
  <identificationInfo>
    <srv:SV_ServiceIdentification>
      <citation>
        <CI_Citation>
          <title>
            <gco:CharacterString>TITLE</gco:CharacterString>
          </title>
        </CI_Citation>
      </citation>
    </srv:SV_ServiceIdentification>
  </identificationInfo>
</MD_Metadata>
```
