-- phpMyAdmin SQL Dump
-- version 3.3.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Erstellungszeit: 28. Mai 2018 um 14:43 (Daten bereinigt)
-- Server Version: 5.5.8
-- PHP-Version: 5.3.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Datenbank: `ingrid_portal`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `admin_activity`
--

DROP TABLE IF EXISTS `admin_activity`;
CREATE TABLE IF NOT EXISTS `admin_activity` (
  `ACTIVITY` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `CATEGORY` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ADMIN` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_NAME` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TIME_STAMP` datetime DEFAULT NULL,
  `IPADDRESS` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ATTR_NAME` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ATTR_VALUE_BEFORE` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ATTR_VALUE_AFTER` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DESCRIPTION` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `admin_activity`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `capability`
--

DROP TABLE IF EXISTS `capability`;
CREATE TABLE IF NOT EXISTS `capability` (
  `CAPABILITY_ID` int(11) NOT NULL,
  `CAPABILITY` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CAPABILITY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `capability`
--

INSERT INTO `capability` (`CAPABILITY_ID`, `CAPABILITY`) VALUES
(1, 'HTML_3_2'),
(2, 'HTML_4_0'),
(3, 'HTML_ACTIVEX'),
(4, 'HTML_CSS1'),
(5, 'HTML_CSS2'),
(6, 'HTML_CSSP'),
(7, 'HTML_DOM'),
(8, 'HTML_DOM_1'),
(9, 'HTML_DOM_2'),
(10, 'HTML_DOM_IE'),
(11, 'HTML_DOM_NS4'),
(12, 'HTML_FORM'),
(13, 'HTML_FRAME'),
(14, 'HTML_IFRAME'),
(15, 'HTML_IMAGE'),
(16, 'HTML_JAVA'),
(17, 'HTML_JAVA1_0'),
(18, 'HTML_JAVA1_1'),
(19, 'HTML_JAVA1_2'),
(20, 'HTML_JAVASCRIPT'),
(21, 'HTML_JAVASCRIPT_1_0'),
(22, 'HTML_JAVASCRIPT_1_1'),
(23, 'HTML_JAVASCRIPT_1_2'),
(24, 'HTML_JAVA_JRE'),
(25, 'HTML_JSCRIPT'),
(26, 'HTML_JSCRIPT1_0'),
(27, 'HTML_JSCRIPT1_1'),
(28, 'HTML_JSCRIPT1_2'),
(29, 'HTML_LAYER'),
(30, 'HTML_NESTED_TABLE'),
(31, 'HTML_PLUGIN'),
(32, 'HTML_PLUGIN_'),
(33, 'HTML_TABLE'),
(34, 'HTML_XML'),
(35, 'HTML_XSL'),
(36, 'HTTP_1_1'),
(37, 'HTTP_COOKIE'),
(38, 'WML_1_0'),
(39, 'WML_1_1'),
(40, 'WML_TABLE'),
(41, 'XML_XINCLUDE'),
(42, 'XML_XPATH'),
(43, 'XML_XSLT');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `client`
--

DROP TABLE IF EXISTS `client`;
CREATE TABLE IF NOT EXISTS `client` (
  `CLIENT_ID` int(11) NOT NULL,
  `EVAL_ORDER` int(11) NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `USER_AGENT_PATTERN` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `MANUFACTURER` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `MODEL` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VERSION` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFERRED_MIMETYPE_ID` int(11) NOT NULL,
  PRIMARY KEY (`CLIENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `client`
--

INSERT INTO `client` (`CLIENT_ID`, `EVAL_ORDER`, `NAME`, `USER_AGENT_PATTERN`, `MANUFACTURER`, `MODEL`, `VERSION`, `PREFERRED_MIMETYPE_ID`) VALUES
(1, 1, 'ie5mac', '.*MSIE 5.*Mac.*', 'Microsoft', 'None', '5.*', 2),
(2, 2, 'safari', '.*Mac.*Safari.*', 'Apple', 'None', '5.*', 2),
(3, 3, 'ie6', '.*MSIE 6.*', 'Microsoft', 'None', '6.0', 2),
(4, 4, 'ie5', '.*MSIE 5.*', 'Microsoft', 'None', '5.5', 2),
(5, 5, 'ns4', '.*Mozilla/4.*', 'Netscape', 'None', '4.75', 2),
(6, 6, 'mozilla', '.*Mozilla/5.*', 'Mozilla', 'Mozilla', '1.x', 2),
(7, 7, 'lynx', 'Lynx.*', 'GNU', 'None', '', 2),
(8, 8, 'nokia_generic', 'Nokia.*', 'Nokia', 'Generic', '', 3),
(9, 9, 'xhtml-basic', 'DoCoMo/2.0.*|KDDI-.*UP.Browser.*|J-PHONE/5.0.*|Vodafone/1.0/.*', 'WAP', 'Generic', '', 1),
(10, 10, 'up', 'UP.*|.*UP.Browser.*', 'United Planet', 'Generic', '', 3),
(11, 11, 'sonyericsson', 'Ercis.*|SonyE.*', 'SonyEricsson', 'Generic', '', 3),
(12, 12, 'wapalizer', 'Wapalizer.*', 'Wapalizer', 'Generic', '', 3),
(13, 13, 'klondike', 'Klondike.*', 'Klondike', 'Generic', '', 3),
(14, 14, 'wml_generic', '.*WML.*|.*WAP.*|.*Wap.*|.*wml.*', 'Generic', 'Generic', '', 3),
(15, 15, 'vxml_generic', '.*VoiceXML.*', 'Generic', 'Generic', '', 4),
(16, 16, 'nuance', 'Nuance.*', 'Nuance', 'Generic', '', 4),
(17, 17, 'agentxml', 'agentxml/1.0.*', 'Unknown', 'Generic', '', 6),
(18, 18, 'opera7', '.*Opera/7.*', 'Opera', 'Opera7', '7.x', 2);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `client_to_capability`
--

DROP TABLE IF EXISTS `client_to_capability`;
CREATE TABLE IF NOT EXISTS `client_to_capability` (
  `CLIENT_ID` int(11) NOT NULL,
  `CAPABILITY_ID` int(11) NOT NULL,
  PRIMARY KEY (`CLIENT_ID`,`CAPABILITY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `client_to_capability`
--

INSERT INTO `client_to_capability` (`CLIENT_ID`, `CAPABILITY_ID`) VALUES
(1, 1),
(1, 4),
(1, 11),
(1, 12),
(1, 13),
(1, 15),
(1, 16),
(1, 20),
(1, 31),
(1, 33),
(1, 37),
(2, 1),
(2, 3),
(2, 4),
(2, 5),
(2, 6),
(2, 10),
(2, 12),
(2, 13),
(2, 14),
(2, 15),
(2, 16),
(2, 20),
(2, 30),
(2, 33),
(2, 37),
(3, 1),
(3, 3),
(3, 4),
(3, 5),
(3, 6),
(3, 10),
(3, 12),
(3, 13),
(3, 14),
(3, 15),
(3, 16),
(3, 20),
(3, 30),
(3, 33),
(3, 37),
(4, 1),
(4, 3),
(4, 4),
(4, 5),
(4, 6),
(4, 10),
(4, 12),
(4, 13),
(4, 14),
(4, 15),
(4, 16),
(4, 20),
(4, 30),
(4, 33),
(4, 37),
(5, 1),
(5, 4),
(5, 11),
(5, 12),
(5, 13),
(5, 15),
(5, 16),
(5, 20),
(5, 29),
(5, 31),
(5, 33),
(5, 37),
(6, 1),
(6, 2),
(6, 4),
(6, 5),
(6, 6),
(6, 8),
(6, 12),
(6, 13),
(6, 14),
(6, 15),
(6, 16),
(6, 20),
(6, 24),
(6, 30),
(6, 31),
(6, 33),
(6, 37),
(7, 12),
(7, 13),
(7, 30),
(7, 33),
(7, 37),
(18, 1),
(18, 2),
(18, 4),
(18, 5),
(18, 6),
(18, 8),
(18, 12),
(18, 13),
(18, 14),
(18, 15),
(18, 16),
(18, 20),
(18, 24),
(18, 30),
(18, 31),
(18, 33),
(18, 37);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `client_to_mimetype`
--

DROP TABLE IF EXISTS `client_to_mimetype`;
CREATE TABLE IF NOT EXISTS `client_to_mimetype` (
  `CLIENT_ID` int(11) NOT NULL,
  `MIMETYPE_ID` int(11) NOT NULL,
  PRIMARY KEY (`CLIENT_ID`,`MIMETYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `client_to_mimetype`
--

INSERT INTO `client_to_mimetype` (`CLIENT_ID`, `MIMETYPE_ID`) VALUES
(1, 2),
(2, 2),
(2, 5),
(2, 6),
(3, 2),
(3, 5),
(3, 6),
(4, 2),
(4, 6),
(5, 2),
(6, 2),
(6, 5),
(6, 6),
(7, 2),
(8, 3),
(9, 1),
(10, 3),
(11, 3),
(12, 3),
(13, 3),
(14, 3),
(15, 4),
(16, 4),
(17, 6),
(18, 2),
(18, 5),
(18, 6);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `clubs`
--

DROP TABLE IF EXISTS `clubs`;
CREATE TABLE IF NOT EXISTS `clubs` (
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `COUNTRY` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `CITY` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `STADIUM` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `CAPACITY` int(11) DEFAULT NULL,
  `FOUNDED` int(11) DEFAULT NULL,
  `PITCH` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `NICKNAME` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `clubs`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `custom_portlet_mode`
--

DROP TABLE IF EXISTS `custom_portlet_mode`;
CREATE TABLE IF NOT EXISTS `custom_portlet_mode` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `CUSTOM_NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `MAPPED_NAME` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PORTAL_MANAGED` smallint(6) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `custom_portlet_mode`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `custom_window_state`
--

DROP TABLE IF EXISTS `custom_window_state`;
CREATE TABLE IF NOT EXISTS `custom_window_state` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `CUSTOM_NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `MAPPED_NAME` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `custom_window_state`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `event_alias`
--

DROP TABLE IF EXISTS `event_alias`;
CREATE TABLE IF NOT EXISTS `event_alias` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `event_alias`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `event_definition`
--

DROP TABLE IF EXISTS `event_definition`;
CREATE TABLE IF NOT EXISTS `event_definition` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE_TYPE` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `event_definition`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `filtered_portlet`
--

DROP TABLE IF EXISTS `filtered_portlet`;
CREATE TABLE IF NOT EXISTS `filtered_portlet` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `filtered_portlet`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `filter_lifecycle`
--

DROP TABLE IF EXISTS `filter_lifecycle`;
CREATE TABLE IF NOT EXISTS `filter_lifecycle` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `filter_lifecycle`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `filter_mapping`
--

DROP TABLE IF EXISTS `filter_mapping`;
CREATE TABLE IF NOT EXISTS `filter_mapping` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `FILTER_NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `filter_mapping`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `folder`
--

DROP TABLE IF EXISTS `folder`;
CREATE TABLE IF NOT EXISTS `folder` (
  `FOLDER_ID` int(11) NOT NULL,
  `PARENT_ID` int(11) DEFAULT NULL,
  `PATH` varchar(240) COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SHORT_TITLE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `IS_HIDDEN` smallint(6) NOT NULL,
  `SKIN` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DEFAULT_LAYOUT_DECORATOR` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DEFAULT_PORTLET_DECORATOR` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DEFAULT_PAGE_NAME` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SUBSITE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ROLE_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `GROUP_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `MEDIATYPE` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `EXT_ATTR_NAME` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `EXT_ATTR_VALUE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `OWNER_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`FOLDER_ID`),
  UNIQUE KEY `UN_FOLDER_1` (`PATH`),
  KEY `IX_FOLDER_1` (`PARENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `folder`
--

INSERT INTO `folder` (`FOLDER_ID`, `PARENT_ID`, `PATH`, `NAME`, `TITLE`, `SHORT_TITLE`, `IS_HIDDEN`, `SKIN`, `DEFAULT_LAYOUT_DECORATOR`, `DEFAULT_PORTLET_DECORATOR`, `DEFAULT_PAGE_NAME`, `SUBSITE`, `USER_PRINCIPAL`, `ROLE_PRINCIPAL`, `GROUP_PRINCIPAL`, `MEDIATYPE`, `LOCALE`, `EXT_ATTR_NAME`, `EXT_ATTR_VALUE`, `OWNER_PRINCIPAL`) VALUES
(1, NULL, '/', '/', 'Root Folder', 'Root Folder', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 1, '/_role', '_role', 'Role', 'Role', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(3, 2, '/_role/user', 'user', 'User', 'User', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, NULL),
(4, 1, '/_user', '_user', 'User', 'User', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(701, 4, '/_user/template', 'template', 'Template', 'Template', 0, NULL, NULL, NULL, NULL, NULL, 'template', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(771, 1, '/administration', 'administration', 'ingrid.page.administration', 'ingrid.page.administration', 0, NULL, NULL, NULL, 'admin-cms.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(772, 1, '/application', 'application', 'ingrid.page.application', 'ingrid.page.application', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(773, 1, '/cms', 'cms', 'ingrid.page.cms', 'ingrid.page.cms', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(774, 1, '/mdek', 'mdek', 'ingrid.page.mdek', 'ingrid.page.mdek', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(775, 1, '/search-catalog', 'search-catalog', 'Search Catalog', 'Search Catalog', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(776, 1, '/search-extended', 'search-extended', 'Search Extended', 'Search Extended', 0, NULL, NULL, NULL, 'search-ext-env-topic-terms.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `folder_constraint`
--

DROP TABLE IF EXISTS `folder_constraint`;
CREATE TABLE IF NOT EXISTS `folder_constraint` (
  `CONSTRAINT_ID` int(11) NOT NULL,
  `FOLDER_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `USER_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ROLE_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `GROUP_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PERMISSIONS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`CONSTRAINT_ID`),
  KEY `IX_FOLDER_CONSTRAINT_1` (`FOLDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `folder_constraint`
--

INSERT INTO `folder_constraint` (`CONSTRAINT_ID`, `FOLDER_ID`, `APPLY_ORDER`, `USER_PRINCIPALS_ACL`, `ROLE_PRINCIPALS_ACL`, `GROUP_PRINCIPALS_ACL`, `PERMISSIONS_ACL`) VALUES
(1, 774, 0, NULL, 'mdek', NULL, 'view');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `folder_constraints_ref`
--

DROP TABLE IF EXISTS `folder_constraints_ref`;
CREATE TABLE IF NOT EXISTS `folder_constraints_ref` (
  `CONSTRAINTS_REF_ID` int(11) NOT NULL,
  `FOLDER_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `UN_FOLDER_CONSTRAINTS_REF_1` (`FOLDER_ID`,`NAME`),
  KEY `IX_FOLDER_CONSTRAINTS_REF_1` (`FOLDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `folder_constraints_ref`
--

INSERT INTO `folder_constraints_ref` (`CONSTRAINTS_REF_ID`, `FOLDER_ID`, `APPLY_ORDER`, `NAME`) VALUES
(1, 1, 0, 'public-view'),
(2, 771, 0, 'admin'),
(3, 771, 1, 'admin-portal'),
(4, 771, 2, 'admin-partner'),
(5, 771, 3, 'admin-provider'),
(6, 774, 0, 'admin-portal');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `folder_menu`
--

DROP TABLE IF EXISTS `folder_menu`;
CREATE TABLE IF NOT EXISTS `folder_menu` (
  `MENU_ID` int(11) NOT NULL,
  `CLASS_NAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `PARENT_ID` int(11) DEFAULT NULL,
  `FOLDER_ID` int(11) DEFAULT NULL,
  `ELEMENT_ORDER` int(11) DEFAULT NULL,
  `NAME` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SHORT_TITLE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TEXT` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `OPTIONS` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DEPTH` int(11) DEFAULT NULL,
  `IS_PATHS` smallint(6) DEFAULT NULL,
  `IS_REGEXP` smallint(6) DEFAULT NULL,
  `PROFILE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `OPTIONS_ORDER` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SKIN` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `IS_NEST` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`MENU_ID`),
  KEY `IX_FOLDER_MENU_1` (`PARENT_ID`),
  KEY `IX_FOLDER_MENU_2` (`FOLDER_ID`),
  KEY `UN_FOLDER_MENU_1` (`FOLDER_ID`,`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `folder_menu`
--

INSERT INTO `folder_menu` (`MENU_ID`, `CLASS_NAME`, `PARENT_ID`, `FOLDER_ID`, `ELEMENT_ORDER`, `NAME`, `TITLE`, `SHORT_TITLE`, `TEXT`, `OPTIONS`, `DEPTH`, `IS_PATHS`, `IS_REGEXP`, `PROFILE`, `OPTIONS_ORDER`, `SKIN`, `IS_NEST`) VALUES
(1, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'footer-menu', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL),
(2, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 1, NULL, 0, NULL, NULL, NULL, NULL, '/disclaimer.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(3, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 1, NULL, 1, NULL, NULL, NULL, 'separator1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(4, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 1, NULL, 2, NULL, NULL, NULL, NULL, '/privacy.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(5, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 1, NULL, 3, NULL, NULL, NULL, 'separator1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(6, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 1, NULL, 4, NULL, NULL, NULL, NULL, '/webmaster.link', 0, 0, 0, NULL, NULL, NULL, NULL),
(7, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 1, NULL, 5, NULL, NULL, NULL, 'separator1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(8, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 1, NULL, 6, NULL, NULL, NULL, NULL, '/service-contact-newsletter.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(9, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'main-menu', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL),
(10, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 0, NULL, NULL, NULL, NULL, '/main-search.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(11, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 1, NULL, NULL, NULL, NULL, '/main-environment.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(12, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 2, NULL, NULL, NULL, NULL, '/main-measures.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(13, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 3, NULL, NULL, NULL, NULL, '/main-service.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(14, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 4, NULL, NULL, NULL, NULL, '/search-catalog/search-catalog-hierarchy.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(15, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 5, NULL, NULL, NULL, NULL, '/main-maps.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(16, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 6, NULL, NULL, NULL, NULL, '/main-chronicle.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(17, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 7, NULL, NULL, NULL, NULL, '/main-features.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(18, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 8, NULL, NULL, NULL, NULL, '/application/main-application.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(19, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 9, NULL, NULL, NULL, NULL, '/administration', 0, 0, 0, NULL, NULL, NULL, NULL),
(20, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 10, NULL, NULL, NULL, NULL, '/Administrative', 0, 0, 0, NULL, NULL, NULL, NULL),
(21, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 9, NULL, 11, NULL, NULL, NULL, NULL, '/mdek', 0, 0, 0, NULL, NULL, NULL, NULL),
(22, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'service-menu', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL),
(23, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 22, NULL, 0, NULL, NULL, NULL, NULL, '/default-page.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(24, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 22, NULL, 1, NULL, NULL, NULL, 'separator1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(25, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 22, NULL, 2, NULL, NULL, NULL, NULL, '/service-myportal.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(26, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 22, NULL, 3, NULL, NULL, NULL, 'separator2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(27, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 22, NULL, 4, NULL, NULL, NULL, NULL, '/service-sitemap.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(28, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 22, NULL, 5, NULL, NULL, NULL, 'separator2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(29, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 22, NULL, 6, NULL, NULL, NULL, NULL, '/help.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(30, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 22, NULL, 7, NULL, NULL, NULL, 'separator2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(31, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 22, NULL, 8, NULL, NULL, NULL, NULL, '/service-contact.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(32, 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', 22, NULL, 9, NULL, NULL, NULL, 'separator1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(33, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 22, NULL, 10, NULL, NULL, NULL, NULL, '/language.link', 0, 0, 0, NULL, NULL, NULL, NULL),
(34, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'sub-menu-about', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL),
(35, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 34, NULL, 0, NULL, NULL, NULL, NULL, '/main-features.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(36, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 34, NULL, 1, NULL, NULL, NULL, NULL, '/main-about.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(37, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 34, NULL, 2, NULL, NULL, NULL, NULL, '/main-about-partner.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(38, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 34, NULL, 3, NULL, NULL, NULL, NULL, '/main-about-data-source.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(39, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'sub-menu-application', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL),
(40, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 39, NULL, 0, NULL, NULL, NULL, NULL, '/application/main-application.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(41, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 39, NULL, 1, NULL, NULL, NULL, NULL, '/application/main-application-0.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(42, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 39, NULL, 2, NULL, NULL, NULL, NULL, '/application/main-application-1.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(43, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 39, NULL, 3, NULL, NULL, NULL, NULL, '/application/main-application-2.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(44, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'sub-menu-catalog', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL),
(45, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 44, NULL, 0, NULL, NULL, NULL, NULL, '/search-catalog/search-catalog-hierarchy.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(46, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 44, NULL, 1, NULL, NULL, NULL, NULL, '/search-catalog/search-catalog-thesaurus.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(47, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'sub-menu-search', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL),
(48, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 1, 0, 'sub-menu-start', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL),
(49, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 48, NULL, 0, NULL, NULL, NULL, NULL, '/default-page.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(50, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 48, NULL, 1, NULL, NULL, NULL, NULL, '/cms/cms-0.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(51, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 48, NULL, 2, NULL, NULL, NULL, NULL, '/cms/cms-1.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(52, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 48, NULL, 3, NULL, NULL, NULL, NULL, '/cms/cms-2.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(53, 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 771, 0, 'sub-menu', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL),
(54, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 0, NULL, NULL, NULL, NULL, '/administration/admin-usermanagement.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(55, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 1, NULL, NULL, NULL, NULL, '/administration/admin-homepage.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(56, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 2, NULL, NULL, NULL, NULL, '/administration/admin-content-rss.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(57, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 3, NULL, NULL, NULL, NULL, '/administration/admin-content-partner.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(58, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 4, NULL, NULL, NULL, NULL, '/administration/admin-content-provider.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(59, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 5, NULL, NULL, NULL, NULL, '/administration/admin-iplugs.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(60, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 6, NULL, NULL, NULL, NULL, '/administration/admin-cms.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(61, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 7, NULL, NULL, NULL, NULL, '/administration/admin-wms.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(62, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 8, NULL, NULL, NULL, NULL, '/administration/admin-statistics.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(63, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 9, NULL, NULL, NULL, NULL, '/administration/admin-portal-profile.psml', 0, 0, 0, NULL, NULL, NULL, NULL),
(64, 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 53, NULL, 10, NULL, NULL, NULL, NULL, '/administration/admin-component-monitor.psml', 0, 0, 0, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `folder_menu_metadata`
--

DROP TABLE IF EXISTS `folder_menu_metadata`;
CREATE TABLE IF NOT EXISTS `folder_menu_metadata` (
  `METADATA_ID` int(11) NOT NULL,
  `MENU_ID` int(11) NOT NULL,
  `NAME` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`METADATA_ID`),
  UNIQUE KEY `UN_FOLDER_MENU_METADATA_1` (`MENU_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_FOLDER_MENU_METADATA_1` (`MENU_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `folder_menu_metadata`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `folder_metadata`
--

DROP TABLE IF EXISTS `folder_metadata`;
CREATE TABLE IF NOT EXISTS `folder_metadata` (
  `METADATA_ID` int(11) NOT NULL,
  `FOLDER_ID` int(11) NOT NULL,
  `NAME` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`METADATA_ID`),
  UNIQUE KEY `UN_FOLDER_METADATA_1` (`FOLDER_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_FOLDER_METADATA_1` (`FOLDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `folder_metadata`
--

INSERT INTO `folder_metadata` (`METADATA_ID`, `FOLDER_ID`, `NAME`, `LOCALE`, `VALUE`) VALUES
(1, 1, 'title', 'es,,', 'Carpeta raiz'),
(2, 1, 'title', 'fr,,', 'Répertoire racine');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `folder_order`
--

DROP TABLE IF EXISTS `folder_order`;
CREATE TABLE IF NOT EXISTS `folder_order` (
  `ORDER_ID` int(11) NOT NULL,
  `FOLDER_ID` int(11) NOT NULL,
  `SORT_ORDER` int(11) NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ORDER_ID`),
  UNIQUE KEY `UN_FOLDER_ORDER_1` (`FOLDER_ID`,`NAME`),
  KEY `IX_FOLDER_ORDER_1` (`FOLDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `folder_order`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `fragment`
--

DROP TABLE IF EXISTS `fragment`;
CREATE TABLE IF NOT EXISTS `fragment` (
  `FRAGMENT_ID` int(11) NOT NULL,
  `CLASS_NAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `PARENT_ID` int(11) DEFAULT NULL,
  `PAGE_ID` int(11) DEFAULT NULL,
  `FRAGMENT_STRING_ID` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `FRAGMENT_STRING_REFID` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `NAME` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SHORT_TITLE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TYPE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SKIN` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DECORATOR` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `STATE` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PMODE` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `LAYOUT_ROW` int(11) DEFAULT NULL,
  `LAYOUT_COLUMN` int(11) DEFAULT NULL,
  `LAYOUT_SIZES` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `LAYOUT_X` float DEFAULT NULL,
  `LAYOUT_Y` float DEFAULT NULL,
  `LAYOUT_Z` float DEFAULT NULL,
  `LAYOUT_WIDTH` float DEFAULT NULL,
  `LAYOUT_HEIGHT` float DEFAULT NULL,
  `OWNER_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`FRAGMENT_ID`),
  KEY `IX_FRAGMENT_1` (`PARENT_ID`),
  KEY `UN_FRAGMENT_1` (`PAGE_ID`),
  KEY `IX_FRAGMENT_2` (`FRAGMENT_STRING_REFID`),
  KEY `IX_FRAGMENT_3` (`FRAGMENT_STRING_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `fragment`
--

INSERT INTO `fragment` (`FRAGMENT_ID`, `CLASS_NAME`, `PARENT_ID`, `PAGE_ID`, `FRAGMENT_STRING_ID`, `FRAGMENT_STRING_REFID`, `NAME`, `TITLE`, `SHORT_TITLE`, `TYPE`, `SKIN`, `DECORATOR`, `STATE`, `PMODE`, `LAYOUT_ROW`, `LAYOUT_COLUMN`, `LAYOUT_SIZES`, `LAYOUT_X`, `LAYOUT_Y`, `LAYOUT_Z`, `LAYOUT_WIDTH`, `LAYOUT_HEIGHT`, `OWNER_PRINCIPAL`) VALUES
(1, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 1, '1', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(2, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9701', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(3, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9702', NULL, 'ingrid-portal-apps::EnvironmentTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(4, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9703', NULL, 'ingrid-portal-apps::RssNewsTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 2, 0, NULL, -1, -1, -1, -1, -1, NULL),
(5, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9704', NULL, 'ingrid-portal-apps::IngridInformPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(6, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9706', NULL, 'ingrid-portal-apps::MeasuresTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 3, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9707', NULL, 'ingrid-portal-apps::ChronicleTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 2, 1, NULL, -1, -1, -1, -1, -1, NULL),
(8, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9708', NULL, 'ingrid-portal-apps::WeatherTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 1, 1, NULL, -1, -1, -1, -1, -1, NULL),
(9, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 1, NULL, '9931', NULL, 'ingrid-portal-apps::ServiceTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 4, 1, NULL, -1, -1, -1, -1, -1, NULL),
(11, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 2, '9', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(12, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 11, NULL, '10', NULL, 'ingrid-portal-apps::DetectJavaScriptPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(14, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 3, '11', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(15, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 14, NULL, '12', NULL, 'ingrid-portal-apps::CMSPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(16, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 14, NULL, '13', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(18, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 4, '14', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(19, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 18, NULL, '15', NULL, 'ingrid-portal-apps::HelpPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(21, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 5, '11060', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(22, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 21, NULL, '11061', NULL, 'ingrid-portal-apps::ShowDataSourcePortlet', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(24, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 6, '16', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(25, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 24, NULL, '17', NULL, 'ingrid-portal-apps::ShowPartnerPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(27, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 7, '18', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(28, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 27, NULL, '19', NULL, 'ingrid-portal-apps::CMSPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(29, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 27, NULL, '20', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(30, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 27, NULL, '21', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 1, 1, NULL, -1, -1, -1, -1, -1, NULL),
(32, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 8, '22', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(33, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 32, NULL, '23', NULL, 'ingrid-portal-apps::ChronicleSearch', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(34, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 32, NULL, '24', NULL, 'ingrid-portal-apps::ChronicleResult', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(35, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 32, NULL, '25', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-header', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(37, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 9, '26', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(38, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 37, NULL, '27', NULL, 'ingrid-portal-apps::EnvironmentSearch', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(39, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 37, NULL, '28', NULL, 'ingrid-portal-apps::EnvironmentResult', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(40, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 37, NULL, '29', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-header', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(42, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 10, '9820', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(43, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 42, NULL, '9821', NULL, 'ingrid-portal-apps::ShowFeaturesPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(45, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 11, '30', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(46, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 45, NULL, '31', NULL, 'ingrid-portal-apps::ShowMapsPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(48, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 12, '32', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(49, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 48, NULL, '33', NULL, 'ingrid-portal-apps::MeasuresSearch', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(50, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 48, NULL, '34', NULL, 'ingrid-portal-apps::MeasuresResult', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(51, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 48, NULL, '35', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-header', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(53, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 13, '36', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(54, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 53, NULL, '37', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(55, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 54, NULL, '38', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(56, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 54, NULL, '39', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-header', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(57, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 54, NULL, '40', NULL, 'ingrid-portal-apps::SearchSimilar', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(58, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 53, NULL, '41', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(59, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 58, NULL, '42', NULL, 'ingrid-portal-apps::SearchResult', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(61, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 14, '43', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(62, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 61, NULL, '44', NULL, 'ingrid-portal-apps::ServiceSearch', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(63, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 61, NULL, '45', NULL, 'ingrid-portal-apps::ServiceResult', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(64, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 61, NULL, '46', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-header', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(66, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 15, '47', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(67, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 66, NULL, '48', NULL, 'ingrid-portal-apps::MyPortalCreateAccountPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(68, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 66, NULL, '49', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(70, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 16, '50', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(71, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 70, NULL, '51', NULL, 'ingrid-portal-apps::MyPortalPasswordForgottenPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(72, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 70, NULL, '52', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(74, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 17, '53', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(75, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 74, NULL, '54', NULL, 'ingrid-portal-apps::CMSPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(77, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 18, '55', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(78, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 77, NULL, '56', NULL, 'ingrid-portal-apps::RssNews', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(80, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 19, '57', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(81, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 80, NULL, '58', NULL, 'ingrid-portal-apps::SearchDetail', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(83, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 20, '59', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(84, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 83, NULL, '60', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(85, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 83, NULL, '61', NULL, 'ingrid-portal-apps::SearchHistory', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(86, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 83, NULL, '62', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(88, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 21, '63', NULL, 'jetspeed-layouts::IngridClearLayout', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(89, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 88, NULL, '64', NULL, 'ingrid-portal-apps::SearchResult', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(91, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 22, '65', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(92, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 91, NULL, '66', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(93, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 91, NULL, '67', NULL, 'ingrid-portal-apps::SearchSavePortlet', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(95, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 23, '68', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(96, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 95, NULL, '69', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(97, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 95, NULL, '70', NULL, 'ingrid-portal-apps::SearchSettings', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(98, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 95, NULL, '71', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(100, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 24, '72', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(101, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 100, NULL, '73', NULL, 'ingrid-portal-apps::ContactNewsletterPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(102, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 100, NULL, '74', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(103, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 100, NULL, '75', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 1, 1, NULL, -1, -1, -1, -1, -1, NULL),
(105, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 25, '76', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(106, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 105, NULL, '9723', NULL, 'ingrid-portal-apps::Contact', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(107, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 105, NULL, '9724', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(109, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 26, '79', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(110, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 109, NULL, '80', NULL, 'ingrid-portal-apps::MyPortalLoginPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(111, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 109, NULL, '81', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(113, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 27, '82', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(114, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 113, NULL, '83', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(116, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 28, '84', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(117, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, '9709', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(118, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, '9710', NULL, 'ingrid-portal-apps::EnvironmentTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(119, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, '9711', NULL, 'ingrid-portal-apps::RssNewsTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 2, 0, NULL, -1, -1, -1, -1, -1, NULL),
(120, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, '9712', NULL, 'ingrid-portal-apps::IngridInformPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(121, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, '9714', NULL, 'ingrid-portal-apps::MeasuresTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 3, 1, NULL, -1, -1, -1, -1, -1, NULL),
(122, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, '9715', NULL, 'ingrid-portal-apps::ChronicleTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 1, 1, NULL, -1, -1, -1, -1, -1, NULL),
(123, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 116, NULL, '9900', NULL, 'ingrid-portal-apps::WeatherTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 2, 1, NULL, -1, -1, -1, -1, -1, NULL),
(125, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 29, '92', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(126, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 125, NULL, '9725', NULL, 'ingrid-portal-apps::MyPortalEditAccountPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(127, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 125, NULL, '9726', NULL, 'ingrid-portal-apps::MyPortalEditAboutInfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(128, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 125, NULL, '9727', NULL, 'ingrid-portal-apps::MyPortalEditNewsletterInfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 1, 1, NULL, -1, -1, -1, -1, -1, NULL),
(129, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 125, NULL, '9728', NULL, 'ingrid-portal-apps::MyPortalEditAdvancedInfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 2, 1, NULL, -1, -1, -1, -1, -1, NULL),
(131, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 30, '97', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(132, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 131, NULL, '98', NULL, 'ingrid-portal-apps::MyPortalPersonalizeOverviewPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(133, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 131, NULL, '99', NULL, 'ingrid-portal-apps::MyPortalPersonalizeHomePortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(134, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 131, NULL, '100', NULL, 'ingrid-portal-apps::MyPortalPersonalizePartnerPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 2, 0, NULL, -1, -1, -1, -1, -1, NULL),
(135, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 131, NULL, '101', NULL, 'ingrid-portal-apps::MyPortalPersonalizeSourcesPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 3, 0, NULL, -1, -1, -1, -1, -1, NULL),
(136, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 131, NULL, '102', NULL, 'ingrid-portal-apps::MyPortalPersonalizeSearchSettingsPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 4, 0, NULL, -1, -1, -1, -1, -1, NULL),
(137, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 131, NULL, '103', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(138, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 131, NULL, '104', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 1, 1, NULL, -1, -1, -1, -1, -1, NULL),
(140, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 31, '105', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(141, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 140, NULL, '106', NULL, 'ingrid-portal-apps::MyPortalOverviewPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(142, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 140, NULL, '107', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(6507, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 728, '108', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(6508, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '9716', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(6509, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '9717', NULL, 'ingrid-portal-apps::EnvironmentTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(6510, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '9718', NULL, 'ingrid-portal-apps::RssNewsTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 2, 0, NULL, -1, -1, -1, -1, -1, NULL),
(6511, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '9719', NULL, 'ingrid-portal-apps::IngridInformPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(6512, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '9721', NULL, 'ingrid-portal-apps::MeasuresTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 3, 1, NULL, -1, -1, -1, -1, -1, NULL),
(6513, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '9722', NULL, 'ingrid-portal-apps::ChronicleTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 1, 1, NULL, -1, -1, -1, -1, -1, NULL),
(6514, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '9901', NULL, 'ingrid-portal-apps::WeatherTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 2, 1, NULL, -1, -1, -1, -1, -1, NULL),
(6515, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 6507, NULL, '11001', NULL, 'ingrid-portal-apps::WeatherTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 2, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7138, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 798, '116', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7139, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7138, NULL, '117', NULL, 'ingrid-portal-apps::AdminCMSPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7141, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 799, '1278', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7142, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7141, NULL, '1279', NULL, 'ingrid-portal-apps::AdminComponentMonitorPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7144, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 800, '118', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7145, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7144, NULL, '119', NULL, 'ingrid-portal-apps::ContentPartnerPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7147, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 801, '120', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7148, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7147, NULL, '121', NULL, 'ingrid-portal-apps::ContentProviderPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7150, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 802, '122', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7151, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7150, NULL, '123', NULL, 'ingrid-portal-apps::ContentRSSPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7153, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 803, '124', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7154, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7153, NULL, '125', NULL, 'ingrid-portal-apps::AdminHomepagePortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7156, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 804, '126', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7157, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7156, NULL, '127', NULL, 'ingrid-portal-apps::AdminIPlugPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7159, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 805, '128', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7160, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7159, NULL, '129', NULL, 'ingrid-portal-apps::AdminPortalProfilePortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7162, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 806, '130', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7163, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7162, NULL, '131', NULL, 'ingrid-portal-apps::AdminStatisticsPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7165, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 807, '132', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7166, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7165, NULL, '133', NULL, 'ingrid-portal-apps::AdminUserPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7167, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7165, NULL, '134', NULL, 'ingrid-portal-apps::AdminUserMigrationPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7169, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 808, '135', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7170, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7169, NULL, '136', NULL, 'ingrid-portal-apps::AdminWMSPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7172, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 809, '11024', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7173, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7172, NULL, '11025', NULL, 'ingrid-portal-apps::IFramePortalPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7175, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 810, '11022', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7176, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7175, NULL, '11023', NULL, 'ingrid-portal-apps::IFramePortalPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7178, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 811, '11026', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7179, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7178, NULL, '11027', NULL, 'ingrid-portal-apps::IFramePortalPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7181, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 812, '11020', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7182, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7181, NULL, '11021', NULL, 'ingrid-portal-apps::IFramePortalPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-teaser', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7184, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 813, '11040', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7185, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7184, NULL, '11041', NULL, 'ingrid-portal-apps::CMSPortlet0', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7187, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 814, '11042', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7188, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7187, NULL, '11043', NULL, 'ingrid-portal-apps::CMSPortlet1', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7190, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 815, '11044', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7191, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7190, NULL, '11045', NULL, 'ingrid-portal-apps::CMSPortlet2', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7193, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 816, '2520', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7194, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7193, NULL, '2521', NULL, 'ingrid-portal-mdek::MdekPortalAdminPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7195, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7193, NULL, '2523', NULL, 'ingrid-portal-mdek::MdekEntryPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 2, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7196, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7193, NULL, '5380', NULL, 'ingrid-portal-mdek::MdekAdminLoginPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7198, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 817, '1780', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7199, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7198, NULL, '1781', NULL, 'ingrid-portal-apps::SearchCatalogHierarchy', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7201, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 818, '1782', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7202, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7201, NULL, '1783', NULL, 'ingrid-portal-apps::SearchCatalogThesaurus', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7203, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7201, NULL, '1980', NULL, 'ingrid-portal-apps::SearchCatalogThesaurusResult', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7204, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7201, NULL, '2000', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 1, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7206, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 819, '137', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7207, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7206, NULL, '138', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7208, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7206, NULL, '139', NULL, 'ingrid-portal-apps::SearchExtAdrAreaPartner', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7209, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7206, NULL, '140', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7211, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 820, '141', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7212, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7211, NULL, '142', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7213, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7211, NULL, '143', NULL, 'ingrid-portal-apps::SearchExtAdrPlaceReference', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7214, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7211, NULL, '144', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7216, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 821, '145', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7217, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7216, NULL, '146', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7218, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7216, NULL, '147', NULL, 'ingrid-portal-apps::SearchExtAdrTopicMode', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7219, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7216, NULL, '148', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7221, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 822, '149', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7222, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7221, NULL, '150', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7223, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7221, NULL, '151', NULL, 'ingrid-portal-apps::SearchExtAdrTopicTerms', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7224, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7221, NULL, '152', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7226, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 823, '153', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7227, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7226, NULL, '154', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7228, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7226, NULL, '155', NULL, 'ingrid-portal-apps::SearchExtEnvAreaContents', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7229, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7226, NULL, '156', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7231, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 824, '157', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7232, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7231, NULL, '158', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7233, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7231, NULL, '159', NULL, 'ingrid-portal-apps::SearchExtEnvAreaPartner', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7234, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7231, NULL, '160', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7236, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 825, '161', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7237, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7236, NULL, '162', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7238, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7236, NULL, '163', NULL, 'ingrid-portal-apps::SearchExtEnvAreaSources', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7239, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7236, NULL, '164', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7241, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 826, '165', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7242, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7241, NULL, '166', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7243, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7241, NULL, '167', NULL, 'ingrid-portal-apps::SearchExtEnvPlaceGeothesaurus', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7244, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7241, NULL, '168', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7246, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 827, '169', NULL, 'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7247, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7246, NULL, '170', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7248, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7247, NULL, '171', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7249, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7246, NULL, '172', NULL, 'ingrid-portal-apps::SearchExtEnvPlaceMap', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7251, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 828, '173', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7252, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7251, NULL, '174', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7253, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7251, NULL, '175', NULL, 'ingrid-portal-apps::SearchExtEnvTimeConstraint', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7254, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7251, NULL, '176', NULL, 'ingrid-portal-apps::SearchExtEnvTimeChronicle', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 2, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7255, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7251, NULL, '177', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7257, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 829, '178', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7258, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7257, NULL, '179', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7259, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7257, NULL, '180', NULL, 'ingrid-portal-apps::SearchExtEnvTopicTerms', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7260, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7257, NULL, '181', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7262, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 830, '182', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7263, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7262, NULL, '183', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7264, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7262, NULL, '184', NULL, 'ingrid-portal-apps::SearchExtEnvTopicThesaurus', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7265, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7262, NULL, '185', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7267, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 831, '2248', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7268, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7267, NULL, '2249', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7269, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7267, NULL, '2250', NULL, 'ingrid-portal-apps::SearchExtLawAreaPartner', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7270, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7267, NULL, '2251', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7272, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 832, '2240', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7273, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7272, NULL, '2241', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7274, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7272, NULL, '2242', NULL, 'ingrid-portal-apps::SearchExtLawTopicTerms', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7275, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7272, NULL, '2243', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7277, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 833, '2244', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7278, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7277, NULL, '2245', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7279, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7277, NULL, '2246', NULL, 'ingrid-portal-apps::SearchExtLawTopicThesaurus', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7280, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7277, NULL, '2247', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7282, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 834, '186', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7283, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7282, NULL, '187', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7284, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7282, NULL, '188', NULL, 'ingrid-portal-apps::SearchExtResTopicAttributes', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7285, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7282, NULL, '189', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL),
(7287, 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL, 835, '190', NULL, 'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL),
(7288, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7287, NULL, '191', NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7289, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7287, NULL, '192', NULL, 'ingrid-portal-apps::SearchExtResTopicTerms', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL),
(7290, 'org.apache.jetspeed.om.page.impl.FragmentImpl', 7287, NULL, '193', NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `fragment_constraint`
--

DROP TABLE IF EXISTS `fragment_constraint`;
CREATE TABLE IF NOT EXISTS `fragment_constraint` (
  `CONSTRAINT_ID` int(11) NOT NULL,
  `FRAGMENT_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `USER_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ROLE_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `GROUP_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PERMISSIONS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`CONSTRAINT_ID`),
  KEY `IX_FRAGMENT_CONSTRAINT_1` (`FRAGMENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `fragment_constraint`
--

INSERT INTO `fragment_constraint` (`CONSTRAINT_ID`, `FRAGMENT_ID`, `APPLY_ORDER`, `USER_PRINCIPALS_ACL`, `ROLE_PRINCIPALS_ACL`, `GROUP_PRINCIPALS_ACL`, `PERMISSIONS_ACL`) VALUES
(1, 7195, 0, NULL, 'mdek', NULL, 'view'),
(2, 7195, 1, 'admin', 'admin-portal', NULL, NULL),
(3, 7195, 2, NULL, 'mdek', NULL, 'view');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `fragment_constraints_ref`
--

DROP TABLE IF EXISTS `fragment_constraints_ref`;
CREATE TABLE IF NOT EXISTS `fragment_constraints_ref` (
  `CONSTRAINTS_REF_ID` int(11) NOT NULL,
  `FRAGMENT_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `UN_FRAGMENT_CONSTRAINTS_REF_1` (`FRAGMENT_ID`,`NAME`),
  KEY `IX_FRAGMENT_CONSTRAINTS_REF_1` (`FRAGMENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `fragment_constraints_ref`
--

INSERT INTO `fragment_constraints_ref` (`CONSTRAINTS_REF_ID`, `FRAGMENT_ID`, `APPLY_ORDER`, `NAME`) VALUES
(1, 7167, 0, 'admin'),
(2, 7194, 0, 'admin-portal'),
(3, 7196, 0, 'admin');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `fragment_pref`
--

DROP TABLE IF EXISTS `fragment_pref`;
CREATE TABLE IF NOT EXISTS `fragment_pref` (
  `PREF_ID` int(11) NOT NULL,
  `FRAGMENT_ID` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `IS_READ_ONLY` smallint(6) NOT NULL,
  PRIMARY KEY (`PREF_ID`),
  UNIQUE KEY `UN_FRAGMENT_PREF_1` (`FRAGMENT_ID`,`NAME`),
  KEY `IX_FRAGMENT_PREF_1` (`FRAGMENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `fragment_pref`
--

INSERT INTO `fragment_pref` (`PREF_ID`, `FRAGMENT_ID`, `NAME`, `IS_READ_ONLY`) VALUES
(1, 8, 'titleKey', 0),
(2, 15, 'cmsKey', 0),
(3, 15, 'infoTemplate', 0),
(4, 15, 'titleKey', 0),
(5, 16, 'infoTemplate', 0),
(6, 16, 'titleKey', 0),
(7, 28, 'cmsKey', 0),
(8, 28, 'helpKey', 0),
(9, 28, 'infoTemplate', 0),
(10, 29, 'infoTemplate', 0),
(11, 29, 'titleKey', 0),
(12, 30, 'infoTemplate', 0),
(13, 30, 'titleKey', 0),
(14, 33, 'helpKey', 0),
(15, 35, 'infoTemplate', 0),
(16, 35, 'titleKey', 0),
(17, 38, 'helpKey', 0),
(18, 40, 'infoLink', 0),
(19, 40, 'infoTemplate', 0),
(20, 40, 'titleKey', 0),
(21, 49, 'helpKey', 0),
(22, 51, 'infoTemplate', 0),
(23, 51, 'titleKey', 0),
(24, 55, 'helpKey', 0),
(25, 55, 'titleKey', 0),
(26, 56, 'infoTemplate', 0),
(27, 56, 'titleKey', 0),
(28, 62, 'helpKey', 0),
(29, 64, 'infoLink', 0),
(30, 64, 'infoTemplate', 0),
(31, 64, 'titleKey', 0),
(32, 68, 'infoTemplate', 0),
(33, 68, 'titleKey', 0),
(34, 72, 'infoTemplate', 0),
(35, 72, 'titleKey', 0),
(36, 75, 'cmsKey', 0),
(37, 75, 'infoTemplate', 0),
(38, 78, 'startWithEntry', 0),
(39, 84, 'helpKey', 0),
(40, 84, 'titleKey', 0),
(41, 86, 'infoTemplate', 0),
(42, 86, 'titleKey', 0),
(43, 92, 'helpKey', 0),
(44, 92, 'titleKey', 0),
(45, 96, 'helpKey', 0),
(46, 96, 'titleKey', 0),
(47, 98, 'infoTemplate', 0),
(48, 98, 'titleKey', 0),
(49, 101, 'helpKey', 0),
(50, 102, 'infoTemplate', 0),
(51, 102, 'titleKey', 0),
(52, 103, 'infoTemplate', 0),
(53, 103, 'titleKey', 0),
(54, 107, 'infoTemplate', 0),
(55, 107, 'titleKey', 0),
(56, 111, 'helpKey', 0),
(57, 111, 'infoTemplate', 0),
(58, 111, 'titleKey', 0),
(59, 114, 'infoTemplate', 0),
(60, 114, 'titleKey', 0),
(61, 123, 'titleKey', 0),
(62, 132, 'titleKey', 0),
(63, 133, 'titleKey', 0),
(64, 134, 'titleKey', 0),
(65, 135, 'titleKey', 0),
(66, 136, 'titleKey', 0),
(67, 137, 'currPage', 0),
(68, 137, 'infoTemplate', 0),
(69, 137, 'titleKey', 0),
(70, 138, 'helpKey', 0),
(71, 138, 'infoTemplate', 0),
(72, 138, 'titleKey', 0),
(73, 142, 'infoTemplate', 0),
(74, 142, 'titleKey', 0),
(6534, 6514, 'titleKey', 0),
(6535, 6515, 'titleKey', 0),
(7219, 7173, 'HEIGHT', 0),
(7220, 7173, 'SRC', 0),
(7221, 7173, 'titleKey', 0),
(7222, 7173, 'WIDTH', 0),
(7223, 7176, 'HEIGHT', 0),
(7224, 7176, 'SRC', 0),
(7225, 7176, 'titleKey', 0),
(7226, 7176, 'WIDTH', 0),
(7227, 7179, 'HEIGHT', 0),
(7228, 7179, 'SRC', 0),
(7229, 7179, 'titleKey', 0),
(7230, 7179, 'WIDTH', 0),
(7231, 7204, 'infoTemplate', 0),
(7232, 7204, 'titleKey', 0),
(7233, 7207, 'helpKey', 0),
(7234, 7207, 'titleKey', 0),
(7235, 7209, 'infoTemplate', 0),
(7236, 7209, 'titleKey', 0),
(7237, 7212, 'helpKey', 0),
(7238, 7212, 'titleKey', 0),
(7239, 7214, 'infoTemplate', 0),
(7240, 7214, 'titleKey', 0),
(7241, 7217, 'helpKey', 0),
(7242, 7217, 'titleKey', 0),
(7243, 7219, 'infoTemplate', 0),
(7244, 7219, 'titleKey', 0),
(7245, 7222, 'helpKey', 0),
(7246, 7222, 'titleKey', 0),
(7247, 7224, 'infoTemplate', 0),
(7248, 7224, 'titleKey', 0),
(7249, 7227, 'helpKey', 0),
(7250, 7227, 'titleKey', 0),
(7251, 7229, 'infoTemplate', 0),
(7252, 7229, 'titleKey', 0),
(7253, 7232, 'helpKey', 0),
(7254, 7232, 'titleKey', 0),
(7255, 7234, 'infoTemplate', 0),
(7256, 7234, 'titleKey', 0),
(7257, 7237, 'helpKey', 0),
(7258, 7237, 'titleKey', 0),
(7259, 7239, 'infoTemplate', 0),
(7260, 7239, 'titleKey', 0),
(7261, 7242, 'helpKey', 0),
(7262, 7242, 'titleKey', 0),
(7263, 7244, 'infoTemplate', 0),
(7264, 7244, 'titleKey', 0),
(7265, 7248, 'helpKey', 0),
(7266, 7248, 'titleKey', 0),
(7267, 7252, 'helpKey', 0),
(7268, 7252, 'titleKey', 0),
(7269, 7255, 'infoTemplate', 0),
(7270, 7255, 'titleKey', 0),
(7271, 7258, 'helpKey', 0),
(7272, 7258, 'titleKey', 0),
(7273, 7260, 'infoTemplate', 0),
(7274, 7260, 'titleKey', 0),
(7275, 7263, 'helpKey', 0),
(7276, 7263, 'titleKey', 0),
(7277, 7265, 'infoTemplate', 0),
(7278, 7265, 'titleKey', 0),
(7279, 7268, 'helpKey', 0),
(7280, 7268, 'titleKey', 0),
(7281, 7270, 'infoTemplate', 0),
(7282, 7270, 'titleKey', 0),
(7283, 7273, 'helpKey', 0),
(7284, 7273, 'titleKey', 0),
(7285, 7275, 'infoTemplate', 0),
(7286, 7275, 'titleKey', 0),
(7287, 7278, 'helpKey', 0),
(7288, 7278, 'titleKey', 0),
(7289, 7280, 'infoTemplate', 0),
(7290, 7280, 'titleKey', 0),
(7291, 7283, 'helpKey', 0),
(7292, 7283, 'titleKey', 0),
(7293, 7285, 'infoTemplate', 0),
(7294, 7285, 'titleKey', 0),
(7295, 7288, 'helpKey', 0),
(7296, 7288, 'titleKey', 0),
(7297, 7290, 'infoTemplate', 0),
(7298, 7290, 'titleKey', 0);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `fragment_pref_value`
--

DROP TABLE IF EXISTS `fragment_pref_value`;
CREATE TABLE IF NOT EXISTS `fragment_pref_value` (
  `PREF_VALUE_ID` int(11) NOT NULL,
  `PREF_ID` int(11) NOT NULL,
  `VALUE_ORDER` int(11) NOT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`PREF_VALUE_ID`),
  KEY `IX_FRAGMENT_PREF_VALUE_1` (`PREF_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `fragment_pref_value`
--

INSERT INTO `fragment_pref_value` (`PREF_VALUE_ID`, `PREF_ID`, `VALUE_ORDER`, `VALUE`) VALUES
(1, 1, 0, 'teaser.weather.title'),
(2, 2, 0, 'portalu.disclaimer'),
(3, 3, 0, '/WEB-INF/templates/default_cms.vm'),
(4, 4, 0, 'disclaimer.title'),
(5, 5, 0, '/WEB-INF/templates/disclaimer_info.vm'),
(6, 6, 0, 'disclaimer.info.title'),
(7, 7, 0, 'portalu.about'),
(8, 8, 0, 'about-1'),
(9, 9, 0, '/WEB-INF/templates/default_cms.vm'),
(10, 10, 0, '/WEB-INF/templates/about_portalu_links.vm'),
(11, 11, 0, 'about.links.title'),
(12, 12, 0, '/WEB-INF/templates/about_portalu_partner.vm'),
(13, 13, 0, 'about.partner.title'),
(14, 14, 0, 'search-chronicle-1'),
(15, 15, 0, '/WEB-INF/templates/chronicle_info_new.vm'),
(16, 16, 0, 'chronicleSearch.info.title'),
(17, 17, 0, 'search-topics-1'),
(18, 18, 0, '/portal/search-extended/search-ext-env-area-contents.psml?select=2'),
(19, 19, 0, '/WEB-INF/templates/environment_info_new.vm'),
(20, 20, 0, 'envSearch.info.title'),
(21, 21, 0, 'search-measure-1'),
(22, 22, 0, '/WEB-INF/templates/measures_info_new.vm'),
(23, 23, 0, 'measuresSearch.info.title'),
(24, 24, 0, 'search-1'),
(25, 25, 0, 'searchSimple.title.result'),
(26, 26, 0, '/WEB-INF/templates/search_simple_info_new.vm'),
(27, 27, 0, 'searchSimple.info.title'),
(28, 28, 0, 'search-service-1'),
(29, 29, 0, '/portal/search-extended/search-ext-env-area-contents.psml?select=3'),
(30, 30, 0, '/WEB-INF/templates/service_info_new.vm'),
(31, 31, 0, 'serviceSearch.info.title'),
(32, 32, 0, '/WEB-INF/templates/myportal/login_teaser.vm'),
(33, 33, 0, 'teaser.login.title'),
(34, 34, 0, '/WEB-INF/templates/myportal/login_teaser.vm'),
(35, 35, 0, 'teaser.login.title'),
(36, 36, 0, 'portalu.privacy'),
(37, 37, 0, '/WEB-INF/templates/default_cms.vm'),
(38, 38, 0, '4'),
(39, 39, 0, 'search-history-1'),
(40, 40, 0, 'searchSimple.title.history'),
(41, 41, 0, '/WEB-INF/templates/search_history_info.vm'),
(42, 42, 0, 'searchHistory.info.title'),
(43, 43, 0, 'search-save-1'),
(44, 44, 0, 'searchSimple.title.save'),
(45, 45, 0, 'search-settings-1'),
(46, 46, 0, 'searchSimple.title.settings'),
(47, 47, 0, '/WEB-INF/templates/search_settings_info.vm'),
(48, 48, 0, 'searchSettings.info.title'),
(49, 49, 0, 'ingrid-newsletter-1'),
(50, 50, 0, '/WEB-INF/templates/contact_back.vm'),
(51, 51, 0, 'teaser.newsletter.contact'),
(52, 52, 0, '/WEB-INF/templates/myportal/login_teaser.vm'),
(53, 53, 0, 'teaser.newsletter.more_info'),
(54, 54, 0, '/WEB-INF/templates/newsletter_teaser.vm'),
(55, 55, 0, 'teaser.newsletter.title'),
(56, 56, 0, 'myingrid-1'),
(57, 57, 0, '/WEB-INF/templates/myportal/login_teaser.vm'),
(58, 58, 0, 'teaser.login.title'),
(59, 59, 0, '/WEB-INF/templates/sitemap.vm'),
(60, 60, 0, 'sitemap.title'),
(61, 61, 0, 'teaser.weather.title'),
(62, 62, 0, 'personalize.overview.title'),
(63, 63, 0, 'personalize.home.title'),
(64, 64, 0, 'personalize.partner.title'),
(65, 65, 0, 'personalize.sources.title'),
(66, 66, 0, 'personalize.settings.title'),
(67, 67, 0, 'personalize'),
(68, 68, 0, '/WEB-INF/templates/myportal/myportal_navigation.vm'),
(69, 69, 0, 'myPortal.info.navigation.title'),
(70, 70, 0, 'myingrid-1'),
(71, 71, 0, '/WEB-INF/templates/myportal/login_teaser.vm'),
(72, 72, 0, 'teaser.login.title'),
(73, 73, 0, '/WEB-INF/templates/myportal/myportal_navigation.vm'),
(74, 74, 0, 'myPortal.info.navigation.title'),
(6534, 6534, 0, 'teaser.weather.title'),
(6535, 6535, 0, 'teaser.weather.title'),
(7219, 7219, 0, '450'),
(7220, 7220, 0, ''),
(7221, 7221, 0, 'ingrid.application.0.title'),
(7222, 7222, 0, '883'),
(7223, 7223, 0, '450'),
(7224, 7224, 0, ''),
(7225, 7225, 0, 'ingrid.application.1.title'),
(7226, 7226, 0, '883'),
(7227, 7227, 0, '450'),
(7228, 7228, 0, ''),
(7229, 7229, 0, 'ingrid.application.2.title'),
(7230, 7230, 0, '883'),
(7231, 7231, 0, '/WEB-INF/templates/search_catalog/search_cat_thesaurus_info_sns.vm'),
(7232, 7232, 0, 'searchCatThesaurus.info.title'),
(7233, 7233, 0, 'ext-search-address-1'),
(7234, 7234, 0, 'searchSimple.title.extended'),
(7235, 7235, 0, '/WEB-INF/templates/search_extended/search_ext_adr_info.vm'),
(7236, 7236, 0, 'searchExtAdr.info.title'),
(7237, 7237, 0, 'ext-search-address-1'),
(7238, 7238, 0, 'searchSimple.title.extended'),
(7239, 7239, 0, '/WEB-INF/templates/search_extended/search_ext_adr_info.vm'),
(7240, 7240, 0, 'searchExtAdr.info.title'),
(7241, 7241, 0, 'ext-search-address-1'),
(7242, 7242, 0, 'searchSimple.title.extended'),
(7243, 7243, 0, '/WEB-INF/templates/search_extended/search_ext_adr_info.vm'),
(7244, 7244, 0, 'searchExtAdr.info.title'),
(7245, 7245, 0, 'ext-search-address-1'),
(7246, 7246, 0, 'searchSimple.title.extended'),
(7247, 7247, 0, '/WEB-INF/templates/search_extended/search_ext_adr_info.vm'),
(7248, 7248, 0, 'searchExtAdr.info.title'),
(7249, 7249, 0, 'ext-search-area-1'),
(7250, 7250, 0, 'searchSimple.title.extended'),
(7251, 7251, 0, '/WEB-INF/templates/search_extended/search_ext_env_area_contents_info.vm'),
(7252, 7252, 0, 'searchExtEnvAreaContents.info.title'),
(7253, 7253, 0, 'ext-search-area-1'),
(7254, 7254, 0, 'searchSimple.title.extended'),
(7255, 7255, 0, '/WEB-INF/templates/search_extended/search_ext_env_area_partner_info.vm'),
(7256, 7256, 0, 'searchExtEnvAreaPartner.info.title'),
(7257, 7257, 0, 'ext-search-area-1'),
(7258, 7258, 0, 'searchSimple.title.extended'),
(7259, 7259, 0, '/WEB-INF/templates/search_extended/search_ext_env_area_sources_info.vm'),
(7260, 7260, 0, 'searchExtEnvAreaSources.info.title'),
(7261, 7261, 0, 'ext-search-spacial-1'),
(7262, 7262, 0, 'searchSimple.title.extended'),
(7263, 7263, 0, '/WEB-INF/templates/search_extended/search_ext_env_place_geothesaurus_info.vm'),
(7264, 7264, 0, 'searchExtEnvPlaceGeothesaurus.info.title'),
(7265, 7265, 0, 'ext-search-spacial-1'),
(7266, 7266, 0, 'searchSimple.title.extended'),
(7267, 7267, 0, 'ext-search-time-1'),
(7268, 7268, 0, 'searchSimple.title.extended'),
(7269, 7269, 0, '/WEB-INF/templates/search_extended/search_ext_env_time_constraint_info.vm'),
(7270, 7270, 0, 'searchExtEnvTimeConstraint.info.title'),
(7271, 7271, 0, 'ext-search-subject-1'),
(7272, 7272, 0, 'searchSimple.title.extended'),
(7273, 7273, 0, '/WEB-INF/templates/search_extended/search_ext_env_topic_terms_info.vm'),
(7274, 7274, 0, 'searchExtEnvTopicTerms.info.title'),
(7275, 7275, 0, 'ext-search-subject-1'),
(7276, 7276, 0, 'searchSimple.title.extended'),
(7277, 7277, 0, '/WEB-INF/templates/search_extended/search_ext_env_topic_thesaurus_info.vm'),
(7278, 7278, 0, 'searchExtEnvTopicThesaurus.info.title'),
(7279, 7279, 0, 'ext-search-area-1'),
(7280, 7280, 0, 'searchSimple.title.extended'),
(7281, 7281, 0, '/WEB-INF/templates/search_extended/search_ext_law_area_partner_info.vm'),
(7282, 7282, 0, 'searchExtEnvAreaPartner.info.title'),
(7283, 7283, 0, 'ext-search-subject-1'),
(7284, 7284, 0, 'searchSimple.title.extended'),
(7285, 7285, 0, '/WEB-INF/templates/search_extended/search_ext_law_topic_terms_info.vm'),
(7286, 7286, 0, 'searchExtEnvTopicTerms.info.title'),
(7287, 7287, 0, 'ext-search-subject-1'),
(7288, 7288, 0, 'searchSimple.title.extended'),
(7289, 7289, 0, '/WEB-INF/templates/search_extended/search_ext_law_topic_thesaurus_info.vm'),
(7290, 7290, 0, 'searchExtEnvTopicThesaurus.info.title'),
(7291, 7291, 0, 'ext-search-research-2'),
(7292, 7292, 0, 'searchSimple.title.extended'),
(7293, 7293, 0, '/WEB-INF/templates/search_extended/search_ext_res_topic_attributes_info.vm'),
(7294, 7294, 0, 'searchExtResTopicAttributes.info.title'),
(7295, 7295, 0, 'ext-search-research-1'),
(7296, 7296, 0, 'searchSimple.title.extended'),
(7297, 7297, 0, '/WEB-INF/templates/search_extended/search_ext_res_topic_terms_info.vm'),
(7298, 7298, 0, 'searchExtResTopicTerms.info.title');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `fragment_prop`
--

DROP TABLE IF EXISTS `fragment_prop`;
CREATE TABLE IF NOT EXISTS `fragment_prop` (
  `PROP_ID` int(11) NOT NULL,
  `FRAGMENT_ID` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `SCOPE` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SCOPE_VALUE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`PROP_ID`),
  UNIQUE KEY `UN_FRAGMENT_PROP_1` (`FRAGMENT_ID`,`NAME`,`SCOPE`,`SCOPE_VALUE`),
  KEY `IX_FRAGMENT_PROP_1` (`FRAGMENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `fragment_prop`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_anniversary`
--

DROP TABLE IF EXISTS `ingrid_anniversary`;
CREATE TABLE IF NOT EXISTS `ingrid_anniversary` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `topic_id` varchar(255) NOT NULL DEFAULT '',
  `topic_name` varchar(255) DEFAULT NULL,
  `date_from` varchar(255) DEFAULT NULL,
  `date_from_year` mediumint(9) DEFAULT NULL,
  `date_from_month` mediumint(9) DEFAULT NULL,
  `date_from_day` mediumint(9) DEFAULT NULL,
  `date_to` varchar(255) DEFAULT NULL,
  `date_to_year` mediumint(9) DEFAULT NULL,
  `date_to_month` mediumint(9) DEFAULT NULL,
  `date_to_day` mediumint(9) DEFAULT NULL,
  `administrative_id` varchar(255) DEFAULT NULL,
  `fetched` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `fetched_for` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `language` varchar(5) DEFAULT 'de',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=18115 ;

--
-- Daten für Tabelle `ingrid_anniversary`
--

INSERT INTO `ingrid_anniversary` (`id`, `topic_id`, `topic_name`, `date_from`, `date_from_year`, `date_from_month`, `date_from_day`, `date_to`, `date_to_year`, `date_to_month`, `date_to_day`, `administrative_id`, `fetched`, `fetched_for`, `language`) VALUES
(18114, 'calendarEvent_180', 'Greenpeace action against French atomic tests', '1992-01-21', 1992, 1, 21, '1992-01-21', 1992, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'en'),
(18113, 't9c22ff_11f605c4a69_-f51', 'Cabinet decides on implementation of EU Batteries Directive', '2009-01-21', 2009, 1, 21, '2009-01-21', 2009, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'en'),
(18112, 't2cd728_12057cca378_1295', 'The 1968 Thule air crash', '1968-01-21', 1968, 1, 21, '1968-01-21', 1968, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'en'),
(18111, 't53da2896_13d1685ed61_3901', 'Permanent Secretariat of Arctic Council established in Tromsø', '2013-01-21', 2013, 1, 21, '2013-01-21', 2013, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'en'),
(18110, 't-6dadf813_143c097fe2a_-8ae', 'Long term exposure to air pollution linked to coronary events', '2014-01-21', 2014, 1, 21, '2014-01-21', 2014, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'en'),
(18105, 't-6dadf813_143c097fe2a_-8ae', 'Feinstaubbelastung führt zu erhöhtem Herzinfarkt-Risiko', '2014-01-21', 2014, 1, 21, '2014-01-21', 2014, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'de'),
(18106, 't2cd728_12057cca378_1295', 'Der Flugzeugabsturz  bei Thule 1968', '1968-01-21', 1968, 1, 21, '1968-01-21', 1968, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'de'),
(18107, 't9c22ff_11f605c4a69_-f51', 'Kabinett beschließt Umsetzung der EU-Batterierichtlinie', '2009-01-21', 2009, 1, 21, '2009-01-21', 2009, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'de'),
(18108, 't53da2896_13d1685ed61_3901', 'Ständiges Sekretariat für den Arktischen Rat in Tromsø gegründet', '2013-01-21', 2013, 1, 21, '2013-01-21', 2013, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'de'),
(18109, 'calendarEvent_180', 'Greenpeace Aktion gegen französische Atomtests', '1992-01-21', 1992, 1, 21, '1992-01-21', 1992, 1, 21, NULL, '2015-01-21 00:00:00', '2015-01-21 00:00:00', 'de');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_chron_eventtypes`
--

DROP TABLE IF EXISTS `ingrid_chron_eventtypes`;
CREATE TABLE IF NOT EXISTS `ingrid_chron_eventtypes` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `form_value` varchar(255) NOT NULL DEFAULT '',
  `query_value` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=16 ;

--
-- Daten für Tabelle `ingrid_chron_eventtypes`
--

INSERT INTO `ingrid_chron_eventtypes` (`id`, `form_value`, `query_value`, `sortkey`) VALUES
(1, 'act', 'activity', 1),
(2, 'his', 'historical', 2),
(3, 'leg', 'legal', 3),
(5, 'dis', 'disaster', 4),
(6, 'cfe', 'conference', 5),
(8, 'yea', 'natureOfTheYear', 6),
(9, 'pub', 'publication', 7),
(13, 'ann', 'anniversary', 8),
(14, 'int', 'interYear', 9),
(15, 'obs', 'observation', 10);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_cms`
--

DROP TABLE IF EXISTS `ingrid_cms`;
CREATE TABLE IF NOT EXISTS `ingrid_cms` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `item_key` varchar(255) NOT NULL DEFAULT '',
  `item_description` varchar(255) DEFAULT NULL,
  `item_changed` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `item_changed_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=25 ;

--
-- Daten für Tabelle `ingrid_cms`
--

INSERT INTO `ingrid_cms` (`id`, `item_key`, `item_description`, `item_changed`, `item_changed_by`) VALUES
(1, 'portalu.teaser.inform', 'PortalU informiert Text', '2014-08-27 00:00:00', 'kst_su'),
(15, 'portalu.disclaimer', 'Impressum', '2013-07-09 00:00:00', 'admin'),
(16, 'portalu.about', 'Über PortalU', '2013-05-28 00:00:00', 'admin'),
(17, 'portalu.privacy', 'Haftungsausschluss', '2012-02-24 00:00:00', 'admin'),
(18, 'portalu.contact.intro.postEmail', 'Adresse auf der Kontaktseite', '2012-02-02 00:00:00', 'kst_cg'),
(19, 'ingrid.home.welcome', 'Ingrid Willkommens Portlet', '2008-07-09 00:00:00', 'kst_cg'),
(20, 'portal.teaser.shortcut', 'Anwendungen', '2012-07-19 15:36:12', 'admin'),
(21, 'portal.teaser.shortcut.query', 'Schnellsuche', '2012-07-19 15:36:12', 'admin'),
(22, 'portal.cms.portlet.0', 'CMSPortlet0', '2012-07-19 15:36:24', 'admin'),
(23, 'portal.cms.portlet.1', 'CMSPortlet1', '2012-07-19 15:36:24', 'admin'),
(24, 'portal.cms.portlet.2', 'CMSPortlet2', '2012-07-19 15:36:24', 'admin');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_cms_item`
--

DROP TABLE IF EXISTS `ingrid_cms_item`;
CREATE TABLE IF NOT EXISTS `ingrid_cms_item` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `fk_ingrid_cms_id` mediumint(9) NOT NULL DEFAULT '0',
  `item_lang` varchar(6) NOT NULL DEFAULT '',
  `item_title` varchar(255) DEFAULT NULL,
  `item_value` text,
  `item_changed` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `item_changed_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=47 ;

--
-- Daten für Tabelle `ingrid_cms_item`
--

INSERT INTO `ingrid_cms_item` (`id`, `fk_ingrid_cms_id`, `item_lang`, `item_title`, `item_value`, `item_changed`, `item_changed_by`) VALUES
(1, 1, 'de', '<span style="text-transform: none;">PORTALU INFORMIERT</span>', '<p style="background:url(/webstats/portalu_macht_zu.png); background-repeat: no-repeat;">\r\n<span style="color:#008000;">Die VwV UDK/GEIN wurde gekündigt.</span><br>Demzufolge wird PortalU Ende 2014 den Betrieb einstellen:<br>\r\n<img src="http://portalu.de/cgi-bin/count_days.pl" alt="das Ende von portalu.de naht."></p>', '2014-08-27 00:00:00', 'kst_su'),
(37, 20, 'de', 'Anwendungen', '', '2012-07-19 15:36:12', 'admin'),
(38, 20, 'en', 'Application', '', '2012-07-19 15:36:12', 'admin'),
(39, 21, 'de', 'Schnellsuche', '', '2012-07-19 15:36:12', 'admin'),
(2, 1, 'en', '<span style="text-transform: none;">PortalU informs</span>', '<p style="background:url(/webstats/portalu_macht_zu.png); background-repeat: no-repeat;">\r\n<span style="color:#008000;">The administrative agreement VwV UDK/GEIN is terminated.</span><br>Thus the information portal PortalU will be shut down:<br>\r\n<img src="http://portalu.de/cgi-bin/count_days.pl" alt="The end of portalu.de is near."></p>\r\n', '2014-08-27 00:00:00', 'kst_su'),
(40, 21, 'en', 'Quick Search', '', '2012-07-19 15:36:12', 'admin'),
(41, 22, 'de', 'CMSPortlet0', '', '2012-07-19 15:36:24', 'admin'),
(42, 22, 'en', 'CMSPortlet0', '', '2012-07-19 15:36:24', 'admin'),
(43, 23, 'de', 'CMSPortlet1', '', '2012-07-19 15:36:24', 'admin'),
(27, 15, 'en', 'Disclaimer', '<a name="herausgeber"></a>\r\n<h2>Publisher</h2>\r\n<p>PortalU is managed by the Coordination Center PortalU at the Environmental, Energy and Climate Protection Ministry of Lower Saxony, Hannover, Germany. Development and maintenance of the portal is financed by a administrative agreement between the German Federal States (Länder) and the Federal Government. </p>\r\n<h3><a href="http://www.kst.portalu.de/" target="_new" title="Link öffnet in neuem Fenster">Coordination Center PortalU</a></h3>\r\n<p>c/o Nieders. Ministerium für Umwelt, Energie und Klimaschutz<br>Archivstrasse 2<br>D-30169 Hannover<br>\r\n	<a href="/ingrid-portal/portal/service-contact.psml">Contact</a>\r\n</p>\r\n<br>\r\n<a name="verantwortlich"></a>\r\n<h2>Overall Responsibility</h2>\r\n<p>Dr. Fred Kruse</p>\r\n<br>\r\n<a name="realisierung"></a>\r\n<h2>Implementation</h2>\r\n<h3><a href="http://www.wemove.com/" target="_new" title="Link opens new window">wemove digital solutions GmbH</a></h3>\r\n<h3><a href="http://www.chives.de/" target="_new" title="Link opens new window">chives - Büro für Webdesign Plus</a></h3>\r\n<br>\r\n<a name="betrieb"></a>\r\n<h2>Operation</h2>     \r\n<h3><a href="http://www.its-technidata.de/" target="_new" title="Link opens new window">TechniData IT Service GmbH</a></h3>\r\n<br>\r\n<a name="haftung"></a>\r\n<h2>Liability Disclaimer</h2>     \r\n<p>The Environment Ministry of Lower Saxony (Niedersächsisches Umweltministerium) does not take any responisbility for the content of web-sites that can be reached through PortalU. Web-sites that are included in the portal are evaluated only technically. A continuous evaluation of the content of the included web-pages in neither possible nor intended. The Environment Ministry of Lower Saxony explicitly rejects all content that potentially infringes upon German legislation or general morality.</p>\r\n<p> </p>\r\n\r\n<h2>Nutzungsbedingungen</h2>\r\n\r\n<p class=MsoNormal>Die im PortalU-Kartenviewer eingebundenen Karten\r\n(Geodatendienste) stammen von behördlichen Anbietern auf Bundes- und\r\nLandesebene. Die Nutzung der kostenfrei angebotenen Geodatendienste ist hierbei\r\nan entsprechende Bedingungen geknüpft, die zu beachten sind.</p>\r\n\r\n<p class=MsoNormal> </p>\r\n\r\n<p class=MsoNormal>Für Dienste aus Bayern sind die Nutzugsbedingungen der\r\nGDI-BY zu beachten:</p>\r\n\r\n<p class=MsoNormal><a\r\nhref="http://www.gdi.bayern.de/Dokumente/Nutzungsbedingungen_allgemein.html">http://www.gdi.bayern.de/Dokumente/Nutzungsbedingungen_allgemein.html</a></p>\r\n\r\n<p class=MsoNormal> </p>\r\n', '2013-07-09 00:00:00', 'admin'),
(28, 15, 'de', 'Impressum', '<h2><a name=herausgeber></a>Herausgeber</h2>\r\n\r\n<p>PortalU wird von der Koordinierungsstelle PortalU im Niedersächsischen\r\nMinisterium für Umwelt, Energie und Klimaschutz auf der Grundlage der <a\r\nhref="http://www.kst.portalu.de/verwaltungskooperation/VVGEIN_endg.pdf"\r\ntarget="_new" title="Link öffnet in neuem Fenster">Bund-Länder-Verwaltungsvereinbarung\r\nUDK/GEIN</a> betrieben und weiterentwickelt.</p>\r\n\r\n<h3><a href="http://www.kst.portalu.de/" target="_new"\r\ntitle="Link \r\nöffnet in neuem Fenster">Koordinierungsstelle PortalU</a></h3>\r\n\r\n<p>Niedersächsisches Ministerium für Umwelt, Energie und Klimaschutz<br>\r\nArchivstrasse 2<br>\r\nD-30169 Hannover<br>\r\n<a href="http://www.portalu.de/ingrid-portal/portal/service-contact.psml">Kontakt</a>\r\n</p>\r\n\r\n<p class=MsoNormal><a name=verantwortlich></a> </p>\r\n\r\n<h2>Verantwortliche Gesamtredaktion</h2>\r\n\r\n<p>Dr. Fred Kruse</p>\r\n\r\n<p class=MsoNormal><a name=realisierung></a> </p>\r\n\r\n<h2>Realisierung</h2>\r\n\r\n\r\n\r\n<h3><a href="http://www.wemove.com/" target="_new"\r\ntitle="Link öffnet in neuem Fenster">wemove digital solutions GmbH</a></h3>\r\n\r\n<h3><a href="http://www.chives.de/" target="_new"\r\ntitle="Link öffnet in \r\nneuem Fenster">chives - Büro für Webdesign Plus\r\nDarmstadt</a></h3>\r\n\r\n<p class=MsoNormal><a name=betrieb></a> </p>\r\n\r\n<h2>Technischer Betrieb</h2>\r\n\r\n<h3><a href="http://www.its-technidata.de/" target="_new"\r\ntitle="Link \r\nöffnet in neuem Fenster">TechniData IT Service GmbH</a></h3>\r\n\r\n<p class=MsoNormal><a name=haftung></a> </p>\r\n\r\n<h2>Haftungsausschluss</h2>\r\n\r\n<p>Das Niedersächsische Ministerium für Umwelt, Energie und Klimaschutz übernimmt keine\r\nVerantwortung für die Inhalte von Websites, die über Links erreicht werden. Die\r\nLinks werden bei der Aufnahme nur kursorisch angesehen und bewertet. Eine\r\nkontinuierliche Prüfung der Inhalte ist weder beabsichtigt noch möglich. Das\r\nNiedersächsische Ministerium für Umwelt und Klimaschutz distanziert sich\r\nausdrücklich von allen Inhalten, die möglicherweise straf- oder\r\nhaftungsrechtlich relevant sind oder gegen die guten Sitten verstoßen.</p>\r\n\r\n<p> </p>\r\n\r\n<h2>Nutzungsbedingungen</h2>\r\n\r\n<p class=MsoNormal>Die im PortalU-Kartenviewer eingebundenen Karten\r\n(Geodatendienste) stammen von behördlichen Anbietern auf Bundes- und\r\nLandesebene. Die Nutzung der kostenfrei angebotenen Geodatendienste ist hierbei\r\nan entsprechende Bedingungen geknüpft, die zu beachten sind.</p>\r\n\r\n<p class=MsoNormal> </p>\r\n\r\n<p class=MsoNormal>Für Dienste aus Bayern sind die Nutzugsbedingungen der\r\nGDI-BY zu beachten:</p>\r\n\r\n<p class=MsoNormal><a\r\nhref="http://www.gdi.bayern.de/Dokumente/Nutzungsbedingungen_allgemein.html">http://www.gdi.bayern.de/Dokumente/Nutzungsbedingungen_allgemein.html</a></p>\r\n\r\n<p class=MsoNormal> </p>\r\n\r\n', '2013-07-09 00:00:00', 'admin'),
(29, 16, 'de', 'Porträt', '<p>Zur richtigen Einschätzung und Bewertung von Umweltsituationen werden umfassende Informationen über die Umwelt benötigt. In der öffentlichen Verwaltung wird eine Vielzahl von Umweltinformationen auf unterschiedlichster Ebene generiert. Diese Informationen sind aber zum Teil nur schwer auffindbar. Zur Verbesserung der Auffindbarkeit wurde deshalb das Umweltportal PortalU ins Leben gerufen. \r\n</p>\r\n<p>PortalU bietet einen zentralen Zugriff auf umweltrelevante Webseiten, Umweltdatenkataloge und Datenbanken von über 450 \r\n<a href="http://www.portalu.de/informationsanbieter" target="_new" title="PortalU - Informationsanbieter - Link öffnet in neuem Fenster">öffentlichen Institutionen</a> in Deutschland. Sie können im gesamten Informationsangebot oder gezielt nach einzelnen Umweltthemen, digitalen Karten, Umweltmesswerten, Presseinformationen oder historischen Ereignissen recherchieren. \r\n</p> \r\n<p>Die modular aufgebaute PortalU-Software InGrid kann man sich als Informationsnetz vorstellen, das Webseiten, Daten und Metadaten unter einem Dach bündelt. Die Software besteht aus einer flexibel konfigurierbaren Portaloberfläche, einem Web-Katalog-Service, einem Karten-Viewer sowie diversen An- und Abfrageschnittstellen zur Recherche in angeschlossenen Systemen bzw. für den Transfer von Informationen. Die PortalU-Software basiert auf Open-Source-Komponenten und Eigenentwicklungen und kann somit innerhalb der öffentlichen Verwaltung lizenzkostenfrei genutzt werden.\r\n</p>\r\n<p>PortalU basiert auf einer <a href="http://www.kst.portalu.de/verwaltungskooperation/VVGEIN_endg.pdf" target="_new" title="Zur Verwaltungsvereinbarung UDK/GEIN - Link öffnet in neuem Fenster">Verwaltungsvereinbarung</a> zwischen Bund und Ländern. Darin verständigen sich die Partner, dass ein gemeinsames Umweltportal zum Nachweis und zur aktiven Verbreitung von Umweltinformationen zum Einsatz kommen soll. Die Erfüllung der Anforderungen der Aarhus-Konvention und der EU-Richtlinie über den Zugang der Öffentlichkeit zu Umweltinformationen bzw. der entsprechenden Umweltinformationsgesetze von Bund und Ländern stehen hierbei im Mittelpunkt. Seit 2007 sind für die Datenkataloge zudem die Maßgaben der europäischen INSPIRE-Richtlinie ausschlaggebend. \r\n</p>\r\n<p>Technisch und inhaltlich wird das Portal von der Koordinierungsstelle PortalU im Niedersächsischen Umweltministerium betreut. Für Fragen und Anregungen wenden Sie sich bitte an die <a href="mailto:kst@portalu.de" title="Schreiben Sie uns - Email Programm startet automatisch">Koordinierungsstelle PortalU</a>. Weitere Hintergrundinformationen finden Sie außerdem auf der <a href="http://www.kst.portalu.de/" target="_new" title="Zur Homepage der Bund-Länder-Kooperation PortalU - Link öffnet in neuem Fenster">Homepage der Bund-Länder-Kooperation PortalU</a>. </p>', '2013-05-28 00:00:00', 'admin'),
(30, 16, 'en', 'Portrait', '<p>Commonly, a broad range of information about the environment is needed for estimating environmental situations accurately. A multiplicity of environmental information is generated by public administration on different hierarchical levels. Unfortunately the access to this information is mostly difficult. The environmental information portal PortalU aims to overcome this obstacle.\r\n</p>\r\n<p>PortalU offers central access to environmentally relevant web pages, data catalogues and databases from over 450 <a href="http://www.portalu.de/informationsanbieter" target="_new" title="PortalU - Information providers - Link opens in new window">public organisations</a> in Germany. You can search through the whole information range or search selectivly through single environmental topics, digital maps, measured data, press information or historical events. \r\n</p> \r\n<p>The PortalU software InGrid consists of several modules, which can be described as information grid. The information grid ties web pages, data and metadata under a single roof. The software consists of a portal surface, which can be configured flexibly, a web catalogue service, a map viewer and several technical interfaces to connect and transfer information. InGrid is based on open source components and internal developments. Therefore it can be used without external licence costs within the public administration.\r\n</p>\r\n<p>PortalU is based on an <a href="http://www.kst.portalu.de/verwaltungskooperation/VVGEIN_endg.pdf" target="_new" title="To the administrative agreement UDK/GEIN - Link opens in new window">administrative agreement</a> between the Federal Republic of Germany and the German Federal States. The agreement partners aim at improving the active dissemination of public environmental information through a common environmental information portal. The main focus is thereby set on the conformance of requirements of the Aarhus Convention and of the EU Directive 2003/4/EC which defines the public access to environmental information, respectively the German environmental information acts (Umweltinformationsgesetze). Besides this, the requirements of the EU INSPIRE Directive 2007/2/EC are crucial for the web catalogue service of PortalU (InGridCatalog).\r\n</p>\r\n<p> The project is managed by the Coordination Center PortalU at the Ministry of Environment of Lower Saxony in Hanover, Germany. For questions and suggestions please contact the <a href="mailto:kst@portalu.de" title="Write us - Email program starts automatically">Coordination Center PortalU</a>. For further background information please visit the <a href="http://www.kst.portalu.de/" target="_new" title="homepage of the Federal-State-Cooperation PortalU - Link opens in new window ">Homepage of the Federal-State-Cooperation PortalU </a>. \r\n</p>', '2013-05-28 00:00:00', 'admin'),
(31, 17, 'de', 'Datenschutz', '<p>PortalU enthält sowohl Inhalte, die als Teledienst nach § 2 Teledienstgesetz (TDG) als auch Inhalte, die als Mediendienst nach § 2 Mediendienste-Staatsvertrag (MDStV) zu bewerten sind. Hierbei werden folgende Verfahrensgrundsätze gewährleistet:<br></p>\r\n<ul>\r\n<li>Bei jedem Zugriff eines Nutzers auf eine Seite aus dem Angebot von PortalU und bei jedem Abruf einer Datei werden Daten über diesen Vorgang in einer Protokolldatei gespeichert. Diese Daten sind nicht personenbezogen. Wir können also nicht nachvollziehen, welcher Nutzer welche Daten abgerufen hat. Die Protokolldaten werden lediglich in anonymisierter Form statistisch ausgewertet und dienen damit der inhaltlichen Verbesserung unseres Angebotes.<br><br>\r\n<br>\r\n<a href="http://portalu.de/piwik-config/">Deaktivierung/Aktivierung der statistischen Erfassung</a>\r\n<br><br>\r\n</li>\r\n<li>Eine Ausnahme besteht innerhalb des Internetangebotes mit der Eingabe persönlicher oder geschäftlicher Daten (eMail-Adresse, Name, Anschrift) zur Anmeldung bei "Mein PortalU" oder der Bestellung des PortalU-Newsletters. Dabei erfolgt die Angabe dieser Daten durch Nutzerinnen und Nutzer ausdrücklich freiwillig. Ihre persönlichen Daten werden von uns selbstverständlich nicht an Dritte weitergegeben. Die Inanspruchnahme aller angebotenen Dienste ist, soweit dies technisch möglich und zumutbar ist, auch ohne Angabe solcher Daten beziehungsweise unter Angabe anonymisierter Daten oder eines Pseudonyms möglich.<br><br>\r\n</li>\r\n<li>Sie können alle allgemein zugänglichen PortalU-Seiten ohne den Einsatz von Cookies benutzen. Wenn Ihre Browser-Einstellungen das Setzen von Cookies zulassen, werden von PortalU sowohl Session-Cookies als auch permanente Cookies gesetzt. Diese dienen ausschließlich der Erhöhung des Bedienungskomforts.\r\n</li>\r\n</ul>', '2012-02-24 00:00:00', 'admin'),
(32, 17, 'en', 'Privacy Policy', '<p>PortalU contains content that is categorized as "Teledienst" (after § 2 Teledienstgesetz (TDG)), as well as content that is categorized as "Mediendienst" (after § 2 Mediendienste-Staatsvertrag (MDStV)). The following policies do apply:<br></p>\r\n<ul>\r\n<li>With each user-access to a content-page in PortalU, the relevant access-data are saved in a log file. This information is not personalized. Therefore it is not possible to reason which user has had access to which content page. The purpose of the log file is purely statistical. The evaluation of the log file helps to improve PortalU.<br><br>\r\n<a href="http://portalu.de/piwik-config/">Deactivation/activation of the statistic collection</a>\r\n<br><br>\r\n</li>\r\n<li>An exeption to our general privacy policy is made when personal data (e-mail, name, address) are provided to register for the PortalU newsletter. This information is provided by the user on a voluntary basis an is saved for internal purposes (mailing of newsletter). The information is not given to third-parties. The use of specific Portal functions does not, as far as technically feasible, depend on the provision of personal data.<br><br>\r\n</li>\r\n<li>You can take benefit of virtually all functions of PortalU without the use of cookies. However, if you choose to allow cookies in your browser, this will increase the useability of the application. \r\n</li>\r\n</ul>', '2012-02-24 00:00:00', 'admin'),
(33, 18, 'de', '', '.</p><p>Unsere Postadresse:</p>\r\n\r\n<p> Koordinierungsstelle PortalU<br />\r\nNiedersächs. Ministerium für Umwelt, Energie und Klimaschutz<br />\r\nArchivstrasse 2<br />\r\nD-30169 Hannover<br /></p>\r\n\r\n<p>Nehmen Sie online Kontakt mit uns auf! Wir werden Ihnen schnellstmöglichst per E-Mail antworten. Die eingegebenen Informationen und Daten werden nur zur Bearbeitung Ihrer Anfrage gespeichert und genutzt. Beachten Sie bitte, dass die Datenübermittlung über das Kontaktformular unverschlüsselt erfolgt. Für vertrauliche Nachrichten nutzen Sie bitte den herkömmlichen Postweg.</p>\r\n', '2012-02-02 00:00:00', 'kst_cg'),
(34, 18, 'en', '', '.</p><p>Our address:</p><p>Niedersächsisches Ministerium für Umwelt und Klimaschutz<br />Koordinierungsstelle PortalU<br />Archivstrasse 2<br />D-30169 Hannover<br /></p> <p>Please contact us! We will answer your request as soon as possible. All data you entered will be saved only to process your request.</p>', '2012-02-02 00:00:00', 'kst_cg'),
(35, 19, 'de', 'Willkommen bei PortalU', '<p>Willkommen bei PortalU, dem Umweltportal Deutschland! Wir bieten Ihnen einen zentralen Zugriff auf mehrere hunderttausend Internetseiten und Datenbankeinträge von öffentlichen Institutionen und Organisationen. Zusätzlich können Sie aktuelle Nachrichten und Veranstaltungshinweise, Umweltmesswerte, Hintergrundinformationen und historische Umweltereignisse über PortalU abrufen.</p><p>Die integrierte Suchmaschine ist eine zentrale Komponente von PortalU. Mit ihrer Hilfe können Sie Webseiten und Datenbankeinträge nach Stichworten durchsuchen. Über die Option "Erweiterte Suche" können Sie zusätzlich ein differenziertes Fachvokabular und deutschlandweite Hintergrundkarten zur Zusammenstellung Ihrer Suchanfrage nutzen.</p><p>PortalU ist eine Kooperation der Umweltverwaltungen im Bund und in den Ländern. Inhaltlich und technisch wird PortalU von der <a href="http://www.kst.portalu.de/" target="_new" title="Link öffnet in neuem Fenster">Koordinierungsstelle PortalU</a> im Niedersächsischen Ministerium für Umwelt und Klimaschutz verwaltet. Wir sind darum bemüht, das System kontinuierlich zu erweitern und zu optimieren. Bei Fragen und Verbesserungsvorschlägen wenden Sie sich bitte an die <a href="mailto:kst@portalu.de">Koordinierungsstelle PortalU</a>.</p>', '2008-07-09 00:00:00', 'kst_cg'),
(36, 19, 'en', 'Welcome to PortalU', '<p>Welcome to PortalU, the German Environmental Information Portal! We offer a comfortable and central access to over 1.000.000 web-pages and database entries from public agencies in Germany. We also guide you directly to up-to-date environmental news, upcoming and past environmental events, environmental monitoring data, and interesting background information on many environmental topics.</p><p>Core-component of PortalU is a powerful search-engine that you can use to look up your terms of interest in web-pages and databases. In the "Extended Search" mode, you can use an environmental thesaurus and a digital mapping tool to compose complex spatio-thematic queries.</p><p>PortalU is the result of a cooperation between the German "Länder" and the German Federal Government. The project is managed by the <a href="http://www.kst.portalu.de/" target="_new" title="Link opens new window">Coordination Center PortalU</a>, a group of environmental and IT experts attached to the Environment Ministry of Lower Saxony in Hannover, Germany. We strive to continuously improve and extend the portal. Please help us in this effort and mail your suggestions or questions to <a href="mailto:kst@portalu.de">Coordination Center PortalU</a>.</p>', '2008-07-09 00:00:00', 'kst_cg'),
(44, 23, 'en', 'CMSPortlet1', '', '2012-07-19 15:36:24', 'admin'),
(45, 24, 'de', 'CMSPortlet2', '', '2012-07-19 15:36:24', 'admin'),
(46, 24, 'en', 'CMSPortlet2', '', '2012-07-19 15:36:24', 'admin');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_env_topic`
--

DROP TABLE IF EXISTS `ingrid_env_topic`;
CREATE TABLE IF NOT EXISTS `ingrid_env_topic` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `form_value` varchar(255) NOT NULL DEFAULT '',
  `query_value` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=22 ;

--
-- Daten für Tabelle `ingrid_env_topic`
--

INSERT INTO `ingrid_env_topic` (`id`, `form_value`, `query_value`, `sortkey`) VALUES
(1, 'abf', 'abfall', 1),
(2, 'alt', 'altlasten', 2),
(3, 'bau', 'bauen', 3),
(4, 'bod', 'boden', 4),
(5, 'che', 'chemikalien', 5),
(6, 'ene', 'energie', 6),
(7, 'for', 'forstwirtschaft', 7),
(8, 'gen', 'gentechnik', 8),
(9, 'geo', 'geologie', 9),
(10, 'ges', 'gesundheit', 10),
(11, 'lae', 'laermerschuetterung', 11),
(12, 'lan', 'landwirtschaft', 12),
(13, 'luf', 'luftklima', 13),
(14, 'nac', 'nachhaltigeentwicklung', 14),
(15, 'nat', 'naturlandschaft', 15),
(16, 'str', 'strahlung', 16),
(17, 'tie', 'tierschutz', 17),
(18, 'uin', 'umweltinformation', 18),
(19, 'uwi', 'umweltwirtschaft', 19),
(20, 'ver', 'verkehr', 20),
(21, 'was', 'wasser', 21);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_lookup`
--

DROP TABLE IF EXISTS `ingrid_lookup`;
CREATE TABLE IF NOT EXISTS `ingrid_lookup` (
  `id` mediumint(9) NOT NULL DEFAULT '0',
  `item_key` varchar(255) NOT NULL DEFAULT '',
  `item_value` varchar(255) DEFAULT NULL,
  `item_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `ingrid_lookup`
--

INSERT INTO `ingrid_lookup` (`id`, `item_key`, `item_value`, `item_date`) VALUES
(1, 'ingrid_db_version', '3.5', '2015-01-14 00:00:00');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_measures_rubric`
--

DROP TABLE IF EXISTS `ingrid_measures_rubric`;
CREATE TABLE IF NOT EXISTS `ingrid_measures_rubric` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `form_value` varchar(255) NOT NULL DEFAULT '',
  `query_value` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Daten für Tabelle `ingrid_measures_rubric`
--

INSERT INTO `ingrid_measures_rubric` (`id`, `form_value`, `query_value`, `sortkey`) VALUES
(1, 'str', 'radiation', 1),
(2, 'luf', 'air', 2),
(3, 'was', 'water', 3),
(4, 'wei', 'misc', 4);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_newsletter_data`
--

DROP TABLE IF EXISTS `ingrid_newsletter_data`;
CREATE TABLE IF NOT EXISTS `ingrid_newsletter_data` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `ingrid_newsletter_data`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_partner`
--

DROP TABLE IF EXISTS `ingrid_partner`;
CREATE TABLE IF NOT EXISTS `ingrid_partner` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `ident` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=18 ;

--
-- Daten für Tabelle `ingrid_partner`
--

INSERT INTO `ingrid_partner` (`id`, `ident`, `name`, `sortkey`) VALUES
(1, 'bund', 'Bund', 1),
(2, 'bw', 'Baden-Württemberg', 2),
(3, 'by', 'Bayern', 3),
(4, 'be', 'Berlin', 4),
(5, 'bb', 'Brandenburg', 5),
(6, 'hb', 'Bremen', 6),
(7, 'hh', 'Hamburg', 7),
(8, 'he', 'Hessen', 8),
(9, 'mv', 'Mecklenburg-Vorpommern', 9),
(10, 'ni', 'Niedersachsen', 10),
(11, 'nw', 'Nordrhein-Westfalen', 11),
(12, 'rp', 'Rheinland-Pfalz', 12),
(13, 'sl', 'Saarland', 13),
(14, 'sn', 'Sachsen', 14),
(15, 'st', 'Sachsen-Anhalt', 15),
(16, 'sh', 'Schleswig-Holstein', 16),
(17, 'th', 'Thüringen', 17);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_principal_pref`
--

DROP TABLE IF EXISTS `ingrid_principal_pref`;
CREATE TABLE IF NOT EXISTS `ingrid_principal_pref` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `principal_name` varchar(251) NOT NULL DEFAULT '',
  `pref_name` varchar(251) NOT NULL DEFAULT '',
  `pref_value` mediumtext,
  `modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `ingrid_principal_pref`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_provider`
--

DROP TABLE IF EXISTS `ingrid_provider`;
CREATE TABLE IF NOT EXISTS `ingrid_provider` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `ident` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `url` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  `sortkey_partner` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=563 ;

--
-- Daten für Tabelle `ingrid_provider`
--

INSERT INTO `ingrid_provider` (`id`, `ident`, `name`, `url`, `sortkey`, `sortkey_partner`) VALUES
(1, 'bu_bmu', 'Bundesministerium für Umwelt, Naturschutz und Reaktorsicherheit', 'http://www.bmu.de/', 1, 1),
(2, 'bu_uba', 'Umweltbundesamt', 'http://www.umweltbundesamt.de/', 2, 1),
(3, 'bu_bfn', 'Bundesamt für Naturschutz', 'http://www.bfn.de/', 3, 1),
(4, 'bu_bfs', 'Bundesamt für Strahlenschutz', 'http://www.bfs.de/', 4, 1),
(5, 'bu_bmf', 'Bundesministerium der Finanzen', 'http://www.bundesfinanzministerium.de/', 5, 1),
(6, 'bu_bmbf', 'Bundesministerium für Bildung und Forschung', 'http://www.bmbf.de/', 6, 1),
(7, 'bu_bmelv', 'Bundesministerium für Ernährung, Landwirtschaft und Verbraucherschutz', 'http://www.bmelv.de/cln_044/DE/00-Home/__Homepage__node.html__nnn=true', 7, 1),
(8, 'bu_bmz', 'Bundesministerium für wirtschaftliche Zusammenarbeit und Entwicklung', 'http://www.bmz.de/', 8, 1),
(9, 'bu_aa', 'Auswärtiges Amt', 'http://www.auswaertiges-amt.de/', 10, 1),
(10, 'bu_bsh', 'Bundesamt für Seeschifffahrt und Hydrographie', 'http://www.bsh.de/', 11, 1),
(11, 'bu_bvl', 'Bundesamt für Verbraucherschutz und Lebensmittelsicherheit', 'http://www.bvl.bund.de/', 12, 1),
(12, 'bu_bgr', 'Bundesanstalt für Geowissenschaften und Rohstoffe', 'http://www.bgr.bund.de/', 13, 1),
(13, 'bu_bfg', 'Bundesanstalt für Gewässerkunde', 'http://www.bafg.de/', 14, 1),
(14, 'bu_nokis', 'Bundesanstalt für Wasserbau - Dienststelle Hamburg', 'http://www.hamburg.baw.de/', 15, 1),
(15, 'bu_bfr', 'Bundesinstitut für Risikobewertung', 'http://www.bfr.bund.de/', 16, 1),
(16, 'bu_bka', 'Bundeskriminalamt', 'http://www.bka.de/', 17, 1),
(18, 'bu_stba', 'Statistisches Bundesamt', 'http://www.destatis.de/', 19, 1),
(19, 'bu_ble', 'Bundesanstalt für Landwirtschaft und Ernährung', 'http://www.ble.de', 20, 1),
(20, 'bu_bpb', 'Bundeszentrale für politische Bildung', 'http://www.bpb.de/', 21, 1),
(21, 'bu_gtz', 'Deutsche Gesellschaft für Technische Zusammenarbeit (GTZ) GmbH', 'http://www.gtz.de/', 22, 1),
(22, 'bu_dwd', 'Deutscher Wetterdienst', 'http://www.dwd.de/', 23, 1),
(23, 'bu_dlr', 'Deutsches Zentrum für Luft- und Raumfahrt DLR e.V.', 'http://www.dlr.de/', 24, 1),
(24, 'bu_kug', 'Koordinierungsstelle PortalU', 'http://www.kst.portalu.de/', 25, 1),
(25, 'bu_labo', 'Länderarbeitsgemeinschaft Boden LABO', 'http://www.labo-deutschland.de/', 26, 1),
(26, 'bu_lawa', 'Länderarbeitsgemeinschaft Wasser', 'http://www.lawa.de/', 27, 1),
(27, 'bu_laofdh', 'Leitstelle des Bundes für Abwassertechnik, Boden- und Grundwasserschutz, Liegenschaftsinformationssystem Außenanlagen LISA', 'http://www.ofd-hannover.de/la/', 28, 1),
(537, 'he_lbhef', 'Landesbetrieb Hessen-Forst', 'http://www.hessen-forst.de/', 0, 8),
(28, 'bu_bpa', 'Presse- und Informationsamt der Bundesregierung', 'http://www.bundesregierung.de/', 29, 1),
(29, 'bu_blauerengel', 'RAL/Umweltbundesamt Umweltzeichen "Blauer Engel"', 'http://www.blauer-engel.de/', 30, 1),
(30, 'bu_sru', 'Rat von Sachverständigen für Umweltfragen (SRU)', 'http://www.umweltrat.de/', 31, 1),
(31, 'bu_ssk', 'Strahlenschutzkommission', 'http://www.ssk.de/', 32, 1),
(32, 'bu_umk', 'Umweltministerkonferenz', 'http://www.umweltministerkonferenz.de/', 33, 1),
(33, 'bu_wbgu', 'Wissenschaftlicher Beirat der Bundesregierung Globale Umweltveränderungen - WBGU', 'http://www.wbgu.de/', 34, 1),
(35, 'bu_uga', 'Umweltgutachterausschuss (UGA)', 'http://www.uga.de/', 36, 1),
(36, 'bu_co2', 'co2online gGmbH Klimaschutzkampagne', 'http://www.co2online.de/', 37, 1),
(38, 'bw_um', 'Ministerium für Umwelt, Klima und Energiewirtschaft Baden-Württemberg', 'http://www.um.baden-wuerttemberg.de/', 1, 2),
(39, 'bw_mi', 'Innenministerium Baden-Württemberg', 'http://www.innenministerium.baden-wuerttemberg.de/', 2, 2),
(40, 'bw_mlr', 'Ministerium für Ländlichen Raum und Verbraucherschutz Baden-Württemberg', 'http://www.mlr.baden-wuerttemberg.de/', 3, 2),
(41, 'bw_mw', 'Ministerium für Finanzen und Wirtschaft Baden-Württemberg', 'http://www.mfw.baden-wuerttemberg.de/', 4, 2),
(42, 'bw_lu', 'Landesanstalt für Umwelt, Messungen und Naturschutz Baden-Württemberg', 'http://www.lubw.baden-wuerttemberg.de/', 5, 2),
(43, 'bw_lgrb', 'Regierungspräsidium Freiburg - Landesamt für Geologie, Rohstoffe und Bergbau', 'http://www.lgrb.uni-freiburg.de', 6, 2),
(44, 'bw_lvm', 'Landesamt für Geoinformation und Landentwicklung Baden-Württemberg', 'http://www.lv-bw.de', 7, 2),
(514, 'bw_vm', 'Ministerium für Verkehr und Infrastruktur Baden-Württemberg', 'http://www.mvi.baden-wuerttemberg.de/', 139, 2),
(47, 'bw_stla', 'Statistisches Landesamt Baden-Württemberg', 'http://www.statistik-bw.de/', 10, 2),
(560, 'bu_fnb', 'Fachnetzwerk Boden', '', 0, 1),
(49, 'by_sugv', 'Bayerisches Staatsministerium für Umwelt und Gesundheit', 'http://www.stmug.bayern.de/', 1, 3),
(200, 'sn_sms', 'Sächsisches Staatsministerium für Soziales', 'http://www.sms.sachsen.de/', 25, 14),
(51, 'by_lfstad', 'Bayerisches Landesamt für Statistik und Datenverarbeitung', 'http://www.statistik.bayern.de/', 3, 3),
(371, 'bu_bbsr', 'Bundesinstitut für Bau-, Stadt- und Raumforschung', 'http://www.bbsr.bund.de', 0, 1),
(54, 'by_brrhoen', 'Biosphärenreservat Rhön', 'http://www.biosphaerenreservat-rhoen.de/', 6, 3),
(55, 'by_npbayw', 'Nationalpark Bayerischer Wald', 'http://www.nationalpark-bayerischer-wald.de/', 7, 3),
(56, 'by_npbg', 'Nationalpark Berchtesgaden', 'http://www.nationalpark-berchtesgaden.de/', 8, 3),
(57, 'be_senst', 'Senatsverwaltung für Stadtentwicklung und Umwelt', 'http://www.stadtentwicklung.berlin.de/', 1, 4),
(58, 'be_snb', 'Stiftung Naturschutz Berlin', 'http://www.stiftung-naturschutz.de/', 2, 4),
(146, 'bu_portalu', 'PortalU - Das Umweltportal Deutschland', 'http://www.portalu.de', 41, 1),
(147, 'st_lagb', 'Landesamt für Geologie und Bergwesen Sachsen-Anhalt (LAGB)', 'http://www.sachsen-anhalt.de/LPSA/index.php?id=15849', 8, 15),
(60, 'bb_mluv', 'Ministerium für Umwelt, Gesundheit und Verbraucherschutz des Landes Brandenburg', 'http://www.mugv.brandenburg.de', 1, 5),
(61, 'hb_sbu', 'Bremer Senator für Umwelt, Bau, Verkehr und Europa', 'http://www.umwelt.bremen.de', 1, 6),
(62, 'hh_su', 'Behörde für Stadtentwicklung und Umwelt Hamburg', 'http://www.hamburg.de/bsu/', 1, 7),
(63, 'hh_wa', 'Behörde für Wirtschaft, Verkehr und Innovation', 'http://www.hamburg.de/bwvi/', 2, 7),
(64, 'hh_bsg', 'Behörde für Gesundheit und Verbraucherschutz', 'http://www.hamburg.de/bgv/', 3, 7),
(65, 'hh_lgv', 'Landesbetrieb Geoinformation und Vermessung Hamburg', 'http://www.hamburg.de/startseite-landesbetrieb-geoinformation-und-vermessung/', 4, 7),
(66, 'hh_npwatt', 'Nationalparkverwaltung Hamburgisches Wattenmeer', 'http://www.nationalpark-wattenmeer.de/hh', 5, 7),
(67, 'hh_argeelbe', 'Flussgebietsgemeinschaft Elbe', 'http://www.fgg-elbe.de/', 6, 7),
(68, 'hh_sth', 'Statistisches Amt für Hamburg und Schleswig-Holstein', 'http://www.statistik-nord.de/', 7, 7),
(69, 'he_hmulv', 'Hessisches Ministerium für Umwelt, Klimaschutz, Landwirtschaft und Verbraucherschutz', 'http://www.umwelt.hessen.de/', 1, 8),
(70, 'he_hlug', 'Hessisches Landesamt für Umwelt und Geologie', 'http://www.hlug.de/', 2, 8),
(71, 'he_umwelt', 'Umweltatlas Hessen', 'http://atlas.umwelt.hessen.de/', 3, 8),
(72, 'mv_um', 'Ministerium für Landwirtschaft, Umwelt und Verbraucherschutz (LU)', 'http://www.lu.mv-regierung.de/', 1, 9),
(73, 'mv_sm', 'Ministerium für Arbeit, Gleichstellung und Soziales', 'http://www.sozial-mv.de/', 2, 9),
(74, 'mv_lung', 'Landesamt für Umwelt, Naturschutz und Geologie Mecklenburg-Vorpommern (LUNG)', 'http://www.lung.mv-regierung.de/', 3, 9),
(75, 'mv_lfmv', 'Landesforst Mecklenburg-Vorpommern AöR', 'http://www.wald-mv.de/', 4, 9),
(76, 'mv_schaalsee', 'Biosphärenreservat Schaalsee', 'http://www.schaalsee.de/', 5, 9),
(77, 'mv_npmueritz', 'Müritz-Nationalpark', 'http://www.nationalpark-mueritz.de/', 6, 9),
(78, 'mv_nvblmv', 'Nationalpark Vorpommersche Boddenlandschaft', 'http://www.nationalpark-vorpommersche-boddenlandschaft.de/', 7, 9),
(79, 'mv_fhstr', 'Fachhochschule Stralsund', 'http://www.fh-stralsund.de/', 8, 9),
(80, 'mv_fhnb', 'Hochschule Neubrandenburg', 'http://www.fh-nb.de/', 9, 9),
(81, 'mv_hswi', 'Hochschule Wismar', 'http://www.hs-wismar.de/', 10, 9),
(82, 'mv_iow', 'Institut für Ostseeforschung Warnemünde', 'http://www.io-warnemuende.de/', 11, 9),
(83, 'mv_unigr', 'Universität Greifswald', 'http://www.uni-greifswald.de/', 12, 9),
(84, 'mv_uniro', 'Universität Rostock', 'http://www.uni-rostock.de/', 13, 9),
(85, 'ni_mu', 'Niedersächsisches Ministerium für Umwelt und Klimaschutz', 'http://www.mu.niedersachsen.de/', 1, 10),
(86, 'ni_mw', 'Niedersächsisches Ministerium für Wirtschaft, Arbeit und Verkehr', 'http://www.mw.niedersachsen.de/', 2, 10),
(87, 'ni_ms', 'Niedersächsisches Ministerium für Soziales, Frauen, Familie, Gesundheit und Integration', 'http://www.ms.niedersachsen.de/', 3, 10),
(88, 'ni_mi', 'Niedersächsisches Ministerium für Inneres und Sport', 'http://www.mi.niedersachsen.de/', 4, 10),
(89, 'ni_ml', 'Niedersächsisches Ministerium für Ernährung, Landwirtschaft, Verbraucherschutz und Landesentwicklung', 'http://www.ml.niedersachsen.de/', 5, 10),
(90, 'ni_nlwkn', 'Niedersächsischer Landesbetrieb für Wasserwirtschaft, Küsten- und Naturschutz', 'http://www.nlwkn.niedersachsen.de/', 6, 10),
(91, 'ni_lbeg', 'Niedersächsisches Landesamt für Bergbau, Energie und Geologie', 'http://www.lbeg.niedersachsen.de', 7, 10),
(93, 'ni_laves', 'Niedersächsisches Landesamt für Verbraucherschutz und Lebensmittelsicherheit', 'http://www.laves.niedersachsen.de/', 9, 10),
(94, 'ni_lga', 'Niedersächsisches Landesgesundheitsamt', 'http://www.nlga.niedersachsen.de/', 10, 10),
(95, 'ni_sbv', 'Niedersächsische Landesbehörde für Straßenbau und Verkehr', 'http://www.strassenbau.niedersachsen.de/', 11, 10),
(96, 'ni_nna', 'Alfred Toepfer Akademie für Naturschutz', 'http://www.nna.niedersachsen.de/', 12, 10),
(97, 'ni_gll', 'Behörden für Geoinformation, Landentwicklung und Liegenschaften Niedersachsen', 'http://www.gll.niedersachsen.de', 13, 10),
(99, 'ni_lgn', 'Landesvermessung und Geobasisinformation Niedersachsen', 'http://www.lgn.niedersachsen.de/', 15, 10),
(100, 'ni_lwkh', 'Landwirtschaftskammer Niedersachsen', 'http://www.lwk-niedersachsen.de/', 16, 10),
(103, 'ni_gaa', 'Niedersächsische Gewerbeaufsicht', 'http://www.gewerbeaufsicht.niedersachsen.de', 19, 10),
(105, 'nw_munlv', 'Ministerium für Umwelt und Naturschutz, Landwirtschaft und Verbraucherschutz des Landes Nordrhein-Westfalen', 'http://www.umwelt.nrw.de/', 1, 11),
(176, 'mv_im', 'Ministerium für Inneres und Sport', 'http://www.mv-regierung.de/im/', 14, 9),
(177, 'mv_am', 'Ministerium für Wirtschaft, Bau und Tourismus', 'http://www.wm.mv-regierung.de/index.htm', 15, 9),
(108, 'nw_gd', 'Geologischer Dienst Nordrhein-Westfalen', 'http://www.gd.nrw.de/', 4, 11),
(111, 'nw_ldvst', 'Landesamt für Datenverarbeitung und Statistik Nordrhein-Westfalen', 'http://www.ugrdl.de/index.html', 7, 11),
(112, 'rp_mufv', 'Ministerium für Umwelt, Landwirtschaft, Ernährung, Weinbau und Forsten Rheinland-Pfalz (MULEWF) ', 'http://www.mulewf.rlp.de/', 1, 12),
(115, 'rp_forst', 'Landesforsten Rheinland-Pfalz', 'http://www.wald-rlp.de/', 2, 12),
(116, 'rp_lzu', 'Landeszentrale für Umweltaufklärung Rheinland-Pfalz', 'http://www.umdenken.de/', 3, 12),
(117, 'sl_mfu', 'Ministerium für Umwelt und Verbraucherschutz des Saarland', 'http://www.saarland.de/ministerium_umwelt_verbraucherschutz.htm', 1, 13),
(118, 'sl_lua', 'Landesamt für Umwelt- und Arbeitsschutz des Saarlandes', 'http://www.lua.saarland.de/', 2, 13),
(119, 'sn_smul', 'Sächsisches Staatsministerium für Umwelt und Landwirtschaft', 'http://www.smul.sachsen.de/smul/index.html', 0, 14),
(263, 'bu_sgd', 'Staatliche Geologische Dienste Deutschlands', 'http://www.infogeo.de', 0, 1),
(121, 'sn_lanu', 'Sächsische Landesstiftung Natur und Umwelt', 'http://www.lanu.de/templates/intro.php', 1, 14),
(122, 'st_mlu', 'Ministerium für Landwirtschaft und Umwelt Sachsen-Anhalt (MLU)', 'http://www.mlu.sachsen-anhalt.de', 1, 15),
(496, 'rp_luwg', 'Landesamt für Umwelt, Wasserwirtschaft und Gewerbeaufsicht Rheinland-Pfalz', 'http://www.luwg.rlp.de/', 0, 12),
(123, 'st_lau', 'Landesamt für Umweltschutz Sachsen-Anhalt (LAU)', 'http://www.sachsen-anhalt.de/LPSA/index.php?id=lau', 7, 15),
(124, 'st_lue', 'Luftüberwachungssystem Sachsen-Anhalt (LÜSA)', 'http://www.mu.sachsen-anhalt.de/lau/luesa/', 22, 15),
(125, 'st_unimd', 'Otto-von-Guericke Universität Magdeburg', 'http://www.uni-magdeburg.de/', 23, 15),
(126, 'sh_munl', 'Ministerium für Landwirtschaft, Umwelt und ländliche Räume des Landes Schleswig-Holstein', 'http://www.schleswig-holstein.de/MLUR/DE/MLUR__node.html', 1, 16),
(127, 'sh_lanu', 'Landesamt für Landwirtschaft, Umwelt und ländliche Räume Schleswig-Holstein', 'http://www.schleswig-holstein.de/LLUR/DE/LLUR__node.html', 2, 16),
(128, 'sh_luesh', 'Lufthygienische Überwachung Schleswig-Holstein', 'http://www.umwelt.schleswig-holstein.de/?196', 3, 16),
(129, 'sh_kfue', 'Kernreaktorfernüberwachung Schleswig-Holstein', 'http://www.kfue-sh.de/', 4, 16),
(130, 'sh_umweltaka', 'Bildungszentrum für Natur, Umwelt und ländliche Räume der Landes Schleswig-Holstein', 'http://www.umweltakademie-sh.de/', 5, 16),
(131, 'sh_npa', 'Nationalpark Schleswig-Holsteinisches Wattenmeer', 'http://www.nationalpark-wattenmeer.de/sh', 6, 16),
(132, 'th_tmlnu', 'Thüringer Ministerium für Landwirtschaft, Forsten, Umwelt und Naturschutz', 'http://www.thueringen.de/th8/tmlfun/', 1, 17),
(133, 'th_tlug', 'Thüringer Landesanstalt für Umwelt und Geologie', 'http://www.tlug-jena.de/de/tlug/', 2, 17),
(138, 'rp_lua', 'Landesuntersuchungsamt Rheinland-Pfalz', 'http://www.lua.rlp.de/', 5, 12),
(139, 'bw_saa', 'SAA Sonderabfallagentur Baden-Württemberg', 'http://www.saa.de/', 13, 2),
(140, 'bw_rp', 'Regierungspräsidien Baden-Württemberg', 'http://www.rp.baden-wuerttemberg.de/', 14, 2),
(141, 'bw_rps', 'Regierungspräsidium Stuttgart', 'http://www.rp.baden-wuerttemberg.de/servlet/PB/menu/1007480/index.html', 15, 2),
(142, 'bu_lai', 'Bund/Länder Arbeitsgemeinschaft für Immissionsschutz - LAI', 'http://www.lai-immissionsschutz.de', 39, 1),
(369, 'hb_gi', 'GeoInformation Bremen', 'http://www.geo.bremen.de', 0, 6),
(145, 'he_hvbg', 'Hessische Verwaltung für Bodenmanagement und Geoinformation', 'http://www.hvbg.hessen.de/', 5, 8),
(150, 'sn_lfp', 'Staatsbetrieb Sachsenforst', 'http://www.sachsenforst.de', 5, 14),
(151, 'sn_np-saechsische-schweiz', 'Nationalpark Sächsische Schweiz', 'http://www.nationalpark-saechsische-schweiz.de/startseite/index.php', 6, 14),
(152, 'sn_ltv', 'Landestalsperrenverwaltung Sachsen', 'http://www.smul.sachsen.de/de/wu/organisation/staatsbetriebe/ltv/index_start.html', 7, 14),
(153, 'sn_brv', 'Biosphärenreservatsverwaltung Oberlausitzer Heide- und Teichlandschaft', 'http://www.biosphaerenreservat-oberlausitz.de/index.html', 8, 14),
(154, 'sn_statistik', 'Statistisches Landesamt Sachsen', 'http://www.statistik.sachsen.de/', 26, 14),
(155, 'st_ms', 'Ministerium für Arbeit und Soziales Sachsen-Anhalt (MS) ', 'http://www.ms.sachsen-anhalt.de', 2, 15),
(156, 'st_mw', 'Ministerium für Wissenschaft und Wirtschaft  Sachsen-Anhalt (MW)', 'http://www.mw.sachsen-anhalt.de', 3, 15),
(157, 'st_mlv', 'Ministerium für Landesentwicklung und Verkehr (MLV)', 'http://www.mlv.sachsen-anhalt.de', 4, 15),
(158, 'st_mi', 'Ministerium des Innern Sachsen-Anhalt (MI)', 'http://www.mi.sachsen-anhalt.de', 5, 15),
(159, 'st_mj', 'Ministerium der Justiz und Gleichstellung Sachsen-Anhalt (MJ)', 'http://www.mj.sachsen-anhalt.de', 6, 15),
(160, 'st_lvermgeo', 'Landesamt für Vermessung und Geoinformationen (LVermGeo)', 'http://www.lvermgeo.sachsen-anhalt.de', 9, 15),
(161, 'st_stala', 'Statistisches Landesamt Sachsen-Anhalt (StaLa)', 'http://www.stala.sachsen-anhalt.de', 10, 15),
(162, 'st_lav', 'Landesamt für Verbraucherschutz Sachsen-Anhalt (LAV)', 'http://www.verbraucherschutz.sachsen-anhalt.de', 11, 15),
(163, 'st_lfa', 'Landesamt für Denkmalpflege und Archäologie (LDA)', 'http://www.archlsa.de', 12, 15),
(164, 'st_laf', 'Landesanstalt für Altlastenfreistellung Sachsen-Anhalt (LAF)', 'http://www.laf-lsa.de', 13, 15),
(165, 'st_llfg', 'Landesanstalt für Landwirtschaft, Forsten und Gartenbau Sachsen-Anhalt (LLFG)', 'http://www.llg-lsa.de', 14, 15),
(166, 'st_lhw', 'Landesbetrieb für Hochwasserschutz und Wasserwirtschaft Sachsen-Anhalt (LHW)', 'http://www.sachsen-anhalt.de/LPSA/index.php?id=13427', 15, 15),
(167, 'st_talsperren', 'Talsperrenbetrieb Sachsen-Anhalt (TSB-LSA)', 'http://www.talsperren-lsa.de', 16, 15),
(168, 'st_landesforstbetrieb', 'Landesforstbetrieb Sachsen-Anhalt (LFB)', 'http://www.landesforstbetrieb.sachsen-anhalt.de', 17, 15),
(169, 'st_lvwa', 'Landesverwaltungsamt Sachsen-Anhalt (LVwA)', 'http://www.landesverwaltungsamt.sachsen-anhalt.de', 18, 15),
(170, 'st_bioreskarstsuedharz', 'Biosphärenreservat Karstlandschaft Südharz', 'http://www.bioreskarstsuedharz.de', 19, 15),
(171, 'st_biosphaerenreservatmittlereelbe', 'Biosphärenreservat Mittelelbe/Flusslandschaft Elbe', 'http://www.biosphaerenreservatmittlereelbe.de', 20, 15),
(172, 'st_nationalpark-harz', 'Nationalparkverwaltung Harz', 'http://www.nationalpark-harz.de', 21, 15),
(173, 'be_senges', 'Senatsverwaltung für Gesundheit, Umwelt und Verbraucherschutz Berlin', 'http://www.berlin.de/sen/umwelt/index.shtml', 3, 4),
(174, 'nw_lanuv', 'Landesamt für Natur, Umwelt und Verbraucherschutz NRW  (LANUV)', 'http://www.lanuv.nrw.de/', 8, 11),
(202, 'bu_bdj', 'Bundesministerium der Justiz', 'http://www.bmj.bund.de', 0, 1),
(178, 'mv_jm', 'Justizministerium Mecklenburg-Vorpommern', 'http://www.jm.mv-regierung.de/', 16, 9),
(179, 'mv_bm', 'Ministerium für Bildung, Wissenschaft und Kultur', 'http://www.bm.regierung-mv.de', 17, 9),
(180, 'mv_vm', 'Ministerium für Energie, Infrastruktur und Landesentwicklung', 'http://www.mv-regierung.de/vm/', 18, 9),
(181, 'mv_sta', 'Statistisches Amt Mecklenburg-Vorpommern', 'http://www.statistik-mv.de/', 19, 9),
(511, 'sh_masg', 'Ministerium für Arbeit, Soziales und Gesundheit', 'http://www.schleswig-holstein.de/MASG/DE/MASG_node.html', 0, 16),
(184, 'mv_stnb', 'Staatliches Amt für Landwirtschaft und Umwelt Mecklenburgische Seenplatte (StALU MS)', 'http://www.mv-regierung.de/staeun/neubrandenburg/', 21, 9),
(185, 'mv_sthst', 'Staatliches Amt für Landwirtschaft und Umwelt Vorpommern (StALU VP)', 'http://www.mv-regierung.de/staeun/stralsund_n/', 22, 9),
(186, 'mv_sthro', 'Staatliches Amt für Landwirtschaft und Umwelt Mittleres Mecklenburg (StALU MM)', 'http://www.mv-regierung.de/staeun/rostock/', 23, 9),
(187, 'mv_stsn', 'Staatliches Amt für Landwirtschaft und Umwelt Westmecklenburg (StALU WM)', 'http://www.mv-regierung.de/staeun/schwerin/', 24, 9),
(188, 'mv_lallf', 'Landesamt für Landwirtschaft, Lebensmittelsicherheit und Fischerei', 'http://www.lallf.de/', 25, 9),
(189, 'mv_afrl', 'Ämter für Raumordnung und Landesplanung Mecklenburg-Vorpommern', 'http://cms.mvnet.de/cms2/AFRL_prod/AFRL/index.jsp', 26, 9),
(196, 'bu_fli', 'Friedrich-Löffler-Institut', 'http://www.fli.bund.de', 42, 1),
(197, 'bu_nrat', 'Rat für Nachhaltige Entwicklung', 'http://www.nachhaltigkeitsrat.de', 43, 1),
(195, 'ni_fho', 'Fachhochschule Osnabrück', 'http://www.hs-osnabrueck.de/', 22, 10),
(194, 'be_npb', 'Naturpark Barnim', 'http://www.np-barnim.de/', 4, 4),
(198, 'bu_baw', 'Bundesanstalt für Wasserbau', 'http://www.baw.de/', 0, 1),
(199, 'by_lumsch', 'Bayerisches Landesamt für Umwelt', 'http://www.lfu.bayern.de', 9, 3),
(201, 'sn_lus', 'Landesuntersuchungsanstalt Sachsen', 'http://www.lua.sachsen.de/', 16, 14),
(204, 'by_sdj', 'Bayerisches Staatsministerium der Justiz', 'http://www.justiz.bayern.de/', 0, 3),
(494, 'bu_dena', 'Deutsche Energie-Agentur GmbH (dena)', 'http://www.dena.de/', 0, 1),
(206, 'hh_bserv', 'Hamburger Gerichte', 'http://justiz.hamburg.de/gerichte/', 0, 7),
(207, 'hh_justiz', 'Behörde für Justiz und Gleichstellung', 'http://www.hamburg.de/justizbehoerde/', 0, 7),
(208, 'he_stk', 'Hessische Staatskanzlei', 'http://www.stk.hessen.de/', 0, 8),
(209, 'ni_stk', 'Niedersächsische Staatskanzlei', 'http://www.stk.niedersachsen.de', 0, 10),
(210, 'nw_im', 'Innenministerium des Landes Nordrhein-Westfalen', 'http://www.im.nrw.de/', 0, 11),
(212, 'sl_justiz', 'Ministerium der Justiz des Saarlandes', 'http://www.saarland.de/ministerium_justiz.htm', 0, 13),
(370, 'bu_dbu', 'Deutsche Bundesstiftung Umwelt', 'http://www.dbu.de/', 0, 1),
(214, 'sh_lreg', 'Landesregierung Schleswig-Holstein', 'http://www.schleswig-holstein.de', 0, 16),
(215, 'th_geo', 'Thüringer Landesamt für Vermessung und Geoinformation', 'http://www.thueringen.de/th9/tlvermgeo/index.aspx', 0, 17),
(216, 'th_justiz', 'Justizministerium des Freistaats Thüringen', 'http://www.thueringen.de/de/justiz/', 0, 17),
(217, 'mv_bergamt', 'Bergamt Mecklenburg-Vorpommern', 'http://www.bergamt-mv.de/', 0, 9),
(218, 'mv_npjasm', 'Nationalpark Jasmund', 'http://www.nationalpark-vorpommersche-boddenlandschaft.de/jasmund/', 0, 9),
(219, 'mv_natnat', 'Nationale Naturlandschaften', 'http://www.natur-mv.de/', 0, 9),
(221, 'bu_bmwi', 'Bundesministerium für Wirtschaft und Technologie', 'http://www.bmwi.de/', 0, 1),
(222, 'nw_brarn', 'Bezirksregierung Arnsberg', 'http://www.bezreg-arnsberg.nrw.de/', 0, 11),
(223, 'nw_brdet', 'Bezirksregierung Detmold', 'http://www.bezreg-detmold.nrw.de', 0, 11),
(224, 'nw_brdue', 'Bezirksregierung Düsseldorf', 'http://www.bezreg-duesseldorf.nrw.de', 0, 11),
(225, 'nw_brkoe', 'Bezirksregierung Köln', 'http://www.bezreg-koeln.nrw.de', 0, 11),
(226, 'nw_brmue', 'Bezirksregierung Münster', 'http://www.bezreg-muenster.nrw.de', 0, 11),
(227, 'nw_lbwuh', 'Landesbetrieb Wald und Holz NRW', 'http://www.wald-und-holz.nrw.de', 0, 11),
(228, 'nw_lwk', 'Landwirtschaftskammer Nordrhein-Westfalen', 'http://www.landwirtschaftskammer.de', 0, 11),
(229, 'rp_sgds', 'Struktur- und Genehmigungsdirektion Süd Rheinland-Pfalz', 'http://www.sgdsued.rlp.de', 6, 12),
(230, 'rp_sgdn', 'Struktur- und Genehmigungsdirektion Nord Rheinland-Pfalz', 'http://www.sgdnord.rlp.de', 7, 12),
(231, 'rp_mwvlw', 'Ministerium für Wirtschaft, Klimaschutz, Energie und Landesplanung Rheinland-Pfalz', 'http://www.mwkel.rlp.de/Startseite/', 8, 12),
(232, 'rp_lgb', 'Landesamt für Geologie und Bergbau Rheinland-Pfalz', 'http://www.lgb-rlp.de', 9, 12),
(233, 'rp_lbm', 'Landesbetrieb Mobilität Rheinland-Pfalz', 'http://www.lbm.rlp.de', 10, 12),
(234, 'rp_ism', 'Ministerium des Innern, für Sport und Infrastruktur Rheinland-Pfalz', 'http://www.isim.rlp.de/', 11, 12),
(235, 'rp_lvermgeo', 'Landesamt für Vermessung und Geobasisinformationen Rheinland-Pfalz', 'http://www.lvermgeo.rlp.de', 12, 12),
(236, 'rp_sla', 'Statistisches Landesamt Rheinland-Pfalz', 'http://www.statistik.rlp.de', 13, 12),
(237, 'rp_lfks', 'Feuerwehr- und Katastrophenschutzschule Rheinland-Pfalz', 'http://www.lfks-rlp.de', 14, 12),
(238, 'rp_masgff', 'Ministerium für Soziales, Arbeit, Gesundheit und Demografie Rheinland-Pfalz', 'http://msagd.rlp.de/', 15, 12),
(240, 'bu_baua', 'Bundesanstalt für Arbeitsschutz und Arbeitsmedizin', 'http://www.baua.bund.de', 0, 1),
(241, 'ni_lk_row', 'Landkreis Rotenburg-Wümme', 'http://www.lk-row.de', 0, 10),
(242, 'ni_lk_nh', 'Landkreis Northeim', 'http://www.landkreis-northeim.de/', 0, 10),
(243, 'ni_reg-hann', 'Region Hannover', 'http://www.hannover.de', 0, 10),
(244, 'ni_lhst-hann', 'Landeshauptstadt Hannover', 'http://www.hannover.de', 0, 10),
(245, 'ni_wb', 'Stadt Wolfsburg', 'http://www.wolfsburg.de', 0, 10),
(246, 'ni_li', 'Stadt Lingen', 'http://www.lingen.de', 0, 10),
(247, 'ni_lk_row_gb', 'Gemeinde Gnarrenburg', 'http://www.gnarrenburg.de', 0, 10),
(248, 'ni_lk_row_se', 'Samtgemeinde Selsingen', 'http://www.selsingen.de', 0, 10),
(249, 'ni_lk_row_si', 'Samtgemeinde Sittensen', 'http://www.sittensen.de', 0, 10),
(250, 'ni_lk_row_rw', 'Stadt Rotenburg (Wümme)', 'http://www.rotenburg-wuemme.de', 0, 10),
(251, 'ni_lk_row_vi', 'Stadt Visselhövede', 'http://www.visselhoevede.de', 0, 10),
(252, 'ni_lk_row_so', 'Samtgemeinde Sottrum', 'http://www.sottrum.de', 0, 10),
(253, 'ni_lk_row_ze', 'Samtgemeinde Zeven', 'http://www.zeven.de', 0, 10),
(254, 'ni_lk_row_fi', 'Samtgemeinde Fintel', 'http://www.fintel.de', 0, 10),
(255, 'ni_lk_row_bo', 'Samtgemeinde Bothel', 'http://www.bothel.de', 0, 10),
(256, 'ni_lk_row_ta', 'Samtgemeinde Tarmstedt', 'http://www.tarmstedt.de', 0, 10),
(257, 'ni_lk_row_bv', 'Stadt Bremervörde', 'http://www.bremervoerde.de', 0, 10),
(258, 'ni_lk_row_sc', 'Gemeinde Scheeßel', 'http://www.scheessel.de', 0, 10),
(259, 'bw_fva', 'Forstliche Versuchs- und Forschungsanstalt Baden-Württemberg', 'http://www.fva-bw.de', 16, 2),
(260, 'rp_kgstgdi', 'Kompetenz- und Geschäftsstelle GDI Rheinland-Pfalz', 'http://www.geoportal.rlp.de/portal/ueber-uns/kompetenz-und-geschaeftsstelle.html', 0, 12),
(562, 'ni_st_rinteln', 'Stadt Rinteln', 'http://www.rinteln.de/', 0, 10),
(262, 'sn_lfulg', 'Sächsisches Landesamt für Umwelt, Landwirtschaft und Geologie', 'http://www.umwelt.sachsen.de/lfulg/', 3, 14),
(264, 'ni_st_bu', 'Stadt Buchholz i. d. N.', 'http://www.buchholz.de', 0, 10),
(265, 'ni_lk_ue', 'Landkreis Uelzen', 'http://www.uelzen.de', 0, 10),
(266, 'ni_st_lg', 'Hansestadt Lüneburg', 'http://www.lueneburg.de', 0, 10),
(267, 'ni_sg_ldf', 'Samtgemeinde Lachendorf', 'http://www.lachendorf.de', 0, 10),
(268, 'ni_st_ce', 'Stadt Celle', 'http://www.celle.de', 0, 10),
(269, 'ni_lk_ce', 'Landkreis Celle', 'http://www.landkreis-celle.de', 0, 10),
(270, 'ni_lk_ld', 'Landkreis Lüchow-Dannenberg', 'http://www.luechow-dannenberg.de', 0, 10),
(271, 'ni_sg_oh', 'Samtgemeinde Oberharz', 'http://www.samtgemeinde-oberharz.de', 0, 10),
(272, 'ni_st_cux', 'Stadt Cuxhaven', 'http://www.cuxhaven.de', 0, 10),
(273, 'ni_st_ach', 'Stadt Achim', 'http://www.achim.de', 0, 10),
(274, 'ni_lk_ost', 'Landkreis Osterholz', 'http://www.landkreis-osterholz.de', 0, 10),
(275, 'ni_lk_ver', 'Landkreis Verden', 'http://www.landkreis-verden.de', 0, 10),
(276, 'ni_st_ver', 'Stadt Verden', 'http://www.verden.de', 0, 10),
(277, 'ni_lk_wolf', 'Landkreis Wolfenbüttel', 'http://www.lk-wf.de', 0, 10),
(278, 'ni_st_salz', 'Stadt Salzgitter', 'http://www.salzgitter.de', 0, 10),
(279, 'ni_st_koe', 'Stadt Königslutter', 'http://www.koenigslutter.de', 0, 10),
(280, 'ni_lk_pe', 'Landkreis Peine', 'http://www.landkreis-peine.de', 0, 10),
(281, 'ni_lk_helm', 'Landkreis Helmstedt', 'http://www.landkreis-helmstedt.de', 0, 10),
(282, 'ni_st_goe', 'Stadt Göttingen', 'http://www.goettingen.de', 0, 10),
(283, 'ni_st_oster', 'Stadt Osterode', 'http://www.osterode.de', 0, 10),
(284, 'ni_lk_gos', 'Landkreis Goslar', 'http://www.landkreis-goslar.de', 0, 10),
(285, 'ni_lk_goe', 'Landkreis Göttingen', 'http://www.landkreisgoettingen.de', 0, 10),
(286, 'ni_st_see', 'Stadt Seelze', 'http://www.stadt-seelze.de', 0, 10),
(287, 'ni_lk_oster', 'Landkreis Osterode', 'http://www.landkreis-osterode.de', 0, 10),
(288, 'ni_st_lang', 'Stadt Langelsheim', 'http://www.langelsheim.de', 0, 10),
(289, 'bu_bbr', 'Bundesamt für Bauwesen und Raumordnung', 'http://www.bbr.bund.de', 0, 1),
(290, 'ni_lk_hi', 'Landkreis Hildesheim', 'http://www.landkreishildesheim.de', 0, 10),
(291, 'ni_st_wu', 'Stadt Wunstorf', 'http://www.wunstorf.de', 0, 10),
(292, 'ni_st_neust', 'Stadt Neustadt a. Rbge.', 'http://www.neustadt-a-rbge.de', 0, 10),
(293, 'ni_lk_schaum', 'Landkreis Schaumburg', 'http://www.landkreis-schaumburg.de', 0, 10),
(294, 'ni_sg_sachs', 'Samtgemeinde Sachsenhagen', 'http://www.sachsenhagen.de', 0, 10),
(295, 'ni_st_ham', 'Stadt Hameln', 'http://www.hameln.de', 0, 10),
(296, 'ni_lk_ham', 'Landkreis Hameln-Pyrmont', 'http://www.hameln-pyrmont.de', 0, 10),
(297, 'ni_lk_nien', 'Landkreis Nienburg/Weser', 'http://www.kreis-ni.de', 0, 10),
(298, 'ni_st_bars', 'Stadt Barsinghausen', 'http://www.stadt-barsinghausen.de', 0, 10),
(299, 'ni_st_spri', 'Stadt Springe', 'http://www.springe.de', 0, 10),
(300, 'ni_lk_weser', 'Landkreis Wesermarsch', 'http://www.lkbra.de', 0, 10),
(301, 'ni_lk_clop', 'Landkreis Cloppenburg', 'http://www.lkclp.de', 0, 10),
(302, 'ni_lk_ammer', 'Landkreis Ammerland', 'http://www.ammerland.de', 0, 10),
(303, 'ni_st_jever', 'Stadt Jever', 'http://www.stadt-jever.de', 0, 10),
(304, 'ni_lk_fries', 'Landkreis Friesland', 'http://www.landkreis-friesland.de', 0, 10),
(305, 'ni_lk_old', 'Landkreis Oldenburg', 'http://www.oldenburg-kreis.de', 0, 10),
(306, 'ni_lk_leer', 'Landkreis Leer', 'http://www.lkleer.de', 0, 10),
(307, 'ni_st_wil', 'Stadt Wilhelmshaven', 'http://www.wilhelmshaven.de', 0, 10),
(308, 'ni_lk_ems', 'Landkreis Emsland', 'http://www.emsland.de', 0, 10),
(309, 'ni_st_pap', 'Stadt Papenburg', 'http://www.papenburg.de', 0, 10),
(310, 'ni_lk_osn', 'Landkreis Osnabrück', 'http://www.lkos.de', 0, 10),
(311, 'ni_st_bram', 'Stadt Bramsche', 'http://www.stadt-bramsche.de', 0, 10),
(312, 'ni_ge_belm', 'Gemeinde Belm', 'http://www.belm.de', 0, 10),
(313, 'ni_st_bers', 'Stadt Bersenbrück', 'http://www.bersenbrueck.de', 0, 10),
(314, 'ni_lk_vech', 'Landkreis Vechta', 'http://www.landkreis-vechta.de', 0, 10),
(316, 'sn_av', 'Anglerverband Sachsen e.V.', 'http://www.av-sachsen.de/data/home.htm', 49, 14),
(317, 'sn_bund', 'Bund für Umwelt und Naturschutz Deutschland (BUND) - Landeverband Sachsen e.V.', 'http://www.bund-sachsen.de', 44, 14),
(318, 'sn_gls', 'Grüne Liga Sachsen e.V.', 'http://www.grueneliga.de/sachsen', 45, 14),
(319, 'bu_dekade', 'UN-Dekade - Bildung für nachhaltige Entwicklung', 'http://www.bne-portal.de', 0, 1),
(320, 'bu_ki', 'Gemeinnützigen Kinderumwelt GmbH', 'http://www.allum.de/', 0, 1),
(322, 'sn_dd', 'Stadt Dresden', 'http://www.dresden.de/', 2, 14),
(324, 'sn_sgv', 'Sächsische Gestütsverwaltung', 'http://www.smul.sachsen.de/sgv/index.html', 11, 14),
(325, 'sn_burgstaedt', 'Stadt Burgstädt', 'http://www.burgstaedt.de/', 12, 14),
(326, 'sn_chemnitz', 'Stadt Chemnitz', 'http://www.chemnitz.de/', 13, 14),
(497, 'rp_jm', 'Ministerium der Justiz und für Verbraucherschutz Rheinland Pfalz', 'http://www.mjv.rlp.de/Startseite/', 0, 12),
(328, 'sn_lk_mittelsachsen', 'Landkreis Mittelsachsen', 'http://www.landkreis-mittelsachsen.de/', 65, 14),
(330, 'sn_lk_zwickau', 'Landkreis Zwickau', 'http://www.landkreis-zwickau.de/', 18, 14),
(544, 'ni_bubmj', 'Bundesministerium der Justiz', '', 0, 10),
(333, 'sn_smwa', 'Sächsisches Staatsministerium für Wirtschaft und Arbeit', 'http://www.smwa.sachsen.de/', 21, 14),
(335, 'sn_ldc', 'Landesdirektion Chemnitz', 'http://www.ldc.sachsen.de/', 23, 14),
(336, 'sn_ldl', 'Landesdirektion Leipzig', 'http://www.ldl.sachsen.de/', 24, 14),
(337, 'sn_tu_dd', 'Technische Universität Dresden', 'http://www.tu-dresden.de/', 27, 14),
(338, 'sn_tu_fg', 'Technische Universität Bergakademie Freiberg', 'http://www.tu-freiberg.de/', 28, 14),
(339, 'sn_uni_l', 'Universität Leipzig', 'http://www.uni-leipzig.de/', 29, 14),
(340, 'sn_tu_c', 'Technische Universität Chemnitz', 'http://www.tu-chemnitz.de/', 30, 14),
(341, 'sn_l', 'Stadt Leipzig', 'http://www.leipzig.de/', 32, 14),
(342, 'sn_smi', 'Sächsisches Staatsministerium des Innern', 'http://www.smi.sachsen.de/', 33, 14),
(345, 'sn_aue', 'Stadt Aue', 'http://www.aue.de/', 34, 14),
(346, 'sn_coswig', 'Stadt Coswig', 'http://www.coswig.de/', 35, 14),
(347, 'sn_freital', 'Stadt Freital', 'http://www.freital.de/', 36, 14),
(348, 'sn_glauchau', 'Stadt Glauchau', 'http://www.glauchau.de/', 37, 14),
(349, 'sn_goerlitz', 'Stadt Görlitz', 'http://www.goerlitz.de/', 38, 14),
(350, 'sn_limbach-oberfrohna', 'Stadt Limbach-Oberfrohna', 'http://www.limbach-oberfrohna.de/', 39, 14),
(351, 'sn_limbach-oberfrohna', 'Stadt Limbach-Oberfrohna', 'http://www.limbach-oberfrohna.de/', 39, 14),
(352, 'sn_markkleeberg', 'Stadt Markkleeberg', 'http://www.markkleeberg.de', 40, 14),
(353, 'sn_lk_meissen', 'Landkreis Meißen', 'http://www.kreis-meissen.org/', 41, 14),
(354, 'sn_lk_vogtland', 'Landkreis Vogtland', 'http://www.vogtlandkreis.de/', 42, 14),
(355, 'sn_nabu', 'Naturschutzbund Deutschlands (NABU) - Landesverband Sachsen', 'http://www.nabu-sachsen.de/', 43, 14),
(356, 'sn_lsh', 'Landesverein Sächsischer Heimatschutz e.V.', 'http://www.saechsischer-heimatschutz.de/', 46, 14),
(357, 'sn_sdw', 'Schutzgemeinschaft Deutscher Wald - Landesverband Sachsen', 'http://www.sdw-sachsen.de', 47, 14),
(375, 'bw_cvua', 'Chemische und Veterinäruntersuchungsämter (CVUA)', 'http://www.untersuchungsaemter-bw.de', 17, 2),
(360, 'sn_grimma', 'Stadt Grimma', 'http://www.grimma.de/', 52, 14),
(361, 'sn_naturparke', 'Naturparke in Sachsen', 'http://www.naturparke.de', 53, 14),
(362, 'sn_smk', 'Sächsisches Staatsministerium für Kultus', 'http://www.sachsen-macht-schule.de/smk/', 54, 14),
(363, 'sn_saena', 'Sächsische Energieagentur - SAENA GmbH', 'http://www.saena.de/', 55, 14),
(364, 'sn_lk_bautzen', 'Landkreis Bautzen', 'http://www.landkreis-bautzen.de/', 59, 14),
(365, 'sn_lk_nordsachsen', 'Landkreis Nordsachsen', 'http://www.landkreis-nordsachsen.de/', 63, 14),
(366, 'sn_lk_leipzig', 'Landkreis Leipzig', 'http://www.landkreisleipzig.de/', 69, 14),
(367, 'sn_freiberg', 'Stadt Freiberg', 'http://www.freiberg.de', 62, 14),
(368, 'sn_lk_goerlitz', 'Landkreis Görlitz', 'http://www.kreis-goerlitz.de/', 67, 14),
(372, 'ni_nbue', 'Niedersächsische Bingostiftung für Umwelt und Entwicklungszusammenarbeit', 'http://www.umweltstiftung.niedersachsen.de', 0, 10),
(373, 'ni_mf', 'Niedersächsisches Finanzministerium', 'http://www.mf.niedersachsen.de/', 5, 10),
(374, 'rp_geoportal', 'Geoportal Rheinland-Pfalz', 'http://www.geoportal.rlp.de/', 0, 12),
(492, 'bw_nvbw', 'Nahverkehrsgesellschaft Baden-Württemberg', 'http://www.nvbw.de/', 138, 2),
(376, 'bw_nsz', 'Naturschutzzentren Baden-Württemberg', 'http://www.naturschutzzentren-bw.de', 18, 2),
(378, 'bw_rpk', 'Regierungspräsidium Karlsruhe', 'http://www.rp-karlsruhe.de', 19, 2),
(379, 'bw_rpt', 'Regierungspräsidium Tübingen', 'http://www.rp-tuebingen.de ', 20, 2),
(513, 'hb_mbhv', 'Magistrat Bremerhaven', 'http://www.bremerhaven.de/stadt-und-politik/politik/magistrat/', 0, 6),
(381, 'bw_mj', 'Justizministerium Baden-Württemberg', 'http://www.jum.baden-wuerttemberg.de', 22, 2),
(382, 'bw_lga', 'Landesgesundheitsamt (LGA) im Regierungspräsidium Stuttgart', 'http://www.rp.baden-wuerttemberg.de', 23, 2),
(383, 'bw_mas', 'Ministerium für Arbeit und Sozialordnung, Familie, Frauen und Senioren Baden-Württemberg', 'http://www.sm.baden-wuerttemberg.de/', 24, 2),
(384, 'bw_unifreifor', 'Fakultät für Forst- und Umweltwissenschaften der UNI-Freiburg', 'http://www.ffu.uni-freiburg.de', 25, 2),
(385, 'bw_unifreilan', 'Institut für Landespflege der Universität Freiburg', 'http://www.landespflege-freiburg.de/', 26, 2),
(386, 'bw_unikarlmet', 'Institut für Meteorologie und Klimaforschung am Karlsruher Institut für Technologie (KIT)', 'http://www.imk.kit.edu/', 27, 2),
(388, 'bw_stadtwald', 'Stadt Waldshut-Tiengen', 'http://www.waldshut-tiengen.de/', 113, 2),
(495, 'bu_afee', 'Agentur für Erneuerbare Energien e. V.', 'http://www.unendlich-viel-energie.de', 0, 1),
(389, 'bw_unistutt', 'Universität Stuttgart', 'http://www.uni-stuttgart.de', 29, 2),
(390, 'bw_laal', 'Landratsamt Alb-Donau-Kreis', 'http://www.alb-donau-kreis.de', 30, 2),
(391, 'bw_labi', 'Landratsamt Biberach', 'http://www.biberach.de', 31, 2),
(392, 'bw_labo', 'Landratsamt Bodenseekreis', 'http://www.bodenseekreis.de', 32, 2),
(393, 'bw_laboe', 'Landratsamt Böblingen', 'http://www.landkreis-boeblingen.de', 33, 2),
(394, 'bw_labr', 'Landratsamt Breisgau-Hochschwarzwald', 'http://www.breisgau-hochschwarzwald.de', 34, 2),
(395, 'bw_laca', 'Landratsamt Calw', 'http://www.kreis-calw.de', 35, 2),
(396, 'bw_laem', 'Landratsamt Emmendingen', 'http://www.landkreis-emmendingen.de', 36, 2),
(397, 'bw_laen', 'Landratsamt Enzkreis', 'http://www.enzkreis.de', 37, 2),
(398, 'bw_laes', 'Landratsamt Esslingen', 'http://www.landkreis-esslingen.de', 38, 2),
(399, 'bw_lafr', 'Landratsamt Freudenstadt', 'http://www.landkreis-freudenstadt.de', 39, 2),
(400, 'bw_lago', 'Landratsamt Göppingen', 'http://www.landkreis-goeppingen.de', 40, 2),
(401, 'bw_laheid', 'Landratsamt Heidenheim', 'http://www.landkreis-heidenheim.de', 41, 2),
(402, 'bw_laheil', 'Landratsamt Heilbronn', 'http://www.landkreis-heilbronn.de', 42, 2),
(403, 'bw_laho', 'Landratsamt Hohenlohekreis', 'http://www.hohenlohekreis.de/', 43, 2),
(404, 'bw_laka', 'Landratsamt Karlsruhe', 'http://www.landkreis-karlsruhe.de', 44, 2),
(405, 'bw_lako', 'Landratsamt Konstanz', 'http://www.landkreis-konstanz.de/', 45, 2),
(406, 'bw_laloe', 'Landratsamt Lörrach', 'http://www.loerrach-landkreis.de', 46, 2),
(407, 'bw_lalu', 'Landratsamt Ludwigsburg', 'http://www.landkreis-ludwigsburg.de', 47, 2),
(408, 'bw_lama', 'Landratsamt Main-Tauber-Kreis', 'http://www.main-tauber-kreis.de', 48, 2),
(409, 'bw_lane', 'Landratsamt Neckar-Odenwald-Kreis', 'http://www.neckar-odenwald-kreis.de', 49, 2),
(410, 'bw_laor', 'Landratsamt Ortenaukreis', 'http://www.ortenaukreis.de', 50, 2),
(411, 'bw_laos', 'Landratsamt Ostalbkreis', 'http://www.ostalbkreis.de', 51, 2),
(412, 'bw_laras', 'Landratsamt Rastatt', 'http://www.landkreis-rastatt.de', 52, 2),
(413, 'bw_larav', 'Landratsamt Ravensburg', 'http://www.landkreis-ravensburg.de', 53, 2),
(414, 'bw_larem', 'Landratsamt Rems-Murr-Kreis', 'http://www.rems-murr-kreis.de', 54, 2),
(415, 'bw_lareu', 'Landratsamt Reutlingen', 'http://www.kreis-reutlingen.de', 55, 2),
(416, 'bw_larhe', 'Landratsamt Rhein-Neckar-Kreis', 'http://www.rhein-neckar-kreis.de', 56, 2),
(417, 'bw_laro', 'Landratsamt Rottweil', 'http://www.landkreis-rottweil.de', 57, 2),
(418, 'bw_laschwae', 'Landratsamt Schwäbisch Hall', 'http://www.landkreis-schwaebisch-hall.de', 58, 2),
(419, 'bw_laschwa', 'Landratsamt Schwarzwald-Baar-Kreis', 'http://www.schwarzwald-baar-kreis.de', 59, 2),
(420, 'bw_lasi', 'Landratsamt Sigmaringen', 'http://www.landkreis-sigmaringen.de', 60, 2),
(421, 'bw_latue', 'Landratsamt Tübingen', 'http://www.kreis-tuebingen.de', 61, 2),
(422, 'bw_latu', 'Landratsamt Tuttlingen', 'http://www.landkreis-tuttlingen.de', 62, 2),
(423, 'bw_lawa', 'Landratsamt Waldshut', 'http://www.landkreis-waldshut.de', 63, 2),
(424, 'bw_lazo', 'Landratsamt Zollernalbkreis', 'http://www.zollernalbkreis.de', 64, 2),
(425, 'bw_muell', 'Müllabfuhr-Zweckverband von Gemeinden des Landkreises Konstanz', 'http://www.mzv-hegau.de', 65, 2),
(426, 'bw_pro', 'Proregio Oberschwaben Gesellschaft zur Landschaftsentwicklung mbH', 'http://www.proregio-oberschwaben.de/', 66, 2),
(427, 'bw_zweckab', 'Zweckverband Abfallverwertung Reutlingen/Tübingen', 'http://www.zav-rt-tue.de', 67, 2),
(428, 'bw_azvb', 'Abwasserzweckverband Breisgauer Bucht', 'http://www.azv-breisgau.de/', 68, 2),
(429, 'bw_azvh', 'Abwasserzweckverband Heidelberg', 'http://www.azv-heidelberg.de', 69, 2),
(430, 'bw_azvo', 'Abwasserzweckverband Raum Offenburg', 'http://www.azv-offenburg.de/', 70, 2),
(432, 'bw_gruen', 'Geschäftsstelle Grüne Nachbarschaft', 'http://www.gruene-nachbarschaft.de/', 72, 2),
(433, 'bw_landess', 'Landeshauptstadt Stuttgart', 'http://www.stuttgart.de', 73, 2),
(434, 'bw_now', 'NOW Zweckverband Wasserversorgung Nordostwürttemberg', 'http://www.now-wasser.de/', 74, 2),
(435, 'bw_stadta', 'Stadt Aalen', 'http://www.sw-aalen.de/', 75, 2),
(436, 'bw_stadtbb', 'Stadt Baden-Baden', 'http://www.baden-baden.de', 76, 2),
(437, 'bw_stadtba', 'Stadt Balingen', 'http://www.balingen.de', 77, 2),
(438, 'bw_stadtbi', 'Stadt Biberach', 'http://www.biberach-riss.de//index.phtml?object=tx|1.300.1&org_obj=nav|383.10.1', 78, 2),
(439, 'bw_stadtboe', 'Stadt Böblingen', 'http://www.boeblingen.kdrs.de', 79, 2),
(441, 'bw_stadtem', 'Stadt Emmendingen', 'http://www.emmendingen.de', 81, 2),
(442, 'bw_stadtes', 'Stadt Esslingen am Neckar', 'http://www.esslingen.de', 82, 2),
(443, 'bw_stadtfrei', 'Stadt Freiburg', 'http://www.freiburg.de', 83, 2),
(444, 'bw_stadtfreu', 'Stadt Freudenstadt', 'http://www.freudenstadt.info/index.phtml?La=1&NavID=611.110', 84, 2),
(445, 'bw_stadtfrie', 'Stadt Friedrichshafen', 'http://www.friedrichshafen.de', 85, 2),
(446, 'bw_stadtgoe', 'Stadt Göppingen', 'http://www.goeppingen.de', 86, 2),
(447, 'bw_stadtheidel', 'Stadt Heidelberg', 'http://www.heidelberg.de', 87, 2),
(448, 'bw_stadtheiden', 'Stadt Heidenheim', 'http://www.heidenheim.de', 88, 2),
(449, 'bw_stadtheil', 'Stadt Heilbronn', 'http://www.heilbronn.de', 89, 2),
(450, 'bw_stadtka', 'Stadt Karlsruhe', 'http://www.karlsruhe.de', 90, 2),
(451, 'bw_stadtko', 'Stadt Konstanz', 'http://www.konstanz.de', 91, 2),
(550, 'bw_techno', 'Technologie- und Innovationszentrum Umwelttechnik und Ressourceneffizienz Baden-Württemberg GmbH', 'http://www.umwelttechnik-bw.de/', 0, 2),
(453, 'bw_stadtloe', 'Stadt Lörrach', 'http://www.loerrach.de', 93, 2),
(454, 'bw_stadtlu', 'Stadt Ludwigsburg', 'http://www.ludwigsburg.de', 94, 2),
(455, 'bw_stadtma', 'Stadt Mannheim', 'http://www.mannheim.de', 95, 2),
(456, 'bw_stadtmo', 'Stadt Mosbach', 'http://www.mosbach.de', 96, 2),
(457, 'bw_stadtof', 'Stadt Offenburg', 'http://www.offenburg.de', 97, 2),
(458, 'bw_stadtpf', 'Stadt Pforzheim', 'http://www.pforzheim.de', 98, 2),
(459, 'bw_stadtras', 'Stadt Rastatt', 'http://www.rastatt.de', 99, 2),
(460, 'bw_stadtrav', 'Stadt Ravensburg', 'http://www.ravensburg.de', 100, 2),
(461, 'bw_stadtreu', 'Stadt Reutlingen', 'http://www.reutlingen.de', 101, 2),
(462, 'bw_stadtrot', 'Stadt Rottweil', 'http://www.rottweil.de', 102, 2),
(463, 'bw_stadtsg', 'Stadt Schwäbisch Gmünd', 'http://www.schwaebisch-gmuend.de', 103, 2),
(464, 'bw_stadtsh', 'Stadt Schwäbisch Hall', 'http://www.schwaebischhall.de', 104, 2),
(465, 'bw_stadtsig', 'Stadt Sigmaringen', 'http://www.sigmaringen.de/', 105, 2),
(466, 'bw_stadtsin', 'Stadt Sindelfingen', 'http://www.sindelfingen.de', 106, 2),
(467, 'bw_stadttau', 'Stadt Tauberbischofsheim', 'http://www.tauberbischofsheim.de', 107, 2),
(468, 'bw_stadttue', 'Stadt Tübingen', 'http://www.tuebingen.de', 108, 2),
(469, 'bw_stadttut', 'Stadt Tuttlingen', 'http://www.tuttlingen.de', 109, 2),
(470, 'bw_stadtulm', 'Stadt Ulm', 'http://www.ulm.de', 110, 2),
(471, 'bw_stadtvill', 'Stadt Villingen-Schwenningen', 'http://www.svs-energie.de', 111, 2),
(472, 'bw_stadtwaib', 'Stadt Waiblingen', 'http://www.waiblingen.de', 112, 2),
(473, 'bw_geopark', 'Verein Geopark Schwäbische Alb e. V.', 'http://www.geopark-alb.de', 114, 2),
(474, 'bw_zweckba', 'Zweckverband Abwasserreinigung Balingen', 'http://www.klaeranlage-balingen.de', 115, 2),
(475, 'bw_zweckbo', 'Zweckverband Bodensee-Wasserversorgung (BWV)', 'http://www.zvbwv.de/', 116, 2),
(476, 'bw_zweckla', 'Zweckverband Landeswasserversorgung', 'http://www.lw-online.de/', 117, 2),
(552, 'ni_gd_ostercap', 'Ostercappeln, Gemeinde', 'http://www.ostercappeln.de/', 0, 10),
(553, 'ni_lk_dieph', 'Diepholz, Landkreis', 'http://www.diepholz.de/', 0, 10),
(479, 'bw_igkb', 'IGKB Internationale Gewässerschutzkommission Bodensee', 'http://www.igkb.org/', 120, 2),
(551, 'ni_buwsv', 'Wasser- und Schifffahrtsverwaltung des Bundes', '', 0, 10),
(481, 'bw_see', 'Internationale Bodenseekonferenz', 'http://www.bodenseekonferenz.org', 122, 2),
(482, 'bw_kea', 'KEA Klimaschutz- und Energieagentur Baden-Württemberg GmbH', 'http://www.keabw.de/', 123, 2),
(485, 'bw_museum', 'Staatliches Museum für Naturkunde in Stuttgart', 'http://www.wildbienen-kataster.de/', 126, 2),
(486, 'bw_rpf', 'Regierungspräsidium Freiburg', 'http://www.rp.baden-wuerttemberg.de/servlet/PB/menu/1007481/index.html', 137, 2),
(493, 'bu_bzga', 'Bundeszentrale für gesundheitliche Aufklärung', 'http://www.bzga.de', 44, 1),
(487, 'sn_geosn', 'Staatsbetrieb Geobasisinformation und Vermessung Sachsen (GeoSN)', 'http://www.landesvermessung.sachsen.de', 70, 14),
(498, 'bu_iksr', 'Internationale Kommission für den Schutz des Rheins', 'http://www.iksr.org/', 0, 1),
(499, 'bu_icpdr', 'Internationale Kommission für den Schutz der Donau', 'http://www.icpdr.org/', 0, 1),
(500, 'bu_moko', 'Moselkommission', 'http://www.moselkommission.org/', 0, 1),
(501, 'bu_ikso', 'Internationale Kommission für den Schutz der Oder gegen Verunreinigung', 'http://www.mkoo.pl/index.php?lang=DE', 0, 1),
(502, 'sh_lbknm', 'Landesbetrieb für Küstenschutz, Nationalpark und Meeresschutz Schleswig-Holstein', 'http://www.schleswig-holstein.de/LKN/DE/LKN_node.html', 0, 16),
(503, 'sh_lva', 'Landesvermessungsamt Schleswig-Holstein ', 'http://www.schleswig-holstein.de/LVERMA/DE/LVERMA_node.html', 0, 16),
(504, 'bu_jki', 'Julius Kühn-Institut, Bundesforschungsinstitut für Kulturpflanzen (JKI)', 'http://oekologischerlandbau.jki.bund.de/index.php?menuid=1', 0, 1),
(505, 'ni_geo', 'Geodatenportal Niedersachsen', 'http://www.geodaten.niedersachsen.de', 0, 10),
(506, 'ni_liag', 'Leibniz-Institut für Angewandte Geophysik', 'http://www.liag-hannover.de', 0, 10),
(507, 'ni_nph', 'Nationalparkverwaltung Harz', 'http://www.nationalpark-harz.de/', 0, 10),
(508, 'ni_lskn', 'Landesbetrieb für Statistik und Kommunikationstechnologie Niedersachsen', 'http://www.lskn.niedersachsen.de/', 0, 10),
(509, 'ni_bre', 'Biosphärenreservat Elbtalaue', 'http://www.elbtalaue.niedersachsen.de', 0, 10),
(510, 'ni_npw', 'Nationalparkverwaltung Niedersächisches Wattenmeer', 'http://www.nationalpark-wattenmeer.de/nds', 0, 10),
(512, 'sl_lkvk', 'Landesamt für Kataster-, Vermessungs- und Kartenwesen des Saarlandes', 'http://www.saarland.de/kataster_vermessung_karten.htm', 0, 13),
(515, 'hh_bsb', 'Behörde für Schule und Berufsbildung Hamburg', ' http://www.hamburg.de/bsb/ ', 0, 7),
(516, 'hh_hu', 'Institut für Hygiene und Umwelt', 'http://www.hamburg.de/hu/ ', 0, 7),
(519, 'sn_landratsamt-pirna.de ', 'Landkreis Sächsische Schweiz-Osterzgebirge', 'http://www.saechsische-schweiz-osterzgebirge.de/', 0, 14),
(520, 'sn_hoyerswerda.de', 'Stadt Hoyerswerda', 'http://www.hoyerswerda.de/', 0, 14),
(521, 'sn_werdau.de', 'Stadt Werdau', 'http://www.werdau.de/werdau/willkommen.asp', 0, 14),
(522, 'sn_riesa.de', 'Stadt Riesa', 'http://www.riesa.de/deu/', 0, 14),
(523, 'sn_crimmitschau.de', 'Stadt Crimmitschau', 'http://www.crimmitschau.de', 0, 14),
(524, 'sn_doebeln.de', 'Stadt Döbeln', 'http://www.doebeln.de/', 0, 14),
(525, 'sn_zittau.de', 'Stadt Zittau', 'http://www.zittau.eu', 0, 14),
(526, 'sn_auerbach', 'Stadt Auerbach', 'http://www.stadt-auerbach.de', 0, 14),
(527, 'sn_radebeul.de', 'Stadt Radebeul', 'http://www.radebeul.de', 0, 14),
(528, 'sn_plauen.de', 'Stadt Plauen', 'http://www.plauen.de/', 0, 14),
(529, 'sn_stadt-meissen.de', 'Stadt Meißen', 'http://www.stadt-meissen.de', 0, 14),
(530, 'sn_bautzen', 'Stadt Bautzen', 'http://www.bautzen.de', 0, 14),
(531, 'sn_pirna.de', 'Stadt Pirna', 'http://www.pirna.de', 0, 14),
(532, 'sn_zwickau.de', 'Stadt Zwickau', 'http://www.zwickau.de/', 0, 14),
(533, 'sn_delitzsch.de', 'Stadt Delitzsch', 'http://www.delitzsch.de/', 0, 14),
(534, 'sn_annaberg-buchholz', 'Stadt Annaberg-Buchholz', 'http://www.annaberg-buchholz.de/', 0, 14),
(535, 'sn_reichenbach-vogtland.de', 'Stadt Reichenbach i. Vogtland', 'http://www.reichenbach-vogtland.de', 0, 14),
(536, 'sn_erzgebirgskreis.de', 'Erzgebirgskreis', 'http://www.erzgebirgskreis.de/', 0, 14),
(538, 'he_lbla', 'Landesbetrieb Landwirtschaft Hessen', 'http://www.llh.hessen.de/', 0, 8),
(540, 'ni_uol', 'Carl von Ossietzky Universität Oldenburg', 'http://www.uni-oldenburg.de', 0, 10),
(545, 'mv_flusselbe', 'Biosphärenreservat Flusslandschaft Elbe-MV ', 'http://www.elbetal-mv.de/', 0, 9),
(546, 'mv_soruegen', 'Biosphärenreservat Südost-Rügen', 'http://www.biosphaerenreservat-suedostruegen.de/', 0, 1),
(547, 'ni_euafv', 'Amt für Veröffentlichungen der Europäischen Union', 'http://publications.europa.eu/index_de.htm', 0, 10),
(548, 'he_uni_kassel', 'Universität Kassel', 'http://www.uni-kassel.de/uni/', 0, 8),
(549, 'sn_landesdirektion.de', 'Landesdirektion Sachsen', 'http://www.lds.sachsen.de/', 0, 14),
(555, 'be_sejv', 'Senatsverwaltung für Justiz und Verbraucherschutz', 'www.berlin.de/sen/justiz/', 5, 4),
(556, 'be_segessoz', 'Senatsverwaltung für Gesundheit und Soziales ', 'www.berlin.de/sen/gessoz', 6, 1),
(557, 'bu_wsv', 'Wasser- und Schifffahrtsverwaltung des Bundes', 'http://www.wsv.de/', 0, 1),
(558, 'bw_kit', 'Karlsruher Institut für Technologie', 'http://www.kit.edu/', 0, 2),
(559, 'bu_vti', 'Johann Heinrich von Thünen-Institut (vTI)', 'http://www.ti.bund.de/', 0, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_rss_source`
--

DROP TABLE IF EXISTS `ingrid_rss_source`;
CREATE TABLE IF NOT EXISTS `ingrid_rss_source` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `provider` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(1023) DEFAULT NULL,
  `url` varchar(255) NOT NULL DEFAULT '',
  `lang` varchar(255) NOT NULL DEFAULT '',
  `categories` varchar(255) DEFAULT NULL,
  `error` varchar(255) DEFAULT NULL,
  `numLastCount` smallint(3) DEFAULT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=65 ;

--
-- Daten für Tabelle `ingrid_rss_source`
--

INSERT INTO `ingrid_rss_source` (`id`, `provider`, `description`, `url`, `lang`, `categories`, `error`, `numLastCount`, `lastUpdate`) VALUES
(1, 'bu_uba', 'Umweltbundesamt', 'http://www.umweltbundesamt.de/rss/presse', 'de', 'all', NULL, 3, '2015-01-21 11:11:33');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_rss_store`
--

DROP TABLE IF EXISTS `ingrid_rss_store`;
CREATE TABLE IF NOT EXISTS `ingrid_rss_store` (
  `link` varchar(255) NOT NULL DEFAULT '',
  `author` varchar(1023) DEFAULT NULL,
  `categories` varchar(255) DEFAULT NULL,
  `copyright` varchar(255) DEFAULT NULL,
  `description` mediumtext,
  `language` varchar(255) DEFAULT NULL,
  `published_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `title` varchar(1023) DEFAULT NULL,
  PRIMARY KEY (`link`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `ingrid_rss_store`
--

INSERT INTO `ingrid_rss_store` (`link`, `author`, `categories`, `copyright`, `description`, `language`, `published_date`, `title`) VALUES
('http://www.umweltbundesamt.de/presse/presseinformationen/kaffeemaschinen-it-geraete-sparsamer-im', 'Umweltbundesamt', NULL, NULL, 'Ab Beginn des neuen Jahres gelten in der Europ&auml;ischen Union niedrigere Verbrauchswerte f&uuml;r eine Reihe von Elektroger&auml;ten des allt&auml;glichen Bedarfs, wenn sie neu auf den Markt gebracht werden. Strengere technische Anforderungen m&uuml;ssen zum Beispiel Kaffeemaschinen und IT-Ger&auml;te wie Modems und Router erf&uuml;llen. F&uuml;r elektrische Back&ouml;fen gilt zudem, dass die Informationen &uuml;ber ihren Energieverbrauch transparenter gestaltet sein m&uuml;ssen.', 'de', '2014-12-23 12:30:00', 'Kaffeemaschinen und IT-Ger&auml;te sparsamer im Stromverbrauch'),
('http://www.umweltbundesamt.de/presse/presseinformationen/gold-fuer-uba-neubau', 'Umweltbundesamt', NULL, NULL, 'Noch klingt der Name nach Zukunft: Das &#8222;Haus 2019&#8220; &#8211; doch schon heute erf&uuml;llt das B&uuml;rogeb&auml;ude des Umweltbundesamtes (UBA) die Anforderungen der europ&auml;ischen Geb&auml;uderichtlinie f&uuml;r das Jahr 2019. Auf der Bau 2015 in M&uuml;nchen &uuml;berreichte daher der Parlamentarische Staatssekret&auml;r im Bundesbauministerium, Florian Pronold, heute die Zertifizierungsurkunde f&uuml;r das neue B&uuml;rogeb&auml;ude des UBA in Berlin-Marienfelde. &#8222;Das &#8218;Haus 2019&#8216; ist das erste Bundesgeb&auml;ude, das mit den anspruchsvollen Vorgaben des &#8218;Bewertungssystems Nachhaltiges Bauen f&uuml;r Bundesgeb&auml;ude&#8216; (BNB) von Beginn an geplant und bewertet wurde&#8220;, so Florian Pronold bei der &Uuml;bergabe der Urkunde. &#8222;Darauf k&ouml;nnen wir zu Recht stolz sein, und ich danke allen Projektbeteiligten f&uuml;r Ihr besonderes Engagement.&#8220; Der gesamte Rohbau, einschlie&szlig;lich der Fassade, ist aus dem nachwachsenden Rohstoff Holz gefertigt. Zudem versorgt sich das Geb&auml;ude komplett selbst mit Energie. &#8222;Mit dem &#8218;Haus 2019&#8216; setzen wir nicht nur ein Zeichen f&uuml;r vorbildliches, nachhaltiges Bauen, sondern zeigen auch beispielhaft, wie Null-Energie-Geb&auml;ude k&uuml;nftig geplant und gebaut werden k&ouml;nnen&#8220;, betonte Maria Krautzberger, Pr&auml;sidentin des Umweltbundesamtes.', 'de', '2015-01-19 19:00:00', '&#8222;Gold&#8220; f&uuml;r UBA-Neubau'),
('http://www.umweltbundesamt.de/presse/presseinformationen/stickstoffueberschuss-ein-umweltproblem-neuem', 'Umweltbundesamt', NULL, NULL, 'In der EU sind fast zwei Drittel aller nat&uuml;rlichen Lebensr&auml;ume &uuml;berd&uuml;ngt. Verantwortlich f&uuml;r den &Uuml;berschuss an N&auml;hrstoffen ist vor allem der Stickstoff aus der Landwirtschaft, der als G&uuml;lle oder Minerald&uuml;nger auf die Felder kommt. Die EU-Kommission hat wiederholt angemahnt, die Stickstoffeintr&auml;ge zu minimieren. Maria Krautzberger, Pr&auml;sidentin des Umweltbundesamtes (UBA): &#8222;Es ist wichtig, dass die EU weiter Impulse f&uuml;r eine Reduzierung der Stickstoff&uuml;bersch&uuml;sse setzt. Gleichzeitig m&uuml;ssen wir auf nationaler Ebene handeln. Dabei ist die D&uuml;ngeverordnung ein wichtiger Ansatz, um Luft, Boden und Grundwasser besser vor zu viel Stickstoff zu sch&uuml;tzen.&#8220;', 'de', '2015-01-08 15:00:00', 'Stickstoff&uuml;berschuss &#8211; ein Umweltproblem mit neuem Ausma&szlig;');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_service_rubric`
--

DROP TABLE IF EXISTS `ingrid_service_rubric`;
CREATE TABLE IF NOT EXISTS `ingrid_service_rubric` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `form_value` varchar(255) NOT NULL DEFAULT '',
  `query_value` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Daten für Tabelle `ingrid_service_rubric`
--

INSERT INTO `ingrid_service_rubric` (`id`, `form_value`, `query_value`, `sortkey`) VALUES
(1, 'pre', 'press', 1),
(2, 'pub', 'publication', 2),
(3, 'ver', 'event', 3);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_sys_codelist`
--

DROP TABLE IF EXISTS `ingrid_sys_codelist`;
CREATE TABLE IF NOT EXISTS `ingrid_sys_codelist` (
  `codelist_id` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(50) DEFAULT NULL,
  `default_enabled` mediumint(9) DEFAULT NULL,
  `maintainable` mediumint(9) DEFAULT NULL,
  PRIMARY KEY (`codelist_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `ingrid_sys_codelist`
--

INSERT INTO `ingrid_sys_codelist` (`codelist_id`, `name`, `default_enabled`, `maintainable`) VALUES
(100, 'Raumbezugssystem (EPSG)', 1, 1),
(101, 'Vertikales Datum', 1, 1),
(102, 'Höhe', 1, 1),
(502, 'Zeitbezug des Datensatzes (Typ)', 1, 1),
(505, 'Adresse', 1, 1),
(510, 'Zeichensatz', 1, 1),
(515, 'Vektorformat (Geometrie)', 1, 1),
(517, 'Schlüsselwort', 1, 1),
(518, 'Periodizität', 1, 1),
(520, 'Medium', 1, 1),
(523, 'Status', 1, 1),
(525, 'Datensatz/Datenserie', 1, 1),
(526, 'Digitale Repräsentation', 1, 1),
(527, 'Klassifikation', 1, 1),
(528, 'Vektorformat (Topologie)', 1, 1),
(99999999, 'Sprache', 1, 0);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_sys_codelist_domain`
--

DROP TABLE IF EXISTS `ingrid_sys_codelist_domain`;
CREATE TABLE IF NOT EXISTS `ingrid_sys_codelist_domain` (
  `codelist_id` bigint(20) NOT NULL DEFAULT '0',
  `domain_id` bigint(20) NOT NULL DEFAULT '0',
  `lang_id` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `def_domain` mediumint(9) DEFAULT NULL,
  PRIMARY KEY (`codelist_id`,`domain_id`,`lang_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `ingrid_sys_codelist_domain`
--

INSERT INTO `ingrid_sys_codelist_domain` (`codelist_id`, `domain_id`, `lang_id`, `name`, `description`, `def_domain`) VALUES
(100, 4178, 94, 'EPSG:4178 / Pulkovo 1942(83) / geographisch', 'Pulkovo 1942(83) / geographisch', 0),
(100, 4178, 121, 'EPSG:4178 / Pulkovo 1942(83) / geographisch', 'Pulkovo 1942(83) / geographisch', 0),
(100, 4230, 94, 'EPSG:4230 / ED50 / geographisch', 'ED50 / geographisch', 0),
(100, 4230, 121, 'EPSG:4230 / ED50 / geographisch', 'ED50 / geographisch', 0),
(100, 4258, 94, 'EPSG:4258 / ETRS89 / geographisch', 'ETRS89 / geographisch', 0),
(100, 4258, 121, 'EPSG:4258 / ETRS89 / geographisch', 'ETRS89 / geographisch', 0),
(100, 4284, 94, 'EPSG:4284 / Pulkovo 1942 / geographisch', 'Pulkovo 1942 / geographisch', 0),
(100, 4284, 121, 'EPSG:4284 / Pulkovo 1942 / geographisch', 'Pulkovo 1942 / geographisch', 0),
(100, 4314, 94, 'EPSG:4314 / DHDN / geographisch', 'DHDN / geographisch', 0),
(100, 4314, 121, 'EPSG:4314 / DHDN / geographisch', 'DHDN / geographisch', 0),
(100, 4326, 94, 'EPSG:4326 / WGS 84 / geographisch', 'WGS 84 / geographisch', 0),
(100, 4326, 121, 'EPSG:4326 / WGS 84 / geographisch', 'WGS 84 / geographisch', 0),
(100, 23031, 94, 'EPSG:23031 / ED50 / UTM Zone 31N', 'ED50 / UTM Zone 31N', 0),
(100, 23031, 121, 'EPSG:23031 / ED50 / UTM Zone 31N', 'ED50 / UTM Zone 31N', 0),
(100, 23032, 94, 'EPSG:23032 / ED50 / UTM Zone 32N', 'ED50 / UTM Zone 32N', 0),
(100, 23032, 121, 'EPSG:23032 / ED50 / UTM Zone 32N', 'ED50 / UTM Zone 32N', 0),
(100, 23033, 94, 'EPSG:23033 / ED50 / UTM Zone 33N', 'ED50 / UTM Zone 33N', 0),
(100, 23033, 121, 'EPSG:23033 / ED50 / UTM Zone 33N', 'ED50 / UTM Zone 33N', 0),
(100, 23631, 94, 'EPSG:32631 / WGS 84 / UTM Zone 31N', 'WGS 84 / UTM Zone 31N', 0),
(100, 23631, 121, 'EPSG:32631 / WGS 84 / UTM Zone 31N', 'WGS 84 / UTM Zone 31N', 0),
(100, 23632, 94, 'EPSG:32632 / WGS 84 / UTM Zone 32N/33N', 'WGS 84 / UTM Zone 32N/33N', 0),
(100, 23632, 121, 'EPSG:32632 / WGS 84 / UTM Zone 32N/33N', 'WGS 84 / UTM Zone 32N/33N', 0),
(100, 25831, 94, 'EPSG:25831 / ETRS89 / UTM Zone 31N', 'ETRS89 / UTM Zone 31N', 0),
(100, 25831, 121, 'EPSG:25831 / ETRS89 / UTM Zone 31N', 'ETRS89 / UTM Zone 31N', 0),
(100, 25832, 94, 'EPSG:25832 / ETRS89 / UTM Zone 32N', 'ETRS89 / UTM Zone 32N', 0),
(100, 25832, 121, 'EPSG:25832 / ETRS89 / UTM Zone 32N', 'ETRS89 / UTM Zone 32N', 0),
(100, 25833, 94, 'EPSG:25833 / ETRS89 / UTM Zone 33N', 'ETRS89 / UTM Zone 33N', 0),
(100, 25833, 121, 'EPSG:25833 / ETRS89 / UTM Zone 33N', 'ETRS89 / UTM Zone 33N', 0),
(100, 28463, 94, 'EPSG:28463 / Pulkovo 1942 / Gauss-Krüger 2N/3N', 'Pulkovo 1942 / Gauss-Krüger 2N/3N', 0),
(100, 28463, 121, 'EPSG:28463 / Pulkovo 1942 / Gauss-Krüger 2N/3N', 'Pulkovo 1942 / Gauss-Krüger 2N/3N', 0),
(100, 31281, 94, 'EPSG:31281 / MGI (Ferro) / Austria West Zone', '-', 0),
(100, 31281, 121, 'EPSG:31281 / MGI (Ferro) / Austria West Zone', '-', 0),
(100, 31282, 94, 'EPSG:31282 / MGI (Ferro) / Austria Central Zone', '-', 0),
(100, 31282, 121, 'EPSG:31282 / MGI (Ferro) / Austria Central Zone', '-', 0),
(100, 31283, 94, 'EPSG:31283 / MGI (Ferro) / Austria East Zone', '-', 0),
(100, 31283, 121, 'EPSG:31283 / MGI (Ferro) / Austria East Zone', '-', 0),
(100, 31284, 94, 'EPSG:31284 / MGI / M28', '-', 0),
(100, 31284, 121, 'EPSG:31284 / MGI / M28', '-', 0),
(100, 31285, 94, 'EPSG:31285 / MGI / M31', '-', 0),
(100, 31285, 121, 'EPSG:31285 / MGI / M31', '-', 0),
(100, 31286, 94, 'EPSG:31286 / MGI / M34', '-', 0),
(100, 31286, 121, 'EPSG:31286 / MGI / M34', '-', 0),
(100, 31287, 94, 'EPSG:31287 / MGI / Austria Lambert', '-', 0),
(100, 31287, 121, 'EPSG:31287 / MGI / Austria Lambert', '-', 0),
(100, 31288, 94, 'EPSG:31288 / MGI (Ferro) / M28', '-', 0),
(100, 31288, 121, 'EPSG:31288 / MGI (Ferro) / M28', '-', 0),
(100, 31289, 94, 'EPSG:31289 / MGI (Ferro) / M31', '-', 0),
(100, 31289, 121, 'EPSG:31289 / MGI (Ferro) / M31', '-', 0),
(100, 31290, 94, 'EPSG:31290 / MGI (Ferro) / M34', '-', 0),
(100, 31290, 121, 'EPSG:31290 / MGI (Ferro) / M34', '-', 0),
(100, 31291, 94, 'EPSG:31291 / MGI (Ferro) / Austria West Zone', '-', 0),
(100, 31291, 121, 'EPSG:31291 / MGI (Ferro) / Austria West Zone', '-', 0),
(100, 31292, 94, 'EPSG:31292 / MGI (Ferro) / Austria Central Zone', '-', 0),
(100, 31292, 121, 'EPSG:31292 / MGI (Ferro) / Austria Central Zone', '-', 0),
(100, 31293, 94, 'EPSG:31293 / MGI (Ferro) / Austria East Zone', '-', 0),
(100, 31293, 121, 'EPSG:31293 / MGI (Ferro) / Austria East Zone', '-', 0),
(100, 31466, 94, 'EPSG:31466 / DHDN / Gauss-Krüger Zone 2', 'DHDN / Gauss-Krüger Zone 2', 0),
(100, 31466, 121, 'EPSG:31466 / DHDN / Gauss-Krüger Zone 2', 'DHDN / Gauss-Krüger Zone 2', 0),
(100, 31467, 94, 'EPSG:31467 /DHDN / Gauss-Krüger Zone 3', 'DHDN / Gauss-Krüger Zone 3', 0),
(100, 31467, 121, 'EPSG:31467 /DHDN / Gauss-Krüger Zone 3', 'DHDN / Gauss-Krüger Zone 3', 0),
(100, 31468, 94, 'EPSG:31468 / DHDN / Gauss-Krüger Zone 4', 'DHDN / Gauss-Krüger Zone 4', 0),
(100, 31468, 121, 'EPSG:31468 / DHDN / Gauss-Krüger Zone 4', 'DHDN / Gauss-Krüger Zone 4', 0),
(100, 31469, 94, 'EPSG:31469 / DHDN / Gauss-Krüger Zone 5', 'DHDN / Gauss-Krüger Zone 5', 0),
(100, 31469, 121, 'EPSG:31469 / DHDN / Gauss-Krüger Zone 5', 'DHDN / Gauss-Krüger Zone 5', 0),
(100, 31491, 94, 'EPSG:31491 / DHDN / Germany zone 1', 'DHDN / Germany zone 1', 0),
(100, 31491, 121, 'EPSG:31491 / DHDN / Germany zone 1', 'DHDN / Germany zone 1', 0),
(100, 31492, 94, 'EPSG:31492 /DHDN / Germany zone 2', 'DHDN / Germany zone 2', 0),
(100, 31492, 121, 'EPSG:31492 /DHDN / Germany zone 2', 'DHDN / Germany zone 2', 0),
(100, 31493, 94, 'EPSG:31493 / DHDN / Germany zone 3', 'DHDN / Germany zone 3', 1),
(100, 31493, 121, 'EPSG:31493 / DHDN / Germany zone 3', 'DHDN / Germany zone 3', 1),
(100, 31494, 94, 'EPSG:31494 / DHDN / Germany zone 4', 'DHDN / Germany zone 4', 0),
(100, 31494, 121, 'EPSG:31494 / DHDN / Germany zone 4', 'DHDN / Germany zone 4', 0),
(100, 31495, 94, 'EPSG:31495 / DHDN / Germany zone 5', 'DHDN / Germany zone 5', 0),
(100, 31495, 121, 'EPSG:31495 / DHDN / Germany zone 5', 'DHDN / Germany zone 5', 0),
(100, 9000001, 94, 'DE_42/83 / GK_3', 'Datum 42/83 with Gauss-Krüger-System', 0),
(100, 9000001, 121, 'DE_42/83 / GK_3', 'Datum 42/83 with Gauss-Krüger-System', 0),
(100, 9000002, 94, 'DE_DHDN / GK_3', 'Datum DHDN with Gauss-Krüger-System (also known as Rauenberg or Potsdam Datum)', 0),
(100, 9000002, 121, 'DE_DHDN / GK_3', 'Datum DHDN with Gauss-Krüger-System (also known as Rauenberg or Potsdam Datum)', 0),
(100, 9000003, 94, 'DE_ETRS89 / UTM', 'Datum ETRS89 for Federal State Brandenburg with UTM Projection (one zone extended)', 0),
(100, 9000003, 121, 'DE_ETRS89 / UTM', 'Datum ETRS89 for Federal State Brandenburg with UTM Projection (one zone extended)', 0),
(100, 9000004, 94, 'DE_PD/83 / GK_3', 'Datum PD/83 with Gauss-Krüger-System (realisation of Postdam Datum for Federal State Thüringen)', 0),
(100, 9000005, 121, 'DE_PD/83 / GK_3', 'Datum PD/83 with Gauss-Krüger-System (realisation of Postdam Datum for Federal State Thüringen)', 0),
(100, 9000006, 94, 'DE_RD/83 / GK_3', 'Datum RD/83 with Gauss-Krüger-System (realisation of Rauenberg Datum for Federal State Sachsen)', 0),
(100, 9000006, 121, 'DE_RD/83 / GK_3', 'Datum RD/83 with Gauss-Krüger-System (realisation of Rauenberg Datum for Federal State Sachsen)', 0),
(101, 62, 94, 'Mean Sea Level', '-', 0),
(101, 62, 121, 'Mean Sea Level', '-', 0),
(101, 63, 94, 'Ordnance Datum Newlyn', '-', 0),
(101, 63, 121, 'Ordnance Datum Newlyn', '-', 0),
(101, 64, 94, 'National Geodetic Vertical Datum 1929', '26 tide gauges in the US and Canada.', 0),
(101, 64, 121, 'National Geodetic Vertical Datum 1929', '26 tide gauges in the US and Canada.', 0),
(101, 65, 94, 'North American Vertical Datum 1988', 'Father''s Point, Rimouski, Quebec.', 0),
(101, 65, 121, 'North American Vertical Datum 1988', 'Father''s Point, Rimouski, Quebec.', 0),
(101, 5105, 94, 'Baltic Sea', 'Average water level at Kronshtadt', 0),
(101, 5105, 121, 'Baltic Sea', 'Average water level at Kronshtadt', 0),
(101, 5107, 94, 'Nivellement general de la France', 'Mean sea level at Marseille', 0),
(101, 5107, 121, 'Nivellement general de la France', 'Mean sea level at Marseille', 0),
(101, 5109, 94, 'Normaal Amsterdams Peil', 'wird in alten Bundesländern genutzt', 1),
(101, 5109, 121, 'Amsterdamer Pegel (NN)', 'wird in alten Bundesländern genutzt', 1),
(101, 5110, 94, 'Oostende', 'Mean low water during 1958', 0),
(101, 5110, 121, 'Oostende', 'Mean low water during 1958', 0),
(101, 5113, 94, 'Sea Level', '-', 0),
(101, 5113, 121, 'Sea Level', '-', 0),
(101, 5114, 94, 'Canadian Vertical Datum of 1928', '-', 0),
(101, 5114, 121, 'Canadian Vertical Datum of 1928', '-', 0),
(101, 5115, 94, 'Piraeus Harbour 1986', 'MSL determined during 1986.', 0),
(101, 5115, 121, 'Piraeus Harbour 1986', 'MSL determined during 1986.', 0),
(101, 5116, 94, 'Helsinki 1960', 'MSL at Helsinki during 1960.', 0),
(101, 5116, 121, 'Helsinki 1960', 'MSL at Helsinki during 1960.', 0),
(101, 5117, 94, 'Rikets hojdsystem 1970', '-', 0),
(101, 5117, 121, 'Rikets hojdsystem 1970', '-', 0),
(101, 5118, 94, 'Nivellement general de la France - Lalle', 'Mean sea level at Marseille.', 0),
(101, 5118, 121, 'Nivellement general de la France - Lalle', 'Mean sea level at Marseille.', 0),
(101, 5119, 94, 'Nivellement general de la France - IGN69', 'Mean sea level at Marseille.', 0),
(101, 5119, 121, 'Nivellement general de la France - IGN69', 'Mean sea level at Marseille.', 0),
(101, 5120, 94, 'Nivellement general de la France - IGN78', '-', 0),
(101, 5120, 121, 'Nivellement general de la France - IGN78', '-', 0),
(101, 5123, 94, 'PDO Height Datum 1993', '-', 0),
(101, 5123, 121, 'PDO Height Datum 1993', '-', 0),
(101, 5127, 94, 'Landesnivellement 1902', 'Origin at Repere Pierre du Niton (RPN) 373.6 metres above msl derived at Marseille.', 0),
(101, 5127, 121, 'Landesnivellement 1902', 'Origin at Repere Pierre du Niton (RPN) 373.6 metres above msl derived at Marseille.', 0),
(101, 5128, 94, 'Landeshohennetz 1995', 'Origin at Repere Pierre du Niton (RPN) 373.6 metres above msl derived at Marseille.', 0),
(101, 5128, 121, 'Landeshohennetz 1995', 'Origin at Repere Pierre du Niton (RPN) 373.6 metres above msl derived at Marseille.', 0),
(101, 5129, 94, 'European Vertical Reference Frame 2000', 'Geopotential number at Normaal Amsterdams Peil is zero.', 0),
(101, 5129, 121, 'European Vertical Reference Frame 2000', 'Geopotential number at Normaal Amsterdams Peil is zero.', 0),
(101, 5130, 94, 'Malin Head', 'Mean sea level between January 1960 and December 1969.', 0),
(101, 5130, 121, 'Malin Head', 'Mean sea level between January 1960 and December 1969.', 0),
(101, 5131, 94, 'Belfast Lough', 'Mean sea level between 1951 and 1956 at Clarendon Dock, Belfast.', 0),
(101, 5131, 121, 'Belfast Lough', 'Mean sea level between 1951 and 1956 at Clarendon Dock, Belfast.', 0),
(101, 5132, 94, 'Dansk Normal Nul', '-', 0),
(101, 5132, 121, 'Dansk Normal Nul', '-', 0),
(101, 5138, 94, 'Ordnance Datum Newlyn (Orkney Isles)', '-', 0),
(101, 5138, 121, 'Ordnance Datum Newlyn (Orkney Isles)', '-', 0),
(101, 5139, 94, 'Fair Isle', '-', 0),
(101, 5139, 121, 'Fair Isle', '-', 0),
(101, 5140, 94, 'Lerwick', '-', 0),
(101, 5140, 121, 'Lerwick', '-', 0),
(101, 5141, 94, 'Foula', '-', 0),
(101, 5141, 121, 'Foula', '-', 0),
(101, 5142, 94, 'Sule Skerry', '-', 0),
(101, 5142, 121, 'Sule Skerry', '-', 0),
(101, 5143, 94, 'North Rona', '-', 0),
(101, 5143, 121, 'North Rona', '-', 0),
(101, 5144, 94, 'Stornoway', '-', 0),
(101, 5144, 121, 'Stornoway', '-', 0),
(101, 5145, 94, 'St. Kilda', '-', 0),
(101, 5145, 121, 'St. Kilda', '-', 0),
(101, 5146, 94, 'Flannan Isles', '-', 0),
(101, 5146, 121, 'Flannan Isles', '-', 0),
(101, 5147, 94, 'St. Marys', '-', 0),
(101, 5147, 121, 'St. Marys', '-', 0),
(101, 5148, 94, 'Douglas', '-', 0),
(101, 5148, 121, 'Douglas', '-', 0),
(101, 5149, 94, 'Fao', '-', 0),
(101, 5149, 121, 'Fao', '-', 0),
(101, 5151, 94, 'Nivellement General Nouvelle Caledonie', '-', 0),
(101, 5151, 121, 'Nivellement General Nouvelle Caledonie', '-', 0),
(101, 900001, 94, 'Kronstädter Pegel (HN)', 'wird in neuen Bundesländern genutzt', 0),
(101, 900001, 121, 'Kronstädter Pegel (HN)', 'wird in neuen Bundesländern genutzt', 0),
(101, 900002, 94, 'DE_AMST / NH', 'normal heights referred to tide gauge Amsterdam (also known as DHHN92)', 0),
(101, 900002, 121, 'DE_AMST / NH', 'normal heights referred to tide gauge Amsterdam (also known as DHHN92)', 0),
(101, 900003, 94, 'DE_AMST / NOH', 'normal-orthometric heights referred to tide gauge Amsterdam (also known as DHHN85)', 0),
(101, 900003, 121, 'DE_AMST / NOH', 'normal-orthometric heights referred to tide gauge Amsterdam (also known as DHHN85)', 0),
(101, 900004, 94, 'DE_KRON / NH', 'normal heights referred to tide gauge Kronstadt (also known as SNN76)', 0),
(101, 900004, 121, 'DE_KRON / NH', 'normal heights referred to tide gauge Kronstadt (also known as SNN76)', 0),
(102, 4, 94, 'Zoll', 'Zoll', 0),
(102, 4, 121, 'Inch', 'Inch', 0),
(102, 9001, 94, 'Metre', 'Metre', 1),
(102, 9001, 121, 'Meter', 'Meter', 1),
(102, 9002, 94, 'Foot', 'Foot', 0),
(102, 9002, 121, 'Fuss', 'Fuss', 0),
(102, 9036, 94, 'Kilometre', 'Kilometre', 0),
(102, 9036, 121, 'Kilometer', 'Kilometer', 0),
(502, 1, 94, 'creation', 'date identifies when the resource was brought into existence', 1),
(502, 1, 121, 'Erstellung', 'Datum, wann die Quelle geschaffen wurde', 1),
(502, 2, 94, 'publication', 'date identifies when the resource was issued', 0),
(502, 2, 121, 'Publikation', 'Dateum, wann die Quelle publiziert wurde', 0),
(502, 3, 94, 'revision', 'date identifies when the resource was examined or re-examined and improved or amended', 0),
(502, 3, 121, 'letzte Änderung', 'Datum, wann eine Revision durchgeführt wurde', 0),
(505, 1, 94, 'Provider', 'Party that supplies the resource', 0),
(505, 1, 121, 'Anbieter', 'Anbieter der Datenquelle', 0),
(505, 2, 94, 'Custodian', 'Party that accepts accountability and responsibility for the data and ensures appropriate care and maintenance of the resource', 0),
(505, 2, 121, 'Datenverantwortung', 'Verantworlicher für die Datenquelle und deren Pflege', 0),
(505, 3, 94, 'Owner', 'Party that owns the resource', 0),
(505, 3, 121, 'Halter', 'Halter der Datenquelle', 0),
(505, 4, 94, 'User', 'Party who uses the resource', 0),
(505, 4, 121, 'Benutzer', 'Benutzer der Datenquelle', 0),
(505, 5, 94, 'Distributor', 'Party who distributes the resource', 0),
(505, 5, 121, 'Vertrieb', 'Vertreiber der Datenquelle', 0),
(505, 6, 94, 'Originator', 'Party who created the resource', 0),
(505, 6, 121, 'Herkunft', 'Ersteller der Datenquelle', 0),
(505, 7, 94, 'Point of Contact', 'Party who can be contacted for acquiring knowledge about or acquisition of the resource', 1),
(505, 7, 121, 'Auskunft', 'Kontaktperson, die Informationen über die Datenqulle geben kann', 1),
(505, 8, 94, 'Principal Investigator', 'Key party responsible for gathering information and conducting research', 0),
(505, 8, 121, 'Datenerfassung', 'Verantworlicher für die Zusammenstellung der Informationen und Durchführung von Forschung', 0),
(505, 9, 94, 'Processor', 'Party who has processed the data in a manner such that the resource has been modified', 0),
(505, 9, 121, 'Auswertung', 'Bearbeiter der Daten in einer Art, dass die Quelle modifiziert wurde.', 0),
(505, 10, 94, 'Publisher', 'Party who published the resource', 0),
(505, 10, 121, 'Herausgeber', 'Phblizierer der Datenquelle', 0),
(510, 1, 94, 'ucs2', '16-bit fixed size Universal Character Set, based on ISO/IEC 10646', 0),
(510, 1, 121, 'ucs2', '16-bit fixierte Größe Universal Character Set, basierend auf ISO/IEC 10646', 0),
(510, 2, 94, 'ucs4', '32-bit fixed size Universal Character Set, based on ISO/IEC 10646', 0),
(510, 2, 121, 'ucs4', '32-bit fixierte Größe Universal Character Set, basierend auf ISO/IEC 10646', 0),
(510, 3, 94, 'utf7', '7-bit variable size UCS Transfer Format, based on ISO/IEC 10646', 0),
(510, 3, 121, 'utf7', '7-bit variable Größe UCS Transfer Format, basierend auf ISO/IEC 10646', 0),
(510, 4, 94, 'utf8', '8-bit variable size UCS Transfer Format, based on ISO/IEC 10646', 0),
(510, 4, 121, 'utf8', '8-bit variable Größe UCS Transfer Format, basierend auf ISO/IEC 10646', 0),
(510, 5, 94, 'utf16', '16-bit variable size UCS Transfer Format, based on ISO/IEC 10646', 0),
(510, 5, 121, 'utf16', '16-bit variable Größe UCS Transfer Format, basierend auf ISO/IEC 10646', 0),
(510, 6, 94, '8859part1', 'latin-1, west European', 0),
(510, 6, 121, '8859part1', 'latin-1, Westeuropäischer', 0),
(510, 7, 94, '8859part2', 'latin-2, central European', 0),
(510, 7, 121, '8859part2', 'latin-2, Zentraleuropäischer', 0),
(510, 8, 94, '8859part3', 'latin-3, south European', 0),
(510, 8, 121, '8859part3', 'latin-3, Südeuropäischer', 0),
(510, 9, 94, '8859part4', 'latin-4, north European', 0),
(510, 9, 121, '8859part4', 'latin-4, Nordeuropäischer', 0),
(510, 10, 94, '8859part5', 'cyrillic code set', 0),
(510, 10, 121, '8859part5', 'Cyrillisch', 0),
(510, 11, 94, '8859part6', 'arabic code set', 0),
(510, 11, 121, '8859part6', 'Arabisch', 0),
(510, 12, 94, '8859part7', 'greek code set', 0),
(510, 12, 121, '8859part7', 'Griechisch', 0),
(510, 13, 94, '8859part8', 'hebrew code set', 0),
(510, 13, 121, '8859part8', 'Hebräisch', 0),
(510, 14, 94, '8859part9', 'latin-5, Turkish code set', 0),
(510, 14, 121, '8859part9', 'latin-5, Türkisch', 0),
(510, 15, 94, '8859part11', 'thai code set', 0),
(510, 15, 121, '8859part11', 'Tailändisch', 0),
(510, 16, 94, '8859part14', 'latin-8 code set', 0),
(510, 16, 121, '8859part14', 'latin-8', 0),
(510, 17, 94, '8859part15', 'latin-9 code set', 0),
(510, 17, 121, '8859part15', 'latin-9', 0),
(510, 18, 94, 'Jis', 'japanese code set used for electronic transmission', 0),
(510, 18, 121, 'Jis', 'Japanisch', 0),
(510, 19, 94, 'ShiftJIS', 'japanese code set used on MS-DOS based machines', 0),
(510, 19, 121, 'ShiftJIS', 'Japanisch (MS-DOS)', 0),
(510, 20, 94, 'EucJP', 'japanese code set used on UNIX based machines', 0),
(510, 20, 121, 'EucJP', 'Japanisch (UNIX)', 0),
(510, 21, 94, 'UsAscii', 'united states ASCII code set (ISO 646 US)', 0),
(510, 21, 121, 'UsAscii', 'USA ASCII', 0),
(510, 22, 94, 'Ebcdic', 'ibm mainframe code set', 0),
(510, 22, 121, 'Ebcdic', 'IBM Mainfraime', 0),
(510, 23, 94, 'EucKR', 'korean code set', 0),
(510, 23, 121, 'EucKR', 'Koreanisch', 0),
(510, 24, 94, 'big5', 'taiwanese code set', 0),
(510, 24, 121, 'big5', 'Tawainesisch', 0),
(515, 1, 94, 'complexes', 'set of geometric primitives such that their boundaries can be represented as a union of other primitives', 0),
(515, 1, 121, 'Komplex', 'Satz geometrischer Primitiver, wobei deren Grenzen durch ein Zusammenschluss anderer Primitiver gebildet werden können', 0),
(515, 2, 94, 'composites', 'connected set of curves, solids or surfaces', 0),
(515, 2, 121, 'Verbund', 'verbundener Satz von Kurven, Körpern oder Oberflächen', 0),
(515, 3, 94, 'curve', 'bounded, 1-dimensional geometric primitive, representing the continuous image of a line', 0),
(515, 3, 121, 'Kurve', 'beschränkte, 1-D geometrische Primitive, die ein continuierliches Bild einer Linie darstellt', 0),
(515, 4, 94, 'point', 'zero-dimensional geometric primitive, representing a position but not having an extent', 0),
(515, 4, 121, 'Punkt', '0-D geometrische Primitive, welche eine Position beschreibt ohne eine Ausdehnung zu haben', 0),
(515, 5, 94, 'solid', 'bounded, connected 3-dimensional geometric primitive, representing the continuous image of a region of space', 0),
(515, 5, 121, 'Körper', 'begrenzte, verbundene 3-D geometrische Primitive, welche ein kontinuierliches Bild eines Raumes beschreibt', 0),
(515, 6, 94, 'surface', 'bounded, connected 2-dimensional geometric, representing the continuous image of a region of a plane', 0),
(515, 6, 121, 'Oberfläche', 'begrenzte, verbundene 2-D geometrische Primitive, welche ein kontinuierliches Bild einer Ebene oder Region beschreibt', 0),
(517, 1, 94, 'Discipline', 'keyword identifies a branch of instruction or specialized learning', 0),
(517, 1, 121, 'Wissenszweig', 'Zweig einer Ausbildung oder spezialisierten Lernens', 0),
(517, 2, 94, 'Place', 'keyword identifies a location', 0),
(517, 2, 121, 'Ortsangabe', 'Ortsangabe', 0),
(517, 3, 94, 'Stratum', 'keyword identifies the layer(s) of any deposited substance', 0),
(517, 3, 121, 'Schicht', 'Schichten einer hinterlegten Substanz', 0),
(517, 4, 94, 'Temporal', 'keyword identifies a time period related to the dataset', 0),
(517, 4, 121, 'Zeitraum', 'Zeitraum in Bezug auf den Datensatz', 0),
(517, 5, 94, 'Theme', 'keyword identifies a particular subject or topic', 0),
(517, 5, 121, 'Themenbereich', 'Spezielles Fachgebiet oder Thema', 0),
(518, 1, 94, 'continual', 'data is repeatedly and frequently updated', 1),
(518, 1, 121, 'kontinuierlich', 'Datenupdate  häufig', 1),
(518, 2, 94, 'daily', 'Data is updated each day', 0),
(518, 2, 121, 'täglich', 'Datenupdate täglich', 0),
(518, 3, 94, 'weekly', 'Ddata is updated on a weekly basis', 0),
(518, 3, 121, 'wöchentlich', 'Datenupdate wöchentlich', 0),
(518, 4, 94, 'fortnightly', 'Data is updated every two weeks', 0),
(518, 4, 121, 'vierzehntägig', 'Datenupdate alle 2 Wochen', 0),
(518, 5, 94, 'monthly', 'Data is updated each month', 0),
(518, 5, 121, 'monatlich', 'Datenupdate monatlich', 0),
(518, 6, 94, 'quarterly', 'Data is updated every three months', 0),
(518, 6, 121, 'vierteljährlich', 'Datenupdate alle 3 Monate', 0),
(518, 7, 94, 'biannually', 'Data is updated twice each year', 0),
(518, 7, 121, 'halbjährlich', 'Datenupdate halbjährlich', 0),
(518, 8, 94, 'annually', 'Data is updated every year', 0),
(518, 8, 121, 'jährlich', 'Datenupdate jährlich', 0),
(518, 9, 94, 'as Needed', 'Data is updated as deemed necessary', 0),
(518, 9, 121, 'bei Bedarf', 'Datenupdate wenn nötig', 0),
(518, 10, 94, 'irregular', 'Data is updated in intervals that are uneven in duration', 0),
(518, 10, 121, 'unregelmäßig', 'Datenupdate in unregelmäßigen Abständen', 0),
(518, 11, 94, 'notPlanned', 'There are no plans to update the data', 0),
(518, 11, 121, 'einmalig', 'Datenupdate nicht geplant', 0),
(518, 12, 94, 'unknown', 'Frequency of maintenance for the data is not known', 0),
(518, 12, 121, 'unbekannt', 'Datenupdate unbekannt', 0),
(520, 1, 94, 'CD Rom', 'CD Rom', 0),
(520, 1, 121, 'CD-ROM', 'CD-ROM', 0),
(520, 2, 94, 'dvd', 'dvd', 0),
(520, 2, 121, 'DVD', 'DVD', 0),
(520, 3, 94, 'dvd Rom', 'dvd Rom', 0),
(520, 3, 121, 'DVD-ROM', 'DVD-ROM', 0),
(520, 4, 94, '3.5-inch diskette', '3'''' Floppy', 0),
(520, 4, 121, '3,5-Zoll Diskette', '3Â¿Â¿ Floppy', 0),
(520, 5, 94, '5.25-inch diskette', '5 Â¼Â¿Â¿ Floppy', 0),
(520, 5, 121, '5,25-Zoll Diskette', '5 Â¼Â¿Â¿ Floppy', 0),
(520, 6, 94, '7 trackTape', '7 trackTape', 0),
(520, 6, 121, '7 Spur Band', '7 Spur Tape', 0),
(520, 7, 94, '9 track Tape', '9 track Tape', 0),
(520, 7, 121, '9 Spur Band', '9 Spur Tape', 0),
(520, 8, 94, '3480 Cartridge', '3480Cartridge', 0),
(520, 8, 121, '3480 Bandkassette', '3480 Cartridge', 0),
(520, 9, 94, '3490 Cartridge', '3490 Cartridge', 0),
(520, 9, 121, '3490 Bandkassette', '3490 Cartridge', 0),
(520, 10, 94, '3580 Cartridge', '3580Cartridge', 0),
(520, 10, 121, '3580 Bandkassette', '3580 Cartridge', 0),
(520, 11, 94, '4mm Cartridge Tape', '4mm Cartridge Tape', 0),
(520, 11, 121, '4mm Bandkassette', '4mm Cartridge Tape', 0),
(520, 12, 94, '8mm Cartridge Tape', '8mm Cartridge Tape', 0),
(520, 12, 121, '8mm Bandkassette', '8mm Cartridge Tape', 0),
(520, 13, 94, '0.25-inch Cartridge Tape', 'Â¼ `Â¿ Cartridge Tape', 0),
(520, 13, 121, '1/4-Zoll Bandkassette', 'Â¼ `Â¿ Cartridge Tape', 0),
(520, 14, 94, 'Digital Linear Tape', 'digital Linear Tape', 0),
(520, 14, 121, 'Digitales Band', 'Digitales Tape', 0),
(520, 15, 94, 'Online', 'online', 0),
(520, 15, 121, 'Online Link', 'Online Link', 0),
(520, 16, 94, 'Satellite', 'Satellite', 0),
(520, 16, 121, 'Satellitenverbindung', 'Satellitenverbindung', 0),
(520, 17, 94, 'Telephone Link', 'telephone Link', 0),
(520, 17, 121, 'Telefon', 'Telefonverbindung', 0),
(520, 18, 94, 'Hardcopy', 'hardcopy', 1),
(520, 18, 121, 'Papier', 'Kopie (Papier), Druckerzeugnisse usw.', 1),
(520, 900001, 94, 'unknown', '-', 0),
(520, 900001, 121, 'unbekannt (*)', 'für Altdatenübernahme', 0),
(520, 900002, 94, 'faxback', 'faxback', 0),
(520, 900002, 121, 'Faxabruf', 'Faxabruf', 0),
(520, 900003, 94, 'mobile radio', 'mobile radio', 0),
(520, 900003, 121, 'Mobilfunk', 'Mobilfunk', 0),
(520, 900004, 94, 'broadcast', 'broadcast', 0),
(520, 900004, 121, 'Rundfunk', 'Rundfunk und Fernsehen', 0),
(520, 900005, 94, 'video text', 'video text', 0),
(520, 900005, 121, 'Videotext', 'Videotext', 0),
(520, 900006, 94, 'Analog Photography', '-', 0),
(520, 900006, 121, 'analoge Fotografie', '-', 0),
(520, 900007, 94, 'microfilm', '-', 0),
(520, 900007, 121, 'Mikrofilm', '-', 0),
(520, 900008, 94, 'Zip Drive', '-', 0),
(520, 900008, 121, 'ZIP-Laufwerk', '-', 0),
(520, 900009, 94, 'e-mail', '-', 0),
(520, 900009, 121, 'E-Mail', '-', 0),
(520, 900010, 94, 'Info Kiosk', '-', 0),
(520, 900010, 121, 'Infokiosk', '-', 0),
(523, 1, 94, 'completed', 'production of the data has been completed', 1),
(523, 1, 121, 'abgeschlossen', 'Datenproduktion ist abgeschlossen', 1),
(523, 2, 94, 'historicalArchive', 'Data has been stored in an offline storage facility', 0),
(523, 2, 121, 'archiviert', 'Die Daten sind in einem Archiv abgelegt', 0),
(523, 3, 94, 'obsolete', 'Data is no longer relevant', 0),
(523, 3, 121, 'obsolet', 'Die Daten sind nicht mehr länger relevant', 0),
(523, 4, 94, 'onGoing', 'Data is continually being updated', 0),
(523, 4, 121, 'kontinuierlich', 'Daten werden kontinuierlich erneuert', 0),
(523, 5, 94, 'planned', 'fixed date has been established upon or by which the data will be created or updated', 0),
(523, 5, 121, 'geplant', 'Ein Zeitpunkt wurde festgelegt, an dem die Daten kreiert werden', 0),
(523, 6, 94, 'required', 'Data needs to be generated or updated', 0),
(523, 6, 121, 'benötigt', 'Die Daten müssen generiert bzw. erneuert werden', 0),
(523, 7, 94, 'underdevelopment', 'Data is currently in the process of being created', 0),
(523, 7, 121, 'in Erstellung', 'Die Daten werden gerade kreiert', 0),
(523, 900001, 94, 'inOperation', '-', 0),
(523, 900001, 121, 'in Produktion', 'Informationssystem als produktiv genutztes System', 0),
(525, 5, 94, 'Dataset', 'information applies to the dataset', 1),
(525, 5, 121, 'Datensatz', 'Information zu dem Datensatz', 1),
(525, 6, 94, 'Dataseries', 'information applies to the series', 0),
(525, 6, 121, 'Datenserie', 'Information zu der Datenserie', 0),
(526, 1, 94, 'vector', 'vector data is used to represent geographic data', 0),
(526, 1, 121, 'Vektor', 'Geographische Daten werden mittels Verktordaten präsentiert', 0),
(526, 2, 94, 'grid', 'grid data is used to represent geographic data', 0),
(526, 2, 121, 'Rasterdaten', 'Geographische Daten werden mittels Rasterdaten dargestellt', 0),
(526, 3, 94, 'textTable', 'textual or tabular data is used to represent geographic data', 0),
(526, 3, 121, 'Texttabelle', 'Geographische Daten werden mittels Texttabellen dargestellt', 0),
(526, 4, 94, 'TIN', 'triangulated irregular network', 0),
(526, 4, 121, 'TIN', 'Geographische Daten werden mittels eines trinangulären irregulären Netzes dargestellt dargestellt', 0),
(526, 5, 94, 'stereoModel', 'three-dimensional view formed by the intersecting homologous rays of an overlapping pair of images', 0),
(526, 5, 121, 'Stereomodell', 'Geographische Daten werden mittels eines stereosehenden Models dargestellt', 0),
(526, 6, 94, 'video', 'scene from a video recording', 0),
(526, 6, 121, 'Video', 'Geographische Daten werden mittels einer Videoaufnahme dargestellt', 0),
(527, 1, 94, 'Farming', 'rearing of animals and/or cultivation of plants', 0),
(527, 1, 121, 'Landwirtschaft', 'Landwirtschaft (Tierzucht oder Pflanzenanbau)', 0),
(527, 2, 94, 'Biota', 'flora and/or fauna in natural environment. Examples: wildlife, vegetation, biological sciences, ecology, wilderness, sealife, wetlands, habitat', 0),
(527, 2, 121, 'Flora, Fauna, Habitate', 'Flora und Fauna in der natürlichen Umwelt', 0),
(527, 3, 94, 'Boundaries', 'legal land descriptions. Examples: political and administrative boundaries', 0),
(527, 3, 121, 'Grenzen', 'Rechtl. Beschreibungen und Grenzen von Land', 0),
(527, 4, 94, 'Climatology MeteorologyAtmosphere', 'processes and phenomena of the atmosphere. Examples: cloud cover, weather, climate, atmospheric conditions, climate change, precipitation', 0),
(527, 4, 121, 'Klima und Atmosphäre', 'Prozesse und Phänomene der Atmosphäre', 0),
(527, 5, 94, 'Economy', 'economic activities, conditions and employment. Examples: production, labour, revenue, commerce, industry, tourism and ecotourism, forestry, fisheries, commercial or subsistence hunting, etc.', 0),
(527, 5, 121, 'Ökonomie', 'Ökonomische Aktivitäten, Konditionen und beschäftigung', 0),
(527, 6, 94, 'Elevation', 'height above or below sea level. Examples: altitude, bathymetry, digital elevation models, slope, derived products', 0),
(527, 6, 121, 'Höhe', 'Höhe über oder unter NN', 0),
(527, 7, 94, 'Environment', 'environmental resources, protection and conservation. Examples: environmental pollution, waste storage and treatment, environmental impact assessment, monitoring environmental risk, nature reserves, landscape', 1),
(527, 7, 121, 'Umwelt', 'Umweltresourcen, -schutz und -erhaltung', 1),
(527, 8, 94, 'Geoscientific Information', 'information pertaining to earth sciences. Examples: geophysical features and processes, geology, minerals, sciences dealing with the composition, structure and origin of the earthÂ¿s rocks, risks of earthquakes', 0),
(527, 8, 121, 'Geowissenschaft', 'Information die Geowissenshaften betreffend', 0),
(527, 9, 94, 'Health', 'health, health services, human ecology, and safety. Examples: disease and illness, factors affecting health, hygiene, substance abuse, mental and physical health, health services', 0),
(527, 9, 121, 'Gesundheit', 'Gesundheit, Gesundheitsservices, Humanökologie und Sicherheit', 0),
(527, 10, 94, 'Imagery Base Maps EarthCover', 'base maps. Examples: land cover, topographic maps, imagery, unclassified images, annotations', 0),
(527, 10, 121, 'Karten', 'Karten', 0),
(527, 11, 94, 'Intelligence Military', 'military bases, structures, activities. Examples: barracks, training grounds, military transportation, information', 0),
(527, 11, 121, 'Militär', 'Militärische Stützpunkte, Strikturen und Aktivitäten', 0),
(527, 12, 94, 'Inland Waters', 'inland water features, drainage systems and their characteristics. Examples: rivers and glaciers, salt lakes, water utilization plans, dams, currents, floods, water quality, hydrographic charts', 0),
(527, 12, 121, 'Binnengewässer', 'Inlandwasser, Abflüsse und deren Eigenschaften und Charakteristika', 0),
(527, 13, 94, 'Location', 'positional information and services. Examples: addresses, geodetic networks, control points, postal zones and services, place names', 0),
(527, 13, 121, 'Ortsangabe', 'Ortsangaben und -services', 0),
(527, 14, 94, 'Oceans', 'features and characteristics of salt water bodies (excluding inland waters): Examples: tides, tidal waves, coastal information, reefs', 0),
(527, 14, 121, 'Ozeane', 'Ozeane und deren Charakteristika und Eigenschaften', 0),
(527, 15, 94, 'PlanningCadastre', 'information used for appropriate actions for future use of the land Examples: land use maps, zoning maps, cadastral surveys, land ownership', 0),
(527, 15, 121, 'Kataster', 'Informationen zu angemessenem Handeln für die zukünftige Nutzung des Landes', 0),
(527, 16, 94, 'Society', 'characteristics of society and cultures. Examples: settlements, anthropology, archaeology, education, traditionalbeliefs, manners and customs, demographic data, recreational areas and activities,', 0),
(527, 16, 121, 'Gesellschaft', 'Charakteristika von Gesellschaft und Kulturen', 0),
(527, 17, 94, 'Structure', 'man-made construction. Examples: buildings, museums, churches, factories, housing, monuments, shops, towers', 0),
(527, 17, 121, 'Bau', 'Kontsruktionen (von Menschen geshaffen)', 0),
(527, 18, 94, 'Transportation', 'means and aids for conveying persons and/or goods. Examples: roads, airports/airstrips, shipping routes, tunnels, nautical charts, vehicle or vessel location, aeronautical charts, railways', 0),
(527, 18, 121, 'Transport', 'Hilfsmittel und Geräte zum Befördern von Personen und/oder Gütern', 0),
(527, 19, 94, 'Utilities Communication', 'energy, water and waste systems and communications infrastructure and services. Examples: hydroelectricity, geothermal, solar and nuclear sources of energy, water purification and distribution, sewage collection and disposal, etc.', 0),
(527, 19, 121, 'Energie und Kommunikation', 'Energie-, Wasser- und Abfallsysteme sowie Kommunikationsinfrastruktur und Services.', 0),
(528, 1, 94, 'geometryOnly', 'geometry objects without any additional structure which describes topology', 1),
(528, 1, 121, 'geometrisch', 'Geometrisch Objekte ohn jegliche Struktur, die die Topologie beschreibt', 1),
(528, 2, 94, 'topology1D', '1-dimensional topological complex', 0),
(528, 2, 121, 'Topologie (1D)', '1-dimensionaler topologischer Komplex', 0),
(528, 3, 94, 'planarGraph', '1-dimensional topological complex which is planar', 0),
(528, 3, 121, 'Planar (1D)', '1-dimensionaler topologischer Komplex, der eine Fläche ist', 0),
(528, 4, 94, 'fullPlanarGraph', '2-dimensional topological complex which is planar', 0),
(528, 4, 121, 'Planar (2D)', '2-dimensionaler topologischer Komplex, der eine Fläche ist', 0),
(528, 5, 94, 'surfaceGraph', '1-dimensional topological complex which is isomorphic to a subset of a surface', 0),
(528, 5, 121, 'Oberflächengraph (1D)', '1-dimensionaler topologischer Komplex, der isomorph zu einer Untermenge einer Oberfläche ist', 0),
(528, 6, 94, 'fullSurfaceGraph', '2-dimensional topological complex which is isomorphic to a subset of a surface', 0),
(528, 6, 121, 'Oberfläche(2D)', '2-dimensionaler topologischer Komplex, der isomorph zu einer Untermenge einer Oberfläche ist', 0),
(528, 7, 94, 'topology3D', '3-dimensional topological complex', 0),
(528, 7, 121, 'Topologie (3D)', '3-dimensionaler topologischer Komplex', 0),
(528, 8, 94, 'fullTopology3D', 'complete coverage of a 3D coordinate space', 0),
(528, 8, 121, 'Raum (3D)', 'Komplette Abdeckung eines 3-D Koordinatenraumes', 0),
(528, 9, 94, 'abstract', 'topological complex without any specified geometric realization', 0),
(528, 9, 121, 'Abstrakte Topologie', 'Topologischer Komplex ohne jegliche spezifische geometrische Realisierung', 0),
(99999999, 94, 94, 'English', '-', 0),
(99999999, 94, 121, 'Englisch', '-', 0),
(99999999, 121, 94, 'German', '-', 1),
(99999999, 121, 121, 'Deutsch', '-', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ingrid_tiny_url`
--

DROP TABLE IF EXISTS `ingrid_tiny_url`;
CREATE TABLE IF NOT EXISTS `ingrid_tiny_url` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_ref` varchar(254) NOT NULL,
  `tiny_key` varchar(254) NOT NULL,
  `tiny_name` varchar(254) NOT NULL,
  `tiny_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `tiny_config` mediumtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `ingrid_tiny_url`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `jetspeed_service`
--

DROP TABLE IF EXISTS `jetspeed_service`;
CREATE TABLE IF NOT EXISTS `jetspeed_service` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `jetspeed_service`
--

INSERT INTO `jetspeed_service` (`ID`, `APPLICATION_ID`, `NAME`) VALUES
(1, 1, 'IdGenerator'),
(2, 1, 'PortletRegistryComponent'),
(3, 1, 'PageManager'),
(4, 1, 'TemplateLocator'),
(5, 1, 'DecorationLocator'),
(6, 1, 'Powertools'),
(7, 1, 'DecorationFactory'),
(8, 1, 'EntityAccessor'),
(9, 1, 'WindowAccessor'),
(10, 1, 'Desktop'),
(11, 1, 'decorationContentCache'),
(12, 1, 'portletContentCache'),
(13, 1, 'PortalConfiguration'),
(22, 3, 'UserManager'),
(23, 3, 'RoleManager'),
(24, 3, 'GroupManager'),
(25, 3, 'Profiler'),
(26, 3, 'PortletRegistryComponent'),
(27, 3, 'PageManager'),
(28, 3, 'PortalAdministration'),
(29, 3, 'PermissionManager'),
(41, 21, 'UserManager'),
(42, 21, 'RoleManager'),
(43, 21, 'GroupManager'),
(44, 21, 'Profiler'),
(45, 21, 'PortletRegistryComponent'),
(46, 21, 'PageManager'),
(47, 21, 'PortalAdministration'),
(48, 21, 'PermissionManager');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `language`
--

DROP TABLE IF EXISTS `language`;
CREATE TABLE IF NOT EXISTS `language` (
  `ID` int(11) NOT NULL,
  `PORTLET_ID` int(11) NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `SUPPORTED_LOCALE` smallint(6) NOT NULL,
  `TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SHORT_TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `KEYWORDS` mediumtext COLLATE utf8_unicode_ci,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `language`
--

INSERT INTO `language` (`ID`, `PORTLET_ID`, `LOCALE_STRING`, `SUPPORTED_LOCALE`, `TITLE`, `SHORT_TITLE`, `KEYWORDS`) VALUES
(1, 1, 'en,,', 0, 'IngridClearLayout', 'IngridClearLayout', NULL),
(2, 2, 'en,,', 0, 'IngridOneColumn', 'OneColumn', NULL),
(3, 3, 'en,,', 0, 'IngridTwoColumns', 'IngridTwoColumns', NULL),
(4, 4, 'en,,', 0, 'SimpleLayoutPortlet', 'SimpleLayout', NULL),
(5, 5, 'en,,', 0, 'One Column', 'OneColumn', NULL),
(6, 6, 'en,,', 0, 'One Column with Tables', 'OneColumnTable', NULL),
(7, 7, 'en,,', 0, 'Two Columns', 'TwoColumns', NULL),
(8, 8, 'en,,', 0, 'Two Columns (15%/85%)', 'Two Columns (15%/85%)', NULL),
(9, 9, 'en,,', 0, 'Three Columns', 'ThreeColumns', NULL),
(10, 10, 'en,,', 0, 'Three Columns with Tables', 'ThreeColumnsTable', NULL),
(11, 11, 'en,,', 0, 'One Column - No Actions', 'OneColumnNoActions', NULL),
(12, 12, 'en,,', 0, 'Two Columns - No Actions', 'TwoColumnsNoActions', NULL),
(13, 13, 'en,,', 0, 'Three Columns - No Actions', 'ThreeColumnsNoActions', NULL),
(14, 14, 'en,,', 0, 'Two Columns (25%/75%) No Actions', 'VelocityTwoColumns2575NoActions', NULL),
(15, 15, 'en,,', 0, 'Two Columns (25%/75%)', 'VelocityTwoColumns2575', NULL),
(16, 16, 'en,,', 0, 'Two Columns (15%,85%) No Actions', '2 Columns 15,85 No Actions', NULL),
(17, 17, 'en,,', 0, 'Two Columns with Tables', 'Two Columns Tables', NULL),
(18, 18, 'en,,', 0, 'Four Columns', 'FourColumns', NULL),
(121, 101, 'en,,', 0, 'mdek', 'mdek', 'mdek'),
(122, 102, 'en,,', 0, 'mdek', 'mdek', 'mdek'),
(123, 103, 'en,,', 0, 'mdek', 'mdek', 'mdek'),
(141, 121, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information'),
(142, 122, 'en,,', 0, 'javascript detection portlet', 'javascript detection portlet', NULL),
(143, 123, 'en,,', 0, 'Help Portlet', 'Help Portlet', 'Help Portlet'),
(144, 124, 'en,,', 0, 'Jobübersicht', 'Jobübersicht', NULL),
(145, 125, 'en,,', 0, 'RSS Feeds administrieren', 'RSS Feeds administrieren', NULL),
(146, 126, 'en,,', 0, 'Partner administrieren', 'Partner administrieren', NULL),
(147, 127, 'en,,', 0, 'Anbieter administrieren', 'Anbieter administrieren', NULL),
(148, 128, 'en,,', 0, 'Benutzer administrieren', 'Benutzer administrieren', NULL),
(149, 129, 'en,,', 0, 'iPlugs/iBus administrieren', 'iPlugs/iBus administrieren', NULL),
(150, 130, 'en,,', 0, 'Inhalte administrieren', 'Inhalte administrieren', NULL),
(151, 131, 'en,,', 0, 'WMS Administration', 'WMS Administration', NULL),
(152, 132, 'en,,', 0, 'Statistiken', 'Statistiken', NULL),
(153, 133, 'en,,', 0, 'Startseite administrieren', 'Startseite administrieren', NULL),
(154, 134, 'en,,', 0, 'Portal Profile', 'Portal Profile', NULL),
(155, 135, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information'),
(156, 136, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information'),
(157, 137, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information'),
(158, 138, 'en,,', 0, 'Portal User Migration', 'Portal User Migration', NULL),
(159, 139, 'en,,', 0, 'Search', 'Search', 'Search'),
(160, 140, 'en,,', 0, 'Environmental Topics', 'Environmental Topics', 'Environmental Topics'),
(161, 141, 'en,,', 0, 'Other News', 'RssNews', 'RssNews'),
(162, 142, 'en,,', 1, 'Other News', 'RssNews', 'RssNews'),
(163, 142, 'de,,', 1, 'Weitere Meldungen', 'RssNews', 'RssNews'),
(164, 143, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information'),
(165, 144, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information'),
(166, 145, 'en,,', 0, 'Info Portlet', 'Info Portlet', 'Information'),
(167, 146, 'en,,', 0, 'Service', 'Service', 'Service'),
(168, 147, 'en,,', 0, 'Environmental Monitoring Data', 'Environmental Monitoring Data', 'Environmental Monitoring Data'),
(169, 148, 'en,,', 0, 'Environmental Almanac', 'Environmental Almanac', 'Environmental Almanac'),
(170, 149, 'en,,', 0, 'Weather warning', 'Weather warning', 'Weather warning'),
(171, 150, 'en,,', 1, 'Contact', 'Contact', 'Contact'),
(172, 150, 'de,,', 1, 'Kontakt', 'Kontakt', 'Kontakt'),
(173, 151, 'en,,', 1, 'Newsletter', 'Newsletter', 'Newsletter'),
(174, 151, 'de,,', 1, 'Newsletter', 'Newsletter', 'Newsletter'),
(175, 152, 'en,,', 1, 'Login', 'Login', 'Login'),
(176, 152, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung'),
(177, 153, 'en,,', 1, 'Login', 'Login', 'Login'),
(178, 153, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung'),
(179, 154, 'en,,', 1, 'Login', 'Login', 'Login'),
(180, 154, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung'),
(181, 155, 'en,,', 1, 'Login', 'Login', 'Login'),
(182, 155, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung'),
(183, 156, 'en,,', 1, 'Login', 'Login', 'Login'),
(184, 156, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung'),
(185, 157, 'en,,', 1, 'Login', 'Login', 'Login'),
(186, 157, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung'),
(187, 158, 'en,,', 1, 'Login', 'Login', 'Login'),
(188, 158, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung'),
(189, 159, 'en,,', 1, 'Login', 'Login', 'Login'),
(190, 159, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung'),
(191, 160, 'en,,', 1, 'Login', 'Login', 'Login'),
(192, 160, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung'),
(193, 161, 'en,,', 1, 'Login', 'Login', 'Login'),
(194, 161, 'de,,', 1, 'Anmeldung', 'Anmeldung', 'Anmeldung'),
(195, 162, 'en,,', 1, 'Information Providers', 'Information Providers', 'Information Providers'),
(196, 162, 'de,,', 1, 'Informationsanbieter', 'Informationsanbieter', 'Informationsanbieter'),
(197, 163, 'en,,', 1, 'Data sources', 'Data sources', 'Data sources'),
(198, 163, 'de,,', 1, 'Datenquellen', 'Datenquellen', 'Datenquellen'),
(199, 164, 'en,,', 1, 'Service', 'Service', 'Service'),
(200, 164, 'de,,', 1, 'Service', 'Service', 'Service'),
(201, 165, 'en,,', 0, 'Service', 'Service', 'Service'),
(202, 166, 'en,,', 1, 'Environmental Monitoring Data', 'Environmental Monitoring Data', 'Environmental Monitoring Data'),
(203, 166, 'de,,', 1, 'Messwerte', 'Messwerte', 'Messwerte'),
(204, 167, 'en,,', 0, 'Environmental Monitoring Data', 'Environmental Monitoring Data', 'Environmental Monitoring Data'),
(205, 168, 'en,,', 1, 'Environmental Topics', 'Environmental Topics', 'Environmental Topics'),
(206, 168, 'de,,', 1, 'Themen', 'Themen', 'Themen'),
(207, 169, 'en,,', 0, 'Environmental Topics', 'Environmental Topics', 'Environmental Topics'),
(208, 170, 'en,,', 0, 'Kartenabschnitt als feste URLs', 'SaveMapsPortlet', NULL),
(209, 171, 'en,,', 0, 'Karten', 'ShowMapsPortlet', NULL),
(210, 172, 'en,,', 1, 'Environmental Almanac', 'Environmental Almanac', 'Environmental Almanac'),
(211, 172, 'de,,', 1, 'Chronik', 'Chronik', 'Chronik'),
(212, 173, 'en,,', 0, 'Environmental Almanac', 'Environmental Almanac', 'Environmental Almanac'),
(213, 174, 'en,,', 0, 'Search', 'Search', 'Search'),
(214, 175, 'en,,', 0, 'Search', 'Search', 'Search'),
(215, 176, 'en,,', 0, 'Search', 'Search', 'Search'),
(216, 177, 'en,,', 0, 'Search Result', 'Search Result', 'Search Result'),
(217, 178, 'en,,', 0, 'Search Result', 'Search Result', 'Search Result'),
(218, 179, 'en,,', 0, 'Search', 'Search', 'Search'),
(219, 180, 'en,,', 0, 'Data Catalogues', 'Data Catalogues', 'Data Catalogues'),
(220, 181, 'en,,', 0, 'Data Catalogues', 'Data Catalogues', 'Data Catalogues'),
(221, 182, 'en,,', 0, 'Data Catalogues', 'Data Catalogues', 'Data Catalogues'),
(222, 183, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(223, 184, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(224, 185, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(225, 186, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(226, 187, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(227, 188, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(228, 189, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(229, 190, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(230, 191, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(231, 192, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(232, 193, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(233, 194, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(234, 195, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(235, 196, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(236, 197, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(237, 198, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(238, 199, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(239, 200, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(240, 201, 'en,,', 0, 'Advanced Search', 'Advanced', 'Advanced'),
(241, 202, 'en,,', 1, 'Feature Overview', 'Feature', 'feature'),
(242, 202, 'de,,', 1, 'Funktionen in der Übersicht', 'Feature', 'feature');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `link`
--

DROP TABLE IF EXISTS `link`;
CREATE TABLE IF NOT EXISTS `link` (
  `LINK_ID` int(11) NOT NULL,
  `PARENT_ID` int(11) NOT NULL,
  `PATH` varchar(240) COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `VERSION` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SHORT_TITLE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `IS_HIDDEN` smallint(6) NOT NULL,
  `SKIN` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TARGET` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `URL` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SUBSITE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ROLE_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `GROUP_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `MEDIATYPE` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `EXT_ATTR_NAME` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `EXT_ATTR_VALUE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `OWNER_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`LINK_ID`),
  UNIQUE KEY `UN_LINK_1` (`PATH`),
  KEY `IX_LINK_1` (`PARENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `link`
--

INSERT INTO `link` (`LINK_ID`, `PARENT_ID`, `PATH`, `NAME`, `VERSION`, `TITLE`, `SHORT_TITLE`, `IS_HIDDEN`, `SKIN`, `TARGET`, `URL`, `SUBSITE`, `USER_PRINCIPAL`, `ROLE_PRINCIPAL`, `GROUP_PRINCIPAL`, `MEDIATYPE`, `LOCALE`, `EXT_ATTR_NAME`, `EXT_ATTR_VALUE`, `OWNER_PRINCIPAL`) VALUES
(1, 1, '/language.link', 'language.link', NULL, 'ingrid.page.language', 'ingrid.page.language', 0, NULL, 'top', '/default-page.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 1, '/webmaster.link', 'webmaster.link', NULL, 'ingrid.page.webmaster.tooltip', 'ingrid.page.webmaster', 0, NULL, 'top', 'mailto:webmaster@portalu.de', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `link_constraint`
--

DROP TABLE IF EXISTS `link_constraint`;
CREATE TABLE IF NOT EXISTS `link_constraint` (
  `CONSTRAINT_ID` int(11) NOT NULL,
  `LINK_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `USER_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ROLE_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `GROUP_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PERMISSIONS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`CONSTRAINT_ID`),
  KEY `IX_LINK_CONSTRAINT_1` (`LINK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `link_constraint`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `link_constraints_ref`
--

DROP TABLE IF EXISTS `link_constraints_ref`;
CREATE TABLE IF NOT EXISTS `link_constraints_ref` (
  `CONSTRAINTS_REF_ID` int(11) NOT NULL,
  `LINK_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `UN_LINK_CONSTRAINTS_REF_1` (`LINK_ID`,`NAME`),
  KEY `IX_LINK_CONSTRAINTS_REF_1` (`LINK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `link_constraints_ref`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `link_metadata`
--

DROP TABLE IF EXISTS `link_metadata`;
CREATE TABLE IF NOT EXISTS `link_metadata` (
  `METADATA_ID` int(11) NOT NULL,
  `LINK_ID` int(11) NOT NULL,
  `NAME` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`METADATA_ID`),
  UNIQUE KEY `UN_LINK_METADATA_1` (`LINK_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_LINK_METADATA_1` (`LINK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `link_metadata`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `locale_encoding_mapping`
--

DROP TABLE IF EXISTS `locale_encoding_mapping`;
CREATE TABLE IF NOT EXISTS `locale_encoding_mapping` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `ENCODING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `locale_encoding_mapping`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `localized_description`
--

DROP TABLE IF EXISTS `localized_description`;
CREATE TABLE IF NOT EXISTS `localized_description` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `OWNER_CLASS_NAME` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `DESCRIPTION` mediumtext COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `localized_description`
--

INSERT INTO `localized_description` (`ID`, `OWNER_ID`, `OWNER_CLASS_NAME`, `DESCRIPTION`, `LOCALE_STRING`) VALUES
(1, 4, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The SimpleLayoutPortlet requires a ViewPage layout template set through the portlet preferences which provides its own layout algorithm', 'en,,'),
(2, 1, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'Jetspeed 2 Layout Portlets Applications', ',,'),
(177, 179, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'Entry point to the mdek application', 'en,,'),
(178, 180, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'Catalog administration portlet for the portal admin', 'en,,'),
(179, 181, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'SuperAdmin-Login to each Catalog with each User', 'en,,'),
(180, 3, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'PortalU Application', ',,'),
(181, 201, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(182, 121, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,'),
(183, 122, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet is called only via javascript', 'en,,'),
(184, 202, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(185, 123, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays help content.', 'en,,'),
(186, 203, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(187, 124, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the monitor component of the portlet.', 'en,,'),
(188, 204, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(189, 125, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the editor for the RSS feeds.', 'en,,'),
(190, 205, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(191, 126, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the editor for the Partners.', 'en,,'),
(192, 206, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(193, 127, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the editor for the Providers.', 'en,,'),
(194, 207, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(195, 208, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This is the template in which you setup an email to be sent after user exists', 'en,,'),
(196, 209, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the roles a new user will be registered with', 'en,,'),
(197, 210, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the groups a new user will be registered with', 'en,,'),
(198, 211, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the rule names a new user will be registered with', 'en,,'),
(199, 212, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the rule values a new user will be registered with', 'en,,'),
(200, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the user administration.', 'en,,'),
(201, 213, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(202, 129, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the editor for the iPlugs.', 'en,,'),
(203, 214, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(204, 130, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the editor for the free text content.', 'en,,'),
(205, 215, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(206, 131, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the wms admin interface links.', 'en,,'),
(207, 216, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(208, 132, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the links to several statistics.', 'en,,'),
(209, 217, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(210, 133, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the home page personalization dialog.', 'en,,'),
(211, 218, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(212, 134, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the portal profile admin interface.', 'en,,'),
(213, 219, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(214, 135, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,'),
(215, 220, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(216, 136, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,'),
(217, 221, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(218, 137, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,'),
(219, 222, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(220, 138, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Portlet displays the portal user migration interface.', 'en,,'),
(221, 223, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(222, 139, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes a search box for the home AND main search page.', 'en,,'),
(223, 224, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(224, 140, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the rubrics of the Environment catalogue page on the home page for quick search.', 'en,,'),
(225, 225, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(226, 141, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the rss news list.', 'en,,'),
(227, 226, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(228, 142, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the rss news list.', 'en,,'),
(229, 227, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(230, 143, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays a content entry from the cms, specified by the preference ''cmsKey''.', 'en,,'),
(231, 228, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(232, 144, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,'),
(233, 229, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(234, 145, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'The Info Portlet displays useful information to the user. The Information template and title can be set via preferences.', 'en,,'),
(235, 230, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(236, 146, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the rubrics of the Service page on the home page for quick search.', 'en,,'),
(237, 231, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(238, 147, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the rubrics of the Measures page on the home page for quick search.', 'en,,'),
(239, 232, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(240, 148, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays an anniversary event on the home page.', 'en,,'),
(241, 233, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(242, 149, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the weather.', 'en,,'),
(243, 234, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(244, 150, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes an environment search box.', 'en,,'),
(245, 235, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(246, 151, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'TODO: Describe the portlet.', 'en,,'),
(247, 236, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(248, 152, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes the login dialog.', 'en,,'),
(249, 237, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(250, 153, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal overview.', 'en,,'),
(251, 238, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(252, 239, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the url to which folks will return after they receive an email', 'en,,'),
(253, 240, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This is the template in which you setup an email to be sent after user exists', 'en,,'),
(254, 241, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the rule names a new user will be registered with', 'en,,'),
(255, 242, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This option defines the rule values a new user will be registered with', 'en,,'),
(256, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal create account dialog.', 'en,,'),
(257, 243, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(258, 155, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal edit account dialog.', 'en,,'),
(259, 244, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(260, 245, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This is the template in which you setup an email to be sent after user exists', 'en,,'),
(261, 156, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal password forgotten dialog.', 'en,,'),
(262, 246, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(263, 157, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal personalization dialog: OverView.', 'en,,'),
(264, 247, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(265, 158, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal personalization dialog: home.', 'en,,'),
(266, 248, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(267, 159, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal personalization dialog: Partner.', 'en,,'),
(268, 249, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(269, 160, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal personalization dialog: Sources.', 'en,,'),
(270, 250, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(271, 161, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the myportal personalization dialog: SearchSettings.', 'en,,'),
(272, 251, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(273, 162, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'TODO: describe the portlet', 'en,,'),
(274, 252, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(275, 163, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'TODO: describe the portlet', 'en,,'),
(276, 253, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(277, 164, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes a service search box.', 'en,,'),
(278, 254, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(279, 165, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results of the service search (service catalogue).', 'en,,'),
(280, 255, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(281, 166, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes a service search box.', 'en,,'),
(282, 256, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(283, 167, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results of the measures search (measures catalogue).', 'en,,'),
(284, 257, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(285, 168, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays an environment search box.', 'en,,'),
(286, 258, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(287, 169, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results of the environment topics search (environment topics catalogue).', 'en,,'),
(288, 259, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(289, 170, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results.', 'en,,'),
(290, 260, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(291, 171, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results.', 'en,,'),
(292, 261, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(293, 172, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes a environment search box.', 'en,,'),
(294, 262, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(295, 173, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results of the environment chronicle search.', 'en,,'),
(296, 263, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(297, 174, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays and handles the search settings.', 'en,,'),
(298, 264, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(299, 175, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays and handles the search save dialog.', 'en,,'),
(300, 265, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(301, 176, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays and handles the search history.', 'en,,'),
(302, 266, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(303, 177, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the "Similar Terms" fragment of the search result page.', 'en,,'),
(304, 267, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(305, 178, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results.', 'en,,'),
(306, 268, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(307, 179, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search detail for dsc results.', 'en,,'),
(308, 269, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(309, 180, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the browsing of the UDK in the catalog search.', 'en,,'),
(310, 270, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(311, 181, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the Search in the UDKs via Thesaurus browsing.', 'en,,'),
(312, 271, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(313, 182, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displays the search results found via Thesaurus Browsing.', 'en,,'),
(314, 272, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(315, 183, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the terms in the extended search for environment info.', 'en,,'),
(316, 273, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(317, 184, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the thesaurus terms in the extended search.', 'en,,'),
(318, 274, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(319, 185, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the geothesaurus terms in the extended search.', 'en,,'),
(320, 275, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(321, 186, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the map page in the extended search.', 'en,,'),
(322, 276, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(323, 187, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the time constraint fragment in the extended search.', 'en,,'),
(324, 277, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(325, 188, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the time references (chronicle) in the extended search.', 'en,,'),
(326, 278, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(327, 189, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the "search area"/"contents" fragment in the extended search.', 'en,,'),
(328, 279, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(329, 190, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the "searcharea"/"sources" fragment in the extended search.', 'en,,'),
(330, 280, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(331, 191, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the "envinfo"/"searcharea"/"partner" fragment in the extended search.', 'en,,'),
(332, 281, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(333, 192, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the terms in the extended search for addresses.', 'en,,'),
(334, 282, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(335, 193, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the mode in the extended search for addresses.', 'en,,'),
(336, 283, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(337, 194, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the place reference in the extended search for addresses.', 'en,,'),
(338, 284, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(339, 195, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the "addresses"/"searcharea"/"partner" fragment in the extended search.', 'en,,'),
(340, 285, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(341, 196, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the terms in the extended search for research.', 'en,,'),
(342, 286, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(343, 197, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the attributes in the extended search for research.', 'en,,'),
(344, 287, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(345, 198, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the terms in the extended search for law.', 'en,,'),
(346, 288, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(347, 199, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the thesaurus terms in the extended search.', 'en,,'),
(348, 289, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(349, 200, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet handles the input of the "law"/"searcharea"/"partner" fragment in the extended search.', 'en,,'),
(350, 290, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(351, 201, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet displayes a combo box containing different languages.', 'en,,'),
(352, 291, 'org.apache.jetspeed.om.portlet.impl.InitParamImpl', 'This parameter sets the template used in view mode.', 'en,,'),
(353, 202, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'This portlet explains all the features the portal has to offer.', 'en,,'),
(354, 21, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'PortalU Application', ',,');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `localized_display_name`
--

DROP TABLE IF EXISTS `localized_display_name`;
CREATE TABLE IF NOT EXISTS `localized_display_name` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `OWNER_CLASS_NAME` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DISPLAY_NAME` mediumtext COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `localized_display_name`
--

INSERT INTO `localized_display_name` (`ID`, `OWNER_ID`, `OWNER_CLASS_NAME`, `DISPLAY_NAME`, `LOCALE_STRING`) VALUES
(1, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Layout for Ingrid Portal Using Velocity, no layout decoration', 'en,,'),
(2, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'One Column Layout for Ingrid Portal Using Velocity', 'en,,'),
(3, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns Layout for Ingrid Portal Using Velocity', 'en,,'),
(4, 4, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Simple readonly fixed Layout', 'en,,'),
(5, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'One Column', 'en,,'),
(6, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity????? 1 ??????', 'ja,,'),
(7, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,,'),
(8, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,TW,'),
(9, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'One Column with Tables', 'en,,'),
(10, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ?????????? 1 ??????', 'ja,,'),
(11, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????', 'zh,,'),
(12, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????', 'zh,TW,'),
(13, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns', 'en,,'),
(14, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ????? 2 ??????', 'ja,,'),
(15, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,,'),
(16, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,TW,'),
(17, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns (15%/85%)', 'en,,'),
(18, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ?????????????????????????????? 2 ?', 'ja,,'),
(19, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????????', 'zh,,'),
(20, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????????', 'zh,TW,'),
(21, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Three Columns', 'en,,'),
(22, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ????? 3 ??????', 'ja,,'),
(23, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?3???', 'zh,,'),
(24, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?3???', 'zh,TW,'),
(25, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Three Columns with Tables', 'en,,'),
(26, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ?????????? 3 ??????', 'ja,,'),
(27, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???3???', 'zh,,'),
(28, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???3???', 'zh,TW,'),
(29, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'One Column - No Actions', 'en,,'),
(30, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??????????? Velocity ????? 1 ??????', 'ja,,'),
(31, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,,'),
(32, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,TW,'),
(33, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns Layout - No Actions', 'en,,'),
(34, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??????????? Velocity ????? 2 ??????', 'ja,,'),
(35, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,,'),
(36, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,TW,'),
(37, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Three Columns - No Actions', 'en,,'),
(38, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??????????? Velocity ????? 3 ??????', 'ja,,'),
(39, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?3???', 'zh,,'),
(40, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?3???', 'zh,TW,'),
(41, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns (25%/75%) No Actions', 'en,,'),
(42, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??????????? Velocity ????? 2 ??????', 'ja,,'),
(43, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,,'),
(44, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity?????', 'zh,TW,'),
(45, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns (25%/75%)', 'en,,'),
(46, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ????? 25/75 ? 2 ??????', 'ja,,'),
(47, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,,'),
(48, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity?????', 'zh,TW,'),
(49, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns (15%,85%) No Actions', 'en,,'),
(50, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '???????? Velocity ?????????????????????????????? 2 ?', 'ja,,'),
(51, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity???????????', 'zh,,'),
(52, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '?????????Velocity???????????', 'zh,TW,'),
(53, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Two Columns with Tables', 'en,,'),
(54, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Velocity ?????????? 2 ??????', 'ja,,'),
(55, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????', 'zh,,'),
(56, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???????', 'zh,TW,'),
(57, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Four Columns', 'en,,'),
(58, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???4???', 'zh,,'),
(59, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', '??Velocity???4???', 'zh,TW,'),
(60, 1, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'Jetspeed 2 Layout Portlets Application', ',,'),
(144, 101, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Entry to Mdek App', 'en,,'),
(145, 102, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Kataloge administrieren', 'en,,'),
(146, 103, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'SuperAdmin-Login', 'en,,'),
(147, 3, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'PortalU Application', ',,'),
(161, 121, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Generisches Informationsportlet', 'en,,'),
(162, 122, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'javascript detection portlet', 'en,,'),
(163, 123, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Help Portlet', 'en,,'),
(164, 124, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminComponentMonitorPortlet', 'en,,'),
(165, 125, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Content RSS Portlet', 'en,,'),
(166, 126, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Content Partner Portlet', 'en,,'),
(167, 127, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Content Provider Portlet', 'en,,'),
(168, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminUserPortlet', 'en,,'),
(169, 129, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminIPlugPortlet', 'en,,'),
(170, 130, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminCMSPortlet', 'en,,'),
(171, 131, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminWMSPortlet', 'en,,'),
(172, 132, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminStatisticsPortlet', 'en,,'),
(173, 133, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminHomepagePortlet', 'en,,'),
(174, 134, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminPortalProfilePortlet', 'en,,'),
(175, 135, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalEditAboutInfoPortlet', 'en,,'),
(176, 136, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalEditNewsletterInfoPortlet', 'en,,'),
(177, 137, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalEditAdvancedInfoPortlet', 'en,,'),
(178, 138, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'AdminUserMigrationPortlet', 'en,,'),
(179, 139, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Einfache Suche', 'en,,'),
(180, 140, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Environment Teaser', 'en,,'),
(181, 141, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Aktuelles', 'en,,'),
(182, 142, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Weitere Meldungen', 'en,,'),
(183, 143, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'CMS portlet', 'en,,'),
(184, 144, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Generisches Informationsportlet', 'en,,'),
(185, 145, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Generisches Willkommens-Portlet', 'en,,'),
(186, 146, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Service Teaser', 'en,,'),
(187, 147, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Measures Teaser', 'en,,'),
(188, 148, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Chronik Teaser', 'en,,'),
(189, 149, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'WeatherTeaser', 'en,,'),
(190, 150, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Contact', 'en,,'),
(191, 151, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ContactNewsletterPortlet', 'en,,'),
(192, 152, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalLoginPortlet', 'en,,'),
(193, 153, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalOverviewPortlet', 'en,,'),
(194, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalCreateAccountPortlet', 'en,,'),
(195, 155, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalEditAccountPortlet', 'en,,'),
(196, 156, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPasswordForgottenPortlet', 'en,,'),
(197, 157, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPersonalizeOverviewPortlet', 'en,,'),
(198, 158, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPersonalizeHomePortlet', 'en,,'),
(199, 159, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPersonalizePartnerPortlet', 'en,,'),
(200, 160, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPersonalizeSourcesPortlet', 'en,,'),
(201, 161, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MyPortalPersonalizeSearchSettingsPortlet', 'en,,'),
(202, 162, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ShowPartnerPortlet', 'en,,'),
(203, 163, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ShowDataSourcePortlet', 'en,,'),
(204, 164, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Service', 'en,,'),
(205, 165, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Service Suchergebnis', 'en,,'),
(206, 166, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Messwerte', 'en,,'),
(207, 167, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Messwerte Suchergebnis', 'en,,'),
(208, 168, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Themen', 'en,,'),
(209, 169, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Themen Suchergebnis', 'en,,'),
(210, 170, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'KartenLinkSave', 'en,,'),
(211, 171, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Karten', 'en,,'),
(212, 172, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Chronik', 'en,,'),
(213, 173, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Chronik Suchergebnis', 'en,,'),
(214, 174, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Sucheinstellungen', 'en,,'),
(215, 175, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'SearchSavePortlet', 'en,,'),
(216, 176, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Suchhistorie', 'en,,'),
(217, 177, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Similar Terms', 'en,,'),
(218, 178, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Suchergebnis', 'en,,'),
(219, 179, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Detailinformation', 'en,,'),
(220, 180, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'KatalogSucheHierarchiebaum', 'en,,'),
(221, 181, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'KatalogSucheThesaurus', 'en,,'),
(222, 182, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'KatalogSucheThesaurusErgebnisse', 'en,,'),
(223, 183, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwThemaSuchbegriffe', 'en,,'),
(224, 184, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwThemaFachwoerterbuch', 'en,,'),
(225, 185, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwRaumGeothesaurus', 'en,,'),
(226, 186, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwRaumKarte', 'en,,'),
(227, 187, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwZeitEinschraenkung', 'en,,'),
(228, 188, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwZeitChronik', 'en,,'),
(229, 189, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwSuchbereichInhalte', 'en,,'),
(230, 190, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwSuchbereichDatenquellen', 'en,,'),
(231, 191, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheUmwSuchbereichBundLaender', 'en,,'),
(232, 192, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheAdrThemaSuchbegriffe', 'en,,'),
(233, 193, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheAdrThemaSuchmodus', 'en,,'),
(234, 194, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheAdrRaumRaumbezug', 'en,,'),
(235, 195, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheAdrSuchbereichBundLaender', 'en,,'),
(236, 196, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheForschungThemaSuchbegriffe', 'en,,'),
(237, 197, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheForschungThemaAttribute', 'en,,'),
(238, 198, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheRechtThemaSuchbegriffe', 'en,,'),
(239, 199, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheRechtThemaFachwoerterbuch', 'en,,'),
(240, 200, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ErweiterteSucheRechtSuchbereichBundLaender', 'en,,'),
(241, 201, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Sprachauswahl', 'en,,'),
(242, 202, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'Features Information', 'en,,'),
(243, 21, 'org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl', 'PortalU Application', ',,');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `mediatype_to_capability`
--

DROP TABLE IF EXISTS `mediatype_to_capability`;
CREATE TABLE IF NOT EXISTS `mediatype_to_capability` (
  `MEDIATYPE_ID` int(11) NOT NULL,
  `CAPABILITY_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEDIATYPE_ID`,`CAPABILITY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `mediatype_to_capability`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `mediatype_to_mimetype`
--

DROP TABLE IF EXISTS `mediatype_to_mimetype`;
CREATE TABLE IF NOT EXISTS `mediatype_to_mimetype` (
  `MEDIATYPE_ID` int(11) NOT NULL,
  `MIMETYPE_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEDIATYPE_ID`,`MIMETYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `mediatype_to_mimetype`
--

INSERT INTO `mediatype_to_mimetype` (`MEDIATYPE_ID`, `MIMETYPE_ID`) VALUES
(1, 2),
(2, 4),
(3, 3),
(4, 1),
(5, 6);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `media_type`
--

DROP TABLE IF EXISTS `media_type`;
CREATE TABLE IF NOT EXISTS `media_type` (
  `MEDIATYPE_ID` int(11) NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `CHARACTER_SET` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TITLE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DESCRIPTION` mediumtext COLLATE utf8_unicode_ci,
  PRIMARY KEY (`MEDIATYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `media_type`
--

INSERT INTO `media_type` (`MEDIATYPE_ID`, `NAME`, `CHARACTER_SET`, `TITLE`, `DESCRIPTION`) VALUES
(1, 'html', 'UTF-8', 'HTML', 'Rich HTML for HTML 4.0 compliants browsers'),
(2, 'vxml', 'UTF-8', 'VoiceXML', 'Format suitable for use with an audio VoiceXML server'),
(3, 'wml', 'UTF-8', 'WML', 'Format for mobile phones and PDAs compatible with WML 1.1'),
(4, 'xhtml-basic', 'UTF-8', 'XHTML', 'XHTML Basic'),
(5, 'xml', '', 'XML', 'XML 1.0');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `mimetype`
--

DROP TABLE IF EXISTS `mimetype`;
CREATE TABLE IF NOT EXISTS `mimetype` (
  `MIMETYPE_ID` int(11) NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`MIMETYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `mimetype`
--

INSERT INTO `mimetype` (`MIMETYPE_ID`, `NAME`) VALUES
(1, 'application/xhtml+xml'),
(2, 'text/html'),
(3, 'text/vnd.wap.wml'),
(4, 'text/vxml'),
(5, 'text/xhtml'),
(6, 'text/xml');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `named_parameter`
--

DROP TABLE IF EXISTS `named_parameter`;
CREATE TABLE IF NOT EXISTS `named_parameter` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `named_parameter`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ojb_dlist`
--

DROP TABLE IF EXISTS `ojb_dlist`;
CREATE TABLE IF NOT EXISTS `ojb_dlist` (
  `ID` int(11) NOT NULL,
  `SIZE_` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `ojb_dlist`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ojb_dlist_entries`
--

DROP TABLE IF EXISTS `ojb_dlist_entries`;
CREATE TABLE IF NOT EXISTS `ojb_dlist_entries` (
  `ID` int(11) NOT NULL,
  `DLIST_ID` int(11) DEFAULT NULL,
  `POSITION_` int(11) DEFAULT NULL,
  `OID_` mediumblob,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `ojb_dlist_entries`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ojb_dmap`
--

DROP TABLE IF EXISTS `ojb_dmap`;
CREATE TABLE IF NOT EXISTS `ojb_dmap` (
  `ID` int(11) NOT NULL,
  `SIZE_` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `ojb_dmap`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ojb_dset`
--

DROP TABLE IF EXISTS `ojb_dset`;
CREATE TABLE IF NOT EXISTS `ojb_dset` (
  `ID` int(11) NOT NULL,
  `SIZE_` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `ojb_dset`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ojb_dset_entries`
--

DROP TABLE IF EXISTS `ojb_dset_entries`;
CREATE TABLE IF NOT EXISTS `ojb_dset_entries` (
  `ID` int(11) NOT NULL,
  `DLIST_ID` int(11) DEFAULT NULL,
  `POSITION_` int(11) DEFAULT NULL,
  `OID_` mediumblob,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `ojb_dset_entries`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ojb_hl_seq`
--

DROP TABLE IF EXISTS `ojb_hl_seq`;
CREATE TABLE IF NOT EXISTS `ojb_hl_seq` (
  `TABLENAME` varchar(175) COLLATE utf8_unicode_ci NOT NULL,
  `FIELDNAME` varchar(70) COLLATE utf8_unicode_ci NOT NULL,
  `MAX_KEY` int(11) DEFAULT NULL,
  `GRAB_SIZE` int(11) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  PRIMARY KEY (`TABLENAME`,`FIELDNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `ojb_hl_seq`
--

INSERT INTO `ojb_hl_seq` (`TABLENAME`, `FIELDNAME`, `MAX_KEY`, `GRAB_SIZE`, `VERSION`) VALUES
('SEQ_CAPABILITY', 'deprecatedColumn', 60, 20, 3),
('SEQ_CLIENT', 'deprecatedColumn', 20, 20, 1),
('SEQ_FOLDER', 'deprecatedColumn', 780, 20, 39),
('SEQ_FOLDER_CONSTRAINT', 'deprecatedColumn', 20, 20, 1),
('SEQ_FOLDER_CONSTRAINTS_REF', 'deprecatedColumn', 20, 20, 1),
('SEQ_FOLDER_MENU', 'deprecatedColumn', 80, 20, 4),
('SEQ_FOLDER_METADATA', 'deprecatedColumn', 20, 20, 1),
('SEQ_FRAGMENT', 'deprecatedColumn', 7300, 20, 365),
('SEQ_FRAGMENT_CONSTRAINT', 'deprecatedColumn', 20, 20, 1),
('SEQ_FRAGMENT_CONSTRAINTS_REF', 'deprecatedColumn', 20, 20, 1),
('SEQ_FRAGMENT_PREF', 'deprecatedColumn', 7300, 20, 365),
('SEQ_FRAGMENT_PREF_VALUE', 'deprecatedColumn', 7300, 20, 365),
('SEQ_JETSPEED_SERVICE', 'deprecatedColumn', 60, 20, 3),
('SEQ_LANGUAGE', 'deprecatedColumn', 260, 20, 13),
('SEQ_LINK', 'deprecatedColumn', 20, 20, 1),
('SEQ_LOCALIZED_DESCRIPTION', 'deprecatedColumn', 360, 20, 18),
('SEQ_LOCALIZED_DISPLAY_NAME', 'deprecatedColumn', 260, 20, 13),
('SEQ_MEDIA_TYPE', 'deprecatedColumn', 20, 20, 1),
('SEQ_MIMETYPE', 'deprecatedColumn', 20, 20, 1),
('SEQ_PA_METADATA_FIELDS', 'deprecatedColumn', 40, 20, 2),
('SEQ_PAGE', 'deprecatedColumn', 840, 20, 42),
('SEQ_PAGE_CONSTRAINTS_REF', 'deprecatedColumn', 860, 20, 43),
('SEQ_PAGE_METADATA', 'deprecatedColumn', 780, 20, 39),
('SEQ_PAGE_SEC_CONSTRAINT_DEF', 'deprecatedColumn', 20, 20, 1),
('SEQ_PAGE_SEC_CONSTRAINTS_DEF', 'deprecatedColumn', 20, 20, 1),
('SEQ_PAGE_SEC_CONSTRAINTS_REF', 'deprecatedColumn', 20, 20, 1),
('SEQ_PAGE_SECURITY', 'deprecatedColumn', 20, 20, 1),
('SEQ_PARAMETER', 'deprecatedColumn', 300, 20, 15),
('SEQ_PD_METADATA_FIELDS', 'deprecatedColumn', 20, 20, 1),
('SEQ_PORTLET_APPLICATION', 'deprecatedColumn', 40, 20, 2),
('SEQ_PORTLET_DEFINITION', 'deprecatedColumn', 220, 20, 11),
('SEQ_PORTLET_PREFERENCE', 'deprecatedColumn', 120, 20, 6),
('SEQ_PORTLET_PREFERENCE_VALUE', 'deprecatedColumn', 120, 20, 6),
('SEQ_PORTLET_SUPPORTS', 'deprecatedColumn', 220, 20, 11),
('SEQ_RULE_CRITERION', 'deprecatedColumn', 40, 20, 2),
('SEQ_SECURITY_ATTRIBUTE', 'deprecatedColumn', 9960, 20, 498),
('SEQ_SECURITY_CREDENTIAL', 'deprecatedColumn', 780, 20, 39),
('SEQ_SECURITY_DOMAIN', 'deprecatedColumn', 20, 20, 1),
('SEQ_SECURITY_PERMISSION', 'deprecatedColumn', 60, 20, 3),
('SEQ_SECURITY_PRINCIPAL', 'deprecatedColumn', 800, 20, 40);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ojb_lockentry`
--

DROP TABLE IF EXISTS `ojb_lockentry`;
CREATE TABLE IF NOT EXISTS `ojb_lockentry` (
  `OID_` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
  `TX_ID` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `TIMESTAMP_` datetime DEFAULT NULL,
  `ISOLATIONLEVEL` int(11) DEFAULT NULL,
  `LOCKTYPE` int(11) DEFAULT NULL,
  PRIMARY KEY (`OID_`,`TX_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `ojb_lockentry`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ojb_nrm`
--

DROP TABLE IF EXISTS `ojb_nrm`;
CREATE TABLE IF NOT EXISTS `ojb_nrm` (
  `NAME` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
  `OID_` mediumblob,
  PRIMARY KEY (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `ojb_nrm`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `page`
--

DROP TABLE IF EXISTS `page`;
CREATE TABLE IF NOT EXISTS `page` (
  `PAGE_ID` int(11) NOT NULL,
  `CLASS_NAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `PARENT_ID` int(11) NOT NULL,
  `PATH` varchar(240) COLLATE utf8_unicode_ci NOT NULL,
  `CONTENT_TYPE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `IS_INHERITABLE` smallint(6) DEFAULT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `VERSION` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SHORT_TITLE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `IS_HIDDEN` smallint(6) DEFAULT NULL,
  `SKIN` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DEFAULT_LAYOUT_DECORATOR` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DEFAULT_PORTLET_DECORATOR` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SUBSITE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ROLE_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `GROUP_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `MEDIATYPE` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `EXT_ATTR_NAME` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `EXT_ATTR_VALUE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `OWNER_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`PAGE_ID`),
  UNIQUE KEY `UN_PAGE_1` (`PATH`),
  KEY `IX_PAGE_1` (`PARENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `page`
--

INSERT INTO `page` (`PAGE_ID`, `CLASS_NAME`, `PARENT_ID`, `PATH`, `CONTENT_TYPE`, `IS_INHERITABLE`, `NAME`, `VERSION`, `TITLE`, `SHORT_TITLE`, `IS_HIDDEN`, `SKIN`, `DEFAULT_LAYOUT_DECORATOR`, `DEFAULT_PORTLET_DECORATOR`, `SUBSITE`, `USER_PRINCIPAL`, `ROLE_PRINCIPAL`, `GROUP_PRINCIPAL`, `MEDIATYPE`, `LOCALE`, `EXT_ATTR_NAME`, `EXT_ATTR_VALUE`, `OWNER_PRINCIPAL`) VALUES
(1, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/default-page.psml', NULL, NULL, 'default-page.psml', NULL, 'ingrid.page.home.tooltip', 'ingrid.page.home', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/detect-js.psml', NULL, NULL, 'detect-js.psml', NULL, 'Detect JavaScript', 'Detect JavaScript', 1, 'orange', 'ingrid-clear', 'clear', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(3, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/disclaimer.psml', NULL, NULL, 'disclaimer.psml', NULL, 'ingrid.page.disclaimer.tooltip', 'ingrid.page.disclaimer', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(4, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/help.psml', NULL, NULL, 'help.psml', NULL, 'ingrid.page.help.link.tooltip', 'ingrid.page.help', 0, 'orange', 'ingrid-help', 'clear', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(5, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-about-data-source.psml', NULL, NULL, 'main-about-data-source.psml', NULL, 'ingrid.page.data.source.tooltip', 'ingrid.page.data.source', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(6, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-about-partner.psml', NULL, NULL, 'main-about-partner.psml', NULL, 'About PortalU', 'About PortalU', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(7, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-about.psml', NULL, NULL, 'main-about.psml', NULL, 'ingrid.page.about.tooltip', 'ingrid.page.about', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(8, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-chronicle.psml', NULL, NULL, 'main-chronicle.psml', NULL, 'ingrid.page.chronicle.tooltip', 'ingrid.page.chronicle', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(9, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-environment.psml', NULL, NULL, 'main-environment.psml', NULL, 'ingrid.page.envtopics.tooltip', 'ingrid.page.envtopics', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(10, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-features.psml', NULL, NULL, 'main-features.psml', NULL, 'ingrid.page.about.tooltip', 'ingrid.page.about', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(11, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-maps.psml', NULL, NULL, 'main-maps.psml', NULL, 'ingrid.page.maps.tooltip', 'ingrid.page.maps', 0, 'orange', 'ingrid-plain', 'clear', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(12, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-measures.psml', NULL, NULL, 'main-measures.psml', NULL, 'ingrid.page.measures.tooltip', 'ingrid.page.measures', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(13, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-search.psml', NULL, NULL, 'main-search.psml', NULL, 'ingrid.page.search.tooltip', 'ingrid.page.search.free', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(14, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/main-service.psml', NULL, NULL, 'main-service.psml', NULL, 'ingrid.page.service.tooltip', 'ingrid.page.service', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(15, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/myportal-create-account.psml', NULL, NULL, 'myportal-create-account.psml', NULL, 'Login', 'Login', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(16, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/myportal-password-forgotten.psml', NULL, NULL, 'myportal-password-forgotten.psml', NULL, 'Login', 'Login', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(17, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/privacy.psml', NULL, NULL, 'privacy.psml', NULL, 'ingrid.page.privacy.tooltip', 'ingrid.page.privacy', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(18, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/rss-news.psml', NULL, NULL, 'rss-news.psml', NULL, 'Home', 'Home', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(19, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/search-detail.psml', NULL, NULL, 'search-detail.psml', NULL, 'Detail Information', 'Detail Information', 0, 'orange', 'ingrid-clear', 'clear', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(20, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/search-history.psml', NULL, NULL, 'search-history.psml', NULL, 'Search History', 'Search History', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(21, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/search-result-js.psml', NULL, NULL, 'search-result-js.psml', NULL, 'Suchresult', 'Suchresult', 0, 'orange', 'ingrid-clear', 'clear', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(22, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/search-save.psml', NULL, NULL, 'search-save.psml', NULL, 'Save Search', 'Save Search', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(23, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/search-settings.psml', NULL, NULL, 'search-settings.psml', NULL, 'Search Settings', 'Search Settings', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(24, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/service-contact-newsletter.psml', NULL, NULL, 'service-contact-newsletter.psml', NULL, 'ingrid.page.newsletter.tooltip', 'ingrid.page.newsletter', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(25, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/service-contact.psml', NULL, NULL, 'service-contact.psml', NULL, 'ingrid.page.contact.tooltip', 'ingrid.page.contact', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(26, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/service-myportal.psml', NULL, NULL, 'service-myportal.psml', NULL, 'ingrid.page.myportal.tooltip', 'ingrid.page.myportal', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(27, 'org.apache.jetspeed.om.page.impl.PageImpl', 1, '/service-sitemap.psml', NULL, NULL, 'service-sitemap.psml', NULL, 'ingrid.page.sitemap.tooltip', 'ingrid.page.sitemap', 0, 'orange', 'ingrid', 'clear', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(28, 'org.apache.jetspeed.om.page.impl.PageImpl', 3, '/_role/user/default-page.psml', NULL, NULL, 'default-page.psml', NULL, 'ingrid.page.home.tooltip', 'ingrid.page.home', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, NULL),
(29, 'org.apache.jetspeed.om.page.impl.PageImpl', 3, '/_role/user/myportal-edit-account.psml', NULL, NULL, 'myportal-edit-account.psml', NULL, 'Login', 'Login', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, NULL),
(30, 'org.apache.jetspeed.om.page.impl.PageImpl', 3, '/_role/user/myportal-personalize.psml', NULL, NULL, 'myportal-personalize.psml', NULL, 'Login', 'Login', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, NULL),
(31, 'org.apache.jetspeed.om.page.impl.PageImpl', 3, '/_role/user/service-myportal.psml', NULL, NULL, 'service-myportal.psml', NULL, 'ingrid.page.myportal', 'ingrid.page.myportal', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, 'user', NULL, NULL, NULL, NULL, NULL, NULL),
(728, 'org.apache.jetspeed.om.page.impl.PageImpl', 701, '/_user/template/default-page.psml', NULL, NULL, 'default-page.psml', NULL, 'ingrid.page.home.tooltip', 'ingrid.page.home', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, 'template', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(798, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-cms.psml', NULL, NULL, 'admin-cms.psml', NULL, 'ingrid.page.admin.cms', 'ingrid.page.admin.cms', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(799, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-component-monitor.psml', NULL, NULL, 'admin-component-monitor.psml', NULL, 'ingrid.page.admin.monitor', 'ingrid.page.admin.monitor', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(800, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-content-partner.psml', NULL, NULL, 'admin-content-partner.psml', NULL, 'ingrid.page.admin.partner', 'ingrid.page.admin.partner', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(801, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-content-provider.psml', NULL, NULL, 'admin-content-provider.psml', NULL, 'ingrid.page.admin.provider', 'ingrid.page.admin.provider', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(802, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-content-rss.psml', NULL, NULL, 'admin-content-rss.psml', NULL, 'ingrid.page.admin.rss', 'ingrid.page.admin.rss', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(803, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-homepage.psml', NULL, NULL, 'admin-homepage.psml', NULL, 'ingrid.page.admin.homepage', 'ingrid.page.admin.homepage', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(804, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-iplugs.psml', NULL, NULL, 'admin-iplugs.psml', NULL, 'ingrid.page.admin.iplugs', 'ingrid.page.admin.iplugs', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(805, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-portal-profile.psml', NULL, NULL, 'admin-portal-profile.psml', NULL, 'ingrid.page.admin.profile', 'ingrid.page.admin.profile', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(806, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-statistics.psml', NULL, NULL, 'admin-statistics.psml', NULL, 'ingrid.page.admin.statistics', 'ingrid.page.admin.statistics', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(807, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-usermanagement.psml', NULL, NULL, 'admin-usermanagement.psml', NULL, 'ingrid.page.admin.usermanagement', 'ingrid.page.admin.usermanagement', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(808, 'org.apache.jetspeed.om.page.impl.PageImpl', 771, '/administration/admin-wms.psml', NULL, NULL, 'admin-wms.psml', NULL, 'ingrid.page.admin.wms', 'ingrid.page.admin.wms', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(809, 'org.apache.jetspeed.om.page.impl.PageImpl', 772, '/application/main-application-0.psml', NULL, NULL, 'main-application-0.psml', NULL, 'ingrid.page.application.0.tooltip', 'ingrid.page.application.0', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(810, 'org.apache.jetspeed.om.page.impl.PageImpl', 772, '/application/main-application-1.psml', NULL, NULL, 'main-application-1.psml', NULL, 'ingrid.page.application.1.tooltip', 'ingrid.page.application.1', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(811, 'org.apache.jetspeed.om.page.impl.PageImpl', 772, '/application/main-application-2.psml', NULL, NULL, 'main-application-2.psml', NULL, 'ingrid.page.application.2.tooltip', 'ingrid.page.application.2', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(812, 'org.apache.jetspeed.om.page.impl.PageImpl', 772, '/application/main-application.psml', NULL, NULL, 'main-application.psml', NULL, 'ingrid.page.application.tooltip', 'ingrid.page.application', 1, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(813, 'org.apache.jetspeed.om.page.impl.PageImpl', 773, '/cms/cms-0.psml', NULL, NULL, 'cms-0.psml', NULL, 'ingrid.page.cms.0.tooltip', 'ingrid.page.cms.0', 1, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(814, 'org.apache.jetspeed.om.page.impl.PageImpl', 773, '/cms/cms-1.psml', NULL, NULL, 'cms-1.psml', NULL, 'ingrid.page.cms.1.tooltip', 'ingrid.page.cms.1', 1, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(815, 'org.apache.jetspeed.om.page.impl.PageImpl', 773, '/cms/cms-2.psml', NULL, NULL, 'cms-2.psml', NULL, 'ingrid.page.cms.2.tooltip', 'ingrid.page.cms.2', 1, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(816, 'org.apache.jetspeed.om.page.impl.PageImpl', 774, '/mdek/mdek_portal_admin.psml', NULL, NULL, 'mdek_portal_admin.psml', NULL, 'ingrid.page.mdek.catadmin', 'ingrid.page.mdek.catadmin', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(817, 'org.apache.jetspeed.om.page.impl.PageImpl', 775, '/search-catalog/search-catalog-hierarchy.psml', NULL, NULL, 'search-catalog-hierarchy.psml', NULL, 'ingrid.page.search.catalog.tooltip', 'ingrid.page.search.catalog', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(818, 'org.apache.jetspeed.om.page.impl.PageImpl', 775, '/search-catalog/search-catalog-thesaurus.psml', NULL, NULL, 'search-catalog-thesaurus.psml', NULL, 'ingrid.page.search.catalog', 'ingrid.page.search.catalog', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(819, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-adr-area-partner.psml', NULL, NULL, 'search-ext-adr-area-partner.psml', NULL, 'Search Extended Address/Area/Partner', 'Search Extended Address/Area/Partner', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(820, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-adr-place-reference.psml', NULL, NULL, 'search-ext-adr-place-reference.psml', NULL, 'Search Extended Address/Place/Reference', 'Search Extended Address/Place/Reference', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(821, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-adr-topic-mode.psml', NULL, NULL, 'search-ext-adr-topic-mode.psml', NULL, 'Search Extended Address/Topic/Mode', 'Search Extended Address/Topic/Mode', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(822, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-adr-topic-terms.psml', NULL, NULL, 'search-ext-adr-topic-terms.psml', NULL, 'Search Extended Address/Topic/Terms', 'Search Extended Address/Topic/Terms', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(823, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-env-area-contents.psml', NULL, NULL, 'search-ext-env-area-contents.psml', NULL, 'SearchExtended Environment/Area/Contents', 'SearchExtended Environment/Area/Contents', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(824, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-env-area-partner.psml', NULL, NULL, 'search-ext-env-area-partner.psml', NULL, 'SearchExtended Environment/Area/Partner', 'SearchExtended Environment/Area/Partner', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(825, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-env-area-sources.psml', NULL, NULL, 'search-ext-env-area-sources.psml', NULL, 'SearchExtended Environment/Area/Contents', 'SearchExtended Environment/Area/Contents', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(826, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-env-place-geothesaurus.psml', NULL, NULL, 'search-ext-env-place-geothesaurus.psml', NULL, 'Search Extended Environment', 'Search Extended Environment', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(827, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-env-place-map.psml', NULL, NULL, 'search-ext-env-place-map.psml', NULL, 'SearchExtended Environment/Place/Map', 'SearchExtended Environment/Place/Map', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(828, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-env-time-constraint.psml', NULL, NULL, 'search-ext-env-time-constraint.psml', NULL, 'SearchExtended Environment/Time/Constraint', 'Environment/Time/Constraint', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(829, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-env-topic-terms.psml', NULL, NULL, 'search-ext-env-topic-terms.psml', NULL, 'Search Extended Environment', 'Search Extended Environment', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(830, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-env-topic-thesaurus.psml', NULL, NULL, 'search-ext-env-topic-thesaurus.psml', NULL, 'Search Extended Environment', 'Search Extended Environment', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(831, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-law-area-partner.psml', NULL, NULL, 'search-ext-law-area-partner.psml', NULL, 'Search Extended Law', 'Search Extended Law', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(832, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-law-topic-terms.psml', NULL, NULL, 'search-ext-law-topic-terms.psml', NULL, 'Search Extended Law', 'Search Extended Law', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(833, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-law-topic-thesaurus.psml', NULL, NULL, 'search-ext-law-topic-thesaurus.psml', NULL, 'Search Extended Law', 'Search Extended Law', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(834, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-res-topic-attributes.psml', NULL, NULL, 'search-ext-res-topic-attributes.psml', NULL, 'Search Extended Research/Topic/Attributes', 'Research/Topic/Attributes', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(835, 'org.apache.jetspeed.om.page.impl.PageImpl', 776, '/search-extended/search-ext-res-topic-terms.psml', NULL, NULL, 'search-ext-res-topic-terms.psml', NULL, 'Search Extended Research/Topic/Terms', 'Search Extended Research/Topic/Terms', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `page_constraint`
--

DROP TABLE IF EXISTS `page_constraint`;
CREATE TABLE IF NOT EXISTS `page_constraint` (
  `CONSTRAINT_ID` int(11) NOT NULL,
  `PAGE_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `USER_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ROLE_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `GROUP_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PERMISSIONS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`CONSTRAINT_ID`),
  KEY `IX_PAGE_CONSTRAINT_1` (`PAGE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `page_constraint`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `page_constraints_ref`
--

DROP TABLE IF EXISTS `page_constraints_ref`;
CREATE TABLE IF NOT EXISTS `page_constraints_ref` (
  `CONSTRAINTS_REF_ID` int(11) NOT NULL,
  `PAGE_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `UN_PAGE_CONSTRAINTS_REF_1` (`PAGE_ID`,`NAME`),
  KEY `IX_PAGE_CONSTRAINTS_REF_1` (`PAGE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `page_constraints_ref`
--

INSERT INTO `page_constraints_ref` (`CONSTRAINTS_REF_ID`, `PAGE_ID`, `APPLY_ORDER`, `NAME`) VALUES
(1, 1, 0, 'public-view'),
(2, 1, 1, 'admin-portal'),
(3, 2, 0, 'public-view'),
(4, 3, 0, 'public-view'),
(5, 4, 0, 'public-view'),
(6, 5, 0, 'public-view'),
(7, 6, 0, 'public-view'),
(8, 7, 0, 'public-view'),
(9, 8, 0, 'public-view'),
(10, 9, 0, 'public-view'),
(11, 11, 0, 'public-view'),
(12, 12, 0, 'public-view'),
(13, 13, 0, 'public-view'),
(14, 14, 0, 'public-view'),
(15, 15, 0, 'public-view'),
(16, 16, 0, 'public-view'),
(17, 17, 0, 'public-view'),
(18, 18, 0, 'public-view'),
(19, 19, 0, 'public-view'),
(20, 20, 0, 'public-view'),
(21, 21, 0, 'public-view'),
(22, 22, 0, 'public-view'),
(23, 23, 0, 'public-view'),
(24, 24, 0, 'public-view'),
(25, 25, 0, 'public-view'),
(26, 26, 0, 'public-view'),
(27, 27, 0, 'public-view'),
(28, 28, 0, 'public-view'),
(29, 29, 0, 'public-view'),
(30, 30, 0, 'public-view'),
(31, 31, 0, 'public-view'),
(728, 728, 0, 'public-edit'),
(798, 798, 0, 'admin'),
(799, 798, 1, 'admin-portal'),
(800, 799, 0, 'admin'),
(801, 799, 1, 'admin-portal'),
(802, 800, 0, 'admin'),
(803, 800, 1, 'admin-portal'),
(804, 801, 0, 'admin'),
(805, 801, 1, 'admin-portal'),
(806, 802, 0, 'admin'),
(807, 802, 1, 'admin-portal'),
(808, 803, 0, 'admin'),
(809, 803, 1, 'admin-portal'),
(810, 804, 0, 'admin'),
(811, 804, 1, 'admin-portal'),
(812, 804, 2, 'admin-partner'),
(813, 804, 3, 'admin-provider'),
(814, 805, 0, 'admin'),
(815, 805, 1, 'admin-portal'),
(816, 806, 0, 'admin'),
(817, 806, 1, 'admin-portal'),
(818, 806, 2, 'admin-partner'),
(819, 806, 3, 'admin-provider'),
(820, 807, 0, 'admin'),
(821, 807, 1, 'admin-partner'),
(822, 807, 2, 'admin-portal'),
(823, 808, 0, 'admin'),
(824, 808, 1, 'admin-portal'),
(825, 809, 0, 'public-view'),
(826, 809, 1, 'admin'),
(827, 809, 2, 'admin-portal'),
(828, 810, 0, 'public-view'),
(829, 810, 1, 'admin'),
(830, 810, 2, 'admin-portal'),
(831, 811, 0, 'public-view'),
(832, 811, 1, 'admin'),
(833, 811, 2, 'admin-portal'),
(834, 812, 0, 'public-view'),
(835, 812, 1, 'admin-portal'),
(836, 813, 0, 'public-view'),
(837, 814, 0, 'public-view'),
(838, 815, 0, 'public-view'),
(839, 817, 0, 'public-view'),
(840, 818, 0, 'public-view'),
(841, 819, 0, 'public-view'),
(842, 820, 0, 'public-view'),
(843, 821, 0, 'public-view'),
(844, 822, 0, 'public-view'),
(845, 823, 0, 'public-view'),
(846, 824, 0, 'public-view'),
(847, 825, 0, 'public-view'),
(848, 826, 0, 'public-view'),
(849, 827, 0, 'public-view'),
(850, 828, 0, 'public-view'),
(851, 829, 0, 'public-view'),
(852, 830, 0, 'public-view'),
(853, 831, 0, 'public-view'),
(854, 832, 0, 'public-view'),
(855, 833, 0, 'public-view'),
(856, 834, 0, 'public-view'),
(857, 835, 0, 'public-view');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `page_menu`
--

DROP TABLE IF EXISTS `page_menu`;
CREATE TABLE IF NOT EXISTS `page_menu` (
  `MENU_ID` int(11) NOT NULL,
  `CLASS_NAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `PARENT_ID` int(11) DEFAULT NULL,
  `PAGE_ID` int(11) DEFAULT NULL,
  `ELEMENT_ORDER` int(11) DEFAULT NULL,
  `NAME` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SHORT_TITLE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TEXT` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `OPTIONS` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DEPTH` int(11) DEFAULT NULL,
  `IS_PATHS` smallint(6) DEFAULT NULL,
  `IS_REGEXP` smallint(6) DEFAULT NULL,
  `PROFILE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `OPTIONS_ORDER` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SKIN` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `IS_NEST` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`MENU_ID`),
  KEY `IX_PAGE_MENU_1` (`PARENT_ID`),
  KEY `IX_PAGE_MENU_2` (`PAGE_ID`),
  KEY `UN_PAGE_MENU_1` (`PAGE_ID`,`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `page_menu`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `page_menu_metadata`
--

DROP TABLE IF EXISTS `page_menu_metadata`;
CREATE TABLE IF NOT EXISTS `page_menu_metadata` (
  `METADATA_ID` int(11) NOT NULL,
  `MENU_ID` int(11) NOT NULL,
  `NAME` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`METADATA_ID`),
  UNIQUE KEY `UN_PAGE_MENU_METADATA_1` (`MENU_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_PAGE_MENU_METADATA_1` (`MENU_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `page_menu_metadata`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `page_metadata`
--

DROP TABLE IF EXISTS `page_metadata`;
CREATE TABLE IF NOT EXISTS `page_metadata` (
  `METADATA_ID` int(11) NOT NULL,
  `PAGE_ID` int(11) NOT NULL,
  `NAME` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`METADATA_ID`),
  UNIQUE KEY `UN_PAGE_METADATA_1` (`PAGE_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_PAGE_METADATA_1` (`PAGE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `page_metadata`
--

INSERT INTO `page_metadata` (`METADATA_ID`, `PAGE_ID`, `NAME`, `LOCALE`, `VALUE`) VALUES
(1, 1, 'meta_descr', 'de,,', 'ingrid.page.home.meta.description'),
(2, 1, 'meta_keywords', 'de,,', 'ingrid.page.home.meta.keywords'),
(3, 1, 'meta_title', 'de,,', 'ingrid.page.home.meta.title'),
(4, 3, 'meta_descr', 'de,,', 'ingrid.page.disclaimer.meta.description'),
(5, 3, 'meta_keywords', 'de,,', 'ingrid.page.disclaimer.meta.keywords'),
(6, 3, 'meta_title', 'de,,', 'ingrid.page.disclaimer.meta.title'),
(7, 4, 'meta_descr', 'de,,', 'ingrid.page.help.meta.description'),
(8, 4, 'meta_keywords', 'de,,', 'ingrid.page.help.meta.keywords'),
(9, 4, 'meta_title', 'de,,', 'ingrid.page.help.meta.title'),
(10, 5, 'meta_descr', 'de,,', 'ingrid.page.data.source.meta.description'),
(11, 5, 'meta_keywords', 'de,,', 'ingrid.page.data.source.meta.keywords'),
(12, 5, 'meta_title', 'de,,', 'ingrid.page.data.source.meta.title'),
(13, 6, 'meta_descr', 'de,,', 'ingrid.page.about.partner.meta.description'),
(14, 6, 'meta_keywords', 'de,,', 'ingrid.page.about.partner.meta.keywords'),
(15, 6, 'meta_title', 'de,,', 'ingrid.page.about.partner.meta.title'),
(16, 7, 'meta_descr', 'de,,', 'ingrid.page.about.meta.description'),
(17, 7, 'meta_keywords', 'de,,', 'ingrid.page.about.meta.keywords'),
(18, 7, 'meta_title', 'de,,', 'ingrid.page.about.meta.title'),
(19, 8, 'meta_descr', 'de,,', 'ingrid.page.chronicle.meta.description'),
(20, 8, 'meta_keywords', 'de,,', 'ingrid.page.chronicle.meta.keywords'),
(21, 8, 'meta_title', 'de,,', 'ingrid.page.chronicle.meta.title'),
(22, 9, 'meta_descr', 'de,,', 'ingrid.page.environment.meta.description'),
(23, 9, 'meta_keywords', 'de,,', 'ingrid.page.environment.meta.keywords'),
(24, 9, 'meta_title', 'de,,', 'ingrid.page.environment.meta.title'),
(25, 10, 'meta_descr', 'de,,', 'ingrid.page.features.meta.description'),
(26, 10, 'meta_keywords', 'de,,', 'ingrid.page.features.meta.keywords'),
(27, 10, 'meta_title', 'de,,', 'ingrid.page.features.meta.title'),
(28, 11, 'meta_descr', 'de,,', 'ingrid.page.maps.meta.description'),
(29, 11, 'meta_keywords', 'de,,', 'ingrid.page.maps.meta.keywords'),
(30, 11, 'meta_title', 'de,,', 'ingrid.page.maps.meta.title'),
(31, 12, 'meta_descr', 'de,,', 'ingrid.page.measures.meta.description'),
(32, 12, 'meta_keywords', 'de,,', 'ingrid.page.measures.meta.keywords'),
(33, 12, 'meta_title', 'de,,', 'ingrid.page.measures.meta.title'),
(34, 13, 'meta_descr', 'de,,', 'ingrid.page.search.meta.description'),
(35, 13, 'meta_keywords', 'de,,', 'ingrid.page.search.meta.keywords'),
(36, 13, 'meta_title', 'de,,', 'ingrid.page.search.meta.title'),
(37, 14, 'meta_descr', 'de,,', 'ingrid.page.service.meta.description'),
(38, 14, 'meta_keywords', 'de,,', 'ingrid.page.service.meta.keywords'),
(39, 14, 'meta_title', 'de,,', 'ingrid.page.service.meta.title'),
(40, 17, 'meta_descr', 'de,,', 'ingrid.page.privacy.meta.description'),
(41, 17, 'meta_keywords', 'de,,', 'ingrid.page.privacy.meta.keywords'),
(42, 17, 'meta_title', 'de,,', 'ingrid.page.privacy.meta.title'),
(43, 18, 'meta_descr', 'de,,', 'ingrid.page.rss.meta.description'),
(44, 18, 'meta_keywords', 'de,,', 'ingrid.page.rss.meta.keywords'),
(45, 18, 'meta_title', 'de,,', 'ingrid.page.rss.meta.title'),
(46, 19, 'meta_descr', 'de,,', 'ingrid.page.detail.meta.description'),
(47, 19, 'meta_keywords', 'de,,', 'ingrid.page.detail.meta.keywords'),
(48, 19, 'meta_title', 'de,,', 'ingrid.page.detail.meta.title'),
(49, 20, 'meta_descr', 'de,,', 'ingrid.page.search.history.meta.description'),
(50, 20, 'meta_keywords', 'de,,', 'ingrid.page.search.history.meta.keywords'),
(51, 20, 'meta_title', 'de,,', 'ingrid.page.search.history.meta.title'),
(52, 23, 'meta_descr', 'de,,', 'ingrid.page.search.settings.meta.description'),
(53, 23, 'meta_keywords', 'de,,', 'ingrid.page.search.settings.meta.keywords'),
(54, 23, 'meta_title', 'de,,', 'ingrid.page.search.settings.meta.title'),
(55, 24, 'meta_descr', 'de,,', 'ingrid.page.newsletter.meta.description'),
(56, 24, 'meta_keywords', 'de,,', 'ingrid.page.newsletter.meta.keywords'),
(57, 24, 'meta_title', 'de,,', 'ingrid.page.newsletter.meta.title'),
(58, 25, 'meta_descr', 'de,,', 'ingrid.page.contact.meta.description'),
(59, 25, 'meta_keywords', 'de,,', 'ingrid.page.contact.meta.keywords'),
(60, 25, 'meta_title', 'de,,', 'ingrid.page.contact.meta.title'),
(61, 26, 'meta_descr', 'de,,', 'ingrid.page.myportal.meta.description'),
(62, 26, 'meta_keywords', 'de,,', 'ingrid.page.myportal.meta.keywords'),
(63, 26, 'meta_title', 'de,,', 'ingrid.page.myportal.meta.title'),
(64, 27, 'meta_descr', 'de,,', 'ingrid.page.sitemap.meta.description'),
(65, 27, 'meta_keywords', 'de,,', 'ingrid.page.sitemap.meta.keywords'),
(66, 27, 'meta_title', 'de,,', 'ingrid.page.sitemap.meta.title'),
(67, 28, 'meta_descr', 'de,,', 'ingrid.page.home.meta.description'),
(68, 28, 'meta_keywords', 'de,,', 'ingrid.page.home.meta.keywords'),
(69, 28, 'meta_title', 'de,,', 'ingrid.page.home.meta.title'),
(697, 728, 'meta_descr', 'de,,', 'ingrid.page.home.meta.description'),
(698, 728, 'meta_keywords', 'de,,', 'ingrid.page.home.meta.keywords'),
(699, 728, 'meta_title', 'de,,', 'ingrid.page.home.meta.title'),
(749, 809, 'meta_descr', 'de,,', 'ingrid.page.application.0.meta.description'),
(750, 809, 'meta_keywords', 'de,,', 'ingrid.page.application.0.meta.keywords'),
(748, 809, 'meta_title', 'de,,', 'ingrid.page.application.0.meta.title'),
(752, 810, 'meta_descr', 'de,,', 'ingrid.page.application.1.meta.description'),
(753, 810, 'meta_keywords', 'de,,', 'ingrid.page.application.1.meta.keywords'),
(751, 810, 'meta_title', 'de,,', 'ingrid.page.application.1.meta.title'),
(755, 811, 'meta_descr', 'de,,', 'ingrid.page.application.2.meta.description'),
(756, 811, 'meta_keywords', 'de,,', 'ingrid.page.application.2.meta.keywords'),
(754, 811, 'meta_title', 'de,,', 'ingrid.page.application.2.meta.title'),
(758, 812, 'meta_descr', 'de,,', 'ingrid.page.application.meta.description'),
(759, 812, 'meta_keywords', 'de,,', 'ingrid.page.application.meta.keywords'),
(757, 812, 'meta_title', 'de,,', 'ingrid.page.application.meta.title'),
(761, 813, 'meta_descr', 'de,,', 'ingrid.page.cms.0.meta.description'),
(762, 813, 'meta_keywords', 'de,,', 'ingrid.page.cms.0.meta.keywords'),
(760, 813, 'meta_title', 'de,,', 'ingrid.page.cms.0.meta.title'),
(764, 814, 'meta_descr', 'de,,', 'ingrid.page.cms.1.meta.description'),
(765, 814, 'meta_keywords', 'de,,', 'ingrid.page.cms.1.meta.keywords'),
(763, 814, 'meta_title', 'de,,', 'ingrid.page.cms.1.meta.title'),
(767, 815, 'meta_descr', 'de,,', 'ingrid.page.cms.2.meta.description'),
(768, 815, 'meta_keywords', 'de,,', 'ingrid.page.cms.2.meta.keywords'),
(766, 815, 'meta_title', 'de,,', 'ingrid.page.cms.2.meta.title'),
(769, 817, 'meta_descr', 'de,,', 'ingrid.page.hierarchy.meta.description'),
(770, 817, 'meta_keywords', 'de,,', 'ingrid.page.hierarchy.meta.keywords'),
(771, 817, 'meta_title', 'de,,', 'ingrid.page.hierarchy.meta.title'),
(772, 818, 'meta_descr', 'de,,', 'ingrid.page.thesaurus.meta.description'),
(773, 818, 'meta_keywords', 'de,,', 'ingrid.page.thesaurus.meta.keywords'),
(774, 818, 'meta_title', 'de,,', 'ingrid.page.thesaurus.meta.title'),
(775, 829, 'meta_descr', 'de,,', 'ingrid.page.ext.search.meta.description'),
(776, 829, 'meta_keywords', 'de,,', 'ingrid.page.ext.search.meta.keywords'),
(777, 829, 'meta_title', 'de,,', 'ingrid.page.ext.search.meta.title');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `page_security`
--

DROP TABLE IF EXISTS `page_security`;
CREATE TABLE IF NOT EXISTS `page_security` (
  `PAGE_SECURITY_ID` int(11) NOT NULL,
  `PARENT_ID` int(11) NOT NULL,
  `PATH` varchar(240) COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `VERSION` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SUBSITE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ROLE_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `GROUP_PRINCIPAL` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `MEDIATYPE` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `EXT_ATTR_NAME` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `EXT_ATTR_VALUE` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`PAGE_SECURITY_ID`),
  UNIQUE KEY `UN_PAGE_SECURITY_1` (`PARENT_ID`),
  UNIQUE KEY `UN_PAGE_SECURITY_2` (`PATH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `page_security`
--

INSERT INTO `page_security` (`PAGE_SECURITY_ID`, `PARENT_ID`, `PATH`, `NAME`, `VERSION`, `SUBSITE`, `USER_PRINCIPAL`, `ROLE_PRINCIPAL`, `GROUP_PRINCIPAL`, `MEDIATYPE`, `LOCALE`, `EXT_ATTR_NAME`, `EXT_ATTR_VALUE`) VALUES
(1, 1, '/page.security', 'page.security', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `page_sec_constraints_def`
--

DROP TABLE IF EXISTS `page_sec_constraints_def`;
CREATE TABLE IF NOT EXISTS `page_sec_constraints_def` (
  `CONSTRAINTS_DEF_ID` int(11) NOT NULL,
  `PAGE_SECURITY_ID` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_DEF_ID`),
  UNIQUE KEY `UN_PAGE_SEC_CONSTRAINTS_DEF_1` (`PAGE_SECURITY_ID`,`NAME`),
  KEY `IX_PAGE_SEC_CONSTRAINTS_DEF_1` (`PAGE_SECURITY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `page_sec_constraints_def`
--

INSERT INTO `page_sec_constraints_def` (`CONSTRAINTS_DEF_ID`, `PAGE_SECURITY_ID`, `NAME`) VALUES
(1, 1, 'admin'),
(2, 1, 'admin-partner'),
(3, 1, 'admin-portal'),
(4, 1, 'admin-provider'),
(5, 1, 'manager'),
(6, 1, 'public-edit'),
(7, 1, 'public-view'),
(8, 1, 'users');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `page_sec_constraints_ref`
--

DROP TABLE IF EXISTS `page_sec_constraints_ref`;
CREATE TABLE IF NOT EXISTS `page_sec_constraints_ref` (
  `CONSTRAINTS_REF_ID` int(11) NOT NULL,
  `PAGE_SECURITY_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `UN_PAGE_SEC_CONSTRAINTS_REF_1` (`PAGE_SECURITY_ID`,`NAME`),
  KEY `IX_PAGE_SEC_CONSTRAINTS_REF_1` (`PAGE_SECURITY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `page_sec_constraints_ref`
--

INSERT INTO `page_sec_constraints_ref` (`CONSTRAINTS_REF_ID`, `PAGE_SECURITY_ID`, `APPLY_ORDER`, `NAME`) VALUES
(1, 1, 0, 'admin');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `page_sec_constraint_def`
--

DROP TABLE IF EXISTS `page_sec_constraint_def`;
CREATE TABLE IF NOT EXISTS `page_sec_constraint_def` (
  `CONSTRAINT_DEF_ID` int(11) NOT NULL,
  `CONSTRAINTS_DEF_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `USER_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ROLE_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `GROUP_PRINCIPALS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PERMISSIONS_ACL` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`CONSTRAINT_DEF_ID`),
  KEY `IX_PAGE_SEC_CONSTRAINT_DEF_1` (`CONSTRAINTS_DEF_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `page_sec_constraint_def`
--

INSERT INTO `page_sec_constraint_def` (`CONSTRAINT_DEF_ID`, `CONSTRAINTS_DEF_ID`, `APPLY_ORDER`, `USER_PRINCIPALS_ACL`, `ROLE_PRINCIPALS_ACL`, `GROUP_PRINCIPALS_ACL`, `PERMISSIONS_ACL`) VALUES
(1, 1, 0, NULL, 'admin', NULL, 'view,edit'),
(2, 2, 0, NULL, 'admin-partner', NULL, 'view,edit'),
(3, 3, 0, NULL, 'admin-portal', NULL, 'view,edit'),
(4, 4, 0, NULL, 'admin-provider', NULL, 'view,edit'),
(5, 5, 0, NULL, 'manager', NULL, 'view'),
(6, 6, 0, '*', NULL, NULL, 'view,edit'),
(7, 7, 0, '*', NULL, NULL, 'view'),
(8, 8, 0, NULL, 'user,manager', NULL, 'view');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `page_statistics`
--

DROP TABLE IF EXISTS `page_statistics`;
CREATE TABLE IF NOT EXISTS `page_statistics` (
  `IPADDRESS` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_NAME` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TIME_STAMP` datetime DEFAULT NULL,
  `PAGE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `ELAPSED_TIME` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `page_statistics`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `parameter`
--

DROP TABLE IF EXISTS `parameter`;
CREATE TABLE IF NOT EXISTS `parameter` (
  `PARAMETER_ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `OWNER_CLASS_NAME` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `PARAMETER_VALUE` mediumtext COLLATE utf8_unicode_ci,
  PRIMARY KEY (`PARAMETER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `parameter`
--

INSERT INTO `parameter` (`PARAMETER_ID`, `OWNER_ID`, `OWNER_CLASS_NAME`, `NAME`, `PARAMETER_VALUE`) VALUES
(1, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'ingrid-clear-layout'),
(2, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(3, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '1'),
(4, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '100%'),
(5, 1, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'OneColumn'),
(6, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'ingrid-one-column'),
(7, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(8, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '1'),
(9, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '100%'),
(10, 2, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'OneColumn'),
(11, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'ingrid-two-columns'),
(12, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(13, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2'),
(14, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '50%,50%'),
(15, 3, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns'),
(16, 4, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(17, 4, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(18, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(19, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(20, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '1'),
(21, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '100%'),
(22, 5, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'OneColumn'),
(23, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'tcolumns'),
(24, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(25, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '1'),
(26, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '100%'),
(27, 6, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'OneColumn'),
(28, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(29, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(30, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2'),
(31, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '50%,50%'),
(32, 7, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns'),
(33, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(34, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(35, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2'),
(36, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '15%,85%'),
(37, 8, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns'),
(38, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(39, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(40, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '3'),
(41, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '33%,33%,33%'),
(42, 9, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'ThreeColumns'),
(43, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'tcolumns'),
(44, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(45, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '3'),
(46, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '33%,33%,33%'),
(47, 10, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'ThreeColumns'),
(48, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(49, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(50, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '1'),
(51, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '100%'),
(52, 11, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'OneColumn'),
(53, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(54, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(55, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2'),
(56, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '50%,50%'),
(57, 12, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns'),
(58, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(59, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(60, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '3'),
(61, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '33%,33%,33%'),
(62, 13, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'ThreeColumns'),
(63, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(64, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(65, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2'),
(66, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '25%,75%'),
(67, 14, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns'),
(68, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(69, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(70, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2'),
(71, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '25%,75%'),
(72, 15, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns'),
(73, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(74, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(75, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2'),
(76, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '15%,85%'),
(77, 16, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns'),
(78, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'tcolumns'),
(79, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(80, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '2'),
(81, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '50%,50%'),
(82, 17, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'TwoColumns'),
(83, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', 'columns'),
(84, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'MaxPage', 'maximized'),
(85, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'columns', '4'),
(86, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'sizes', '20%,30%,30%,20%'),
(87, 18, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'layoutType', 'FourColumns'),
(179, 101, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/mdek/mdek_entry.vm'),
(180, 102, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/mdek/mdek_portal_admin.vm'),
(181, 103, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/mdek/mdek_admin_login.vm'),
(201, 121, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/default_info.vm'),
(202, 123, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/help.vm'),
(203, 124, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/component_monitor.vm'),
(204, 125, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/content_rss.vm'),
(205, 126, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/content_partner.vm'),
(206, 127, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/content_provider.vm'),
(207, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_user_browser.vm'),
(208, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'emailTemplate', '/WEB-INF/templates/administration/userCreatedConfirmationEmail.vm'),
(209, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'roles', 'user'),
(210, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'groups', ''),
(211, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'rulesNames', 'user-role-fallback'),
(212, 128, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'rulesValues', 'page'),
(213, 129, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_iplug.vm'),
(214, 130, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_cms_browser.vm'),
(215, 131, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_wms.vm'),
(216, 132, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_statistics.vm'),
(217, 133, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_homepage.vm'),
(218, 134, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_portal_profile.vm'),
(219, 135, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/about_portalu_partner.vm'),
(220, 136, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/newsletter_teaser.vm'),
(221, 137, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/login_teaser.vm'),
(222, 138, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/administration/admin_user_migration.vm'),
(223, 139, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_simple.vm'),
(224, 140, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/environment_teaser.vm'),
(225, 141, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/rss_news_teaser.vm'),
(226, 142, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/rss_news.vm'),
(227, 143, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/default_cms.vm'),
(228, 144, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/default_cms.vm'),
(229, 145, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/default_cms.vm'),
(230, 146, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/service_teaser_new.vm'),
(231, 147, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/measures_teaser_new.vm'),
(232, 148, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/chronicle_teaser_new.vm'),
(233, 149, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/weather_teaser.vm'),
(234, 150, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/contact.vm'),
(235, 151, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/contact_newsletter.vm'),
(236, 152, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/login.vm'),
(237, 153, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_overview.vm'),
(238, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_create_account.vm'),
(239, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'returnURL', '/myportal-create-account.psml'),
(240, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'emailTemplate', '/WEB-INF/templates/myportal/userRegistrationEmail.vm'),
(241, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'rulesNames', 'user-role-fallback'),
(242, 154, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'rulesValues', 'page'),
(243, 155, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_edit_account.vm'),
(244, 156, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_password_forgotten.vm'),
(245, 156, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'emailTemplate', '/WEB-INF/templates/myportal/forgottenPasswdEmail.vm'),
(246, 157, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_personalize_overview.vm'),
(247, 158, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_personalize_home.vm'),
(248, 159, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_personalize_partner.vm'),
(249, 160, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_personalize_sources.vm'),
(250, 161, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/myportal/myportal_personalize_search_settings.vm'),
(251, 162, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/show_partner.vm'),
(252, 163, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/show_data_source.vm'),
(253, 164, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/service_search.vm'),
(254, 165, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/empty.vm'),
(255, 166, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/measures_search.vm'),
(256, 167, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/empty.vm'),
(257, 168, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/environment_search.vm'),
(258, 169, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/empty.vm'),
(259, 170, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/save_maps.vm'),
(260, 171, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/show_maps.vm'),
(261, 172, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/chronicle_search.vm'),
(262, 173, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/empty.vm'),
(263, 174, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_settings.vm'),
(264, 175, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_save.vm'),
(265, 176, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_history.vm'),
(266, 177, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_similar.vm'),
(267, 178, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_result.vm'),
(268, 179, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_detail.vm'),
(269, 180, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_catalog/search_cat_hierarchy.vm'),
(270, 181, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_catalog/search_cat_thesaurus.vm'),
(271, 182, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_catalog/search_cat_thesaurus_result.vm'),
(272, 183, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_topic_terms.vm'),
(273, 184, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_topic_thesaurus.vm'),
(274, 185, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_place_geothesaurus.vm'),
(275, 186, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_place_map.vm'),
(276, 187, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_time_constraint.vm'),
(277, 188, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_time_chronicle.vm'),
(278, 189, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_area_contents.vm'),
(279, 190, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_area_sources.vm'),
(280, 191, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_env_area_partner.vm'),
(281, 192, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_adr_topic_terms.vm'),
(282, 193, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_adr_topic_mode.vm'),
(283, 194, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_adr_place_reference.vm'),
(284, 195, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_adr_area_partner.vm'),
(285, 196, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_res_topic_terms.vm'),
(286, 197, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_res_topic_attributes.vm'),
(287, 198, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_law_topic_terms.vm'),
(288, 199, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_law_topic_thesaurus.vm'),
(289, 200, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/search_extended/search_ext_law_area_partner.vm'),
(290, 201, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/language.vm'),
(291, 202, 'org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl', 'ViewPage', '/WEB-INF/templates/features.vm');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `parameter_alias`
--

DROP TABLE IF EXISTS `parameter_alias`;
CREATE TABLE IF NOT EXISTS `parameter_alias` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `parameter_alias`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `pa_metadata_fields`
--

DROP TABLE IF EXISTS `pa_metadata_fields`;
CREATE TABLE IF NOT EXISTS `pa_metadata_fields` (
  `ID` int(11) NOT NULL,
  `OBJECT_ID` int(11) NOT NULL,
  `COLUMN_VALUE` mediumtext COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `pa_metadata_fields`
--

INSERT INTO `pa_metadata_fields` (`ID`, `OBJECT_ID`, `COLUMN_VALUE`, `NAME`, `LOCALE_STRING`) VALUES
(1, 1, 'Jetspeed Layout Portlets', 'title', 'en,,'),
(2, 1, 'Layout Portlets', 'title', 'en,,'),
(3, 1, 'J2 Team', 'creator', 'en,,'),
(4, 1, 'true', 'layout-app', 'en,,'),
(5, 1, '2.2', 'pa-version', 'en,,'),
(8, 3, 'ingrid portal mdek', 'title', 'en,,'),
(9, 3, 'ingrid portal mdek', 'title', 'en,,'),
(21, 21, 'ingrid portal applications', 'title', 'en,,'),
(22, 21, 'ingrid portal applications', 'title', 'en,,');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `pa_security_constraint`
--

DROP TABLE IF EXISTS `pa_security_constraint`;
CREATE TABLE IF NOT EXISTS `pa_security_constraint` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `TRANSPORT` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `pa_security_constraint`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `pd_metadata_fields`
--

DROP TABLE IF EXISTS `pd_metadata_fields`;
CREATE TABLE IF NOT EXISTS `pd_metadata_fields` (
  `ID` int(11) NOT NULL,
  `OBJECT_ID` int(11) NOT NULL,
  `COLUMN_VALUE` mediumtext COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `pd_metadata_fields`
--

INSERT INTO `pd_metadata_fields` (`ID`, `OBJECT_ID`, `COLUMN_VALUE`, `NAME`, `LOCALE_STRING`) VALUES
(1, 4, '*', 'selector.conditional.role', 'en,,'),
(2, 5, '*', 'selector.conditional.role', 'en,,'),
(3, 6, '*', 'selector.conditional.role', 'en,,'),
(4, 7, '*', 'selector.conditional.role', 'en,,'),
(5, 8, '*', 'selector.conditional.role', 'en,,'),
(6, 9, '*', 'selector.conditional.role', 'en,,'),
(7, 10, '*', 'selector.conditional.role', 'en,,'),
(8, 11, '*', 'selector.conditional.role', 'en,,'),
(9, 12, '*', 'selector.conditional.role', 'en,,'),
(10, 13, '*', 'selector.conditional.role', 'en,,'),
(11, 14, '*', 'selector.conditional.role', 'en,,'),
(12, 15, '*', 'selector.conditional.role', 'en,,'),
(13, 16, '*', 'selector.conditional.role', 'en,,'),
(14, 17, '*', 'selector.conditional.role', 'en,,'),
(15, 18, '*', 'selector.conditional.role', 'en,,');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `portlet_application`
--

DROP TABLE IF EXISTS `portlet_application`;
CREATE TABLE IF NOT EXISTS `portlet_application` (
  `APPLICATION_ID` int(11) NOT NULL,
  `APP_NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `CONTEXT_PATH` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `REVISION` int(11) NOT NULL,
  `VERSION` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `APP_TYPE` int(11) DEFAULT NULL,
  `CHECKSUM` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SECURITY_REF` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DEFAULT_NAMESPACE` varchar(120) COLLATE utf8_unicode_ci DEFAULT NULL,
  `RESOURCE_BUNDLE` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`APPLICATION_ID`),
  UNIQUE KEY `UK_APPLICATION` (`APP_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `portlet_application`
--

INSERT INTO `portlet_application` (`APPLICATION_ID`, `APP_NAME`, `CONTEXT_PATH`, `REVISION`, `VERSION`, `APP_TYPE`, `CHECKSUM`, `SECURITY_REF`, `DEFAULT_NAMESPACE`, `RESOURCE_BUNDLE`) VALUES
(1, 'jetspeed-layouts', '<portal>', 1, '1.0', 1, '982190297', NULL, '', NULL),
(3, 'ingrid-portal-mdek', '/ingrid-portal-mdek', 1, '1.0', 0, '382981415', NULL, '', NULL),
(21, 'ingrid-portal-apps', '/ingrid-portal-apps', 2, '1.0', 0, '3223520607', NULL, '', NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `portlet_definition`
--

DROP TABLE IF EXISTS `portlet_definition`;
CREATE TABLE IF NOT EXISTS `portlet_definition` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `CLASS_NAME` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `EXPIRATION_CACHE` int(11) DEFAULT NULL,
  `RESOURCE_BUNDLE` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFERENCE_VALIDATOR` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SECURITY_REF` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `CACHE_SCOPE` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `CLONE_PARENT` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `portlet_definition`
--

INSERT INTO `portlet_definition` (`ID`, `NAME`, `CLASS_NAME`, `APPLICATION_ID`, `EXPIRATION_CACHE`, `RESOURCE_BUNDLE`, `PREFERENCE_VALIDATOR`, `SECURITY_REF`, `CACHE_SCOPE`, `CLONE_PARENT`) VALUES
(1, 'IngridClearLayout', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'de.ingrid.portal.resources.PortalLayoutResources', NULL, NULL, 'private', NULL),
(2, 'IngridOneColumn', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'de.ingrid.portal.resources.PortalLayoutResources', NULL, NULL, 'private', NULL),
(3, 'IngridTwoColumns', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'de.ingrid.portal.resources.PortalLayoutResources', NULL, NULL, 'private', NULL),
(4, 'SimpleLayout', 'org.apache.jetspeed.portlets.layout.LayoutPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(5, 'VelocityOneColumn', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(6, 'VelocityOneColumnTable', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(7, 'VelocityTwoColumns', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(8, 'VelocityTwoColumnsSmallLeft', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(9, 'VelocityThreeColumns', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(10, 'VelocityThreeColumnsTable', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(11, 'VelocityOneColumnNoActions', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(12, 'VelocityTwoColumnsNoActions', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(13, 'VelocityThreeColumnsNoActions', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(14, 'VelocityTwoColumns2575NoActions', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(15, 'VelocityTwoColumns2575', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(16, 'VelocityTwoColumnsSmallLeftNoActions', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(17, 'VelocityTwoColumnsTable', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(18, 'VelocityFourColumns', 'org.apache.jetspeed.portlets.layout.MultiColumnPortlet', 1, 0, 'org.apache.jetspeed.portlets.layout.resources.LayoutResource', NULL, NULL, 'private', NULL),
(101, 'MdekEntryPortlet', 'de.ingrid.portal.portlets.mdek.MdekEntryPortlet', 3, 0, 'de.ingrid.portal.resources.MdekResources', NULL, NULL, 'private', NULL),
(102, 'MdekPortalAdminPortlet', 'de.ingrid.portal.portlets.mdek.MdekPortalAdminPortlet', 3, 0, 'de.ingrid.portal.resources.MdekResources', NULL, NULL, 'private', NULL),
(103, 'MdekAdminLoginPortlet', 'de.ingrid.portal.portlets.mdek.MdekAdminLoginPortlet', 3, 0, 'de.ingrid.portal.resources.MdekResources', NULL, NULL, 'private', NULL),
(121, 'InfoPortlet', 'de.ingrid.portal.portlets.InfoPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL),
(122, 'DetectJavaScriptPortlet', 'de.ingrid.portal.portlets.DetectJavaScriptPortlet', 21, 0, NULL, NULL, NULL, 'private', NULL),
(123, 'HelpPortlet', 'de.ingrid.portal.portlets.HelpPortlet', 21, 0, 'de.ingrid.portal.resources.HelpPortletResources', NULL, NULL, 'private', NULL),
(124, 'AdminComponentMonitorPortlet', 'de.ingrid.portal.portlets.admin.AdminComponentMonitorPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(125, 'ContentRSSPortlet', 'de.ingrid.portal.portlets.admin.ContentRSSPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(126, 'ContentPartnerPortlet', 'de.ingrid.portal.portlets.admin.ContentPartnerPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(127, 'ContentProviderPortlet', 'de.ingrid.portal.portlets.admin.ContentProviderPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(128, 'AdminUserPortlet', 'de.ingrid.portal.portlets.admin.AdminUserPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(129, 'AdminIPlugPortlet', 'de.ingrid.portal.portlets.admin.AdminIPlugPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(130, 'AdminCMSPortlet', 'de.ingrid.portal.portlets.admin.AdminCMSPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(131, 'AdminWMSPortlet', 'de.ingrid.portal.portlets.admin.AdminWMSPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(132, 'AdminStatisticsPortlet', 'de.ingrid.portal.portlets.admin.AdminStatisticsPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(133, 'AdminHomepagePortlet', 'de.ingrid.portal.portlets.admin.AdminHomepagePortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(134, 'AdminPortalProfilePortlet', 'de.ingrid.portal.portlets.admin.AdminPortalProfilePortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(135, 'MyPortalEditAboutInfoPortlet', 'de.ingrid.portal.portlets.InfoPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL),
(136, 'MyPortalEditNewsletterInfoPortlet', 'de.ingrid.portal.portlets.InfoPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL),
(137, 'MyPortalEditAdvancedInfoPortlet', 'de.ingrid.portal.portlets.InfoPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL),
(138, 'AdminUserMigrationPortlet', 'de.ingrid.portal.portlets.admin.AdminUserMigrationPortlet', 21, 0, 'de.ingrid.portal.resources.AdminPortalResources', NULL, NULL, 'private', NULL),
(139, 'SearchSimple', 'de.ingrid.portal.portlets.SearchSimplePortlet', 21, 0, 'de.ingrid.portal.resources.SearchSimpleResources', NULL, NULL, 'private', NULL),
(140, 'EnvironmentTeaser', 'de.ingrid.portal.portlets.EnvironmentTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.EnvironmentSearchResources', NULL, NULL, 'private', NULL),
(141, 'RssNewsTeaser', 'de.ingrid.portal.portlets.RssNewsTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.RssNewsResources', NULL, NULL, 'private', NULL),
(142, 'RssNews', 'de.ingrid.portal.portlets.RssNewsPortlet', 21, 0, 'de.ingrid.portal.resources.RssNewsResources', NULL, NULL, 'private', NULL),
(143, 'CMSPortlet', 'de.ingrid.portal.portlets.CMSPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL),
(144, 'IngridInformPortlet', 'de.ingrid.portal.portlets.CMSPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL),
(145, 'IngridWelcomePortlet', 'de.ingrid.portal.portlets.CMSPortlet', 21, 0, 'de.ingrid.portal.resources.InfoPortletResources', NULL, NULL, 'private', NULL),
(146, 'ServiceTeaser', 'de.ingrid.portal.portlets.ServiceTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.ServiceSearchResources', NULL, NULL, 'private', NULL),
(147, 'MeasuresTeaser', 'de.ingrid.portal.portlets.MeasuresTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.MeasuresSearchResources', NULL, NULL, 'private', NULL),
(148, 'ChronicleTeaser', 'de.ingrid.portal.portlets.ChronicleTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.ChronicleSearchResources', NULL, NULL, 'private', NULL),
(149, 'WeatherTeaser', 'de.ingrid.portal.portlets.WeatherTeaserPortlet', 21, 0, 'de.ingrid.portal.resources.WeatherTeaserResources', NULL, NULL, 'private', NULL),
(150, 'Contact', 'de.ingrid.portal.portlets.ContactPortlet', 21, 0, 'de.ingrid.portal.resources.ContactResources', NULL, NULL, 'private', NULL),
(151, 'ContactNewsletterPortlet', 'de.ingrid.portal.portlets.ContactNewsletterPortlet', 21, 0, 'de.ingrid.portal.resources.ContactNewsletterResources', NULL, NULL, 'private', NULL),
(152, 'MyPortalLoginPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalLoginPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL),
(153, 'MyPortalOverviewPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalOverviewPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL),
(154, 'MyPortalCreateAccountPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalCreateAccountPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL),
(155, 'MyPortalEditAccountPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalEditAccountPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL),
(156, 'MyPortalPasswordForgottenPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPasswordForgottenPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL),
(157, 'MyPortalPersonalizeOverviewPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPersonalizeOverviewPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL),
(158, 'MyPortalPersonalizeHomePortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPersonalizeHomePortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL),
(159, 'MyPortalPersonalizePartnerPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPersonalizePartnerPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL),
(160, 'MyPortalPersonalizeSourcesPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPersonalizeSourcesPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL),
(161, 'MyPortalPersonalizeSearchSettingsPortlet', 'de.ingrid.portal.portlets.myportal.MyPortalPersonalizeSearchSettingsPortlet', 21, 0, 'de.ingrid.portal.resources.MyPortalResources', NULL, NULL, 'private', NULL),
(162, 'ShowPartnerPortlet', 'de.ingrid.portal.portlets.ShowPartnerPortlet', 21, 0, 'de.ingrid.portal.resources.ShowPartnerPortletResources', NULL, NULL, 'private', NULL),
(163, 'ShowDataSourcePortlet', 'de.ingrid.portal.portlets.ShowDataSourcePortlet', 21, 0, 'de.ingrid.portal.resources.ShowDataSourcePortletResources', NULL, NULL, 'private', NULL),
(164, 'ServiceSearch', 'de.ingrid.portal.portlets.ServiceSearchPortlet', 21, 0, 'de.ingrid.portal.resources.ServiceSearchResources', NULL, NULL, 'private', NULL),
(165, 'ServiceResult', 'de.ingrid.portal.portlets.ServiceResultPortlet', 21, 0, 'de.ingrid.portal.resources.ServiceSearchResources', NULL, NULL, 'private', NULL),
(166, 'MeasuresSearch', 'de.ingrid.portal.portlets.MeasuresSearchPortlet', 21, 0, 'de.ingrid.portal.resources.MeasuresSearchResources', NULL, NULL, 'private', NULL),
(167, 'MeasuresResult', 'de.ingrid.portal.portlets.MeasuresResultPortlet', 21, 0, 'de.ingrid.portal.resources.MeasuresSearchResources', NULL, NULL, 'private', NULL),
(168, 'EnvironmentSearch', 'de.ingrid.portal.portlets.EnvironmentSearchPortlet', 21, 0, 'de.ingrid.portal.resources.EnvironmentSearchResources', NULL, NULL, 'private', NULL),
(169, 'EnvironmentResult', 'de.ingrid.portal.portlets.EnvironmentResultPortlet', 21, 0, 'de.ingrid.portal.resources.EnvironmentSearchResources', NULL, NULL, 'private', NULL),
(170, 'SaveMapsPortlet', 'de.ingrid.portal.portlets.SaveMapsPortlet', 21, 0, 'de.ingrid.portal.resources.CommonResources', NULL, NULL, 'private', NULL),
(171, 'ShowMapsPortlet', 'de.ingrid.portal.portlets.ShowMapsPortlet', 21, 0, 'de.ingrid.portal.resources.CommonResources', NULL, NULL, 'private', NULL),
(172, 'ChronicleSearch', 'de.ingrid.portal.portlets.ChronicleSearchPortlet', 21, 0, 'de.ingrid.portal.resources.ChronicleSearchResources', NULL, NULL, 'private', NULL),
(173, 'ChronicleResult', 'de.ingrid.portal.portlets.ChronicleResultPortlet', 21, 0, 'de.ingrid.portal.resources.ChronicleSearchResources', NULL, NULL, 'private', NULL),
(174, 'SearchSettings', 'de.ingrid.portal.portlets.SearchSettingsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchSimpleResources', NULL, NULL, 'private', NULL),
(175, 'SearchSavePortlet', 'de.ingrid.portal.portlets.SearchSavePortlet', 21, 0, 'de.ingrid.portal.resources.SearchSimpleResources', NULL, NULL, 'private', NULL),
(176, 'SearchHistory', 'de.ingrid.portal.portlets.SearchHistoryPortlet', 21, 0, 'de.ingrid.portal.resources.SearchSimpleResources', NULL, NULL, 'private', NULL),
(177, 'SearchSimilar', 'de.ingrid.portal.portlets.SearchSimilarPortlet', 21, 0, 'de.ingrid.portal.resources.SearchResultResources', NULL, NULL, 'private', NULL),
(178, 'SearchResult', 'de.ingrid.portal.portlets.SearchResultPortlet', 21, 0, 'de.ingrid.portal.resources.SearchResultResources', NULL, NULL, 'private', NULL),
(179, 'SearchDetail', 'de.ingrid.portal.portlets.SearchDetailPortlet', 21, 0, 'de.ingrid.portal.resources.SearchDetailResources', NULL, NULL, 'private', NULL),
(180, 'SearchCatalogHierarchy', 'de.ingrid.portal.portlets.searchcatalog.SearchCatalogHierarchyPortlet', 21, 0, 'de.ingrid.portal.resources.SearchCatalogResources', NULL, NULL, 'private', NULL),
(181, 'SearchCatalogThesaurus', 'de.ingrid.portal.portlets.searchcatalog.SearchCatalogThesaurusPortlet', 21, 0, 'de.ingrid.portal.resources.SearchCatalogResources', NULL, NULL, 'private', NULL),
(182, 'SearchCatalogThesaurusResult', 'de.ingrid.portal.portlets.searchcatalog.SearchCatalogThesaurusResultPortlet', 21, 0, 'de.ingrid.portal.resources.SearchCatalogResources', NULL, NULL, 'private', NULL),
(183, 'SearchExtEnvTopicTerms', 'de.ingrid.portal.portlets.searchext.SearchExtEnvTopicTermsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(184, 'SearchExtEnvTopicThesaurus', 'de.ingrid.portal.portlets.searchext.SearchExtEnvTopicThesaurusPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(185, 'SearchExtEnvPlaceGeothesaurus', 'de.ingrid.portal.portlets.searchext.SearchExtEnvPlaceGeothesaurusPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(186, 'SearchExtEnvPlaceMap', 'de.ingrid.portal.portlets.searchext.SearchExtEnvPlaceMapPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(187, 'SearchExtEnvTimeConstraint', 'de.ingrid.portal.portlets.searchext.SearchExtEnvTimeConstraintPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(188, 'SearchExtEnvTimeChronicle', 'de.ingrid.portal.portlets.searchext.SearchExtEnvTimeChroniclePortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(189, 'SearchExtEnvAreaContents', 'de.ingrid.portal.portlets.searchext.SearchExtEnvAreaContentsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(190, 'SearchExtEnvAreaSources', 'de.ingrid.portal.portlets.searchext.SearchExtEnvAreaSourcesPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(191, 'SearchExtEnvAreaPartner', 'de.ingrid.portal.portlets.searchext.SearchExtEnvAreaPartnerPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(192, 'SearchExtAdrTopicTerms', 'de.ingrid.portal.portlets.searchext.SearchExtAdrTopicTermsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(193, 'SearchExtAdrTopicMode', 'de.ingrid.portal.portlets.searchext.SearchExtAdrTopicModePortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(194, 'SearchExtAdrPlaceReference', 'de.ingrid.portal.portlets.searchext.SearchExtAdrPlaceReferencePortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(195, 'SearchExtAdrAreaPartner', 'de.ingrid.portal.portlets.searchext.SearchExtAdrAreaPartnerPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(196, 'SearchExtResTopicTerms', 'de.ingrid.portal.portlets.searchext.SearchExtResTopicTermsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(197, 'SearchExtResTopicAttributes', 'de.ingrid.portal.portlets.searchext.SearchExtResTopicAttributesPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(198, 'SearchExtLawTopicTerms', 'de.ingrid.portal.portlets.searchext.SearchExtLawTopicTermsPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(199, 'SearchExtLawTopicThesaurus', 'de.ingrid.portal.portlets.searchext.SearchExtLawTopicThesaurusPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(200, 'SearchExtLawAreaPartner', 'de.ingrid.portal.portlets.searchext.SearchExtLawAreaPartnerPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(201, 'LanguageSwitch', 'de.ingrid.portal.portlets.LanguageSwitchPortlet', 21, 0, 'de.ingrid.portal.resources.SearchExtendedResources', NULL, NULL, 'private', NULL),
(202, 'ShowFeaturesPortlet', 'de.ingrid.portal.portlets.ShowFeaturesPortlet', 21, 0, 'de.ingrid.portal.resources.FeaturesResources', NULL, NULL, 'private', NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `portlet_filter`
--

DROP TABLE IF EXISTS `portlet_filter`;
CREATE TABLE IF NOT EXISTS `portlet_filter` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `FILTER_NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `FILTER_CLASS` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `portlet_filter`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `portlet_listener`
--

DROP TABLE IF EXISTS `portlet_listener`;
CREATE TABLE IF NOT EXISTS `portlet_listener` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `LISTENER_CLASS` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `portlet_listener`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `portlet_preference`
--

DROP TABLE IF EXISTS `portlet_preference`;
CREATE TABLE IF NOT EXISTS `portlet_preference` (
  `ID` int(11) NOT NULL,
  `DTYPE` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `APPLICATION_NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `PORTLET_NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `ENTITY_ID` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_NAME` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `NAME` varchar(254) COLLATE utf8_unicode_ci NOT NULL,
  `READONLY` smallint(6) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UIX_PORTLET_PREFERENCE` (`DTYPE`,`APPLICATION_NAME`,`PORTLET_NAME`,`ENTITY_ID`,`USER_NAME`,`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `portlet_preference`
--

INSERT INTO `portlet_preference` (`ID`, `DTYPE`, `APPLICATION_NAME`, `PORTLET_NAME`, `ENTITY_ID`, `USER_NAME`, `NAME`, `READONLY`) VALUES
(49, 'portlet', 'ingrid-portal-mdek', 'MdekEntryPortlet', NULL, NULL, 'titleKey', 0),
(50, 'portlet', 'ingrid-portal-mdek', 'MdekPortalAdminPortlet', NULL, NULL, 'titleKey', 0),
(51, 'portlet', 'ingrid-portal-mdek', 'MdekAdminLoginPortlet', NULL, NULL, 'titleKey', 0),
(61, 'portlet', 'ingrid-portal-apps', 'MyPortalEditAboutInfoPortlet', NULL, NULL, 'infoTemplate', 0),
(62, 'portlet', 'ingrid-portal-apps', 'MyPortalEditAboutInfoPortlet', NULL, NULL, 'titleKey', 0),
(63, 'portlet', 'ingrid-portal-apps', 'MyPortalEditNewsletterInfoPortlet', NULL, NULL, 'infoTemplate', 0),
(64, 'portlet', 'ingrid-portal-apps', 'MyPortalEditNewsletterInfoPortlet', NULL, NULL, 'titleKey', 0),
(65, 'portlet', 'ingrid-portal-apps', 'MyPortalEditAdvancedInfoPortlet', NULL, NULL, 'infoTemplate', 0),
(66, 'portlet', 'ingrid-portal-apps', 'MyPortalEditAdvancedInfoPortlet', NULL, NULL, 'titleKey', 0),
(67, 'portlet', 'ingrid-portal-apps', 'SearchSimple', NULL, NULL, 'portlet-type', 0),
(68, 'portlet', 'ingrid-portal-apps', 'SearchSimple', NULL, NULL, 'default-vertical-position', 0),
(69, 'portlet', 'ingrid-portal-apps', 'SearchSimple', NULL, NULL, 'helpKey', 0),
(70, 'portlet', 'ingrid-portal-apps', 'SearchSimple', NULL, NULL, 'titleKey', 0),
(71, 'portlet', 'ingrid-portal-apps', 'EnvironmentTeaser', NULL, NULL, 'portlet-type', 0),
(72, 'portlet', 'ingrid-portal-apps', 'EnvironmentTeaser', NULL, NULL, 'default-vertical-position', 0),
(73, 'portlet', 'ingrid-portal-apps', 'EnvironmentTeaser', NULL, NULL, 'helpKey', 0),
(74, 'portlet', 'ingrid-portal-apps', 'EnvironmentTeaser', NULL, NULL, 'titleKey', 0),
(75, 'portlet', 'ingrid-portal-apps', 'RssNewsTeaser', NULL, NULL, 'portlet-type', 0),
(76, 'portlet', 'ingrid-portal-apps', 'RssNewsTeaser', NULL, NULL, 'default-vertical-position', 0),
(77, 'portlet', 'ingrid-portal-apps', 'RssNewsTeaser', NULL, NULL, 'helpKey', 0),
(78, 'portlet', 'ingrid-portal-apps', 'RssNewsTeaser', NULL, NULL, 'titleKey', 0),
(79, 'portlet', 'ingrid-portal-apps', 'RssNewsTeaser', NULL, NULL, 'noOfEntriesDisplayed', 0),
(80, 'portlet', 'ingrid-portal-apps', 'CMSPortlet', NULL, NULL, 'cmsKey', 0),
(81, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'portlet-type', 0),
(82, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'cmsKey', 0),
(83, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'default-vertical-position', 0),
(84, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'helpKey', 0),
(85, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'infoTemplate', 0),
(86, 'portlet', 'ingrid-portal-apps', 'IngridInformPortlet', NULL, NULL, 'titleKey', 0),
(87, 'portlet', 'ingrid-portal-apps', 'IngridWelcomePortlet', NULL, NULL, 'portlet-type', 0),
(88, 'portlet', 'ingrid-portal-apps', 'IngridWelcomePortlet', NULL, NULL, 'cmsKey', 0),
(89, 'portlet', 'ingrid-portal-apps', 'IngridWelcomePortlet', NULL, NULL, 'default-vertical-position', 0),
(90, 'portlet', 'ingrid-portal-apps', 'IngridWelcomePortlet', NULL, NULL, 'infoTemplate', 0),
(91, 'portlet', 'ingrid-portal-apps', 'IngridWelcomePortlet', NULL, NULL, 'titleKey', 0),
(92, 'portlet', 'ingrid-portal-apps', 'ServiceTeaser', NULL, NULL, 'portlet-type', 0),
(93, 'portlet', 'ingrid-portal-apps', 'ServiceTeaser', NULL, NULL, 'default-vertical-position', 0),
(94, 'portlet', 'ingrid-portal-apps', 'ServiceTeaser', NULL, NULL, 'helpKey', 0),
(95, 'portlet', 'ingrid-portal-apps', 'ServiceTeaser', NULL, NULL, 'titleKey', 0),
(96, 'portlet', 'ingrid-portal-apps', 'MeasuresTeaser', NULL, NULL, 'portlet-type', 0),
(97, 'portlet', 'ingrid-portal-apps', 'MeasuresTeaser', NULL, NULL, 'default-vertical-position', 0),
(98, 'portlet', 'ingrid-portal-apps', 'MeasuresTeaser', NULL, NULL, 'helpKey', 0),
(99, 'portlet', 'ingrid-portal-apps', 'MeasuresTeaser', NULL, NULL, 'titleKey', 0),
(100, 'portlet', 'ingrid-portal-apps', 'ChronicleTeaser', NULL, NULL, 'portlet-type', 0),
(101, 'portlet', 'ingrid-portal-apps', 'ChronicleTeaser', NULL, NULL, 'default-vertical-position', 0),
(102, 'portlet', 'ingrid-portal-apps', 'ChronicleTeaser', NULL, NULL, 'helpKey', 0),
(103, 'portlet', 'ingrid-portal-apps', 'ChronicleTeaser', NULL, NULL, 'titleKey', 0),
(104, 'portlet', 'ingrid-portal-apps', 'WeatherTeaser', NULL, NULL, 'portlet-type', 0),
(105, 'portlet', 'ingrid-portal-apps', 'WeatherTeaser', NULL, NULL, 'default-vertical-position', 0),
(106, 'portlet', 'ingrid-portal-apps', 'WeatherTeaser', NULL, NULL, 'titleKey', 0),
(107, 'portlet', 'ingrid-portal-apps', 'SearchCatalogHierarchy', NULL, NULL, 'helpKey', 0),
(108, 'portlet', 'ingrid-portal-apps', 'SearchCatalogThesaurus', NULL, NULL, 'helpKey', 0);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `portlet_preference_value`
--

DROP TABLE IF EXISTS `portlet_preference_value`;
CREATE TABLE IF NOT EXISTS `portlet_preference_value` (
  `ID` int(11) NOT NULL,
  `PREF_ID` int(11) NOT NULL,
  `IDX` smallint(6) NOT NULL,
  `PREF_VALUE` varchar(4000) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`,`PREF_ID`,`IDX`),
  KEY `IX_PORTLET_PREFERENCE` (`PREF_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `portlet_preference_value`
--

INSERT INTO `portlet_preference_value` (`ID`, `PREF_ID`, `IDX`, `PREF_VALUE`) VALUES
(49, 49, 0, 'mdek.title.entry'),
(50, 50, 0, 'mdek.title.portaladmin'),
(51, 51, 0, 'mdek.title.adminlogin'),
(61, 61, 0, '/WEB-INF/templates/myportal/myportal_navigation.vm'),
(62, 62, 0, 'myPortal.info.navigation.title'),
(63, 63, 0, '/WEB-INF/templates/newsletter_teaser.vm'),
(64, 64, 0, 'teaser.newsletter.title'),
(65, 65, 0, '/WEB-INF/templates/myportal/login_teaser.vm'),
(66, 66, 0, 'teaser.login.title'),
(67, 67, 0, 'ingrid-home'),
(68, 68, 0, '0'),
(69, 69, 0, 'search-1'),
(70, 70, 0, 'searchSimple.title.search'),
(71, 71, 0, 'ingrid-home'),
(72, 72, 0, '1'),
(73, 73, 0, 'search-topics-1'),
(74, 74, 0, 'teaser.environment.title'),
(75, 75, 0, 'ingrid-home'),
(76, 76, 0, '2'),
(77, 77, 0, 'rss-news-1'),
(78, 78, 0, 'news.teaser.title'),
(79, 79, 0, '4'),
(80, 80, 0, 'portalu.cms.default'),
(81, 81, 0, 'ingrid-home-marginal'),
(82, 82, 0, 'portalu.teaser.inform'),
(83, 83, 0, '0'),
(84, 84, 0, 'ingrid-inform-1'),
(85, 85, 0, '/WEB-INF/templates/default_cms.vm'),
(86, 86, 0, 'teaser.ingridInform.title'),
(87, 87, 0, 'ingrid-home'),
(88, 88, 0, 'ingrid.home.welcome'),
(89, 89, 0, '3'),
(90, 90, 0, '/WEB-INF/templates/default_cms.vm'),
(91, 91, 0, 'ingrid.home.welcome.title'),
(92, 92, 0, 'ingrid-home-marginal'),
(93, 93, 0, '1'),
(94, 94, 0, 'search-service-1'),
(95, 95, 0, 'teaser.service.title'),
(96, 96, 0, 'ingrid-home-marginal'),
(97, 97, 0, '2'),
(98, 98, 0, 'search-measure-1'),
(99, 99, 0, 'teaser.measures.title'),
(100, 100, 0, 'ingrid-home-marginal'),
(101, 101, 0, '3'),
(102, 102, 0, 'search-chronicle-1'),
(103, 103, 0, 'chronicle.teaser.title'),
(104, 104, 0, 'ingrid-home-marginal'),
(105, 105, 0, '4'),
(106, 106, 0, 'teaser.weather.title'),
(107, 107, 0, 'search-catalog-1'),
(108, 108, 0, 'search-catalog-2');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `portlet_statistics`
--

DROP TABLE IF EXISTS `portlet_statistics`;
CREATE TABLE IF NOT EXISTS `portlet_statistics` (
  `IPADDRESS` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_NAME` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TIME_STAMP` datetime DEFAULT NULL,
  `PAGE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PORTLET` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `ELAPSED_TIME` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `portlet_statistics`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `portlet_supports`
--

DROP TABLE IF EXISTS `portlet_supports`;
CREATE TABLE IF NOT EXISTS `portlet_supports` (
  `SUPPORTS_ID` int(11) NOT NULL,
  `PORTLET_ID` int(11) NOT NULL,
  `MIME_TYPE` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `MODES` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `STATES` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`SUPPORTS_ID`),
  UNIQUE KEY `UK_SUPPORTS` (`PORTLET_ID`,`MIME_TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `portlet_supports`
--

INSERT INTO `portlet_supports` (`SUPPORTS_ID`, `PORTLET_ID`, `MIME_TYPE`, `MODES`, `STATES`) VALUES
(1, 1, 'text/html', '"view"', ''),
(2, 2, 'text/html', '"view"', ''),
(3, 3, 'text/html', '"view"', ''),
(4, 4, 'text/html', '"view"', ''),
(5, 5, 'text/html', '"view","edit","help"', ''),
(6, 5, 'text/vnd.wap.wml', '"view"', ''),
(7, 6, 'text/html', '"view","edit","help"', ''),
(8, 7, 'text/html', '"view","edit","help"', ''),
(9, 8, 'text/html', '"view","edit","help"', ''),
(10, 9, 'text/html', '"view","edit","help"', ''),
(11, 10, 'text/html', '"view","edit","help"', ''),
(12, 11, 'text/html', '"view"', ''),
(13, 11, 'text/vnd.wap.wml', '"view"', ''),
(14, 12, 'text/html', '"view"', ''),
(15, 13, 'text/html', '"view"', ''),
(16, 14, 'text/html', '"view"', ''),
(17, 15, 'text/html', '"view","edit","help"', ''),
(18, 16, 'text/html', '"view"', ''),
(19, 17, 'text/html', '"view","edit","help"', ''),
(20, 18, 'text/html', '"view","edit","help"', ''),
(103, 101, 'text/html', '"view"', ''),
(104, 102, 'text/html', '"view"', ''),
(105, 103, 'text/html', '"view"', ''),
(121, 121, 'text/html', '"view"', ''),
(122, 122, 'text/html', '"view"', ''),
(123, 123, 'text/html', '"view"', ''),
(124, 124, 'text/html', '"view"', ''),
(125, 125, 'text/html', '"view"', ''),
(126, 126, 'text/html', '"view"', ''),
(127, 127, 'text/html', '"view"', ''),
(128, 128, 'text/html', '"view"', ''),
(129, 129, 'text/html', '"view"', ''),
(130, 130, 'text/html', '"view"', ''),
(131, 131, 'text/html', '"view"', ''),
(132, 132, 'text/html', '"view"', ''),
(133, 133, 'text/html', '"view"', ''),
(134, 134, 'text/html', '"view"', ''),
(135, 135, 'text/html', '"view"', ''),
(136, 136, 'text/html', '"view"', ''),
(137, 137, 'text/html', '"view"', ''),
(138, 138, 'text/html', '"view"', ''),
(139, 139, 'text/html', '"view"', ''),
(140, 140, 'text/html', '"view"', ''),
(141, 141, 'text/html', '"view"', ''),
(142, 142, 'text/html', '"view"', ''),
(143, 143, 'text/html', '"view"', ''),
(144, 144, 'text/html', '"view"', ''),
(145, 145, 'text/html', '"view"', ''),
(146, 146, 'text/html', '"view"', ''),
(147, 147, 'text/html', '"view"', ''),
(148, 148, 'text/html', '"view"', ''),
(149, 149, 'text/html', '"view"', ''),
(150, 150, 'text/html', '"view"', ''),
(151, 151, 'text/html', '"view"', ''),
(152, 152, 'text/html', '"view"', ''),
(153, 153, 'text/html', '"view"', ''),
(154, 154, 'text/html', '"view"', ''),
(155, 155, 'text/html', '"view"', ''),
(156, 156, 'text/html', '"view"', ''),
(157, 157, 'text/html', '"view"', ''),
(158, 158, 'text/html', '"view"', ''),
(159, 159, 'text/html', '"view"', ''),
(160, 160, 'text/html', '"view"', ''),
(161, 161, 'text/html', '"view"', ''),
(162, 162, 'text/html', '"view"', ''),
(163, 163, 'text/html', '"view"', ''),
(164, 164, 'text/html', '"view"', ''),
(165, 165, 'text/html', '"view"', ''),
(166, 166, 'text/html', '"view"', ''),
(167, 167, 'text/html', '"view"', ''),
(168, 168, 'text/html', '"view"', ''),
(169, 169, 'text/html', '"view"', ''),
(170, 170, 'text/html', '"view"', ''),
(171, 171, 'text/html', '"view"', ''),
(172, 172, 'text/html', '"view"', ''),
(173, 173, 'text/html', '"view"', ''),
(174, 174, 'text/html', '"view"', ''),
(175, 175, 'text/html', '"view"', ''),
(176, 176, 'text/html', '"view"', ''),
(177, 177, 'text/html', '"view"', ''),
(178, 178, 'text/html', '"view"', ''),
(179, 179, 'text/html', '"view"', ''),
(180, 180, 'text/html', '"view"', ''),
(181, 181, 'text/html', '"view"', ''),
(182, 182, 'text/html', '"view"', ''),
(183, 183, 'text/html', '"view"', ''),
(184, 184, 'text/html', '"view"', ''),
(185, 185, 'text/html', '"view"', ''),
(186, 186, 'text/html', '"view"', ''),
(187, 187, 'text/html', '"view"', ''),
(188, 188, 'text/html', '"view"', ''),
(189, 189, 'text/html', '"view"', ''),
(190, 190, 'text/html', '"view"', ''),
(191, 191, 'text/html', '"view"', ''),
(192, 192, 'text/html', '"view"', ''),
(193, 193, 'text/html', '"view"', ''),
(194, 194, 'text/html', '"view"', ''),
(195, 195, 'text/html', '"view"', ''),
(196, 196, 'text/html', '"view"', ''),
(197, 197, 'text/html', '"view"', ''),
(198, 198, 'text/html', '"view"', ''),
(199, 199, 'text/html', '"view"', ''),
(200, 200, 'text/html', '"view"', ''),
(201, 201, 'text/html', '"view"', ''),
(202, 202, 'text/html', '"view"', '');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `principal_permission`
--

DROP TABLE IF EXISTS `principal_permission`;
CREATE TABLE IF NOT EXISTS `principal_permission` (
  `PRINCIPAL_ID` int(11) NOT NULL,
  `PERMISSION_ID` int(11) NOT NULL,
  PRIMARY KEY (`PRINCIPAL_ID`,`PERMISSION_ID`),
  KEY `IX_PRINCIPAL_PERMISSION_1` (`PERMISSION_ID`),
  KEY `IX_PRINCIPAL_PERMISSION_2` (`PRINCIPAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `principal_permission`
--

INSERT INTO `principal_permission` (`PRINCIPAL_ID`, `PERMISSION_ID`) VALUES
(8, 1),
(11, 1),
(12, 1),
(13, 1),
(13, 2),
(13, 3),
(13, 4),
(8, 5),
(8, 6),
(13, 6),
(13, 7),
(13, 8),
(11, 11),
(12, 11),
(11, 12),
(12, 12),
(4, 17),
(9, 18),
(8, 19),
(8, 20),
(13, 22),
(4, 23),
(13, 24),
(13, 41);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `principal_rule_assoc`
--

DROP TABLE IF EXISTS `principal_rule_assoc`;
CREATE TABLE IF NOT EXISTS `principal_rule_assoc` (
  `PRINCIPAL_NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `LOCATOR_NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `RULE_ID` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`PRINCIPAL_NAME`,`LOCATOR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `principal_rule_assoc`
--

INSERT INTO `principal_rule_assoc` (`PRINCIPAL_NAME`, `LOCATOR_NAME`, `RULE_ID`) VALUES
('admin', 'page', 'role-fallback'),
('subsite', 'page', 'subsite-role-fallback-home'),
('subsite2', 'page', 'subsite2-role-fallback-home');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `processing_event`
--

DROP TABLE IF EXISTS `processing_event`;
CREATE TABLE IF NOT EXISTS `processing_event` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `processing_event`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `profile_page_assoc`
--

DROP TABLE IF EXISTS `profile_page_assoc`;
CREATE TABLE IF NOT EXISTS `profile_page_assoc` (
  `LOCATOR_HASH` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `PAGE_ID` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`LOCATOR_HASH`,`PAGE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `profile_page_assoc`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `profiling_rule`
--

DROP TABLE IF EXISTS `profiling_rule`;
CREATE TABLE IF NOT EXISTS `profiling_rule` (
  `RULE_ID` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `CLASS_NAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`RULE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `profiling_rule`
--

INSERT INTO `profiling_rule` (`RULE_ID`, `CLASS_NAME`, `TITLE`) VALUES
('group-fallback', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A role based fallback algorithm based on Jetspeed-1 group-based fallback'),
('j1', 'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule', 'The default profiling rule following the Jetspeed-1 hard-coded profiler fallback algorithm.'),
('j2', 'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule', 'The default profiling rule for users and mediatype minus language and country.'),
('path', 'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule', 'use a path to locate.'),
('role-fallback', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A role based fallback algorithm based on Jetspeed-1 role-based fallback'),
('role-group', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A role based fallback algorithm that searches all groups and roles for a user'),
('security', 'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule', 'The security profiling rule needed for credential change requirements.'),
('subsite-role-fallback-home', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A rule based on role fallback algorithm with specified subsite and home page'),
('subsite2-role-fallback-home', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A rule based on role fallback algorithm with specified subsite and home page'),
('user-role-fallback', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A role based fallback algorithm based on Jetspeed-1 role-based fallback'),
('user-rolecombo-fallback', 'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule', 'A role based fallback algorithm based on Jetspeed-1 role-based fallback');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `public_parameter`
--

DROP TABLE IF EXISTS `public_parameter`;
CREATE TABLE IF NOT EXISTS `public_parameter` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `IDENTIFIER` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `public_parameter`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `publishing_event`
--

DROP TABLE IF EXISTS `publishing_event`;
CREATE TABLE IF NOT EXISTS `publishing_event` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `publishing_event`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE IF NOT EXISTS `qrtz_blob_triggers` (
  `TRIGGER_NAME` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  `BLOB_DATA` blob,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_blob_triggers`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE IF NOT EXISTS `qrtz_calendars` (
  `CALENDAR_NAME` varchar(80) NOT NULL DEFAULT '',
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`CALENDAR_NAME`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_calendars`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE IF NOT EXISTS `qrtz_cron_triggers` (
  `TRIGGER_NAME` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  `CRON_EXPRESSION` varchar(80) NOT NULL DEFAULT '',
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_cron_triggers`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE IF NOT EXISTS `qrtz_fired_triggers` (
  `ENTRY_ID` varchar(95) NOT NULL DEFAULT '',
  `TRIGGER_NAME` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  `IS_VOLATILE` char(1) NOT NULL DEFAULT '',
  `INSTANCE_NAME` varchar(80) NOT NULL DEFAULT '',
  `FIRED_TIME` bigint(13) NOT NULL DEFAULT '0',
  `PRIORITY` int(11) NOT NULL DEFAULT '0',
  `STATE` varchar(16) NOT NULL DEFAULT '',
  `JOB_NAME` varchar(80) DEFAULT NULL,
  `JOB_GROUP` varchar(80) DEFAULT NULL,
  `IS_STATEFUL` char(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` char(1) DEFAULT NULL,
  PRIMARY KEY (`ENTRY_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_fired_triggers`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE IF NOT EXISTS `qrtz_job_details` (
  `JOB_NAME` varchar(80) NOT NULL DEFAULT '',
  `JOB_GROUP` varchar(80) NOT NULL DEFAULT '',
  `DESCRIPTION` varchar(120) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(128) NOT NULL DEFAULT '',
  `IS_DURABLE` char(1) NOT NULL DEFAULT '',
  `IS_VOLATILE` char(1) NOT NULL DEFAULT '',
  `IS_STATEFUL` char(1) NOT NULL DEFAULT '',
  `REQUESTS_RECOVERY` char(1) NOT NULL DEFAULT '',
  `JOB_DATA` blob,
  PRIMARY KEY (`JOB_NAME`,`JOB_GROUP`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_job_details`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_job_listeners`
--

DROP TABLE IF EXISTS `qrtz_job_listeners`;
CREATE TABLE IF NOT EXISTS `qrtz_job_listeners` (
  `JOB_NAME` varchar(80) NOT NULL DEFAULT '',
  `JOB_GROUP` varchar(80) NOT NULL DEFAULT '',
  `JOB_LISTENER` varchar(80) NOT NULL DEFAULT '',
  PRIMARY KEY (`JOB_NAME`,`JOB_GROUP`,`JOB_LISTENER`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_job_listeners`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE IF NOT EXISTS `qrtz_locks` (
  `LOCK_NAME` varchar(40) NOT NULL DEFAULT '',
  PRIMARY KEY (`LOCK_NAME`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_locks`
--

INSERT INTO `qrtz_locks` (`LOCK_NAME`) VALUES
('CALENDAR_ACCESS'),
('JOB_ACCESS'),
('MISFIRE_ACCESS'),
('STATE_ACCESS'),
('TRIGGER_ACCESS');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE IF NOT EXISTS `qrtz_paused_trigger_grps` (
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  PRIMARY KEY (`TRIGGER_GROUP`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_paused_trigger_grps`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE IF NOT EXISTS `qrtz_scheduler_state` (
  `INSTANCE_NAME` varchar(80) NOT NULL DEFAULT '',
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL DEFAULT '0',
  `CHECKIN_INTERVAL` bigint(13) NOT NULL DEFAULT '0',
  PRIMARY KEY (`INSTANCE_NAME`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_scheduler_state`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE IF NOT EXISTS `qrtz_simple_triggers` (
  `TRIGGER_NAME` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  `REPEAT_COUNT` bigint(7) NOT NULL DEFAULT '0',
  `REPEAT_INTERVAL` bigint(12) NOT NULL DEFAULT '0',
  `TIMES_TRIGGERED` bigint(7) NOT NULL DEFAULT '0',
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_simple_triggers`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE IF NOT EXISTS `qrtz_triggers` (
  `TRIGGER_NAME` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  `JOB_NAME` varchar(80) NOT NULL DEFAULT '',
  `JOB_GROUP` varchar(80) NOT NULL DEFAULT '',
  `IS_VOLATILE` char(1) NOT NULL DEFAULT '',
  `DESCRIPTION` varchar(120) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL DEFAULT '',
  `TRIGGER_TYPE` varchar(8) NOT NULL DEFAULT '',
  `START_TIME` bigint(13) NOT NULL DEFAULT '0',
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(80) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `JOB_NAME` (`JOB_NAME`,`JOB_GROUP`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_triggers`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `qrtz_trigger_listeners`
--

DROP TABLE IF EXISTS `qrtz_trigger_listeners`;
CREATE TABLE IF NOT EXISTS `qrtz_trigger_listeners` (
  `TRIGGER_NAME` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_LISTENER` varchar(80) NOT NULL DEFAULT '',
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_LISTENER`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `qrtz_trigger_listeners`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `rule_criterion`
--

DROP TABLE IF EXISTS `rule_criterion`;
CREATE TABLE IF NOT EXISTS `rule_criterion` (
  `CRITERION_ID` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `RULE_ID` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `FALLBACK_ORDER` int(11) NOT NULL,
  `REQUEST_TYPE` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `COLUMN_VALUE` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `FALLBACK_TYPE` int(11) DEFAULT '1',
  PRIMARY KEY (`CRITERION_ID`),
  KEY `IX_RULE_CRITERION_1` (`RULE_ID`),
  KEY `IX_RULE_CRITERION_2` (`RULE_ID`,`FALLBACK_ORDER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `rule_criterion`
--

INSERT INTO `rule_criterion` (`CRITERION_ID`, `RULE_ID`, `FALLBACK_ORDER`, `REQUEST_TYPE`, `NAME`, `COLUMN_VALUE`, `FALLBACK_TYPE`) VALUES
('1', 'j1', 0, 'path.session', 'page', 'default-page', 0),
('10', 'role-group', 1, 'navigation', 'navigation', '/', 2),
('11', 'role-group', 2, 'group', 'group', NULL, 2),
('12', 'group-fallback', 0, 'group', 'group', NULL, 2),
('13', 'group-fallback', 1, 'path.session', 'page', 'default-page', 0),
('14', 'security', 0, 'hard.coded', 'page', '/my-account.psml', 0),
('15', 'j2', 0, 'path.session', 'page', 'default-page', 0),
('16', 'j2', 1, 'group.role.user', 'user', NULL, 0),
('17', 'j2', 2, 'mediatype', 'mediatype', NULL, 1),
('18', 'user-role-fallback', 0, 'user', 'user', NULL, 2),
('19', 'user-role-fallback', 1, 'navigation', 'navigation', '/', 2),
('2', 'j1', 1, 'group.role.user', 'user', NULL, 0),
('20', 'user-role-fallback', 2, 'role', 'role', NULL, 2),
('21', 'user-role-fallback', 3, 'path.session', 'page', 'default-page', 1),
('22', 'user-rolecombo-fallback', 0, 'user', 'user', NULL, 2),
('23', 'user-rolecombo-fallback', 1, 'navigation', 'navigation', '/', 2),
('24', 'user-rolecombo-fallback', 2, 'rolecombo', 'role', NULL, 2),
('25', 'user-rolecombo-fallback', 3, 'path.session', 'page', 'default-page', 1),
('26', 'subsite-role-fallback-home', 0, 'navigation', 'navigation', 'subsite-root', 2),
('27', 'subsite-role-fallback-home', 1, 'role', 'role', NULL, 2),
('28', 'subsite-role-fallback-home', 2, 'path', 'path', 'subsite-default-page', 0),
('29', 'subsite2-role-fallback-home', 0, 'navigation', 'navigation', 'subsite-root', 2),
('3', 'j1', 2, 'mediatype', 'mediatype', NULL, 1),
('30', 'subsite2-role-fallback-home', 1, 'role', 'role', NULL, 2),
('31', 'subsite2-role-fallback-home', 2, 'path', 'path', 'subsite2-default-page', 0),
('4', 'j1', 3, 'language', 'language', NULL, 1),
('5', 'j1', 4, 'country', 'country', NULL, 1),
('6', 'role-fallback', 0, 'role', 'role', NULL, 2),
('7', 'role-fallback', 1, 'path.session', 'page', 'default-page', 0),
('8', 'path', 0, 'path', 'path', '/', 0),
('9', 'role-group', 0, 'role', 'role', NULL, 2);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `runtime_option`
--

DROP TABLE IF EXISTS `runtime_option`;
CREATE TABLE IF NOT EXISTS `runtime_option` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `OWNER_CLASS_NAME` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `runtime_option`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `runtime_value`
--

DROP TABLE IF EXISTS `runtime_value`;
CREATE TABLE IF NOT EXISTS `runtime_value` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `RVALUE` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `runtime_value`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `secured_portlet`
--

DROP TABLE IF EXISTS `secured_portlet`;
CREATE TABLE IF NOT EXISTS `secured_portlet` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `secured_portlet`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `security_attribute`
--

DROP TABLE IF EXISTS `security_attribute`;
CREATE TABLE IF NOT EXISTS `security_attribute` (
  `ATTR_ID` int(11) NOT NULL,
  `PRINCIPAL_ID` int(11) NOT NULL,
  `ATTR_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `ATTR_VALUE` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ATTR_ID`,`PRINCIPAL_ID`,`ATTR_NAME`),
  KEY `IX_NAME_LOOKUP` (`ATTR_NAME`),
  KEY `IX_PRINCIPAL_ATTR` (`PRINCIPAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `security_attribute`
--

INSERT INTO `security_attribute` (`ATTR_ID`, `PRINCIPAL_ID`, `ATTR_NAME`, `ATTR_VALUE`) VALUES
(27, 16, 'user.business-info.online.email', 'yrmail@yrmail.yrmail'),
(28, 16, 'user.business-info.postal.city', ''),
(29, 16, 'user.business-info.postal.postalcode', ''),
(30, 16, 'user.business-info.postal.street', ''),
(31, 16, 'user.custom.ingrid.user.age.group', '0'),
(32, 16, 'user.custom.ingrid.user.attention.from', ''),
(33, 16, 'user.custom.ingrid.user.interest', '0'),
(34, 16, 'user.custom.ingrid.user.profession', '0'),
(35, 16, 'user.custom.ingrid.user.subscribe.newsletter', ''),
(36, 16, 'user.name.family', 'Administrator'),
(37, 16, 'user.name.given', 'System'),
(38, 16, 'user.name.prefix', '0');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `security_credential`
--

DROP TABLE IF EXISTS `security_credential`;
CREATE TABLE IF NOT EXISTS `security_credential` (
  `CREDENTIAL_ID` int(11) NOT NULL,
  `PRINCIPAL_ID` int(11) NOT NULL,
  `CREDENTIAL_VALUE` varchar(254) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TYPE` smallint(6) NOT NULL,
  `UPDATE_ALLOWED` smallint(6) NOT NULL,
  `IS_STATE_READONLY` smallint(6) NOT NULL,
  `UPDATE_REQUIRED` smallint(6) NOT NULL,
  `IS_ENCODED` smallint(6) NOT NULL,
  `IS_ENABLED` smallint(6) NOT NULL,
  `AUTH_FAILURES` smallint(6) NOT NULL,
  `IS_EXPIRED` smallint(6) NOT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `MODIFIED_DATE` datetime NOT NULL,
  `PREV_AUTH_DATE` datetime DEFAULT NULL,
  `LAST_AUTH_DATE` datetime DEFAULT NULL,
  `EXPIRATION_DATE` date DEFAULT NULL,
  PRIMARY KEY (`CREDENTIAL_ID`),
  KEY `IX_SECURITY_CREDENTIAL_1` (`PRINCIPAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `security_credential`
--

INSERT INTO `security_credential` (`CREDENTIAL_ID`, `PRINCIPAL_ID`, `CREDENTIAL_VALUE`, `TYPE`, `UPDATE_ALLOWED`, `IS_STATE_READONLY`, `UPDATE_REQUIRED`, `IS_ENCODED`, `IS_ENABLED`, `AUTH_FAILURES`, `IS_EXPIRED`, `CREATION_DATE`, `MODIFIED_DATE`, `PREV_AUTH_DATE`, `LAST_AUTH_DATE`, `EXPIRATION_DATE`) VALUES
(3, 16, 'liiHgKcA1sEBisdWUN9fLEc2gBo=', 0, 1, 0, 0, 1, 1, 0, 0, '2014-12-10 14:50:41', '2015-01-21 11:16:21', '2015-01-21 11:06:08', '2015-01-21 11:16:21', NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `security_domain`
--

DROP TABLE IF EXISTS `security_domain`;
CREATE TABLE IF NOT EXISTS `security_domain` (
  `DOMAIN_ID` int(11) NOT NULL,
  `DOMAIN_NAME` varchar(254) COLLATE utf8_unicode_ci DEFAULT NULL,
  `REMOTE` smallint(6) DEFAULT '0',
  `ENABLED` smallint(6) DEFAULT '1',
  `OWNER_DOMAIN_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`DOMAIN_ID`),
  UNIQUE KEY `UIX_DOMAIN_NAME` (`DOMAIN_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `security_domain`
--

INSERT INTO `security_domain` (`DOMAIN_ID`, `DOMAIN_NAME`, `REMOTE`, `ENABLED`, `OWNER_DOMAIN_ID`) VALUES
(1, '[default]', 0, 1, NULL),
(2, '[system]', 0, 1, NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `security_permission`
--

DROP TABLE IF EXISTS `security_permission`;
CREATE TABLE IF NOT EXISTS `security_permission` (
  `PERMISSION_ID` int(11) NOT NULL,
  `PERMISSION_TYPE` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(254) COLLATE utf8_unicode_ci NOT NULL,
  `ACTIONS` varchar(254) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`PERMISSION_ID`),
  UNIQUE KEY `UIX_SECURITY_PERMISSION` (`PERMISSION_TYPE`,`NAME`,`ACTIONS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `security_permission`
--

INSERT INTO `security_permission` (`PERMISSION_ID`, `PERMISSION_TYPE`, `NAME`, `ACTIONS`) VALUES
(1, 'folder', '/', 'view'),
(11, 'folder', '/__subsite-root', 'view'),
(13, 'folder', '/__subsite-root/_role/subsite', 'view, edit'),
(14, 'folder', '/__subsite-root/_role/subsite/-', 'view, edit'),
(15, 'folder', '/__subsite-root/_role/subsite2', 'view, edit'),
(16, 'folder', '/__subsite-root/_role/subsite2/-', 'view, edit'),
(12, 'folder', '/__subsite-root/-', 'view'),
(9, 'folder', '/_user/user', 'view, edit'),
(10, 'folder', '/_user/user/-', 'view, edit'),
(2, 'folder', '/*', 'view'),
(3, 'folder', '/anotherdir/-', 'view'),
(4, 'folder', '/non-java/-', 'view'),
(5, 'folder', '/Public', 'view, edit'),
(6, 'folder', '/Public/-', 'view, edit'),
(7, 'folder', '/third-party/-', 'view'),
(8, 'folder', '/top-links/-', 'view'),
(18, 'folder', '<<ALL FILES>>', 'view'),
(17, 'folder', '<<ALL FILES>>', 'view, edit'),
(19, 'page', '/default-page.psml', 'view'),
(20, 'page', '/rss.psml', 'view'),
(41, 'portlet', 'ingrid-portal-apps::*', 'view, edit'),
(22, 'portlet', 'ingrid-portal-mdek::*', 'view, edit'),
(23, 'portlet', 'j2-admin::*', 'view, edit'),
(24, 'portlet', 'jetspeed-layouts::*', 'view, edit');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `security_principal`
--

DROP TABLE IF EXISTS `security_principal`;
CREATE TABLE IF NOT EXISTS `security_principal` (
  `PRINCIPAL_ID` int(11) NOT NULL,
  `PRINCIPAL_TYPE` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `PRINCIPAL_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `IS_MAPPED` smallint(6) NOT NULL,
  `IS_ENABLED` smallint(6) NOT NULL,
  `IS_READONLY` smallint(6) NOT NULL,
  `IS_REMOVABLE` smallint(6) NOT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `MODIFIED_DATE` datetime NOT NULL,
  `DOMAIN_ID` int(11) NOT NULL,
  PRIMARY KEY (`PRINCIPAL_ID`),
  UNIQUE KEY `UIX_SECURITY_PRINCIPAL` (`PRINCIPAL_TYPE`,`PRINCIPAL_NAME`,`DOMAIN_ID`),
  KEY `IX_SECURITY_DOMAIN_1` (`DOMAIN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `security_principal`
--

INSERT INTO `security_principal` (`PRINCIPAL_ID`, `PRINCIPAL_TYPE`, `PRINCIPAL_NAME`, `IS_MAPPED`, `IS_ENABLED`, `IS_READONLY`, `IS_REMOVABLE`, `CREATION_DATE`, `MODIFIED_DATE`, `DOMAIN_ID`) VALUES
(1, 'group', 'accounting', 1, 1, 0, 1, '2014-12-10 14:50:39', '2014-12-10 14:50:39', 1),
(2, 'group', 'engineering', 1, 1, 0, 1, '2014-12-10 14:50:39', '2014-12-10 14:50:39', 1),
(3, 'group', 'marketing', 1, 1, 0, 1, '2014-12-10 14:50:39', '2014-12-10 14:50:39', 1),
(4, 'role', 'admin', 1, 1, 0, 1, '2014-12-10 14:50:39', '2014-12-10 14:50:39', 1),
(5, 'role', 'admin-partner', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1),
(6, 'role', 'admin-portal', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1),
(7, 'role', 'admin-provider', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1),
(8, 'role', 'guest', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1),
(9, 'role', 'manager', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1),
(10, 'role', 'mdek', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1),
(11, 'role', 'subsite', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1),
(12, 'role', 'subsite2', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1),
(13, 'role', 'user', 1, 1, 0, 1, '2014-12-10 14:50:40', '2014-12-10 14:50:40', 1),
(16, 'user', 'admin', 1, 1, 0, 1, '2014-12-10 14:50:41', '2015-01-21 10:50:26', 1),
(20, 'user', 'guest', 1, 1, 0, 1, '2014-12-10 14:50:41', '2014-12-10 14:50:44', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `security_principal_assoc`
--

DROP TABLE IF EXISTS `security_principal_assoc`;
CREATE TABLE IF NOT EXISTS `security_principal_assoc` (
  `ASSOC_NAME` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `FROM_PRINCIPAL_ID` int(11) NOT NULL,
  `TO_PRINCIPAL_ID` int(11) NOT NULL,
  PRIMARY KEY (`ASSOC_NAME`,`FROM_PRINCIPAL_ID`,`TO_PRINCIPAL_ID`),
  KEY `IX_TO_PRINCIPAL_ASSOC_LOOKUP` (`ASSOC_NAME`,`TO_PRINCIPAL_ID`),
  KEY `IX_FROM_PRINCIPAL_ASSOC` (`FROM_PRINCIPAL_ID`),
  KEY `IX_TO_PRINCIPAL_ASSOC` (`TO_PRINCIPAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `security_principal_assoc`
--

INSERT INTO `security_principal_assoc` (`ASSOC_NAME`, `FROM_PRINCIPAL_ID`, `TO_PRINCIPAL_ID`) VALUES
('isMemberOf', 16, 4),
('isMemberOf', 16, 9),
('isMemberOf', 16, 13),
('isMemberOf', 20, 8);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `security_role`
--

DROP TABLE IF EXISTS `security_role`;
CREATE TABLE IF NOT EXISTS `security_role` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `security_role`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `security_role_reference`
--

DROP TABLE IF EXISTS `security_role_reference`;
CREATE TABLE IF NOT EXISTS `security_role_reference` (
  `ID` int(11) NOT NULL,
  `PORTLET_DEFINITION_ID` int(11) NOT NULL,
  `ROLE_NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `ROLE_LINK` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `security_role_reference`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `sso_site`
--

DROP TABLE IF EXISTS `sso_site`;
CREATE TABLE IF NOT EXISTS `sso_site` (
  `SITE_ID` int(11) NOT NULL,
  `NAME` varchar(254) COLLATE utf8_unicode_ci NOT NULL,
  `URL` varchar(254) COLLATE utf8_unicode_ci NOT NULL,
  `ALLOW_USER_SET` smallint(6) DEFAULT '0',
  `REQUIRES_CERTIFICATE` smallint(6) DEFAULT '0',
  `CHALLENGE_RESPONSE_AUTH` smallint(6) DEFAULT '0',
  `FORM_AUTH` smallint(6) DEFAULT '0',
  `FORM_USER_FIELD` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `FORM_PWD_FIELD` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `REALM` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DOMAIN_ID` int(11) NOT NULL,
  PRIMARY KEY (`SITE_ID`),
  UNIQUE KEY `UIX_SITE_NAME` (`NAME`),
  UNIQUE KEY `UIX_SITE_URL` (`URL`),
  KEY `IX_SECURITY_DOMAIN_2` (`DOMAIN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `sso_site`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user_activity`
--

DROP TABLE IF EXISTS `user_activity`;
CREATE TABLE IF NOT EXISTS `user_activity` (
  `ACTIVITY` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `CATEGORY` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_NAME` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TIME_STAMP` datetime DEFAULT NULL,
  `IPADDRESS` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ATTR_NAME` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ATTR_VALUE_BEFORE` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ATTR_VALUE_AFTER` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DESCRIPTION` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `user_activity`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user_attribute`
--

DROP TABLE IF EXISTS `user_attribute`;
CREATE TABLE IF NOT EXISTS `user_attribute` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `user_attribute`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user_attribute_ref`
--

DROP TABLE IF EXISTS `user_attribute_ref`;
CREATE TABLE IF NOT EXISTS `user_attribute_ref` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  `NAME_LINK` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `user_attribute_ref`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user_statistics`
--

DROP TABLE IF EXISTS `user_statistics`;
CREATE TABLE IF NOT EXISTS `user_statistics` (
  `IPADDRESS` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_NAME` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TIME_STAMP` datetime DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `ELAPSED_TIME` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Daten für Tabelle `user_statistics`
--


--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `folder`
--
ALTER TABLE `folder`
  ADD CONSTRAINT `FK_FOLDER_1` FOREIGN KEY (`PARENT_ID`) REFERENCES `folder` (`FOLDER_ID`) ON DELETE CASCADE;
