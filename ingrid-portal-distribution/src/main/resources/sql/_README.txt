
=====================
1. initiale Datenbank
=====================

initiale Portal-Datenbank (für Neuinstallation):
------------------------------------------------
VERSION 3.5 (komplett neues Schema wegen Jetspeed Update):

    MySQL Server Version 5.5.8:
        ingrid_portal_35_mysql_558.sql

    Oracle Database 10g Express Edition Release 10.2.0.1.0 - Production:
        ingrid_portal_35_oracle_10_2.dmp
        ingrid_portal_35_oracle_10_2.log

        
initiale IGE-Datenbank:
-----------------------
mdek_0_"plattform".sql


===============================
2. Update vorhandene Datenbank:
===============================

Update Skripte nacheinander aufsteigend nach Versionsnr auf initialer Datenbank ausführen (Plattform wählen).
Updates einer Versionsnr können auch auf mehrere Dateien verteilt sein, diese sind dann mit *a, *b, *c ... gekennzeichnet und in dieser Reihenfolge auszuführen.


Portal-Datenbank:
-----------------
Die Migration einer Portaldatenbank nach Version 3.5 muss manuell via Jetspeed Tools ausgeführt werden.
Aufgrund des Updates von Jetspeed von Version 2.1 auf 2.3 ist dies ein mehrstufiger Prozess.
Siehe hierzu separate Dokumentation in Verzeichnis "migration_35".

Bis und ab der Version 3.5 liegen die Update Skripte in folgender Form vor:

update_"versionsnr"_"plattform".sql


IGE-Datenbank:
--------------
Die Update Skripte liegen in folgender Form vor:

mdek_"versionsnr"_"plattform".sql
(für Oracle keine Zwischenupdates vor 3.0.0, nur ein Update mdek_3.0.0_oracle)
