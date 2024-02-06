-- Create temporary table
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value int
);

-- Hide 'mdek/mdek_portal_admin.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/mdek/mdek_portal_admin.psml';

-- Set folder "/mdek" to hidden
UPDATE folder SET is_hidden = 1 WHERE path = '/mdek';

-- Delete temporary table
DROP TABLE IF EXISTS ingrid_temp;