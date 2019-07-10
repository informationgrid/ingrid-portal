---
# ID des GUI Elements
guid: 3221
# optional ID der Objektklasse
oid: 3
# title, used as window title
title: Kopplungstyp
---

# Kopplungstyp

Die Art der Kopplung vom Service zu den Daten. Der Typ 'tight' bewirkt, dass ein Verweis zu einem Datensatz existieren muss.

# ISO Abbildung

Das Feld wird in ISO 19119 beschrieben. Es existiert keine deutsche Übersetzung. 

## Abbildung ISO 19139 XML

```XML
<MD_Metadata>
  <identificationInfo>
    <srv:SV_ServiceIdentification>
      <srv:couplingType>
        <srv:SV_CouplingTyp codeListValue="[KOPPLUNGSTYP]"/>
      </srv:couplingType>
    </srv:SV_ServiceIdentification>
  </identificationInfo>
</MD_Metadata>  
```
