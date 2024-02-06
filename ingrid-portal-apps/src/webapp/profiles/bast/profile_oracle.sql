-- Create temporary table
BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;;

CREATE TABLE  ingrid_temp (
    temp_key VARCHAR2(255),
    temp_value NUMBER(10,0)
);

-- Hide 'mdek/mdek_portal_admin.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/mdek/mdek_portal_admin.psml';

-- Set folder "/mdek" to hidden
UPDATE folder SET is_hidden = 1 WHERE path = '/mdek';

-- Delete temporary table
DROP TABLE ingrid_temp;