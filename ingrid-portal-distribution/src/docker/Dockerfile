FROM ingrid-java:jdk-8
ENV INGRID_USER=root

ADD ingrid-portal*-installer.jar /

RUN unzip ingrid-portal*-installer.jar -d /tmp \
    && mkdir -p /opt/ingrid/ingrid-portal \
    && cp -R /tmp/distribution/* /opt/ingrid/ingrid-portal/ \
    && mv /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-base.war /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ROOT.war \
    && mkdir -p /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ROOT \
    && unzip /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ROOT.war -d /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ROOT \
    && mv /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ROOT/WEB-INF/deploy/*.war /opt/ingrid/ingrid-portal/apache-tomcat/webapps \
    && mkdir -p /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-apps \
    && unzip /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-apps.war -d /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-apps \
    && mkdir -p /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-mdek-application \
    && unzip /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-mdek-application.war -d /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-mdek-application \
    && sed -i 's/jdbc:mysql:\/\/localhost\/ingrid-portal?autoReconnect=true/jdbc:mysql:\/\/mysql\/ingrid_portal/' /opt/ingrid/ingrid-portal/apache-tomcat/conf/Catalina/localhost/ingrid-portal-apps.xml \
    && sed -i 's/password=""/password="${DB_PASSWORD}"/' /opt/ingrid/ingrid-portal/apache-tomcat/conf/Catalina/localhost/ingrid-portal-apps.xml \
    && sed -i 's/jdbc:mysql:\/\/localhost/jdbc:mysql:\/\/mysql/' /opt/ingrid/ingrid-portal/apache-tomcat/conf/Catalina/localhost/ingrid-portal-mdek.xml \
    && sed -i 's/password=""/password="${DB_PASSWORD}"/' /opt/ingrid/ingrid-portal/apache-tomcat/conf/Catalina/localhost/ingrid-portal-mdek.xml \
    && sed -i 's/jdbc:mysql:\/\/localhost\/ingrid-portal?autoReconnect=true/jdbc:mysql:\/\/mysql\/ingrid_portal/' /opt/ingrid/ingrid-portal/apache-tomcat/conf/Catalina/localhost/ROOT.xml \
    && sed -i 's/password=""/password="${DB_PASSWORD}"/' /opt/ingrid/ingrid-portal/apache-tomcat/conf/Catalina/localhost/ROOT.xml \
    && sed -i 's/jdbc:mysql:\/\/localhost/jdbc:mysql:\/\/mysql/' /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-mdek-application/WEB-INF/classes/default-datasource.properties \
    && sed -i 's/hibernate.password=/hibernate.password=${DB_PASSWORD}/' /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-mdek-application/WEB-INF/classes/default-datasource.properties \
    && sed -i 's/portal.logger.resource=.*$/portal.logger.resource=http:\/\/localhost:8080\/logger.html/' /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-apps/WEB-INF/classes/ingrid-portal-apps.properties \
    && echo "communications.ibus=/ingrid-group:ibus,ingrid-ibus-container,9900" > /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-apps/WEB-INF/classes/ingrid-portal-apps.override.properties \
    && mkdir -p /opt/ingrid/ingrid-portal/apache-tomcat/logs \
    # && chgrp -R ingrid /opt/ingrid \
    # && chown -R ingrid /opt/ingrid \
    && chmod 755 /opt/ingrid/ingrid-portal/apache-tomcat/bin/*.sh \
    && rm -Rf /tmp/* \
    && rm ingrid-*.jar

COPY ./files /

WORKDIR /opt/ingrid/ingrid-portal/apache-tomcat
EXPOSE 8000
EXPOSE 8009
EXPOSE 8080
EXPOSE 11000

CMD ["bash", "/run.sh"]
