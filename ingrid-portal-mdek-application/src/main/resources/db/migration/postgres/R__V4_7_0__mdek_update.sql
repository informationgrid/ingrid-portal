UPDATE info SET value_name = '4.7.0' WHERE  info.key_name = 'version';

-- Fix user table for standalone IGE
DROP TABLE repo_user;

CREATE TABLE repo_user (
   login character varying(255) NOT NULL,
   version integer NOT NULL,
   password character varying(255) DEFAULT NULL,
   first_name character varying(255) DEFAULT NULL,
   surname character varying(255) DEFAULT NULL,
   email character varying(255) DEFAULT NULL,
   PRIMARY KEY (login)
);

UPDATE help_messages
SET help_text = 'Eintrag von Adressverweisen zu Personen oder Institutionen, die weitergehende Informationen zum beschriebenen Datensatz geben können. Bei Bedarf können diese Verweise geändert werden. In der ersten Spalte wird jeweils die Art des Verweises eingetragen ( Ansprechpartner, Anbieter, etc.). Über den Link "Adresse hinzufügen" wird der Verweis selbst angelegt. Als Auswahlmöglichkeit stehen alle in der Adressverwaltung des aktuellen Kataloges bereits eingetragenen Adressen zur Verfügung. Über das Kontextmenü ist es möglich, Adressen zu kopieren und einzufügen.<br><br>Es ist mind. eine Adresse anzugeben. Es sollte mind. ein Ansprechpartner angegeben werden.<br><br><b>Mögliche Einträge laut ISO/INSPIRE (Abweichungen möglich):</b><br><br><b>Anbieter</b><br>Anbieter der Ressource; ISO Adressrolle: resourceProvider<br><br><b>Ansprechpartner</b><br>Kontakt für Informationen zur Ressource oder deren Bezugsmöglichkeiten; ISO Adressrolle: pointOfContact<br><br><b>Autor</b><br>Verfasser der Ressource; ISO Adressrolle: author<br><br><b>Bearbeiter</b><br>Person oder Stelle, die die Ressource in einem Arbeitsschritt verändert hat; ISO Adressrolle: processor<br><br><b>Eigentümer</b><br>Eigentümer der Ressource; ISO Adressrolle: owner<br><br><b>Herausgeber</b><br>Person oder Stelle, welche die Ressource veröffentlicht; ISO Adressrolle: publisher<br><br><b>Nutzer</b><br>Nutzer der Ressource; ISO Adressrolle: user<br><br><b>Projektleitung</b><br>Person oder Stelle, die verantwortlich für die Erhebung der Daten, Untersuchung ist; ISO Adressrolle: principalInvestigator<br><br><b>Urheber</b><br>Erzeuger der Ressource; ISO Adressrolle: originator<br><br><b>Vertrieb</b><br>Person oder Stelle für den Vertrieb; ISO Adressrolle: distributor, wird für ISO Stuktur distributionContact verwendet<br><br><b>Verwalter</b><br>Person oder Stelle, welche die Zuständigkeit und Verantwortlichkeit für einen Datensatz übernommen hat und seine sachgerechte Pflege und Wartung sichert; ISO Adressrolle: custodian',
sample = 'Ansprechpartner / Robbe, Antje, Anbieter / Dr. Seehund, Siegfried'
WHERE gui_id = 1000 AND language = 'de';
