/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.handler;

import java.util.List;

import de.ingrid.mdek.MdekUtils.IdcEntityOrderBy;
import de.ingrid.mdek.MdekUtils.IdcQAEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.IdcWorkEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.beans.TreeNodeBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectStatisticsResultBean;
import de.ingrid.mdek.beans.query.ThesaurusStatisticsResultBean;

public interface ObjectRequestHandler {

	public List<TreeNodeBean> getRootObjects();
	public List<TreeNodeBean> getSubObjects(String uuid);
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
	public TreeNodeBean copyObject(String fromUuid, String toUuid, boolean copySubTree);
	public void moveObjectSubTree(String fromUuid, String oldParentUuid, String newParentUuid, boolean forcePublicationCondition);
	public MdekDataBean assignObjectToQA(MdekDataBean data);
	public MdekDataBean reassignObjectToAuthor(MdekDataBean data);
	public ObjectSearchResultBean getWorkObjects(IdcWorkEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits);
	public ObjectSearchResultBean getQAObjects(WorkState workState, IdcQAEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits);
	public ObjectStatisticsResultBean getObjectStatistics(String objUuid);
	public ThesaurusStatisticsResultBean getObjectThesaurusStatistics(String objUuid, boolean thesaurusTerms, int startHit, int numHits);
}
