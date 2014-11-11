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
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.AddressStatisticsResultBean;
import de.ingrid.mdek.beans.query.ThesaurusStatisticsResultBean;

public interface AddressRequestHandler {

	public List<TreeNodeBean> getRootAddresses(boolean freeAddressesOnly);
	public List<TreeNodeBean> getSubAddresses(String uuid);
	public MdekAddressBean getAddressDetail(String uuid);
	public MdekAddressBean getPublishedAddressDetail(String uuid);
	public MdekAddressBean getInitialAddress(String parentUuid);
	public MdekAddressBean saveAddress(MdekAddressBean data);
	public MdekAddressBean publishAddress(MdekAddressBean data, boolean forcePublicationCondition);
	public MdekAddressBean assignAddressToQA(MdekAddressBean data);
	public MdekAddressBean reassignAddressToAuthor(MdekAddressBean data);
	public void deleteAddress(String uuid, boolean forceDeleteReferences);
	public boolean deleteAddressWorkingCopy(String uuid, boolean forceDeleteReferences);
	public boolean canCutAddress(String uuid);
	public boolean canCopyAddress(String uuid);
	public List<String> getPathToAddress(String uuid);
	public TreeNodeBean copyAddress(String fromUuid, String toUuid, boolean copySubTree, boolean copyToFreeAddress);
	public void moveAddressSubTree(String fromUuid, String oldParentUuid, String newParentUuid, boolean moveToFreeAddress, boolean forcePublicationCondition);
	public MdekAddressBean fetchAddressObjectReferences(String addrUuid, int startIndex, int numRefs);
	public AddressSearchResultBean getWorkAddresses(IdcWorkEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits);
	public AddressSearchResultBean getQAAddresses(WorkState workState, IdcQAEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits);
	public AddressStatisticsResultBean getAddressStatistics(String uuid, boolean freeAddressesOnly);
	public ThesaurusStatisticsResultBean getAddressThesaurusStatistics(String adrUuid, boolean freeAddressesOnly, boolean thesaurusTerms, int startHit, int numHits);
	public String mergeAddressToSubAddresses(String adrUuid);
}
