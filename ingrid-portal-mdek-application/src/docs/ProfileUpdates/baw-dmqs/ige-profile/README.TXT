igc-iso-mapping.js
==================

JavaScript Code for ISO export of additional fields. Development workflow:

- copy JavaScript to additional field / IDF Mapping
- Restart IGE
- control in ISO preview


importAdditionalFieldBawDmqs.xml
================================

JavaScript Code for ISO Import. Develpment workflow:

- TestCase CSWImportBawDmsq.java in Project ingrid-mdek/ingrid-mdek-job
- based on ISO data in src/test/resources/importAdditionalFieldDocBawDmqs.xml
- uses javascript from src/test/resources/importAdditionalFieldBawDmqs.xml
- After successful dev, copy importAdditionalFieldBawDmqs.xml to this location
- deployment: insert the <scriptedCswMappingImport> Element to any control in the XML profile in DB


ige_profile.sql
===============

Complete IGE profile as stored in database. Can be applied to database to restore the initial profile.


behaviours.user.js
==================

User behavior of the BAW DMQS profile.
