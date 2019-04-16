# Anleitung für mcloud-Profil

Bauen der WAR für mcloud:
<pre>
cd ingrid-portal-mdek-application
mvn clean package -DskipTests -Pprofile-mcloud-build
</pre>

Danach ist die Datei "ingrid-portal-mdek-application.war" im Verzeichnis

Starten der Docker-Container
<pre>
cd mcloud-docker
docker-compose up -d --build
</pre>