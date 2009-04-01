package de.ingrid.mdek.beans;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.util.MdekObjectUtils;
import de.ingrid.utils.IngridDocument;

public class SNSLocationUpdateResult {
	private String title;
	private String code;
	private String action;
	private List<MdekDataBean> objEntities;
	private int objects;

	public SNSLocationUpdateResult() {}

	// Construct a result for an update message returned by the backend
	public SNSLocationUpdateResult(Map<String, Object> updateMessage, boolean extractObjectEntities) {
		if (updateMessage.get(MdekKeys.JOBINFO_NUM_OBJECTS) != null) {
			objects = (Integer) updateMessage.get(MdekKeys.JOBINFO_NUM_OBJECTS);
		}

		title = (String) updateMessage.get(MdekKeys.LOCATION_NAME);
		code = (String) updateMessage.get(MdekKeys.LOCATION_CODE);
		action = (String) updateMessage.get(MdekKeys.JOBINFO_MESSAGES);

		if (extractObjectEntities) {
			IngridDocument objEntitiesDoc = new IngridDocument();
			objEntitiesDoc.put(MdekKeys.OBJ_ENTITIES, updateMessage.get(MdekKeys.OBJ_ENTITIES));
			objEntities = MdekObjectUtils.extractDetailedObjects(objEntitiesDoc);
		}
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<MdekDataBean> getObjEntities() {
		return objEntities;
	}
	public void setObjEntities(List<MdekDataBean> objEntities) {
		this.objEntities = objEntities;
	}
	public int getObjects() {
		return objects;
	}
	public void setObjects(int objects) {
		this.objects = objects;
	}

	public String[] toStringArray() {
		return new String[] { title, code, action, String.valueOf(objects) };
	}
}
