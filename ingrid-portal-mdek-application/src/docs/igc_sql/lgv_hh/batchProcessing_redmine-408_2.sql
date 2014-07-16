-- 2. Es sollen alle Objekte, die unterhalb des Objektes „PLIS / Digitale Erfassung aller gültigen Bebauungspläne“ (BA2FE933-F3AE-4EF3-BE16-E471F857A179) hängen, folgenden Verweis erhalten:
--   Verweistyp: unspezifischer Verweis
--   Bezeichnung des Verweises: URL zu weiteren Informationen über den Datensatz
--   Internet-Adresse (URL): http://www.hamburg.de/bebauungsplaene-online.de
--   URL-Typ: Internet
-- Auch hier sollen nur die Objekte diesen Verweis erhalten, die ihn noch nicht haben.

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
-- select object instance with according URL data (null if not set, due to outer join)
    SELECT obj.id obj_id, obj.obj_uuid obj_uuid, obj.work_state obj_wstate, obj.obj_name obj_name, 
    urlRef.id url_id
    FROM object_node objNode
    LEFT JOIN t01_object obj ON (objNode.obj_uuid = obj.obj_uuid)
-- link exists?: we only check URL, NOT Verweistyp, URL-Typ ...
    LEFT OUTER JOIN t017_url_ref urlRef ON (obj.id = urlRef.obj_id AND urlRef.url_link LIKE '%www.hamburg.de/bebauungsplaene-online.de%') 
    WHERE objNode.tree_path LIKE v_parentUuid
    ORDER BY obj.obj_uuid
  ) LOOP
    DBMS_OUTPUT.PUT_LINE('Checking node ' || node.obj_uuid || ' ' || node.obj_wstate || ' ' || node.obj_name);
-- "Verweis" -> ONE RECORD
    IF (node.url_id IS NULL) THEN
      DBMS_OUTPUT.PUT_LINE('  INSERT t017_url_ref record');
      INSERT INTO t017_url_ref (id, obj_id, url_link, special_ref, special_name, content, url_type)
        VALUES (v_nextId, node.obj_id, 'http://www.hamburg.de/bebauungsplaene-online.de', 9999, 'unspezifischer Verweis', 'URL zu weiteren Informationen über den Datensatz', 1);
      v_nextId := v_nextId + 1;
    END IF;
-- commit per object
    commit;
  END LOOP;
END;
/
