#!/bin/bash
PROFILES_DIR="webapps/ingrid-portal-apps/profiles"
HIERARCHY_DIR="webapps/ingrid-portal-mdek-application/dojo-sources/ingrid/hierarchy"

cd /opt/ingrid/ingrid-portal/apache-tomcat

if [[ ! $JAVA_OPTS == *"DB_PASSWORD"* ]]; then
    echo "Database password not set or empty. Setting it empty to make sure that it's really set."
    export JAVA_OPTS="$JAVA_OPTS -DDB_PASSWORD="""
fi

if [ "$PORTAL_PROFILE" ]; then
    echo "Using specific portal profile: $PORTAL_PROFILE"

    if [ ! -d "$PROFILES_DIR/$PORTAL_PROFILE" ]; then
        echo >&2 "PROFILE DIRECTORY NOT FOUND: '$PROFILES_DIR/$PORTAL_PROFILE'"
        exit 1
    fi

    echo "Copying profile files into portal directories ..."
    cp -R $PROFILES_DIR/$PORTAL_PROFILE/ingrid-portal/* webapps/ROOT
    cp -R $PROFILES_DIR/$PORTAL_PROFILE/ingrid-portal-apps/* webapps/ingrid-portal-apps
    cp -R $PROFILES_DIR/$PORTAL_PROFILE/ingrid-portal-mdek-application/* webapps/ingrid-portal-mdek-application
    cp -R $PROFILES_DIR/$PORTAL_PROFILE/ingrid-webmap-client/* webapps/ingrid-webmap-client

    echo "Copy repeatable SQL file for profile ..."
    cp $PROFILES_DIR/$PORTAL_PROFILE/profile_mysql.sql webapps/ROOT/WEB-INF/classes/db/migration/mysql/afterMigrate.sql
    cp $PROFILES_DIR/$PORTAL_PROFILE/profile_postgres.sql webapps/ROOT/WEB-INF/classes/db/migration/postgres/afterMigrate.sql
    cp $PROFILES_DIR/$PORTAL_PROFILE/profile_oracle.sql webapps/ROOT/WEB-INF/classes/db/migration/oracle/afterMigrate.sql

    # specific options for UVP
    if [ "$PORTAL_PROFILE" == "uvp" ]; then
        # deactivate behaviours
        find $HIERARCHY_DIR -not -path 'webapps/ingrid-portal-mdek-application/dojo-sources/ingrid/hierarchy/behaviours/uvp/*' -type f -name '*.js' -not -name 'folder*' -exec sed -i 's/defaultActive \?: \?true/defaultActive: false/' {} \;
        find $HIERARCHY_DIR -not -path 'webapps/ingrid-portal-mdek-application/dojo-sources/ingrid/hierarchy/behaviours/uvp/*' -type f -name '*.js' -not -name 'folder*' -exec sed -i 's/defaultActive \?: \?!0/defaultActive:0/' {} \;

        # increase session timeout to 120 minutes
        sed -i 's/<session-timeout>30<\/session-timeout>/<session-timeout>120<\/session-timeout>/' conf/web.xml
    fi

else
    echo "No specific portal profile used."
fi

if [ "$DEBUG" = 'true' ]; then
    ./bin/catalina.sh jpda run
else
    ./bin/catalina.sh run
fi
