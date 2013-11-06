
Vorgehen Upgrade InGrid LGV HH 3.3.1
====================================

Nachfolgend sind die einzelnen Komponenten aufgeführt und nötige Schritte zum Upgrade.
Prinzipiell besitzt jeder Installer eine UPDATE Funktionalität, bei der bestehende Komponenten auf die neue Version aktualisiert werden (Konfigurationen bleiben erhalten und werden evtl. erweitert).
Sollte dies nicht zum Ziel führen, kann die Komponente auch NEU installiert werden, dann müssen die Konfigurationen manuell angepasst werden.
Vor jeder Installation gilt:
- BACKUP DER ALTEN KOMPONENTE + BACKUP DER ZUGEHÖRIGEN DATENBANKEN !

Alle Installer finden sich unter:
http://213.144.28.209/ingrid-distributions/


ingrid-codelist-repository
--------------------------
- STOPPEN DER ALTEN KOMPONENTE
- BACKUP DER ALTEN KOMPONENTE
- Installer: http://213.144.28.209/ingrid-distributions/ingrid-codelist-repository/3.3.1/
- Installation via Update Funktionalität
Anmerkung:
- Beim Update des Codelist Repositories versucht der Installer neue Syslisten ins Repo aufzunehmen, d.h. die bestehende Datendatei
    {INSTALLATIONS_VERZEICHNIS}/ingrid-codelist-repository/data/codelists.xml
  wird erweitert (via patch).
  Sollten hier Probleme auftreten, so erscheint in der Ausgabe ein PATCH-ERROR. Im Verzeichnis {INSTALLATIONS_VERZEICHNIS}/ingrid-codelist-repository/data liegt dann die Ausgangs-Datendatei als codelists.xml.orig, die veränderte Datendatei als codelists.xml und die nicht ausgeführten Veränderungen als codelists.xml.rej
  Tritt ein PATCH-ERROR auf, so entfernt man am Besten die Datendatei codelists.xml und startet das Codelist-Repo neu. Dann werden alle default Codelisten eingelesen, eigene Änderungen müssen dann nachträglich eingepflegt werden.
  In 3.3.1 hinzugekommene Codelisten sind z.B. "6400 - Kategorien (Open Data)", "6500 - Lizenzen" und "8000 - Objektklassen", diese müssen im Codelist Repo vorliegen.


ingrid-iplug-ige
----------------
- STOPPEN DER ALTEN KOMPONENTE
- BACKUP DER ALTEN KOMPONENTE
- BACKUP DER DATENBANK (IGC Katalog)
- Installer: http://213.144.28.209/ingrid-distributions/ingrid-iplug-ige/3.3.1/
- Installation via Update Funktionalität
Anmerkung:
Beim Update wird auch der referenzierte Katalog auf den neuesten Stand gebracht. In der Ausgabe befindet sich
  updateIGCDb:
       [echo] Erstellung/Aktualisierung IGC Katalog auf Version 3.3.1.
       ...
Unter anderem werden doppelte Orig IDs gelöscht, neue Open Data Felder angelegt und Open Data Datensätze (Schlagwort "#opendata_hh#") migriert.
Details finden sich in der Datei "importer_..._log.log".


ingrid-iplug-dsc-scripted
-------------------------
- STOPPEN DER ALTEN KOMPONENTE
- BACKUP DER ALTEN KOMPONENTE
- Installer: http://213.144.28.209/ingrid-distributions/ingrid-iplug-dsc-scripted/3.3.1/
- Installation via Update Funktionalität
- Das normale Verhalten nach der Installation ist, dass Open Data Datensätze (neue Checkbox "Open Data" aktiv) im Index mit dem Schlagwort "opendata" indexiert werden (im dsc-scripted für Objekte).
Weitere Anpassungen HH:
Um den Index auch mit dem Schlagwort "#opendata_hh#" zu erweitern, auf dem dann auch die Facette "Opendata" funktioniert, muss folgendes ausgeführt werden (im dsc-scripted für Objekte):
    - Neues Mapping File, das "#opendata_hh#" Schlagwort in den Index schreibt. Hierzu die mitgelieferte Datei
        ingrid-iplug-dsc-scripted/igc_to_lucene_lgv_hh.js
      nach
        {INSTALLATIONS_VERZEICHNIS}/ingrid-iplug-dsc-scripted/conf/mapping
      kopieren
    - Um das Mapping File zu aktivieren, die mitgelieferte Datei
        ingrid-iplug-dsc-scripted/spring.xml
      nach
        {INSTALLATIONS_VERZEICHNIS}/ingrid-iplug-dsc-scripted/webapp/WEB-INF
      kopieren (alte spring.xml vorher sichern).


ingrid-portal
-------------
- Portal Profil wieder auf 'PortalU' zurück stellen !
- STOPPEN DER ALTEN KOMPONENTE
- BACKUP DER ALTEN KOMPONENTE
- BACKUP DER DATENBANK (ingrid-portal + mdek)
- Installer: http://213.144.28.209/ingrid-distributions/ingrid-portal/3.3.1/
- Installation via Update Funktionalität
- Profil auf 'LGV Hamburg' umstellen
Weitere Anpassungen HH:
- Im InGrid Editor müssen unter "Gesamtkatalogmanagement -> Zusätzliche Felder" die Scripte eingespielt werden, die das gewünschte dynamische Verhalten / Mapping umsetzen.
Siehe hierzu die mitgelieferte Datei
    ige-profile/lgv_hamburg.js
Gemäß dieser Datei alle Scripte / Zusätzlichen Felder ergänzen und danach den Editor NEU LADEN.


Weitere optionale 3.3.1 Komponenten
-----------------------------------
Weiterhin wurden folgende Komponenten als 3.3.1 released, diese sind aber nicht zwingend für die gewünschten Änderungen in HH nötig.

http://213.144.28.209/ingrid-distributions/ingrid-interface-csw/3.3.1/
http://213.144.28.209/ingrid-distributions/ingrid-interface-csw/3.3.1/site/changes-report.html

http://213.144.28.209/ingrid-distributions/ingrid-iplug-csw-dsc/3.3.1/
http://213.144.28.209/ingrid-distributions/ingrid-iplug-csw-dsc/3.3.1/site/changes-report.html

http://213.144.28.209/ingrid-distributions/ingrid-iplug-dsc-mapclient/3.3.1/
http://213.144.28.209/ingrid-distributions/ingrid-iplug-dsc-mapclient/3.3.1/site/changes-report.html

http://213.144.28.209/ingrid-distributions/ingrid-iplug-excel/3.3.1/
http://213.144.28.209/ingrid-distributions/ingrid-iplug-excel/3.3.1/site/changes-report.html

http://213.144.28.209/ingrid-distributions/ingrid-iplug-xml/3.3.1/
http://213.144.28.209/ingrid-distributions/ingrid-iplug-xml/3.3.1/site/changes-report.html
