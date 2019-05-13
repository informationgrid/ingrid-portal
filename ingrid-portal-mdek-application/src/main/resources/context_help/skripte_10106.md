---
# ID des GUI Elements
guid: 10106
# title, used as window title
title: Skripte
---

# Skripte

In diesem Bereich ist es möglich, zusätzlich JavaScript Code beim Starten des InGrid-Editors auszuführen. Dieser kann dazu dienen, dass ein Feld bspw. auf spezielle Ereignisse reagiert oder die funktionsweise des Feldes sogar erweitert.<br/>Unter dem IDF-Mapping wird der Code in JavaScript hinterlegt, welcher für die Abbildung des Feldes in das Ingrid-Daten-Format benötigt wird.

## Beispiel:

// lässt in einer Textbox (mit ID "userEmail") nur gültige Emailadressen zu<br/>dijit.byId("userEmail").regExpGen = dojox.validate.regexp.emailAddress;<br/><br/>// spezielle Überprüfung eines Textfeldes<br/>dijit.byId("doNotAllowNoInTextfield").validator = function(value) {if (value == "NO") return false; else return true;}<br/><br/>// Überprüfung vor einer Veröffentlichung<br/>dojo.subscribe("/onBeforeObjectPublish", function(/*Array*/notPublishableIDs) { if (dijit.byId("aField").get("value")) == "Kein" notPublishableIDs.push("aField");})
