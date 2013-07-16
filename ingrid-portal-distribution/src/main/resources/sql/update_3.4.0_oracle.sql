-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.4.0', item_date = SYSDATE WHERE item_key ='ingrid_db_version';

-- Fix link to wemove page in Impressum Content
UPDATE ingrid_cms_item SET item_value = replace(item_value, 'www.wemove.com/contact.php', 'www.wemove.com/') WHERE item_title = 'Impressum';
