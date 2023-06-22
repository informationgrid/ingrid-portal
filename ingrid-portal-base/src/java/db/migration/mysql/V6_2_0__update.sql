-- DB Version
UPDATE ingrid_lookup SET item_value = '6.2.0', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value mediumint
);

INSERT INTO ingrid_cms (id, item_key, item_description, item_changed_by) VALUES ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'ingrid.teaser.inform.2', 'Erweiterte Informationen', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'ingrid.teaser.inform.2'), 'de', 'Erweiterte Informationen', '', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'ingrid.teaser.inform.2'), 'en', 'Erweiterte Informationen', '', 'admin');

-- delete temporary table
DROP TABLE IF EXISTS ingrid_temp;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER_MENU';