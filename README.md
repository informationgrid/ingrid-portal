Portal
====================

This software is part of the InGrid software package. The Portal provides

- a user interface to search the InGrid data
- a map client to visualize geo spatial data
- a ISO 19115/19119/INSPIRE compatible meta data editor



Features
--------

- faceted search on the InGrid data space
- Administration GUI to manage aspects of the portal and the InGrid Installation
- detailed view on the provided data
- map client to display geo spatial data, such as connected Web Map Services or KML Data
- JavaScript based comfortable meta data editor

Requirements
-------------

- a running InGrid Software System

Installation
------------

Download from https://dev.informationgrid.eu/ingrid-distributions/ingrid-portal/
 
or

build from source with `mvn package assembly:single`.

Execute

```
java -jar ingrid-portal-x.x.x-installer.jar
```

and follow the install instructions.

Obtain further information at http://www.ingrid-oss.eu/ (sorry only in German)


Contribute
----------

- Issue Tracker: https://github.com/informationgrid/ingrid-portal/issues
- Source Code: https://github.com/informationgrid/ingrid-portal
 
### Set up eclipse project

```
mvn eclipse:eclipse
```

and import project into eclipse. There are several sub projects:

- ingrid-portal-base: The JSR-286 compatible portal framework based on Jetspeed 2
- ingrid-portal-apps: The portal application that encapsules the InGrid portal
- ingrid-portal-layout: Layout definitions for the InGrid Portal
- ingrid-portal-mdek-application: JavaScript (Dojo Toolkit) based meta data editor (German MDEK: **M**etadaten **E**rfassungs **K**omponente = English IGE: **I**n**G**rid **E**ditor)
- ingrid-portal-mdek: Portal integration of the InGrid Editor
- ingrid-portal-distribution: The assembly project to gather the pieces and build an installer

Alternatively use the maven plugin of you IDE.

### Development

For development on the InGrid-Editor a special environment is needed. 
This is provided by multiple Docker containers.

When starting all the containers within the ingrid-portal directory with:

`docker compose up -d`

then a mysql container with several databases is initialized and started, 
as well as phpmyadmin for administration of the databases.

By running

`mvn jetty:run -Denv=dev`

the webapplication is started and can be accessed by the URL:

`http://localhost:8088/ingrid-portal-mdek-application/start_dev.jsp?user=mdek`

To develop a specific profile, use

```
run.sh [<profile>]
```

from the sub directory `ingrid-portal-mdek-application`. The optional _profile_ parameter adds the sources from the profile directory (ingrid-portal-apps/src/webapp/profiles/<profile>) for the startup. A jetty server is started and the sources can be edited directly. When editing Java classes, the server is automatically restarted. If you change resource files like messages.properties, then the server also needs to be restarted.


For complete function the backend (ingrid-mdek aka ingird-iplug-ige) also has to be started.

For IntelliJ IDEA there's a dojo plugin for easier handling of imports:
https://github.com/TomDevs/needsmoredojo


#### Debug

Execute the following commands or use the `run.sh` script file.

```
set MAVEN_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n
mvn jetty:run -Denv=dev
```


#### Debug under eclipse

When the jetty server is started then the InGrid-Editor can also be debugged via remote debugging. The settings are:
```
Connection Type: Standard (Socket Attach)
Host: 127.0.0.1
Port: 8000
```

Support
-------

If you are having issues, please let us know: info@informationgrid.eu

License
-------

The project is licensed under the EUPL license.
