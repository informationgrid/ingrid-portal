-- MySQL dump 10.11
--
-- Host: localhost    Database: ingrid-portal
-- ------------------------------------------------------
-- Server version	5.0.33
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO,MYSQL40' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `capability`
--

DROP TABLE IF EXISTS `capability`;
CREATE TABLE `capability` (
  `CAPABILITY_ID` mediumint(9) NOT NULL,
  `CAPABILITY` varchar(80) NOT NULL,
  PRIMARY KEY  (`CAPABILITY_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `capability`
--

LOCK TABLES `capability` WRITE;
/*!40000 ALTER TABLE `capability` DISABLE KEYS */;
INSERT INTO `capability` VALUES (1,'HTML_3_2'),(2,'HTML_4_0'),(3,'HTML_ACTIVEX'),(4,'HTML_CSS1'),(5,'HTML_CSS2'),(6,'HTML_CSSP'),(7,'HTML_DOM'),(8,'HTML_DOM_1'),(9,'HTML_DOM_2'),(10,'HTML_DOM_IE'),(11,'HTML_DOM_NS4'),(12,'HTML_FORM'),(13,'HTML_FRAME'),(14,'HTML_IFRAME'),(15,'HTML_IMAGE'),(16,'HTML_JAVA'),(17,'HTML_JAVA1_0'),(18,'HTML_JAVA1_1'),(19,'HTML_JAVA1_2'),(20,'HTML_JAVASCRIPT'),(21,'HTML_JAVASCRIPT_1_0'),(22,'HTML_JAVASCRIPT_1_1'),(23,'HTML_JAVASCRIPT_1_2'),(24,'HTML_JAVA_JRE'),(25,'HTML_JSCRIPT'),(26,'HTML_JSCRIPT1_0'),(27,'HTML_JSCRIPT1_1'),(28,'HTML_JSCRIPT1_2'),(29,'HTML_LAYER'),(30,'HTML_NESTED_TABLE'),(31,'HTML_PLUGIN'),(32,'HTML_TABLE'),(33,'HTML_XML'),(34,'HTML_XSL'),(35,'HTTP_1_1'),(36,'HTTP_COOKIE'),(37,'WML_1_0'),(38,'WML_1_1'),(39,'WML_TABLE'),(40,'XML_XINCLUDE'),(41,'XML_XPATH'),(42,'XML_XSLT');
/*!40000 ALTER TABLE `capability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
CREATE TABLE `client` (
  `CLIENT_ID` mediumint(9) NOT NULL,
  `EVAL_ORDER` mediumint(9) NOT NULL,
  `NAME` varchar(80) NOT NULL,
  `USER_AGENT_PATTERN` varchar(128) default NULL,
  `MANUFACTURER` varchar(80) default NULL,
  `MODEL` varchar(80) default NULL,
  `VERSION` varchar(40) default NULL,
  `PREFERRED_MIMETYPE_ID` mediumint(9) NOT NULL,
  PRIMARY KEY  (`CLIENT_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES (1,1,'ie5mac','.*MSIE 5.*Mac.*','Microsoft','None','5.*',2),(2,2,'safari','.*Mac.*Safari.*','Apple','None','5.*',2),(3,3,'ie6','.*MSIE 6.*','Microsoft','None','6.0',2),(4,4,'ie5','.*MSIE 5.*','Microsoft','None','5.5',2),(5,5,'ns4','.*Mozilla/4.*','Netscape','None','4.75',2),(6,6,'mozilla','.*Mozilla/5.*','Mozilla','Mozilla','1.x',2),(7,7,'lynx','Lynx.*','GNU','None','',2),(8,8,'nokia_generic','Nokia.*','Nokia','Generic','',3),(9,9,'xhtml-basic','DoCoMo/2.0.*|KDDI-.*UP.Browser.*|J-PHONE/5.0.*|Vodafone/1.0/.*','WAP','Generic','',1),(10,10,'up','UP.*|.*UP.Browser.*','United Planet','Generic','',3),(11,11,'sonyericsson','Ercis.*|SonyE.*','SonyEricsson','Generic','',3),(12,12,'wapalizer','Wapalizer.*','Wapalizer','Generic','',3),(13,13,'klondike','Klondike.*','Klondike','Generic','',3),(14,14,'wml_generic','.*WML.*|.*WAP.*|.*Wap.*|.*wml.*','Generic','Generic','',3),(15,15,'vxml_generic','.*VoiceXML.*','Generic','Generic','',4),(16,16,'nuance','Nuance.*','Nuance','Generic','',4),(17,17,'agentxml','agentxml/1.0.*','Unknown','Generic','',6),(18,18,'opera7','.*Opera/7.*','Opera','Opera7','7.x',2);
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_to_capability`
--

DROP TABLE IF EXISTS `client_to_capability`;
CREATE TABLE `client_to_capability` (
  `CLIENT_ID` mediumint(9) NOT NULL,
  `CAPABILITY_ID` mediumint(9) NOT NULL
) ENGINE=MyISAM;

--
-- Dumping data for table `client_to_capability`
--

LOCK TABLES `client_to_capability` WRITE;
/*!40000 ALTER TABLE `client_to_capability` DISABLE KEYS */;
INSERT INTO `client_to_capability` VALUES (1,1),(1,16),(1,20),(1,32),(1,12),(1,13),(1,15),(1,31),(1,4),(1,11),(1,36),(2,1),(2,16),(2,20),(2,32),(2,30),(2,12),(2,13),(2,15),(2,3),(2,4),(2,5),(2,6),(2,14),(2,10),(2,36),(3,1),(3,16),(3,20),(3,32),(3,30),(3,12),(3,13),(3,15),(3,3),(3,4),(3,5),(3,6),(3,14),(3,10),(3,36),(4,1),(4,16),(4,20),(4,32),(4,30),(4,12),(4,13),(4,15),(4,3),(4,4),(4,5),(4,6),(4,14),(4,10),(4,36),(5,1),(5,16),(5,20),(5,32),(5,12),(5,13),(5,15),(5,4),(5,29),(5,31),(5,11),(5,36),(6,1),(6,2),(6,16),(6,24),(6,20),(6,32),(6,30),(6,12),(6,13),(6,14),(6,15),(6,4),(6,5),(6,6),(6,8),(6,31),(6,36),(7,32),(7,30),(7,12),(7,13),(7,36),(18,1),(18,2),(18,32),(18,16),(18,24),(18,20),(18,30),(18,12),(18,13),(18,15),(18,14),(18,4),(18,5),(18,6),(18,8),(18,36),(18,31);
/*!40000 ALTER TABLE `client_to_capability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_to_mimetype`
--

DROP TABLE IF EXISTS `client_to_mimetype`;
CREATE TABLE `client_to_mimetype` (
  `CLIENT_ID` mediumint(9) NOT NULL,
  `MIMETYPE_ID` mediumint(9) NOT NULL
) ENGINE=MyISAM;

--
-- Dumping data for table `client_to_mimetype`
--

LOCK TABLES `client_to_mimetype` WRITE;
/*!40000 ALTER TABLE `client_to_mimetype` DISABLE KEYS */;
INSERT INTO `client_to_mimetype` VALUES (1,2),(2,2),(2,6),(2,5),(3,2),(3,6),(3,5),(4,2),(4,6),(5,2),(6,2),(6,5),(6,6),(7,2),(8,3),(9,1),(10,3),(11,3),(12,3),(13,3),(14,3),(15,4),(16,4),(17,6),(18,2),(18,6),(18,5);
/*!40000 ALTER TABLE `client_to_mimetype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clubs`
--

DROP TABLE IF EXISTS `clubs`;
CREATE TABLE `clubs` (
  `NAME` varchar(80) NOT NULL,
  `COUNTRY` varchar(40) NOT NULL,
  `CITY` varchar(40) NOT NULL,
  `STADIUM` varchar(80) NOT NULL,
  `CAPACITY` mediumint(9) default NULL,
  `FOUNDED` mediumint(9) default NULL,
  `PITCH` varchar(40) default NULL,
  `NICKNAME` varchar(40) default NULL,
  PRIMARY KEY  (`NAME`)
) ENGINE=MyISAM;

--
-- Dumping data for table `clubs`
--

LOCK TABLES `clubs` WRITE;
/*!40000 ALTER TABLE `clubs` DISABLE KEYS */;
/*!40000 ALTER TABLE `clubs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_portlet_mode`
--

DROP TABLE IF EXISTS `custom_portlet_mode`;
CREATE TABLE `custom_portlet_mode` (
  `ID` mediumint(9) NOT NULL,
  `APPLICATION_ID` mediumint(9) NOT NULL,
  `CUSTOM_NAME` varchar(150) NOT NULL,
  `MAPPED_NAME` varchar(150) default NULL,
  `DESCRIPTION` mediumtext,
  PRIMARY KEY  (`ID`),
  KEY `APPLICATION_ID` (`APPLICATION_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `custom_portlet_mode`
--

LOCK TABLES `custom_portlet_mode` WRITE;
/*!40000 ALTER TABLE `custom_portlet_mode` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_portlet_mode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_window_state`
--

DROP TABLE IF EXISTS `custom_window_state`;
CREATE TABLE `custom_window_state` (
  `ID` mediumint(9) NOT NULL,
  `APPLICATION_ID` mediumint(9) NOT NULL,
  `CUSTOM_NAME` varchar(150) NOT NULL,
  `MAPPED_NAME` varchar(150) default NULL,
  `DESCRIPTION` mediumtext,
  PRIMARY KEY  (`ID`),
  KEY `APPLICATION_ID` (`APPLICATION_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `custom_window_state`
--

LOCK TABLES `custom_window_state` WRITE;
/*!40000 ALTER TABLE `custom_window_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_window_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `folder`
--

DROP TABLE IF EXISTS `folder`;
CREATE TABLE `folder` (
  `FOLDER_ID` mediumint(9) NOT NULL,
  `PARENT_ID` mediumint(9) default NULL,
  `PATH` varchar(240) NOT NULL,
  `NAME` varchar(80) NOT NULL,
  `TITLE` varchar(100) default NULL,
  `SHORT_TITLE` varchar(40) default NULL,
  `IS_HIDDEN` int(11) NOT NULL,
  `SKIN` varchar(80) default NULL,
  `DEFAULT_LAYOUT_DECORATOR` varchar(80) default NULL,
  `DEFAULT_PORTLET_DECORATOR` varchar(80) default NULL,
  `DEFAULT_PAGE_NAME` varchar(80) default NULL,
  `SUBSITE` varchar(40) default NULL,
  `USER_PRINCIPAL` varchar(40) default NULL,
  `ROLE_PRINCIPAL` varchar(40) default NULL,
  `GROUP_PRINCIPAL` varchar(40) default NULL,
  `MEDIATYPE` varchar(15) default NULL,
  `LOCALE` varchar(20) default NULL,
  `EXT_ATTR_NAME` varchar(15) default NULL,
  `EXT_ATTR_VALUE` varchar(40) default NULL,
  `OWNER_PRINCIPAL` varchar(40) default NULL,
  PRIMARY KEY  (`FOLDER_ID`),
  UNIQUE KEY `PATH` (`PATH`),
  KEY `IX_FOLDER_1` (`PARENT_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `folder`
--

LOCK TABLES `folder` WRITE;
/*!40000 ALTER TABLE `folder` DISABLE KEYS */;
INSERT INTO `folder` VALUES (1,NULL,'/','/','Root Folder','Root Folder',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,1,'/_role','_role',' Role',' Role',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,2,'/_role/user','user','User','User',0,NULL,NULL,NULL,NULL,NULL,NULL,'user',NULL,NULL,NULL,NULL,NULL,NULL),(4,1,'/_user','_user',' User',' User',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,4,'/_user/template','template','Template','Template',0,NULL,NULL,NULL,NULL,NULL,'template',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,1,'/administration','administration','ingrid.page.administration','ingrid.page.administration',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(7,1,'/search-extended','search-extended','Search Extended','Search Extended',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `folder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `folder_constraint`
--

DROP TABLE IF EXISTS `folder_constraint`;
CREATE TABLE `folder_constraint` (
  `CONSTRAINT_ID` mediumint(9) NOT NULL,
  `FOLDER_ID` mediumint(9) NOT NULL,
  `APPLY_ORDER` mediumint(9) NOT NULL,
  `USER_PRINCIPALS_ACL` varchar(120) default NULL,
  `ROLE_PRINCIPALS_ACL` varchar(120) default NULL,
  `GROUP_PRINCIPALS_ACL` varchar(120) default NULL,
  `PERMISSIONS_ACL` varchar(120) default NULL,
  PRIMARY KEY  (`CONSTRAINT_ID`),
  KEY `IX_FOLDER_CONSTRAINT_1` (`FOLDER_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `folder_constraint`
--

LOCK TABLES `folder_constraint` WRITE;
/*!40000 ALTER TABLE `folder_constraint` DISABLE KEYS */;
/*!40000 ALTER TABLE `folder_constraint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `folder_constraints_ref`
--

DROP TABLE IF EXISTS `folder_constraints_ref`;
CREATE TABLE `folder_constraints_ref` (
  `CONSTRAINTS_REF_ID` mediumint(9) NOT NULL,
  `FOLDER_ID` mediumint(9) NOT NULL,
  `APPLY_ORDER` mediumint(9) NOT NULL,
  `NAME` varchar(40) NOT NULL,
  PRIMARY KEY  (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `FOLDER_ID` (`FOLDER_ID`,`NAME`),
  KEY `IX_FOLDER_CONSTRAINTS_REF_1` (`FOLDER_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `folder_constraints_ref`
--

LOCK TABLES `folder_constraints_ref` WRITE;
/*!40000 ALTER TABLE `folder_constraints_ref` DISABLE KEYS */;
INSERT INTO `folder_constraints_ref` VALUES (1,1,0,'public-view'),(2,6,0,'admin'),(3,6,1,'admin-portal'),(4,6,2,'admin-partner'),(5,6,3,'admin-provider');
/*!40000 ALTER TABLE `folder_constraints_ref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `folder_menu`
--

DROP TABLE IF EXISTS `folder_menu`;
CREATE TABLE `folder_menu` (
  `MENU_ID` mediumint(9) NOT NULL,
  `CLASS_NAME` varchar(100) NOT NULL,
  `PARENT_ID` mediumint(9) default NULL,
  `FOLDER_ID` mediumint(9) default NULL,
  `ELEMENT_ORDER` mediumint(9) default NULL,
  `NAME` varchar(100) default NULL,
  `TITLE` varchar(100) default NULL,
  `SHORT_TITLE` varchar(40) default NULL,
  `TEXT` varchar(100) default NULL,
  `OPTIONS` varchar(255) default NULL,
  `DEPTH` mediumint(9) default NULL,
  `IS_PATHS` int(11) default NULL,
  `IS_REGEXP` int(11) default NULL,
  `PROFILE` varchar(80) default NULL,
  `OPTIONS_ORDER` varchar(255) default NULL,
  `SKIN` varchar(80) default NULL,
  `IS_NEST` int(11) default NULL,
  PRIMARY KEY  (`MENU_ID`),
  KEY `IX_FOLDER_MENU_1` (`PARENT_ID`),
  KEY `UN_FOLDER_MENU_1` (`FOLDER_ID`,`NAME`)
) ENGINE=MyISAM;

--
-- Dumping data for table `folder_menu`
--

LOCK TABLES `folder_menu` WRITE;
/*!40000 ALTER TABLE `folder_menu` DISABLE KEYS */;
INSERT INTO `folder_menu` VALUES (1,'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl',NULL,1,0,'service-menu',NULL,NULL,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL),(2,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',1,NULL,0,NULL,NULL,NULL,NULL,'/default-page.psml',0,0,0,NULL,NULL,NULL,NULL),(3,'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl',1,NULL,1,NULL,NULL,NULL,'separator1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',1,NULL,2,NULL,NULL,NULL,NULL,'/service-myportal.psml',0,0,0,NULL,NULL,NULL,NULL),(5,'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl',1,NULL,3,NULL,NULL,NULL,'separator2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',1,NULL,4,NULL,NULL,NULL,NULL,'/service-sitemap.psml',0,0,0,NULL,NULL,NULL,NULL),(7,'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl',1,NULL,5,NULL,NULL,NULL,'separator2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(8,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',1,NULL,6,NULL,NULL,NULL,NULL,'/help.psml',0,0,0,NULL,NULL,NULL,NULL),(9,'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl',1,NULL,7,NULL,NULL,NULL,'separator2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(10,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',1,NULL,8,NULL,NULL,NULL,NULL,'/service-contact.psml',0,0,0,NULL,NULL,NULL,NULL),(11,'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl',1,NULL,9,NULL,NULL,NULL,'separator1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(12,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',1,NULL,10,NULL,NULL,NULL,NULL,'/language.link',0,0,0,NULL,NULL,NULL,NULL),(13,'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl',NULL,1,0,'footer-menu',NULL,NULL,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL),(14,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',13,NULL,0,NULL,NULL,NULL,NULL,'/disclaimer.psml',0,0,0,NULL,NULL,NULL,NULL),(15,'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl',13,NULL,1,NULL,NULL,NULL,'separator1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(16,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',13,NULL,2,NULL,NULL,NULL,NULL,'/privacy.psml',0,0,0,NULL,NULL,NULL,NULL),(17,'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl',13,NULL,3,NULL,NULL,NULL,'separator1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(18,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',13,NULL,4,NULL,NULL,NULL,NULL,'/webmaster.link',0,0,0,NULL,NULL,NULL,NULL),(19,'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl',NULL,1,0,'main-menu',NULL,NULL,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL),(20,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',19,NULL,0,NULL,NULL,NULL,NULL,'/main-search.psml',0,0,0,NULL,NULL,NULL,NULL),(21,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',19,NULL,1,NULL,NULL,NULL,NULL,'/main-service.psml',0,0,0,NULL,NULL,NULL,NULL),(22,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',19,NULL,2,NULL,NULL,NULL,NULL,'/main-measures.psml',0,0,0,NULL,NULL,NULL,NULL),(23,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',19,NULL,3,NULL,NULL,NULL,NULL,'/main-environment.psml',0,0,0,NULL,NULL,NULL,NULL),(24,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',19,NULL,4,NULL,NULL,NULL,NULL,'/main-maps.psml',0,0,0,NULL,NULL,NULL,NULL),(25,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',19,NULL,5,NULL,NULL,NULL,NULL,'/main-chronicle.psml',0,0,0,NULL,NULL,NULL,NULL),(26,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',19,NULL,6,NULL,NULL,NULL,NULL,'/main-about.psml',0,0,0,NULL,NULL,NULL,NULL),(27,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',19,NULL,7,NULL,NULL,NULL,NULL,'/administration',0,0,0,NULL,NULL,NULL,NULL),(28,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',19,NULL,8,NULL,NULL,NULL,NULL,'/Administrative',0,0,0,NULL,NULL,NULL,NULL),(29,'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl',NULL,6,0,'sub-menu',NULL,NULL,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL),(30,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,0,NULL,NULL,NULL,NULL,'/administration/admin-usermanagement.psml',0,0,0,NULL,NULL,NULL,NULL),(31,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,1,NULL,NULL,NULL,NULL,'/administration/admin-homepage.psml',0,0,0,NULL,NULL,NULL,NULL),(32,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,2,NULL,NULL,NULL,NULL,'/administration/admin-content-rss.psml',0,0,0,NULL,NULL,NULL,NULL),(33,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,3,NULL,NULL,NULL,NULL,'/administration/admin-content-partner.psml',0,0,0,NULL,NULL,NULL,NULL),(34,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,4,NULL,NULL,NULL,NULL,'/administration/admin-content-provider.psml',0,0,0,NULL,NULL,NULL,NULL),(35,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,5,NULL,NULL,NULL,NULL,'/administration/admin-iplugs.psml',0,0,0,NULL,NULL,NULL,NULL),(36,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,6,NULL,NULL,NULL,NULL,'/administration/admin-cms.psml',0,0,0,NULL,NULL,NULL,NULL),(37,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,7,NULL,NULL,NULL,NULL,'/administration/admin-wms.psml',0,0,0,NULL,NULL,NULL,NULL),(38,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,8,NULL,NULL,NULL,NULL,'/administration/admin-statistics.psml',0,0,0,NULL,NULL,NULL,NULL),(39,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,9,NULL,NULL,NULL,NULL,'/administration/admin-portal-profile.psml',0,0,0,NULL,NULL,NULL,NULL),(40,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,10,NULL,NULL,NULL,NULL,'/administration/admin-component-monitor.psml',0,0,0,NULL,NULL,NULL,NULL),(41,'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl',29,NULL,11,NULL,NULL,NULL,NULL,'/administration/dwr-test.psml',0,0,0,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `folder_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `folder_menu_metadata`
--

DROP TABLE IF EXISTS `folder_menu_metadata`;
CREATE TABLE `folder_menu_metadata` (
  `METADATA_ID` mediumint(9) NOT NULL,
  `MENU_ID` mediumint(9) NOT NULL,
  `NAME` varchar(15) NOT NULL,
  `LOCALE` varchar(20) NOT NULL,
  `VALUE` varchar(100) NOT NULL,
  PRIMARY KEY  (`METADATA_ID`),
  UNIQUE KEY `MENU_ID` (`MENU_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_FOLDER_MENU_METADATA_1` (`MENU_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `folder_menu_metadata`
--

LOCK TABLES `folder_menu_metadata` WRITE;
/*!40000 ALTER TABLE `folder_menu_metadata` DISABLE KEYS */;
/*!40000 ALTER TABLE `folder_menu_metadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `folder_metadata`
--

DROP TABLE IF EXISTS `folder_metadata`;
CREATE TABLE `folder_metadata` (
  `METADATA_ID` mediumint(9) NOT NULL,
  `FOLDER_ID` mediumint(9) NOT NULL,
  `NAME` varchar(15) NOT NULL,
  `LOCALE` varchar(20) NOT NULL,
  `VALUE` varchar(100) NOT NULL,
  PRIMARY KEY  (`METADATA_ID`),
  UNIQUE KEY `FOLDER_ID` (`FOLDER_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_FOLDER_METADATA_1` (`FOLDER_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `folder_metadata`
--

LOCK TABLES `folder_metadata` WRITE;
/*!40000 ALTER TABLE `folder_metadata` DISABLE KEYS */;
INSERT INTO `folder_metadata` VALUES (1,1,'title','fr,,','Répertoire racine'),(2,1,'title','es,,','Carpeta raiz');
/*!40000 ALTER TABLE `folder_metadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `folder_order`
--

DROP TABLE IF EXISTS `folder_order`;
CREATE TABLE `folder_order` (
  `ORDER_ID` mediumint(9) NOT NULL,
  `FOLDER_ID` mediumint(9) NOT NULL,
  `SORT_ORDER` mediumint(9) NOT NULL,
  `NAME` varchar(80) NOT NULL,
  PRIMARY KEY  (`ORDER_ID`),
  UNIQUE KEY `FOLDER_ID` (`FOLDER_ID`,`NAME`),
  KEY `IX_FOLDER_ORDER_1` (`FOLDER_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `folder_order`
--

LOCK TABLES `folder_order` WRITE;
/*!40000 ALTER TABLE `folder_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `folder_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fragment`
--

DROP TABLE IF EXISTS `fragment`;
CREATE TABLE `fragment` (
  `FRAGMENT_ID` mediumint(9) NOT NULL,
  `PARENT_ID` mediumint(9) default NULL,
  `PAGE_ID` mediumint(9) default NULL,
  `NAME` varchar(100) default NULL,
  `TITLE` varchar(100) default NULL,
  `SHORT_TITLE` varchar(40) default NULL,
  `TYPE` varchar(40) default NULL,
  `SKIN` varchar(80) default NULL,
  `DECORATOR` varchar(80) default NULL,
  `STATE` varchar(10) default NULL,
  `PMODE` varchar(10) default NULL,
  `LAYOUT_ROW` mediumint(9) default NULL,
  `LAYOUT_COLUMN` mediumint(9) default NULL,
  `LAYOUT_SIZES` varchar(20) default NULL,
  `LAYOUT_X` double default NULL,
  `LAYOUT_Y` double default NULL,
  `LAYOUT_Z` double default NULL,
  `LAYOUT_WIDTH` double default NULL,
  `LAYOUT_HEIGHT` double default NULL,
  `EXT_PROP_NAME_1` varchar(40) default NULL,
  `EXT_PROP_VALUE_1` varchar(80) default NULL,
  `EXT_PROP_NAME_2` varchar(40) default NULL,
  `EXT_PROP_VALUE_2` varchar(80) default NULL,
  `OWNER_PRINCIPAL` varchar(40) default NULL,
  PRIMARY KEY  (`FRAGMENT_ID`),
  KEY `IX_FRAGMENT_1` (`PARENT_ID`),
  KEY `UN_FRAGMENT_1` (`PAGE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `fragment`
--

LOCK TABLES `fragment` WRITE;
/*!40000 ALTER TABLE `fragment` DISABLE KEYS */;
INSERT INTO `fragment` VALUES (1,NULL,1,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(2,1,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(3,1,NULL,'ingrid-portal-apps::EnvironmentTeaser',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(4,1,NULL,'ingrid-portal-apps::RssNewsTeaser',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,2,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(5,1,NULL,'ingrid-portal-apps::IngridInformPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(6,1,NULL,'ingrid-portal-apps::ServiceTeaser',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,1,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(7,1,NULL,'ingrid-portal-apps::MeasuresTeaser',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,2,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(8,1,NULL,'ingrid-portal-apps::ChronicleTeaser',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,3,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(9,NULL,2,'jetspeed-layouts::IngridClearLayout',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(10,9,NULL,'ingrid-portal-apps::DetectJavaScriptPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(11,NULL,3,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(12,11,NULL,'ingrid-portal-apps::CMSPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(13,11,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(14,NULL,4,'jetspeed-layouts::IngridClearLayout',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(15,14,NULL,'ingrid-portal-apps::HelpPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(16,NULL,5,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(17,16,NULL,'ingrid-portal-apps::ShowPartnerPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(18,NULL,6,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(19,18,NULL,'ingrid-portal-apps::CMSPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(20,18,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(21,18,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,1,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(22,NULL,7,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(23,22,NULL,'ingrid-portal-apps::ChronicleSearch',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(24,22,NULL,'ingrid-portal-apps::ChronicleResult',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(25,22,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(26,NULL,8,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(27,26,NULL,'ingrid-portal-apps::EnvironmentSearch',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(28,26,NULL,'ingrid-portal-apps::EnvironmentResult',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(29,26,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(30,NULL,9,'jetspeed-layouts::IngridClearLayout',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(31,30,NULL,'ingrid-portal-apps::ShowMapsPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(32,NULL,10,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(33,32,NULL,'ingrid-portal-apps::MeasuresSearch',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(34,32,NULL,'ingrid-portal-apps::MeasuresResult',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(35,32,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(36,NULL,11,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(37,36,NULL,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,0,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(38,37,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(39,37,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(40,37,NULL,'ingrid-portal-apps::SearchSimilar',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(41,36,NULL,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(42,41,NULL,'ingrid-portal-apps::SearchResult',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,0,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(43,NULL,12,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(44,43,NULL,'ingrid-portal-apps::ServiceSearch',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(45,43,NULL,'ingrid-portal-apps::ServiceResult',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(46,43,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(47,NULL,13,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(48,47,NULL,'ingrid-portal-apps::MyPortalCreateAccountPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(49,47,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(50,NULL,14,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(51,50,NULL,'ingrid-portal-apps::MyPortalPasswordForgottenPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(52,50,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(53,NULL,15,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(54,53,NULL,'ingrid-portal-apps::CMSPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(55,NULL,16,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(56,55,NULL,'ingrid-portal-apps::RssNews',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(57,NULL,17,'jetspeed-layouts::IngridClearLayout',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(58,57,NULL,'ingrid-portal-apps::SearchDetail',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(59,NULL,18,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(60,59,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(61,59,NULL,'ingrid-portal-apps::SearchHistory',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(62,59,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(63,NULL,19,'jetspeed-layouts::IngridClearLayout',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(64,63,NULL,'ingrid-portal-apps::SearchResult',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(65,NULL,20,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(66,65,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(67,65,NULL,'ingrid-portal-apps::SearchSavePortlet',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(68,NULL,21,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(69,68,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(70,68,NULL,'ingrid-portal-apps::SearchSettings',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(71,68,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(72,NULL,22,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(73,72,NULL,'ingrid-portal-apps::ContactNewsletterPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(74,72,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(75,72,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,1,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(76,NULL,23,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(77,76,NULL,'ingrid-portal-apps::Contact',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(78,76,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(79,NULL,24,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(80,79,NULL,'ingrid-portal-apps::MyPortalLoginPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(81,79,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(82,NULL,25,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(83,82,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(84,NULL,26,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(85,84,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(86,84,NULL,'ingrid-portal-apps::EnvironmentTeaser',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(87,84,NULL,'ingrid-portal-apps::RssNewsTeaser',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,2,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(88,84,NULL,'ingrid-portal-apps::IngridInformPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(89,84,NULL,'ingrid-portal-apps::ServiceTeaser',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,1,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(90,84,NULL,'ingrid-portal-apps::MeasuresTeaser',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,2,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(91,84,NULL,'ingrid-portal-apps::ChronicleTeaser',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,3,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(92,NULL,27,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(93,92,NULL,'ingrid-portal-apps::MyPortalEditAccountPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(94,92,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(95,92,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,1,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(96,92,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,2,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(97,NULL,28,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(98,97,NULL,'ingrid-portal-apps::MyPortalPersonalizeOverviewPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(99,97,NULL,'ingrid-portal-apps::MyPortalPersonalizeHomePortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(100,97,NULL,'ingrid-portal-apps::MyPortalPersonalizePartnerPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,2,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(101,97,NULL,'ingrid-portal-apps::MyPortalPersonalizeSourcesPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,3,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(102,97,NULL,'ingrid-portal-apps::MyPortalPersonalizeSearchSettingsPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,4,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(103,97,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(104,97,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,1,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(105,NULL,29,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(106,105,NULL,'ingrid-portal-apps::MyPortalOverviewPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(107,105,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(108,NULL,30,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(109,108,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(110,108,NULL,'ingrid-portal-apps::EnvironmentTeaser',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(111,108,NULL,'ingrid-portal-apps::RssNewsTeaser',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,2,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(112,108,NULL,'ingrid-portal-apps::IngridInformPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(113,108,NULL,'ingrid-portal-apps::ServiceTeaser',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,1,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(114,108,NULL,'ingrid-portal-apps::MeasuresTeaser',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,2,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(115,108,NULL,'ingrid-portal-apps::ChronicleTeaser',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,3,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(116,NULL,31,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(117,116,NULL,'ingrid-portal-apps::AdminCMSPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(118,NULL,32,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(119,118,NULL,'ingrid-portal-apps::AdminComponentMonitorPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(120,NULL,33,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(121,120,NULL,'ingrid-portal-apps::ContentPartnerPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(122,NULL,34,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(123,122,NULL,'ingrid-portal-apps::ContentProviderPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(124,NULL,35,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(125,124,NULL,'ingrid-portal-apps::ContentRSSPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(126,NULL,36,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(127,126,NULL,'ingrid-portal-apps::AdminHomepagePortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(128,NULL,37,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(129,128,NULL,'ingrid-portal-apps::AdminIPlugPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(130,NULL,38,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(131,130,NULL,'ingrid-portal-apps::AdminPortalProfilePortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(132,NULL,39,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(133,132,NULL,'ingrid-portal-apps::AdminStatisticsPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(134,NULL,40,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(135,134,NULL,'ingrid-portal-apps::AdminUserPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(136,134,NULL,'ingrid-portal-apps::AdminUserMigrationPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(137,NULL,41,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(138,137,NULL,'ingrid-portal-apps::AdminWMSPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(139,NULL,42,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(140,139,NULL,'ingrid-portal-apps::DWRTestPortlet',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(141,NULL,43,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(142,141,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(143,141,NULL,'ingrid-portal-apps::SearchExtAdrAreaPartner',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(144,141,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(145,NULL,44,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(146,145,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(147,145,NULL,'ingrid-portal-apps::SearchExtAdrPlaceReference',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(148,145,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(149,NULL,45,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(150,149,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(151,149,NULL,'ingrid-portal-apps::SearchExtAdrTopicMode',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(152,149,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(153,NULL,46,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(154,153,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(155,153,NULL,'ingrid-portal-apps::SearchExtAdrTopicTerms',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(156,153,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(157,NULL,47,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(158,157,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(159,157,NULL,'ingrid-portal-apps::SearchExtEnvAreaContents',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(160,157,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(161,NULL,48,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(162,161,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(163,161,NULL,'ingrid-portal-apps::SearchExtEnvAreaPartner',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(164,161,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(165,NULL,49,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(166,165,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(167,165,NULL,'ingrid-portal-apps::SearchExtEnvAreaSources',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(168,165,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(169,NULL,50,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(170,169,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(171,169,NULL,'ingrid-portal-apps::SearchExtEnvPlaceGeothesaurus',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(172,169,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(173,NULL,51,'jetspeed-layouts::IngridOneColumn',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(174,173,NULL,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,0,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(175,174,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(176,173,NULL,'ingrid-portal-apps::SearchExtEnvPlaceMap',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(177,NULL,52,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(178,177,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(179,177,NULL,'ingrid-portal-apps::SearchExtEnvTimeConstraint',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(180,177,NULL,'ingrid-portal-apps::SearchExtEnvTimeChronicle',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,2,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(181,177,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(182,NULL,53,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(183,182,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(184,182,NULL,'ingrid-portal-apps::SearchExtEnvTopicTerms',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(185,182,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(186,NULL,54,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(187,186,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(188,186,NULL,'ingrid-portal-apps::SearchExtEnvTopicThesaurus',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(189,186,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(190,NULL,55,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(191,190,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(192,190,NULL,'ingrid-portal-apps::SearchExtResTopicAttributes',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(193,190,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(194,NULL,56,'jetspeed-layouts::IngridTwoColumns',NULL,NULL,'layout',NULL,NULL,NULL,NULL,-1,-1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(195,194,NULL,'ingrid-portal-apps::SearchSimple',NULL,NULL,'portlet',NULL,NULL,NULL,NULL,0,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(196,194,NULL,'ingrid-portal-apps::SearchExtResTopicTerms',NULL,NULL,'portlet',NULL,'clear',NULL,NULL,1,0,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL),(197,194,NULL,'ingrid-portal-apps::InfoPortlet',NULL,NULL,'portlet',NULL,'ingrid-marginal-teaser',NULL,NULL,0,1,NULL,-1,-1,-1,-1,-1,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `fragment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fragment_constraint`
--

DROP TABLE IF EXISTS `fragment_constraint`;
CREATE TABLE `fragment_constraint` (
  `CONSTRAINT_ID` mediumint(9) NOT NULL,
  `FRAGMENT_ID` mediumint(9) NOT NULL,
  `APPLY_ORDER` mediumint(9) NOT NULL,
  `USER_PRINCIPALS_ACL` varchar(120) default NULL,
  `ROLE_PRINCIPALS_ACL` varchar(120) default NULL,
  `GROUP_PRINCIPALS_ACL` varchar(120) default NULL,
  `PERMISSIONS_ACL` varchar(120) default NULL,
  PRIMARY KEY  (`CONSTRAINT_ID`),
  KEY `IX_FRAGMENT_CONSTRAINT_1` (`FRAGMENT_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `fragment_constraint`
--

LOCK TABLES `fragment_constraint` WRITE;
/*!40000 ALTER TABLE `fragment_constraint` DISABLE KEYS */;
/*!40000 ALTER TABLE `fragment_constraint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fragment_constraints_ref`
--

DROP TABLE IF EXISTS `fragment_constraints_ref`;
CREATE TABLE `fragment_constraints_ref` (
  `CONSTRAINTS_REF_ID` mediumint(9) NOT NULL,
  `FRAGMENT_ID` mediumint(9) NOT NULL,
  `APPLY_ORDER` mediumint(9) NOT NULL,
  `NAME` varchar(40) NOT NULL,
  PRIMARY KEY  (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `FRAGMENT_ID` (`FRAGMENT_ID`,`NAME`),
  KEY `IX_FRAGMENT_CONSTRAINTS_REF_1` (`FRAGMENT_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `fragment_constraints_ref`
--

LOCK TABLES `fragment_constraints_ref` WRITE;
/*!40000 ALTER TABLE `fragment_constraints_ref` DISABLE KEYS */;
INSERT INTO `fragment_constraints_ref` VALUES (1,136,0,'admin');
/*!40000 ALTER TABLE `fragment_constraints_ref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fragment_pref`
--

DROP TABLE IF EXISTS `fragment_pref`;
CREATE TABLE `fragment_pref` (
  `PREF_ID` mediumint(9) NOT NULL,
  `FRAGMENT_ID` mediumint(9) NOT NULL,
  `NAME` varchar(40) NOT NULL,
  `IS_READ_ONLY` int(11) NOT NULL,
  PRIMARY KEY  (`PREF_ID`),
  UNIQUE KEY `FRAGMENT_ID` (`FRAGMENT_ID`,`NAME`),
  KEY `IX_FRAGMENT_PREF_1` (`FRAGMENT_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `fragment_pref`
--

LOCK TABLES `fragment_pref` WRITE;
/*!40000 ALTER TABLE `fragment_pref` DISABLE KEYS */;
INSERT INTO `fragment_pref` VALUES (1,2,'titleKey',0),(2,2,'helpKey',0),(3,3,'titleKey',0),(4,3,'helpKey',0),(5,4,'noOfEntriesDisplayed',0),(6,4,'titleKey',0),(7,4,'helpKey',0),(8,5,'infoTemplate',0),(9,5,'cmsKey',0),(10,5,'helpKey',0),(11,6,'titleKey',0),(12,6,'helpKey',0),(13,7,'titleKey',0),(14,7,'helpKey',0),(15,8,'titleKey',0),(16,8,'helpKey',0),(17,12,'infoTemplate',0),(18,12,'titleKey',0),(19,12,'cmsKey',0),(20,13,'infoTemplate',0),(21,13,'titleKey',0),(22,19,'infoTemplate',0),(23,19,'cmsKey',0),(24,19,'helpKey',0),(25,20,'infoTemplate',0),(26,20,'titleKey',0),(27,21,'infoTemplate',0),(28,21,'titleKey',0),(29,23,'helpKey',0),(30,25,'infoTemplate',0),(31,25,'titleKey',0),(32,27,'helpKey',0),(33,29,'infoTemplate',0),(34,29,'titleKey',0),(35,29,'infoLink',0),(36,31,'helpKey',0),(37,33,'helpKey',0),(38,35,'infoTemplate',0),(39,35,'titleKey',0),(40,38,'titleKey',0),(41,38,'helpKey',0),(42,39,'infoTemplate',0),(43,39,'titleKey',0),(44,44,'helpKey',0),(45,46,'infoTemplate',0),(46,46,'titleKey',0),(47,46,'infoLink',0),(48,49,'infoTemplate',0),(49,49,'titleKey',0),(50,52,'infoTemplate',0),(51,52,'titleKey',0),(52,54,'infoTemplate',0),(53,54,'cmsKey',0),(54,56,'startWithEntry',0),(55,60,'titleKey',0),(56,60,'helpKey',0),(57,62,'infoTemplate',0),(58,62,'titleKey',0),(59,66,'titleKey',0),(60,66,'helpKey',0),(61,69,'titleKey',0),(62,69,'helpKey',0),(63,71,'infoTemplate',0),(64,71,'titleKey',0),(65,74,'infoTemplate',0),(66,74,'titleKey',0),(67,74,'helpKey',0),(68,75,'infoTemplate',0),(69,75,'titleKey',0),(70,78,'infoTemplate',0),(71,78,'titleKey',0),(72,81,'infoTemplate',0),(73,81,'titleKey',0),(74,81,'helpKey',0),(75,83,'infoTemplate',0),(76,83,'titleKey',0),(77,85,'titleKey',0),(78,85,'helpKey',0),(79,86,'titleKey',0),(80,86,'helpKey',0),(81,87,'noOfEntriesDisplayed',0),(82,87,'titleKey',0),(83,87,'helpKey',0),(84,88,'infoTemplate',0),(85,88,'titleKey',0),(86,88,'helpKey',0),(87,89,'titleKey',0),(88,90,'titleKey',0),(89,91,'titleKey',0),(90,94,'infoTemplate',0),(91,94,'titleKey',0),(92,94,'currPage',0),(93,95,'infoTemplate',0),(94,95,'titleKey',0),(95,96,'infoTemplate',0),(96,96,'titleKey',0),(97,98,'titleKey',0),(98,99,'titleKey',0),(99,100,'titleKey',0),(100,101,'titleKey',0),(101,102,'titleKey',0),(102,103,'infoTemplate',0),(103,103,'titleKey',0),(104,103,'currPage',0),(105,104,'infoTemplate',0),(106,104,'titleKey',0),(107,104,'helpKey',0),(108,107,'infoTemplate',0),(109,107,'titleKey',0),(110,109,'titleKey',0),(111,109,'helpKey',0),(112,110,'titleKey',0),(113,110,'helpKey',0),(114,111,'noOfEntriesDisplayed',0),(115,111,'titleKey',0),(116,111,'helpKey',0),(117,112,'infoTemplate',0),(118,112,'titleKey',0),(119,112,'helpKey',0),(120,113,'titleKey',0),(121,114,'titleKey',0),(122,115,'titleKey',0),(123,142,'titleKey',0),(124,142,'helpKey',0),(125,144,'infoTemplate',0),(126,144,'titleKey',0),(127,146,'titleKey',0),(128,146,'helpKey',0),(129,148,'infoTemplate',0),(130,148,'titleKey',0),(131,150,'titleKey',0),(132,150,'helpKey',0),(133,152,'infoTemplate',0),(134,152,'titleKey',0),(135,154,'titleKey',0),(136,154,'helpKey',0),(137,156,'infoTemplate',0),(138,156,'titleKey',0),(139,158,'titleKey',0),(140,158,'helpKey',0),(141,160,'infoTemplate',0),(142,160,'titleKey',0),(143,162,'titleKey',0),(144,162,'helpKey',0),(145,164,'infoTemplate',0),(146,164,'titleKey',0),(147,166,'titleKey',0),(148,166,'helpKey',0),(149,168,'infoTemplate',0),(150,168,'titleKey',0),(151,170,'titleKey',0),(152,170,'helpKey',0),(153,172,'infoTemplate',0),(154,172,'titleKey',0),(155,175,'titleKey',0),(156,175,'helpKey',0),(157,178,'titleKey',0),(158,178,'helpKey',0),(159,181,'infoTemplate',0),(160,181,'titleKey',0),(161,183,'titleKey',0),(162,183,'helpKey',0),(163,185,'infoTemplate',0),(164,185,'titleKey',0),(165,187,'titleKey',0),(166,187,'helpKey',0),(167,189,'infoTemplate',0),(168,189,'titleKey',0),(169,191,'titleKey',0),(170,191,'helpKey',0),(171,193,'infoTemplate',0),(172,193,'titleKey',0),(173,195,'titleKey',0),(174,195,'helpKey',0),(175,197,'infoTemplate',0),(176,197,'titleKey',0);
/*!40000 ALTER TABLE `fragment_pref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fragment_pref_value`
--

DROP TABLE IF EXISTS `fragment_pref_value`;
CREATE TABLE `fragment_pref_value` (
  `PREF_VALUE_ID` mediumint(9) NOT NULL,
  `PREF_ID` mediumint(9) NOT NULL,
  `VALUE_ORDER` mediumint(9) NOT NULL,
  `VALUE` varchar(100) NOT NULL,
  PRIMARY KEY  (`PREF_VALUE_ID`),
  KEY `IX_FRAGMENT_PREF_VALUE_1` (`PREF_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `fragment_pref_value`
--

LOCK TABLES `fragment_pref_value` WRITE;
/*!40000 ALTER TABLE `fragment_pref_value` DISABLE KEYS */;
INSERT INTO `fragment_pref_value` VALUES (1,1,0,'searchSimple.title.search'),(2,2,0,'search-1'),(3,3,0,'teaser.environment.title'),(4,4,0,'search-topics-1'),(5,5,0,'4'),(6,6,0,'news.teaser.title'),(7,7,0,'rss-news-1'),(8,8,0,'/WEB-INF/templates/ingrid_inform_teaser.vm'),(9,9,0,'portalu.teaser.inform'),(10,10,0,'ingrid-inform-1'),(11,11,0,'teaser.service.title'),(12,12,0,'search-service-1'),(13,13,0,'teaser.measures.title'),(14,14,0,'search-measure-1'),(15,15,0,'chronicle.teaser.title'),(16,16,0,'search-chronicle-1'),(17,17,0,'/WEB-INF/templates/default_cms.vm'),(18,18,0,'disclaimer.title'),(19,19,0,'portalu.disclaimer'),(20,20,0,'/WEB-INF/templates/disclaimer_info.vm'),(21,21,0,'disclaimer.info.title'),(22,22,0,'/WEB-INF/templates/default_cms.vm'),(23,23,0,'portalu.about'),(24,24,0,'about-1'),(25,25,0,'/WEB-INF/templates/about_portalu_links.vm'),(26,26,0,'about.links.title'),(27,27,0,'/WEB-INF/templates/about_portalu_partner.vm'),(28,28,0,'about.partner.title'),(29,29,0,'search-chronicle-1'),(30,30,0,'/WEB-INF/templates/chronicle_info.vm'),(31,31,0,'chronicleSearch.info.title'),(32,32,0,'search-topics-1'),(33,33,0,'/WEB-INF/templates/environment_info.vm'),(34,34,0,'envSearch.info.title'),(35,35,0,'/portal/search-extended/search-ext-env-area-contents.psml?select=2'),(36,36,0,'maps-1'),(37,37,0,'search-measure-1'),(38,38,0,'/WEB-INF/templates/measures_info.vm'),(39,39,0,'measuresSearch.info.title'),(40,40,0,'searchSimple.title.result'),(41,41,0,'search-results-1'),(42,42,0,'/WEB-INF/templates/search_simple_info.vm'),(43,43,0,'searchSimple.info.title'),(44,44,0,'search-service-1'),(45,45,0,'/WEB-INF/templates/service_info.vm'),(46,46,0,'serviceSearch.info.title'),(47,47,0,'/portal/search-extended/search-ext-env-area-contents.psml?select=3'),(48,48,0,'/WEB-INF/templates/myportal/login_teaser.vm'),(49,49,0,'teaser.login.title'),(50,50,0,'/WEB-INF/templates/myportal/login_teaser.vm'),(51,51,0,'teaser.login.title'),(52,52,0,'/WEB-INF/templates/default_cms.vm'),(53,53,0,'portalu.privacy'),(54,54,0,'4'),(55,55,0,'searchSimple.title.history'),(56,56,0,'search-history-1'),(57,57,0,'/WEB-INF/templates/search_history_info.vm'),(58,58,0,'searchHistory.info.title'),(59,59,0,'searchSimple.title.save'),(60,60,0,'search-save-1'),(61,61,0,'searchSimple.title.settings'),(62,62,0,'search-settings-1'),(63,63,0,'/WEB-INF/templates/search_settings_info.vm'),(64,64,0,'searchSettings.info.title'),(65,65,0,'/WEB-INF/templates/contact_back.vm'),(66,66,0,'teaser.newsletter.contact'),(67,67,0,'ingrid-newsletter-1'),(68,68,0,'/WEB-INF/templates/myportal/login_teaser.vm'),(69,69,0,'teaser.newsletter.more_info'),(70,70,0,'/WEB-INF/templates/newsletter_teaser.vm'),(71,71,0,'teaser.newsletter.title'),(72,72,0,'/WEB-INF/templates/myportal/login_teaser.vm'),(73,73,0,'teaser.login.title'),(74,74,0,'myingrid-1'),(75,75,0,'/WEB-INF/templates/sitemap.vm'),(76,76,0,'sitemap.title'),(77,77,0,'searchSimple.title.search'),(78,78,0,'search-1'),(79,79,0,'teaser.environment.title'),(80,80,0,'search-topics-1'),(81,81,0,'4'),(82,82,0,'news.teaser.title'),(83,83,0,'rss-news-1'),(84,84,0,'/WEB-INF/templates/ingrid_inform_teaser.vm'),(85,85,0,'teaser.ingridInform.title'),(86,86,0,'ingrid-inform-1'),(87,87,0,'teaser.service.title'),(88,88,0,'teaser.measures.title'),(89,89,0,'chronicle.teaser.title'),(90,90,0,'/WEB-INF/templates/myportal/myportal_navigation.vm'),(91,91,0,'myPortal.info.navigation.title'),(92,92,0,'editAccount'),(93,93,0,'/WEB-INF/templates/newsletter_teaser.vm'),(94,94,0,'teaser.newsletter.title'),(95,95,0,'/WEB-INF/templates/myportal/login_teaser.vm'),(96,96,0,'teaser.login.title'),(97,97,0,'personalize.overview.title'),(98,98,0,'personalize.home.title'),(99,99,0,'personalize.partner.title'),(100,100,0,'personalize.sources.title'),(101,101,0,'personalize.settings.title'),(102,102,0,'/WEB-INF/templates/myportal/myportal_navigation.vm'),(103,103,0,'myPortal.info.navigation.title'),(104,104,0,'personalize'),(105,105,0,'/WEB-INF/templates/myportal/login_teaser.vm'),(106,106,0,'teaser.login.title'),(107,107,0,'myingrid-1'),(108,108,0,'/WEB-INF/templates/myportal/myportal_navigation.vm'),(109,109,0,'myPortal.info.navigation.title'),(110,110,0,'searchSimple.title.search'),(111,111,0,'search-1'),(112,112,0,'teaser.environment.title'),(113,113,0,'search-topics-1'),(114,114,0,'4'),(115,115,0,'news.teaser.title'),(116,116,0,'rss-news-1'),(117,117,0,'/WEB-INF/templates/ingrid_inform_teaser.vm'),(118,118,0,'teaser.ingridInform.title'),(119,119,0,'ingrid-inform-1'),(120,120,0,'teaser.service.title'),(121,121,0,'teaser.measures.title'),(122,122,0,'chronicle.teaser.title'),(123,123,0,'searchSimple.title.extended'),(124,124,0,'ext-search-address-1'),(125,125,0,'/WEB-INF/templates/search_extended/search_ext_adr_info.vm'),(126,126,0,'searchExtAdr.info.title'),(127,127,0,'searchSimple.title.extended'),(128,128,0,'ext-search-address-1'),(129,129,0,'/WEB-INF/templates/search_extended/search_ext_adr_info.vm'),(130,130,0,'searchExtAdr.info.title'),(131,131,0,'searchSimple.title.extended'),(132,132,0,'ext-search-address-1'),(133,133,0,'/WEB-INF/templates/search_extended/search_ext_adr_info.vm'),(134,134,0,'searchExtAdr.info.title'),(135,135,0,'searchSimple.title.extended'),(136,136,0,'ext-search-address-1'),(137,137,0,'/WEB-INF/templates/search_extended/search_ext_adr_info.vm'),(138,138,0,'searchExtAdr.info.title'),(139,139,0,'searchSimple.title.extended'),(140,140,0,'ext-search-area-1'),(141,141,0,'/WEB-INF/templates/search_extended/search_ext_env_area_contents_info.vm'),(142,142,0,'searchExtEnvAreaContents.info.title'),(143,143,0,'searchSimple.title.extended'),(144,144,0,'ext-search-area-1'),(145,145,0,'/WEB-INF/templates/search_extended/search_ext_env_area_partner_info.vm'),(146,146,0,'searchExtEnvAreaPartner.info.title'),(147,147,0,'searchSimple.title.extended'),(148,148,0,'ext-search-area-1'),(149,149,0,'/WEB-INF/templates/search_extended/search_ext_env_area_sources_info.vm'),(150,150,0,'searchExtEnvAreaSources.info.title'),(151,151,0,'searchSimple.title.extended'),(152,152,0,'ext-search-spacial-1'),(153,153,0,'/WEB-INF/templates/search_extended/search_ext_env_place_geothesaurus_info.vm'),(154,154,0,'searchExtEnvPlaceGeothesaurus.info.title'),(155,155,0,'searchSimple.title.extended'),(156,156,0,'ext-search-spacial-1'),(157,157,0,'searchSimple.title.extended'),(158,158,0,'ext-search-time-1'),(159,159,0,'/WEB-INF/templates/search_extended/search_ext_env_time_constraint_info.vm'),(160,160,0,'searchExtEnvTimeConstraint.info.title'),(161,161,0,'searchSimple.title.extended'),(162,162,0,'ext-search-subject-1'),(163,163,0,'/WEB-INF/templates/search_extended/search_ext_env_topic_terms_info.vm'),(164,164,0,'searchExtEnvTopicTerms.info.title'),(165,165,0,'searchSimple.title.extended'),(166,166,0,'ext-search-subject-1'),(167,167,0,'/WEB-INF/templates/search_extended/search_ext_env_topic_thesaurus_info.vm'),(168,168,0,'searchExtEnvTopicThesaurus.info.title'),(169,169,0,'searchSimple.title.extended'),(170,170,0,'ext-search-research-2'),(171,171,0,'/WEB-INF/templates/search_extended/search_ext_res_topic_attributes_info.vm'),(172,172,0,'searchExtResTopicAttributes.info.title'),(173,173,0,'searchSimple.title.extended'),(174,174,0,'ext-search-research-1'),(175,175,0,'/WEB-INF/templates/search_extended/search_ext_res_topic_terms_info.vm'),(176,176,0,'searchExtResTopicTerms.info.title');
/*!40000 ALTER TABLE `fragment_pref_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_anniversary`
--

DROP TABLE IF EXISTS `ingrid_anniversary`;
CREATE TABLE `ingrid_anniversary` (
  `id` mediumint(9) NOT NULL auto_increment,
  `topic_id` varchar(255) NOT NULL,
  `topic_name` varchar(255) default NULL,
  `date_from` varchar(255) default NULL,
  `date_from_year` mediumint(9) default NULL,
  `date_from_month` mediumint(9) default NULL,
  `date_from_day` mediumint(9) default NULL,
  `date_to` varchar(255) default NULL,
  `date_to_year` mediumint(9) default NULL,
  `date_to_month` mediumint(9) default NULL,
  `date_to_day` mediumint(9) default NULL,
  `administrative_id` varchar(255) default NULL,
  `fetched` timestamp NOT NULL,
  `fetched_for` datetime NOT NULL,
  `language` varchar(5) default 'de',
  PRIMARY KEY  (`id`),
  KEY `IX_ingrid_anniversary_1` (`fetched_for`),
  KEY `IX_ingrid_anniversary_2` (`language`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ingrid_anniversary`
--

LOCK TABLES `ingrid_anniversary` WRITE;
/*!40000 ALTER TABLE `ingrid_anniversary` DISABLE KEYS */;
/*!40000 ALTER TABLE `ingrid_anniversary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_chron_eventtypes`
--

DROP TABLE IF EXISTS `ingrid_chron_eventtypes`;
CREATE TABLE `ingrid_chron_eventtypes` (
  `id` mediumint(9) NOT NULL auto_increment,
  `form_value` varchar(255) NOT NULL,
  `query_value` varchar(255) NOT NULL,
  `sortkey` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=13;

--
-- Dumping data for table `ingrid_chron_eventtypes`
--

LOCK TABLES `ingrid_chron_eventtypes` WRITE;
/*!40000 ALTER TABLE `ingrid_chron_eventtypes` DISABLE KEYS */;
INSERT INTO `ingrid_chron_eventtypes` VALUES (1,'ini','initiative',1),(2,'his','historical',2),(3,'law','law',3),(4,'ins','institution',4),(5,'dis','disaster',5),(6,'cfe','conference',6),(7,'cve','convention',7),(8,'yea','ofTheYear',8),(9,'pub','publication',9),(10,'gui','guideline',10),(11,'mar','marineAccident',11),(12,'ind','industrialAccident',12);
/*!40000 ALTER TABLE `ingrid_chron_eventtypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_cms`
--

DROP TABLE IF EXISTS `ingrid_cms`;
CREATE TABLE `ingrid_cms` (
  `id` mediumint(9) NOT NULL auto_increment,
  `item_key` varchar(255) NOT NULL,
  `item_description` varchar(255) default NULL,
  `item_changed` timestamp NOT NULL,
  `item_changed_by` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=20;

--
-- Dumping data for table `ingrid_cms`
--

LOCK TABLES `ingrid_cms` WRITE;
/*!40000 ALTER TABLE `ingrid_cms` DISABLE KEYS */;
INSERT INTO `ingrid_cms` VALUES (1,'portalu.teaser.inform','PortalU informiert Text','2006-09-14 22:00:00','admin'),(15,'portalu.disclaimer','Impressum','2006-09-14 22:00:00','admin'),(16,'portalu.about','Über PortalU','2006-09-14 22:00:00','admin'),(17,'portalu.privacy','Haftungsausschluss','2006-09-14 22:00:00','admin'),(18,'portalu.contact.intro.postEmail','Adresse auf der Kontaktseite','2006-09-14 22:00:00','admin'),(19,'ingrid.home.welcome','Ingrid Willkommens Portlet','2006-09-14 22:00:00','admin');
/*!40000 ALTER TABLE `ingrid_cms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_cms_item`
--

DROP TABLE IF EXISTS `ingrid_cms_item`;
CREATE TABLE `ingrid_cms_item` (
  `id` mediumint(9) NOT NULL auto_increment,
  `fk_ingrid_cms_id` mediumint(9) NOT NULL,
  `item_lang` varchar(6) NOT NULL,
  `item_title` varchar(255) default NULL,
  `item_value` text,
  `item_changed` timestamp NOT NULL,
  `item_changed_by` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=37;

--
-- Dumping data for table `ingrid_cms_item`
--

LOCK TABLES `ingrid_cms_item` WRITE;
/*!40000 ALTER TABLE `ingrid_cms_item` DISABLE KEYS */;
INSERT INTO `ingrid_cms_item` VALUES (1,1,'de','<span style=\"text-transform: none;\">PortalU INFORMIERT</span>','<p>Besuchen Sie PortalU® vom 6. bis 8. September auf dem 20. Internationalen Symposium Umweltinformatik - EnviroInfo 2006 - in Graz</p> <p class=\"iconLink\"> <span class=\"icon\"><img src=\"/ingrid-portal-apps/images/icn_linkextern.gif\" alt=\"externer Link\"></span> <span><a href=\"http://enviroinfo.know-center.tugraz.at/\" target=\"_new\" title=\"Link öffnet in neuem Fenster\">enviroinfo.know-center.tugraz.at/</a></span> <span class=\"clearer\"></span></p>','2006-09-14 22:00:00','admin'),(2,1,'en','<span style=\"text-transform: none;\">PortalU INFORMS</span>','<p>Visit PortalU® on September 6 to 8 at the 20th International Symposium on Informatics for Environmental Protection - EnviroInfo 2006 - in Graz (Austria)</p> <p class=\"iconLink\"> <span class=\"icon\"><img src=\"/ingrid-portal-apps/images/icn_linkextern.gif\" alt=\"External Link\"></span> <span><a href=\"http://enviroinfo.know-center.tugraz.at/\" target=\"_new\" title=\"Link opens new window\">enviroinfo.know-center.tugraz.at/</a></span> <span class=\"clearer\"></span></p>','2006-09-14 22:00:00','admin'),(27,15,'en','Disclaimer','<a name=\"herausgeber\"></a>\r\n<h2>Publisher</h2>\r\n<p>PortalU is managed by the Coordination Center PortalU at the Environment Ministry of Lower Saxony, Hannover, Germany. Development and maintenance of the portal is financed by a administrative agreement between the German Federal States (Länder) and the Federal Government. </p>\r\n<h3>Coordination Center PortalU</h3>\r\n<p>Niedersächsisches Umweltministerium<br>Archivstrasse 2<br>D-30169 Hannover<br>\r\n	<a href=\"/ingrid-portal/portal/service-contact.psml\">Contact</a>\r\n</p>\r\n<br>\r\n<a name=\"verantwortlich\"></a>\r\n<h2>Overall Responsibility</h2>\r\n<p>Dr. Fred Kruse</p>\r\n<br>\r\n<a name=\"realisierung\"></a>\r\n<h2>Implementation</h2>\r\n<h3><a href=\"http://www.gistec-online.de/\" target=\"_new\" title=\"Link opens new window\">GIStec GmbH</a></h3>\r\n<h3><a href=\"http://www.media-style.com/\" target=\"_new\" title=\"Link opens new window\">media style GmbH</a></h3>\r\n<h3><a href=\"http://www.wemove.com/contact.php\" target=\"_new\" title=\"Link opens new window\">wemove digital solutions GmbH</a></h3>\r\n<h3><a href=\"http://www.chives.de/\" target=\"_new\" title=\"Link opens new window\">chives - Büro für Webdesign Plus</a></h3>\r\n<br>\r\n<a name=\"betrieb\"></a>\r\n<h2>Operation</h2>     \r\n<h3><a href=\"http://www.its-technidata.de/\" target=\"_new\" title=\"Link opens new window\">TechniData IT Service GmbH</a></h3>\r\n<br>\r\n<a name=\"haftung\"></a>\r\n<h2>Liability Disclaimer</h2>     \r\n<p>The Environment Ministry of Lower Saxony (Niedersächsisches Umweltministerium) does not take any responisbility for the content of web-sites that can be reached through PortalU. Web-sites that are included in the portal are evaluated only technically. A continuous evaluation of the content of the included web-pages in neither possible nor intended. The Environment Ministry of Lower Saxony explicitly rejects all content that potentially infringes upon German legislation or general morality.</p>','2006-09-14 22:00:00','admin'),(28,15,'de','Impressum','<a name=\"herausgeber\"></a>\r\n<h2>Herausgeber</h2>\r\n<p>PortalU wird von der Koordinierungsstelle PortalU im Niedersächsischen Umweltministerium auf der Grundlage der <a href=\"http://www.kst.portalu.de/ueberuns/VVGEIN_endg.pdf\" target=\"_new\" title=\"Link öffnet in neuem Fenster\">Bund-Länder-Verwaltungsvereinbarung UDK/GEIN</a> betrieben und weiterentwickelt.</p>\r\n<h3>Koordinierungsstelle PortalU</h3>\r\n<p>\r\nNiedersächsisches Umweltministerium<br>Archivstrasse 2<br>D-30169 Hannover<br>\r\n<a href=\"/ingrid-portal/portal/service-contact.psml\">Kontakt</a>\r\n</p>\r\n<br>\r\n<a name=\"verantwortlich\"></a>\r\n<h2>Verantwortliche Gesamtredaktion</h2>\r\n<p>Dr. Fred Kruse</p>\r\n<br>\r\n<a name=\"realisierung\"></a>\r\n<h2>Realisierung</h2>\r\n<h3><a href=\"http://www.gistec-online.de/\" target=\"_new\" title=\"Link öffnet in neuem Fenster\">GIStec GmbH</a></h3>\r\n<h3><a href=\"http://www.media-style.com/\" target=\"_new\" title=\"Link öffnet in neuem Fenster\">media style GmbH</a></h3>\r\n<h3><a href=\"http://www.wemove.com/contact.php\" target=\"_new\" title=\"Link öffnet in neuem Fenster\">wemove digital solutions GmbH</a></h3>\r\n<h3><a href=\"http://www.chives.de/\" target=\"_new\" title=\"Link öffnet in neuem Fenster\">chives - Büro für Webdesign Plus Darmstadt</a></h3>\r\n<br>\r\n<a name=\"betrieb\"></a>\r\n<h2>Technischer Betrieb</h2>     \r\n<h3><a href=\"http://www.its-technidata.de/\" target=\"_new\" title=\"Link öffnet in neuem Fenster\">TechniData IT Service GmbH</a></h3>\r\n<br>\r\n<a name=\"haftung\"></a>\r\n<h2>Haftungsausschluss</h2>     \r\n<p>Das Niedersächsische Umweltministerium übernimmt keine Verantwortung für die Inhalte von Websites, die über Links erreicht werden. Die Links werden bei der Aufnahme nur kursorisch angesehen und bewertet. Eine kontinuierliche Prüfung der Inhalte ist weder beabsichtigt noch möglich. Das Niedersächsische Umweltministerium distanziert sich ausdrücklich von allen Inhalten, die möglicherweise straf- oder haftungsrechtlich relevant sind oder gegen die guten Sitten verstoßen.</p>','2006-09-14 22:00:00','admin'),(29,16,'de','Über PortalU','<p>Willkommen bei PortalU, dem Umweltportal Deutschland! Wir bieten Ihnen einen zentralen Zugriff auf mehrere hunderttausend Internetseiten und Datenbankeinträge von öffentlichen Institutionen und Organisationen. Zusätzlich können Sie aktuelle Nachrichten und Veranstaltungshinweise, Umweltmesswerte, Hintergrundinformationen und historische Umweltereignisse über PortalU abrufen.</p><p>Die integrierte Suchmaschine ist eine zentrale Komponente von PortalU. Mit ihrer Hilfe können Sie Webseiten und Datenbankeinträge nach Stichworten durchsuchen. Über die Option \"Erweiterte Suche\" können Sie zusätzlich ein differenziertes Fachvokabular und deutschlandweite Hintergrundkarten zur Zusammenstellung Ihrer Suchanfrage nutzen.</p><p>PortalU ist eine Kooperation der Umweltverwaltungen im Bund und in den Ländern. Inhaltlich und technisch wird PortalU von der <a href=\"http://www.kst.portalu.de/\" target=\"_new\" title=\"Link öffnet in neuem Fenster\">Koordinierungsstelle PortalU</a> im Niedersächsischen Umweltministerium verwaltet. Wir sind darum bemüht, das System kontinuierlich zu erweitern und zu optimieren. Bei Fragen und Verbesserungsvorschlägen wenden Sie sich bitte an die <a href=\"mailto:kst@portalu.de\">Koordinierungsstelle PortalU</a>.</p>','2006-09-14 22:00:00','admin'),(30,16,'en','About PortalU','<p>Welcome to PortalU, the German Environmental Information Portal! We offer a comfortable and central access to over 1.000.000 web-pages and database entries from public agencies in Germany. We also guide you directly to up-to-date environmental news, upcoming and past environmental events, environmental monitoring data, and interesting background information on many environmental topics.</p><p>Core-component of PortalU is a powerful search-engine that you can use to look up your terms of interest in web-pages and databases. In the \"Extended Search\" mode, you can use an environmental thesaurus and a digital mapping tool to compose complex spatio-thematic queries.</p><p>PortalU is the result of a cooperation between the German \"Länder\" and the German Federal Government. The project is managed by the <a href=\"http://www.kst.portalu.de/\" target=\"_new\" title=\"Link opens new window\">Coordination Center PortalU</a>, a group of environmental and IT experts attached to the Environment Ministry of Lower Saxony in Hannover, Germany. We strive to continuously improve and extend the portal. Please help us in this effort and mail your suggestions or questions to <a href=\"mailto:kst@portalu.de\">Coordination Center PortalU</a>.</p>','2006-09-14 22:00:00','admin'),(31,17,'de','Datenschutz','<p>PortalU enthält sowohl Inhalte, die als Teledienst nach § 2 Teledienstgesetz (TDG) als auch Inhalte, die als Mediendienst nach § 2 Mediendienste-Staatsvertrag (MDStV) zu bewerten sind. Hierbei werden folgende Verfahrensgrundsätze gewährleistet:<br></p>\r\n<ul>\r\n<li>Bei jedem Zugriff eines Nutzers auf eine Seite aus dem Angebot von PortalU und bei jedem Abruf einer Datei werden Daten über diesen Vorgang in einer Protokolldatei gespeichert. Diese Daten sind nicht personenbezogen. Wir können also nicht nachvollziehen, welcher Nutzer welche Daten abgerufen hat. Die Protokolldaten werden lediglich in anonymisierter Form statistisch ausgewertet und dienen damit der inhaltlichen Verbesserung unseres Angebotes.<br><br>\r\n</li>\r\n<li>Eine Ausnahme besteht innerhalb des Internetangebotes mit der Eingabe persönlicher oder geschäftlicher Daten (eMail-Adresse, Name, Anschrift) zur Anmeldung bei \"Mein PortalU\" oder der Bestellung des PortalU-Newsletters. Dabei erfolgt die Angabe dieser Daten durch Nutzerinnen und Nutzer ausdrücklich freiwillig. Ihre persönlichen Daten werden von uns selbstverständlich nicht an Dritte weitergegeben. Die Inanspruchnahme aller angebotenen Dienste ist, soweit dies technisch möglich und zumutbar ist, auch ohne Angabe solcher Daten beziehungsweise unter Angabe anonymisierter Daten oder eines Pseudonyms möglich.<br><br>\r\n</li>\r\n<li>Sie können alle allgemein zugänglichen PortalU-Seiten ohne den Einsatz von Cookies benutzen. Wenn Ihre Browser-Einstellungen das Setzen von Cookies zulassen, werden von PortalU sowohl Session-Cookies als auch permanente Cookies gesetzt. Diese dienen ausschließlich der Erhöhung des Bedienungskomforts.\r\n</li>\r\n</ul>','2006-09-14 22:00:00','admin'),(32,17,'en','Privacy Policy','<p>PortalU contains content that is categorized as \"Teledienst\" (after § 2 Teledienstgesetz (TDG)), as well as content that is categorized as \"Mediendienst\" (after § 2 Mediendienste-Staatsvertrag (MDStV)). The following policies do apply:<br></p>\r\n<ul>\r\n<li>With each user-access to a content-page in PortalU, the relevant access-data are saved in a log file. This information is not personalized. Therefore it is not possible to reason which user has had access to which content page. The purpose of the log file is purely statistical. The evaluation of the log file helps to improve PortalU.<br><br>\r\n</li>\r\n<li>An exeption to our general privacy policy is made when personal data (e-mail, name, address) are provided to register for the PortalU newsletter. This information is provided by the user on a voluntary basis an is saved for internal purposes (mailing of newsletter). The information is not given to third-parties. The use of specific Portal functions does not, as far as technically feasible, depend on the provision of personal data.<br><br>\r\n</li>\r\n<li>You can take benefit of virtually all functions of PortalU without the use of cookies. However, if you choose to allow cookies in your browser, this will increase the useability of the application. \r\n</li>\r\n</ul>','2006-09-14 22:00:00','admin'),(33,18,'de','','.</p><p>Unsere Postadresse:</p>\r\n<p>Niedersächsisches Umweltministerium<br />Koordinierungsstelle PortalU<br />Archivstrasse 2<br />D-30169 Hannover<br /></p> <p>Nehmen Sie online Kontakt mit uns auf! Wir werden Ihnen schnellstmöglichst per E-Mail antworten. Die eingegebenen Informationen und Daten werden nur zur Bearbeitung Ihrer Anfrage gespeichert und genutzt.</p>','2006-09-14 22:00:00','admin'),(34,18,'en','','.</p><p>Our address:</p><p>Niedersächsisches Umweltministerium<br />Koordinierungsstelle PortalU<br />Archivstrasse 2<br />D-30169 Hannover<br /></p> <p>Please contact us! We will answer your request as soon as possible. All data you entered will be saved only to process your request.</p>','2006-09-14 22:00:00','admin'),(35,19,'de','Willkommen bei PortalU','<p>Willkommen bei PortalU, dem Umweltportal Deutschland! Wir bieten Ihnen einen zentralen Zugriff auf mehrere hunderttausend Internetseiten und Datenbankeinträge von öffentlichen Institutionen und Organisationen. Zusätzlich können Sie aktuelle Nachrichten und Veranstaltungshinweise, Umweltmesswerte, Hintergrundinformationen und historische Umweltereignisse über PortalU abrufen.</p><p>Die integrierte Suchmaschine ist eine zentrale Komponente von PortalU. Mit ihrer Hilfe können Sie Webseiten und Datenbankeinträge nach Stichworten durchsuchen. Über die Option \"Erweiterte Suche\" können Sie zusätzlich ein differenziertes Fachvokabular und deutschlandweite Hintergrundkarten zur Zusammenstellung Ihrer Suchanfrage nutzen.</p><p>PortalU ist eine Kooperation der Umweltverwaltungen im Bund und in den Ländern. Inhaltlich und technisch wird PortalU von der <a href=\"http://www.kst.portalu.de/\" target=\"_new\" title=\"Link öffnet in neuem Fenster\">Koordinierungsstelle PortalU</a> im Niedersächsischen Umweltministerium verwaltet. Wir sind darum bemüht, das System kontinuierlich zu erweitern und zu optimieren. Bei Fragen und Verbesserungsvorschlägen wenden Sie sich bitte an die <a href=\"mailto:kst@portalu.de\">Koordinierungsstelle PortalU</a>.</p>','2006-09-14 22:00:00','admin'),(36,19,'en','Welcome to PortalU','<p>Welcome to PortalU, the German Environmental Information Portal! We offer a comfortable and central access to over 1.000.000 web-pages and database entries from public agencies in Germany. We also guide you directly to up-to-date environmental news, upcoming and past environmental events, environmental monitoring data, and interesting background information on many environmental topics.</p><p>Core-component of PortalU is a powerful search-engine that you can use to look up your terms of interest in web-pages and databases. In the \"Extended Search\" mode, you can use an environmental thesaurus and a digital mapping tool to compose complex spatio-thematic queries.</p><p>PortalU is the result of a cooperation between the German \"Länder\" and the German Federal Government. The project is managed by the <a href=\"http://www.kst.portalu.de/\" target=\"_new\" title=\"Link opens new window\">Coordination Center PortalU</a>, a group of environmental and IT experts attached to the Environment Ministry of Lower Saxony in Hannover, Germany. We strive to continuously improve and extend the portal. Please help us in this effort and mail your suggestions or questions to <a href=\"mailto:kst@portalu.de\">Coordination Center PortalU</a>.</p>','2006-09-14 22:00:00','admin');
/*!40000 ALTER TABLE `ingrid_cms_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_codelist`
--

DROP TABLE IF EXISTS `ingrid_codelist`;
CREATE TABLE `ingrid_codelist` (
  `codelist_id` bigint(20) NOT NULL default '0',
  `name` varchar(50) default NULL,
  `default_enabled` mediumint(9) default NULL,
  `maintainable` mediumint(9) default NULL,
  PRIMARY KEY  (`codelist_id`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ingrid_codelist`
--

LOCK TABLES `ingrid_codelist` WRITE;
/*!40000 ALTER TABLE `ingrid_codelist` DISABLE KEYS */;
INSERT INTO `ingrid_codelist` VALUES (100,'Raumbezugssystem (EPSG)',1,1),(101,'Vertikales Datum',1,1),(102,'Höhe',1,1),(502,'Zeitbezug des Datensatzes (Typ)',1,1),(505,'Adresse',1,1),(510,'Zeichensatz',1,1),(515,'Vektorformat (Geometrie)',1,1),(517,'Schlüsselwort',1,1),(518,'Periodizität',1,1),(520,'Medium',1,1),(523,'Status',1,1),(525,'Datensatz/Datenserie',1,1),(526,'Digitale Repräsentation',1,1),(527,'Klassifikation',1,1),(528,'Vektorformat (Topologie)',1,1),(99999999,'Sprache',1,0);
/*!40000 ALTER TABLE `ingrid_codelist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_codelist_domain`
--

DROP TABLE IF EXISTS `ingrid_codelist_domain`;
CREATE TABLE `ingrid_codelist_domain` (
  `codelist_id` bigint(20) NOT NULL default '0',
  `domain_id` bigint(20) NOT NULL default '0',
  `lang_id` bigint(20) NOT NULL default '0',
  `name` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `def_domain` mediumint(9) default NULL,
  PRIMARY KEY  (`codelist_id`,`domain_id`,`lang_id`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ingrid_codelist_domain`
--

LOCK TABLES `ingrid_codelist_domain` WRITE;
/*!40000 ALTER TABLE `ingrid_codelist_domain` DISABLE KEYS */;
INSERT INTO `ingrid_codelist_domain` VALUES (100,4178,94,'EPSG:4178 / Pulkovo 1942(83) / geographisch','Pulkovo 1942(83) / geographisch',0),(100,4178,121,'EPSG:4178 / Pulkovo 1942(83) / geographisch','Pulkovo 1942(83) / geographisch',0),(100,4230,94,'EPSG:4230 / ED50 / geographisch','ED50 / geographisch',0),(100,4230,121,'EPSG:4230 / ED50 / geographisch','ED50 / geographisch',0),(100,4258,94,'EPSG:4258 / ETRS89 / geographisch','ETRS89 / geographisch',0),(100,4258,121,'EPSG:4258 / ETRS89 / geographisch','ETRS89 / geographisch',0),(100,4284,94,'EPSG:4284 / Pulkovo 1942 / geographisch','Pulkovo 1942 / geographisch',0),(100,4284,121,'EPSG:4284 / Pulkovo 1942 / geographisch','Pulkovo 1942 / geographisch',0),(100,4314,94,'EPSG:4314 / DHDN / geographisch','DHDN / geographisch',0),(100,4314,121,'EPSG:4314 / DHDN / geographisch','DHDN / geographisch',0),(100,4326,94,'EPSG:4326 / WGS 84 / geographisch','WGS 84 / geographisch',0),(100,4326,121,'EPSG:4326 / WGS 84 / geographisch','WGS 84 / geographisch',0),(100,23031,94,'EPSG:23031 / ED50 / UTM Zone 31N','ED50 / UTM Zone 31N',0),(100,23031,121,'EPSG:23031 / ED50 / UTM Zone 31N','ED50 / UTM Zone 31N',0),(100,23032,94,'EPSG:23032 / ED50 / UTM Zone 32N','ED50 / UTM Zone 32N',0),(100,23032,121,'EPSG:23032 / ED50 / UTM Zone 32N','ED50 / UTM Zone 32N',0),(100,23033,94,'EPSG:23033 / ED50 / UTM Zone 33N','ED50 / UTM Zone 33N',0),(100,23033,121,'EPSG:23033 / ED50 / UTM Zone 33N','ED50 / UTM Zone 33N',0),(100,23631,94,'EPSG:32631 / WGS 84 / UTM Zone 31N','WGS 84 / UTM Zone 31N',0),(100,23631,121,'EPSG:32631 / WGS 84 / UTM Zone 31N','WGS 84 / UTM Zone 31N',0),(100,23632,94,'EPSG:32632 / WGS 84 / UTM Zone 32N/33N','WGS 84 / UTM Zone 32N/33N',0),(100,23632,121,'EPSG:32632 / WGS 84 / UTM Zone 32N/33N','WGS 84 / UTM Zone 32N/33N',0),(100,25831,94,'EPSG:25831 / ETRS89 / UTM Zone 31N','ETRS89 / UTM Zone 31N',0),(100,25831,121,'EPSG:25831 / ETRS89 / UTM Zone 31N','ETRS89 / UTM Zone 31N',0),(100,25832,94,'EPSG:25832 / ETRS89 / UTM Zone 32N','ETRS89 / UTM Zone 32N',0),(100,25832,121,'EPSG:25832 / ETRS89 / UTM Zone 32N','ETRS89 / UTM Zone 32N',0),(100,25833,94,'EPSG:25833 / ETRS89 / UTM Zone 33N','ETRS89 / UTM Zone 33N',0),(100,25833,121,'EPSG:25833 / ETRS89 / UTM Zone 33N','ETRS89 / UTM Zone 33N',0),(100,28463,94,'EPSG:28463 / Pulkovo 1942 / Gauss-Krüger 2N/3N','Pulkovo 1942 / Gauss-Krüger 2N/3N',0),(100,28463,121,'EPSG:28463 / Pulkovo 1942 / Gauss-Krüger 2N/3N','Pulkovo 1942 / Gauss-Krüger 2N/3N',0),(100,31281,94,'EPSG:31281 / MGI (Ferro) / Austria West Zone','-',0),(100,31281,121,'EPSG:31281 / MGI (Ferro) / Austria West Zone','-',0),(100,31282,94,'EPSG:31282 / MGI (Ferro) / Austria Central Zone','-',0),(100,31282,121,'EPSG:31282 / MGI (Ferro) / Austria Central Zone','-',0),(100,31283,94,'EPSG:31283 / MGI (Ferro) / Austria East Zone','-',0),(100,31283,121,'EPSG:31283 / MGI (Ferro) / Austria East Zone','-',0),(100,31284,94,'EPSG:31284 / MGI / M28','-',0),(100,31284,121,'EPSG:31284 / MGI / M28','-',0),(100,31285,94,'EPSG:31285 / MGI / M31','-',0),(100,31285,121,'EPSG:31285 / MGI / M31','-',0),(100,31286,94,'EPSG:31286 / MGI / M34','-',0),(100,31286,121,'EPSG:31286 / MGI / M34','-',0),(100,31287,94,'EPSG:31287 / MGI / Austria Lambert','-',0),(100,31287,121,'EPSG:31287 / MGI / Austria Lambert','-',0),(100,31288,94,'EPSG:31288 / MGI (Ferro) / M28','-',0),(100,31288,121,'EPSG:31288 / MGI (Ferro) / M28','-',0),(100,31289,94,'EPSG:31289 / MGI (Ferro) / M31','-',0),(100,31289,121,'EPSG:31289 / MGI (Ferro) / M31','-',0),(100,31290,94,'EPSG:31290 / MGI (Ferro) / M34','-',0),(100,31290,121,'EPSG:31290 / MGI (Ferro) / M34','-',0),(100,31291,94,'EPSG:31291 / MGI (Ferro) / Austria West Zone','-',0),(100,31291,121,'EPSG:31291 / MGI (Ferro) / Austria West Zone','-',0),(100,31292,94,'EPSG:31292 / MGI (Ferro) / Austria Central Zone','-',0),(100,31292,121,'EPSG:31292 / MGI (Ferro) / Austria Central Zone','-',0),(100,31293,94,'EPSG:31293 / MGI (Ferro) / Austria East Zone','-',0),(100,31293,121,'EPSG:31293 / MGI (Ferro) / Austria East Zone','-',0),(100,31466,94,'EPSG:31466 / DHDN / Gauss-Krüger Zone 2','DHDN / Gauss-Krüger Zone 2',0),(100,31466,121,'EPSG:31466 / DHDN / Gauss-Krüger Zone 2','DHDN / Gauss-Krüger Zone 2',0),(100,31467,94,'EPSG:31467 /DHDN / Gauss-Krüger Zone 3','DHDN / Gauss-Krüger Zone 3',0),(100,31467,121,'EPSG:31467 /DHDN / Gauss-Krüger Zone 3','DHDN / Gauss-Krüger Zone 3',0),(100,31468,94,'EPSG:31468 / DHDN / Gauss-Krüger Zone 4','DHDN / Gauss-Krüger Zone 4',0),(100,31468,121,'EPSG:31468 / DHDN / Gauss-Krüger Zone 4','DHDN / Gauss-Krüger Zone 4',0),(100,31469,94,'EPSG:31469 / DHDN / Gauss-Krüger Zone 5','DHDN / Gauss-Krüger Zone 5',0),(100,31469,121,'EPSG:31469 / DHDN / Gauss-Krüger Zone 5','DHDN / Gauss-Krüger Zone 5',0),(100,31491,94,'EPSG:31491 / DHDN / Germany zone 1','DHDN / Germany zone 1',0),(100,31491,121,'EPSG:31491 / DHDN / Germany zone 1','DHDN / Germany zone 1',0),(100,31492,94,'EPSG:31492 /DHDN / Germany zone 2','DHDN / Germany zone 2',0),(100,31492,121,'EPSG:31492 /DHDN / Germany zone 2','DHDN / Germany zone 2',0),(100,31493,94,'EPSG:31493 / DHDN / Germany zone 3','DHDN / Germany zone 3',1),(100,31493,121,'EPSG:31493 / DHDN / Germany zone 3','DHDN / Germany zone 3',1),(100,31494,94,'EPSG:31494 / DHDN / Germany zone 4','DHDN / Germany zone 4',0),(100,31494,121,'EPSG:31494 / DHDN / Germany zone 4','DHDN / Germany zone 4',0),(100,31495,94,'EPSG:31495 / DHDN / Germany zone 5','DHDN / Germany zone 5',0),(100,31495,121,'EPSG:31495 / DHDN / Germany zone 5','DHDN / Germany zone 5',0),(100,9000001,94,'DE_42/83 / GK_3','Datum 42/83 with Gauss-Krüger-System',0),(100,9000001,121,'DE_42/83 / GK_3','Datum 42/83 with Gauss-Krüger-System',0),(100,9000002,94,'DE_DHDN / GK_3','Datum DHDN with Gauss-Krüger-System (also known as Rauenberg or Potsdam Datum)',0),(100,9000002,121,'DE_DHDN / GK_3','Datum DHDN with Gauss-Krüger-System (also known as Rauenberg or Potsdam Datum)',0),(100,9000003,94,'DE_ETRS89 / UTM','Datum ETRS89 for Federal State Brandenburg with UTM Projection (one zone extended)',0),(100,9000003,121,'DE_ETRS89 / UTM','Datum ETRS89 for Federal State Brandenburg with UTM Projection (one zone extended)',0),(100,9000004,94,'DE_PD/83 / GK_3','Datum PD/83 with Gauss-Krüger-System (realisation of Postdam Datum for Federal State Thüringen)',0),(100,9000005,121,'DE_PD/83 / GK_3','Datum PD/83 with Gauss-Krüger-System (realisation of Postdam Datum for Federal State Thüringen)',0),(100,9000006,94,'DE_RD/83 / GK_3','Datum RD/83 with Gauss-Krüger-System (realisation of Rauenberg Datum for Federal State Sachsen)',0),(100,9000006,121,'DE_RD/83 / GK_3','Datum RD/83 with Gauss-Krüger-System (realisation of Rauenberg Datum for Federal State Sachsen)',0),(101,62,94,'Mean Sea Level','-',0),(101,62,121,'Mean Sea Level','-',0),(101,63,94,'Ordnance Datum Newlyn','-',0),(101,63,121,'Ordnance Datum Newlyn','-',0),(101,64,94,'National Geodetic Vertical Datum 1929','26 tide gauges in the US and Canada.',0),(101,64,121,'National Geodetic Vertical Datum 1929','26 tide gauges in the US and Canada.',0),(101,65,94,'North American Vertical Datum 1988','Father\'s Point, Rimouski, Quebec.',0),(101,65,121,'North American Vertical Datum 1988','Father\'s Point, Rimouski, Quebec.',0),(101,5105,94,'Baltic Sea','Average water level at Kronshtadt',0),(101,5105,121,'Baltic Sea','Average water level at Kronshtadt',0),(101,5107,94,'Nivellement general de la France','Mean sea level at Marseille',0),(101,5107,121,'Nivellement general de la France','Mean sea level at Marseille',0),(101,5109,94,'Normaal Amsterdams Peil','wird in alten Bundesländern genutzt',1),(101,5109,121,'Amsterdamer Pegel (NN)','wird in alten Bundesländern genutzt',1),(101,5110,94,'Oostende','Mean low water during 1958',0),(101,5110,121,'Oostende','Mean low water during 1958',0),(101,5113,94,'Sea Level','-',0),(101,5113,121,'Sea Level','-',0),(101,5114,94,'Canadian Vertical Datum of 1928','-',0),(101,5114,121,'Canadian Vertical Datum of 1928','-',0),(101,5115,94,'Piraeus Harbour 1986','MSL determined during 1986.',0),(101,5115,121,'Piraeus Harbour 1986','MSL determined during 1986.',0),(101,5116,94,'Helsinki 1960','MSL at Helsinki during 1960.',0),(101,5116,121,'Helsinki 1960','MSL at Helsinki during 1960.',0),(101,5117,94,'Rikets hojdsystem 1970','-',0),(101,5117,121,'Rikets hojdsystem 1970','-',0),(101,5118,94,'Nivellement general de la France - Lalle','Mean sea level at Marseille.',0),(101,5118,121,'Nivellement general de la France - Lalle','Mean sea level at Marseille.',0),(101,5119,94,'Nivellement general de la France - IGN69','Mean sea level at Marseille.',0),(101,5119,121,'Nivellement general de la France - IGN69','Mean sea level at Marseille.',0),(101,5120,94,'Nivellement general de la France - IGN78','-',0),(101,5120,121,'Nivellement general de la France - IGN78','-',0),(101,5123,94,'PDO Height Datum 1993','-',0),(101,5123,121,'PDO Height Datum 1993','-',0),(101,5127,94,'Landesnivellement 1902','Origin at Repere Pierre du Niton (RPN) 373.6 metres above msl derived at Marseille.',0),(101,5127,121,'Landesnivellement 1902','Origin at Repere Pierre du Niton (RPN) 373.6 metres above msl derived at Marseille.',0),(101,5128,94,'Landeshohennetz 1995','Origin at Repere Pierre du Niton (RPN) 373.6 metres above msl derived at Marseille.',0),(101,5128,121,'Landeshohennetz 1995','Origin at Repere Pierre du Niton (RPN) 373.6 metres above msl derived at Marseille.',0),(101,5129,94,'European Vertical Reference Frame 2000','Geopotential number at Normaal Amsterdams Peil is zero.',0),(101,5129,121,'European Vertical Reference Frame 2000','Geopotential number at Normaal Amsterdams Peil is zero.',0),(101,5130,94,'Malin Head','Mean sea level between January 1960 and December 1969.',0),(101,5130,121,'Malin Head','Mean sea level between January 1960 and December 1969.',0),(101,5131,94,'Belfast Lough','Mean sea level between 1951 and 1956 at Clarendon Dock, Belfast.',0),(101,5131,121,'Belfast Lough','Mean sea level between 1951 and 1956 at Clarendon Dock, Belfast.',0),(101,5132,94,'Dansk Normal Nul','-',0),(101,5132,121,'Dansk Normal Nul','-',0),(101,5138,94,'Ordnance Datum Newlyn (Orkney Isles)','-',0),(101,5138,121,'Ordnance Datum Newlyn (Orkney Isles)','-',0),(101,5139,94,'Fair Isle','-',0),(101,5139,121,'Fair Isle','-',0),(101,5140,94,'Lerwick','-',0),(101,5140,121,'Lerwick','-',0),(101,5141,94,'Foula','-',0),(101,5141,121,'Foula','-',0),(101,5142,94,'Sule Skerry','-',0),(101,5142,121,'Sule Skerry','-',0),(101,5143,94,'North Rona','-',0),(101,5143,121,'North Rona','-',0),(101,5144,94,'Stornoway','-',0),(101,5144,121,'Stornoway','-',0),(101,5145,94,'St. Kilda','-',0),(101,5145,121,'St. Kilda','-',0),(101,5146,94,'Flannan Isles','-',0),(101,5146,121,'Flannan Isles','-',0),(101,5147,94,'St. Marys','-',0),(101,5147,121,'St. Marys','-',0),(101,5148,94,'Douglas','-',0),(101,5148,121,'Douglas','-',0),(101,5149,94,'Fao','-',0),(101,5149,121,'Fao','-',0),(101,5151,94,'Nivellement General Nouvelle Caledonie','-',0),(101,5151,121,'Nivellement General Nouvelle Caledonie','-',0),(101,900001,94,'Kronstädter Pegel (HN)','wird in neuen Bundesländern genutzt',0),(101,900001,121,'Kronstädter Pegel (HN)','wird in neuen Bundesländern genutzt',0),(101,900002,94,'DE_AMST / NH','normal heights referred to tide gauge Amsterdam (also known as DHHN92)',0),(101,900002,121,'DE_AMST / NH','normal heights referred to tide gauge Amsterdam (also known as DHHN92)',0),(101,900003,94,'DE_AMST / NOH','normal-orthometric heights referred to tide gauge Amsterdam (also known as DHHN85)',0),(101,900003,121,'DE_AMST / NOH','normal-orthometric heights referred to tide gauge Amsterdam (also known as DHHN85)',0),(101,900004,94,'DE_KRON / NH','normal heights referred to tide gauge Kronstadt (also known as SNN76)',0),(101,900004,121,'DE_KRON / NH','normal heights referred to tide gauge Kronstadt (also known as SNN76)',0),(102,4,94,'Zoll','Zoll',0),(102,4,121,'Inch','Inch',0),(102,9001,94,'Metre','Metre',1),(102,9001,121,'Meter','Meter',1),(102,9002,94,'Foot','Foot',0),(102,9002,121,'Fuss','Fuss',0),(102,9036,94,'Kilometre','Kilometre',0),(102,9036,121,'Kilometer','Kilometer',0),(502,1,94,'creation','date identifies when the resource was brought into existence',1),(502,1,121,'Erstellung','Datum, wann die Quelle geschaffen wurde',1),(502,2,94,'publication','date identifies when the resource was issued',0),(502,2,121,'Publikation','Dateum, wann die Quelle publiziert wurde',0),(502,3,94,'revision','date identifies when the resource was examined or re-examined and improved or amended',0),(502,3,121,'letzte Änderung','Datum, wann eine Revision durchgeführt wurde',0),(505,1,94,'Provider','Party that supplies the resource',0),(505,1,121,'Anbieter','Anbieter der Datenquelle',0),(505,2,94,'Custodian','Party that accepts accountability and responsibility for the data and ensures appropriate care and maintenance of the resource',0),(505,2,121,'Datenverantwortung','Verantworlicher für die Datenquelle und deren Pflege',0),(505,3,94,'Owner','Party that owns the resource',0),(505,3,121,'Halter','Halter der Datenquelle',0),(505,4,94,'User','Party who uses the resource',0),(505,4,121,'Benutzer','Benutzer der Datenquelle',0),(505,5,94,'Distributor','Party who distributes the resource',0),(505,5,121,'Vertrieb','Vertreiber der Datenquelle',0),(505,6,94,'Originator','Party who created the resource',0),(505,6,121,'Herkunft','Ersteller der Datenquelle',0),(505,7,94,'Point of Contact','Party who can be contacted for acquiring knowledge about or acquisition of the resource',1),(505,7,121,'Auskunft','Kontaktperson, die Informationen über die Datenqulle geben kann',1),(505,8,94,'Principal Investigator','Key party responsible for gathering information and conducting research',0),(505,8,121,'Datenerfassung','Verantworlicher für die Zusammenstellung der Informationen und Durchführung von Forschung',0),(505,9,94,'Processor','Party who has processed the data in a manner such that the resource has been modified',0),(505,9,121,'Auswertung','Bearbeiter der Daten in einer Art, dass die Quelle modifiziert wurde.',0),(505,10,94,'Publisher','Party who published the resource',0),(505,10,121,'Herausgeber','Phblizierer der Datenquelle',0),(510,1,94,'ucs2','16-bit fixed size Universal Character Set, based on ISO/IEC 10646',0),(510,1,121,'ucs2','16-bit fixierte Größe Universal Character Set, basierend auf ISO/IEC 10646',0),(510,2,94,'ucs4','32-bit fixed size Universal Character Set, based on ISO/IEC 10646',0),(510,2,121,'ucs4','32-bit fixierte Größe Universal Character Set, basierend auf ISO/IEC 10646',0),(510,3,94,'utf7','7-bit variable size UCS Transfer Format, based on ISO/IEC 10646',0),(510,3,121,'utf7','7-bit variable Größe UCS Transfer Format, basierend auf ISO/IEC 10646',0),(510,4,94,'utf8','8-bit variable size UCS Transfer Format, based on ISO/IEC 10646',0),(510,4,121,'utf8','8-bit variable Größe UCS Transfer Format, basierend auf ISO/IEC 10646',0),(510,5,94,'utf16','16-bit variable size UCS Transfer Format, based on ISO/IEC 10646',0),(510,5,121,'utf16','16-bit variable Größe UCS Transfer Format, basierend auf ISO/IEC 10646',0),(510,6,94,'8859part1','latin-1, west European',0),(510,6,121,'8859part1','latin-1, Westeuropäischer',0),(510,7,94,'8859part2','latin-2, central European',0),(510,7,121,'8859part2','latin-2, Zentraleuropäischer',0),(510,8,94,'8859part3','latin-3, south European',0),(510,8,121,'8859part3','latin-3, Südeuropäischer',0),(510,9,94,'8859part4','latin-4, north European',0),(510,9,121,'8859part4','latin-4, Nordeuropäischer',0),(510,10,94,'8859part5','cyrillic code set',0),(510,10,121,'8859part5','Cyrillisch',0),(510,11,94,'8859part6','arabic code set',0),(510,11,121,'8859part6','Arabisch',0),(510,12,94,'8859part7','greek code set',0),(510,12,121,'8859part7','Griechisch',0),(510,13,94,'8859part8','hebrew code set',0),(510,13,121,'8859part8','Hebräisch',0),(510,14,94,'8859part9','latin-5, Turkish code set',0),(510,14,121,'8859part9','latin-5, Türkisch',0),(510,15,94,'8859part11','thai code set',0),(510,15,121,'8859part11','Tailändisch',0),(510,16,94,'8859part14','latin-8 code set',0),(510,16,121,'8859part14','latin-8',0),(510,17,94,'8859part15','latin-9 code set',0),(510,17,121,'8859part15','latin-9',0),(510,18,94,'Jis','japanese code set used for electronic transmission',0),(510,18,121,'Jis','Japanisch',0),(510,19,94,'ShiftJIS','japanese code set used on MS-DOS based machines',0),(510,19,121,'ShiftJIS','Japanisch (MS-DOS)',0),(510,20,94,'EucJP','japanese code set used on UNIX based machines',0),(510,20,121,'EucJP','Japanisch (UNIX)',0),(510,21,94,'UsAscii','united states ASCII code set (ISO 646 US)',0),(510,21,121,'UsAscii','USA ASCII',0),(510,22,94,'Ebcdic','ibm mainframe code set',0),(510,22,121,'Ebcdic','IBM Mainfraime',0),(510,23,94,'EucKR','korean code set',0),(510,23,121,'EucKR','Koreanisch',0),(510,24,94,'big5','taiwanese code set',0),(510,24,121,'big5','Tawainesisch',0),(515,1,94,'complexes','set of geometric primitives such that their boundaries can be represented as a union of other primitives',0),(515,1,121,'Komplex','Satz geometrischer Primitiver, wobei deren Grenzen durch ein Zusammenschluss anderer Primitiver gebildet werden können',0),(515,2,94,'composites','connected set of curves, solids or surfaces',0),(515,2,121,'Verbund','verbundener Satz von Kurven, Körpern oder Oberflächen',0),(515,3,94,'curve','bounded, 1-dimensional geometric primitive, representing the continuous image of a line',0),(515,3,121,'Kurve','beschränkte, 1-D geometrische Primitive, die ein continuierliches Bild einer Linie darstellt',0),(515,4,94,'point','zero-dimensional geometric primitive, representing a position but not having an extent',0),(515,4,121,'Punkt','0-D geometrische Primitive, welche eine Position beschreibt ohne eine Ausdehnung zu haben',0),(515,5,94,'solid','bounded, connected 3-dimensional geometric primitive, representing the continuous image of a region of space',0),(515,5,121,'Körper','begrenzte, verbundene 3-D geometrische Primitive, welche ein kontinuierliches Bild eines Raumes beschreibt',0),(515,6,94,'surface','bounded, connected 2-dimensional geometric, representing the continuous image of a region of a plane',0),(515,6,121,'Oberfläche','begrenzte, verbundene 2-D geometrische Primitive, welche ein kontinuierliches Bild einer Ebene oder Region beschreibt',0),(517,1,94,'Discipline','keyword identifies a branch of instruction or specialized learning',0),(517,1,121,'Wissenszweig','Zweig einer Ausbildung oder spezialisierten Lernens',0),(517,2,94,'Place','keyword identifies a location',0),(517,2,121,'Ortsangabe','Ortsangabe',0),(517,3,94,'Stratum','keyword identifies the layer(s) of any deposited substance',0),(517,3,121,'Schicht','Schichten einer hinterlegten Substanz',0),(517,4,94,'Temporal','keyword identifies a time period related to the dataset',0),(517,4,121,'Zeitraum','Zeitraum in Bezug auf den Datensatz',0),(517,5,94,'Theme','keyword identifies a particular subject or topic',0),(517,5,121,'Themenbereich','Spezielles Fachgebiet oder Thema',0),(518,1,94,'continual','data is repeatedly and frequently updated',1),(518,1,121,'kontinuierlich','Datenupdate  häufig',1),(518,2,94,'daily','Data is updated each day',0),(518,2,121,'täglich','Datenupdate täglich',0),(518,3,94,'weekly','Ddata is updated on a weekly basis',0),(518,3,121,'wöchentlich','Datenupdate wöchentlich',0),(518,4,94,'fortnightly','Data is updated every two weeks',0),(518,4,121,'vierzehntägig','Datenupdate alle 2 Wochen',0),(518,5,94,'monthly','Data is updated each month',0),(518,5,121,'monatlich','Datenupdate monatlich',0),(518,6,94,'quarterly','Data is updated every three months',0),(518,6,121,'vierteljährlich','Datenupdate alle 3 Monate',0),(518,7,94,'biannually','Data is updated twice each year',0),(518,7,121,'halbjährlich','Datenupdate halbjährlich',0),(518,8,94,'annually','Data is updated every year',0),(518,8,121,'jährlich','Datenupdate jährlich',0),(518,9,94,'as Needed','Data is updated as deemed necessary',0),(518,9,121,'bei Bedarf','Datenupdate wenn nötig',0),(518,10,94,'irregular','Data is updated in intervals that are uneven in duration',0),(518,10,121,'unregelmäßig','Datenupdate in unregelmäßigen Abständen',0),(518,11,94,'notPlanned','There are no plans to update the data',0),(518,11,121,'einmalig','Datenupdate nicht geplant',0),(518,12,94,'unknown','Frequency of maintenance for the data is not known',0),(518,12,121,'unbekannt','Datenupdate unbekannt',0),(520,1,94,'CD Rom','CD Rom',0),(520,1,121,'CD-ROM','CD-ROM',0),(520,2,94,'dvd','dvd',0),(520,2,121,'DVD','DVD',0),(520,3,94,'dvd Rom','dvd Rom',0),(520,3,121,'DVD-ROM','DVD-ROM',0),(520,4,94,'3.5-inch diskette','3\'\' Floppy',0),(520,4,121,'3,5-Zoll Diskette','3Â¿Â¿ Floppy',0),(520,5,94,'5.25-inch diskette','5 Â¼Â¿Â¿ Floppy',0),(520,5,121,'5,25-Zoll Diskette','5 Â¼Â¿Â¿ Floppy',0),(520,6,94,'7 trackTape','7 trackTape',0),(520,6,121,'7 Spur Band','7 Spur Tape',0),(520,7,94,'9 track Tape','9 track Tape',0),(520,7,121,'9 Spur Band','9 Spur Tape',0),(520,8,94,'3480 Cartridge','3480Cartridge',0),(520,8,121,'3480 Bandkassette','3480 Cartridge',0),(520,9,94,'3490 Cartridge','3490 Cartridge',0),(520,9,121,'3490 Bandkassette','3490 Cartridge',0),(520,10,94,'3580 Cartridge','3580Cartridge',0),(520,10,121,'3580 Bandkassette','3580 Cartridge',0),(520,11,94,'4mm Cartridge Tape','4mm Cartridge Tape',0),(520,11,121,'4mm Bandkassette','4mm Cartridge Tape',0),(520,12,94,'8mm Cartridge Tape','8mm Cartridge Tape',0),(520,12,121,'8mm Bandkassette','8mm Cartridge Tape',0),(520,13,94,'0.25-inch Cartridge Tape','Â¼ `Â¿ Cartridge Tape',0),(520,13,121,'1/4-Zoll Bandkassette','Â¼ `Â¿ Cartridge Tape',0),(520,14,94,'Digital Linear Tape','digital Linear Tape',0),(520,14,121,'Digitales Band','Digitales Tape',0),(520,15,94,'Online','online',0),(520,15,121,'Online Link','Online Link',0),(520,16,94,'Satellite','Satellite',0),(520,16,121,'Satellitenverbindung','Satellitenverbindung',0),(520,17,94,'Telephone Link','telephone Link',0),(520,17,121,'Telefon','Telefonverbindung',0),(520,18,94,'Hardcopy','hardcopy',1),(520,18,121,'Papier','Kopie (Papier), Druckerzeugnisse usw.',1),(520,900001,94,'unknown','-',0),(520,900001,121,'unbekannt (*)','für Altdatenübernahme',0),(520,900002,94,'faxback','faxback',0),(520,900002,121,'Faxabruf','Faxabruf',0),(520,900003,94,'mobile radio','mobile radio',0),(520,900003,121,'Mobilfunk','Mobilfunk',0),(520,900004,94,'broadcast','broadcast',0),(520,900004,121,'Rundfunk','Rundfunk und Fernsehen',0),(520,900005,94,'video text','video text',0),(520,900005,121,'Videotext','Videotext',0),(520,900006,94,'Analog Photography','-',0),(520,900006,121,'analoge Fotografie','-',0),(520,900007,94,'microfilm','-',0),(520,900007,121,'Mikrofilm','-',0),(520,900008,94,'Zip Drive','-',0),(520,900008,121,'ZIP-Laufwerk','-',0),(520,900009,94,'e-mail','-',0),(520,900009,121,'E-Mail','-',0),(520,900010,94,'Info Kiosk','-',0),(520,900010,121,'Infokiosk','-',0),(523,1,94,'completed','production of the data has been completed',1),(523,1,121,'abgeschlossen','Datenproduktion ist abgeschlossen',1),(523,2,94,'historicalArchive','Data has been stored in an offline storage facility',0),(523,2,121,'archiviert','Die Daten sind in einem Archiv abgelegt',0),(523,3,94,'obsolete','Data is no longer relevant',0),(523,3,121,'obsolet','Die Daten sind nicht mehr länger relevant',0),(523,4,94,'onGoing','Data is continually being updated',0),(523,4,121,'kontinuierlich','Daten werden kontinuierlich erneuert',0),(523,5,94,'planned','fixed date has been established upon or by which the data will be created or updated',0),(523,5,121,'geplant','Ein Zeitpunkt wurde festgelegt, an dem die Daten kreiert werden',0),(523,6,94,'required','Data needs to be generated or updated',0),(523,6,121,'benötigt','Die Daten müssen generiert bzw. erneuert werden',0),(523,7,94,'underdevelopment','Data is currently in the process of being created',0),(523,7,121,'in Erstellung','Die Daten werden gerade kreiert',0),(523,900001,94,'inOperation','-',0),(523,900001,121,'in Produktion','Informationssystem als produktiv genutztes System',0),(525,5,94,'Dataset','information applies to the dataset',1),(525,5,121,'Datensatz','Information zu dem Datensatz',1),(525,6,94,'Dataseries','information applies to the series',0),(525,6,121,'Datenserie','Information zu der Datenserie',0),(526,1,94,'vector','vector data is used to represent geographic data',0),(526,1,121,'Vektor','Geographische Daten werden mittels Verktordaten präsentiert',0),(526,2,94,'grid','grid data is used to represent geographic data',0),(526,2,121,'Rasterdaten','Geographische Daten werden mittels Rasterdaten dargestellt',0),(526,3,94,'textTable','textual or tabular data is used to represent geographic data',0),(526,3,121,'Texttabelle','Geographische Daten werden mittels Texttabellen dargestellt',0),(526,4,94,'TIN','triangulated irregular network',0),(526,4,121,'TIN','Geographische Daten werden mittels eines trinangulären irregulären Netzes dargestellt dargestellt',0),(526,5,94,'stereoModel','three-dimensional view formed by the intersecting homologous rays of an overlapping pair of images',0),(526,5,121,'Stereomodell','Geographische Daten werden mittels eines stereosehenden Models dargestellt',0),(526,6,94,'video','scene from a video recording',0),(526,6,121,'Video','Geographische Daten werden mittels einer Videoaufnahme dargestellt',0),(527,1,94,'Farming','rearing of animals and/or cultivation of plants',0),(527,1,121,'Landwirtschaft','Landwirtschaft (Tierzucht oder Pflanzenanbau)',0),(527,2,94,'Biota','flora and/or fauna in natural environment. Examples: wildlife, vegetation, biological sciences, ecology, wilderness, sealife, wetlands, habitat',0),(527,2,121,'Flora, Fauna, Habitate','Flora und Fauna in der natürlichen Umwelt',0),(527,3,94,'Boundaries','legal land descriptions. Examples: political and administrative boundaries',0),(527,3,121,'Grenzen','Rechtl. Beschreibungen und Grenzen von Land',0),(527,4,94,'Climatology MeteorologyAtmosphere','processes and phenomena of the atmosphere. Examples: cloud cover, weather, climate, atmospheric conditions, climate change, precipitation',0),(527,4,121,'Klima und Atmosphäre','Prozesse und Phänomene der Atmosphäre',0),(527,5,94,'Economy','economic activities, conditions and employment. Examples: production, labour, revenue, commerce, industry, tourism and ecotourism, forestry, fisheries, commercial or subsistence hunting, etc.',0),(527,5,121,'Ökonomie','Ökonomische Aktivitäten, Konditionen und beschäftigung',0),(527,6,94,'Elevation','height above or below sea level. Examples: altitude, bathymetry, digital elevation models, slope, derived products',0),(527,6,121,'Höhe','Höhe über oder unter NN',0),(527,7,94,'Environment','environmental resources, protection and conservation. Examples: environmental pollution, waste storage and treatment, environmental impact assessment, monitoring environmental risk, nature reserves, landscape',1),(527,7,121,'Umwelt','Umweltresourcen, -schutz und -erhaltung',1),(527,8,94,'Geoscientific Information','information pertaining to earth sciences. Examples: geophysical features and processes, geology, minerals, sciences dealing with the composition, structure and origin of the earthÂ¿s rocks, risks of earthquakes',0),(527,8,121,'Geowissenschaft','Information die Geowissenshaften betreffend',0),(527,9,94,'Health','health, health services, human ecology, and safety. Examples: disease and illness, factors affecting health, hygiene, substance abuse, mental and physical health, health services',0),(527,9,121,'Gesundheit','Gesundheit, Gesundheitsservices, Humanökologie und Sicherheit',0),(527,10,94,'Imagery Base Maps EarthCover','base maps. Examples: land cover, topographic maps, imagery, unclassified images, annotations',0),(527,10,121,'Karten','Karten',0),(527,11,94,'Intelligence Military','military bases, structures, activities. Examples: barracks, training grounds, military transportation, information',0),(527,11,121,'Militär','Militärische Stützpunkte, Strikturen und Aktivitäten',0),(527,12,94,'Inland Waters','inland water features, drainage systems and their characteristics. Examples: rivers and glaciers, salt lakes, water utilization plans, dams, currents, floods, water quality, hydrographic charts',0),(527,12,121,'Binnengewässer','Inlandwasser, Abflüsse und deren Eigenschaften und Charakteristika',0),(527,13,94,'Location','positional information and services. Examples: addresses, geodetic networks, control points, postal zones and services, place names',0),(527,13,121,'Ortsangabe','Ortsangaben und -services',0),(527,14,94,'Oceans','features and characteristics of salt water bodies (excluding inland waters): Examples: tides, tidal waves, coastal information, reefs',0),(527,14,121,'Ozeane','Ozeane und deren Charakteristika und Eigenschaften',0),(527,15,94,'PlanningCadastre','information used for appropriate actions for future use of the land Examples: land use maps, zoning maps, cadastral surveys, land ownership',0),(527,15,121,'Kataster','Informationen zu angemessenem Handeln für die zukünftige Nutzung des Landes',0),(527,16,94,'Society','characteristics of society and cultures. Examples: settlements, anthropology, archaeology, education, traditionalbeliefs, manners and customs, demographic data, recreational areas and activities,',0),(527,16,121,'Gesellschaft','Charakteristika von Gesellschaft und Kulturen',0),(527,17,94,'Structure','man-made construction. Examples: buildings, museums, churches, factories, housing, monuments, shops, towers',0),(527,17,121,'Bau','Kontsruktionen (von Menschen geshaffen)',0),(527,18,94,'Transportation','means and aids for conveying persons and/or goods. Examples: roads, airports/airstrips, shipping routes, tunnels, nautical charts, vehicle or vessel location, aeronautical charts, railways',0),(527,18,121,'Transport','Hilfsmittel und Geräte zum Befördern von Personen und/oder Gütern',0),(527,19,94,'Utilities Communication','energy, water and waste systems and communications infrastructure and services. Examples: hydroelectricity, geothermal, solar and nuclear sources of energy, water purification and distribution, sewage collection and disposal, etc.',0),(527,19,121,'Energie und Kommunikation','Energie-, Wasser- und Abfallsysteme sowie Kommunikationsinfrastruktur und Services.',0),(528,1,94,'geometryOnly','geometry objects without any additional structure which describes topology',1),(528,1,121,'geometrisch','Geometrisch Objekte ohn jegliche Struktur, die die Topologie beschreibt',1),(528,2,94,'topology1D','1-dimensional topological complex',0),(528,2,121,'Topologie (1D)','1-dimensionaler topologischer Komplex',0),(528,3,94,'planarGraph','1-dimensional topological complex which is planar',0),(528,3,121,'Planar (1D)','1-dimensionaler topologischer Komplex, der eine Fläche ist',0),(528,4,94,'fullPlanarGraph','2-dimensional topological complex which is planar',0),(528,4,121,'Planar (2D)','2-dimensionaler topologischer Komplex, der eine Fläche ist',0),(528,5,94,'surfaceGraph','1-dimensional topological complex which is isomorphic to a subset of a surface',0),(528,5,121,'Oberflächengraph (1D)','1-dimensionaler topologischer Komplex, der isomorph zu einer Untermenge einer Oberfläche ist',0),(528,6,94,'fullSurfaceGraph','2-dimensional topological complex which is isomorphic to a subset of a surface',0),(528,6,121,'Oberfläche(2D)','2-dimensionaler topologischer Komplex, der isomorph zu einer Untermenge einer Oberfläche ist',0),(528,7,94,'topology3D','3-dimensional topological complex',0),(528,7,121,'Topologie (3D)','3-dimensionaler topologischer Komplex',0),(528,8,94,'fullTopology3D','complete coverage of a 3D coordinate space',0),(528,8,121,'Raum (3D)','Komplette Abdeckung eines 3-D Koordinatenraumes',0),(528,9,94,'abstract','topological complex without any specified geometric realization',0),(528,9,121,'Abstrakte Topologie','Topologischer Komplex ohne jegliche spezifische geometrische Realisierung',0),(99999999,94,94,'English','-',0),(99999999,94,121,'Englisch','-',0),(99999999,121,94,'German','-',1),(99999999,121,121,'Deutsch','-',1);
/*!40000 ALTER TABLE `ingrid_codelist_domain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_env_funct_category`
--

DROP TABLE IF EXISTS `ingrid_env_funct_category`;
CREATE TABLE `ingrid_env_funct_category` (
  `id` mediumint(9) NOT NULL auto_increment,
  `form_value` varchar(255) NOT NULL,
  `query_value` varchar(255) NOT NULL,
  `sortkey` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7;

--
-- Dumping data for table `ingrid_env_funct_category`
--

LOCK TABLES `ingrid_env_funct_category` WRITE;
/*!40000 ALTER TABLE `ingrid_env_funct_category` DISABLE KEYS */;
INSERT INTO `ingrid_env_funct_category` VALUES (1,'rec','rechtliches',1),(2,'kon','konzeptionelles',2),(3,'sta','statusberichte',3),(4,'umw','umweltzustand',4),(5,'dat','datenkarten',5),(6,'ris','risikobewertungen',6);
/*!40000 ALTER TABLE `ingrid_env_funct_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_env_topic`
--

DROP TABLE IF EXISTS `ingrid_env_topic`;
CREATE TABLE `ingrid_env_topic` (
  `id` mediumint(9) NOT NULL auto_increment,
  `form_value` varchar(255) NOT NULL,
  `query_value` varchar(255) NOT NULL,
  `sortkey` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=22;

--
-- Dumping data for table `ingrid_env_topic`
--

LOCK TABLES `ingrid_env_topic` WRITE;
/*!40000 ALTER TABLE `ingrid_env_topic` DISABLE KEYS */;
INSERT INTO `ingrid_env_topic` VALUES (1,'abf','abfall',1),(2,'alt','altlasten',2),(3,'bau','bauen',3),(4,'bod','boden',4),(5,'che','chemikalien',5),(6,'ene','energie',6),(7,'for','forstwirtschaft',7),(8,'gen','gentechnik',8),(9,'geo','geologie',9),(10,'ges','gesundheit',10),(11,'lae','laermerschuetterung',11),(12,'lan','landwirtschaft',12),(13,'luf','luftklima',13),(14,'nac','nachhaltigeentwicklung',14),(15,'nat','naturlandschaft',15),(16,'str','strahlung',16),(17,'tie','tierschutz',17),(18,'uin','umweltinformation',18),(19,'uwi','umweltwirtschaft',19),(20,'ver','verkehr',20),(21,'was','wasser',21);
/*!40000 ALTER TABLE `ingrid_env_topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_lookup`
--

DROP TABLE IF EXISTS `ingrid_lookup`;
CREATE TABLE `ingrid_lookup` (
  `id` bigint(20) NOT NULL auto_increment,
  `item_key` varchar(255) default NULL,
  `item_value` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2;

--
-- Dumping data for table `ingrid_lookup`
--

LOCK TABLES `ingrid_lookup` WRITE;
/*!40000 ALTER TABLE `ingrid_lookup` DISABLE KEYS */;
INSERT INTO `ingrid_lookup` VALUES (1,'ingrid_db_version','1');
/*!40000 ALTER TABLE `ingrid_lookup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_measures_rubric`
--

DROP TABLE IF EXISTS `ingrid_measures_rubric`;
CREATE TABLE `ingrid_measures_rubric` (
  `id` mediumint(9) NOT NULL auto_increment,
  `form_value` varchar(255) NOT NULL,
  `query_value` varchar(255) NOT NULL,
  `sortkey` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=5;

--
-- Dumping data for table `ingrid_measures_rubric`
--

LOCK TABLES `ingrid_measures_rubric` WRITE;
/*!40000 ALTER TABLE `ingrid_measures_rubric` DISABLE KEYS */;
INSERT INTO `ingrid_measures_rubric` VALUES (1,'str','radiation',1),(2,'luf','air',2),(3,'was','water',3),(4,'wei','misc',4);
/*!40000 ALTER TABLE `ingrid_measures_rubric` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_newsletter_data`
--

DROP TABLE IF EXISTS `ingrid_newsletter_data`;
CREATE TABLE `ingrid_newsletter_data` (
  `id` mediumint(9) NOT NULL auto_increment,
  `firstname` varchar(255) default NULL,
  `lastname` varchar(255) default NULL,
  `email` varchar(255) default NULL,
  `created` timestamp NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `IX_email_1` (`email`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ingrid_newsletter_data`
--

LOCK TABLES `ingrid_newsletter_data` WRITE;
/*!40000 ALTER TABLE `ingrid_newsletter_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `ingrid_newsletter_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_partner`
--

DROP TABLE IF EXISTS `ingrid_partner`;
CREATE TABLE `ingrid_partner` (
  `id` mediumint(9) NOT NULL auto_increment,
  `ident` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `sortkey` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `IX_ingrid_partner_1` (`sortkey`)
) ENGINE=MyISAM AUTO_INCREMENT=18;

--
-- Dumping data for table `ingrid_partner`
--

LOCK TABLES `ingrid_partner` WRITE;
/*!40000 ALTER TABLE `ingrid_partner` DISABLE KEYS */;
INSERT INTO `ingrid_partner` VALUES (1,'bund','Bund',1),(2,'bw','Baden-Württemberg',2),(3,'by','Bayern',3),(4,'be','Berlin',4),(5,'bb','Brandenburg',5),(6,'hb','Bremen',6),(7,'hh','Hamburg',7),(8,'he','Hessen',8),(9,'mv','Mecklenburg-Vorpommern',9),(10,'ni','Niedersachsen',10),(11,'nw','Nordrhein-Westfalen',11),(12,'rp','Rheinland-Pfalz',12),(13,'sl','Saarland',13),(14,'sn','Sachsen',14),(15,'st','Sachsen-Anhalt',15),(16,'sh','Schleswig-Holstein',16),(17,'th','Thüringen',17);
/*!40000 ALTER TABLE `ingrid_partner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_principal_pref`
--

DROP TABLE IF EXISTS `ingrid_principal_pref`;
CREATE TABLE `ingrid_principal_pref` (
  `id` mediumint(9) NOT NULL auto_increment,
  `principal_name` varchar(251) NOT NULL,
  `pref_name` varchar(251) NOT NULL,
  `pref_value` mediumtext,
  `modified_date` timestamp NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ingrid_principal_pref`
--

LOCK TABLES `ingrid_principal_pref` WRITE;
/*!40000 ALTER TABLE `ingrid_principal_pref` DISABLE KEYS */;
/*!40000 ALTER TABLE `ingrid_principal_pref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_provider`
--

DROP TABLE IF EXISTS `ingrid_provider`;
CREATE TABLE `ingrid_provider` (
  `id` mediumint(9) NOT NULL auto_increment,
  `ident` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `sortkey` mediumint(9) NOT NULL,
  `sortkey_partner` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `IX_ingrid_provider_1` (`sortkey_partner`),
  KEY `IX_ingrid_provider_2` (`sortkey`),
  KEY `IX_ingrid_provider_3` (`ident`)
) ENGINE=MyISAM AUTO_INCREMENT=142;

--
-- Dumping data for table `ingrid_provider`
--

LOCK TABLES `ingrid_provider` WRITE;
/*!40000 ALTER TABLE `ingrid_provider` DISABLE KEYS */;
INSERT INTO `ingrid_provider` VALUES (1,'bu_bmu','Bundesministerium für Umwelt, Naturschutz und Reaktorsicherheit','http://www.bmu.de/',1,1),(2,'bu_uba','Umweltbundesamt','http://www.umweltbundesamt.de/',2,1),(3,'bu_bfn','Bundesamt für Naturschutz','http://www.bfn.de/',3,1),(4,'bu_bfs','Bundesamt für Strahlenschutz','http://www.bfs.de/',4,1),(5,'bu_bmf','Bundesministerium der Finanzen','http://www.bundesfinanzministerium.de/',5,1),(6,'bu_bmbf','Bundesministerium für Bildung und Forschung','http://www.bmbf.de/',6,1),(7,'bu_bmelv','Bundesministerium für Ernährung, Landwirtschaft und Verbraucherschutz','http://www.bmelv.de/cln_044/DE/00-Home/__Homepage__node.html__nnn=true',7,1),(8,'bu_bmz','Bundesministerium für wirtschaftliche Zusammenarbeit und Entwicklung','http://www.bmz.de/',8,1),(9,'bu_aa','Auswärtiges Amt','http://www.auswaertiges-amt.de/',10,1),(10,'bu_bsh','Bundesamt für Seeschifffahrt und Hydrographie','http://www.bsh.de/',11,1),(11,'bu_bvl','Bundesamt für Verbraucherschutz und Lebensmittelsicherheit','http://www.bvl.bund.de/',12,1),(12,'bu_bgr','Bundesanstalt für Geowissenschaften und Rohstoffe','http://www.bgr.bund.de/',13,1),(13,'bu_bfg','Bundesanstalt für Gewässerkunde','http://www.bafg.de/',14,1),(14,'bu_nokis','Bundesanstalt für Wasserbau - Dienststelle Hamburg','http://www.hamburg.baw.de/',15,1),(15,'bu_bfr','Bundesinstitut für Risikobewertung','http://www.bfr.bund.de/',16,1),(16,'bu_bka','Bundeskriminalamt','http://www.bka.de/',17,1),(17,'bu_rki','Robert-Koch-Institut','http://www.rki.de/',18,1),(18,'bu_stba','Statistisches Bundesamt','http://www.destatis.de/',19,1),(19,'bu_ble','Bundesanstalt für Landwirtschaft und Ernährung','http://www.ble.de',20,1),(20,'bu_bpb','Bundeszentrale für politische Bildung','http://www.bpb.de/',21,1),(21,'bu_gtz','Deutsche Gesellschaft für Technische Zusammenarbeit (GTZ) GmbH','http://www.gtz.de/',22,1),(22,'bu_dwd','Deutscher Wetterdienst','http://www.dwd.de/',23,1),(23,'bu_dlr','Deutsches Zentrum für Luft- und Raumfahrt DLR e.V.','http://www.dlr.de/',24,1),(24,'bu_kug','Koordinierungsstelle PortalU','http://www.kst.portalu.de/',25,1),(25,'bu_labo','Länderarbeitsgemeinschaft Boden LABO','http://www.labo-deutschland.de/',26,1),(26,'bu_lawa','Länderarbeitsgemeinschaft Wasser','http://www.lawa.de/',27,1),(27,'bu_laofdh','Leitstelle des Bundes für Abwassertechnik, Boden- und Grundwasserschutz, Kampfmittelräumung und das Liegenschaftsinformationssystem Außenanlagen LISA','http://www.ofd-hannover.de/la/',28,1),(28,'bu_bpa','Presse- und Informationsamt der Bundesregierung','http://www.bundesregierung.de/',29,1),(29,'bu_blauerengel','RAL/Umweltbundesamt Umweltzeichen \"Blauer Engel\"','http://www.blauer-engel.de/',30,1),(30,'bu_sru','Rat von Sachverständigen für Umweltfragen (SRU)','http://www.umweltrat.de/',31,1),(31,'bu_ssk','Strahlenschutzkommission','http://www.ssk.de/',32,1),(32,'bu_umk','Umweltministerkonferenz','http://www.umweltministerkonferenz.de/',33,1),(33,'bu_wbgu','Wissenschaftlicher Beirat der Bundesregierung Globale Umweltveränderungen - WBGU','http://www.wbgu.de/',34,1),(34,'bu_agenda','Agenda-Transfer. Agentur für Nachhaltigkeit GmbH','http://www.agenda-transfer.de/',35,1),(35,'bu_uga','Umweltgutachterausschuss (UGA)','http://www.uga.de/',36,1),(36,'bu_co2','co2online gGmbH Klimaschutzkampagne','http://www.co2online.de/',37,1),(37,'bu_dekade','Weltdekade ?Bildung für nachhaltige Entwicklung?','http://www.dekade.org/index.htm',38,1),(38,'bw_um','Umweltministerium Baden-Württemberg','http://www.um.baden-wuerttemberg.de/',1,2),(39,'bw_mi','Innenministerium Baden-Württemberg','http://www.innenministerium.baden-wuerttemberg.de/',2,2),(40,'bw_mlr','Ministerium für Ernährung und Ländlichen Raum Baden-Württemberg','http://www.mlr.baden-wuerttemberg.de/',3,2),(41,'bw_mw','Wirtschaftsministerium Baden-Württemberg','http://www.wm.baden-wuerttemberg.de/',4,2),(42,'bw_lu','Landesanstalt für Umwelt, Messungen und Naturschutz Baden-Württemberg','http://www.lubw.baden-wuerttemberg.de/',5,2),(43,'bw_lgrb','Regierungspräsidium Freiburg - Abt. 9 Landesamt für Geologie, Rohstoffe und Boden','http://www.lgrb.uni-freiburg.de/lgrb/home/index_html',6,2),(44,'bw_lvm','Landesvermessungsamt Baden-Württemberg','http://www.lv-bw.de/lvshop2/index.htm',7,2),(45,'bw_lel','Informationsdienst der Landwirtschaftsverwaltung Baden-Württemberg','http://www.landwirtschaft-mlr.baden-wuerttemberg.de/servlet/PB/-s/153h2f7ajcsu0yhrl7ib9karcotj9kv/menu/1034707_l1/index.html',8,2),(46,'bw_gaa','Gewerbeaufsicht Baden-Württemberg','http://www.gewerbeaufsicht.baden-wuerttemberg.de/',9,2),(47,'bw_stla','Statistisches Landesamt Baden-Württemberg','http://www.statistik-bw.de/',10,2),(48,'bw_fzk','Forschungszentrum Karlsruhe GmbH','http://www.fzk.de/',11,2),(49,'by_sugv','Bayerisches Staatsministerium für Umwelt, Gesundheit und Verbraucherschutz','http://www.stmugv.bayern.de/',1,3),(50,'by_gla','Bayerisches Geologisches Landesamt','http://www.geologie.bayern.de/',2,3),(51,'by_lfstad','Bayerisches Landesamt für Statistik und Datenverarbeitung','http://www.statistik.bayern.de/',3,3),(52,'by_lfu','Bayerisches Landesamt für Umweltschutz','http://www.bayern.de/lfu/',4,3),(53,'by_lfw','Bayerisches Landesamt für Wasserwirtschaft','http://www.bayern.de/lfw',5,3),(54,'by_brrhoen','Biosphärenreservat Rhön','http://www.biosphaerenreservat-rhoen.de/',6,3),(55,'by_npbayw','Nationalpark Bayerischer Wald','http://www.nationalpark-bayerischer-wald.de/',7,3),(56,'by_npbg','Nationalpark Berchtesgaden','http://www.nationalpark-berchtesgaden.de/',8,3),(57,'be_senst','Senatsverwaltung für Stadtentwicklung','http://www.stadtentwicklung.berlin.de/umwelt/',1,4),(58,'be_snb','Stiftung Naturschutz Berlin','http://www.stiftung-naturschutz.de/',2,4),(59,'be_uacw','Amt für Umwelt, Natur und Verkehr Charlottenburg-Wilmersdorf','http://www.charlottenburg-wilmersdorf.de/umweltamt',3,4),(60,'bb_mluv','Ministerium für Ländliche Entwicklung, Umwelt und Verbraucherschutz des Landes Brandenburg','http://www.mluv.brandenburg.de/',1,5),(61,'hb_sbu','Senator für Bau und Umwelt Bremen','http://www.umwelt.bremen.de/buisy/scripts/buisy.asp',1,6),(62,'hh_su','Behörde für Stadtentwicklung und Umwelt Hamburg','http://fhh.hamburg.de/stadt/Aktuell/behoerden/stadtentwicklung-umwelt/start.html',1,7),(63,'hh_wa','Behörde für Wirtschaft und Arbeit Hamburg','http://fhh.hamburg.de/stadt/Aktuell/behoerden/wirtschaft-arbeit/start.html',2,7),(64,'hh_bsg','Behörde für Soziales, Familie, Gesundheit und Verbraucherschutz','http://fhh.hamburg.de/stadt/Aktuell/behoerden/bsg/start.html',3,7),(65,'hh_lgv','Landesbetrieb Geoinformation und Vermessung Hamburg','http://fhh.hamburg.de/stadt/Aktuell/weitere-einrichtungen/landesbetrieb-geoinformation-und-vermessung',4,7),(66,'hh_npwatt','Nationalpark Hamburgisches Wattenmeer','http://fhh1.hamburg.de/Behoerden/Umweltbehoerde/nphw/',5,7),(67,'hh_argeelbe','Arbeitsgemeinschaft für die Reinhaltung der Elbe','http://www.arge-elbe.de/',6,7),(68,'hh_sth','Statistisches Amt für Hamburg und Schleswig-Holstein','http://www.statistik-nord.de/index.php?id=32',7,7),(69,'he_hmulv','Hessisches Ministerium für Umwelt, ländlichen Raum und Verbraucherschutz','http://www.hmulv.hessen.de/',1,8),(70,'he_hlug','Hessisches Landesamt für Umwelt und Geologie','http://www.hlug.de/',2,8),(71,'he_umwelt','Umweltatlas Hessen','http://atlas.umwelt.hessen.de/',3,8),(72,'mv_um','Umweltministerium Mecklenburg-Vorpommern','http://www.um.mv-regierung.de/',1,9),(73,'mv_sm','Sozialministerium Mecklenburg-Vorpommern','http://www.sozial-mv.de/',2,9),(74,'mv_lung','Landesamt für Umwelt, Naturschutz und Geologie Mecklenburg-Vorpommern (LUNG)','http://www.lung.mv-regierung.de/',3,9),(75,'mv_lfmv','Landesforst Mecklenburg-Vorpommern AöR','http://www.wald-mv.de/',4,9),(76,'mv_schaalsee','Amt für das Biosphärenreservat Schaalsee','http://www.schaalsee.de/',5,9),(77,'mv_npmueritz','Nationalparkamt Müritz','http://www.nationalpark-mueritz.de/',6,9),(78,'mv_nvblmv','Nationalparkamt Vorpommersche Boddenlandschaft','http://www.nationalpark-vorpommersche-bodde/nlandschaft.de/',7,9),(79,'mv_fhstr','Fachhochschule Stralsund','http://www.fh-stralsund.de/',8,9),(80,'mv_fhnb','Hochschule Neubrandenburg','http://www.fh-nb.de/',9,9),(81,'mv_hswi','Hochschule Wismar','http://www.hs-wismar.de/',10,9),(82,'mv_iow','Institut für Ostseeforschung Warnemünde','http://www.io-warnemuende/.de/',11,9),(83,'mv_unigr','Universität Greifswald','http://www.uni-greifswald.de/',12,9),(84,'mv_uniro','Universität Rostock','http://www.uni-rostock.de/',13,9),(85,'ni_mu','Niedersächsisches Umweltministerium','http://www.mu.niedersachsen.de/',1,10),(86,'ni_mw','Niedersächsisches Ministerium für Wirtschaft, Arbeit und Verkehr','http://www.mw.niedersachsen.de/',2,10),(87,'ni_ms','Niedersächsisches Ministerium für Soziales, Frauen, Familie und Gesundheit','http://www.ms.niedersachsen.de/',3,10),(88,'ni_mi','Niedersächsisches Ministerium für Inneres und Sport','http://www.mi.niedersachsen.de/',4,10),(89,'ni_ml','Niedersächsisches Ministerium für den ländlichen Raum, Ernährung, Landwirtschaft und Verbraucherschutz','http://www.ml.niedersachsen.de/',5,10),(90,'ni_nlwkn','Niedersächsischer Landesbetrieb für Wasserwirtschaft, Küsten- und Naturschutz','http://www.nlwkn.de/',6,10),(91,'ni_lbeg','Landesamt für Bergbau, Energie und Geologie','http://www.lbeg.niedersachsen.de/master/C17456312_L20_D0.html',7,10),(92,'ni_nls','Niedersächsisches Landesamt für Statistik','http://www.nls.niedersachsen.de/',8,10),(93,'ni_laves','Niedersächsisches Landesamt für Verbraucherschutz und Lebensmittelsicherheit','http://www.laves.niedersachsen.de/',9,10),(94,'ni_lga','Niedersächsisches Landesgesundheitsamt','http://www.nlga.niedersachsen.de/',10,10),(95,'ni_sbv','Niedersächsische Landesbehörde für Straßenbau und Verkehr','http://www.strassenbau.niedersachsen.de/',11,10),(96,'ni_nna','Alfred Toepfer Akademie für Naturschutz','http://www.nna.de/',12,10),(97,'ni_gll','Behörden für Geoinformation, Landentwicklung und Liegenschaften Niedersachsen','http://www.gll.niedersachsen.de',13,10),(98,'ni_gga','Institut für Geowissenschaftliche Gemeinschaftsaufgaben ','http://www.gga-hannover.de',14,10),(99,'ni_lgn','Landesvermessung und Geobasisinformation Niedersachsen','http://www.lgn.niedersachsen.de/',15,10),(100,'ni_lwkh','Landwirtschaftskammer Hannover','http://www.lwk-hannover.de/',16,10),(101,'ni_npwatt','Nationalpark Wattenmeer','http://www.nationalpark-wattenmeer.niedersachsen.de/master/C5912120_L20_D0.html',17,10),(102,'ni_npharz','Nationalparkverwaltung Harz','http://www.nationalpark-harz.de/',18,10),(103,'ni_gaa','Niedersächsische Gewerbeaufsicht','http://www.gewerbeaufsicht.niedersachsen.de/master/C1717445_N1717446_L20_D0_I1717444.html',19,10),(104,'ni_elbtal','Biosphärenreservat Elbtalaue','http://www.elbtalaue.niedersachsen.de/master/C6933729_L20_D0.html',20,10),(105,'nw_munlv','Ministerium für Umwelt und Naturschutz, Landwirtschaft und Verbraucherschutz des Landes Nordrhein-Westfalen','http://www.munlv.nrw.de/',1,11),(106,'nw_lua','Landesumweltamt Nordrhein-Westfalen','http://www.lua.nrw.de/',2,11),(107,'nw_loebf','Landesanstalt für Ökologie, Bodenordnung und Forsten des Landes Nordrhein-Westfalen (LÖBF)','http://www.loebf.nrw.de/',3,11),(108,'nw_gd','Geologischer Dienst Nordrhein-Westfalen','http://www.gd.nrw.de/',4,11),(109,'nw_stuaha','Staatliches Umweltamt Hagen','http://www.stua-ha.nrw.de/',5,11),(110,'nw_stualp','Staatliches Umweltamt Lippstadt','http://www.stua-lp.nrw.de/',6,11),(111,'nw_ldvst','Landesamt für Datenverarbeitung und Statistik Nordrhein-Westfalen','http://www.ugrdl.de/index.html',7,11),(112,'rp_mufv','Ministerium für Umwelt, Forsten und Verbraucherschutz Rheinland-Pfalz','http://www.mufv.rlp.de/',1,12),(115,'rp_forst','Landesforsten Rheinland-Pfalz','http://www.wald-rlp.de/',2,12),(116,'rp_lzu','Landeszentrale für Umweltaufklärung Rheinland-Pfalz','http://www.umdenken.de/',3,12),(117,'sl_mfu','Ministerium für Umwelt Saarland','http://www.umwelt.saarland.de/',1,13),(118,'sl_lua','Landesamt für Umwelt- und Arbeitsschutz Saarland','http://www.lua.saarland.de/',2,13),(119,'sn_smul','Sächsisches Staatsministerium für Umwelt und Landwirtschaft','http://www.umwelt.sachsen.de/de/wu/umwelt/index.html',1,14),(120,'sn_lfug','Sächsisches Landesamt für Umwelt und Geologie','http://www.umwelt.sachsen.de/lfug/',2,14),(121,'sn_lanu','Sächsische Landesstiftung Natur und Umwelt','http://www.lanu.de/templates/intro.php',3,14),(122,'st_mlu','Ministerium für Landwirtschaft und Umwelt Sachsen-Anhalt','http://www.sachsen-anhalt.de/rcs/LSA/pub/Ch1/fld8311011390180834/mainfldmtgpollxof/pgnotsi64lb6/index.jsp',1,15),(123,'st_lau','Landesamt für Umweltschutz Sachsen-Anhalt','http://www.mu.sachsen-anhalt.de/lau/de/fault.htm',2,15),(124,'st_lue','Luftüberwachungssystem Sachsen-Anhalt','http://www.mu.sachsen-anhalt.de/lau/luesa/',3,15),(125,'st_unimd','Otto-von-Guericke Universität Magdeburg','http://www.uni-magde/burg.de/',4,15),(126,'sh_munl','Ministerium für Landwirtschaft, Umwelt und ländliche Räume des Landes Schleswig-Holstein','http://www.umwelt.schleswig-holstein.de/servlet/is/154/',1,16),(127,'sh_lanu','Landesamt für Natur und Umwelt Schleswig-Holstein','http://www.lanu.landsh.de/',2,16),(128,'sh_luesh','Lufthygienische Überwachung Schleswig-Holstein','http://www.umwelt.schleswig-holstein.de/?196',3,16),(129,'sh_kfue','Kernreaktorfernüberwachung Schleswig-Holstein','http://www.kfue-sh.de/',4,16),(130,'sh_umweltaka','Akademie für Natur und Umwelt des Landes Schleswig-Holstein','http://www.umweltakademie-sh.de/',5,16),(131,'sh_npa','Nationalpark Schleswig-Holsteinisches Wattenmeer','http://www.wattenmeer-nationalpark.de/',6,16),(132,'th_tmlnu','Thüringer Ministerium für Landwirtschaft, Naturschutz und Umwelt','http://www.thueringen.de/de/tmlnu/',1,17),(133,'th_tlug','Thüringer Landesanstalt für Umwelt und Geologie','http://www.tlug-jena.de/index.html',2,17),(134,'ni_geomdk','geoMDK Niedersachsen','http://www.geomdk.niedersachsen.de/',21,10),(137,'rp_luwg','Landesamt für Umwelt, Wasserwirtschaft und Gewerbeaufsicht Rheinland-Pfalz','http://www.luwg.rlp.de/',4,12),(138,'rp_lua','Landesuntersuchungsamt Rheinland-Pfalz','http://www.lua.rlp.de/',5,12),(139,'bw_saa','SAA Sonderabfallagentur','http://www.saa.de/',12,2),(140,'bw_rp','Regierungspräsidien Baden-Württemberg','http://www.rp.baden-wuerttemberg.de/',13,2),(141,'bw_rps','Regierungspräsidium Stuttgart','http://www.landentwicklung-mlr.baden-wuerttemberg.de/',14,2);
/*!40000 ALTER TABLE `ingrid_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_rss_source`
--

DROP TABLE IF EXISTS `ingrid_rss_source`;
CREATE TABLE `ingrid_rss_source` (
  `id` mediumint(9) NOT NULL auto_increment,
  `provider` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  `url` varchar(255) NOT NULL,
  `lang` varchar(255) NOT NULL,
  `categories` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=14;

--
-- Dumping data for table `ingrid_rss_source`
--

LOCK TABLES `ingrid_rss_source` WRITE;
/*!40000 ALTER TABLE `ingrid_rss_source` DISABLE KEYS */;
INSERT INTO `ingrid_rss_source` VALUES (1,'UBA','RSS-UBA','http://www.uba.de/rss/ubapresseinfo.xml','de','all'),(2,'BFN','BFN-UBA','http://www.bfn.de/6.100.html','de','all'),(3,'MUF RLP','MUF RLP','http://www.mufv.rlp.de/rss/rss_1_20.xml','de','all'),(4,'BFN','BfN-Skripten','http://www.bfn.de/0502_skripten.100.html','de','all'),(5,'NI_MU','MU_aktuell_PI_GB','http://www.niedersachsen.de/rss/rss_19947156_598_20.rss','de','all'),(6,'MBU','BMU','http://www.bmu.de/allgemein/rss/35401.rss','de','all'),(7,'MU SACHSEN','MU SACHSEN','http://www.medienservice.sachsen.de/app/WebObjects/mspublic.woa/wa/rssFeed','de','umwelt,landwirtschaft/forst'),(8,'LfUG Sachsen','LfUG Sachsen','http://www.umwelt.sachsen.de/de/wu/umwelt/lfug/lfug-internet/start.xml','de','all'),(9,'MLUV Brandenburg','RSS MLUV Brandenburg','http://www.mluv.brandenburg.de/cms/list.php/mluv_presse_rss','de','all'),(12,'bw_lu','Landesanstalt für Umwelt, Messungen und Naturschutz Baden-Württemberg','http://www.lubw.baden-wuerttemberg.de/servlet/is/Entry.20732.DisplayRSS2/','de','all'),(13,'bw_statistik','RSS Badenwürtemberg (Statistik)','http://www.statistik.baden-wuerttemberg.de/UmweltVerkehr/rss.aspx','de','all');
/*!40000 ALTER TABLE `ingrid_rss_source` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_rss_store`
--

DROP TABLE IF EXISTS `ingrid_rss_store`;
CREATE TABLE `ingrid_rss_store` (
  `link` varchar(255) NOT NULL,
  `author` varchar(255) default NULL,
  `categories` varchar(255) default NULL,
  `copyright` varchar(255) default NULL,
  `description` mediumtext,
  `language` varchar(255) default NULL,
  `published_date` timestamp NOT NULL,
  `title` varchar(255) default NULL,
  PRIMARY KEY  (`link`),
  KEY `IX_ingrid_rss_store_1` (`published_date`),
  KEY `IX_ingrid_rss_store_2` (`language`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ingrid_rss_store`
--

LOCK TABLES `ingrid_rss_store` WRITE;
/*!40000 ALTER TABLE `ingrid_rss_store` DISABLE KEYS */;
/*!40000 ALTER TABLE `ingrid_rss_store` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrid_service_rubric`
--

DROP TABLE IF EXISTS `ingrid_service_rubric`;
CREATE TABLE `ingrid_service_rubric` (
  `id` mediumint(9) NOT NULL auto_increment,
  `form_value` varchar(255) NOT NULL,
  `query_value` varchar(255) NOT NULL,
  `sortkey` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4;

--
-- Dumping data for table `ingrid_service_rubric`
--

LOCK TABLES `ingrid_service_rubric` WRITE;
/*!40000 ALTER TABLE `ingrid_service_rubric` DISABLE KEYS */;
INSERT INTO `ingrid_service_rubric` VALUES (1,'pre','press',1),(2,'pub','publication',2),(3,'ver','event',3);
/*!40000 ALTER TABLE `ingrid_service_rubric` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jetspeed_service`
--

DROP TABLE IF EXISTS `jetspeed_service`;
CREATE TABLE `jetspeed_service` (
  `ID` mediumint(9) NOT NULL,
  `APPLICATION_ID` mediumint(9) NOT NULL,
  `NAME` varchar(150) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `jetspeed_service`
--

LOCK TABLES `jetspeed_service` WRITE;
/*!40000 ALTER TABLE `jetspeed_service` DISABLE KEYS */;
/*!40000 ALTER TABLE `jetspeed_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `language`
--

DROP TABLE IF EXISTS `language`;
CREATE TABLE `language` (
  `ID` mediumint(9) NOT NULL,
  `PORTLET_ID` mediumint(9) NOT NULL,
  `TITLE` varchar(100) default NULL,
  `SHORT_TITLE` varchar(100) default NULL,
  `LOCALE_STRING` varchar(50) NOT NULL,
  `KEYWORDS` mediumtext,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `language`
--

LOCK TABLES `language` WRITE;
/*!40000 ALTER TABLE `language` DISABLE KEYS */;
/*!40000 ALTER TABLE `language` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `link`
--

DROP TABLE IF EXISTS `link`;
CREATE TABLE `link` (
  `LINK_ID` mediumint(9) NOT NULL,
  `PARENT_ID` mediumint(9) NOT NULL,
  `PATH` varchar(240) NOT NULL,
  `NAME` varchar(80) NOT NULL,
  `VERSION` varchar(40) default NULL,
  `TITLE` varchar(100) default NULL,
  `SHORT_TITLE` varchar(40) default NULL,
  `IS_HIDDEN` int(11) NOT NULL,
  `SKIN` varchar(80) default NULL,
  `TARGET` varchar(80) default NULL,
  `URL` varchar(255) default NULL,
  `SUBSITE` varchar(40) default NULL,
  `USER_PRINCIPAL` varchar(40) default NULL,
  `ROLE_PRINCIPAL` varchar(40) default NULL,
  `GROUP_PRINCIPAL` varchar(40) default NULL,
  `MEDIATYPE` varchar(15) default NULL,
  `LOCALE` varchar(20) default NULL,
  `EXT_ATTR_NAME` varchar(15) default NULL,
  `EXT_ATTR_VALUE` varchar(40) default NULL,
  `OWNER_PRINCIPAL` varchar(40) default NULL,
  PRIMARY KEY  (`LINK_ID`),
  UNIQUE KEY `PATH` (`PATH`),
  KEY `IX_LINK_1` (`PARENT_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `link`
--

LOCK TABLES `link` WRITE;
/*!40000 ALTER TABLE `link` DISABLE KEYS */;
INSERT INTO `link` VALUES (1,1,'/language.link','language.link',NULL,'ingrid.page.language','ingrid.page.language',0,NULL,'top','/default-page.psml',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,1,'/webmaster.link','webmaster.link',NULL,'ingrid.page.webmaster','ingrid.page.webmaster',0,NULL,'top','mailto:webmaster@portalu.de',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `link_constraint`
--

DROP TABLE IF EXISTS `link_constraint`;
CREATE TABLE `link_constraint` (
  `CONSTRAINT_ID` mediumint(9) NOT NULL,
  `LINK_ID` mediumint(9) NOT NULL,
  `APPLY_ORDER` mediumint(9) NOT NULL,
  `USER_PRINCIPALS_ACL` varchar(120) default NULL,
  `ROLE_PRINCIPALS_ACL` varchar(120) default NULL,
  `GROUP_PRINCIPALS_ACL` varchar(120) default NULL,
  `PERMISSIONS_ACL` varchar(120) default NULL,
  PRIMARY KEY  (`CONSTRAINT_ID`),
  KEY `IX_LINK_CONSTRAINT_1` (`LINK_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `link_constraint`
--

LOCK TABLES `link_constraint` WRITE;
/*!40000 ALTER TABLE `link_constraint` DISABLE KEYS */;
/*!40000 ALTER TABLE `link_constraint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `link_constraints_ref`
--

DROP TABLE IF EXISTS `link_constraints_ref`;
CREATE TABLE `link_constraints_ref` (
  `CONSTRAINTS_REF_ID` mediumint(9) NOT NULL,
  `LINK_ID` mediumint(9) NOT NULL,
  `APPLY_ORDER` mediumint(9) NOT NULL,
  `NAME` varchar(40) NOT NULL,
  PRIMARY KEY  (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `LINK_ID` (`LINK_ID`,`NAME`),
  KEY `IX_LINK_CONSTRAINTS_REF_1` (`LINK_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `link_constraints_ref`
--

LOCK TABLES `link_constraints_ref` WRITE;
/*!40000 ALTER TABLE `link_constraints_ref` DISABLE KEYS */;
/*!40000 ALTER TABLE `link_constraints_ref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `link_metadata`
--

DROP TABLE IF EXISTS `link_metadata`;
CREATE TABLE `link_metadata` (
  `METADATA_ID` mediumint(9) NOT NULL,
  `LINK_ID` mediumint(9) NOT NULL,
  `NAME` varchar(15) NOT NULL,
  `LOCALE` varchar(20) NOT NULL,
  `VALUE` varchar(100) NOT NULL,
  PRIMARY KEY  (`METADATA_ID`),
  UNIQUE KEY `LINK_ID` (`LINK_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_LINK_METADATA_1` (`LINK_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `link_metadata`
--

LOCK TABLES `link_metadata` WRITE;
/*!40000 ALTER TABLE `link_metadata` DISABLE KEYS */;
/*!40000 ALTER TABLE `link_metadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `localized_description`
--

DROP TABLE IF EXISTS `localized_description`;
CREATE TABLE `localized_description` (
  `ID` mediumint(9) NOT NULL,
  `OBJECT_ID` mediumint(9) NOT NULL,
  `CLASS_NAME` varchar(255) NOT NULL,
  `DESCRIPTION` mediumtext NOT NULL,
  `LOCALE_STRING` varchar(50) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `localized_description`
--

LOCK TABLES `localized_description` WRITE;
/*!40000 ALTER TABLE `localized_description` DISABLE KEYS */;
/*!40000 ALTER TABLE `localized_description` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `localized_display_name`
--

DROP TABLE IF EXISTS `localized_display_name`;
CREATE TABLE `localized_display_name` (
  `ID` mediumint(9) NOT NULL,
  `OBJECT_ID` mediumint(9) NOT NULL,
  `CLASS_NAME` varchar(255) default NULL,
  `DISPLAY_NAME` mediumtext NOT NULL,
  `LOCALE_STRING` varchar(50) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `localized_display_name`
--

LOCK TABLES `localized_display_name` WRITE;
/*!40000 ALTER TABLE `localized_display_name` DISABLE KEYS */;
/*!40000 ALTER TABLE `localized_display_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media_type`
--

DROP TABLE IF EXISTS `media_type`;
CREATE TABLE `media_type` (
  `MEDIATYPE_ID` mediumint(9) NOT NULL,
  `NAME` varchar(80) NOT NULL,
  `CHARACTER_SET` varchar(40) default NULL,
  `TITLE` varchar(80) default NULL,
  `DESCRIPTION` mediumtext,
  PRIMARY KEY  (`MEDIATYPE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `media_type`
--

LOCK TABLES `media_type` WRITE;
/*!40000 ALTER TABLE `media_type` DISABLE KEYS */;
INSERT INTO `media_type` VALUES (1,'html','UTF-8','HTML','Rich HTML for HTML 4.0 compliants browsers'),(2,'vxml','UTF-8','VoiceXML','Format suitable for use with an audio VoiceXML server'),(3,'wml','UTF-8','WML','Format for mobile phones and PDAs compatible with WML 1.1'),(4,'xhtml-basic','UTF-8','XHTML','XHTML Basic'),(5,'xml','','XML','XML 1.0');
/*!40000 ALTER TABLE `media_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mediatype_to_capability`
--

DROP TABLE IF EXISTS `mediatype_to_capability`;
CREATE TABLE `mediatype_to_capability` (
  `MEDIATYPE_ID` mediumint(9) NOT NULL,
  `CAPABILITY_ID` mediumint(9) NOT NULL
) ENGINE=MyISAM;

--
-- Dumping data for table `mediatype_to_capability`
--

LOCK TABLES `mediatype_to_capability` WRITE;
/*!40000 ALTER TABLE `mediatype_to_capability` DISABLE KEYS */;
/*!40000 ALTER TABLE `mediatype_to_capability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mediatype_to_mimetype`
--

DROP TABLE IF EXISTS `mediatype_to_mimetype`;
CREATE TABLE `mediatype_to_mimetype` (
  `MEDIATYPE_ID` mediumint(9) NOT NULL,
  `MIMETYPE_ID` mediumint(9) NOT NULL
) ENGINE=MyISAM;

--
-- Dumping data for table `mediatype_to_mimetype`
--

LOCK TABLES `mediatype_to_mimetype` WRITE;
/*!40000 ALTER TABLE `mediatype_to_mimetype` DISABLE KEYS */;
INSERT INTO `mediatype_to_mimetype` VALUES (1,2),(2,4),(3,3),(4,1),(5,6);
/*!40000 ALTER TABLE `mediatype_to_mimetype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mimetype`
--

DROP TABLE IF EXISTS `mimetype`;
CREATE TABLE `mimetype` (
  `MIMETYPE_ID` mediumint(9) NOT NULL,
  `NAME` varchar(80) NOT NULL,
  PRIMARY KEY  (`MIMETYPE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `mimetype`
--

LOCK TABLES `mimetype` WRITE;
/*!40000 ALTER TABLE `mimetype` DISABLE KEYS */;
INSERT INTO `mimetype` VALUES (1,'application/xhtml+xml'),(2,'text/html'),(3,'text/vnd.wap.wml'),(4,'text/vxml'),(5,'text/xhtml'),(6,'text/xml');
/*!40000 ALTER TABLE `mimetype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_dlist`
--

DROP TABLE IF EXISTS `ojb_dlist`;
CREATE TABLE `ojb_dlist` (
  `ID` mediumint(9) NOT NULL,
  `SIZE_` mediumint(9) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ojb_dlist`
--

LOCK TABLES `ojb_dlist` WRITE;
/*!40000 ALTER TABLE `ojb_dlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_dlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_dlist_entries`
--

DROP TABLE IF EXISTS `ojb_dlist_entries`;
CREATE TABLE `ojb_dlist_entries` (
  `ID` mediumint(9) NOT NULL,
  `DLIST_ID` mediumint(9) default NULL,
  `POSITION_` mediumint(9) default NULL,
  `OID_` longblob,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ojb_dlist_entries`
--

LOCK TABLES `ojb_dlist_entries` WRITE;
/*!40000 ALTER TABLE `ojb_dlist_entries` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_dlist_entries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_dmap`
--

DROP TABLE IF EXISTS `ojb_dmap`;
CREATE TABLE `ojb_dmap` (
  `ID` mediumint(9) NOT NULL,
  `SIZE_` mediumint(9) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ojb_dmap`
--

LOCK TABLES `ojb_dmap` WRITE;
/*!40000 ALTER TABLE `ojb_dmap` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_dmap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_dset`
--

DROP TABLE IF EXISTS `ojb_dset`;
CREATE TABLE `ojb_dset` (
  `ID` mediumint(9) NOT NULL,
  `SIZE_` mediumint(9) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ojb_dset`
--

LOCK TABLES `ojb_dset` WRITE;
/*!40000 ALTER TABLE `ojb_dset` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_dset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_dset_entries`
--

DROP TABLE IF EXISTS `ojb_dset_entries`;
CREATE TABLE `ojb_dset_entries` (
  `ID` mediumint(9) NOT NULL,
  `DLIST_ID` mediumint(9) default NULL,
  `POSITION_` mediumint(9) default NULL,
  `OID_` longblob,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ojb_dset_entries`
--

LOCK TABLES `ojb_dset_entries` WRITE;
/*!40000 ALTER TABLE `ojb_dset_entries` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_dset_entries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_hl_seq`
--

DROP TABLE IF EXISTS `ojb_hl_seq`;
CREATE TABLE `ojb_hl_seq` (
  `TABLENAME` varchar(175) NOT NULL,
  `FIELDNAME` varchar(70) NOT NULL,
  `MAX_KEY` mediumint(9) default NULL,
  `GRAB_SIZE` mediumint(9) default NULL,
  `VERSION` mediumint(9) default NULL,
  PRIMARY KEY  (`TABLENAME`,`FIELDNAME`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ojb_hl_seq`
--

LOCK TABLES `ojb_hl_seq` WRITE;
/*!40000 ALTER TABLE `ojb_hl_seq` DISABLE KEYS */;
INSERT INTO `ojb_hl_seq` VALUES ('SEQ_PREFS_NODE','deprecatedColumn',20,20,1),('SEQ_CAPABILITY','deprecatedColumn',60,20,3),('SEQ_MIMETYPE','deprecatedColumn',20,20,1),('SEQ_MEDIA_TYPE','deprecatedColumn',20,20,1),('SEQ_CLIENT','deprecatedColumn',20,20,1),('SEQ_RULE_CRITERION','deprecatedColumn',40,20,2),('SEQ_SECURITY_PRINCIPAL','deprecatedColumn',20,20,1),('SEQ_SECURITY_CREDENTIAL','deprecatedColumn',20,20,1),('SEQ_PREFS_PROPERTY_VALUE','deprecatedColumn',20,20,1),('SEQ_SECURITY_PERMISSION','deprecatedColumn',20,20,1),('SEQ_FOLDER','deprecatedColumn',20,20,1),('SEQ_FOLDER_METADATA','deprecatedColumn',20,20,1),('SEQ_FOLDER_CONSTRAINTS_REF','deprecatedColumn',20,20,1),('SEQ_FOLDER_MENU','deprecatedColumn',60,20,3),('SEQ_PAGE','deprecatedColumn',60,20,3),('SEQ_FRAGMENT','deprecatedColumn',200,20,10),('SEQ_FRAGMENT_PREF','deprecatedColumn',180,20,9),('SEQ_FRAGMENT_PREF_VALUE','deprecatedColumn',180,20,9),('SEQ_PAGE_CONSTRAINTS_REF','deprecatedColumn',80,20,4),('SEQ_PAGE_METADATA','deprecatedColumn',60,20,3),('SEQ_LINK','deprecatedColumn',20,20,1),('SEQ_FRAGMENT_CONSTRAINTS_REF','deprecatedColumn',20,20,1),('SEQ_PAGE_SECURITY','deprecatedColumn',20,20,1),('SEQ_PAGE_SEC_CONSTRAINTS_DEF','deprecatedColumn',20,20,1),('SEQ_PAGE_SEC_CONSTRAINT_DEF','deprecatedColumn',20,20,1),('SEQ_PAGE_SEC_CONSTRAINTS_REF','deprecatedColumn',20,20,1);
/*!40000 ALTER TABLE `ojb_hl_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_lockentry`
--

DROP TABLE IF EXISTS `ojb_lockentry`;
CREATE TABLE `ojb_lockentry` (
  `OID_` varchar(250) NOT NULL,
  `TX_ID` varchar(50) NOT NULL,
  `TIMESTAMP_` timestamp NOT NULL,
  `ISOLATIONLEVEL` mediumint(9) default NULL,
  `LOCKTYPE` mediumint(9) default NULL,
  PRIMARY KEY  (`OID_`,`TX_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ojb_lockentry`
--

LOCK TABLES `ojb_lockentry` WRITE;
/*!40000 ALTER TABLE `ojb_lockentry` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_lockentry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_nrm`
--

DROP TABLE IF EXISTS `ojb_nrm`;
CREATE TABLE `ojb_nrm` (
  `NAME` varchar(250) NOT NULL,
  `OID_` longblob,
  PRIMARY KEY  (`NAME`)
) ENGINE=MyISAM;

--
-- Dumping data for table `ojb_nrm`
--

LOCK TABLES `ojb_nrm` WRITE;
/*!40000 ALTER TABLE `ojb_nrm` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_nrm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pa_metadata_fields`
--

DROP TABLE IF EXISTS `pa_metadata_fields`;
CREATE TABLE `pa_metadata_fields` (
  `ID` mediumint(9) NOT NULL,
  `OBJECT_ID` mediumint(9) NOT NULL,
  `COLUMN_VALUE` mediumtext NOT NULL,
  `NAME` varchar(100) NOT NULL,
  `LOCALE_STRING` varchar(50) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `OBJECT_ID` (`OBJECT_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `pa_metadata_fields`
--

LOCK TABLES `pa_metadata_fields` WRITE;
/*!40000 ALTER TABLE `pa_metadata_fields` DISABLE KEYS */;
/*!40000 ALTER TABLE `pa_metadata_fields` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page`
--

DROP TABLE IF EXISTS `page`;
CREATE TABLE `page` (
  `PAGE_ID` mediumint(9) NOT NULL,
  `PARENT_ID` mediumint(9) NOT NULL,
  `PATH` varchar(240) NOT NULL,
  `NAME` varchar(80) NOT NULL,
  `VERSION` varchar(40) default NULL,
  `TITLE` varchar(100) default NULL,
  `SHORT_TITLE` varchar(40) default NULL,
  `IS_HIDDEN` int(11) NOT NULL,
  `SKIN` varchar(80) default NULL,
  `DEFAULT_LAYOUT_DECORATOR` varchar(80) default NULL,
  `DEFAULT_PORTLET_DECORATOR` varchar(80) default NULL,
  `SUBSITE` varchar(40) default NULL,
  `USER_PRINCIPAL` varchar(40) default NULL,
  `ROLE_PRINCIPAL` varchar(40) default NULL,
  `GROUP_PRINCIPAL` varchar(40) default NULL,
  `MEDIATYPE` varchar(15) default NULL,
  `LOCALE` varchar(20) default NULL,
  `EXT_ATTR_NAME` varchar(15) default NULL,
  `EXT_ATTR_VALUE` varchar(40) default NULL,
  `OWNER_PRINCIPAL` varchar(40) default NULL,
  PRIMARY KEY  (`PAGE_ID`),
  UNIQUE KEY `PATH` (`PATH`),
  KEY `IX_PAGE_1` (`PARENT_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `page`
--

LOCK TABLES `page` WRITE;
/*!40000 ALTER TABLE `page` DISABLE KEYS */;
INSERT INTO `page` VALUES (1,1,'/default-page.psml','default-page.psml',NULL,'ingrid.page.home','ingrid.page.home',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,1,'/detect-js.psml','detect-js.psml',NULL,'Detect JavaScript','Detect JavaScript',1,'orange','ingrid-clear','clear',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,1,'/disclaimer.psml','disclaimer.psml',NULL,'ingrid.page.disclaimer','ingrid.page.disclaimer',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,1,'/help.psml','help.psml',NULL,'ingrid.page.help','ingrid.page.help',0,'orange','ingrid-help','clear',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,1,'/main-about-partner.psml','main-about-partner.psml',NULL,'About PortalU','About PortalU',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,1,'/main-about.psml','main-about.psml',NULL,'ingrid.page.about','ingrid.page.about',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(7,1,'/main-chronicle.psml','main-chronicle.psml',NULL,'ingrid.page.chronicle','ingrid.page.chronicle',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(8,1,'/main-environment.psml','main-environment.psml',NULL,'ingrid.page.envtopics','ingrid.page.envtopics',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(9,1,'/main-maps.psml','main-maps.psml',NULL,'ingrid.page.maps','ingrid.page.maps',0,'orange','ingrid-popup','clear',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(10,1,'/main-measures.psml','main-measures.psml',NULL,'ingrid.page.measures','ingrid.page.measures',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(11,1,'/main-search.psml','main-search.psml',NULL,'ingrid.page.search','ingrid.page.search',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(12,1,'/main-service.psml','main-service.psml',NULL,'ingrid.page.service','ingrid.page.service',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(13,1,'/myportal-create-account.psml','myportal-create-account.psml',NULL,'Login','Login',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(14,1,'/myportal-password-forgotten.psml','myportal-password-forgotten.psml',NULL,'Login','Login',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(15,1,'/privacy.psml','privacy.psml',NULL,'ingrid.page.privacy','ingrid.page.privacy',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(16,1,'/rss-news.psml','rss-news.psml',NULL,'Home','Home',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(17,1,'/search-detail.psml','search-detail.psml',NULL,'Detail Information','Detail Information',0,'orange','ingrid-popup','clear',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(18,1,'/search-history.psml','search-history.psml',NULL,'Search History','Search History',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(19,1,'/search-result-js.psml','search-result-js.psml',NULL,'Suchresult','Suchresult',0,'orange','ingrid-clear','clear',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(20,1,'/search-save.psml','search-save.psml',NULL,'Save Search','Save Search',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(21,1,'/search-settings.psml','search-settings.psml',NULL,'Search Settings','Search Settings',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(22,1,'/service-contact-newsletter.psml','service-contact-newsletter.psml',NULL,'Newsletter','Newsletter',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(23,1,'/service-contact.psml','service-contact.psml',NULL,'ingrid.page.contact','ingrid.page.contact',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(24,1,'/service-myportal.psml','service-myportal.psml',NULL,'ingrid.page.myportal','ingrid.page.myportal',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(25,1,'/service-sitemap.psml','service-sitemap.psml',NULL,'ingrid.page.sitemap','ingrid.page.sitemap',0,'orange','ingrid','clear',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(26,3,'/_role/user/default-page.psml','default-page.psml',NULL,'ingrid.page.home','ingrid.page.home',0,'orange','ingrid','ingrid-teaser',NULL,NULL,'user',NULL,NULL,NULL,NULL,NULL,NULL),(27,3,'/_role/user/myportal-edit-account.psml','myportal-edit-account.psml',NULL,'Login','Login',0,'orange','ingrid','ingrid-teaser',NULL,NULL,'user',NULL,NULL,NULL,NULL,NULL,NULL),(28,3,'/_role/user/myportal-personalize.psml','myportal-personalize.psml',NULL,'Login','Login',0,'orange','ingrid','ingrid-teaser',NULL,NULL,'user',NULL,NULL,NULL,NULL,NULL,NULL),(29,3,'/_role/user/service-myportal.psml','service-myportal.psml',NULL,'ingrid.page.myportal','ingrid.page.myportal',0,'orange','ingrid','ingrid-teaser',NULL,NULL,'user',NULL,NULL,NULL,NULL,NULL,NULL),(30,5,'/_user/template/default-page.psml','default-page.psml',NULL,'ingrid.page.home','ingrid.page.home',0,'orange','ingrid','ingrid-teaser',NULL,'template',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(31,6,'/administration/admin-cms.psml','admin-cms.psml',NULL,'Inhalte','Inhalte',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(32,6,'/administration/admin-component-monitor.psml','admin-component-monitor.psml',NULL,'Überwachung','Überwachung',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(33,6,'/administration/admin-content-partner.psml','admin-content-partner.psml',NULL,'Partner','Partner',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(34,6,'/administration/admin-content-provider.psml','admin-content-provider.psml',NULL,'Anbieter','Anbieter',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(35,6,'/administration/admin-content-rss.psml','admin-content-rss.psml',NULL,'RSS','RSS',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(36,6,'/administration/admin-homepage.psml','admin-homepage.psml',NULL,'Startseite','Startseite',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(37,6,'/administration/admin-iplugs.psml','admin-iplugs.psml',NULL,'iPlugs/iBus','iPlugs/iBus',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(38,6,'/administration/admin-portal-profile.psml','admin-portal-profile.psml',NULL,'Portal Profile','Portal Profile',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(39,6,'/administration/admin-statistics.psml','admin-statistics.psml',NULL,'Statistiken','Statistiken',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(40,6,'/administration/admin-usermanagement.psml','admin-usermanagement.psml',NULL,'Benutzer','Benutzer',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(41,6,'/administration/admin-wms.psml','admin-wms.psml',NULL,'WMS','WMS',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(42,6,'/administration/dwr-test.psml','dwr-test.psml',NULL,'DWR Test Portlet','DWR Test Portlet',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(43,7,'/search-extended/search-ext-adr-area-partner.psml','search-ext-adr-area-partner.psml',NULL,'Search Extended Address/Area/Partner','Search Extended Address/Area/Partner',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(44,7,'/search-extended/search-ext-adr-place-reference.psml','search-ext-adr-place-reference.psml',NULL,'Search Extended Address/Place/Reference','Search Extended Address/Place/Reference',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(45,7,'/search-extended/search-ext-adr-topic-mode.psml','search-ext-adr-topic-mode.psml',NULL,'Search Extended Address/Topic/Mode','Search Extended Address/Topic/Mode',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(46,7,'/search-extended/search-ext-adr-topic-terms.psml','search-ext-adr-topic-terms.psml',NULL,'Search Extended Address/Topic/Terms','Search Extended Address/Topic/Terms',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(47,7,'/search-extended/search-ext-env-area-contents.psml','search-ext-env-area-contents.psml',NULL,'SearchExtended Environment/Area/Contents','SearchExtended Environment/Area/Contents',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(48,7,'/search-extended/search-ext-env-area-partner.psml','search-ext-env-area-partner.psml',NULL,'SearchExtended Environment/Area/Partner','SearchExtended Environment/Area/Partner',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(49,7,'/search-extended/search-ext-env-area-sources.psml','search-ext-env-area-sources.psml',NULL,'SearchExtended Environment/Area/Contents','SearchExtended Environment/Area/Contents',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(50,7,'/search-extended/search-ext-env-place-geothesaurus.psml','search-ext-env-place-geothesaurus.psml',NULL,'Search Extended Environment','Search Extended Environment',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(51,7,'/search-extended/search-ext-env-place-map.psml','search-ext-env-place-map.psml',NULL,'SearchExtended Environment/Place/Map','SearchExtended Environment/Place/Map',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(52,7,'/search-extended/search-ext-env-time-constraint.psml','search-ext-env-time-constraint.psml',NULL,'SearchExtended Environment/Time/Constraint','Environment/Time/Constraint',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(53,7,'/search-extended/search-ext-env-topic-terms.psml','search-ext-env-topic-terms.psml',NULL,'Search Extended Environment','Search Extended Environment',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(54,7,'/search-extended/search-ext-env-topic-thesaurus.psml','search-ext-env-topic-thesaurus.psml',NULL,'Search Extended Environment','Search Extended Environment',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(55,7,'/search-extended/search-ext-res-topic-attributes.psml','search-ext-res-topic-attributes.psml',NULL,'Search Extended Research/Topic/Attributes','Research/Topic/Attributes',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(56,7,'/search-extended/search-ext-res-topic-terms.psml','search-ext-res-topic-terms.psml',NULL,'Search Extended Research/Topic/Terms','Search Extended Research/Topic/Terms',0,'orange','ingrid','ingrid-teaser',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `page` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page_constraint`
--

DROP TABLE IF EXISTS `page_constraint`;
CREATE TABLE `page_constraint` (
  `CONSTRAINT_ID` mediumint(9) NOT NULL,
  `PAGE_ID` mediumint(9) NOT NULL,
  `APPLY_ORDER` mediumint(9) NOT NULL,
  `USER_PRINCIPALS_ACL` varchar(120) default NULL,
  `ROLE_PRINCIPALS_ACL` varchar(120) default NULL,
  `GROUP_PRINCIPALS_ACL` varchar(120) default NULL,
  `PERMISSIONS_ACL` varchar(120) default NULL,
  PRIMARY KEY  (`CONSTRAINT_ID`),
  KEY `IX_PAGE_CONSTRAINT_1` (`PAGE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `page_constraint`
--

LOCK TABLES `page_constraint` WRITE;
/*!40000 ALTER TABLE `page_constraint` DISABLE KEYS */;
/*!40000 ALTER TABLE `page_constraint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page_constraints_ref`
--

DROP TABLE IF EXISTS `page_constraints_ref`;
CREATE TABLE `page_constraints_ref` (
  `CONSTRAINTS_REF_ID` mediumint(9) NOT NULL,
  `PAGE_ID` mediumint(9) NOT NULL,
  `APPLY_ORDER` mediumint(9) NOT NULL,
  `NAME` varchar(40) NOT NULL,
  PRIMARY KEY  (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `PAGE_ID` (`PAGE_ID`,`NAME`),
  KEY `IX_PAGE_CONSTRAINTS_REF_1` (`PAGE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `page_constraints_ref`
--

LOCK TABLES `page_constraints_ref` WRITE;
/*!40000 ALTER TABLE `page_constraints_ref` DISABLE KEYS */;
INSERT INTO `page_constraints_ref` VALUES (1,1,0,'public-view'),(2,1,1,'admin-portal'),(3,2,0,'public-view'),(4,3,0,'public-view'),(5,4,0,'public-view'),(6,5,0,'public-view'),(7,6,0,'public-view'),(8,7,0,'public-view'),(9,8,0,'public-view'),(10,9,0,'public-view'),(11,10,0,'public-view'),(12,11,0,'public-view'),(13,12,0,'public-view'),(14,13,0,'public-view'),(15,14,0,'public-view'),(16,15,0,'public-view'),(17,16,0,'public-view'),(18,17,0,'public-view'),(19,18,0,'public-view'),(20,19,0,'public-view'),(21,20,0,'public-view'),(22,21,0,'public-view'),(23,22,0,'public-view'),(24,23,0,'public-view'),(25,24,0,'public-view'),(26,25,0,'public-view'),(27,26,0,'public-view'),(28,27,0,'public-view'),(29,28,0,'public-view'),(30,29,0,'public-view'),(31,30,0,'public-edit'),(32,31,0,'admin'),(33,31,1,'admin-portal'),(34,32,0,'admin'),(35,32,1,'admin-portal'),(36,33,0,'admin'),(37,33,1,'admin-portal'),(38,34,0,'admin'),(39,34,1,'admin-portal'),(40,35,0,'admin'),(41,35,1,'admin-portal'),(42,36,0,'admin'),(43,36,1,'admin-portal'),(44,37,0,'admin'),(45,37,1,'admin-portal'),(46,37,2,'admin-partner'),(47,37,3,'admin-provider'),(48,38,0,'admin'),(49,39,0,'admin'),(50,39,1,'admin-portal'),(51,39,2,'admin-partner'),(52,39,3,'admin-provider'),(53,40,0,'admin'),(54,40,1,'admin-partner'),(55,40,2,'admin-portal'),(56,41,0,'admin'),(57,41,1,'admin-portal'),(58,42,0,'admin'),(59,43,0,'public-view'),(60,44,0,'public-view'),(61,45,0,'public-view'),(62,46,0,'public-view'),(63,47,0,'public-view'),(64,48,0,'public-view'),(65,49,0,'public-view'),(66,50,0,'public-view'),(67,51,0,'public-view'),(68,52,0,'public-view'),(69,53,0,'public-view'),(70,54,0,'public-view'),(71,55,0,'public-view'),(72,56,0,'public-view');
/*!40000 ALTER TABLE `page_constraints_ref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page_menu`
--

DROP TABLE IF EXISTS `page_menu`;
CREATE TABLE `page_menu` (
  `MENU_ID` mediumint(9) NOT NULL,
  `CLASS_NAME` varchar(100) NOT NULL,
  `PARENT_ID` mediumint(9) default NULL,
  `PAGE_ID` mediumint(9) default NULL,
  `ELEMENT_ORDER` mediumint(9) default NULL,
  `NAME` varchar(100) default NULL,
  `TITLE` varchar(100) default NULL,
  `SHORT_TITLE` varchar(40) default NULL,
  `TEXT` varchar(100) default NULL,
  `OPTIONS` varchar(255) default NULL,
  `DEPTH` mediumint(9) default NULL,
  `IS_PATHS` int(11) default NULL,
  `IS_REGEXP` int(11) default NULL,
  `PROFILE` varchar(80) default NULL,
  `OPTIONS_ORDER` varchar(255) default NULL,
  `SKIN` varchar(80) default NULL,
  `IS_NEST` int(11) default NULL,
  PRIMARY KEY  (`MENU_ID`),
  KEY `IX_PAGE_MENU_1` (`PARENT_ID`),
  KEY `UN_PAGE_MENU_1` (`PAGE_ID`,`NAME`)
) ENGINE=MyISAM;

--
-- Dumping data for table `page_menu`
--

LOCK TABLES `page_menu` WRITE;
/*!40000 ALTER TABLE `page_menu` DISABLE KEYS */;
/*!40000 ALTER TABLE `page_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page_menu_metadata`
--

DROP TABLE IF EXISTS `page_menu_metadata`;
CREATE TABLE `page_menu_metadata` (
  `METADATA_ID` mediumint(9) NOT NULL,
  `MENU_ID` mediumint(9) NOT NULL,
  `NAME` varchar(15) NOT NULL,
  `LOCALE` varchar(20) NOT NULL,
  `VALUE` varchar(100) NOT NULL,
  PRIMARY KEY  (`METADATA_ID`),
  UNIQUE KEY `MENU_ID` (`MENU_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_PAGE_MENU_METADATA_1` (`MENU_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `page_menu_metadata`
--

LOCK TABLES `page_menu_metadata` WRITE;
/*!40000 ALTER TABLE `page_menu_metadata` DISABLE KEYS */;
/*!40000 ALTER TABLE `page_menu_metadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page_metadata`
--

DROP TABLE IF EXISTS `page_metadata`;
CREATE TABLE `page_metadata` (
  `METADATA_ID` mediumint(9) NOT NULL,
  `PAGE_ID` mediumint(9) NOT NULL,
  `NAME` varchar(15) NOT NULL,
  `LOCALE` varchar(20) NOT NULL,
  `VALUE` varchar(100) NOT NULL,
  PRIMARY KEY  (`METADATA_ID`),
  UNIQUE KEY `PAGE_ID` (`PAGE_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_PAGE_METADATA_1` (`PAGE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `page_metadata`
--

LOCK TABLES `page_metadata` WRITE;
/*!40000 ALTER TABLE `page_metadata` DISABLE KEYS */;
INSERT INTO `page_metadata` VALUES (1,2,'title','de,,','Detect JavaScript'),(2,5,'title','de,,','Über PortalU'),(3,13,'title','de,,','Mein PortalU'),(4,14,'title','de,,','Mein PortalU'),(5,16,'title','de,,','Startseite'),(6,17,'title','de,,','Detailinformationen'),(7,18,'title','de,,','Suchhistorie'),(8,19,'title','de,,','Suchergebnis'),(9,20,'title','de,,','Suche speichern'),(10,21,'title','de,,','Sucheinstellungen'),(11,22,'title','de,,','Newsletter'),(12,27,'title','de,,','Mein PortalU'),(13,28,'title','de,,','Mein PortalU'),(14,31,'title','en,,','Content'),(15,32,'title','en,,','Monitoring'),(16,33,'title','en,,','Partner'),(17,34,'title','en,,','Provider'),(18,35,'title','en,,','RSS'),(19,36,'title','en,,','Homepage'),(20,37,'title','en,,','iPlugs/iBus'),(21,38,'title','en,,','Portal Profile'),(22,39,'title','en,,','Stats'),(23,40,'title','en,,','User'),(24,41,'title','en,,','WMS'),(25,42,'title','en,,','DWR Test Portlet'),(26,43,'title','de,,','Erweiterte Suche Adressen/Suchbereich/BundLaender'),(27,44,'title','de,,','Erweiterte Suche Adressen/Raum/Raumbezug'),(28,45,'title','de,,','Erweiterte Suche Adressen/Thema/Suchmodus'),(29,46,'title','de,,','Erweiterte Suche Adressen/Thema/Suchbegriffe'),(30,47,'title','de,,','Erweiterte Suche Umweltinformationen/Suchbereich/Inhalte'),(31,48,'title','de,,','Erweiterte Suche Umweltinformationen/Suchbereich/BundLaender'),(32,49,'title','de,,','Erweiterte Suche Umweltinformationen/Suchbereich/Inhalte'),(33,50,'title','de,,','Erweiterte Suche Umweltinformationen'),(34,51,'title','de,,','Erweiterte Suche Umweltinformationen/Raum/Karte'),(35,52,'title','de,,','Erweiterte Suche Umweltinformationen/Zeit/Zeiteinschraenkung'),(36,52,'short-title','de,,','Umweltinf./Zeit/Zeiteinschraenkung'),(37,53,'title','de,,','Erweiterte Suche Umweltinformationen'),(38,54,'title','de,,','Erweiterte Suche Umweltinformationen'),(39,55,'title','de,,','Erweiterte Suche Forschung/Thema/Attribute'),(40,55,'short-title','de,,','Forschung/Thema/Attribute'),(41,56,'title','de,,','Erweiterte Suche Forschung/Thema/Suchbegriffe');
/*!40000 ALTER TABLE `page_metadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page_sec_constraint_def`
--

DROP TABLE IF EXISTS `page_sec_constraint_def`;
CREATE TABLE `page_sec_constraint_def` (
  `CONSTRAINT_DEF_ID` mediumint(9) NOT NULL,
  `CONSTRAINTS_DEF_ID` mediumint(9) NOT NULL,
  `APPLY_ORDER` mediumint(9) NOT NULL,
  `USER_PRINCIPALS_ACL` varchar(120) default NULL,
  `ROLE_PRINCIPALS_ACL` varchar(120) default NULL,
  `GROUP_PRINCIPALS_ACL` varchar(120) default NULL,
  `PERMISSIONS_ACL` varchar(120) default NULL,
  PRIMARY KEY  (`CONSTRAINT_DEF_ID`),
  KEY `IX_PAGE_SEC_CONSTRAINT_DEF_1` (`CONSTRAINTS_DEF_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `page_sec_constraint_def`
--

LOCK TABLES `page_sec_constraint_def` WRITE;
/*!40000 ALTER TABLE `page_sec_constraint_def` DISABLE KEYS */;
INSERT INTO `page_sec_constraint_def` VALUES (1,1,0,NULL,'admin',NULL,'view,edit'),(2,2,0,NULL,'manager',NULL,'view'),(3,3,0,NULL,'user,manager',NULL,'view'),(4,4,0,'*',NULL,NULL,'view'),(5,5,0,'*',NULL,NULL,'view,edit'),(6,6,0,NULL,'admin-portal',NULL,'view,edit'),(7,7,0,NULL,'admin-partner',NULL,'view,edit'),(8,8,0,NULL,'admin-provider',NULL,'view,edit');
/*!40000 ALTER TABLE `page_sec_constraint_def` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page_sec_constraints_def`
--

DROP TABLE IF EXISTS `page_sec_constraints_def`;
CREATE TABLE `page_sec_constraints_def` (
  `CONSTRAINTS_DEF_ID` mediumint(9) NOT NULL,
  `PAGE_SECURITY_ID` mediumint(9) NOT NULL,
  `NAME` varchar(40) NOT NULL,
  PRIMARY KEY  (`CONSTRAINTS_DEF_ID`),
  UNIQUE KEY `PAGE_SECURITY_ID` (`PAGE_SECURITY_ID`,`NAME`),
  KEY `IX_PAGE_SEC_CONSTRAINTS_DEF_1` (`PAGE_SECURITY_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `page_sec_constraints_def`
--

LOCK TABLES `page_sec_constraints_def` WRITE;
/*!40000 ALTER TABLE `page_sec_constraints_def` DISABLE KEYS */;
INSERT INTO `page_sec_constraints_def` VALUES (1,1,'admin'),(2,1,'manager'),(3,1,'users'),(4,1,'public-view'),(5,1,'public-edit'),(6,1,'admin-portal'),(7,1,'admin-partner'),(8,1,'admin-provider');
/*!40000 ALTER TABLE `page_sec_constraints_def` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page_sec_constraints_ref`
--

DROP TABLE IF EXISTS `page_sec_constraints_ref`;
CREATE TABLE `page_sec_constraints_ref` (
  `CONSTRAINTS_REF_ID` mediumint(9) NOT NULL,
  `PAGE_SECURITY_ID` mediumint(9) NOT NULL,
  `APPLY_ORDER` mediumint(9) NOT NULL,
  `NAME` varchar(40) NOT NULL,
  PRIMARY KEY  (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `PAGE_SECURITY_ID` (`PAGE_SECURITY_ID`,`NAME`),
  KEY `IX_PAGE_SEC_CONSTRAINTS_REF_1` (`PAGE_SECURITY_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `page_sec_constraints_ref`
--

LOCK TABLES `page_sec_constraints_ref` WRITE;
/*!40000 ALTER TABLE `page_sec_constraints_ref` DISABLE KEYS */;
INSERT INTO `page_sec_constraints_ref` VALUES (1,1,0,'admin');
/*!40000 ALTER TABLE `page_sec_constraints_ref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page_security`
--

DROP TABLE IF EXISTS `page_security`;
CREATE TABLE `page_security` (
  `PAGE_SECURITY_ID` mediumint(9) NOT NULL,
  `PARENT_ID` mediumint(9) NOT NULL,
  `PATH` varchar(240) NOT NULL,
  `NAME` varchar(80) NOT NULL,
  `VERSION` varchar(40) default NULL,
  `SUBSITE` varchar(40) default NULL,
  `USER_PRINCIPAL` varchar(40) default NULL,
  `ROLE_PRINCIPAL` varchar(40) default NULL,
  `GROUP_PRINCIPAL` varchar(40) default NULL,
  `MEDIATYPE` varchar(15) default NULL,
  `LOCALE` varchar(20) default NULL,
  `EXT_ATTR_NAME` varchar(15) default NULL,
  `EXT_ATTR_VALUE` varchar(40) default NULL,
  PRIMARY KEY  (`PAGE_SECURITY_ID`),
  UNIQUE KEY `PARENT_ID` (`PARENT_ID`),
  UNIQUE KEY `PATH` (`PATH`)
) ENGINE=MyISAM;

--
-- Dumping data for table `page_security`
--

LOCK TABLES `page_security` WRITE;
/*!40000 ALTER TABLE `page_security` DISABLE KEYS */;
INSERT INTO `page_security` VALUES (1,1,'/page.security','page.security',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `page_security` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page_statistics`
--

DROP TABLE IF EXISTS `page_statistics`;
CREATE TABLE `page_statistics` (
  `IPADDRESS` varchar(80) default NULL,
  `USER_NAME` varchar(80) default NULL,
  `TIME_STAMP` timestamp NOT NULL,
  `PAGE` varchar(80) default NULL,
  `STATUS` mediumint(9) default NULL,
  `ELAPSED_TIME` bigint(20) default NULL
) ENGINE=MyISAM;

--
-- Dumping data for table `page_statistics`
--

LOCK TABLES `page_statistics` WRITE;
/*!40000 ALTER TABLE `page_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `page_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parameter`
--

DROP TABLE IF EXISTS `parameter`;
CREATE TABLE `parameter` (
  `PARAMETER_ID` mediumint(9) NOT NULL,
  `PARENT_ID` mediumint(9) NOT NULL,
  `CLASS_NAME` varchar(255) NOT NULL,
  `NAME` varchar(80) NOT NULL,
  `PARAMETER_VALUE` mediumtext NOT NULL,
  PRIMARY KEY  (`PARAMETER_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `parameter`
--

LOCK TABLES `parameter` WRITE;
/*!40000 ALTER TABLE `parameter` DISABLE KEYS */;
/*!40000 ALTER TABLE `parameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pd_metadata_fields`
--

DROP TABLE IF EXISTS `pd_metadata_fields`;
CREATE TABLE `pd_metadata_fields` (
  `ID` mediumint(9) NOT NULL,
  `OBJECT_ID` mediumint(9) NOT NULL,
  `COLUMN_VALUE` mediumtext NOT NULL,
  `NAME` varchar(100) NOT NULL,
  `LOCALE_STRING` varchar(50) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `OBJECT_ID` (`OBJECT_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `pd_metadata_fields`
--

LOCK TABLES `pd_metadata_fields` WRITE;
/*!40000 ALTER TABLE `pd_metadata_fields` DISABLE KEYS */;
/*!40000 ALTER TABLE `pd_metadata_fields` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `portlet_application`
--

DROP TABLE IF EXISTS `portlet_application`;
CREATE TABLE `portlet_application` (
  `APPLICATION_ID` mediumint(9) NOT NULL,
  `APP_NAME` varchar(80) NOT NULL,
  `APP_IDENTIFIER` varchar(80) default NULL,
  `VERSION` varchar(80) default NULL,
  `APP_TYPE` mediumint(9) default NULL,
  `CHECKSUM` varchar(80) default NULL,
  `DESCRIPTION` varchar(80) default NULL,
  `WEB_APP_ID` mediumint(9) NOT NULL,
  `SECURITY_REF` varchar(40) default NULL,
  PRIMARY KEY  (`APPLICATION_ID`),
  UNIQUE KEY `APP_NAME` (`APP_NAME`)
) ENGINE=MyISAM;

--
-- Dumping data for table `portlet_application`
--

LOCK TABLES `portlet_application` WRITE;
/*!40000 ALTER TABLE `portlet_application` DISABLE KEYS */;
/*!40000 ALTER TABLE `portlet_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `portlet_content_type`
--

DROP TABLE IF EXISTS `portlet_content_type`;
CREATE TABLE `portlet_content_type` (
  `CONTENT_TYPE_ID` mediumint(9) NOT NULL,
  `PORTLET_ID` mediumint(9) NOT NULL,
  `CONTENT_TYPE` varchar(30) NOT NULL,
  `MODES` mediumtext,
  PRIMARY KEY  (`CONTENT_TYPE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `portlet_content_type`
--

LOCK TABLES `portlet_content_type` WRITE;
/*!40000 ALTER TABLE `portlet_content_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `portlet_content_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `portlet_definition`
--

DROP TABLE IF EXISTS `portlet_definition`;
CREATE TABLE `portlet_definition` (
  `ID` mediumint(9) NOT NULL,
  `NAME` varchar(80) default NULL,
  `CLASS_NAME` varchar(255) default NULL,
  `APPLICATION_ID` mediumint(9) NOT NULL,
  `PORTLET_IDENTIFIER` varchar(80) default NULL,
  `EXPIRATION_CACHE` varchar(30) default NULL,
  `RESOURCE_BUNDLE` varchar(255) default NULL,
  `PREFERENCE_VALIDATOR` varchar(255) default NULL,
  `SECURITY_REF` varchar(40) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `portlet_definition`
--

LOCK TABLES `portlet_definition` WRITE;
/*!40000 ALTER TABLE `portlet_definition` DISABLE KEYS */;
/*!40000 ALTER TABLE `portlet_definition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `portlet_entity`
--

DROP TABLE IF EXISTS `portlet_entity`;
CREATE TABLE `portlet_entity` (
  `PEID` mediumint(9) NOT NULL,
  `ID` varchar(255) NOT NULL,
  `APP_NAME` varchar(255) NOT NULL,
  `PORTLET_NAME` varchar(255) NOT NULL,
  PRIMARY KEY  (`PEID`),
  UNIQUE KEY `ID` (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `portlet_entity`
--

LOCK TABLES `portlet_entity` WRITE;
/*!40000 ALTER TABLE `portlet_entity` DISABLE KEYS */;
/*!40000 ALTER TABLE `portlet_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `portlet_statistics`
--

DROP TABLE IF EXISTS `portlet_statistics`;
CREATE TABLE `portlet_statistics` (
  `IPADDRESS` varchar(80) default NULL,
  `USER_NAME` varchar(80) default NULL,
  `TIME_STAMP` timestamp NOT NULL,
  `PAGE` varchar(80) default NULL,
  `PORTLET` varchar(255) default NULL,
  `STATUS` mediumint(9) default NULL,
  `ELAPSED_TIME` bigint(20) default NULL
) ENGINE=MyISAM;

--
-- Dumping data for table `portlet_statistics`
--

LOCK TABLES `portlet_statistics` WRITE;
/*!40000 ALTER TABLE `portlet_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `portlet_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prefs_node`
--

DROP TABLE IF EXISTS `prefs_node`;
CREATE TABLE `prefs_node` (
  `NODE_ID` mediumint(9) NOT NULL,
  `PARENT_NODE_ID` mediumint(9) default NULL,
  `NODE_NAME` varchar(100) default NULL,
  `NODE_TYPE` smallint(6) default NULL,
  `FULL_PATH` varchar(254) default NULL,
  `CREATION_DATE` timestamp NOT NULL,
  `MODIFIED_DATE` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`NODE_ID`),
  KEY `PARENT_NODE_ID` (`PARENT_NODE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `prefs_node`
--

LOCK TABLES `prefs_node` WRITE;
/*!40000 ALTER TABLE `prefs_node` DISABLE KEYS */;
INSERT INTO `prefs_node` VALUES (1,NULL,'',1,'/','2007-06-08 13:57:04','2007-06-08 13:57:04'),(2,NULL,'',0,'/','2007-06-08 13:57:04','2007-06-08 13:57:04'),(3,2,'role',0,'/role','2007-06-08 13:57:07','2007-06-08 13:57:07'),(4,3,'admin',0,'/role/admin','2007-06-08 13:57:07','2007-06-08 13:57:07'),(5,3,'guest',0,'/role/guest','2007-06-08 13:57:07','2007-06-08 13:57:07'),(6,3,'user',0,'/role/user','2007-06-08 13:57:07','2007-06-08 13:57:07'),(7,3,'dev',0,'/role/dev','2007-06-08 13:57:07','2007-06-08 13:57:07'),(8,3,'devmgr',0,'/role/devmgr','2007-06-08 13:57:07','2007-06-08 13:57:07'),(9,2,'user',0,'/user','2007-06-08 13:57:07','2007-06-08 13:57:07'),(10,9,'admin',0,'/user/admin','2007-06-08 13:57:07','2007-06-08 13:57:07'),(11,10,'userinfo',0,'/user/admin/userinfo','2007-06-08 13:57:08','2007-06-08 13:57:08'),(12,9,'guest',0,'/user/guest','2007-06-08 13:57:08','2007-06-08 13:57:08'),(13,12,'userinfo',0,'/user/guest/userinfo','2007-06-08 13:57:08','2007-06-08 13:57:08'),(14,9,'devmgr',0,'/user/devmgr','2007-06-08 13:57:08','2007-06-08 13:57:08'),(15,14,'userinfo',0,'/user/devmgr/userinfo','2007-06-08 13:57:08','2007-06-08 13:57:08'),(16,3,'admin-portal',0,'/role/admin-portal','2007-06-08 13:57:08','2007-06-08 13:57:08'),(17,3,'admin-partner',0,'/role/admin-partner','2007-06-08 13:57:08','2007-06-08 13:57:08'),(18,3,'admin-provider',0,'/role/admin-provider','2007-06-08 13:57:08','2007-06-08 13:57:08');
/*!40000 ALTER TABLE `prefs_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prefs_property_value`
--

DROP TABLE IF EXISTS `prefs_property_value`;
CREATE TABLE `prefs_property_value` (
  `PROPERTY_VALUE_ID` mediumint(9) NOT NULL,
  `NODE_ID` mediumint(9) default NULL,
  `PROPERTY_NAME` varchar(100) default NULL,
  `PROPERTY_VALUE` varchar(254) default NULL,
  `CREATION_DATE` timestamp NOT NULL,
  `MODIFIED_DATE` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`PROPERTY_VALUE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `prefs_property_value`
--

LOCK TABLES `prefs_property_value` WRITE;
/*!40000 ALTER TABLE `prefs_property_value` DISABLE KEYS */;
INSERT INTO `prefs_property_value` VALUES (1,11,'user.name.given','System','2007-06-08 13:57:08','2007-06-08 13:57:08'),(2,11,'user.name.family','Administrator','2007-06-08 13:57:08','2007-06-08 13:57:08'),(3,15,'user.name.given','Dev','2007-06-08 13:57:08','2007-06-08 13:57:08'),(4,15,'user.name.family','Manager','2007-06-08 13:57:08','2007-06-08 13:57:08');
/*!40000 ALTER TABLE `prefs_property_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `principal_permission`
--

DROP TABLE IF EXISTS `principal_permission`;
CREATE TABLE `principal_permission` (
  `PRINCIPAL_ID` mediumint(9) NOT NULL,
  `PERMISSION_ID` mediumint(9) NOT NULL,
  PRIMARY KEY  (`PRINCIPAL_ID`,`PERMISSION_ID`),
  KEY `PERMISSION_ID` (`PERMISSION_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `principal_permission`
--

LOCK TABLES `principal_permission` WRITE;
/*!40000 ALTER TABLE `principal_permission` DISABLE KEYS */;
INSERT INTO `principal_permission` VALUES (1,1),(3,2),(6,3);
/*!40000 ALTER TABLE `principal_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `principal_rule_assoc`
--

DROP TABLE IF EXISTS `principal_rule_assoc`;
CREATE TABLE `principal_rule_assoc` (
  `PRINCIPAL_NAME` varchar(80) NOT NULL,
  `LOCATOR_NAME` varchar(80) NOT NULL,
  `RULE_ID` varchar(80) NOT NULL,
  PRIMARY KEY  (`PRINCIPAL_NAME`,`LOCATOR_NAME`)
) ENGINE=MyISAM;

--
-- Dumping data for table `principal_rule_assoc`
--

LOCK TABLES `principal_rule_assoc` WRITE;
/*!40000 ALTER TABLE `principal_rule_assoc` DISABLE KEYS */;
INSERT INTO `principal_rule_assoc` VALUES ('guest','page','j2'),('devmgr','page','user-role-fallback'),('admin','page','role-fallback');
/*!40000 ALTER TABLE `principal_rule_assoc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_page_assoc`
--

DROP TABLE IF EXISTS `profile_page_assoc`;
CREATE TABLE `profile_page_assoc` (
  `LOCATOR_HASH` varchar(40) NOT NULL,
  `PAGE_ID` varchar(80) NOT NULL,
  UNIQUE KEY `LOCATOR_HASH` (`LOCATOR_HASH`,`PAGE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `profile_page_assoc`
--

LOCK TABLES `profile_page_assoc` WRITE;
/*!40000 ALTER TABLE `profile_page_assoc` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_page_assoc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profiling_rule`
--

DROP TABLE IF EXISTS `profiling_rule`;
CREATE TABLE `profiling_rule` (
  `RULE_ID` varchar(80) NOT NULL,
  `CLASS_NAME` varchar(100) NOT NULL,
  `TITLE` varchar(100) default NULL,
  PRIMARY KEY  (`RULE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `profiling_rule`
--

LOCK TABLES `profiling_rule` WRITE;
/*!40000 ALTER TABLE `profiling_rule` DISABLE KEYS */;
INSERT INTO `profiling_rule` VALUES ('group-fallback','org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule','A role based fallback algorithm based on Jetspeed-1 group-based fallback'),('ip-address','org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule','Resolves pages based on the clients remote IP address.'),('j1','org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule','The default profiling rule following the Jetspeed-1 hard-coded profiler fallback algorithm.'),('j2','org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule','The default profiling rule for users and mediatype minus language and country.'),('path','org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule','use a path to locate.'),('role-fallback','org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule','A role based fallback algorithm based on Jetspeed-1 role-based fallback'),('role-group','org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule','A role based fallback algorithm that searches all groups and roles for a user'),('security','org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule','The security profiling rule needed for credential change requirements.'),('user-role-fallback','org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule','A role based fallback algorithm based on Jetspeed-1 role-based fallback'),('user-rolecombo-fallback','org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule','A role based fallback algorithm based on Jetspeed-1 role-based fallback');
/*!40000 ALTER TABLE `profiling_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers` (
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY  (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_blob_triggers`
--

LOCK TABLES `qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars` (
  `CALENDAR_NAME` varchar(80) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY  (`CALENDAR_NAME`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_calendars`
--

LOCK TABLES `qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers` (
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `CRON_EXPRESSION` varchar(80) NOT NULL,
  `TIME_ZONE_ID` varchar(80) default NULL,
  PRIMARY KEY  (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_cron_triggers`
--

LOCK TABLES `qrtz_cron_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_cron_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_cron_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers` (
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `INSTANCE_NAME` varchar(80) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(80) default NULL,
  `JOB_GROUP` varchar(80) default NULL,
  `IS_STATEFUL` varchar(1) default NULL,
  `REQUESTS_RECOVERY` varchar(1) default NULL,
  PRIMARY KEY  (`ENTRY_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_fired_triggers`
--

LOCK TABLES `qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details` (
  `JOB_NAME` varchar(80) NOT NULL,
  `JOB_GROUP` varchar(80) NOT NULL,
  `DESCRIPTION` varchar(120) default NULL,
  `JOB_CLASS_NAME` varchar(128) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `IS_STATEFUL` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY  (`JOB_NAME`,`JOB_GROUP`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_job_details`
--

LOCK TABLES `qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `qrtz_job_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_listeners`
--

DROP TABLE IF EXISTS `qrtz_job_listeners`;
CREATE TABLE `qrtz_job_listeners` (
  `JOB_NAME` varchar(80) NOT NULL,
  `JOB_GROUP` varchar(80) NOT NULL,
  `JOB_LISTENER` varchar(80) NOT NULL,
  PRIMARY KEY  (`JOB_NAME`,`JOB_GROUP`,`JOB_LISTENER`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_job_listeners`
--

LOCK TABLES `qrtz_job_listeners` WRITE;
/*!40000 ALTER TABLE `qrtz_job_listeners` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_job_listeners` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks` (
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY  (`LOCK_NAME`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_locks`
--

LOCK TABLES `qrtz_locks` WRITE;
/*!40000 ALTER TABLE `qrtz_locks` DISABLE KEYS */;
INSERT INTO `qrtz_locks` VALUES ('CALENDAR_ACCESS'),('JOB_ACCESS'),('MISFIRE_ACCESS'),('STATE_ACCESS'),('TRIGGER_ACCESS');
/*!40000 ALTER TABLE `qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  PRIMARY KEY  (`TRIGGER_GROUP`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--

LOCK TABLES `qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state` (
  `INSTANCE_NAME` varchar(80) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY  (`INSTANCE_NAME`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_scheduler_state`
--

LOCK TABLES `qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `qrtz_scheduler_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers` (
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(7) NOT NULL,
  PRIMARY KEY  (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_simple_triggers`
--

LOCK TABLES `qrtz_simple_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simple_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simple_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_trigger_listeners`
--

DROP TABLE IF EXISTS `qrtz_trigger_listeners`;
CREATE TABLE `qrtz_trigger_listeners` (
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `TRIGGER_LISTENER` varchar(80) NOT NULL,
  PRIMARY KEY  (`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_LISTENER`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_trigger_listeners`
--

LOCK TABLES `qrtz_trigger_listeners` WRITE;
/*!40000 ALTER TABLE `qrtz_trigger_listeners` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_trigger_listeners` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers` (
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `JOB_NAME` varchar(80) NOT NULL,
  `JOB_GROUP` varchar(80) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `DESCRIPTION` varchar(120) default NULL,
  `NEXT_FIRE_TIME` bigint(13) default NULL,
  `PREV_FIRE_TIME` bigint(13) default NULL,
  `PRIORITY` int(11) default NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) default NULL,
  `CALENDAR_NAME` varchar(80) default NULL,
  `MISFIRE_INSTR` smallint(2) default NULL,
  `JOB_DATA` blob,
  PRIMARY KEY  (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `JOB_NAME` (`JOB_NAME`,`JOB_GROUP`)
) ENGINE=MyISAM;

--
-- Dumping data for table `qrtz_triggers`
--

LOCK TABLES `qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule_criterion`
--

DROP TABLE IF EXISTS `rule_criterion`;
CREATE TABLE `rule_criterion` (
  `CRITERION_ID` varchar(80) NOT NULL,
  `RULE_ID` varchar(80) NOT NULL,
  `FALLBACK_ORDER` mediumint(9) NOT NULL,
  `REQUEST_TYPE` varchar(40) NOT NULL,
  `NAME` varchar(80) NOT NULL,
  `COLUMN_VALUE` varchar(128) default NULL,
  `FALLBACK_TYPE` mediumint(9) default '1',
  PRIMARY KEY  (`CRITERION_ID`),
  KEY `IX_RULE_CRITERION_1` (`RULE_ID`,`FALLBACK_ORDER`)
) ENGINE=MyISAM;

--
-- Dumping data for table `rule_criterion`
--

LOCK TABLES `rule_criterion` WRITE;
/*!40000 ALTER TABLE `rule_criterion` DISABLE KEYS */;
INSERT INTO `rule_criterion` VALUES ('1','group-fallback',0,'group','group',NULL,2),('2','group-fallback',1,'path.session','page','default-page',0),('3','ip-address',0,'ip','ip',NULL,0),('4','j1',0,'path.session','page','default-page',0),('5','j1',1,'group.role.user','user',NULL,0),('6','j1',2,'mediatype','mediatype',NULL,1),('7','j1',3,'language','language',NULL,1),('8','j1',4,'country','country',NULL,1),('9','j2',1,'group.role.user','user',NULL,0),('10','j2',2,'mediatype','mediatype',NULL,1),('11','j2',0,'path.session','page','default-page',0),('12','path',0,'path','path','/',0),('13','role-fallback',0,'role','role',NULL,2),('14','role-fallback',1,'path.session','page','default-page',0),('15','role-group',0,'role','role',NULL,2),('16','role-group',1,'navigation','navigation','/',2),('17','role-group',2,'group','group',NULL,2),('18','security',0,'hard.coded','page','/my-account.psml',0),('19','user-role-fallback',0,'user','user',NULL,2),('20','user-role-fallback',1,'navigation','navigation','/',2),('21','user-role-fallback',2,'role','role',NULL,2),('22','user-role-fallback',3,'path.session','page','default-page',1),('23','user-rolecombo-fallback',0,'user','user',NULL,2),('24','user-rolecombo-fallback',1,'navigation','navigation','/',2),('25','user-rolecombo-fallback',2,'rolecombo','role',NULL,2),('26','user-rolecombo-fallback',3,'path.session','page','default-page',1);
/*!40000 ALTER TABLE `rule_criterion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security_credential`
--

DROP TABLE IF EXISTS `security_credential`;
CREATE TABLE `security_credential` (
  `CREDENTIAL_ID` mediumint(9) NOT NULL,
  `PRINCIPAL_ID` mediumint(9) NOT NULL,
  `COLUMN_VALUE` varchar(254) NOT NULL,
  `TYPE` smallint(6) NOT NULL,
  `CLASSNAME` varchar(254) default NULL,
  `UPDATE_REQUIRED` int(11) NOT NULL,
  `IS_ENCODED` int(11) NOT NULL,
  `IS_ENABLED` int(11) NOT NULL,
  `AUTH_FAILURES` smallint(6) NOT NULL,
  `IS_EXPIRED` int(11) NOT NULL,
  `CREATION_DATE` timestamp NOT NULL,
  `MODIFIED_DATE` timestamp NOT NULL default '0000-00-00 00:00:00',
  `PREV_AUTH_DATE` timestamp NOT NULL default '0000-00-00 00:00:00',
  `LAST_AUTH_DATE` timestamp NOT NULL default '0000-00-00 00:00:00',
  `EXPIRATION_DATE` datetime default NULL,
  PRIMARY KEY  (`CREDENTIAL_ID`),
  KEY `PRINCIPAL_ID` (`PRINCIPAL_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `security_credential`
--

LOCK TABLES `security_credential` WRITE;
/*!40000 ALTER TABLE `security_credential` DISABLE KEYS */;
INSERT INTO `security_credential` VALUES (1,6,'liiHgKcA1sEBisdWUN9fLEc2gBo=',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,1,1,0,0,'2007-06-08 13:57:07','2007-06-08 13:57:08','2007-06-08 13:57:08','2007-06-08 13:57:07',NULL),(2,8,'BbKbeW83cuCk0x/yyqL+WyKSUxE=',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,1,1,0,0,'2007-06-08 13:57:08','2007-06-08 13:57:08','2007-06-08 13:57:08','2007-06-08 13:57:08',NULL);
/*!40000 ALTER TABLE `security_credential` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security_group_role`
--

DROP TABLE IF EXISTS `security_group_role`;
CREATE TABLE `security_group_role` (
  `GROUP_ID` mediumint(9) NOT NULL,
  `ROLE_ID` mediumint(9) NOT NULL,
  PRIMARY KEY  (`GROUP_ID`,`ROLE_ID`),
  KEY `ROLE_ID` (`ROLE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `security_group_role`
--

LOCK TABLES `security_group_role` WRITE;
/*!40000 ALTER TABLE `security_group_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `security_group_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security_permission`
--

DROP TABLE IF EXISTS `security_permission`;
CREATE TABLE `security_permission` (
  `PERMISSION_ID` mediumint(9) NOT NULL,
  `CLASSNAME` varchar(254) NOT NULL,
  `NAME` varchar(254) NOT NULL,
  `ACTIONS` varchar(254) NOT NULL,
  `CREATION_DATE` timestamp NOT NULL,
  `MODIFIED_DATE` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`PERMISSION_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `security_permission`
--

LOCK TABLES `security_permission` WRITE;
/*!40000 ALTER TABLE `security_permission` DISABLE KEYS */;
INSERT INTO `security_permission` VALUES (1,'org.apache.jetspeed.security.PortletPermission','j2-admin::*','view, edit','2007-06-08 13:57:08','2007-06-08 13:57:08'),(2,'org.apache.jetspeed.security.PortletPermission','jetspeed-layouts::*','view, edit','2007-06-08 13:57:08','2007-06-08 13:57:08'),(3,'de.ingrid.portal.security.permission.IngridPortalPermission','admin','','2007-06-08 13:57:08','2007-06-08 13:57:08'),(4,'de.ingrid.portal.security.permission.IngridPortalPermission','admin.portal','','2007-06-08 13:57:08','2007-06-08 13:57:08'),(5,'de.ingrid.portal.security.permission.IngridPortalPermission','admin.portal.partner','','2007-06-08 13:57:08','2007-06-08 13:57:08'),(6,'de.ingrid.portal.security.permission.IngridPortalPermission','admin.portal.partner.provider.index','','2007-06-08 13:57:08','2007-06-08 13:57:08'),(7,'de.ingrid.portal.security.permission.IngridPortalPermission','admin.portal.partner.provider.catalog','','2007-06-08 13:57:08','2007-06-08 13:57:08');
/*!40000 ALTER TABLE `security_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security_principal`
--

DROP TABLE IF EXISTS `security_principal`;
CREATE TABLE `security_principal` (
  `PRINCIPAL_ID` mediumint(9) NOT NULL,
  `CLASSNAME` varchar(254) NOT NULL,
  `IS_MAPPING_ONLY` int(11) NOT NULL,
  `IS_ENABLED` int(11) NOT NULL,
  `FULL_PATH` varchar(254) NOT NULL,
  `CREATION_DATE` timestamp NOT NULL,
  `MODIFIED_DATE` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`PRINCIPAL_ID`),
  UNIQUE KEY `FULL_PATH` (`FULL_PATH`)
) ENGINE=MyISAM;

--
-- Dumping data for table `security_principal`
--

LOCK TABLES `security_principal` WRITE;
/*!40000 ALTER TABLE `security_principal` DISABLE KEYS */;
INSERT INTO `security_principal` VALUES (1,'org.apache.jetspeed.security.InternalRolePrincipalImpl',0,1,'/role/admin','2007-06-08 13:57:07','2007-06-08 13:57:08'),(2,'org.apache.jetspeed.security.InternalRolePrincipalImpl',0,1,'/role/guest','2007-06-08 13:57:07','2007-06-08 13:57:07'),(3,'org.apache.jetspeed.security.InternalRolePrincipalImpl',0,1,'/role/user','2007-06-08 13:57:07','2007-06-08 13:57:08'),(4,'org.apache.jetspeed.security.InternalRolePrincipalImpl',0,1,'/role/dev','2007-06-08 13:57:07','2007-06-08 13:57:07'),(5,'org.apache.jetspeed.security.InternalRolePrincipalImpl',0,1,'/role/devmgr','2007-06-08 13:57:07','2007-06-08 13:57:07'),(6,'org.apache.jetspeed.security.InternalUserPrincipalImpl',0,1,'/user/admin','2007-06-08 13:57:07','2007-06-08 13:57:08'),(7,'org.apache.jetspeed.security.InternalUserPrincipalImpl',0,1,'/user/guest','2007-06-08 13:57:08','2007-06-08 13:57:08'),(8,'org.apache.jetspeed.security.InternalUserPrincipalImpl',0,1,'/user/devmgr','2007-06-08 13:57:08','2007-06-08 13:57:08'),(9,'org.apache.jetspeed.security.InternalRolePrincipalImpl',0,1,'/role/admin-portal','2007-06-08 13:57:08','2007-06-08 13:57:08'),(10,'org.apache.jetspeed.security.InternalRolePrincipalImpl',0,1,'/role/admin-partner','2007-06-08 13:57:08','2007-06-08 13:57:08'),(11,'org.apache.jetspeed.security.InternalRolePrincipalImpl',0,1,'/role/admin-provider','2007-06-08 13:57:08','2007-06-08 13:57:08');
/*!40000 ALTER TABLE `security_principal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security_role`
--

DROP TABLE IF EXISTS `security_role`;
CREATE TABLE `security_role` (
  `ID` mediumint(9) NOT NULL,
  `WEB_APPLICATION_ID` mediumint(9) NOT NULL,
  `ROLE_NAME` varchar(150) NOT NULL,
  `DESCRIPTION` varchar(150) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `security_role`
--

LOCK TABLES `security_role` WRITE;
/*!40000 ALTER TABLE `security_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `security_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security_role_reference`
--

DROP TABLE IF EXISTS `security_role_reference`;
CREATE TABLE `security_role_reference` (
  `ID` mediumint(9) NOT NULL,
  `PORTLET_DEFINITION_ID` mediumint(9) NOT NULL,
  `ROLE_NAME` varchar(150) NOT NULL,
  `ROLE_LINK` varchar(150) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `security_role_reference`
--

LOCK TABLES `security_role_reference` WRITE;
/*!40000 ALTER TABLE `security_role_reference` DISABLE KEYS */;
/*!40000 ALTER TABLE `security_role_reference` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security_user_group`
--

DROP TABLE IF EXISTS `security_user_group`;
CREATE TABLE `security_user_group` (
  `USER_ID` mediumint(9) NOT NULL,
  `GROUP_ID` mediumint(9) NOT NULL,
  PRIMARY KEY  (`USER_ID`,`GROUP_ID`),
  KEY `GROUP_ID` (`GROUP_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `security_user_group`
--

LOCK TABLES `security_user_group` WRITE;
/*!40000 ALTER TABLE `security_user_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `security_user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security_user_role`
--

DROP TABLE IF EXISTS `security_user_role`;
CREATE TABLE `security_user_role` (
  `USER_ID` mediumint(9) NOT NULL,
  `ROLE_ID` mediumint(9) NOT NULL,
  PRIMARY KEY  (`USER_ID`,`ROLE_ID`),
  KEY `ROLE_ID` (`ROLE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `security_user_role`
--

LOCK TABLES `security_user_role` WRITE;
/*!40000 ALTER TABLE `security_user_role` DISABLE KEYS */;
INSERT INTO `security_user_role` VALUES (6,1),(6,3),(7,2),(8,3),(8,4),(8,5);
/*!40000 ALTER TABLE `security_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sso_cookie`
--

DROP TABLE IF EXISTS `sso_cookie`;
CREATE TABLE `sso_cookie` (
  `COOKIE_ID` mediumint(9) NOT NULL,
  `COOKIE` varchar(1024) NOT NULL,
  `CREATE_DATE` timestamp NOT NULL,
  PRIMARY KEY  (`COOKIE_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `sso_cookie`
--

LOCK TABLES `sso_cookie` WRITE;
/*!40000 ALTER TABLE `sso_cookie` DISABLE KEYS */;
/*!40000 ALTER TABLE `sso_cookie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sso_cookie_to_remote`
--

DROP TABLE IF EXISTS `sso_cookie_to_remote`;
CREATE TABLE `sso_cookie_to_remote` (
  `COOKIE_ID` mediumint(9) NOT NULL,
  `REMOTE_PRINCIPAL_ID` mediumint(9) NOT NULL,
  PRIMARY KEY  (`COOKIE_ID`,`REMOTE_PRINCIPAL_ID`),
  KEY `REMOTE_PRINCIPAL_ID` (`REMOTE_PRINCIPAL_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `sso_cookie_to_remote`
--

LOCK TABLES `sso_cookie_to_remote` WRITE;
/*!40000 ALTER TABLE `sso_cookie_to_remote` DISABLE KEYS */;
/*!40000 ALTER TABLE `sso_cookie_to_remote` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sso_principal_to_remote`
--

DROP TABLE IF EXISTS `sso_principal_to_remote`;
CREATE TABLE `sso_principal_to_remote` (
  `PRINCIPAL_ID` mediumint(9) NOT NULL,
  `REMOTE_PRINCIPAL_ID` mediumint(9) NOT NULL,
  PRIMARY KEY  (`PRINCIPAL_ID`,`REMOTE_PRINCIPAL_ID`),
  KEY `REMOTE_PRINCIPAL_ID` (`REMOTE_PRINCIPAL_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `sso_principal_to_remote`
--

LOCK TABLES `sso_principal_to_remote` WRITE;
/*!40000 ALTER TABLE `sso_principal_to_remote` DISABLE KEYS */;
/*!40000 ALTER TABLE `sso_principal_to_remote` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sso_site`
--

DROP TABLE IF EXISTS `sso_site`;
CREATE TABLE `sso_site` (
  `SITE_ID` mediumint(9) NOT NULL,
  `NAME` varchar(254) NOT NULL,
  `URL` varchar(254) NOT NULL,
  `ALLOW_USER_SET` int(11) default '0',
  `REQUIRES_CERTIFICATE` int(11) default '0',
  `CHALLENGE_RESPONSE_AUTH` int(11) default '0',
  `FORM_AUTH` int(11) default '0',
  `FORM_USER_FIELD` varchar(128) default NULL,
  `FORM_PWD_FIELD` varchar(128) default NULL,
  `REALM` varchar(128) default NULL,
  PRIMARY KEY  (`SITE_ID`),
  UNIQUE KEY `URL` (`URL`)
) ENGINE=MyISAM;

--
-- Dumping data for table `sso_site`
--

LOCK TABLES `sso_site` WRITE;
/*!40000 ALTER TABLE `sso_site` DISABLE KEYS */;
/*!40000 ALTER TABLE `sso_site` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sso_site_to_principals`
--

DROP TABLE IF EXISTS `sso_site_to_principals`;
CREATE TABLE `sso_site_to_principals` (
  `SITE_ID` mediumint(9) NOT NULL,
  `PRINCIPAL_ID` mediumint(9) NOT NULL,
  PRIMARY KEY  (`SITE_ID`,`PRINCIPAL_ID`),
  KEY `PRINCIPAL_ID` (`PRINCIPAL_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `sso_site_to_principals`
--

LOCK TABLES `sso_site_to_principals` WRITE;
/*!40000 ALTER TABLE `sso_site_to_principals` DISABLE KEYS */;
/*!40000 ALTER TABLE `sso_site_to_principals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sso_site_to_remote`
--

DROP TABLE IF EXISTS `sso_site_to_remote`;
CREATE TABLE `sso_site_to_remote` (
  `SITE_ID` mediumint(9) NOT NULL,
  `PRINCIPAL_ID` mediumint(9) NOT NULL,
  PRIMARY KEY  (`SITE_ID`,`PRINCIPAL_ID`),
  KEY `PRINCIPAL_ID` (`PRINCIPAL_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `sso_site_to_remote`
--

LOCK TABLES `sso_site_to_remote` WRITE;
/*!40000 ALTER TABLE `sso_site_to_remote` DISABLE KEYS */;
/*!40000 ALTER TABLE `sso_site_to_remote` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_attribute`
--

DROP TABLE IF EXISTS `user_attribute`;
CREATE TABLE `user_attribute` (
  `ID` mediumint(9) NOT NULL,
  `APPLICATION_ID` mediumint(9) NOT NULL,
  `NAME` varchar(150) default NULL,
  `DESCRIPTION` varchar(150) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `APPLICATION_ID` (`APPLICATION_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `user_attribute`
--

LOCK TABLES `user_attribute` WRITE;
/*!40000 ALTER TABLE `user_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_attribute_ref`
--

DROP TABLE IF EXISTS `user_attribute_ref`;
CREATE TABLE `user_attribute_ref` (
  `ID` mediumint(9) NOT NULL,
  `APPLICATION_ID` mediumint(9) NOT NULL,
  `NAME` varchar(150) default NULL,
  `NAME_LINK` varchar(150) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `APPLICATION_ID` (`APPLICATION_ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `user_attribute_ref`
--

LOCK TABLES `user_attribute_ref` WRITE;
/*!40000 ALTER TABLE `user_attribute_ref` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_attribute_ref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_statistics`
--

DROP TABLE IF EXISTS `user_statistics`;
CREATE TABLE `user_statistics` (
  `IPADDRESS` varchar(80) default NULL,
  `USER_NAME` varchar(80) default NULL,
  `TIME_STAMP` timestamp NOT NULL,
  `STATUS` mediumint(9) default NULL,
  `ELAPSED_TIME` bigint(20) default NULL
) ENGINE=MyISAM;

--
-- Dumping data for table `user_statistics`
--

LOCK TABLES `user_statistics` WRITE;
/*!40000 ALTER TABLE `user_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_application`
--

DROP TABLE IF EXISTS `web_application`;
CREATE TABLE `web_application` (
  `ID` mediumint(9) NOT NULL,
  `CONTEXT_ROOT` varchar(255) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM;

--
-- Dumping data for table `web_application`
--

LOCK TABLES `web_application` WRITE;
/*!40000 ALTER TABLE `web_application` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_application` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2007-06-08 14:10:27
