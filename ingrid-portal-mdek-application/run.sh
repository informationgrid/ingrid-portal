export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

if [ -z $1 ]; then
    mvn jetty:run -Denv=dev --add-opens java.base/java.lang=ALL-UNNAMED
else
    mvn jetty:run -Denv=dev --add-opens java.base/java.lang=ALL-UNNAMED -Dprofile=$1
fi