-- DB Version
UPDATE ingrid_lookup SET item_value = '4.0.2.1', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

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
