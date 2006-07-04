  Neue jetspeed Version
  =====================

- maven jetspeed plugin (libs) holen:
  (- Neues jetspeed plugin installieren:
    maven -DartifactId=maven-jetspeed2-plugin -DgroupId=org.apache.portals.jetspeed-2 -Dversion=2.1-dev plugin:download
    (ausgeführt im aktualisierten svn HEAD trunk mit build.properties: org.apache.jetspeed.project.home = C:/wemove/portalu/dev/jetspeed-2_svn))
  - BESSER, jetspeed von source builden, dann hat man auf alle Faelle die aktuellsten Sourcen und auch das aktuellste jetspeed maven plugin !
    Mit dem plugin aus dem repository GINGS NICHT !
    http://portals.apache.org/jetspeed-2/getting-started-source.html
  
- alles clearen -> holt auch schon akt. Jetspeed libs, via jetspeed plugin:
  - maven ingrid-portal:clean (in top directory ingrid-portal)
  - TOMCAT WORK VERZEICHNISSE LOESCHEN !!!
- ingrid-base:
  - minapp in base generieren:
    maven j2:portal.genapp.minimal
  - test compile etc.:
    maven war:install
  - fuer eclipse .classpath aktualisieren (VORHER alten sichern ?):
    - maven eclipse:generate-classpath
    - ouput path anpassen !
  - LOKALE JETSPEED ÄNDERUNGEN MIT AKT. FILES VERGLEICHEN UND ANPASSEN:
    - assemblys (.xml)
    - Überschriebene Klassen und Subklassen
  - Datenbank aktualisieren:
    - SQL erzeugen:   maven ingrid:portal.conf.sql
    - SQL einspielen: maven j2:db.create.production
- ingrid-apps:
  - Jetspeed version in project.properties umstellen:
    jetspeed.version=2.1-dev
  - test compile etc.:
    maven war:install
  - fuer eclipse .classpath aktualisieren (VORHER alten sichern ?):
    - maven eclipse:generate-classpath
    - ouput path anpassen !
- ingrid-layout:
  - Jetspeed version in project.properties umstellen:
    jetspeed.version=2.1-dev
  - test compile etc.:
    maven war:install
  - fuer eclipse .classpath aktualisieren (VORHER alten sichern ?):
    - maven eclipse:generate-classpath
    - ouput path anpassen !
- tomcat:
  - neue libs nach shared\lib übernehmen (alte loeschen)


  Setup Entwicklungsumgebung für Portalframework
  ==============================================
am Bsp. einer mit j2:portal.genapp erzeugten neuen Portal-Umgebung mit der artifact.id "portalu"

Anm.:
  - gute Beschreibung maven -> http://www-128.ibm.com/developerworks/java/library/j-maven/
  - j2 Maven plugin goals -> http://portals.apache.org/jetspeed-2/j2-maven-plugin.html

Directories:
  - unter ./src gibts java und test directory:
    - java: hier kommen z.B. Sourcen von Jetspeed rein, die gefixt werden muessen
    - test: hier kommen die Test-Klassen rein umd die geänderet Funktionalität zu testen

maven:
------
  - maven setup in project-info.xml und project.xml (source-, test-, ressourcen-Verzeichnis ...), NUR DIESE DÜRFEN GEÄNDERT WERDEN !!!
    DER REST (core-build.xml, full-portal.xml, jetspeed-components.xml) WIRD BEI JESTPEED UPDATE ÜBERSPIELT (enthalten Jetspeed dependencies) !
    -> s. goal j2:portal.conf.project auf j2 Maven plugin Seite (s.o.)
  - war erstellen "maven war:install"
  - kompiliert nach target/classes
  - erstellt komplette webapp unter target/portalu
  - WICHTIG: die unter .\target\portalu\WEB-INF\classes abgelegten .properties files (OJB) werden von Eclipse gelöscht, wenn Eclipse da rein kompiliert, AM BESTEN DIESE nach .\src\java kopieren, dann kopiert die eclipse mit rein !
  
eclipse:
--------
  - Top Verzeichnis als Java Projekt einbinden
  - Src, Build Path etc. korrekt aufsetzen, die benoetigten libs werden direkt im maven Repository referenziert (über Umgebungsvariable MAVEN_REPO)
    -> welche libs für "hinzugefügte sourcen aus J2" gebraucht werden kann man wie folgt ermitteln:
        - entweder aus den Dependencies in den maven project files im entsprechenden Subprojekt der Jetspeed-2.0 distribution
        - ODER schoen über die komplette in Eclipse kompilierte Jetspeed-2.0 Distribution (Klasse anklicken -> show Declaration)
  - den output Path auf ".\target\portalu\WEB-INF\classes" setzen (direkt in die webapp rein kompilieren)
    -> ACHTUNG: eclipse bereinigt per Default den output Ordner, die dort von maven erzeugten ressourcen (OJB props etc.) am Besten vorab nach ./src/java kopieren, dann übernimmt die eclipse (s.o.)

Tomcat:
-------
  - eclipse sysdeo Tomcat Plugin:
    - in eclipse preferences: Context declaration mode "Context files" -> erzeugt eigene Context files in TOMCAT_HOME\conf\Catalina\localhost
    - in project -> properties:
      - webapp Verzeichnis auf "/target/portalu"
      - URI z.B. auf "/portalu_dev" setzen
        -> in TOMCAT_HOME\conf\Catalina\localhost wird Context file: portalu_dev.xml erzeugt
        - portalu_dev.xml ANPASSEN ! Da muss die DB Verbindung rein (<Realm> <Resource>) s. von maven generiertes Context file (nach deployment per maven)
  - Tomcat starten
    ->  ACHTUNG:
        - DIE PORTLET WEBAPPS MUESSEN EINMAL ÜBER DAS ENTWICKLUNGSVERZEICHNIS DEPLOYED WERDEN (\target\portalu\WEB-INF\deploy)
          -> DIES FÜHRT NICHT NUR ZUR VERÄNDERUNG DER PORTLET WEBAPP .war (s.u.) SONDERN AUCH DES PORTALS (\WEB-INF\apps\jetspeed-layouts KOMMT HINZU)
        - alle benötigten Portlet Webapps sollten beim Starten des "Entwicklungs Portals" im webapp Verzeichnis sein (es sei denn, man benutzt die Entwicklungsversion einer Portlet-App, s.u.), da die von den default PSML Files referenziert werden
       (werden mit "maven j2:quickStart" erzeugt, die .war files kann man sich ja dann sichern)
  - http://localhost:8080/portalu_dev zeigt das Portal AUS DER ENTWICKLUNGSUMGEBUNG !
  
ACHTUNG:
  - Es sollte immer nur ein Portal im Tomcat drin sein, mehrere führen zu Problemen (Pluto Container), d.h. wenn man das Portal über maven deployed, also wirklich das war file nach Tomcat kopiert und da ein neues Context file anlegt, sollte das "Entwicklungs Kontext File" entfernt werden (z.B. umbenennen nach .bak). Genau so auch umgekehrt (Context File umbenennen, .war und entsprechendes Verzeichnis im Tomcat löschen; "maven j2:remove.wars" entfernt auch die portlet webapps, die werden aber gebraucht ...)


  Setup Entwicklungsumgebung für Portlet Applikation
  ==================================================
am Bsp. portlet app "demo" aus J2.0 Distribution in separatem Entwicklungsverzeichnis

maven:
------
  - in project-info.xml, project.xml und project.properties Projekt beschreiben (koennte man auch zu project.xml zusammenfassen)
  - war erstellen "maven war:install"
  - kompiliert nach target/classes
  - erstellt komplette webapp unter target/demo
    -> ACHTUNG: Diese webapp muss noch verändert werden, bevor Sie als Portlet Applikation verwendet werden kann, s.u.

eclipse:
--------
  - s.o. Portalframework
  - den output Path auf ".\target\demo\WEB-INF\classes" setzen (direkt in die webapp rein kompilieren)

Tomcat:
-------
  - s.o. Portalframework
  - in project -> properties:
    - webapp Verzeichnis auf "/target/demo"
    - URI AUF "/demo" setzen
      -> in TOMCAT_HOME\conf\Catalina\localhost wird Context file: demo.xml erzeugt, muss nicht mehr geändert werden (kein DB Zugriff !?)

ACHTUNG !!!!!!!
  - die erzeugte webapp muss einmal über das Portal (portalu) deployed werden
    -> dabei wird die web.xml und portlet.xml verändert, ausserdem wird die taglib portlet.tld ins WEB-INF\tld Verzeichnis gespielt
    - obige veränderet Dateien aus dem .war holen und in die webapp Umgebung im Entwicklungsverzeichnis kopieren
    - AM BESTEN die Dateien aufheben, damit man die im Entwicklungsverzeichnis immer wieder drüber spielen kann (nach nem maven build)
    - die korrekt deployte webapp "demo" muss aus Tomcat wieder entfernt werden (.war und Verzeichnis unter /webapps)
