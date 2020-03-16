FROM docker-registry.wemove.com/ingrid-java:jdk-8
ENV INGRID_USER=root
ENV PORTAL_CACHE_ENABLE=true
ENV IBUS_IP=ibus
ENV DB_USER=root
ENV DB_PASSWORD=""
ENV DB_DRIVERCLASS=com.mysql.jdbc.Driver
ENV DB_URL_MDEK=jdbc:mysql:\\/\\/mysql\\/mdek
ENV DB_URL_PORTAL=jdbc:mysql:\\/\\/mysql\\/ingrid_portal
ENV DB_DIALECT=org.hibernate.dialect.MySQL5InnoDBDialect
ENV SMTP_HOST=localhost
ENV MAIL_SENDER=test@wemove.de
ENV IGE_DIRECT_LINK=http://localhost:8080/ingrid-portal-mdek-application/index.jsp
ENV UPLOAD_DOCS_DIR=/tmp/ingrid/upload/documents/
ENV UPLOAD_PARTS_DIR=/tmp/ingrid/upload/parts/
ENV TOYBOX_SRC=https\\:\\/\\/d16ahjtmf9d1au\\.cloudfront\\.net\\/inject\\.bundle\\.js

ADD ingrid-portal*-installer.jar /

RUN unzip ingrid-portal*-installer.jar -d /tmp \
    && mkdir -p /opt/ingrid/ingrid-portal \
    && cp -R /tmp/distribution/* /opt/ingrid/ingrid-portal/ \
    && mv /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-base.war /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ROOT.war \
    && mkdir -p /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ROOT \
    && unzip -q /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ROOT.war -d /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ROOT \
    && mv /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ROOT/WEB-INF/deploy/*.war /opt/ingrid/ingrid-portal/apache-tomcat/webapps \
    && mkdir -p /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-apps \
    && unzip -q /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-apps.war -d /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-apps \
    && mkdir -p /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-mdek \
    && unzip -q /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-mdek.war -d /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-mdek \
    && mkdir -p /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-mdek-application \
    && unzip -q /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-mdek-application.war -d /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-mdek-application \
    && mkdir -p /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-webmap-client \
    && unzip -q /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-webmap-client.war -d /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-webmap-client \
    && sed -i 's/portal.logger.resource=.*$/portal.logger.resource=http:\/\/localhost:8080\/logger.html/' /opt/ingrid/ingrid-portal/apache-tomcat/webapps/ingrid-portal-apps/WEB-INF/classes/ingrid-portal-apps.properties \
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
