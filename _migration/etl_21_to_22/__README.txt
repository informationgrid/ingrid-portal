
Vorgehen Migration von 2.1.4 nach 2.2.2:

- von blunt nach lokal einlesen
    - auf blunt exportieren:
        mysqldump -u root -p --add-drop-table ingrid-portal > ingrid-portal_20141203.sql
    - lokal Datenbank erzeugen:
        mysql -u root -p
        CREATE DATABASE ingrid_portalu DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
        quit
    - lokal einspielen (windows):
        mysql -u root -p ingrid_portalu < ingrid-portal_20141203.sql > mysql_import_ingrid-portal_out.txt

- Portal Datenbank mit sql Skripten auf Version 214 migrieren (../mysql_migrate_ingrid-portal.sql, ../oracle_migrate_ingrid-portal.sql)

- ACHTUNG !!!
    Die blunt ingrid-portal Datenbank ist korrupt ! Der Export (nä. Schritt) wirft Fehler beim Erzeugen der Clients !
    Irgend etwas stimmt mit der 'client' Tabelle und allen assoziierten Tabellen nicht.
    DESHALB DIESE TABELLEN MIT DEN LOKALEN 'ingrid-portal' TABELLEN ERSETZT:
        - folgende Tabellen lokal exportiert in sql Datei inkl. DROP TABLE (phpMyAdmin):
            capability, client, client_to_capability, client_to_mimetype, mimetype
          nach Datei C:\wemove\ingrid\dev\svn\ingrid-portal\branches\ingrid-portal-3.5_REDMINE-304_JetspeedMigration\_migration\etl_21_to_22\_fromBlunt\ingrid-portal_fixBluntWithLocalData.sql
        - diese Datei dann importiert nach ingrid_portalu
    DANN GEHT's !

- Mit JS 2.1.4 Installer die Datenbank exportieren
  s. http://portals.apache.org/jetspeed-2/guide-etl-migration.html

    java -jar jetspeed-2.1.4-installer.jar
    
        ...
        - lokalen Treiber wäheln:
            C:\wemove\ingrid\dev\svn\ingrid-portal\branches\ingrid-portal-3.5_REDMINE-304_JetspeedMigration\_migration\etl_21_to_22\mysql-connector-java-5.1.6.jar

    Bsp. lokale MySql Datenbank für export:
      <Resource name="jdbc/jetspeed" auth="Container"
              factory="org.apache.commons.dbcp.BasicDataSourceFactory"
              type="javax.sql.DataSource" username="root" password=""
              driverClassName="com.mysql.jdbc.Driver" url="jdbc:mysql://localhost/ingrid-portal?autoReconnect=true"
              maxActive="100" maxIdle="30" maxWait="10000" validationQuery="SELECT 1"
              testOnBorrow="true" testOnReturn="false" testWhileIdle="true" timeBetweenEvictionRunsMillis="1800000"
              removeAbandoned="true" removeAbandonedTimeout="60" logAbandoned="true"/>

- MySQL Datenbank js2_import_22x erzeugen
            > mysql -u root -p
            > CREATE DATABASE js2_import_222 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
            > quit

- mit JS 2.2.* Installer bestehende leere Datenbank initialisieren
    java -jar jetspeed-installer-2.2.2.jar

- mit JS 2.2.* Installer die exportierten Daten importieren
    - erst in j2-data.xml alle Ingrid "Permission" entfernen, da bei denen "type=unknown" ist (nach 'type="unknown"'suchen)!
      (in Datenbank "security_permission.CLASSNAME" = de.ingrid.portal.security.permission.IngridPortalPermission ! Das ist nicht bekannt !).

    - java -jar jetspeed-installer-2.2.2.jar

- Ingrid Tabellen exportieren und importieren !
    Export via phpMyAdmin (ingrid_* + qrtz_*)
        s. ingrid-portal_export.sql
    Import via phpMyAdmin