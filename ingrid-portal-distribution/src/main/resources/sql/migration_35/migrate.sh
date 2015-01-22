echo "1) Migration auf Version 2.1.4"
mysql -uroot ingrid-portal < mysql_migrate_ingrid-portal.sql
echo "Fertig"
echo ""

echo "2) Download Jetspeed Installer 2.1.4"
wget -N http://ftp.fau.de/apache/portals/jetspeed-2/binaries/jetspeed-2.1.4-installer.jar
echo ""

echo "3) Löschen der Permissions aus DB"
mysql -uroot ingrid-portal < delete_permissions.sql

echo "4) Daten-Export (Options: 2,3,root,,jdbc:mysql://localhost:3306/ingrid-portal,/home/ingrid/jetspeed-migration-script/mysql-connector-java-5.1.6.jar)"
java -jar jetspeed-2.1.4-installer.jar
echo ""

echo "5) Download Jetspeed Installer 2.2.2"
wget -N http://ftp.fau.de/apache/portals/jetspeed-2/binaries/jetspeed-installer-2.2.2.jar
echo ""

echo "6) Datenbank-Initialisierung (Options: 3,3,root,,jdbc:mysql://localhost:3306/ingrid_portal,/home/ingrid/jetspeed-migration-script/mysql-connector-java-5.1.6.jar)"
java -jar jetspeed-installer-2.2.2.jar
echo ""

echo "7) Daten-Import (Options: 4,3,root,,jdbc:mysql://localhost:3306/ingrid_portal,/home/ingrid/jetspeed-migration-script/mysql-connector-java-5.1.6.jar)"
java -jar jetspeed-installer-2.2.2.jar
echo ""

echo "8) Ingrid Tabellen exportieren und importieren"
mysql ingrid-portal -uroot -e 'show tables like "ingrid_%"' | grep -v Tables_in | xargs mysqldump ingrid-portal -uroot > ingrid-tables.sql
mysql ingrid-portal -uroot -e 'show tables like "qrtz_%"' | grep -v Tables_in | xargs mysqldump ingrid-portal -uroot >> ingrid-tables.sql
mysql -uroot ingrid_portal < ingrid-tables.sql
echo ""