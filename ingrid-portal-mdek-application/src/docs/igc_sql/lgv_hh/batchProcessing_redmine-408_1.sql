-- 1. Es sollen alle Objekte, die unterhalb des Objektes „PLIS / Digitale Erfassung aller gültigen Bebauungspläne“ (BA2FE933-F3AE-4EF3-BE16-E471F857A179) hängen, 
-- den Haken bei „Veröffentlichung gemäß HmbTG“ bekommen (publicationHmbTG = true) mit „Öffentliche Pläne“ (hmbtg_12_oeffentliche_plaene) als Informationsgegenstand.
-- Teilweise wurde bei den Objekten allerdings schon ein Informationsgegenstand ausgewählt. Hier soll kein zweiter dazukommen…

-- wemove issue: https://redmine.wemove.com/issues/408
-- Tested on:
--   Oracle Database 10g Express Edition 10.2.0.1.0
--   Oracle Database 11g Express Edition 11.2.0.2.0
-- with Oracle SQL Developer 3.2.20.09
-- with IGC Version: 3.4.0

set serveroutput on;
DECLARE
-- SET OBJECT PARENT UUID HERE !
  v_parentUuid VARCHAR2(40 CHAR) := '%BA2FE933-F3AE-4EF3-BE16-E471F857A179%';
-- !!! max number of new ids in this script (hilo generator) !!!
  v_maxLow NUMBER := 32767;
  v_nextHi NUMBER(24,0);
  v_nextId NUMBER(24,0);
BEGIN
-- initialize ID generation via hilo mechanism
  SELECT next_hi INTO v_nextHi FROM hibernate_unique_key;
  DBMS_OUTPUT.PUT_LINE('in nextHi: ' || v_nextHi);
-- immediately update next_hi to have my own block of ids !
  DELETE FROM hibernate_unique_key;
  INSERT INTO hibernate_unique_key (next_hi) VALUES ((v_nextHi+1));
  DBMS_OUTPUT.PUT_LINE('out nextHi: ' || (v_nextHi+1));
  commit;
  v_nextId := v_nextHi * v_maxLow;
  DBMS_OUTPUT.PUT_LINE('nextId: ' || v_nextId);

-- loop all child objects ! Including "in Bearbeitung" (B) AND "veröffentlicht" (V) instances !
  FOR node IN (
-- select object instance with according additional data (null if not set, due to outer join)
    SELECT obj.id obj_id, obj.obj_uuid obj_uuid, obj.work_state obj_wstate, obj.obj_name obj_name, 
    addFieldHmbTG.id hmbtg1_id, addFieldHmbTG.field_key hmbtg1_fkey, addFieldHmbTG.data hmbtg1_data,
    addFieldHmbTGInfo.id hmbtg2_id, addFieldHmbTGInfo.field_key hmbtg2_fkey, addFieldHmbTGInfo.data hmbtg2_data,
    addFieldHmbTGInfoItem.id hmbtg3_id, addFieldHmbTGInfoItem.field_key hmbtg3_fkey, addFieldHmbTGInfoItem.list_item_id hmbtg3_itemid, addFieldHmbTGInfoItem.data hmbtg3_data
    FROM object_node objNode
    LEFT JOIN t01_object obj ON (objNode.obj_uuid = obj.obj_uuid)
    LEFT OUTER JOIN additional_field_data addFieldHmbTG ON (obj.id = addFieldHmbTG.obj_id AND addFieldHmbTG.field_key = 'publicationHmbTG') 
    LEFT OUTER JOIN additional_field_data addFieldHmbTGInfo ON (obj.id = addFieldHmbTGInfo.obj_id AND addFieldHmbTGInfo.field_key = 'Informationsgegenstand') 
    LEFT OUTER JOIN additional_field_data addFieldHmbTGInfoItem ON (addFieldHmbTGInfo.id = addFieldHmbTGInfoItem.parent_field_id) 
    WHERE objNode.tree_path LIKE v_parentUuid
    ORDER BY obj.obj_uuid
  ) LOOP
    DBMS_OUTPUT.PUT_LINE('Checking node ' || node.obj_uuid || ' ' || node.obj_wstate || ' ' || node.obj_name);
-- "Veröffentlichung gemäß HmbTG" -> ONE RECORD
    IF (node.hmbtg1_id IS NULL) THEN
      DBMS_OUTPUT.PUT_LINE('  INSERT publicationHmbTG record');
      INSERT INTO additional_field_data (id, obj_id, field_key, data)
        VALUES (v_nextId, node.obj_id, 'publicationHmbTG', 'true');
      v_nextId := v_nextId + 1;
    ELSIF NOT (node.hmbtg1_data LIKE 'true') THEN
      DBMS_OUTPUT.PUT_LINE('  UPDATE publicationHmbTG record to true');
      UPDATE additional_field_data SET data = 'true' WHERE id = node.hmbtg1_id;
    END IF;
-- "Informationsgegenstand" -> TWO RECORDS (list + list item)
    IF (node.hmbtg2_id IS NULL) THEN
      DBMS_OUTPUT.PUT_LINE('  INSERT Informationsgegenstand record');
      INSERT INTO additional_field_data (id, obj_id, field_key)
        VALUES (v_nextId, node.obj_id, 'Informationsgegenstand');
      node.hmbtg2_id := v_nextId;
      v_nextId := v_nextId + 1;
    END IF;
    IF (node.hmbtg3_id IS NULL) THEN
      DBMS_OUTPUT.PUT_LINE('  INSERT hmbtg_12_oeffentliche_plaene record');
      INSERT INTO additional_field_data (id, obj_id, field_key, list_item_id, data, parent_field_id)
        VALUES (v_nextId, node.obj_id, 'informationHmbTG', 'hmbtg_12_oeffentliche_plaene', 'Öffentliche Pläne', node.hmbtg2_id);
      v_nextId := v_nextId + 1;
    END IF;
-- commit per object
    commit;
  END LOOP;
END;
/
