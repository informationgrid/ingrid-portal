set MAVEN_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n

if %1.==. goto:DEFAULT
mvn jetty:run -Denv=dev -Dprofile=%1
goto:eof

:DEFAULT
mvn jetty:run -Denv=dev