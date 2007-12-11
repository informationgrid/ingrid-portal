Alle sql Skripte (*.sql) in src/sql/mysql, ../oracle, ... werden beim maven.xml goal 'createdb' u.U. ausgeführt.
Deshalb "temporaere" Skripte hier in tmp Folder und mit Endung ".txt" (zur Sicherheit).
Die finalen Update Skripte finden sich in "start-ingrid-portal" (Distributionen).