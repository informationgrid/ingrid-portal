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

### Development

For development of the InGrid-Editor, go to the directory ingrid-portal-mdek-application and execute the following command:

```
run.sh [<profile>]
```

The optional _profile_ parameter adds the sources from the profile directory (ingrid-portal-apps/src/webapp/profiles/<profile>) for the startup. A jetty server is started and the sources can be edited directly. When editing Java classes, the server is automatically restarted. If you change resource files like messages.properties, then the server also needs to be restarted.

There's also a corresponding windows batch file _start.bat_, which works the same.

For IntelliJ IDEA there's a dojo plugin for easier handling of imports:
https://github.com/TomDevs/needsmoredojo

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
