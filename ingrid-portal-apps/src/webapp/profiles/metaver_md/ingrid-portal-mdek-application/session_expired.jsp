<%--
  **************************************************-
  Ingrid Portal Base
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

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <title>MetaVer</title>
        <meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <meta name="description" content="MetaVer bietet kostenlosen und werbefreien Zugang zu Informationen &ouml;ffentlicher Institutionen und Organisationen. " />
        <meta name="author" content="wemove digital solutions" />
        <meta name="keywords" lang="de" content="MetaVer, Umweltportal, Umweltinformationen, Deutschland, Bund, Bundesl&auml;nder, L&auml;nder, &ouml;ffentliche Institutionen, &ouml;ffentliche Organisationen, Suche, Recherche, werbefrei, kostenlos, Umweltdatenkataloge, Umwelt, UDK, Datenkataloge, Datenbanken" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
        <meta name="robots" content="index,follow" />
        <link rel="shortcut icon" href="/decorations/layout/ingrid/images/favicon.ico" />
        <link rel="stylesheet" href="/decorations/layout/ingrid/css/main.css" />
        <link rel="stylesheet" href="/decorations/layout/ingrid/css/override.css" />
        <script src="/decorations/layout/ingrid/scripts/jquery/jquery.min.js"></script>

        <script src="/decorations/layout/ingrid/scripts/jquery/nicescroll/jquery.nicescroll.min.js"></script>
        <script src="/decorations/layout/ingrid/scripts/jquery/foundation/foundation.min.js"></script>
        <script src="/decorations/layout/ingrid/scripts/jquery/select2/select2.full.js"></script>
        <script src="/decorations/layout/ingrid/scripts/main.js"></script>
    </head>
    <body>
        <div class="container">
            <header>
                <div class="row">
                    <div class="columns xsmall-7 small-7 medium-5 large-5 xlarge-5">
                        <div class="logo">
                            <a href="/startseite"><img src="/decorations/layout/ingrid/images/template/logo.svg" alt="MetaVer"/></a>
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
                                <a class="icon" href="/log-in">
                                    <span class="ic-ic-arrow"></span>
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
                                <a href="/startseite"><img class="footer__logo" src="/decorations/layout/ingrid/images/template/footer-logo.svg" alt="MetaVer"></a>
                            </div>
                            <div class="copyright">
                                <span class="icon"></span>
                                <span class="text copyright_text">Landesbetrieb Geoinformation und Vermessung. Alle Rechte vorbehalten.</span>
                            </div>
                        </div>
                        <div class="xsmall-24 small-24 medium-24 large-24 xlarge-13 columns">
                            <div class="footer-menu-entries">
                                <a href="/hilfe" title="Erläuterungen zur Nutzung von MetaVer - Neues Fenster öffnet sich" class="icon"><span class="text">Hilfe</span></a>
                                <a href="/kontakt" title="Ihre Nachricht, Fragen oder Anregungen direkt an MetaVer" class="icon"><span class="text">Kontakt</span></a>
                                <a href="/inhaltsverzeichnis" title="Alle Inhalte von MetaVer auf einen Blick" class="icon"><span class="text">Sitemap</span></a>
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
</html>
