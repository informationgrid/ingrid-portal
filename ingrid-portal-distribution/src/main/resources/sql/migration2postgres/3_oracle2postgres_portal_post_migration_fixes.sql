
-- Execute on migrated POSTGRES portal database (from ORACLE) to add stuff removed from migration


-- ADD CURRENT_TIMESTAMP as default

ALTER TABLE ingrid_anniversary ALTER COLUMN fetched SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_cms ALTER COLUMN item_changed SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_cms_item ALTER COLUMN item_changed SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_lookup ALTER COLUMN item_date SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_newsletter_data ALTER COLUMN created SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_principal_pref ALTER COLUMN modified_date SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_rss_source ALTER COLUMN lastupdate SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_rss_store ALTER COLUMN published_date SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_tiny_url ALTER COLUMN tiny_date SET DEFAULT CURRENT_TIMESTAMP;


-- Fix remaining uppercase columns !

ALTER TABLE pa_metadata_fields RENAME "COLUMN_VALUE" TO column_value;

ALTER TABLE pd_metadata_fields RENAME "COLUMN_VALUE" TO column_value;

ALTER TABLE rule_criterion RENAME "COLUMN_VALUE" TO column_value;
