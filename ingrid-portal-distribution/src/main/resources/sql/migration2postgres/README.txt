
Migration PORTAL/MDEK Datenbank von MySQL/Oracle nach PostgreSQL
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

2.1. PORTAL Datenbank:
---------------------- 
- im folgenden wird das Migration Toolkit Installationsverzeichnis als MTK_HOME bezeichnet
- Quell- und Zieldatenbank werden eingestellt unter:
    MTK_HOME/etc/toolkit.properties
  Die Zieldatenbank muss im Postgres Server bereits existieren (z.B. "CREATE DATABASE ingrid_portal").

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

2.2. MDEK Datenbank:
--------------------
- s. 2.1., es wird lediglich die MDEK Datenbank in der SRC_DB_URL und TARGET_DB_URL angegeben, also z.B. "mdek" statt "ingrid_portal".
  Bei Oracle wird die Quelldatenbank (Schema) über SRC_DB_USER gesetzt. 



3. Fix PORTAL Quelldatenbank vor Migration:
------------------------------------
- die Portal Datenbank muss vor dem Migrieren bearbeitet werden, um Fehler bei der Migration zu verhindern
- dazu folgendes Skript ausführen, abhängig von Quelldatenbank:
	MySQL:
		1_mysql_portal_prae_migration_fixes.sql
	Oracle:
		1_oracle_portal_prae_migration_fixes.sql
	

4. Ausführen Migration:
-----------------------
Hier unterscheidet sich das Vorgehen bei der PORTAL Datenbank abhängig vom Typ der Quelldatenbank.
Die Oracle Migration der PORTAL Datenbank migriert Schema und Daten korrekt in einem Aufruf.
Die MySQL Migration der PORTAL Datenbank ist aufwendiger, da zuerst das Schema separat migriert werden muss, um die Spaltennamen korrekt abzubilden.

4.1. ORACLE PORTAL Datenbank:
-----------------------------
- nach MTK_HOME/bin wechseln
- Ausführen der batch Datei runMTK.bat

  Beispielhaft für Oracle Migration:

    .\runMTK.bat -sourcedbtype oracle -targetdbtype postgresql -targetSchema public INGRID_PORTAL
    
- das Schema INGRID_PORTAL der Quelldatenbank wird in der Postgres Datenbank ins Schema public migriert, dies ist das default Schema und wird im IGE iPlug per default so erwartet.

4.2. ORACLE MDEK Datenbank:
---------------------------
- s. 4.1., es wird lediglich das zu migrierende MDEK Schema der Quelldatenbank statt dem PORTAL Schema angegeben, also z.B. MDEK statt INGRID_PORTAL


4.3. MYSQL PORTAL Datenbank:
---------------------------- 
Die Default-Migration von MySQL bildet die Spaltennamen exakt mit Groß-/Kleinschreibung nach Postgres ab, setzt also die Spaltennamen in "" (z.B. fragment."FRAGMENT_ID").
Da die Portal SQL Statements generell in Kleinschreibung ausgeführt werden (z.B. fragment.fragment_id) führt dies zu einem Fehler.
Damit die Groß-/Kleinschreibung in Postgres keine Rolle spielt wird mit dem Migrationstool das Schema zunächst offline migriert, die Großschreibung entfernt und dann eingespielt.
Danach werden die Daten migriert.
Dafür wie folgt vorgehen:
  
- nach MTK_HOME/bin wechseln
- Ausführen der batch Datei runMTK.bat zur offline Migration des Schemas. Das Schema wird im Beispiel nach C:\mtkOffline gespielt, das Verzeichnis muss existieren:

    .\runMTK.bat -sourcedbtype mysql -targetdbtype postgresql -targetSchema public -offlineMigration C:\mtkOffline -schemaOnly ingrid_portal
    
- Editieren der Datei mtk_public_ddl.sql unter C:\mtkOffline :

    Alle " mit Leerzeichen ersetzen

- Einspielen der Datei mtk_public_ddl.sql in der Zieldatenbank unter dem Schema public, dies ist das default Schema und wird im IGE iPlug per default so erwartet.

- Ausführen der batch Datei runMTK.bat zum Einspielen der Daten aus der MySQL Datenbank nach Postgres:

    .\runMTK.bat -sourcedbtype mysql -targetdbtype postgresql -targetSchema public -dataOnly ingrid_portal

4.4. MYSQL MDEK Datenbank:
-------------------------- 
- Die MDEK Datenbank kann direkt mit dem Tool migriert werden, es ist kein separates Migrieren von Schema und Daten notwendig, also z.B.

    .\runMTK.bat -sourcedbtype mysql -targetdbtype postgresql -targetSchema public mdek


4.5. Migration weitere Hilfe
----------------------------
- eine Ausgabe aller möglichen Migrations-Parameter ist wie folgt möglich:
    .\runMTK.bat -help
- Weitere Hilfe s. https://www.enterprisedb.com/docs/en/9.5/migrate/EDB_Postgres_Migration_Guide.1.23.html


5. Fix PORTAL Quelldatenbank nach Migration:
----------------------------------
- nach getätigter Migration kann die PORTAL Quelldatenbank wieder in den ursprünglichen Zustand zurückversetzt werden
- dazu folgendes Skript ausführen, abhängig von Quelldatenbank:
	MySQL:
		2_mysql_portal_undo_prae_migration_fixes.sql
	Oracle:
		2_oracle_portal_undo_prae_migration_fixes.sql


6. Fix PORTAL Zieldatenbank nach Migration:
------------------------------------
- auf der migrierten PORTAL Postgres Datenbank müssen noch Änderungen ausgeführt werden, um z.B. fehlende Indexes oder default Werte hinzuzufügen
- dazu folgendes Skript auf der PORTAL Postgres Datenbank ausführen, abhängig von Quelldatenbank:
	MySQL:
		3_mysql2postgres_portal_post_migration_fixes.sql
	Oracle:
		3_oracle2postgres_portal_post_migration_fixes.sql

Alle Skripte wurden auf der Portaldatenbank in der Version 3.6.2 oder 4.0.0 getestet.
Die Skripte können aber auch für andere Portal Versionen angewandt werden, da sich das Schema bzgl. der Skript Inhalte nicht verändert hat.


7. Postgres Datenbankeinstellungen im Portal
--------------------------------------------
Die Einstellungen für die Postgres Datenbank erfolgen im Portal in folgenden Dateien:

- PORTAL/apache-tomcat/conf/Catalina/localhost

	ingrid-portal-apps.xml,
	ROOT.xml:

		url="jdbc:postgresql://localhost:5432/ingrid_portal"
		driverClassName="org.postgresql.Driver"
		username="postgres" password="..."
		validationQuery="SELECT 1"
		
	ingrid-portal-mdek.xml:

		url="jdbc:postgresql://localhost:5432/mdek"
		... (s.o.)

- PORTAL/apache-tomcat/webapps/ingrid-portal-apps/WEB-INF/classes/
- PORTAL/apache-tomcat/webapps/ingrid-portal-mdek/WEB-INF/classes/

	hibernate.cfg.xml:

		<property name="dialect">org.hibernate.dialect.PostgreSQLDialect</property>
		
- PORTAL/apache-tomcat/webapps/ingrid-portal-mdek-application/WEB-INF/classes/

	default-datasource.properties:

		hibernate.driverClass=org.postgresql.Driver
		hibernate.user=postgres
		hibernate.password=...
		hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
		hibernate.jdbcUrl=jdbc:postgresql://localhost:5432/mdek

Die Dateien werden bei einer Neuinstallation des Portals automatisch mit den eingegebenen PostgreSQL Einstellungen versorgt.
Soll ein bestehendes Portal auf Postgres umgeschaltet werden (auf migrierte Datenbanken), so müssen die Dateien manuell angepasst werden.
