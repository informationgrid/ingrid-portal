---
# ID des GUI Elements
guid: 1030
# title, used as window title
title: Verantwortlicher
---

# Verantwortlicher

Eintrag des Adressverweises zur Person oder Institution, welche für diesen Metadatensatz verantwortlich und auskunftsfähig ist.

Der Kontakt ist als Verantwortlicher bei Metadatensätzen eingetragen. Dessen E-Mail-Adresse wird verwendet für die Metadatenauskunft. Um die Personenbezogenen Daten zu schützen, kann hier eine alternative E-Mail-Adresse angeben werden (als Funktionsadresse). Für die Angaben Auskunft Metadaten im Portal bzw Metadata Point of Contact über CSW wird dann die E-Mail Metadatenauskunft verwendet. Ist diese Email nicht angegeben, wird die Benutzer-E-Mail-Adresse verwendet. (Siehe auch Nutzerverwaltung des Kataloges)

## Beispiel:

Mustermann, Erika

# ISO Abbildung

Für die Metadaten verantwortliche Stelle.

Domain: 8 (gmd:contact)


## Abbildung ISO 19139 XML

```XML
<MD_Metadata>
  <contact>
    <CI_ResponsibleParty uuid="UUID OF CONTACT">
      <organisationName>
        <gco:CharacterString>NAME/INSTITUTION OF CONTACT</gco:CharacterString>
      </organisationName>
      <contactInfo>
        <CI_Contact>
          <address>
            <CI_Address>
              <electronicMailAddress>
                <gco:CharacterString>EMAIL OF CONTACT</gco:CharacterString>
              </electronicMailAddress>
            </CI_Address>
          </address>
        </CI_Contact>
      </contactInfo>
      <role>
        <CI_RoleCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#CI_RoleCode" codeListValue="pointOfContact"/>
      </role>
    </CI_ResponsibleParty>
  </contact>
</MD_Metadata>
```
