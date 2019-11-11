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
- java 8
- maven 3.5.x (The build does not work with mavane 3.6.x, see https://github.com/fhoeben/hsac-fitnesse-fixtures/issues/238 for a work around.)

Installation
------------

Download from https://distributions.informationgrid.eu/ingrid-portal/

or

build from source with `mvn clean package`.

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

### Setup project

Import project as Maven project. There are several sub projects:

- ingrid-portal-base: The JSR-286 compatible portal framework based on Jetspeed 2
- ingrid-portal-apps: The portal application that encapsules the InGrid portal
- ingrid-portal-layout: Layout definitions for the InGrid Portal
- ingrid-portal-mdek-application: JavaScript (Dojo Toolkit) based meta data editor (German MDEK: **M**eta**D**aten **E**rfassungs **K**omponente = English IGE: **I**n**G**rid **E**ditor)
- ingrid-portal-mdek: Portal integration of the InGrid Editor
- ingrid-portal-distribution: The assembly project to gather the pieces and build an installer

Alternatively use the maven plugin of you IDE.

### Development

For development on the InGrid editor a special **environment** is needed. It consists of the following components:

- Database server
- InGrid backend
- InGrid editor (this repository)

To set up the environment execute the following steps:

##### Database server

The database server is hosted inside a *Docker* container which is set up by executing the following command inside the project's root directory:

```
docker-compose -f docker-compose.ige.yml up -d
```

The following containers will be created:

- *mysql*: MySQL server containing several databases
  - Connection parameters:
    - User: root
    - Password: *empty*
- *phpmyadmin*: Web application for managing the MySQL databases
  - Login:
    - Server: mysql
    - Username/Password as mentioned above

##### InGrid backend

The InGrid backend is provided as a git repository:

```
git clone https://github.com/informationgrid/ingrid-mdek
```

For instructions how to set up and run the application in Eclipse see https://github.com/informationgrid/ingrid-mdek/blob/master/README.md

##### InGrid editor

The InGrid editor is a web application that is started by executing the following command inside the `ingrid-portal-mdek-application` sub directory:

```
run.sh [<profile>]
```

The comand starts a development server (Jetty), that allows for directly editing the application sources. When editing Java classes, the server is automatically restarted. Editing resource files like *messages.properties* requires the server to be restarted.

The optional `profile` parameter adds the sources from the profile directory (`ingrid-portal-apps/src/webapp/profiles/<profile>`) for startup. 

To access the application in the browser open the URL:

http://localhost:8088/ingrid-portal-mdek-application/start_dev.jsp?user=mdek

For IntelliJ IDEA there's a dojo plugin for easier handling of imports:
https://github.com/TomDevs/needsmoredojo

### Troubleshooting

- **Cannot connect to backend**:
  - Error messages: 
    - Browser: *Der Benutzer konnte nicht im Katalog gefunden werden. Bitte überprüfen Sie die mdek-Datenbank und den dazugehörigen Katalog.*
    - Application log: *PROBLEMS CONNECTING TO: /ingrid-group:ige-iplug-test*
  - Solution: 
    - Make sure InGrid backend is running
    - Make sure the user (e.g. *mdek*) exists in the database (schema `mdek`, table `user_data`) and the `plug_id` column value (e.g. */ingrid-group:ige-iplug-test*) matches the property `communications.ige.clientName` set up in the InGrid backend.

### Debugging

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
