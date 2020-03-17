#!/bin/bash
###
# **************************************************-
# InGrid Portal Distribution
# ==================================================
# Copyright (C) 2014 - 2019 wemove digital solutions GmbH
# ==================================================
# Licensed under the EUPL, Version 1.1 or – as soon they will be
# approved by the European Commission - subsequent versions of the
# EUPL (the "Licence");
# 
# You may not use this work except in compliance with the Licence.
# You may obtain a copy of the Licence at:
# 
# http://ec.europa.eu/idabc/eupl5
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the Licence is distributed on an "AS IS" basis,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the Licence for the specific language governing permissions and
# limitations under the Licence.
# **************************************************#
###
PROFILES_DIR="webapps/ingrid-portal-apps/profiles"
HIERARCHY_DIR="webapps/ingrid-portal-mdek-application/dojo-sources/ingrid/hierarchy"

cd /opt/ingrid/ingrid-portal/apache-tomcat

if [ -e /initialized ]
then
    echo "Container already initialized"
else
    # Perform some substitutions if environment variables are set
    if [ ! -z "${DB_DRIVERCLASS}" ]; then
        # common substution scripts for sed
        substitution_catalina_db_user="s@username=\"root\"@username=\"${DB_USER}\"@"
        substitution_catalina_db_pass='s@password=""@password="${DB_PASSWORD}"@' # No variable substitution in shell/sed. Database password will be set in the JAVA_OPTS environment variable.
        substitution_catalina_db_driver="s@driverClassName=\"com.mysql.jdbc.Driver\"@driverClassName=\"${DB_DRIVERCLASS}\"@"
        substitution_catalina_db_url_portal="s@url=\"jdbc:mysql://localhost/ingrid-portal[^\"]*\"@url=\"${DB_URL_PORTAL}\"@"
        substitution_catalina_db_url_mdek="s@url=\"jdbc:mysql://localhost/mdek[^\"]*\"@url=\"${DB_URL_MDEK}\"@"

        # substitutions in Tomcat context files
        sed -i \
            -e $substitution_catalina_db_user \
            -e $substitution_catalina_db_pass \
            -e $substitution_catalina_db_driver \
            -e $substitution_catalina_db_url_portal \
            \
            conf/Catalina/localhost/ingrid-portal-apps.xml

        sed -i \
            -e $substitution_catalina_db_user \
            -e $substitution_catalina_db_pass \
            -e $substitution_catalina_db_driver \
            -e $substitution_catalina_db_url_mdek \
            \
            conf/Catalina/localhost/ingrid-portal-mdek.xml

        sed -i \
            -e $substitution_catalina_db_user \
            -e $substitution_catalina_db_pass \
            -e $substitution_catalina_db_driver \
            -e $substitution_catalina_db_url_portal \
            \
            conf/Catalina/localhost/ROOT.xml

        # Substitute the dialect in hibernate xml files
        sed -i \
            -e "s@org.hibernate.dialect.MySQLDialect@${DB_DIALECT}@" \
            -e "s@org.hibernate.dialect.MySQLInnoDBDialect@${DB_DIALECT}@" \
            \
            webapps/ingrid-portal-apps/WEB-INF/classes/hibernate.cfg.xml \
            webapps/ingrid-portal-mdek/WEB-INF/classes/hibernate.cfg.xml

        # Substitute values in datasource properties file
        sed -i \
            -e "s@hibernate.driverClass=com.mysql.jdbc.Driver@hibernate.driverClass=${DB_DRIVERCLASS}@" \
            -e "s@hibernate.user=root@hibernate.user=${DB_USER}@" \
            -e "s/hibernate.password=$/hibernate.password=${DB_PASSWORD}/" \
            -e "s@hibernate.dialect=org.hibernate.dialect.MySQLInnoDBDialect@hibernate.dialect=${DB_DIALECT}@" \
            -e "s@hibernate.jdbcUrl=jdbc:mysql://localhost/mdek@hibernate.jdbcUrl=${DB_URL_MDEK}@" \
            \
            webapps/ingrid-portal-mdek-application/WEB-INF/classes/default-datasource.properties

        # Replace the quartz driver delegate class
        sed -i \
            -e "s@org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate@org.quartz.jobStore.driverDelegateClass = ${QUARTZ_DRIVERCLASS}@" \
            \
            webapps/ingrid-portal-apps/WEB-INF/classes/quartz.properties
    fi

    sed -i 's/communications.ibus=\/ingrid-group\\:ibus-live,127.0.0.1,9900/communications.ibus=\/ingrid-group:ibus,'${IBUS_IP}',9900/' webapps/ingrid-portal-apps/WEB-INF/classes/ingrid-portal-apps.properties
    sed -i 's/portal.enable.caching=true/portal.enable.caching='${PORTAL_CACHE_ENABLE}'/' webapps/ingrid-portal-apps/WEB-INF/classes/ingrid-portal-apps.properties
    sed -i 's/csw.interface.url=https:\/\/dev.informationgrid.eu\/csw/csw.interface.url='${PORTAL_CSW_URL}'/' webapps/ingrid-portal-apps/WEB-INF/classes/ingrid-portal-apps.properties

    # handle portal profiles
    if [ "$PORTAL_PROFILE" ]; then
        echo "Using specific portal profile: $PORTAL_PROFILE"

        if [ ! -d "$PROFILES_DIR/$PORTAL_PROFILE" ]; then
            echo >&2 "PROFILE DIRECTORY NOT FOUND: '$PROFILES_DIR/$PORTAL_PROFILE'"
            exit 1
        fi

        # NUMIS extends UVP layout
        if [ "$PORTAL_PROFILE" == "numis" ]; then
            echo "Copying profile files from parent (uvp) into portal directories ..."
            cp -R $PROFILES_DIR/uvp/ingrid-portal/* webapps/ROOT
            cp -R $PROFILES_DIR/uvp/ingrid-portal-apps/* webapps/ingrid-portal-apps
            cp -R $PROFILES_DIR/uvp/ingrid-portal-mdek/* webapps/ingrid-portal-mdek
            if [ "$NI_SWITCH_PORTAL" ]; then
                sed -i 's/uvp.niedersachsen.de/'${NI_SWITCH_PORTAL}'/' $PROFILES_DIR/$PORTAL_PROFILE/ingrid-portal/decorations/layout/ingrid/footer.vm
                sed -i 's/uvp.niedersachsen.de/'${NI_SWITCH_PORTAL}'/' $PROFILES_DIR/$PORTAL_PROFILE/ingrid-portal/decorations/layout/ingrid-untitled/footer.vm
            fi
        fi

        # UVP-NI extends UVP
        if [ "$PORTAL_PROFILE" == "uvp-ni" ]; then
            echo "Copying profile files from parent (uvp) into portal directories ..."
            cp -R $PROFILES_DIR/uvp/ingrid-portal/* webapps/ROOT
            cp -R $PROFILES_DIR/uvp/ingrid-portal-apps/* webapps/ingrid-portal-apps
            cp -R $PROFILES_DIR/uvp/ingrid-portal-mdek/* webapps/ingrid-portal-mdek
            cp -R $PROFILES_DIR/uvp/ingrid-portal-mdek-application/* webapps/ingrid-portal-mdek-application
            cp -R $PROFILES_DIR/uvp/ingrid-webmap-client/* webapps/ingrid-webmap-client
            if [ "$NI_SWITCH_PORTAL" ]; then
                sed -i 's/numis.niedersachsen.de/'${NI_SWITCH_PORTAL}'/' $PROFILES_DIR/$PORTAL_PROFILE/ingrid-portal/decorations/layout/ingrid/footer.vm
                sed -i 's/numis.niedersachsen.de/'${NI_SWITCH_PORTAL}'/' $PROFILES_DIR/$PORTAL_PROFILE/ingrid-portal/decorations/layout/ingrid-untitled/footer.vm
            fi
        fi

        echo "Copying profile files into portal directories ..."
        cp -R $PROFILES_DIR/$PORTAL_PROFILE/ingrid-portal/* webapps/ROOT
        cp -R $PROFILES_DIR/$PORTAL_PROFILE/ingrid-portal-apps/* webapps/ingrid-portal-apps
        cp -R $PROFILES_DIR/$PORTAL_PROFILE/ingrid-portal-mdek/* webapps/ingrid-portal-mdek
        cp -R $PROFILES_DIR/$PORTAL_PROFILE/ingrid-portal-mdek-application/* webapps/ingrid-portal-mdek-application
        cp -R $PROFILES_DIR/$PORTAL_PROFILE/ingrid-webmap-client/* webapps/ingrid-webmap-client
        cp -R $PROFILES_DIR/$PORTAL_PROFILE/ingrid-webmap-client/frontend/src/* webapps/ingrid-webmap-client/frontend/prd

        echo "Copy repeatable SQL file for profile ..."
        cp $PROFILES_DIR/$PORTAL_PROFILE/profile_mysql.sql webapps/ROOT/WEB-INF/classes/db/migration/mysql/afterMigrate.sql
        cp $PROFILES_DIR/$PORTAL_PROFILE/profile_postgres.sql webapps/ROOT/WEB-INF/classes/db/migration/postgres/afterMigrate.sql
        cp $PROFILES_DIR/$PORTAL_PROFILE/profile_oracle.sql webapps/ROOT/WEB-INF/classes/db/migration/oracle/afterMigrate.sql

        # specific options for UVP
        if [ "$PORTAL_PROFILE" == "uvp" ]; then
            # deactivate behaviours
            find $HIERARCHY_DIR -not -path 'webapps/ingrid-portal-mdek-application/dojo-sources/ingrid/hierarchy/behaviours/uvp/*' -type f -name '*.js' -not -name 'folder*' -not -name 'addresses.js*' -exec sed -i 's/defaultActive \?: \?true/defaultActive: false/' {} \;
            find $HIERARCHY_DIR -not -path 'webapps/ingrid-portal-mdek-application/dojo-sources/ingrid/hierarchy/behaviours/uvp/*' -type f -name '*.js' -not -name 'folder*' -not -name 'addresses.js*' -exec sed -i 's/defaultActive \?: \?!0/defaultActive:0/' {} \;

            # increase session timeout to 120 minutes
            sed -i 's/<session-timeout>30<\/session-timeout>/<session-timeout>120<\/session-timeout>/' conf/web.xml
        fi

        # specific options for UVP-NI
        if [ "$PORTAL_PROFILE" == "uvp-ni" ]; then
            # deactivate behaviours
            find $HIERARCHY_DIR -not -path 'webapps/ingrid-portal-mdek-application/dojo-sources/ingrid/hierarchy/behaviours/uvp/*' -type f -name '*.js' -not -name 'folder*' -exec sed -i 's/defaultActive \?: \?true/defaultActive: false/' {} \;
            find $HIERARCHY_DIR -not -path 'webapps/ingrid-portal-mdek-application/dojo-sources/ingrid/hierarchy/behaviours/uvp/*' -type f -name '*.js' -not -name 'folder*' -exec sed -i 's/defaultActive \?: \?!0/defaultActive:0/' {} \;

            # increase session timeout to 120 minutes
            sed -i 's/<session-timeout>30<\/session-timeout>/<session-timeout>120<\/session-timeout>/' conf/web.xml
        fi

    else
        echo "No specific portal profile used."
    fi
    
    # IGE standalone configuration
    if [ "$STANDALONE_IGE" == "true" ]; then
        # remove all contexts except ingrid-portal-mdek-application
        rm -rf webapps/ingrid-portal-apps*
        rm -rf webapps/ingrid-webmap-client*
        rm -rf webapps/ingrid-portal-mdek.war
        rm -rf webapps/ingrid-portal-mdek/
        rm -rf webapps/ROOT*

        # remove context files
        rm conf/Catalina/localhost/ingrid-portal-apps.xml conf/Catalina/localhost/ingrid-portal-mdek.xml conf/Catalina/localhost/ingrid-webmap-client.xml conf/Catalina/localhost/ROOT.xml

        # adapt configuration use standalone version of IGE
        sed -i 's/installation.standalone=false/installation.standalone=true/' webapps/ingrid-portal-mdek-application/WEB-INF/classes/mdek.properties
        echo 'admin.password=admin' > webapps/ingrid-portal-mdek-application/WEB-INF/classes/igeAdminUser.properties
    fi

    # SMTP_HOST
    sed -i 's/workflow.mail.smtp=localhost/workflow.mail.smtp=$SMTP_HOST/' webapps/ingrid-portal-mdek-application/WEB-INF/classes/mdek.properties
    sed -i 's/workflow.mail.sender=test@wemove.de/workflow.mail.sender=$MAIL_SENDER/' webapps/ingrid-portal-mdek-application/WEB-INF/classes/mdek.properties
    sed -i 's/workflow.mail.smtp.user=/workflow.mail.smtp.user=$MAIL_USER/' webapps/ingrid-portal-mdek-application/WEB-INF/classes/mdek.properties
    sed -i 's/workflow.mail.smtp.password=/workflow.mail.smtp.password=$MAIL_PASSWORD/' webapps/ingrid-portal-mdek-application/WEB-INF/classes/mdek.properties

    sed -i 's/mdek.directLink=http:\/\/localhost:8080\/ingrid-portal-mdek-application\/index.jsp/mdek.directLink=$IGE_DIRECT_LINK/' webapps/ingrid-portal-mdek-application/WEB-INF/classes/mdek.properties

    # Upload settings
    sed -i 's/upload.docsdir=\/tmp\/ingrid\/upload\/documents\//upload.docsdir='${UPLOAD_DOCS_DIR}'/' webapps/ingrid-portal-mdek-application/WEB-INF/classes/mdek.properties
    sed -i 's/upload.partsdir=\/tmp\/ingrid\/upload\/parts\//upload.partsdir='${UPLOAD_PARTS_DIR}'/' webapps/ingrid-portal-mdek-application/WEB-INF/classes/mdek.properties

    # Add toybox script if TOYBOX_TOKEN is define
    if [ "$TOYBOX_TOKEN" ]; then
        cat webapps/ROOT/decorations/layout/ingrid/header.vm | grep -q "ToyboxSnippet" || sed -i 's/<\/head>/<script src="'${TOYBOX_SRC}'" async data-id="ToyboxSnippet" data-token="'${TOYBOX_TOKEN}'"><\/script><\/head>/' webapps/ROOT/decorations/layout/ingrid/header.vm
        cat webapps/ROOT/decorations/layout/ingrid/header.vm | grep -q "ToyboxSnippet" || sed -i 's/<\/head>/<script src="'${TOYBOX_SRC}'" async data-id="ToyboxSnippet" data-token="'${TOYBOX_TOKEN}'"><\/script><\/head>/' webapps/ROOT/decorations/layout/ingrid-untitled/header.vm
    fi

    # Change measure client elasticsearch URL if MEASURECLIENT_ES_URL is define
    if [ "$MEASURECLIENT_ES_URL" ]; then
        sed -i 's|http.*-measure/|'${MEASURECLIENT_ES_URL}'|' webapps/ingrid-portal-apps/WEB-INF/templates/measures_search.vm
    fi

    # Change admin password for mapclient admin GUI if MAPCLIENT_ADMIN_PW is define
    if [ "$MAPCLIENT_ADMIN_PW" ]; then
        sed -i 's|password="admin" roles="admin-gui|password="'${MAPCLIENT_ADMIN_PW}'" roles="admin-gui|' conf/tomcat-users.xml
    fi

    # Change server.xml
    if [ "$HOSTNAME" ]; then
        sed -i 's|address=".*"|address="'${HOSTNAME}'"|' conf/server.xml
    fi

    touch /initialized
fi

if [ "$DEBUG" = 'true' ]; then
    ./bin/catalina.sh jpda run
else
    ./bin/catalina.sh run
fi
