
    Beispielvorgehen für Migration Portaldatenbank von InGrid 3.4 nach InGrid 3.5
    =============================================================================

Vorgehen Migration von Portal Datenbank von blunt (Jetspeed 2.1) nach Jetspeed 2.1.4 nach Jetspeed 2.2.2 (gleiches Schema wie 2.3):
    http://portals.apache.org/jetspeed-2/guide-migration.html
    http://portals.apache.org/jetspeed-2/guide-etl-migration.html

- von blunt nach lokal einlesen
    - auf blunt exportieren:
        mysqldump -u root -p --add-drop-table ingrid-portal > ingrid-portal_20141203.sql
    - lokal Datenbank erzeugen:
        mysql -u root -p
        CREATE DATABASE ingrid_portalu DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
        quit
    - lokal einspielen (windows):
        mysql -u root -p ingrid_portalu < ingrid-portal_20141203.sql > mysql_import_ingrid-portal_out.txt

- Portal Datenbank mit InGrid Script auf ingrid_lookup 3.4.1 heben, wenn noch nicht geschehen (update_3.4.1_mysql.sql)
        
- Portal Datenbank mit sql Skripten auf Jetspeed Version 214 migrieren (mysql_migrate_ingrid-portal.sql, oracle_migrate_ingrid-portal.sql)

- Vorbereitung Export der Datenbank via Jetspeed Tools:
  - Aus der Portal Datenbank alle InGrid Permissions löschen !!! Diese können zwar exportiert, aber nicht importiert werden (und werden nicht mehr benötigt) !

        DELETE FROM principal_permission WHERE PERMISSION_ID IN (SELECT PERMISSION_ID FROM security_permission WHERE CLASSNAME LIKE 'de.ingrid.portal.security.permission%');
        DELETE FROM security_permission WHERE CLASSNAME LIKE 'de.ingrid.portal.security.permission%';

  (- Spezialfall für blunt !
     Die blunt ingrid-portal Datenbank ist korrupt ! Der Export (nä. Schritt) wirft Fehler beim Erzeugen der Clients !
         [java] Failed to process XML export of C:\wemove\ingrid\dev\svn\ingrid-portal\branches\ingrid-portal-3.5_REDMINE-304_JetspeedMigration\_migration\etl_21_to_22\mysql_export_214_portalu/j2-data.xml: org.apache.jetspeed.serializer.SerializerException: Creating a serialized representation of Client failed with message null
         [java] org.apache.jetspeed.serializer.SerializerException: Creating a serialized representation of Client failed with message null
         [java] 	at org.apache.jetspeed.serializer.JetspeedSerializerImpl.createJSClient(JetspeedSerializerImpl.java:1524)
         [java] 	at org.apache.jetspeed.serializer.JetspeedSerializerImpl.exportClients(JetspeedSerializerImpl.java:1554)
         [java] 	at org.apache.jetspeed.serializer.JetspeedSerializerImpl.exportCapabilitiesInfrastructure(JetspeedSerializerImpl.java:1635)
         [java] 	at org.apache.jetspeed.serializer.JetspeedSerializerImpl.processExport(JetspeedSerializerImpl.java:1028)
         [java] 	at org.apache.jetspeed.serializer.JetspeedSerializerBase.exportData(JetspeedSerializerBase.java:251)
         [java] 	at org.apache.jetspeed.serializer.JetspeedSerializerApplication.main(JetspeedSerializerApplication.java:440)
     Grund:
        In der Tabelle capability GIBT ES ZWEI EINTRÄGE 'HTML_PLUGIN' (CAPABILITY_ID 21 + 23) !!!
     Fix:
        - Umbenennung des 2. Eintrags in 'HTML_PLUGIN_' (CAPABILITY_ID 23) und Ersetzen CAPABILITY_ID 23 durch 21 in Tabelle client_to_capability ! DANN GEHT EXPORT !

            UPDATE capability SET CAPABILITY = 'HTML_PLUGIN_' WHERE CAPABILITY_ID = 23;
            UPDATE client_to_capability SET CAPABILITY_ID = 21 WHERE CAPABILITY_ID = 23;
  )

- Mit JS 2.1.4 Installer die Datenbank exportieren
  s. http://portals.apache.org/jetspeed-2/guide-etl-migration.html

    java -jar jetspeed-2.1.4-installer.jar
        ...
        - lokalen Treiber wählen: mysql-connector-java-5.1.6.jar

- Neue Portal Datenbank erzeugen (im Bsp. ingrid_portal_new)
    MySQL:
        > mysql -u root -p
        > CREATE DATABASE ingrid_portal_new DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
        > quit
    Oracle:
        Neuen Benutzer "ingrid_portal_new" anlegen

- mit JS 2.2.* Installer bestehende leere Datenbank initialisieren
    java -jar jetspeed-installer-2.2.2.jar

- mit JS 2.2.* Installer die exportierten Daten importieren

    - java -jar jetspeed-installer-2.2.2.jar

- Ingrid Tabellen exportieren und importieren !
    MySQL:
        Export z.B. via phpMyAdmin (ingrid_* + qrtz_* Tabellen)
        Import z.B. via phpMyAdmin

    Oracle:
        - DDL und Daten müssen separat exportiert und importiert werden.
          DDL-Export berücksichtigt auch Trigger, Sequenzen, Indexe.
          Daten-Export beinhaltet auch CLOBS (nur via datapump ! SQL export mit Daten beinhaltet KEINE CLOBS !).
        - DDL export / import via SQL:
            Export via SQL Developer:
                - Extras -> Datenbankexport
                    - Verbindung "ingrid_portal" / "Schema anzeigen" AUS / "Daten exportieren" AUS / "Speichern unter": export_ingrid_ddl.sql
                    - "Objekte angeben": mehr... / Typ: Table / ingrid_* + qrtz_* Tabellen auf rechte Seite
                    - Fertig stellen
            Import via SQL Developer:
                - Datei -> Öffnen -> export_ingrid_ddl.sql
                - "Skript ausführen" -> Verbindung "ingrid_portal_new"
                - commit
        - Daten export / import via datapump:
            Export via SQL Developer:
                - Ansicht -> DBA
                - Data Pump -> Exportjobs Kontext: "Assistent Data Pump-Export"
                    - Verbindung "ingrid_portal" / "Nur Daten"
                    - "Tabellendaten": nur ingrid_* + qrtz_* Tabellen nach unten
                    - Rest beibehalten (export Dateiname etc.)
            Import via SQL Developer:
                - Ansicht -> DBA
                - Data Pump -> Importjobs Kontext: "Assistent Data Pump-Import"
                    - Verbindung "ingrid_portal_new" / "Nur Daten" / gleiche Datei wie beim Export
                    - Filter: ingrid_* + qrtz_* Tabellen aus .dmp File werden angezeigt -> nach rechts klicken !
                    - Erneute Zuordnung: "Schemas neu zuordnen" -> "Zeile hinzufügen" -> Quelle "INGRID_PORTAL", Ziel: "ingrid_portal_new" (Schema mappen, ist im dmp mit drin)
                    - Rest beibehalten (export Dateiname etc.)
