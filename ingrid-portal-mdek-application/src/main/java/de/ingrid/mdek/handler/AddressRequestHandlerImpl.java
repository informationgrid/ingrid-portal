package de.ingrid.mdek.handler;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekError;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekError.MdekErrorType;
import de.ingrid.mdek.MdekUtils.IdcEntityOrderBy;
import de.ingrid.mdek.MdekUtils.IdcQAEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.IdcStatisticsSelectionType;
import de.ingrid.mdek.MdekUtils.IdcWorkEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.beans.TreeNodeBean;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.AddressStatisticsResultBean;
import de.ingrid.mdek.beans.query.ThesaurusStatisticsResultBean;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.caller.IMdekCaller.FetchQuantity;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekAddressUtils;
import de.ingrid.mdek.util.MdekEmailUtils;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class AddressRequestHandlerImpl implements AddressRequestHandler {

	private final static Logger log = Logger.getLogger(AddressRequestHandlerImpl.class);

	// Injected by Spring
	private ConnectionFacade connectionFacade;

	// Initialized by spring through the init method
	private IMdekClientCaller mdekClientCaller;
	private IMdekCallerAddress mdekCallerAddress;

	// Number of object references that are initially loaded from the backend
	private final static int NUM_INITIAL_REFERENCES = 20;


	public void init() {
		mdekClientCaller = connectionFacade.getMdekClientCaller();
		mdekCallerAddress = connectionFacade.getMdekCallerAddress();
	}

	
	public boolean canCopyAddress(String uuid) {
		// Copy is always allowed. Placeholder for future changes
		return true;
	}

	public boolean canCutAddress(String uuid) {
		IngridDocument response = mdekCallerAddress.checkAddressSubTree(connectionFacade.getCurrentPlugId(), uuid, MdekSecurityUtils.getCurrentUserUuid());
		if (mdekClientCaller.getResultFromResponse(response) == null) {
			MdekErrorUtils.handleError(response);
		} else {
			IngridDocument result = mdekClientCaller.getResultFromResponse(response);
			boolean hasWorkingCopy = result.getBoolean(MdekKeys.RESULTINFO_HAS_WORKING_COPY);
			if (hasWorkingCopy) {
				// Throw an error. An address that is about to be moved must not have working copies as children
				throw new MdekException(new MdekError(MdekErrorType.SUBTREE_HAS_WORKING_COPIES));
			}
		}
		return true;
	}

	public TreeNodeBean copyAddress(String fromUuid, String toUuid, boolean copySubTree, boolean copyToFreeAddress) {
		IngridDocument response = mdekCallerAddress.copyAddress(connectionFacade.getCurrentPlugId(), fromUuid, toUuid, copySubTree, copyToFreeAddress, MdekSecurityUtils.getCurrentUserUuid());
		return MdekAddressUtils.extractSingleSimpleAddressFromResponse(response);
	}

	public void deleteAddress(String uuid, boolean forceDeleteReferences) {
		IngridDocument response = mdekCallerAddress.deleteAddress(connectionFacade.getCurrentPlugId(), uuid, forceDeleteReferences, MdekSecurityUtils.getCurrentUserUuid());
		IngridDocument result = MdekUtils.extractAdditionalInformationFromResponse(response);

		boolean markedDeleted = result.getBoolean(MdekKeys.RESULTINFO_WAS_MARKED_DELETED);
		if (markedDeleted) {
			MdekEmailUtils.sendAddressMarkedDeletedMail(uuid);
		}
	}

	public boolean deleteAddressWorkingCopy(String uuid, boolean forceDeleteReferences) {
		IngridDocument response = mdekCallerAddress.deleteAddressWorkingCopy(connectionFacade.getCurrentPlugId(), uuid, forceDeleteReferences, MdekSecurityUtils.getCurrentUserUuid());

		IngridDocument result = MdekUtils.extractAdditionalInformationFromResponse(response);
		return (Boolean) result.get(MdekKeys.RESULTINFO_WAS_FULLY_DELETED);
	}

	public MdekAddressBean getAddressDetail(String uuid) {
		IngridDocument response = mdekCallerAddress.fetchAddress(connectionFacade.getCurrentPlugId(), uuid, FetchQuantity.EDITOR_ENTITY, de.ingrid.mdek.MdekUtils.IdcEntityVersion.WORKING_VERSION, 0, NUM_INITIAL_REFERENCES, MdekSecurityUtils.getCurrentUserUuid());
		return MdekAddressUtils.extractSingleAddressFromResponse(response);	
	}

	public MdekAddressBean getPublishedAddressDetail(String uuid) {
		IngridDocument response = mdekCallerAddress.fetchAddress(connectionFacade.getCurrentPlugId(), uuid, FetchQuantity.EDITOR_ENTITY, de.ingrid.mdek.MdekUtils.IdcEntityVersion.PUBLISHED_VERSION, 0, NUM_INITIAL_REFERENCES, MdekSecurityUtils.getCurrentUserUuid());
		return MdekAddressUtils.extractSingleAddressFromResponse(response);	
	}
	
	public MdekAddressBean getInitialAddress(String parentUuid) {
		IngridDocument adr = new IngridDocument();
		adr.put(MdekKeys.PARENT_UUID, parentUuid);

		IngridDocument response = mdekCallerAddress.getInitialAddress(connectionFacade.getCurrentPlugId(), adr, MdekSecurityUtils.getCurrentUserUuid());

		// TODO: Set this value in the backend. Temproarily set it here till it gets fixed!
		MdekAddressBean dat = MdekAddressUtils.extractSingleAddressFromResponse(response);
		dat.setAddressOwner(MdekSecurityUtils.getCurrentUserUuid());
		return dat;
	}

	public List<String> getPathToAddress(String uuid) {
		IngridDocument response = mdekCallerAddress.getAddressPath(connectionFacade.getCurrentPlugId(), uuid, MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractPathFromResponse(response);
	}

	public List<TreeNodeBean> getRootAddresses(boolean freeAddressesOnly) {
		IngridDocument response = mdekCallerAddress.fetchTopAddresses(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid(), freeAddressesOnly);
		return MdekAddressUtils.extractAddressesFromResponse(response);
	}

	public List<TreeNodeBean> getSubAddresses(String uuid, int depth) {
		IngridDocument response = mdekCallerAddress.fetchSubAddresses(connectionFacade.getCurrentPlugId(), uuid, MdekSecurityUtils.getCurrentUserUuid());
		return MdekAddressUtils.extractAddressesFromResponse(response);
	}

	public void moveAddressSubTree(String fromUuid, String oldParentUuid, String newParentUuid, boolean moveToFreeAddress) {
		IngridDocument response = mdekCallerAddress.moveAddress(connectionFacade.getCurrentPlugId(), fromUuid, newParentUuid, moveToFreeAddress, MdekSecurityUtils.getCurrentUserUuid());
		if (MdekUtils.getResultFromResponse(response) == null) {
			MdekErrorUtils.handleError(response);

		} else {
			MdekEmailUtils.sendAddressMovedMail(fromUuid, oldParentUuid, newParentUuid);
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

		IngridDocument response = mdekCallerAddress.publishAddress(connectionFacade.getCurrentPlugId(), adr, true, 0, NUM_INITIAL_REFERENCES, MdekSecurityUtils.getCurrentUserUuid());
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

		IngridDocument response = mdekCallerAddress.assignAddressToQA(connectionFacade.getCurrentPlugId(), adr, true, 0, NUM_INITIAL_REFERENCES, MdekSecurityUtils.getCurrentUserUuid());
		MdekAddressBean result = MdekAddressUtils.extractSingleAddressFromResponse(response);
		if (result != null) {
			MdekEmailUtils.sendAddressAssignedToQAMail(result);
		}

		return result;
	}

	public MdekAddressBean reassignAddressToAuthor(MdekAddressBean data) {
		IngridDocument adr = (IngridDocument) MdekAddressUtils.convertFromAddressRepresentation(data);

		log.debug("Sending the following address to the author:");
		log.debug(adr);

		IngridDocument response = mdekCallerAddress.reassignAddressToAuthor(connectionFacade.getCurrentPlugId(), adr, true, 0, NUM_INITIAL_REFERENCES, MdekSecurityUtils.getCurrentUserUuid());
		MdekAddressBean result = MdekAddressUtils.extractSingleAddressFromResponse(response);
		if (result != null) {
			MdekEmailUtils.sendAddressReassignedMail(result);
		}

		return result;
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

		IngridDocument response = mdekCallerAddress.storeAddress(connectionFacade.getCurrentPlugId(), adr, true, 0, NUM_INITIAL_REFERENCES, MdekSecurityUtils.getCurrentUserUuid());
		return MdekAddressUtils.extractSingleAddressFromResponse(response);
	}

	public MdekAddressBean fetchAddressObjectReferences(String addrUuid, int startIndex, int numRefs) {
		IngridDocument response = mdekCallerAddress.fetchAddressObjectReferences(connectionFacade.getCurrentPlugId(), addrUuid, startIndex, numRefs, MdekSecurityUtils.getCurrentUserUuid());
		return MdekAddressUtils.extractSingleAddressFromResponse(response);
	}

	public AddressSearchResultBean getWorkAddresses(IdcWorkEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits) {
		IngridDocument response = mdekCallerAddress.getWorkAddresses(connectionFacade.getCurrentPlugId(), selectionType, orderBy, orderAsc, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public AddressSearchResultBean getQAAddresses(WorkState workState, IdcQAEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits) {
		IngridDocument response = mdekCallerAddress.getQAAddresses(connectionFacade.getCurrentPlugId(), workState, selectionType, orderBy, orderAsc, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public AddressStatisticsResultBean getAddressStatistics(String adrUuid, boolean freeAddressesOnly) {
		// The Parameters startHit and numHit are ignored for IdcEntitySelectionType.STATISTICS_CLASSES_AND_STATES
		int startHit = 0;
		int numHits = 0;
		IngridDocument response = mdekCallerAddress.getAddressStatistics(connectionFacade.getCurrentPlugId(), adrUuid, freeAddressesOnly, IdcStatisticsSelectionType.CLASSES_AND_STATES, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		return MdekAddressUtils.extractAddressStatistics(result);
	}

	public ThesaurusStatisticsResultBean getAddressThesaurusStatistics(String adrUuid, boolean freeAddressesOnly, boolean thesaurusTerms, int startHit, int numHits) {
		IdcStatisticsSelectionType selectionType = thesaurusTerms ? IdcStatisticsSelectionType.SEARCHTERMS_THESAURUS : IdcStatisticsSelectionType.SEARCHTERMS_FREE;
		IngridDocument response = mdekCallerAddress.getAddressStatistics(connectionFacade.getCurrentPlugId(), adrUuid, freeAddressesOnly, selectionType, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		return MdekUtils.extractThesaurusStatistics(result);
	}

	
	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}


	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

}
