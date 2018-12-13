-- Grundlegendes Schema für Portal 4.2.1 MySQL

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


CREATE TABLE `admin_activity` (
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

CREATE TABLE `capability` (
  `CAPABILITY_ID` int(11) NOT NULL,
  `CAPABILITY` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CAPABILITY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `client` (
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

CREATE TABLE `client_to_capability` (
  `CLIENT_ID` int(11) NOT NULL,
  `CAPABILITY_ID` int(11) NOT NULL,
  PRIMARY KEY (`CLIENT_ID`,`CAPABILITY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `client_to_mimetype` (
  `CLIENT_ID` int(11) NOT NULL,
  `MIMETYPE_ID` int(11) NOT NULL,
  PRIMARY KEY (`CLIENT_ID`,`MIMETYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `clubs` (
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

CREATE TABLE `custom_portlet_mode` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `CUSTOM_NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `MAPPED_NAME` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PORTAL_MANAGED` smallint(6) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `custom_window_state` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `CUSTOM_NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `MAPPED_NAME` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `event_alias` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `event_definition` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE_TYPE` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `filtered_portlet` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `filter_lifecycle` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `filter_mapping` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `FILTER_NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `folder` (
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

CREATE TABLE `folder_constraint` (
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

CREATE TABLE `folder_constraints_ref` (
  `CONSTRAINTS_REF_ID` int(11) NOT NULL,
  `FOLDER_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `UN_FOLDER_CONSTRAINTS_REF_1` (`FOLDER_ID`,`NAME`),
  KEY `IX_FOLDER_CONSTRAINTS_REF_1` (`FOLDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `folder_menu` (
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

CREATE TABLE `folder_menu_metadata` (
  `METADATA_ID` int(11) NOT NULL,
  `MENU_ID` int(11) NOT NULL,
  `NAME` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`METADATA_ID`),
  UNIQUE KEY `UN_FOLDER_MENU_METADATA_1` (`MENU_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_FOLDER_MENU_METADATA_1` (`MENU_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `folder_metadata` (
  `METADATA_ID` int(11) NOT NULL,
  `FOLDER_ID` int(11) NOT NULL,
  `NAME` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`METADATA_ID`),
  UNIQUE KEY `UN_FOLDER_METADATA_1` (`FOLDER_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_FOLDER_METADATA_1` (`FOLDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `folder_order` (
  `ORDER_ID` int(11) NOT NULL,
  `FOLDER_ID` int(11) NOT NULL,
  `SORT_ORDER` int(11) NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ORDER_ID`),
  UNIQUE KEY `UN_FOLDER_ORDER_1` (`FOLDER_ID`,`NAME`),
  KEY `IX_FOLDER_ORDER_1` (`FOLDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `fragment` (
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

CREATE TABLE `fragment_constraint` (
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

CREATE TABLE `fragment_constraints_ref` (
  `CONSTRAINTS_REF_ID` int(11) NOT NULL,
  `FRAGMENT_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `UN_FRAGMENT_CONSTRAINTS_REF_1` (`FRAGMENT_ID`,`NAME`),
  KEY `IX_FRAGMENT_CONSTRAINTS_REF_1` (`FRAGMENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `fragment_pref` (
  `PREF_ID` int(11) NOT NULL,
  `FRAGMENT_ID` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `IS_READ_ONLY` smallint(6) NOT NULL,
  PRIMARY KEY (`PREF_ID`),
  UNIQUE KEY `UN_FRAGMENT_PREF_1` (`FRAGMENT_ID`,`NAME`),
  KEY `IX_FRAGMENT_PREF_1` (`FRAGMENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `fragment_pref_value` (
  `PREF_VALUE_ID` int(11) NOT NULL,
  `PREF_ID` int(11) NOT NULL,
  `VALUE_ORDER` int(11) NOT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`PREF_VALUE_ID`),
  KEY `IX_FRAGMENT_PREF_VALUE_1` (`PREF_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `fragment_prop` (
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

CREATE TABLE `ingrid_anniversary` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_chron_eventtypes` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `form_value` varchar(255) NOT NULL DEFAULT '',
  `query_value` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_cms` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `item_key` varchar(255) NOT NULL DEFAULT '',
  `item_description` varchar(255) DEFAULT NULL,
  `item_changed` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `item_changed_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_cms_item` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `fk_ingrid_cms_id` mediumint(9) NOT NULL DEFAULT '0',
  `item_lang` varchar(6) NOT NULL DEFAULT '',
  `item_title` varchar(255) DEFAULT NULL,
  `item_value` text,
  `item_changed` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `item_changed_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_env_topic` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `form_value` varchar(255) NOT NULL DEFAULT '',
  `query_value` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_lookup` (
  `id` mediumint(9) NOT NULL DEFAULT '0',
  `item_key` varchar(255) NOT NULL DEFAULT '',
  `item_value` varchar(255) DEFAULT NULL,
  `item_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_measures_rubric` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `form_value` varchar(255) NOT NULL DEFAULT '',
  `query_value` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_newsletter_data` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_partner` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `ident` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_principal_pref` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `principal_name` varchar(251) NOT NULL DEFAULT '',
  `pref_name` varchar(251) NOT NULL DEFAULT '',
  `pref_value` mediumtext,
  `modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_provider` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `ident` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `url` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  `sortkey_partner` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_rss_source` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_rss_store` (
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

CREATE TABLE `ingrid_service_rubric` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `form_value` varchar(255) NOT NULL DEFAULT '',
  `query_value` varchar(255) NOT NULL DEFAULT '',
  `sortkey` mediumint(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE `ingrid_tiny_url` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_ref` varchar(254) NOT NULL,
  `tiny_key` varchar(254) NOT NULL,
  `tiny_name` varchar(254) NOT NULL,
  `tiny_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `tiny_config` mediumtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `jetspeed_service` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `language` (
  `ID` int(11) NOT NULL,
  `PORTLET_ID` int(11) NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `SUPPORTED_LOCALE` smallint(6) NOT NULL,
  `TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SHORT_TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `KEYWORDS` mediumtext COLLATE utf8_unicode_ci,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `link` (
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

CREATE TABLE `link_constraint` (
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

CREATE TABLE `link_constraints_ref` (
  `CONSTRAINTS_REF_ID` int(11) NOT NULL,
  `LINK_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `UN_LINK_CONSTRAINTS_REF_1` (`LINK_ID`,`NAME`),
  KEY `IX_LINK_CONSTRAINTS_REF_1` (`LINK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `link_metadata` (
  `METADATA_ID` int(11) NOT NULL,
  `LINK_ID` int(11) NOT NULL,
  `NAME` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`METADATA_ID`),
  UNIQUE KEY `UN_LINK_METADATA_1` (`LINK_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_LINK_METADATA_1` (`LINK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `locale_encoding_mapping` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `ENCODING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `localized_description` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `OWNER_CLASS_NAME` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `DESCRIPTION` mediumtext COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `localized_display_name` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `OWNER_CLASS_NAME` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DISPLAY_NAME` mediumtext COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `mediatype_to_capability` (
  `MEDIATYPE_ID` int(11) NOT NULL,
  `CAPABILITY_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEDIATYPE_ID`,`CAPABILITY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `mediatype_to_mimetype` (
  `MEDIATYPE_ID` int(11) NOT NULL,
  `MIMETYPE_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEDIATYPE_ID`,`MIMETYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `media_type` (
  `MEDIATYPE_ID` int(11) NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `CHARACTER_SET` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TITLE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `DESCRIPTION` mediumtext COLLATE utf8_unicode_ci,
  PRIMARY KEY (`MEDIATYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `mimetype` (
  `MIMETYPE_ID` int(11) NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`MIMETYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `named_parameter` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `ojb_dlist` (
  `ID` int(11) NOT NULL,
  `SIZE_` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `ojb_dlist_entries` (
  `ID` int(11) NOT NULL,
  `DLIST_ID` int(11) DEFAULT NULL,
  `POSITION_` int(11) DEFAULT NULL,
  `OID_` mediumblob,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `ojb_dmap` (
  `ID` int(11) NOT NULL,
  `SIZE_` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `ojb_dset` (
  `ID` int(11) NOT NULL,
  `SIZE_` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `ojb_dset_entries` (
  `ID` int(11) NOT NULL,
  `DLIST_ID` int(11) DEFAULT NULL,
  `POSITION_` int(11) DEFAULT NULL,
  `OID_` mediumblob,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `ojb_hl_seq` (
  `TABLENAME` varchar(175) COLLATE utf8_unicode_ci NOT NULL,
  `FIELDNAME` varchar(70) COLLATE utf8_unicode_ci NOT NULL,
  `MAX_KEY` int(11) DEFAULT NULL,
  `GRAB_SIZE` int(11) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  PRIMARY KEY (`TABLENAME`,`FIELDNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `ojb_lockentry` (
  `OID_` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
  `TX_ID` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `TIMESTAMP_` datetime DEFAULT NULL,
  `ISOLATIONLEVEL` int(11) DEFAULT NULL,
  `LOCKTYPE` int(11) DEFAULT NULL,
  PRIMARY KEY (`OID_`,`TX_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `ojb_nrm` (
  `NAME` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
  `OID_` mediumblob,
  PRIMARY KEY (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `page` (
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

CREATE TABLE `page_constraint` (
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

CREATE TABLE `page_constraints_ref` (
  `CONSTRAINTS_REF_ID` int(11) NOT NULL,
  `PAGE_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `UN_PAGE_CONSTRAINTS_REF_1` (`PAGE_ID`,`NAME`),
  KEY `IX_PAGE_CONSTRAINTS_REF_1` (`PAGE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `page_menu` (
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

CREATE TABLE `page_menu_metadata` (
  `METADATA_ID` int(11) NOT NULL,
  `MENU_ID` int(11) NOT NULL,
  `NAME` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`METADATA_ID`),
  UNIQUE KEY `UN_PAGE_MENU_METADATA_1` (`MENU_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_PAGE_MENU_METADATA_1` (`MENU_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `page_metadata` (
  `METADATA_ID` int(11) NOT NULL,
  `PAGE_ID` int(11) NOT NULL,
  `NAME` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `VALUE` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`METADATA_ID`),
  UNIQUE KEY `UN_PAGE_METADATA_1` (`PAGE_ID`,`NAME`,`LOCALE`,`VALUE`),
  KEY `IX_PAGE_METADATA_1` (`PAGE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `page_security` (
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

CREATE TABLE `page_sec_constraints_def` (
  `CONSTRAINTS_DEF_ID` int(11) NOT NULL,
  `PAGE_SECURITY_ID` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_DEF_ID`),
  UNIQUE KEY `UN_PAGE_SEC_CONSTRAINTS_DEF_1` (`PAGE_SECURITY_ID`,`NAME`),
  KEY `IX_PAGE_SEC_CONSTRAINTS_DEF_1` (`PAGE_SECURITY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `page_sec_constraints_ref` (
  `CONSTRAINTS_REF_ID` int(11) NOT NULL,
  `PAGE_SECURITY_ID` int(11) NOT NULL,
  `APPLY_ORDER` int(11) NOT NULL,
  `NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CONSTRAINTS_REF_ID`),
  UNIQUE KEY `UN_PAGE_SEC_CONSTRAINTS_REF_1` (`PAGE_SECURITY_ID`,`NAME`),
  KEY `IX_PAGE_SEC_CONSTRAINTS_REF_1` (`PAGE_SECURITY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `page_sec_constraint_def` (
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

CREATE TABLE `page_statistics` (
  `IPADDRESS` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_NAME` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TIME_STAMP` datetime DEFAULT NULL,
  `PAGE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `ELAPSED_TIME` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `parameter` (
  `PARAMETER_ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `OWNER_CLASS_NAME` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `PARAMETER_VALUE` mediumtext COLLATE utf8_unicode_ci,
  PRIMARY KEY (`PARAMETER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `parameter_alias` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `pa_metadata_fields` (
  `ID` int(11) NOT NULL,
  `OBJECT_ID` int(11) NOT NULL,
  `COLUMN_VALUE` mediumtext COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `pa_security_constraint` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `TRANSPORT` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `pd_metadata_fields` (
  `ID` int(11) NOT NULL,
  `OBJECT_ID` int(11) NOT NULL,
  `COLUMN_VALUE` mediumtext COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `LOCALE_STRING` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `portlet_application` (
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

CREATE TABLE `portlet_definition` (
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

CREATE TABLE `portlet_filter` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `FILTER_NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `FILTER_CLASS` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `portlet_listener` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `LISTENER_CLASS` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `portlet_preference` (
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

CREATE TABLE `portlet_preference_value` (
  `ID` int(11) NOT NULL,
  `PREF_ID` int(11) NOT NULL,
  `IDX` smallint(6) NOT NULL,
  `PREF_VALUE` varchar(4000) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`,`PREF_ID`,`IDX`),
  KEY `IX_PORTLET_PREFERENCE` (`PREF_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `portlet_statistics` (
  `IPADDRESS` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_NAME` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TIME_STAMP` datetime DEFAULT NULL,
  `PAGE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PORTLET` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `ELAPSED_TIME` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `portlet_supports` (
  `SUPPORTS_ID` int(11) NOT NULL,
  `PORTLET_ID` int(11) NOT NULL,
  `MIME_TYPE` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `MODES` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `STATES` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`SUPPORTS_ID`),
  UNIQUE KEY `UK_SUPPORTS` (`PORTLET_ID`,`MIME_TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `principal_permission` (
  `PRINCIPAL_ID` int(11) NOT NULL,
  `PERMISSION_ID` int(11) NOT NULL,
  PRIMARY KEY (`PRINCIPAL_ID`,`PERMISSION_ID`),
  KEY `IX_PRINCIPAL_PERMISSION_1` (`PERMISSION_ID`),
  KEY `IX_PRINCIPAL_PERMISSION_2` (`PRINCIPAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `principal_rule_assoc` (
  `PRINCIPAL_NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `LOCATOR_NAME` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `RULE_ID` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`PRINCIPAL_NAME`,`LOCATOR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `processing_event` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `profile_page_assoc` (
  `LOCATOR_HASH` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `PAGE_ID` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`LOCATOR_HASH`,`PAGE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `profiling_rule` (
  `RULE_ID` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `CLASS_NAME` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `TITLE` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`RULE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `public_parameter` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `IDENTIFIER` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `publishing_event` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `LOCAL_PART` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `NAMESPACE` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `PREFIX` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `qrtz_blob_triggers` (
  `TRIGGER_NAME` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  `BLOB_DATA` blob,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `qrtz_calendars` (
  `CALENDAR_NAME` varchar(80) NOT NULL DEFAULT '',
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`CALENDAR_NAME`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `qrtz_cron_triggers` (
  `TRIGGER_NAME` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  `CRON_EXPRESSION` varchar(80) NOT NULL DEFAULT '',
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `qrtz_fired_triggers` (
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

CREATE TABLE `qrtz_job_details` (
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

CREATE TABLE `qrtz_job_listeners` (
  `JOB_NAME` varchar(80) NOT NULL DEFAULT '',
  `JOB_GROUP` varchar(80) NOT NULL DEFAULT '',
  `JOB_LISTENER` varchar(80) NOT NULL DEFAULT '',
  PRIMARY KEY (`JOB_NAME`,`JOB_GROUP`,`JOB_LISTENER`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `qrtz_locks` (
  `LOCK_NAME` varchar(40) NOT NULL DEFAULT '',
  PRIMARY KEY (`LOCK_NAME`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `qrtz_paused_trigger_grps` (
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  PRIMARY KEY (`TRIGGER_GROUP`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `qrtz_scheduler_state` (
  `INSTANCE_NAME` varchar(80) NOT NULL DEFAULT '',
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL DEFAULT '0',
  `CHECKIN_INTERVAL` bigint(13) NOT NULL DEFAULT '0',
  PRIMARY KEY (`INSTANCE_NAME`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `qrtz_simple_triggers` (
  `TRIGGER_NAME` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  `REPEAT_COUNT` bigint(7) NOT NULL DEFAULT '0',
  `REPEAT_INTERVAL` bigint(12) NOT NULL DEFAULT '0',
  `TIMES_TRIGGERED` bigint(7) NOT NULL DEFAULT '0',
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `qrtz_triggers` (
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

CREATE TABLE `qrtz_trigger_listeners` (
  `TRIGGER_NAME` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_GROUP` varchar(80) NOT NULL DEFAULT '',
  `TRIGGER_LISTENER` varchar(80) NOT NULL DEFAULT '',
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_LISTENER`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `rule_criterion` (
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

CREATE TABLE `runtime_option` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `OWNER_CLASS_NAME` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `runtime_value` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `RVALUE` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `schema_version` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8_unicode_ci NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `secured_portlet` (
  `ID` int(11) NOT NULL,
  `OWNER_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `security_attribute` (
  `ATTR_ID` int(11) NOT NULL,
  `PRINCIPAL_ID` int(11) NOT NULL,
  `ATTR_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `ATTR_VALUE` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ATTR_ID`,`PRINCIPAL_ID`,`ATTR_NAME`),
  KEY `IX_NAME_LOOKUP` (`ATTR_NAME`),
  KEY `IX_PRINCIPAL_ATTR` (`PRINCIPAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `security_credential` (
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

CREATE TABLE `security_domain` (
  `DOMAIN_ID` int(11) NOT NULL,
  `DOMAIN_NAME` varchar(254) COLLATE utf8_unicode_ci DEFAULT NULL,
  `REMOTE` smallint(6) DEFAULT '0',
  `ENABLED` smallint(6) DEFAULT '1',
  `OWNER_DOMAIN_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`DOMAIN_ID`),
  UNIQUE KEY `UIX_DOMAIN_NAME` (`DOMAIN_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `security_permission` (
  `PERMISSION_ID` int(11) NOT NULL,
  `PERMISSION_TYPE` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(254) COLLATE utf8_unicode_ci NOT NULL,
  `ACTIONS` varchar(254) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`PERMISSION_ID`),
  UNIQUE KEY `UIX_SECURITY_PERMISSION` (`PERMISSION_TYPE`,`NAME`,`ACTIONS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `security_principal` (
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

CREATE TABLE `security_principal_assoc` (
  `ASSOC_NAME` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `FROM_PRINCIPAL_ID` int(11) NOT NULL,
  `TO_PRINCIPAL_ID` int(11) NOT NULL,
  PRIMARY KEY (`ASSOC_NAME`,`FROM_PRINCIPAL_ID`,`TO_PRINCIPAL_ID`),
  KEY `IX_TO_PRINCIPAL_ASSOC_LOOKUP` (`ASSOC_NAME`,`TO_PRINCIPAL_ID`),
  KEY `IX_FROM_PRINCIPAL_ASSOC` (`FROM_PRINCIPAL_ID`),
  KEY `IX_TO_PRINCIPAL_ASSOC` (`TO_PRINCIPAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `security_role` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `security_role_reference` (
  `ID` int(11) NOT NULL,
  `PORTLET_DEFINITION_ID` int(11) NOT NULL,
  `ROLE_NAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `ROLE_LINK` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `sso_site` (
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

CREATE TABLE `user_activity` (
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

CREATE TABLE `user_attribute` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `user_attribute_ref` (
  `ID` int(11) NOT NULL,
  `APPLICATION_ID` int(11) NOT NULL,
  `NAME` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  `NAME_LINK` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `user_statistics` (
  `IPADDRESS` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `USER_NAME` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TIME_STAMP` datetime DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `ELAPSED_TIME` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


ALTER TABLE `folder`
  ADD CONSTRAINT `FK_FOLDER_1` FOREIGN KEY (`PARENT_ID`) REFERENCES `folder` (`FOLDER_ID`) ON DELETE CASCADE;
