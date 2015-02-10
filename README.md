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



### Debug under eclipse

TDB

Support
-------

If you are having issues, please let us know: info@informationgrid.eu

License
-------

The project is licensed under the EUPL license.
