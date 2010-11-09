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