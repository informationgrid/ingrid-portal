
Migration 3.2.0 von MySQL nach Oracle
=====================================

Basis-Migration via SQL Developer
---------------------------------
Download SQL Developer z.B. hier:
http://www.oracle.com/technetwork/developer-tools/sql-developer/downloads/index.html?ssSourceSiteId=ocomen

Migration Dokumentation s. 
http://docs.oracle.com/cd/E18464_01/doc.30/e17472.pdf
s. Chapter 2

Basis vor Migration:
- (Initiale XE Verbindung angelegt mit User "System")
- Neuen Benutzer MIGRATIONS/MIGRATIONS angelegt (XE -> Andere Benutzer)
- Neue Verbindung Migration_Repository@XE angelegt mit Benutzer MIGRATIONS
- MySQL Verbindung angelegt (Treiber z.B.: mysql-connector-java-5.1.21)

Migration:

ACHTUNG:
Der Name der MySQL Datenbank sollte KEIN "-" enthalten, da dies zu Problemen führt.
Also vorher "ingrid-portal" z.B. umbenennen nach "ingrid_portal" !!!

- Rechtsklick auf Datenbank in MySql Verbindung -> "Zu Oracle migrieren ..."
- In Wizard:
	- Zieldatenbank: Als Verbindung auch Repository Verbindung wählen (mit "Zielobjekte löschen")
	- Daten verschieben: zunächst Daten "Offline" migrieren (mit "Daten leeren")
- Nach Migration NEUE VERBINDUNG anlegen zu migrierter Datenbank: username / password ist der Datenbankname -> ACHTUNG: User ist case insensitive / passwd ist lowercase !
	- bei Migration Datenbank ingrid_portal ist USER / PASSWD z.B. INGRID_PORTAL / ingrid_portal
- Dann via Migrationsprojekt Daten zu neuer Verbindung migrieren
	- Rechtsklick auf "Konvertierte Datenbankobjekte" (in Ansicht "Migrationsprojekte") und "Daten verschieben..."
	- Modus: Online, Quelle: "MySQL", Ziel: neu angelegte Verbindung, "Daten leeren"


Danach Fixes ausführen (z.B. fix Datentypen spezieller Columns ...)
-------------------------------------------------------------------
- Skripts ausführen:
	- Portal:	migration_portal3.2.0_fixes.sql
	- mdek:		(kein Fix nötig)

ACHTUNG:
Im Portal werden alle Quartz Jobs gelöscht, da diese aus den persistenten MySQL Daten auf Oracle nicht erzeugt werden können (Fehler: EOF beim Lesen BLOB).
Die Default Jobs werden dann beim Starten des Portals wieder erzeugt.


Danach in Anwendungen Datenbank Verbindung umstellen
----------------------------------------------------
- Portal, s. README_oracle.txt in apache-tomcat-...\conf\Catalina\localhost
