
Vorgehen Migration von 2.1.4 nach 2.2.2:

- s. http://portals.apache.org/jetspeed-2/guide-etl-migration.html

- vorneweg: Portal Datenbank mit sql Skripten auf Version 214 migrieren (mysql_migrate_ingrid-portal.sql, oracle_migrate_ingrid-portal.sql)

- Mit JS 2.1.4 Installer die Datenbank exportieren
  <Resource name="jdbc/jetspeed" auth="Container"
          factory="org.apache.commons.dbcp.BasicDataSourceFactory"
          type="javax.sql.DataSource" username="root" password=""
          driverClassName="com.mysql.jdbc.Driver" url="jdbc:mysql://localhost/ingrid-portal?autoReconnect=true"
          maxActive="100" maxIdle="30" maxWait="10000" validationQuery="SELECT 1"
          testOnBorrow="true" testOnReturn="false" testWhileIdle="true" timeBetweenEvictionRunsMillis="1800000"
          removeAbandoned="true" removeAbandonedTimeout="60" logAbandoned="true"/>

- MySQL Datenbank js2_import_22x erzeugen
            > mysql -u root -p
            > CREATE DATABASE js2_import_221 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
            > quit

- mit JS 2.2.* Installer bestehende leere Datenbank initialisieren
        jdbc:mysql://localhost/js2_import_221
        jdbc:mysql://localhost/js2_import_222

- mit JS 2.2.* Installer die exportierten Daten importieren
    - erst in j2-data.xml alle Ingrid "Permission" entfernen, da bei denen "type=unknown" (in Datenbank "security_permission.CLASSNAME" = de.ingrid.portal.security.permission.IngridPortalPermission ! Das ist nicht bekannt !).
    -
        jdbc:mysql://localhost/js2_import_221
        jdbc:mysql://localhost/js2_import_222

- Ingrid Tabellen exportieren und importieren !
    Export via phpMyAdmin (ingrid_* + qrtz_*)
        s. ingrid-portal_export.sql
    Import via phpMyAdmin