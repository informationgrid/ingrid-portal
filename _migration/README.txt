
Ermittlung SQL:
===============

-------------------------
SQL erzeugen via jetspeed, also Datenbank anlegen

- JS 2.1
    - auf maven 1 umstellen ..., s. C:\wemove\ingrid\dev\help\README.txt
    - Manual:
        - http://portals.apache.org/jetspeed-2.1.3/devguide/j2-maven-plugin.html
        - http://portals.apache.org/jetspeed-2.1.3/devguide/m1-getting-started-source.html
    - build.properties anpassen (mysql, oracle)
    - bauen, erzeugt DDL in target (nach C:\wemove\ingrid\dev\jetspeed\JETSPEED-RELEASE-2.1\_ddl_from_target verschoben)
        maven j2:portal.conf.sql

- JS 2.1.3, 2.1.4 wie JS 2.1
    
- JS TRUNK
    - minimale production MySQL Datenbank
        - jetspeed-mvn-settings.xml editieren und entsprechende Datenbank angeben
        - Datenbank erzeugen
            > mysql -u root -p
            > CREATE DATABASE js2_trunk DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
            > quit
        - Minimale Datenbank erzeugen
            mvn jetspeed:mvn -Dtarget=min-db
    - Oracle entsprechend, nur den user vorher in Oracle DB erzeugen !
    - DDL aus target verschoben nach C:\wemove\ingrid\dev\jetspeed\JETSPEED_trunk\_ddl_from_target


-------------------------
2.1 zu 2.1.2 (keine jars in Repos mehr, deshalb via derby sql!)

- von jeweiliger Jetspeed Version das entsprechende /etc Verzeichnis ausgecheckt
    z.B. C:\wemove\ingrid\dev\jetspeed\JETSPEED-RELEASE-2.2.2\svn\tags\JETSPEED-RELEASE-2.1.1_etc
- Unterschiede Schema / Inhalte:
    Vergleich /etc/sql + /etc/schema der Jetspeed Versionen
        -> SQL Unterschiede in \sql\derby\schema
        -> Inhalt Unterschiede in \sql\min\j2-seed.xml
- Unterschiede SQL Syntax Derby / MySQL:
    Vergleich zwischen
        C:\wemove\ingrid\dev\jetspeed\JETSPEED-RELEASE-2.1\svn\tags\JETSPEED-RELEASE-2.1\target\portal-sql\mysql\schema\phase1-schema.sql
    und
        C:\wemove\ingrid\dev\jetspeed\JETSPEED-RELEASE-2.1\svn\tags\JETSPEED-RELEASE-2.1\etc\sql\derby\schema\phase1-schema.sql
