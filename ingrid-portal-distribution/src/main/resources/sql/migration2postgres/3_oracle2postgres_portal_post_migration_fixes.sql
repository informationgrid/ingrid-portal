
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


-- ADDED AFTER 4.0.3 RELEASE to be always executed when migrating
-- fix from update_4.0.2.1_postgres.sql
-- fix qrtz boolean columns on postgres, needs boolean instead of character !
-- e.g. see https://issues.liferay.com/browse/LEP-6855

ALTER TABLE qrtz_fired_triggers ALTER is_volatile TYPE boolean USING CASE is_volatile WHEN '1' THEN TRUE ELSE FALSE END;
ALTER TABLE qrtz_fired_triggers ALTER is_stateful TYPE boolean USING CASE is_stateful WHEN '1' THEN TRUE ELSE FALSE END;
ALTER TABLE qrtz_fired_triggers ALTER requests_recovery TYPE boolean USING CASE requests_recovery WHEN '1' THEN TRUE ELSE FALSE END;

ALTER TABLE qrtz_job_details ALTER is_durable TYPE boolean USING CASE is_durable WHEN '1' THEN TRUE ELSE FALSE END;
ALTER TABLE qrtz_job_details ALTER is_volatile TYPE boolean USING CASE is_volatile WHEN '1' THEN TRUE ELSE FALSE END;
ALTER TABLE qrtz_job_details ALTER is_stateful TYPE boolean USING CASE is_stateful WHEN '1' THEN TRUE ELSE FALSE END;
ALTER TABLE qrtz_job_details ALTER requests_recovery TYPE boolean USING CASE requests_recovery WHEN '1' THEN TRUE ELSE FALSE END;

ALTER TABLE qrtz_triggers ALTER is_volatile TYPE boolean USING CASE is_volatile WHEN '1' THEN TRUE ELSE FALSE END;
