
-- Execute on ORACLE portal database before migration


-- REMOVE DEFAULT SYSDATE, causes error when migrating

ALTER TABLE ingrid_anniversary MODIFY fetched DEFAULT NULL;

ALTER TABLE ingrid_cms MODIFY item_changed DEFAULT NULL;

ALTER TABLE ingrid_cms_item MODIFY item_changed DEFAULT NULL;

ALTER TABLE ingrid_lookup MODIFY item_date DEFAULT NULL;

ALTER TABLE ingrid_newsletter_data MODIFY created DEFAULT NULL;

ALTER TABLE ingrid_principal_pref MODIFY modified_date DEFAULT NULL;

ALTER TABLE ingrid_rss_source MODIFY lastUpdate DEFAULT NULL;

ALTER TABLE  ingrid_rss_store MODIFY published_date DEFAULT NULL;

ALTER TABLE   ingrid_tiny_url MODIFY tiny_date DEFAULT NULL;
