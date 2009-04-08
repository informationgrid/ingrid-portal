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
	public List<TreeNodeBean> getSubAddresses(String uuid, int depth);
	public MdekAddressBean getAddressDetail(String uuid);
	public MdekAddressBean getPublishedAddressDetail(String uuid);
	public MdekAddressBean getInitialAddress(String parentUuid);
	public MdekAddressBean saveAddress(MdekAddressBean data);
	public MdekAddressBean publishAddress(MdekAddressBean data);
	public MdekAddressBean assignAddressToQA(MdekAddressBean data);
	public MdekAddressBean reassignAddressToAuthor(MdekAddressBean data);
	public void deleteAddress(String uuid, boolean forceDeleteReferences);
	public boolean deleteAddressWorkingCopy(String uuid, boolean forceDeleteReferences);
	public boolean canCutAddress(String uuid);
	public boolean canCopyAddress(String uuid);
	public List<String> getPathToAddress(String uuid);
	public TreeNodeBean copyAddress(String fromUuid, String toUuid, boolean copySubTree, boolean copyToFreeAddress);
	public void moveAddressSubTree(String fromUuid, String oldParentUuid, String newParentUuid, boolean moveToFreeAddress);
	public MdekAddressBean fetchAddressObjectReferences(String addrUuid, int startIndex, int numRefs);
	public AddressSearchResultBean getWorkAddresses(IdcWorkEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits);
	public AddressSearchResultBean getQAAddresses(WorkState workState, IdcQAEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits);
	public AddressStatisticsResultBean getAddressStatistics(String uuid, boolean freeAddressesOnly);
	public ThesaurusStatisticsResultBean getAddressThesaurusStatistics(String adrUuid, boolean freeAddressesOnly, boolean thesaurusTerms, int startHit, int numHits);
}
