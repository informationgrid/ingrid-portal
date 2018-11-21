
MetaVer 4.2.1 Oracle nach MySQL Migration mit SQLLINES
======================================================

Vorbereitung
------------
Download und Installation Tools s. auch https://redmine.wemove.com/projects/ingrid/wiki/HH_LGV_Testumgebung_Oracle#Migration-nach-MySQL

Migration geschieht mit "SQLines Data" Tool s. http://www.sqlines.com/sqldata/oracle-to-mysql

Für Oracle und MySQL Connections folgendes installieren:
- Oracle Instant Client: http://www.sqlines.com/sqldata_oracle_connection
  Installiert und "oci.dll" in "...\sqlinesdata31773_x64_win\sqldata.cfg" hinzugefügt
- MySQL Connector C: http://www.sqlines.com/sqldata_mysql_connection
  "MySQL Connector C 6.1" installiert und "...MySQL Connector C 6.1\lib" in PATH hinzugefügt


Migration MDEK
--------------

- MySQL Datenbank anglegen:
    CREATE DATABASE IF NOT EXISTS mdek_metaver DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

- Schema einspielen und an Oracle Spaltenreihenfolge anpassen:
    421_mdek_mysql_schema.sql
    421_mdek_mysql_schema_fix_tables.sql

- in ORACLE Datenbank Tabelle umbenennen, damit Transfer funktioniert (keine Kleinschreibung !):
    alter table "MDEK_METAVER"."schema_version" rename to SCHEMA_VERSION;

- dann via SQLLINES Daten einspielen:
    - Source Oracle
        - 192.168.0.237:1521/xe
        - User Name: MDEK_METAVER
    - Source MySQL
        - localhost 3306
        - Database: mdek_metaver
    - Settings
        - Table List: MDEK_METAVER.*
          "Load the Default Schema" angeklickt lassen
        - Transfer Options: Truncate
        - Performance Options: 1 (damit log chronologisch abläuft)
    - click auf "Transfer"
    - in Tab "Raw Log" detaillierte Ausgaben, auch in log Dateien im sqllines Verzeichnis
    - click auf "Validate" überprüft Anzahl Zeilen in Tabellen

- Achtung: REPO_USER Tabelle ist nicht vorhanden und ist auch leer, muss nicht migriert werden (nur nötig, wenn Standalone Editor)


Migration PORTAL
----------------

- MySQL Datenbank anlegen:
    CREATE DATABASE IF NOT EXISTS portal_metaver DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

- Schema einspielen und an Oracle Spaltenreihenfolge anpassen:
    421_portal_mysql_schema.sql
    421_portal_mysql_schema_fix_tables.sql

- in ORACLE Datenbank Tabelle umbenennen, damit Transfer funktioniert (keine Kleinschreibung !):
    alter table "PORTAL_METAVER"."schema_version" rename to SCHEMA_VERSION;

- dann via SQLLINES Daten einspielen:
    - Source Oracle
        - User Name: PORTAL_METAVER
    - Source MySQL
        - Database: portal_metaver
    - Settings
        - Table List: PORTAL_METAVER.*
        ... s.o.
