Eclipse WTP einrichten
----------------------
Die Mdek-Applikation kann separat als Eclipse WTP betrieben werden. Dies vereinfacht die Entwicklung,
da der Hotdeploy Mechanismus genutzt werden kann. Es muss also nicht immer das gesamte Projekt mit maven
gebaut werden um kleinen Änderungen zu testen.

Folgende Schritte sind dazu notwendig:
- Einrichten eines mdek iplug (siehe ingrid-mdek projekt doku)
- Einrichten der 'mdek' Datenbank
- Einrichten eines Servers unter Eclipse (New... Other... Server)
- pom.xml nach dev-pom.xml kopieren
- in <groupId>org.apache.maven.plugins</groupId>
  
  in der configuration muss Schlüssel
  
  <useProjectReferences>false</useProjectReferences>
  
  eingetragen werden.

- Eclipse Projekt erstellen mit 'dev-pom.xml'. Dort werden alle Abhängigkeiten direkt eingebunden.
 - mvn -f dev-pom.xml eclipse:clean eclipse:eclipse
- Refresh Project
- Projekt zum Server hinzufügen (Servers view, rechtsklick auf server, add project...)
- In Server Launch Config (Doppelklick auf Tomcat Server Eintrag in server View -> Open launch config..)

  -Djava.endorsed.dirs="C:\Programme\Apache\apache-tomcat-6.0.29\endorsed"
  
  als Start-Argument eintragen.
- Server starten


Server root ist: %WORKSPACE_DIR%\.metadata\.plugins\org.eclipse.wst.server.core\tmp0
Die mdek-Webapp befindet sich in: %WORKSPACE_DIR%\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\ingrid-portal-mdek-application

Es werden ausschliesslich libs verwendet, die in der dev-pom.xml angegeben wurden (direkt aus dem maven2 repo).
Das shared\lib Verzeichnis der normalen Tomcat Installation hat hiermit NICHTS mehr zu tun (falls man sich mal
nicht sicher ist welche libs verwendet werden :) )!

Da wir das Portal nicht mehr für die Anmeldung verwenden können, müssen wir uns 'manuell' anmelden.
Dies kann über verschiedene Wege gemacht werden:
1. Die Klasse de.ingrid.mdek.util.MdekSecurityUtils enthält eine Methode 'public static UserData getCurrentPortalUserData()'.
   Diese Methode liest das user principal aus und für den gefundenen Namen die Daten aus der mdek Datenbank.
   Die Methode kann einfach immer ein fixes UserData Objekt zurückliefern: 'return getUserData("mdek");'
2. Login über dev_login.jsp
   Die Datei dev_login.jsp setzt den Benutzernamen auf einen beliebigen Wert (über den Parameter 'user') und leitet die Anfrage
   an die mdek-app weiter (über Parameter 'page') steuerbar. Beispiele:
   - Anmelden als Benutzer 'mdek'. Standard Ziel ist mdek_entry.jsp:
     http://localhost:8080/ingrid-portal-mdek-application/dev_login.jsp?user=mdek
   - Anmelden als Benutzer 'mdek'. Zieladresse ist mdek_admin_entry.jsp:
     http://localhost:8080/ingrid-portal-mdek-application/dev_login.jsp?user=mdek&page=mdek_admin_entry.jsp
   - Anmelden als Benutzer 'mdek' an der dwr Testseite:
     http://localhost:8080/ingrid-portal-mdek-application/dev_login.jsp?user=mdek&page=dwr

   - Mögliche Parameter:
     - user=mdek -> Als Benutzername wird 'mdek' verwendet. Der Benutzer muss in der mdek Datenbank existieren
       und mit dem verwendeten iplug korrekt verbunden sein
     - page=mdek_admin_entry.jsp -> weiterleiten zu mdek_admin_entry.jsp. Default ist mdek_entry.jsp
     - debug=true -> setze dojo Debug auf true. Default ist false
     - lang=en -> setze die verwendete Sprache auf Englisch. Default ist Deutsch (de)

Die zweite Methode ist zu bevorzugen falls man sich häufig mit verschiedenen Benutzernamen anmelden möchte (Testen der Qualitätskontrolle)

Achtung, es muss ein entsprechender Eintrag in der mdek Datenbank vorhanden sein, der den user mit der Adresse im ige-iPlug verbindet.

z.B. INSERT INTO `mdek`.`user_data` (`id`, `version`, `addr_uuid`, `portal_login`, `plug_id`) VALUES ('1', '1', 'BF9FD87B-6D67-4B7A-B554-AB30BB2A0863', 'mdek', 'ige-iplug-test');

DWR Testseite
-------------
Über die DWR Testseite kann getestet werden, ob die verschiedenen Services korrekt eingebunden wurden.
Die DWR Testseite ist über folgende URL zu erreichen (user ersetzen):
http://localhost:8080/ingrid-portal-mdek-application/dev_login.jsp?user=mdek&page=dwr
dev_login, da man für die meissten Funktionen als Benutzer angemeldet sein muss.


Verwenden von lokalen Projekten an Stelle von jars aus maven repo
-----------------------------------------------------------------
Dazu muss folgendes getan werden:
- Project Properties, Java Build Path:
  - Projekt bei 'Projects' hinzufügen (z.B. ingrid-iplug-sns)
  - Entsprechende Library bei 'Libraries' entfernen (M2_REPO/de/ingrid/...)
- Project Properties, Java EE Module Dependencies:
  - Projekt als Abhängigkeit selektieren (sollte ganz oben in der Liste erscheinen)
  - jar als Abhängigkeit deselektieren (var/M2_REPO/...)