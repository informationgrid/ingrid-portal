
-- Execute on MYSQL portal database before migration


-- REMOVE DEFAULT CURRENT_TIMESTAMP, not migrated correctly

ALTER TABLE ingrid_anniversary ALTER fetched DROP DEFAULT;

ALTER TABLE ingrid_cms ALTER item_changed DROP DEFAULT;

ALTER TABLE ingrid_cms_item ALTER item_changed DROP DEFAULT;

ALTER TABLE ingrid_lookup ALTER item_date DROP DEFAULT;

ALTER TABLE ingrid_newsletter_data ALTER created DROP DEFAULT;

ALTER TABLE ingrid_principal_pref ALTER modified_date DROP DEFAULT;

ALTER TABLE ingrid_rss_source ALTER lastUpdate DROP DEFAULT;

ALTER TABLE ingrid_rss_store ALTER published_date DROP DEFAULT;

ALTER TABLE ingrid_tiny_url ALTER tiny_date DROP DEFAULT;
