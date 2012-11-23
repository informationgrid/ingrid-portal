-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  value mediumint
);

-- Zwischenspeichern von der fragment_id von der Seite 'mdek_portal_admin.psml'
INSERT INTO ingrid_temp (VALUE) VALUES ((SELECT FRAGMENT_ID FROM fragment WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE NAME='mdek_portal_admin.psml')));
 
-- portlet fragment -> fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- new portlet FRAGMENT_ID
(SELECT value FROM ingrid_temp), -- layout FRAGMENT_ID
NULL, 'ingrid-portal-mdek::MdekAdminLoginPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

INSERT INTO fragment_constraints_ref (CONSTRAINTS_REF_ID, FRAGMENT_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_CONSTRAINTS_REF'),
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- same portlet FRAGMENT_ID from above
0, 'admin');
 
-- Updating the sequence table, because of missing counter for fragment_constraint
INSERT INTO ojb_hl_seq (TABLENAME,FIELDNAME,MAX_KEY,GRAB_SIZE,VERSION)
VALUES ('SEQ_FRAGMENT_CONSTRAINT', 'deprecatedColumn', '0', '20', '0');
 
-- merging two pages to one (mdek-Metadaten)
UPDATE fragment SET PARENT_ID = (SELECT value FROM ingrid_temp),
LAYOUT_ROW = '2' WHERE NAME='ingrid-portal-mdek::MdekEntryPortlet';

UPDATE fragment SET PARENT_ID = (SELECT value FROM ingrid_temp),
LAYOUT_ROW = '3' WHERE NAME='ingrid-portal-mdek::MdekQuickViewPortlet';
 
INSERT INTO fragment_constraints_ref (CONSTRAINTS_REF_ID, FRAGMENT_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_CONSTRAINTS_REF'),
(SELECT FRAGMENT_ID FROM fragment where name='ingrid-portal-mdek::MdekPortalAdminPortlet'),
0, 'admin-portal');

INSERT INTO fragment_constraint (CONSTRAINT_ID, FRAGMENT_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_CONSTRAINT'),
(SELECT FRAGMENT_ID FROM fragment where name='ingrid-portal-mdek::MdekEntryPortlet'),
0, null, 'mdek', null, 'view');

INSERT INTO fragment_constraint (CONSTRAINT_ID, FRAGMENT_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_CONSTRAINT'),
(SELECT FRAGMENT_ID FROM fragment where name='ingrid-portal-mdek::MdekEntryPortlet'),
0, 'admin', 'admin-portal', null, null);

INSERT INTO fragment_constraint (CONSTRAINT_ID, FRAGMENT_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL) 
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_CONSTRAINT'),
(SELECT FRAGMENT_ID FROM fragment where name='ingrid-portal-mdek::MdekQuickViewPortlet'),
0, null, 'mdek', null, 'view');

INSERT INTO fragment_constraint (CONSTRAINT_ID, FRAGMENT_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL) 
VALUES (
(SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_CONSTRAINT'),
(SELECT FRAGMENT_ID FROM fragment where name='ingrid-portal-mdek::MdekQuickViewPortlet'),
0, 'admin', 'admin-portal', null, null);

DELETE FROM page_constraint WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE PATH='/mdek/mdek_entry.psml');
DELETE FROM page_constraint WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE PATH='/mdek/mdek_portal_admin.psml');

DELETE FROM page_constraints_ref WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE PATH='/mdek/mdek_portal_admin.psml');

DELETE FROM page WHERE PATH='/mdek/mdek_entry.psml';
 
-- am Ende ALLE BENUTZTEN SEQUENZEN hoch setzen
-- =====================

 
 -- fragment
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT';

 -- fragment
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT_CONSTRAINTS_REF';

UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT_CONSTRAINT';

-- entfernen der temporären Tabelle
-- =====================
DROP TABLE ingrid_temp;