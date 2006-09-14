DROP TABLE IF EXISTS ingrid_lookup;
CREATE TABLE ingrid_lookup
(
    id MEDIUMINT NOT NULL,
    item_key VARCHAR(255) NOT NULL,
    item_value VARCHAR(255),
    PRIMARY KEY(id)
);

INSERT INTO ingrid_lookup (id, item_key, item_value) VALUES (1, 'ingrid_db_version', '1');

INSERT INTO SECURITY_PRINCIPAL VALUES(1100,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/admin-portal','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(1101,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/admin-partner','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(1102,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/admin-provider','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');

INSERT INTO SECURITY_PRINCIPAL VALUES(1110,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/adminportal','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(1111,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/adminpartner','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(1112,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/adminprovider','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_CREDENTIAL VALUES(1113,1110,'adminportal',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2006-08-22 16:27:12.572','2006-08-22 16:27:12.572',null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(1114,1111,'adminpartner',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2006-08-22 16:27:12.572','2006-08-22 16:27:12.572',null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(1115,1112,'adminprovider',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2006-08-22 16:27:12.572','2006-08-22 16:27:12.572',null,null,null);
INSERT INTO SECURITY_USER_ROLE VALUES(1110,8);
INSERT INTO SECURITY_USER_ROLE VALUES(1110,1100);
INSERT INTO SECURITY_USER_ROLE VALUES(1111,8);
INSERT INTO SECURITY_USER_ROLE VALUES(1111,1101);
INSERT INTO SECURITY_USER_ROLE VALUES(1112,8);
INSERT INTO SECURITY_USER_ROLE VALUES(1112,1102);
INSERT INTO SECURITY_PERMISSION VALUES(1199,'de.ingrid.portal.security.permission.IngridPortalPermission','admin','','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(1200,'de.ingrid.portal.security.permission.IngridPortalPermission','admin.portal','','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(1201,'de.ingrid.portal.security.permission.IngridPortalPermission','admin.portal.partner','','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(1202,'de.ingrid.portal.security.permission.IngridPortalPermission','admin.portal.partner.provider.index','','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(1203,'de.ingrid.portal.security.permission.IngridPortalPermission','admin.portal.partner.provider.catalog','','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(1204,'de.ingrid.portal.security.permission.IngridPartnerPermission','partner.he','','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(1205,'de.ingrid.portal.security.permission.IngridProviderPermission','provider.he_hmulv','','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(1206,'de.ingrid.portal.security.permission.IngridProviderPermission','provider.he_hlug','','2006-08-22 16:27:12.572','2006-08-22 16:27:12.572');
INSERT INTO PRINCIPAL_PERMISSION VALUES(1100,1200);
INSERT INTO PRINCIPAL_PERMISSION VALUES(1111,1201);
INSERT INTO PRINCIPAL_PERMISSION VALUES(1111,1204);
INSERT INTO PRINCIPAL_PERMISSION VALUES(1112,1202);
INSERT INTO PRINCIPAL_PERMISSION VALUES(1112,1203);
INSERT INTO PRINCIPAL_PERMISSION VALUES(1112,1204);
INSERT INTO PRINCIPAL_PERMISSION VALUES(1112,1205);
INSERT INTO PRINCIPAL_PERMISSION VALUES(1112,1206);
INSERT INTO PRINCIPAL_PERMISSION VALUES(1,1199);

DROP TABLE IF EXISTS ingrid_cms;
CREATE TABLE ingrid_cms
(
    id MEDIUMINT NOT NULL,
    item_key VARCHAR(255) NOT NULL,
    item_description VARCHAR(255),
    item_changed TIMESTAMP,
    item_changed_by VARCHAR(255),
    PRIMARY KEY(id)
);
ALTER TABLE `ingrid_cms` CHANGE `id` `id` MEDIUMINT( 9 ) NOT NULL AUTO_INCREMENT

DROP TABLE IF EXISTS ingrid_cms_item;
CREATE TABLE ingrid_cms_item
(
    id MEDIUMINT NOT NULL,
    fk_ingrid_cms_id MEDIUMINT NOT NULL,
    item_lang VARCHAR(6) NOT NULL,
    item_title VARCHAR(255),
    item_value TEXT,
    item_changed TIMESTAMP,
    item_changed_by VARCHAR(255),
    PRIMARY KEY(id)
);
ALTER TABLE `ingrid_cms_item` CHANGE `id` `id` MEDIUMINT( 9 ) NOT NULL AUTO_INCREMENT 

INSERT INTO ingrid_cms (id, item_key, item_description, item_changed, item_changed_by) 
VALUES (1, 'portalu.teaser.inform', 'PortalU informiert Text', NOW(), 'admin');

INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed, item_changed_by) 
VALUES (1, 1, 'de', '', '<p>Besuchen Sie PortalU&reg; vom 6.&nbsp;bis&nbsp;8.&nbsp;September auf dem 20.&nbsp;Internationalen Symposium Umweltinformatik - EnviroInfo 2006 - in Graz</p>
<p class="iconLink">
  <span class="icon"><img src="/ingrid-portal-apps/images/icn_linkextern.gif" alt="externer Link"></span>
  <span><a href="http://enviroinfo.know-center.tugraz.at/" target="_new" title="Link öffnet in neuem Fenster">enviroinfo.know-center.tugraz.at/</a></span>
  <span class="clearer"></span>
</p>', NOW(), 'admin');

INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed, item_changed_by) 
VALUES (2, 1, 'en', '', '<p>Visit PortalU&reg; on September&nbsp;6&nbsp;to&nbsp;8 at the 20th&nbsp;International Symposium on Informatics for Environmental Protection - EnviroInfo 2006 - in Graz (Austria)</p>
<p class="iconLink">
  <span class="icon"><img src="/ingrid-portal-apps/images/icn_linkextern.gif" alt="External Link"></span>
  <span><a href="http://enviroinfo.know-center.tugraz.at/" target="_new" title="Link opens new window">enviroinfo.know-center.tugraz.at/</a></span>
  <span class="clearer"></span>
</p>', NOW(), 'admin');
