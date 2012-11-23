
Switch Portal to Oracle:
-----------------
Tomcat:
- Use the
    conf\Catalina\localhost\ROOT.xml.oracle
    conf\Catalina\localhost\ingrid-portal-apps.xml.oracle
    conf\Catalina\localhost\ingrid-portal-mdek.xml.oracle
  context files and adapt JDBC settings.
- Adapt settings to ORACLE in
    webapps\ingrid-portal-apps\WEB-INF\classes\hibernate.cfg.xml (SQL dialect)
    webapps\ingrid-portal-mdek\WEB-INF\classes\hibernate.cfg.xml (SQL dialect)
    webapps\ingrid-portal-mdek-application\WEB-INF\classes\default-datasource.properties


OJDBC Driver:
-------------

Because of problems with different locales (see http://forums.oracle.com/forums/thread.jspa?threadID=378016)
we provide a different ojdbc driver, than the one loaded via dependencies.
Problems occur because we pass "-Duser.language=en" to tomcat (in GS Soil).

So ojdbc driver is removed from dependencies and
Oracle Database 10g Release 2 (10.2.0.2) JDBC Driver
is supplied in
apache-tomcat-...\shared\lib

See
http://www.oracle.com/technetwork/database/features/jdbc/jdbc-10201-088211.html
http://www.oracle.com/technetwork/database/features/jdbc/readme-jdbc-10202-085005.html
Fixed Bug:
4629654 ORA-12705 at JDBC connect with mixed language/country for locale
