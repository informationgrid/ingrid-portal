<%--
  **************************************************-
  InGrid Portal Apps
  ==================================================
  Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
        <title>UVP</title>
        <meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
        <meta http-equiv="Content-style-type" content="text/css" />
        <meta http-equiv="content-language" content="de" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
        <meta name="description" content="UVP bietet kostenlosen und werbefreien Zugang zu Informationen &ouml;ffentlicher Institutionen und Organisationen. " />
        <meta name="author" content="wemove digital solutions" />
        <meta name="keywords" lang="de" content="UVP, Umweltportal, Umweltinformationen, Deutschland, Bund, Bundesl&auml;nder, L&auml;nder, &ouml;ffentliche Institutionen, &ouml;ffentliche Organisationen, Suche, Recherche, werbefrei, kostenlos, Umweltdatenkataloge, Umwelt, UDK, Datenkataloge, Datenbanken" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
        <meta name="robots" content="index,follow" />
        <link rel="shortcut icon" href="#" />
        <link rel="stylesheet" href="/decorations/layout/uvp/css/main.css" />
        <link rel="stylesheet" href="/decorations/layout/ingrid/css/override.css" />
    </head>
    <body>
        <div class="container">
            <header>
                <div class="row">
                    <div class="columns small-7 medium-9 large-7 xlarge-6">
                        <div class="logo">
                            <a href="/startseite" class="hide-for-medium hide-for-xsmall-only">
                                <img src="/decorations/layout/uvp/images/template/logo.svg" alt="UVP"/>
                            </a>
                            <a href="/startseite" class="show-for-medium hide-for-xsmall-only">
                                <img src="/decorations/layout/uvp/images/template/logo-mit-tag.svg" alt="UVP"/>
                            </a>
                            <a href="/startseite" class="show-for-xsmall-only">
                                <img src="/decorations/layout/uvp/images/template/logo-xsmall.svg" alt="UVP"/>
                            </a>
                        </div>
                    </div>
                    <div class="columns xsmall-10 small-11 medium-9 large-7 xlarge-6 nav-tabs">
                        <div class="menu-tab-row">
                            <a class="menu-tab" href="/log-in" title="Login">
                                <div class="link-menu-tab">
                                    <span class="ic-ic-user"></span>
                                    <span>Login</span>
                                </div>
                            </a>
                        </div>
                    </div>
                </div>
            </header>
            <div class="body">
                <div class="banner subpage">
                    <div class="subpage-wrapper" style="background-image: url('/decorations/layout/uvp/images/template/drops-subpage.svg');">
                        <div class="row">
                            <div class="columns dark">
                                <h1><fmt:message key="ui.entry.session.expired" /></h1>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="columns">
                        <div class="form">
                            <p>
                                <fmt:message key="ui.entry.session.expired.text" />
                            </p>
                        </div>
                    </div>
                </div>
            </div>
            <footer>
                <div class="footer">
                    <div class="row">
                        <div class="xsmall-24 large-7 xlarge-6 columns">
                            <div class="footer-menu-entries">
                                <a href="/impressum" title="Inhaltlich und technisch Verantwortliche, Nutzungsbedingungen, Haftungsausschluss" class="entry">
                                    <span class="text">Impressum</span>
                                </a>
                                <a href="/datenschutzbestimmung" title="Unsere Verpflichtung zum Umgang mit persönlichen Informationen" class="entry">
                                    <span class="text">Datenschutz</span>
                                </a>
                            </div>
                        </div>
                        <div class="xsmall-24 large-17 xlarge-18 columns">
                            <div class="copyright">
                                <p>© 2020 Landesbetrieb Geoinformation und Vermessung</p>
                            </div>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    </body>
</html>
