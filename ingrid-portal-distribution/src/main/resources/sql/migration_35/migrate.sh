###
# **************************************************-
# InGrid Portal Distribution
# ==================================================
# Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
echo "1) Migration MySQL Portal-Datenbank auf Jetspeed Version 2.1.4"
mysql -uroot ingrid-portal < mysql_migrate_ingrid-portal.sql
echo "Fertig"
echo ""

echo "2) Download Jetspeed Installer 2.1.4"
wget -N http://ftp.fau.de/apache/portals/jetspeed-2/binaries/jetspeed-2.1.4-installer.jar
echo ""

echo "3) Löschen der Permissions aus DB"
mysql -uroot ingrid-portal < delete_permissions.sql

echo "4) Daten-Export (Options: 2,3,root,,jdbc:mysql://localhost:3306/ingrid-portal,migration_35/mysql-connector-java-*.jar)"
java -jar jetspeed-2.1.4-installer.jar
echo ""

echo "5) Download Jetspeed Installer 2.2.2"
wget -N http://ftp.fau.de/apache/portals/jetspeed-2/binaries/jetspeed-installer-2.2.2.jar
echo ""

echo "6) Datenbank-Initialisierung (Options: 3,3,root,,jdbc:mysql://localhost:3306/ingrid_portal,migration_35/mysql-connector-java-*.jar)"
java -jar jetspeed-installer-2.2.2.jar
echo ""

echo "7) Daten-Import (Options: 4,3,root,,jdbc:mysql://localhost:3306/ingrid_portal,migration_35/mysql-connector-java-*.jar)"
java -jar jetspeed-installer-2.2.2.jar
echo ""

echo "8) Ingrid Tabellen exportieren und importieren"
mysql ingrid-portal -uroot -e 'show tables like "ingrid_%"' | grep -v Tables_in | xargs mysqldump ingrid-portal -uroot > ingrid-tables.sql
mysql ingrid-portal -uroot -e 'show tables like "qrtz_%"' | grep -v Tables_in | xargs mysqldump ingrid-portal -uroot >> ingrid-tables.sql
mysql -uroot ingrid_portal < ingrid-tables.sql
echo ""
