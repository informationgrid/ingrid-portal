<%--
  **************************************************-
  InGrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
        <meta http-equiv="Content-style-type" content="text/css" />
        <meta http-equiv="content-language" content="de" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
        <meta name="description" content="InGrid-Portal bietet kostenlosen und werbefreien Zugang zu Informationen &ouml;ffentlicher Institutionen und Organisationen. " />
        <meta name="author" content="wemove digital solutions" />
        <meta name="keywords" lang="de" content="InGrid-Portal, Umweltportal, Umweltinformationen, Deutschland, Bund, Bundesl&auml;nder, L&auml;nder, &ouml;ffentliche Institutionen, &ouml;ffentliche Organisationen, Suche, Recherche, werbefrei, kostenlos, Umweltdatenkataloge, Umwelt, UDK, Datenkataloge, Datenbanken" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
        <meta name="robots" content="index,follow" />
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto+Condensed:400,700"></link>
        <link rel="shortcut icon" href="/decorations/layout/ingrid/images/favicon.ico " />
        <link rel="stylesheet" type="text/css" media="screen, projection" href="/decorations/layout/ingrid/css/style.css" />
        <link rel="stylesheet" type="text/css" media="screen, projection" href="/decorations/layout/ingrid/css/override.css" />
        <script language="JavaScript" src="/decorations/layout/ingrid/scripts/ingrid.js" type="text/javascript"></script>
    </head>
    <body>
        <div class="nav-overlay">
            <a title="Login" class="nav-overlay__item nav-overlay__item--level1" href="/personalisierungsoptionen">Login</a>
            <ul class="nav__mobile nav__foot">
                <li>
                    <a title="Erläuterungen zur Nutzung von InGrid-Portal" href="/hilfe">Hilfe</a>
                </li>
                <li>
                    <a title="Ihre Nachricht, Fragen oder Anregungen direkt an InGrid-Portal" href="/kontakt">Kontakt</a>
                </li>
                <li>
                    <a title="Alle Inhalte von InGrid-Portal auf einen Blick" href="/inhaltsverzeichnis">Sitemap</a>
                </li>
                <li>
                    <a title="Inhaltlich und technisch Verantwortliche, Nutzungsbedingungen, Haftungsausschluss" href="/impressum">Impressum</a>
                </li>
                <li>
                    <a title="Unsere Verpflichtung zum Umgang mit pers&ouml;nlichen Informationen" href="/datenschutzbestimmung">Datenschutz</a>
                </li>
            </ul>
        </div>

        <div class="page">
            <header class="header">
                <div class="ob-box-padded-more ob-box-center">
                    <div class="header__widgets__section">
                        <div class="header__widget nav-toggle mq-show-xxl js-nav-mobile-toggle">
                            <svg class="icon">
                                <use xlink:href="#burger"></use>
                            </svg>
                            <svg class="icon ob-fade">
                                <use xlink:href="#cross"></use>
                            </svg>
                        </div>
                    </div>
                    <div class="header__widgets__section">
                        <a class="header__widget header__logo" href="/startseite" title="Startseite von InGrid-Portal">
                            <img class="mq-hide-xxl desktop-logo" src="/decorations/layout/ingrid/images/template/logo.svg"></img>
                            <img class="mq-show-xxl mobile__logo" src="/decorations/layout/ingrid/images/template/mobile-logo.svg"></img>
                        </a>
                    </div>
                </div> 
                <nav class="nav-desktop mq-hide-xxl">
                    <ul class="nav-desktop__list ob-box-wide ob-box-padded ob-box-center">
                        <li class="nav-desktop__item">
                          <a class="nav-desktop__title" href="/personalisierungsoptionen" title="Login">Login</a>
                        </li>
                    </ul>
                </nav>
            </header>
            <!-- CONTENT BLOCK -->
            <section class="block block--padded">
                <div class="ob-box-wide ob-box-padded ob-box-center">
                    <article class="content ob-container">
                        <h1><fmt:message key="ui.entry.session.expired" /></h1>
                        <p><fmt:message key="ui.entry.session.expired.text" /></p>
                        <br />
                    </article>
                </div>
            </section>
            <footer class="footer">
                <div class="ob-box-padded ob-box-center ob-clear">
                    <div class="footer__top">
                        <a href="/startseite">
                            <!--<img class="footer__logo" src="/decorations/layout/ingrid/images/template/mobile-logo.png"></img>-->
                        </a>
                        <p class="copyright">© Niedersächsisches Ministerium für Umwelt, Energie, Bauen und Klimaschutz</p>
                    </div>
                </div>
                <hr class="bx-top-0 bx-bot-0"></hr>
                <div class="grid">
                    <div class="column column--3-4-xl">
                        <div class="ob-box-padded ob-box-center ob-clear">
                            <div class="footer__section">
                                <nav class="footer__middle mq-hide-l">
                                    <a title="Erläuterungen zur Nutzung von InGrid-Portal" href="/hilfe">Hilfe</a>
                                    <a title="Ihre Nachricht, Fragen oder Anregungen direkt an InGrid-Portal" href="/kontakt">Kontakt</a>
                                    <a title="Alle Inhalte von InGrid-Portal auf einen Blick" href="/inhaltsverzeichnis">Sitemap</a>
                                    <a title="Inhaltlich und technisch Verantwortliche, Nutzungsbedingungen, Haftungsausschluss" href="/impressum">Impressum</a>
                                    <a title="Unsere Verpflichtung zum Umgang mit pers&ouml;nlichen Informationen" href="/datenschutzbestimmung">Datenschutz</a>
                                   </nav>
                            </div>
                        </div>
                    </div>
                    <div class="column column--1-4-xl">
                        <hr class="hr-big mq-hide-l mq-show-xxl bx-top-0 bx-bot-0"></hr>
                        <div class="ob-box-padded ob-box-center ob-clear">
                            <div class="footer__section">
                                <div class="footer__bottom"> </div>
                            </div>
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
