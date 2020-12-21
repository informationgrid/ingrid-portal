FROM alpine AS builder

COPY ingrid-portal*-installer.jar ./files /

RUN unzip ingrid-portal-*-installer.jar -d /tmp \
    && export INSTALL_DIR=`ls -d1 /tmp/distribution` \
    && mv $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-base.war $INSTALL_DIR/apache-tomcat/webapps/ROOT.war \
    && mkdir -p $INSTALL_DIR/apache-tomcat/webapps/ROOT \
    && unzip -q $INSTALL_DIR/apache-tomcat/webapps/ROOT.war -d $INSTALL_DIR/apache-tomcat/webapps/ROOT \
    && rm $INSTALL_DIR/apache-tomcat/webapps/ROOT.war \
    && mv $INSTALL_DIR/apache-tomcat/webapps/ROOT/WEB-INF/deploy/*.war $INSTALL_DIR/apache-tomcat/webapps \
    && mkdir -p $INSTALL_DIR/apache-tomcat/webapps/ROOT/html/nw/res \
    && mkdir -p $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-apps \
    && unzip -q $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-apps.war -d $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-apps \
    && rm $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-apps.war \
    && mkdir -p $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-mdek \
    && unzip -q $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-mdek.war -d $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-mdek \
    && rm $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-mdek.war \
    && mkdir -p $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-mdek-application \
    && unzip -q $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-mdek-application.war -d $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-mdek-application \
    && rm $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-mdek-application.war \
    && mkdir -p $INSTALL_DIR/apache-tomcat/webapps/ingrid-webmap-client \
    && unzip -q $INSTALL_DIR/apache-tomcat/webapps/ingrid-webmap-client.war -d $INSTALL_DIR/apache-tomcat/webapps/ingrid-webmap-client \
    && rm $INSTALL_DIR/apache-tomcat/webapps/ingrid-webmap-client.war \
    && sed -i 's/portal.logger.resource=.*$/portal.logger.resource=http:\/\/localhost:8080\/logger.html/' $INSTALL_DIR/apache-tomcat/webapps/ingrid-portal-apps/WEB-INF/classes/ingrid-portal-apps.properties \
    && mkdir -p $INSTALL_DIR/apache-tomcat/logs \
    && cp /run.sh -p $INSTALL_DIR/apache-tomcat/ \
    && cp /wait-for-it.sh -p $INSTALL_DIR/apache-tomcat/ \
    && chmod 755 $INSTALL_DIR/apache-tomcat/bin/*.sh


FROM docker-registry.wemove.com/ingrid-java:jre-8-alpine
ENV INGRID_USER=root
ENV PORTAL_CACHE_ENABLE=true
ENV IBUS_IP=ibus
ENV DB_USER=root
ENV DB_PASSWORD=""
ENV DB_DRIVERCLASS=com.mysql.jdbc.Driver
ENV DB_URL_MDEK=jdbc:mysql:\\/\\/mysql\\/mdek
ENV DB_URL_PORTAL=jdbc:mysql:\\/\\/mysql\\/ingrid_portal
ENV DB_DIALECT=org.hibernate.dialect.MySQLInnoDBDialect
ENV SMTP_HOST=localhost
ENV MAIL_SENDER=CHANGEME@wemove.com
ENV IGE_DIRECT_LINK=http://localhost:8080/ingrid-portal-mdek-application/index.jsp
ENV UPLOAD_DOCS_DIR=/tmp/ingrid/upload/documents/
ENV UPLOAD_PARTS_DIR=/tmp/ingrid/upload/parts/
ENV TOYBOX_SRC=https\\:\\/\\/d16ahjtmf9d1au\\.cloudfront\\.net\\/inject\\.bundle\\.js
ENV JAVA_OPTS='-Dfile.encoding=UTF8'

# user, group ingrid (1000:1000) is created in base image
COPY --chown=1000:1000 --from=builder /tmp/distribution/ /opt/ingrid/ingrid-portal/

USER ingrid

WORKDIR /opt/ingrid/ingrid-portal/apache-tomcat
EXPOSE 8000
EXPOSE 8009
EXPOSE 8080
EXPOSE 11000

CMD ["bash", "/run.sh"]
