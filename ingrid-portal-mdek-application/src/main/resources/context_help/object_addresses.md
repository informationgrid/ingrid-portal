---
# ID des GUI Elements
guid: 1000
# optional ID der Objektklasse
#oid: 3
# title, used as window title
title: Adressen
---

# Adressen

Eintrag von Adressverweisen zu Personen oder Institutionen, die weitergehende Informationen zum beschriebenen Datensatz geben können. Bei Bedarf können diese Verweise geändert werden. In der ersten Spalte wird jeweils die Art des Verweises eingetragen ( Ansprechpartner, Anbieter, etc.). Über den Link "Adresse hinzufügen" wird der Verweis selbst angelegt. Als Auswahlmöglichkeit stehen alle in der Adressverwaltung des aktuellen Kataloges bereits eingetragenen Adressen zur Verfügung. Über das Kontextmenü ist es möglich, Adressen zu kopieren und einzufügen.


Es ist mind. eine Adresse anzugeben. Es sollte mind. ein Ansprechpartner angegeben werden.

Mögliche Einträge laut ISO/INSPIRE (Abweichungen möglich):

## Anbieter

Anbieter der Ressource; ISO Adressrolle: resourceProvider

## Ansprechpartner

Kontakt für Informationen zur Ressource oder deren Bezugsmöglichkeiten; ISO Adressrolle: pointOfContact

## Autor

Verfasser der Ressource; ISO Adressrolle: author

## Bearbeiter

Person oder Stelle, die die Ressource in einem Arbeitsschritt verändert hat; ISO Adressrolle: processor

## Eigentümer

Eigentümer der Ressource; ISO Adressrolle: owner

## Herausgeber

Person oder Stelle, welche die Ressource veröffentlicht; ISO Adressrolle: publisher

## Nutzer

Nutzer der Ressource; ISO Adressrolle: user

## Projektleitung

Person oder Stelle, die verantwortlich für die Erhebung der Daten, Untersuchung ist; ISO Adressrolle: principalInvestigator

## Urheber

Erzeuger der Ressource; ISO Adressrolle: originator

## Vertrieb

Person oder Stelle für den Vertrieb; ISO Adressrolle: distributor, wird für ISO Stuktur distributionContact verwendet

## Verwalter

Person oder Stelle, welche die Zuständigkeit und Verantwortlichkeit für einen Datensatz übernommen hat und seine sachgerechte Pflege und Wartung sichert; ISO Adressrolle: custodian


## Beispiel:

Ansprechpartner / Robbe, Antje, Anbieter / Dr. Seehund, Siegfried

# ISO Abbildung

Kontaktinformation zu Person(en) und Organisation(en), welche im Bezug zur Ressource stehen

Domain: 29


## Abbildung ISO 19139 XML

```XML
<MD_Metadata>
  <identificationInfo>
    <MD_DataIdentification>
      <pointOfContact>
        <CI_ResponsibleParty uuid="UUID OF CONTACT">
          CONTACT INFOS
          <role>
            <CI_RoleCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#CI_RoleCode" codeListValue="ROLE CODE"/>
          </role>
        </CI_ResponsibleParty>
    </MD_DataIdentification>
  </identificationInfo>
</MD_Metadata>
```