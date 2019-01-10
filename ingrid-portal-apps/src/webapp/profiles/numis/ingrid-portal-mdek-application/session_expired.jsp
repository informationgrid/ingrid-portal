<%--
  **************************************************-
  InGrid Portal Apps
  ==================================================
  Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- Set the locale to the value of parameter 'lang' and init the message bundle messages.properties -->
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setLocale
    value='<%=request.getParameter("lang") == null ? "de" : request.getParameter("lang")%>'
    scope="session" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <title>NUMIS - Das niedersächsische Umweltportal</title>
        <meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
        <meta http-equiv="Content-style-type" content="text/css" />
        <meta http-equiv="content-language" content="de" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
        <meta name="description" content="NUMIS bietet kostenlosen und werbefreien Zugang zu Informationen &ouml;ffentlicher Institutionen und Organisationen. " />
        <meta name="author" content="© 2017 Niedersächsisches Ministerium für Umwelt, Energie, Bauen und Klimaschutz" />
        <meta name="keywords" lang="de" content="NUMIS, Umweltportal, Umweltinformationen, Deutschland, Bund, Bundesl&auml;nder, L&auml;nder, &ouml;ffentliche Institutionen, &ouml;ffentliche Organisationen, Suche, Recherche, werbefrei, kostenlos, Umweltdatenkataloge, Umwelt, UDK, Datenkataloge, Datenbanken" />
        <meta name="copyright" content="© 2017 Niedersächsisches Ministerium für Umwelt, Energie, Bauen und Klimaschutz" />
        <meta name="robots" content="index,follow" />
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto+Condensed:400,700"></link>
        <link rel="shortcut icon" href="/decorations/layout/ingrid/images/favicon.ico " />
        <link rel="stylesheet" type="text/css" media="screen, projection" href="/decorations/layout/ingrid/css/style.css" />
        <link rel="stylesheet" type="text/css" media="screen, projection" href="/decorations/layout/ingrid/css/override.css" />
        <script language="JavaScript" src="/decorations/layout/ingrid/scripts/ingrid.js" type="text/javascript"></script>
    </head>
    <body class="ingrid">
        <div class="nav-overlay">
            <a title="Login" class="nav-overlay__item nav-overlay__item--level1" href="/log-in">Login</a>
            <ul class="nav__mobile nav__foot">
                <li>
                   <a title="Erläuterungen zur Nutzung von NUMIS" href="/hilfe">Hilfe</a>
                </li>
                <li>
                   <a title="Ihre Nachricht, Fragen oder Anregungen direkt an NUMIS" href="/kontakt">Kontakt</a>
               </li>
                <li>
                   <a title="Alle Inhalte von NUMIS auf einen Blick" href="/inhaltsverzeichnis">Inhalt</a>
               </li>
                <li>
                   <a title="Inhaltlich und technisch Verantwortliche, Nutzungsbedingungen, Haftungsausschluss" href="/impressum">Impressum</a>
               </li>
                <li>
                   <a title="Unsere Verpflichtung zum Umgang mit pers&ouml;nlichen Informationen" href="/datenschutzbestimmung">Datenschutz</a>
               </li>
            </ul>
        </div>
        <div id="wrapper">
            <header class="header">
                <div class="ob-box-wide ob-box-padded ob-box-center">
                    <div class="header_content">
                        <div class="header__widgets__section">
                            <div class="header__widget nav-toggle mq-show-xxl js-nav-mobile-toggle">
                                <svg class="icon"> <use xlink:href="#burger"> </use></svg>
                                <svg class="icon ob-fade"> <use xlink:href="#cross"> </use></svg>
                            </div>
                        </div>
                        <div class="header__widgets__section header_logo_section">
                            <a class="header__logo header__widget header__logo" href="/startseite" title="Startseite von NUMIS aufrufen ">
                                <img class="mq-hide-xxl desktop-logo" src="/decorations/layout/ingrid/images/template/mu-logo.png ">
                                <img class="mq-show-xxl mq-hide-m mobile__logo" src="/decorations/layout/ingrid/images/template/mu-logo.png ">
                                <img class="mq-show-m mobile__logo" src="/decorations/layout/ingrid/images/template/mobile-logo.png ">
                            </a>
                        </div>
                        <div class="header__widgets__section sub-header mq-hide-xxl">
                            <div class="header__widget">
                                <a class="header__widget__toggle tx-upper " href="/log-in">Login</a>
                            </div>
                        </div>
                    </div>
                </div>
            </header>
            <!-- CONTENT BLOCK -->
            <section class="block block--padded">
                <div class="ob-box-wide ob-box-padded ob-box-center">
                    <article class="content ob-container">
                        <h1>
                            <fmt:message key="ui.entry.session.expired" />
                        </h1>
                        <p>
                            <fmt:message key="ui.entry.session.expired.text" />
                        </p>
                        <br />
                    </article>
                </div>
            </section>
            <footer class="footer">
                <div class="ob-box-wide ob-box-padded ob-box-center">
                    <a href="/startseite">
                        <img class="footer__logo" src="/decorations/layout/ingrid/images/template/mobile-logo.png ">
                    </a>
                   <hr class="bx-top-xs bx-bot-xs">
                    <div class="grid grid--gutter">
                        <div class="column column--3-5-xl">
                            <p class="copyright">© 2017 Niedersächsisches Ministerium für Umwelt, Energie, Bauen und Klimaschutz</p>
                        </div>
                        <div class="column column--2-5-xl">
                            <nav class="footer__nav">
                                <a href="/hilfe" title="Erläuterungen zur Nutzung von NUMIS - Neues Fenster öffnet sich">Hilfe</a>
                                <a href="/kontakt" title="Ihre Nachricht, Fragen oder Anregungen direkt an NUMIS">Kontakt</a>
                                <a href="/inhaltsverzeichnis" title="Alle Inhalte von NUMIS auf einen Blick">Inhalt</a>
                                <a href="/impressum" title="Inhaltlich und technisch Verantwortliche, Nutzungsbedingungen, Haftungsausschluss">Impressum</a>
                                <a href="/datenschutzbestimmung" title="Unsere Verpflichtung zum Umgang mit persönlichen Informationen">Datenschutz</a>
                            </nav>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
        <!-- Global scripts -->
        <script src="/decorations/layout/ingrid/scripts/modernizr.custom.min.js"></script>
        <script src="/decorations/layout/ingrid/scripts/jquery-2.1.4.min.js"></script>
        <script src="/decorations/layout/ingrid/scripts/fastclick.min.js"></script>
        <script src="/decorations/layout/ingrid/scripts/all.js"></script>
    </body>
</html>
