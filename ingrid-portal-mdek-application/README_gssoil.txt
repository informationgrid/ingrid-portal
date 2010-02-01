This project has a "gssoil" profile in the pom !!!
Call the "gssoil" profile to create artifact suited for GS Soil !

mvn -Pgssoil ...

Then the libraries and the resources needed for the GS Soil implementation of the Thesaurus,
Gazetteer and FullClassify API are downloaded and included in the webapp.
To activate GSSoil implementation of API you have to inject it via Spring in the file 
WEB-INF\external-services.xml (instead of SNS implementation).
