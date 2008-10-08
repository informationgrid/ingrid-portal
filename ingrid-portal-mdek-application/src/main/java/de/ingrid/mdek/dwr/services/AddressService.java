package de.ingrid.mdek.dwr.services;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.query.AddressStatisticsResultBean;
import de.ingrid.mdek.beans.query.ThesaurusStatisticsResultBean;

public interface AddressService {

	public MdekAddressBean getAddressData(String nodeUuid, Boolean useWorkingCopy);
	public MdekAddressBean getPublishedAddressData(String nodeUuid);
	public MdekAddressBean saveAddressData(MdekAddressBean data, Boolean useWorkingCopy);
	public MdekAddressBean assignAddressToQA(MdekAddressBean data);
	public MdekAddressBean reassignAddressToAuthor(MdekAddressBean data);
	public List<MdekAddressBean> getQAAddresses(String workState, String selectionType, Integer maxNum);
	public Map<String, Object> copyAddress(String nodeUuid, String dstNodeUuid, Boolean includeChildren, Boolean copyToFreeAddress);
	public void moveAddress(String nodeUuid, String dstNodeUuid, boolean moveToFreeAddress);
	public void deleteAddress(String nodeUuid, Boolean forceDeleteReferences, Boolean markOnly);
	public MdekAddressBean deleteAddressWorkingCopy(String nodeUuid, Boolean forceDeleteReferences, Boolean markOnly);
	public MdekAddressBean createNewAddress(String parentUuid);
	public List<String> getPathToAddress(String targetUuid);
	public boolean canCutAddress(String parentUuid);
	public boolean canCopyAddress(String parentUuid);
	public MdekAddressBean fetchAddressObjectReferences(String addrUuid, int startIndex, int numRefs);
	public AddressStatisticsResultBean getAddressStatistics(String objUuid, boolean freeAddressesOnly);
	public ThesaurusStatisticsResultBean getAddressThesaurusStatistics(String adrUuid, boolean freeAddressesOnly, boolean thesaurusTerms, int startHit, int numHits);
}