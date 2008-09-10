package de.ingrid.mdek.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ingrid.mdek.beans.address.MdekAddressBean;

public interface AddressRequestHandler {

	public ArrayList<HashMap<String, Object>> getRootAddresses(boolean freeAddressesOnly);
	public ArrayList<HashMap<String, Object>> getSubAddresses(String uuid, int depth);
	public MdekAddressBean getAddressDetail(String uuid);
	public MdekAddressBean getInitialAddress(String parentUuid);
	public MdekAddressBean saveAddress(MdekAddressBean data);
	public MdekAddressBean publishAddress(MdekAddressBean data);
	public MdekAddressBean assignAddressToQA(MdekAddressBean data);
	public void deleteAddress(String uuid, boolean forceDeleteReferences);
	public boolean deleteAddressWorkingCopy(String uuid, boolean forceDeleteReferences);
	public boolean canCutAddress(String uuid);
	public boolean canCopyAddress(String uuid);
	public List<String> getPathToAddress(String uuid);
	public Map<String, Object> copyAddress(String fromUuid, String toUuid, boolean copySubTree, boolean copyToFreeAddress);
	public void moveAddressSubTree(String fromUuid, String toUuid, boolean moveToFreeAddress);
	public MdekAddressBean fetchAddressObjectReferences(String addrUuid, int startIndex, int numRefs);
}
