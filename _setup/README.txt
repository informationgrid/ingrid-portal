
Setup Portal mit neuem Jetspeed
===============================

- Jetspeed bauen ins lokale repo
    - auschecken: http://svn.apache.org/repos/asf/portals/jetspeed-2/portal/trunk
    - mvn clean install -U

- portal-utils bauen ins lokale repo
    - auschecken: https://213.144.28.209:444/svn/ingrid/ingrid-portal-utils/branches/ingrid-portal-utils-3.5_REDMINE-304_JetspeedMigration
    - mvn clean install -U

- Portal bauen ins lokale repo (nicht alles bauen !)
    - auschecken: https://213.144.28.209:444/svn/ingrid/ingrid-portal/branches/ingrid-portal-3.5_REDMINE-304_JetspeedMigration
    - bauen:
        - ingrid-portal-layout
        - ingrid-portal-base
        - ingrid-portal-apps

- Portal anpassen:
    - derby Datenbank rein unter top portal directory als "_derby" (_derby.zip)
    - portal-apps:
        - auf derby umstellen in Datei /src/java/hibernate.cfg.xml
            <!--    <property name="dialect">org.hibernate.dialect.MySQLDialect</property> -->
            <property name="dialect">org.hibernate.dialect.DerbyDialect</property>
    - portal-base:
        - Jetspeed log config auf console umstellen in target (nach jedem "clean"-build drüber kopieren, ersetzt jetspeed leider mit eigener config)
            ingrid-portal-base\target\ingrid-portal\WEB-INF\classes\log4j.xml
        - PageManager NICHT auf Datenbank, folgendes entfernen (ACHTUNG: wird bei Update wieder geholt ! Noch nicht angepasst)
            - ingrid-portal-base\src\webapp\WEB-INF\assembly
                interceptors.xml
                page-manager.xml

- Tomcat aufsetzen:
    - tomcat kopieren
    - lib/shared wie in zip
    - context files wie in zip und anpassen (ROOT.xml, ingrid-portal-apps.xml)
