FROM tomcat:7-jre8
COPY /ingrid-portal-mdek-application.war /usr/local/tomcat/webapps/ingrid-portal-mdek-application.war

RUN mkdir -p /usr/local/tomcat/webapps/ingrid-portal-mdek-application \
    && unzip /usr/local/tomcat/webapps/ingrid-portal-mdek-application.war -d /usr/local/tomcat/webapps/ingrid-portal-mdek-application \
    && sed -i 's/jdbc:mysql:\/\/localhost/jdbc:mysql:\/\/db/' /usr/local/tomcat/webapps/ingrid-portal-mdek-application/WEB-INF/classes/default-datasource.properties \
    && sed -i 's/hibernate.password=/hibernate.password=${DB_PASSWORD}/' /usr/local/tomcat/webapps/ingrid-portal-mdek-application/WEB-INF/classes/default-datasource.properties \
    && echo 'installation.standalone=true' >> /usr/local/tomcat/webapps/ingrid-portal-mdek-application/WEB-INF/classes/mdek.override.properties \
    && echo 'admin.password=admin' > /usr/local/tomcat/webapps/ingrid-portal-mdek-application/WEB-INF/classes/igeAdminUser.properties

ADD frontend/log4j2.xml frontend/tomcat-juli-adapters.jar /usr/local/tomcat/lib/