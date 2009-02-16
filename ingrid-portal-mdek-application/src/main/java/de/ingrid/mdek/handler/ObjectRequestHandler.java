package de.ingrid.mdek.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ingrid.mdek.MdekUtils.IdcEntityOrderBy;
import de.ingrid.mdek.MdekUtils.IdcQAEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.IdcWorkEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectStatisticsResultBean;
import de.ingrid.mdek.beans.query.ThesaurusStatisticsResultBean;

public interface ObjectRequestHandler {

	public ArrayList<HashMap<String, Object>> getRootObjects();
	public ArrayList<HashMap<String, Object>> getSubObjects(String uuid, int depth);
	public MdekDataBean getObjectDetail(String uuid);
	public MdekDataBean getPublishedObjectDetail(String uuid);
	public MdekDataBean getInitialObject(String parentUuid);
	public MdekDataBean saveObject(MdekDataBean data);
	// Change the title to 'newTitle' for the object with the given 'uuid'
	public void updateObjectTitle(String uuid, String newTitle);
	public MdekDataBean publishObject(MdekDataBean data, boolean forcePublicationCondition);
	public void deleteObject(String uuid, boolean forceDeleteReferences);
	public boolean deleteObjectWorkingCopy(String uuid, boolean forceDeleteReferences);
	public boolean canCutObject(String uuid);
	public boolean canCopyObject(String uuid);
	public List<String> getPathToObject(String uuid);
	public Map<String, Object> copyObject(String fromUuid, String toUuid, boolean copySubTree);
	public void moveObjectSubTree(String fromUuid, String oldParentUuid, String newParentUuid, boolean forcePublicationCondition);
	public MdekDataBean assignObjectToQA(MdekDataBean data);
	public MdekDataBean reassignObjectToAuthor(MdekDataBean data);
	public ObjectSearchResultBean getWorkObjects(IdcWorkEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits);
	public ObjectSearchResultBean getQAObjects(WorkState workState, IdcQAEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits);
	public ObjectStatisticsResultBean getObjectStatistics(String objUuid);
	public ThesaurusStatisticsResultBean getObjectThesaurusStatistics(String objUuid, boolean thesaurusTerms, int startHit, int numHits);
}
