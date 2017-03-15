
Migration Portal/Mdek Datenbank von MySQL/Oracle nach PostgreSQL
================================================================

Die Migration wird mit dem Tool EDB Postgres Migration Toolkit per Kommandozeile ausgeführt.
Dieses Tool kann von einer normalen Postgres Installation via "StackBuilder" nachinstalliert werden und funktioniert auf Windows und Linux.
https://www.enterprisedb.com/products-services-training/products-overview/postgres-plus-solution-pack/migration-toolkit

Ein Manual findet sich unter:
https://www.enterprisedb.com/docs/en/9.5/migrate/toc.html

Im folgenden ist das Vorgehen auf Windows beschrieben, unter Unix ist dies adäquat auszuführen, s. Manual.


Vorgehen Migration auf Windows nach PostgreSQL 9.5.5
----------------------------------------------------

1. Migrationstool installieren:
-------------------------------
- im PostgreSQL StartMenu "Application Stack Builder" starten, um zusätzliche Software für Postgres zu installieren
- nach Auswahl der vorhandenen Postgres Installation erscheinen die zusätzlichen Anwendungen, hier folgendes wählen:
  - "Registration Required and Trial Products" -> "EnterpriseDB Tools" -> "Migration Toolkit ..."
- zum Download muss ein Account angelegt werden
- der Installationsprozess via StackBuilder ist hier beschrieben:
  https://www.enterprisedb.com/docs/en/9.5/migrate/EDB_Postgres_Migration_Guide.1.12.html
- das Migrationstool ist in Java realisiert, für den Zugriff auf die Datenbanken müssen die entsprechenden Datenbanktreiber in Java bekannt sein.
  Dazu aus dem installierten IGE iPlug aus dem lib Verzeichnis die Dateien:
    - postgresql-*.jar
    - ojdbc-*.jar
    - mysql-connector-java-*.jar
  nach JAVA_HOME/jre/lib/ext kopieren.


2. Konfiguration Migrationstool:
--------------------------------
- im folgenden wird das Migration Toolkit Installationsverzeichnis als MTK_HOME bezeichnet
- Quell- und Zieldatenbank werden eingestellt unter:
    MTK_HOME/etc/toolkit.properties
  Die Zieldatenbank muss im Postgres Server bereits existieren (z.B. CREATE DATABASE ingrid_portal).

  Eine MySQL Migration hätte beispielhaft folgenden Inhalt:
  
    SRC_DB_URL=jdbc:mysql://localhost:3306/ingrid_portal
    SRC_DB_USER=root
    SRC_DB_PASSWORD=...

    TARGET_DB_URL=jdbc:postgresql://localhost:5432/ingrid_portal
    TARGET_DB_USER=postgres
    TARGET_DB_PASSWORD=...

  Eine Oracle Migration hätte beispielhaft folgenden Inhalt:

    SRC_DB_URL=jdbc:oracle:thin:@192.168.0.237:1521:XE
    SRC_DB_USER=INGRID_PORTAL
    SRC_DB_PASSWORD=...

    TARGET_DB_URL=jdbc:postgresql://localhost:5432/ingrid_portal
    TARGET_DB_USER=postgres
    TARGET_DB_PASSWORD=...

- Weitere Hilfe s. https://www.enterprisedb.com/docs/en/9.5/migrate/EDB_Postgres_Migration_Guide.1.14.html


3. Fix Quelldatenbank vor Migration:
------------------------------------
- die Portal Datenbank muss vor dem Migrieren bearbeitet werden, um Fehler bei der Migration zu verhindern
- dazu folgendes Skript ausführen, abhängig von Quelldatenbank:
	MySQL:
		1_mysql_portal_prae_migration_fixes.sql
	Oracle:
		1_oracle_portal_prae_migration_fixes.sql
	

4. Ausführen Migration:
-----------------------
Hier unterscheidet sich das Vorgehen abhängig von der Quelldatenbank.
Die Oracle Migration migriert Schema und Daten korrekt in einem Aufruf.
Die MySQL Migration ist aufwendiger, da zuerst das Schema separat migriert werden muss, um die Spaltennamen korrekt abzubilden.

4.1. Ausführen Migration ORACLE:
--------------------------------
- nach MTK_HOME/bin wechseln
- Ausführen der batch Datei runMTK.bat

  Beispielhaft für Oracle Migration:

    .\runMTK.bat -sourcedbtype oracle -targetdbtype postgresql -targetSchema public INGRID_PORTAL
    
- das Schema der Quelldatenbank wird in der Postgres Datenbank ins Schema public migriert, dies ist das default Schema und wird im IGE iPlug per default so erwartet.

4.1. Ausführen Migration MYSQL:
-------------------------------
Die Default-Migration von MySQL bildet die Spaltennamen exakt mit Groß-/Kleinschreibung nach Postgres ab, setzt also die Spaltennamen in "" (z.B. fragment."FRAGMENT_ID").
Zum Referenzieren der Spalten (SQL) muss dann wieder die exakte Schreibweise in "" angegeben werden, was sehr nachteilig ist.
Damit die Groß-/Kleinschreibung keine Rolle spielt wird mit dem Migrationstool das Schema zunächst offline migriert, verändert und eingespielt.
Dafür wie folgt vorgehen:
  
- nach MTK_HOME/bin wechseln
- Ausführen der batch Datei runMTK.bat zur offline Migration des Schemas. Das Schema wird im Beispiel nach C:\mtkOffline gespielt:

    .\runMTK.bat -sourcedbtype mysql -targetdbtype postgresql -targetSchema public -offlineMigration C:\mtkOffline -schemaOnly ingrid_portal
    
- Editieren der Datei mtk_public_ddl.sql unter C:\mtkOffline :

    Alle " mit Leerzeichen ersetzen

- Einspielen der Datei mtk_public_ddl.sql in der Zieldatenbank unter dem Schema public, dies ist das default Schema und wird im IGE iPlug per default so erwartet.

- Ausführen der batch Datei runMTK.bat zum Einspielen der Daten aus der MySQL Datenbank nach Postgres:

    .\runMTK.bat -sourcedbtype mysql -targetdbtype postgresql -targetSchema public -dataOnly ingrid_portal

4.2. Migration Hilfe
--------------------
- eine Ausgabe aller möglichen Migrations-Parameter ist wie folgt möglich:
    .\runMTK.bat -help
- Weitere Hilfe s. https://www.enterprisedb.com/docs/en/9.5/migrate/EDB_Postgres_Migration_Guide.1.23.html


5. Fix Quelldatenbank nach Migration:
----------------------------------
- nach getätigter Migration kann die Quelldatenbank wieder in den ursprünglichen Zustand zurückversetzt werden
- dazu folgendes Skript ausführen, abhängig von Quelldatenbank:
	MySQL:
		2_mysql_portal_undo_prae_migration_fixes.sql
	Oracle:
		2_oracle_portal_undo_prae_migration_fixes.sql


6. Fix Zieldatenbank nach Migration:
------------------------------------
- auf der migrierten Postgres Datenbank müssen noch Änderungen ausgeführt werden, um z.B. fehlende Indexes oder default Werte hinzuzufügen
- dazu folgendes Skript auf Postgres ausführen, abhängig von Quelldatenbank:
	MySQL:
		3_mysql2postgres_portal_post_migration_fixes.sql
	Oracle:
		3_oracle2postgres_portal_post_migration_fixes.sql

Alle Skripte wurden auf der Portaldatenbank in der Version 3.6.2 oder 4.0.0 getestet.
Die Skripte können aber auch für andere Portal Versionen angewandt werden, da sich das Schema bzgl. der Skript Inhalte nicht verändert hat.
 