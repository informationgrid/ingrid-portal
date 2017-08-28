
=====================
1. initiale Datenbank
=====================

initiale Portal-Datenbank:
--------------------------
InGrid Portal VERSION 3.5 (komplett neues Schema wegen Jetspeed Update):
Die Datenbank muss aus den Dumps neu erzeugt werden.

Wird auf MySQL vom Installer erzeugt und eingelesen.

    MySQL Server Version 5.5.8:
        ingrid_portal_35_mysql_558.sql

Auf Oracle muss die Datenbank (z.B. User ingrid_portal) manuell erzeugt und der dump importiert werden.

    Oracle Database 10g Express Edition Release 10.2.0.1.0 - Production:
        ingrid_portal_35_oracle_10_2.dmp
        ingrid_portal_35_oracle_10_2.log

        Beispiel Import nach user INGRID_PORTAL_NEW (Schema mapping, die Portal Datenbank aber am besten INGRID_PORTAL nennen, dann muss das Schema nicht gemappt werden):
            impdp INGRID_PORTAL_NEW/pwd DIRECTORY=DATA_PUMP_DIR DUMPFILE=ingrid_portal_35_oracle_10_2.dmp remap_schema=INGRID_PORTAL:INGRID_PORTAL_NEW TABLE_EXISTS_ACTION=replace LOGFILE=INGRID_PORTAL_IMPORT.log

        
initiale IGE-Datenbank (mdek):
------------------------------
Wird auf MySQL vom Installer erzeugt und eingelesen.

Auf Oracle muss die Datenbank (z.B. User mdek) manuell erzeugt werden, kann jedoch leer sein.
Schema und Daten werden vom Installer eingelesen.


===============================
2. Update vorhandene Datenbank:
===============================

Die Update Skripte werden bei der Installation mit installiert und vom Installer ausgeführt (Verzeichnis sql).

Manueller Update:
Update Skripte nacheinander aufsteigend nach Versionsnr auf initialer Datenbank ausführen (Plattform wählen).
Updates einer Versionsnr können auch auf mehrere Dateien verteilt sein, diese sind dann mit *a, *b, *c ... gekennzeichnet und in dieser Reihenfolge auszuführen.


Portal-Datenbank:
-----------------
Die Migration einer Portaldatenbank nach Version 3.5 muss manuell via Jetspeed Tools ausgeführt werden.
Aufgrund des Updates von Jetspeed von Version 2.1 auf 2.3 ist dies ein mehrstufiger Prozess.
Siehe hierzu separate Dokumentation im Verzeichnis "sql/migration_35" der 3.5 Portal Installation.

Bis und ab der Version 3.5 liegen die Update Skripte in folgender Form vor:

update_"versionsnr"_"plattform".sql


IGE-Datenbank (mdek):
---------------------
Die Update Skripte liegen in folgender Form vor:

mdek_"versionsnr"_"plattform".sql
(für Oracle keine Zwischenupdates vor 3.0.0, nur ein Update mdek_3.0.0_oracle)
