package de.ingrid.mdek.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekError;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekError.MdekErrorType;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.caller.IMdekCaller;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerAbstract.Quantity;
import de.ingrid.mdek.dwr.util.HTTPSessionHelper;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekAddressUtils;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class AddressRequestHandlerImpl implements AddressRequestHandler {

	private final static Logger log = Logger.getLogger(AddressRequestHandlerImpl.class);

	// Injected by Spring
	private ConnectionFacade connectionFacade;

	// Initialized by spring through the init method
	private IMdekCaller mdekCaller;
	private IMdekCallerAddress mdekCallerAddress;

	// Number of object references that are initially loaded from the backend
	private final static int NUM_INITIAL_REFERENCES = 20;


	public void init() {
		mdekCaller = connectionFacade.getMdekCaller();
		mdekCallerAddress = connectionFacade.getMdekCallerAddress();
	}

	
	public boolean canCopyAddress(String uuid) {
		// Copy is always allowed. Placeholder for future changes
		return true;
	}

	public boolean canCutAddress(String uuid) {
		IngridDocument response = mdekCallerAddress.checkAddressSubTree(connectionFacade.getCurrentPlugId(), uuid, HTTPSessionHelper.getCurrentSessionId());
		if (mdekCaller.getResultFromResponse(response) == null) {
			MdekErrorUtils.handleError(response);
		} else {
			IngridDocument result = mdekCaller.getResultFromResponse(response);
			boolean hasWorkingCopy = result.getBoolean(MdekKeys.RESULTINFO_HAS_WORKING_COPY);
			if (hasWorkingCopy) {
				// Throw an error. An address that is about to be moved must not have working copies as children
				throw new MdekException(new MdekError(MdekErrorType.SUBTREE_HAS_WORKING_COPIES));
			}
		}
		return true;
	}

	public Map<String, Object> copyAddress(String fromUuid, String toUuid, boolean copySubTree, boolean copyToFreeAddress) {
		IngridDocument response = mdekCallerAddress.copyAddress(connectionFacade.getCurrentPlugId(), fromUuid, toUuid, copySubTree, copyToFreeAddress, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractSingleSimpleAddressFromResponse(response);
	}

	public void deleteAddress(String uuid, boolean forceDeleteReferences) {
		IngridDocument response = mdekCallerAddress.deleteAddress(connectionFacade.getCurrentPlugId(), uuid, forceDeleteReferences, HTTPSessionHelper.getCurrentSessionId());
		IngridDocument result = MdekUtils.extractAdditionalInformationFromResponse(response);
	}

	public boolean deleteAddressWorkingCopy(String uuid, boolean forceDeleteReferences) {
		IngridDocument response = mdekCallerAddress.deleteAddressWorkingCopy(connectionFacade.getCurrentPlugId(), uuid, forceDeleteReferences, HTTPSessionHelper.getCurrentSessionId());

		IngridDocument result = MdekUtils.extractAdditionalInformationFromResponse(response);
		return (Boolean) result.get(MdekKeys.RESULTINFO_WAS_FULLY_DELETED);
	}

	public MdekAddressBean getAddressDetail(String uuid) {
		IngridDocument response = mdekCallerAddress.fetchAddress(connectionFacade.getCurrentPlugId(), uuid, Quantity.DETAIL_ENTITY, de.ingrid.mdek.MdekUtils.IdcEntityVersion.WORKING_VERSION, 0, NUM_INITIAL_REFERENCES, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractSingleAddressFromResponse(response);	
	}

	public MdekAddressBean getPublishedAddressDetail(String uuid) {
		IngridDocument response = mdekCallerAddress.fetchAddress(connectionFacade.getCurrentPlugId(), uuid, Quantity.DETAIL_ENTITY, de.ingrid.mdek.MdekUtils.IdcEntityVersion.PUBLISHED_VERSION, 0, NUM_INITIAL_REFERENCES, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractSingleAddressFromResponse(response);	
	}
	
	public MdekAddressBean getInitialAddress(String parentUuid) {
		IngridDocument adr = new IngridDocument();
		adr.put(MdekKeys.PARENT_UUID, parentUuid);

		IngridDocument response = mdekCallerAddress.getInitialAddress(connectionFacade.getCurrentPlugId(), adr, HTTPSessionHelper.getCurrentSessionId());

		// TODO: Set this value in the backend. Temproarily set it here till it gets fixed!
		MdekAddressBean dat = MdekAddressUtils.extractSingleAddressFromResponse(response);
		dat.setAddressOwner(HTTPSessionHelper.getCurrentSessionId());
		return dat;
	}

	public List<String> getPathToAddress(String uuid) {
		IngridDocument response = mdekCallerAddress.getAddressPath(connectionFacade.getCurrentPlugId(), uuid, HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractPathFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getRootAddresses(boolean freeAddressesOnly) {
		IngridDocument response = mdekCallerAddress.fetchTopAddresses(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentSessionId(), freeAddressesOnly);
		return MdekAddressUtils.extractAddressesFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getSubAddresses(String uuid, int depth) {
		IngridDocument response = mdekCallerAddress.fetchSubAddresses(connectionFacade.getCurrentPlugId(), uuid, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractAddressesFromResponse(response);
	}

	public void moveAddressSubTree(String fromUuid, String toUuid, boolean moveToFreeAddress) {
		IngridDocument response = mdekCallerAddress.moveAddress(connectionFacade.getCurrentPlugId(), fromUuid, toUuid, moveToFreeAddress, HTTPSessionHelper.getCurrentSessionId());
		if (MdekUtils.getResultFromResponse(response) == null) {
			MdekErrorUtils.handleError(response);
		}
	}

	public MdekAddressBean publishAddress(MdekAddressBean data) {
		IngridDocument adr = (IngridDocument) MdekAddressUtils.convertFromAddressRepresentation(data);

		if (data.getUuid().equalsIgnoreCase("newNode")) {
			adr.remove(MdekKeys.UUID);
			adr.remove(MdekKeys.ID);
		}

		log.debug("Sending the following address for publishing:");
		log.debug(adr);

		IngridDocument response = mdekCallerAddress.publishAddress(connectionFacade.getCurrentPlugId(), adr, true, 0, NUM_INITIAL_REFERENCES, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractSingleAddressFromResponse(response);
	}

	public MdekAddressBean assignAddressToQA(MdekAddressBean data) {
		IngridDocument adr = (IngridDocument) MdekAddressUtils.convertFromAddressRepresentation(data);

		if (data.getUuid().equalsIgnoreCase("newNode")) {
			adr.remove(MdekKeys.UUID);
			adr.remove(MdekKeys.ID);
		}

		log.debug("Sending the following address to QA:");
		log.debug(adr);

		IngridDocument response = mdekCallerAddress.assignAddressToQA(connectionFacade.getCurrentPlugId(), adr, true, 0, NUM_INITIAL_REFERENCES, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractSingleAddressFromResponse(response);
	}

	public MdekAddressBean saveAddress(MdekAddressBean data) {
		IngridDocument adr = (IngridDocument) MdekAddressUtils.convertFromAddressRepresentation(data);
//		log.debug("saveAddress() not implemented yet.");

		if (data.getUuid().equalsIgnoreCase("newNode")) {
			adr.remove(MdekKeys.UUID);
			adr.remove(MdekKeys.ID);
		}

		log.debug("Sending the following address for storage:");
		log.debug(adr);

		IngridDocument response = mdekCallerAddress.storeAddress(connectionFacade.getCurrentPlugId(), adr, true, 0, NUM_INITIAL_REFERENCES, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractSingleAddressFromResponse(response);
	}

	public MdekAddressBean fetchAddressObjectReferences(String addrUuid, int startIndex, int numRefs) {
		IngridDocument response = mdekCallerAddress.fetchAddressObjectReferences(connectionFacade.getCurrentPlugId(), addrUuid, startIndex, numRefs, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractSingleAddressFromResponse(response);
	}


	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}


	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

}
