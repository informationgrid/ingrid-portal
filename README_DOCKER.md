# Usage

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

For complete function the backend (ingrid-mdek aka ingird-iplug-ige) also has to be started.

# Debug

Execute the following commands or use the `run.bat` script file.

```
set MAVEN_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n
mvn jetty:run -Denv=dev
```

In Eclipse start a new remote debug session via socket attach to port 8000 on 127.0.0.1.

