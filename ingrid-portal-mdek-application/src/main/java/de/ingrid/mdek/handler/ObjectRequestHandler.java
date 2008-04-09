package de.ingrid.mdek.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ingrid.mdek.beans.MdekDataBean;

public interface ObjectRequestHandler {

	public ArrayList<HashMap<String, Object>> getRootObjects();
	public ArrayList<HashMap<String, Object>> getSubObjects(String uuid, int depth);
	public MdekDataBean getObjectDetail(String uuid);
	public MdekDataBean getInitialObject(String parentUuid);
	public MdekDataBean saveObject(MdekDataBean data);
	public MdekDataBean publishObject(MdekDataBean data, boolean forcePublicationCondition);
	public void deleteObject(String uuid, boolean forceDeleteReferences);
	public boolean deleteObjectWorkingCopy(String uuid, boolean forceDeleteReferences);
	public boolean canCutObject(String uuid);
	public boolean canCopyObject(String uuid);
	public List<String> getPathToObject(String uuid);
	public Map<String, Object> copyObject(String fromUuid, String toUuid, boolean copySubTree);
	public void moveObjectSubTree(String fromUuid, String toUuid, boolean forcePublicationCondition);
}
