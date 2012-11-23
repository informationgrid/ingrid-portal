
initiales Schema:
=================

Portal-Datenbank:
-----------------
ingrid-portal-mysql40.sql
ingrid-portal-oracle_10_2_user_ingrid.dmp

IGE-Datenbank:
--------------
mdek_0_"plattform".sql



Updates:
========

Update Skripte nacheinander aufsteigend nach Versionsnr auf initialem Schema ausführen (Plattform wählen).
Updates einer Versionsnr können auch auf mehrere Deteien verteilt sein, diese sind dann mit *a, *b, *c ... gekennzeichnet und in dieser Reihenfolge auszuführen.

Portal-Datenbank:
-----------------
update_"versionsnr"_"plattform".sql

IGE-Datenbank:
--------------
mdek_"versionsnr"_"plattform".sql
(für Oracle keine Zwischenupdates vor 3.0.0, nur ein Update mdek_3.0.0_oracle)
