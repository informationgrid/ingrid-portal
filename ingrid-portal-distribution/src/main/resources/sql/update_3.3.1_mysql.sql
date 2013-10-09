-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.3.1', item_date = NOW() WHERE item_key ='ingrid_db_version';

-- Fix link to wemove page in Impressum Content
UPDATE ingrid_cms_item SET item_value = replace(item_value, 'www.wemove.com/contact.php', 'www.wemove.com/') WHERE item_title = 'Impressum';
