help_messages.csv reflects the latest state of the help messages ! Edit with Open Office then create SQL for start-portal (see description below).

Import in Open Office:
- Use # as a separator and ' as a text delimiter

Import in MySQL/ Create SQL:
- select table "help_messages"
- import "csv" file
	- replace table content
	- separator: #
	- enclosed with: '
- export table "help_messages"
	- add 'drop table'

Export from MySQL to CSV:
- select CSV
	- separator: #
	- enclosed with: '

Beschreibung, s. auch https://dev2.wemove.com/jira/browse/INGRID23-238
    angehängt ist die csv Datei mit allen aktuellen Hilfetexten.
    Diese bitte in OpenOffice (!) öffnen mit
    - Zeichensatz Unicode (UTF-8)
    - getrennt durch Andere -> #
    - Texttrenner -> '

    In den Inhalten bitte nicht die Zeichen # und ' verwenden.

    Aus OpenOffice kann das Ganze wieder als csv gespeichert werden mit:
    - "Speichern unter": Text CSV auswählen + checkbox "Filtereinstellungen bearbeiten" setzen !!!
    - dann "akt. Format beibehalten" und Einstellungen wie oben (Zeichensatz, Trenner ...)

    In dem File sind die deutschen Texte für die genannten gui_ids drinnen. Bitte entsprechend die engl. am Ende einfügen mit aufsteigender Datensatz Id (falls noch nicht drinnen).
