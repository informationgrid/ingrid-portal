<%--
  **************************************************-
  InGrid Portal Apps
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
        <title>Geodatenkatalog Niederrhein</title>
        <link rel="shortcut icon" href="/decorations/layout/ingrid/images/favicon.ico">
        <link rel="prefetch" href="/decorations/layout/ingrid/images/template/icons.svg">
        <meta http-equiv="Content-type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta content="width=device-width, initial-scale=1" name="viewport">
        <meta name="description" content="Geodatenkatalog Niederrhein bietet kostenlosen und werbefreien Zugang zu Informationen Institutionen und Organisationen. ">
        <meta name="author" content="2023 Zweckverband Kommunales Rechenzentrum Niederrhein">
        <meta name="keywords" content="Geodatenkatalog Niederrhein, Portal, Informationen, Institutionen, Organisationen, Suche, Recherche, werbefrei, kostenlos, Datenkataloge, Datenbanken" lang="de">
        <meta name="copyright" content="2023 Zweckverband Kommunales Rechenzentrum Niederrhein">
        <meta name="robots" content="index,follow">
        <link rel="stylesheet" href="/decorations/layout/ingrid/css/main.css">
        <link rel="stylesheet" href="/decorations/layout/ingrid/css/override.css">
        <link rel="stylesheet" href="/decorations/layout/ingrid/css/print.css" media="print">

        <script src="/decorations/layout/ingrid/scripts/ingrid.js"></script>
        <!-- Global scripts -->
        <script src="/decorations/layout/ingrid/scripts/modernizr.custom.min.js"></script>
        <script src="/decorations/layout/ingrid/scripts/jquery/jquery.min.js"></script>

        <script src="/decorations/layout/ingrid/scripts/jquery/nicescroll/jquery.nicescroll.min.js"></script>
        <script src="/decorations/layout/ingrid/scripts/jquery/foundation/foundation.min.js"></script>
        <script src="/decorations/layout/ingrid/scripts/jquery/select2/select2.full.js"></script>
        <script src="/decorations/layout/ingrid/scripts/main.js"></script>
        <meta class="foundation-mq">
    </head>
    <body>
        <div class="container">
            <div class="header-menu" style="display: none; overflow: hidden; outline: none; cursor: grab; touch-action: none;" role="navigation" aria-label="Navigation Menu">
                <div class="header-menu-close">
                    <button type="button" class="button ">Menu<span class="ic-ic-cross"></span></button>
                </div>
                <div class="menu-main-links">
                    <div class="highlighted">
                        <a href="/freitextsuche" class="header-menu-entry " title="Freitextsuche nach Informationen"><span class="text">Suche</span></a>
                        <a href="/hintergrundinformationen" class="header-menu-entry " title="Hintergrundinformationen zu KRZN"><span class="text">Über KRZN</span></a>
                    </div>
                    <a href="/impressum" title="Inhaltlich und technisch Verantwortliche, Nutzungsbedingungen, Haftungsausschluss" target="_blank"><span class="text">Impressum</span></a>
                    <a href="/datenschutzbestimmung" title="Unsere Verpflichtung zum Umgang mit persönlichen Informationen" target="_blank"><span class="text">Datenschutz</span></a>
                    <a href="/barrierefreiheit" title="Barrierefreiheit" target="_blank"><span class="text">Barrierefreiheit</span></a>
                </div>
                <div class="menu-sub-links"></div>
            </div>
            <header>
                <div class="row">
                    <div class="columns xsmall-19 small-19 medium-19 large-17 xlarge-18">
                        <div class="row">
                            <div class="columns xsmall-4 small-4 medium-4 large-4 xlarge-4">
                                <div class="logo" role="banner">
                                    <a href="/startseite" title="Startseite vom Geodatenkatalog Niederrhein">
                                        <img src="/decorations/layout/ingrid/images/template/logo.png" alt="Geodatenkatalog Niederrhein">
                                    </a>
                                </div>
                            </div>
                            <div class="columns xsmall-10 small-10 medium-20 large-20 xlarge-18">
                                <div class="desktop__title">
                                    <span>
                                        <strong>G</strong>eodatenkatalog <strong>N</strong>iederrhein
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="columns xsmall-3 small-3 medium-3 large-3 xlarge-3 nav-tabs">
                        <div class="menu-tab-row" role="navigation" aria-label="Navigation Header">
                            <a class="menu-tab " href="/freitextsuche" title="Freitextsuche nach Informationen">
                                <div class="link-menu-tab">
                                    <span class="ic-ic-lupe"></span>
                                    <span class="text">Suche</span>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="columns">
                        <div class="header-menu-open">
                            <button type="button" class="button xsmall-button">
                                <span class="ic-ic-hamburger"></span>
                            </button>
                            <button type="button" class="button small-button">
                                Menu<span class="ic-ic-hamburger"></span>
                            </button>
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
                        <div class="xsmall-24 small-24 medium-24 large-24 xlarge-10 columns">
                            <div class="logo">
                                <a href="/startseite" title="Startseite zu KRZN">
                                    <img class="footer__logo" src="/decorations/layout/ingrid/images/template/footer-logo.svg" alt="KRZN">
                                </a>
                        </div>
                        <div class="copyright">
                            <span class="icon"></span>
                            <span class="text copyright_text">2023 Zweckverband Kommunales Rechenzentrum Niederrhein</span>
                        </div>
                    </div>
                    <div class="xsmall-24 small-24 medium-24 large-24 xlarge-14 columns">
                        <div class="footer-menu-entries" role="navigation" aria-label="Navigation Footer">
                            <a href="/impressum" title="Inhaltlich und technisch Verantwortliche, Nutzungsbedingungen, Haftungsausschluss" class="icon" target="_blank"><span class="text">Impressum</span></a>
                            <a href="/datenschutzbestimmung" title="Unsere Verpflichtung zum Umgang mit persönlichen Informationen" class="icon" target="_blank"><span class="text">Datenschutz</span></a>
                            <a href="/barrierefreiheit" title="Barrierefreiheit" class="icon" target="_blank"><span class="text">Barrierefreiheit</span></a>
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
