
-- Execute on ORACLE portal database after migration to undo changes for migration

-- ADD DEFAULT SYSDATE again

ALTER TABLE ingrid_anniversary MODIFY fetched DEFAULT SYSDATE;

ALTER TABLE ingrid_cms MODIFY item_changed DEFAULT SYSDATE;

ALTER TABLE ingrid_cms_item MODIFY item_changed DEFAULT SYSDATE;

ALTER TABLE ingrid_lookup MODIFY item_date DEFAULT SYSDATE;

ALTER TABLE ingrid_newsletter_data MODIFY created DEFAULT SYSDATE;

ALTER TABLE ingrid_principal_pref MODIFY modified_date DEFAULT SYSDATE;

ALTER TABLE ingrid_rss_source MODIFY lastUpdate DEFAULT SYSDATE;

ALTER TABLE  ingrid_rss_store MODIFY published_date DEFAULT SYSDATE;

ALTER TABLE   ingrid_tiny_url MODIFY tiny_date DEFAULT SYSDATE;
