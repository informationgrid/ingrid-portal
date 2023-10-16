<%--
  **************************************************-
  InGrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2023 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- Set the locale to the value of parameter 'lang' and init the message bundle messages.properties -->
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value='<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>' scope="session" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <title>InGrid-Portal</title>
        <meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
        <meta name="description" content="InGrid-Portal bietet kostenlosen und werbefreien Zugang zu Informationen &ouml;ffentlicher Institutionen und Organisationen. " />
        <meta name="author" content="wemove digital solutions" />
        <meta name="keywords" lang="de" content="InGrid-Portal, Umweltportal, Umweltinformationen, Deutschland, Bund, Bundesl&auml;nder, L&auml;nder, &ouml;ffentliche Institutionen, &ouml;ffentliche Organisationen, Suche, Recherche, werbefrei, kostenlos, Umweltdatenkataloge, Umwelt, UDK, Datenkataloge, Datenbanken" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
        <meta name="robots" content="index,follow" />
        <link rel="shortcut icon" href="/decorations/layout/ingrid/images/favicon.ico" />
        <link rel="stylesheet" href="/decorations/layout/ingrid/css/main.css" />
        <link rel="stylesheet" href="/decorations/layout/ingrid/css/override.css" />
        <link rel="stylesheet" href="/decorations/layout/ingrid/css/print.css" media="print">

        <script src="/decorations/layout/ingrid/scripts/ingrid.js"></script>
        <!-- Global scripts -->
        <script src="/decorations/layout/ingrid/scripts/modernizr.custom.min.js"></script>
        <script src="/decorations/layout/ingrid/scripts/jquery/jquery.min.js"></script>

        <script src="/decorations/layout/ingrid/scripts/jquery/nicescroll/jquery.nicescroll.min.js"></script>
        <script src="/decorations/layout/ingrid/scripts/jquery/foundation/foundation.min.js"></script>
        <script src="/decorations/layout/ingrid/scripts/jquery/select2/select2.full.js"></script>
        <script src="/decorations/layout/ingrid/scripts/main.js"></script>
    </head>
    <body>
        <div class="container">
            <div class="header-menu" style="display: none; overflow: hidden; outline: none;" tabindex="1">
                <div class="header-menu-close">
                    <button type="button" class="button ">Menü<span class="ic-ic-cross"></span></button>
                </div>
                <div class="menu-main-links">
                    <div class="highlighted">
                        <a href="/freitextsuche" class="header-menu-entry " title="Freitextsuche nach Umweltinformationen"><span class="text">Suche</span></a>
                        <a href="/kartendienste" class="header-menu-entry " title="Interaktive thematische Karten"><span class="text">Karte</span></a>
                        <a href="/messwertsuche" class="header-menu-entry " title="Suche nach Messdaten"><span class="text">Messwerte</span></a>
                        <a href="/datenkataloge" class="header-menu-entry " title="Datenkataloge"><span class="text">Kataloge</span></a>
                        <a href="/hintergrundinformationen" class="header-menu-entry " title="Hintergrundinformationen zu InGrid-Portal"><span class="text">Über InGrid</span></a>
                        <a href="/informationsanbieter" class="header-menu-entry " title="Kooperationspartner und Informationsanbieter"><span class="text">Informationsanbieter</span></a>
                        <a href="/datenquellen" class="header-menu-entry " title="Angeschlossene Datenbanken"><span class="text">Datenquellen</span></a>
                        <a href="/api" class="header-menu-entry " title="API Übersicht"><span class="text">API</span></a>
                    </div>
                    <a href="/hilfe" title="Erläuterungen zur Nutzung von InGrid-Portal - Neues Fenster öffnet sich"><span class="text">Hilfe</span></a>
                    <a href="/kontakt" title="Ihre Nachricht, Fragen oder Anregungen direkt an InGrid-Portal"><span class="text">Kontakt</span></a>
                    <a href="/inhaltsverzeichnis" title="Alle Inhalte von InGrid-Portal auf einen Blick"><span class="text">Sitemap</span></a>
                    <a href="/impressum" title="Inhaltlich und technisch Verantwortliche, Nutzungsbedingungen, Haftungsausschluss"><span class="text">Impressum</span></a>
                    <a href="/datenschutzbestimmung" title="Unsere Verpflichtung zum Umgang mit persönlichen Informationen"><span class="text">Datenschutz</span></a>
                    <a href="/barrierefreiheit" title="Er&auml;uterung zur Barrierefreiheit"><span class="text">Barrierefreiheit</span></a>
                </div>
            </div>
            <header>
                <div class="row">
                    <div class="columns xsmall-7 small-7 medium-7 large-7 xlarge-7">
                        <div class="logo">
                            <a href="/startseite"><img src="/decorations/layout/ingrid/images/template/logo.svg" alt="InGrid-Portal"/></a>
                        </div>
                    </div>
                    <div class="columns xsmall-16 small-15 medium-15 large-16 xlarge-14 nav-tabs">
                        <div class="menu-tab-row">
                            <a class="menu-tab" href="/freitextsuche" title="Freitextsuche nach Umweltinformationen">
                                <div class="link-menu-tab">
                                    <span class="ic-ic-lupe"></span>
                                    <span class="text">Suche</span>
                                </div>
                            </a>
                            <a class="menu-tab " href="/kartendienste" title="Interaktive thematische Karten">
                                <div class="link-menu-tab">
                                    <span class="ic-ic-karten"></span>
                                    <span class="text">Karte</span>
                                </div>
                            </a>
                            <a class="menu-tab " href="/messwertsuche" title="Suche nach Messdaten">
                                <div class="link-menu-tab">
                                    <span class="ic-ic-chemie"></span>
                                    <span class="text">Messwerte</span>
                                </div>
                            </a>
                            <a class="menu-tab " href="/datenkataloge" title="Datenkataloge">
                                <div class="link-menu-tab">
                                    <span class="ic-ic-datenkataloge"></span>
                                    <span class="text">Kataloge</span>
                                </div>
                            </a>
                            <a class="menu-tab " href="api" title="API Übersicht">
                                <div class="link-menu-tab">
                                    <span class="ic-ic-center"></span>
                                    <span class="text">API</span>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="columns">
                        <div class="header-menu-open">
                            <button type="button" class="button xsmall-button"><span class="ic-ic-hamburger"></span></button>
                            <button type="button" class="button small-button">Menü<span class="ic-ic-hamburger"></span></button>
                        </div>
                    </div>
                </div>
            </header>
            <div class="body">
                <div class="banner subpage">
                    <div class="subpage-wrapper" style="background-image: url('/decorations/layout/ingrid/images/template/drops-subpage.svg');">
                        <div class="row align-center">
                            <div class="large-20 columns dark">
                                <h1><fmt:message key="ui.entry.session.expired" /></h1>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row content-small">
                    <div class="columns">
                        <div class="form">
                            <p>
                                <fmt:message key="ui.entry.session.expired.text" />
                            </p>
                            <div class="link-list">
                                <a class="button" href="/log-in">
                                    <span class="text">Login</span>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <footer>
                <div class="footer">
                    <hr>
                    <div class="row">
                        <div class="xsmall-24 small-24 medium-24 large-24 xlarge-11 columns">
                            <div class="logo">
                                <a href="/startseite"><img class="footer__logo" src="/decorations/layout/ingrid/images/template/footer-logo.svg" alt="InGrid-Portal"></a>
                            </div>
                            <div class="copyright">
                                <span class="icon"></span>
                                <span class="text copyright_text">Nieders&auml;chsisches Ministerium f&uuml;r Umwelt, Energie, Bauen und Klimaschutz</span>
                            </div>
                        </div>
                        <div class="xsmall-24 small-24 medium-24 large-24 xlarge-13 columns">
                            <div class="footer-menu-entries">
                                <a href="/hilfe" title="Erläuterungen zur Nutzung von InGrid-Portal - Neues Fenster öffnet sich" class="icon"><span class="text">Hilfe</span></a>
                                <a href="/kontakt" title="Ihre Nachricht, Fragen oder Anregungen direkt an InGrid-Portal" class="icon"><span class="text">Kontakt</span></a>
                                <a href="/inhaltsverzeichnis" title="Alle Inhalte von InGrid-Portal auf einen Blick" class="icon"><span class="text">Sitemap</span></a>
                                <a href="/impressum" title="Inhaltlich und technisch Verantwortliche, Nutzungsbedingungen, Haftungsausschluss" class="icon"><span class="text">Impressum</span></a>
                                <a href="/datenschutzbestimmung" title="Unsere Verpflichtung zum Umgang mit persönlichen Informationen" class="icon"><span class="text">Datenschutz</span></a>
                                <a href="/barrierefreiheit" title="Er&auml;uterung zur Barrierefreiheit" class="icon"><span class="text">Barrierefreiheit</span></a>
                            </div>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    </body>
    <script src="/decorations/layout/ingrid/scripts/all.js"></script>
    <script>
      // Add shrink class
      var body = $("body");

      if(window.scrollY > 100) {
          body.addClass("shrink");
      }
      window.onscroll = function(e) {
        if (this.scrollY > 100) {
          body.addClass("shrink");
        } else {
          body.removeClass("shrink");
        }
      };
    </script>
</html>
