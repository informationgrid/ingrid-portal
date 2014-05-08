-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.4.1', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Update for profile BAW_WSV
-- ==========================
-- Add "admin-portal" page_constraints_ref to get the edit mode for the iFrame portlets as "admin-portal".
-- -----------------------------

-- add page_constraints_ref to main-application.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT page_id FROM page WHERE name = 'main-application.psml'), 2, 'admin-portal');

-- add page_constraints_ref to main-application-0.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT page_id FROM page WHERE name = 'main-application-0.psml'), 2, 'admin-portal');
	
-- add page_constraints_ref to main-application-1.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT page_id FROM page WHERE name = 'main-application-1.psml'), 2, 'admin-portal');

-- add page_constraints_ref to main-application-2.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT page_id FROM page WHERE name = 'main-application-2.psml'), 2, 'admin-portal');
    
-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_CONSTRAINTS_REF';
