package de.ingrid.mdek.dwr.services;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.beans.object.MdekDataBean;

public interface ObjectService {

	public MdekDataBean getNodeData(String nodeUuid, String nodeType, Boolean useWorkingCopy);
	public MdekDataBean getPublishedNodeData(String nodeUuid);
	public MdekDataBean saveNodeData(MdekDataBean data, Boolean useWorkingCopy, Boolean forcePublicationCondition);
	public MdekDataBean assignObjectToQA(MdekDataBean data);
	public Map<String, Object> copyNode(String nodeUuid, String dstNodeUuid, Boolean includeChildren);
	public void moveNode(String nodeUuid, String dstNodeUuid, boolean forcePublicationCondition);
	public void deleteNode(String nodeUuid, Boolean forceDeleteReferences, Boolean markOnly);
	public MdekDataBean deleteObjectWorkingCopy(String nodeUuid, Boolean forceDeleteReferences, Boolean markOnly);
	public MdekDataBean createNewNode(String parentUuid);
	public List<String> getPathToObject(String targetUuid);
	public boolean canCutObject(String parentUuid);
	public boolean canCopyObject(String parentUuid);
}