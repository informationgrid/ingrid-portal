---
# ID des GUI Elements
guid: 5100
# optional ID der Objektklasse
#oid: 1
# title, used as window title
title: Dateibeschreibung der Vorschaugrafik
---

# Dateibeschreibung der Vorschaugrafik

Textliche Beschreibung des Inhalts der Grafik.


# ISO Abbildung

Grafik, die die Ressource darstellt (möglichst einschließlich Legende)

Domain: 31 (gmd:graphicOverview), 50 (gmd:fileDescription)


## Abbildung ISO 19139 XML

```XML
<MD_Metadata>
  <identificationInfo>
    <MD_DataIdentification>
      <graphicOverview>
        <MD_BrowseGraphic>
          <fileDescription>
            <gco:CharacterString>DESCRIPTION OF IMAGE</gco:CharacterString>
          </fileDescription>
        </MD_BrowseGraphic>
      </graphicOverview>
    </MD_DataIdentification>
  </identificationInfo>
</MD_Metadata>
```