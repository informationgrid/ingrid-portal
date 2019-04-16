---
# ID des GUI Elements
guid: 10008
# title, used as window title
title: Freier Raumbezug 
---

# Freier Raumbezug 

Informationen über die räumliche Zuordnung des in dem Objekt beschriebenen Datenbestand. Es können frei wählbare Raumbezugs-Koordinaten hinzugefügt werden. Der Wertebereich im WGS ist folgendermaßen definiert: <ul><li>Breite (Latitude): -90 bis 90</li><li>Länge (Longitude): -180 bis 180</li></ul>Über den Link "erben" können alle freien Raumbezüge des übergeordneten Objektes übernommen werden. Dabei werden nur neue Raumbezüge übernommen!


# ISO Abbildung

Geografische Ausdehnung: geografische Komponente der Ausdehnung des betreffenden Objekts

Domain: 336 (gmd:geographicElement)

## Abbildung ISO 19139

```
<gmd:geographicElement>
  <gmd:EX_GeographicDescription>
    <gmd:extentTypeCode>
      <gco:Boolean>true</gco:Boolean>
    </gmd:extentTypeCode>
    <gmd:geographicIdentifier>
      <gmd:MD_Identifier>
        <gmd:code>
          <gco:CharacterString>[FREIER RAUMBEZUG]</gco:CharacterString>
        </gmd:code>
      </gmd:MD_Identifier>
    </gmd:geographicIdentifier>
  </gmd:EX_GeographicDescription>
</gmd:geographicElement>
<gmd:geographicElement>
  <gmd:EX_GeographicBoundingBox>
    <gmd:extentTypeCode>
      <gco:Boolean>true</gco:Boolean>
    </gmd:extentTypeCode>
    <gmd:westBoundLongitude>
      <gco:Decimal>[Länge 1]</gco:Decimal>
    </gmd:westBoundLongitude>
    <gmd:eastBoundLongitude>
      <gco:Decimal>[Länge 2]</gco:Decimal>
    </gmd:eastBoundLongitude>
    <gmd:southBoundLatitude>
      <gco:Decimal>[Breite 1]</gco:Decimal>
    </gmd:southBoundLatitude>
    <gmd:northBoundLatitude>
      <gco:Decimal>[Breite 2]</gco:Decimal>
    </gmd:northBoundLatitude>
  </gmd:EX_GeographicBoundingBox>
</gmd:geographicElement>
```