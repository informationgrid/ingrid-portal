package de.ingrid.mdek.dwr.services;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.MdekAddressBean;
import de.ingrid.mdek.handler.AddressRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekErrorUtils;

public class AddressServiceImpl implements AddressService {

	private final static Logger log = Logger.getLogger(AddressServiceImpl.class);	

	// Injected by Spring
	private AddressRequestHandler addressRequestHandler;

	private final static String ADDRESS_APPTYPE = "A";

	public boolean canCopyAddress(String uuid) {
		log.debug("Query if address can be copied: "+uuid);

		try {
			addressRequestHandler.canCopyAddress(uuid);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while checking if address can be cut.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error checking if address can be copied.", e);
		}
		return true;
	}

	public boolean canCutAddress(String uuid) {
		log.debug("Query if address can be cut: "+uuid);

		try {
			addressRequestHandler.canCutAddress(uuid);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while checking if address can be cut.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error checking if address can be cut.", e);
		}
		return true;
	}

	
	public Map<String, Object> copyAddress(String nodeUuid, String dstNodeUuid,
			Boolean includeChildren, Boolean copyToFreeAddress) {
		log.debug("Copying address with ID: "+nodeUuid+" to ID: "+dstNodeUuid);

		try {
			Map<String, Object> copyResult = addressRequestHandler.copyAddress(nodeUuid, dstNodeUuid, includeChildren, copyToFreeAddress);
			if (copyResult != null) {
				return TreeServiceImpl.addTreeNodeAddressInfo(copyResult);
			} else {
				return null;
			}
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while copying address.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.debug("Error copying address.", e);
			throw new RuntimeException(e);
		}
	}

	public MdekAddressBean createNewAddress(String parentUuid) {
		log.debug("creating new address with parent id = "+parentUuid);		
		MdekAddressBean data = null;
		try {
			data = addressRequestHandler.getInitialAddress(parentUuid);
		} catch (Exception e) {
			log.error("Error while getting address data.", e);
		}

		data.setNodeAppType(ADDRESS_APPTYPE);
		data.setUuid("newNode");
		return data;
	}

	
	public void deleteAddress(String uuid, Boolean forceDeleteReferences, Boolean markOnly) {
		try {
			addressRequestHandler.deleteAddress(uuid, forceDeleteReferences);
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("Error deleting address.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (RuntimeException e) {
			log.debug("Error deleting address", e);
			throw e;
		}
	}

	
	public MdekAddressBean deleteAddressWorkingCopy(String uuid, Boolean forceDeleteReferences, Boolean markOnly) {
		try {
			boolean wasFullyDeleted = addressRequestHandler.deleteAddressWorkingCopy(uuid, forceDeleteReferences);
			if (!wasFullyDeleted) {
				return addressRequestHandler.getAddressDetail(uuid);
			}
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("Error deleting address working Copy.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.debug("Error deleting address working Copy.", e);
			throw new RuntimeException(e);
		}
		return null;
	}

	public MdekAddressBean getAddressData(String nodeUuid, Boolean useWorkingCopy) {
		MdekAddressBean address = null; 

		try {
			address = addressRequestHandler.getAddressDetail(nodeUuid);
		} catch (Exception e) {
			log.debug("Error while getting address data.", e);
		}

		// TODO check for errors and throw an exception?
		// Return a newly created node
		if (address == null) {;}

		return address;
	}

	public List<String> getPathToAddress(String uuid) {
		return addressRequestHandler.getPathToAddress(uuid);
	}


	public void moveAddress(String nodeUuid, String dstNodeUuid, boolean moveToFreeAddress) {
		log.debug("Moving address with ID: "+nodeUuid+" to ID: "+dstNodeUuid);

		try {
			addressRequestHandler.moveAddressSubTree(nodeUuid, dstNodeUuid, moveToFreeAddress);
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while moving address.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.debug("Error moving address.", e);
			throw new RuntimeException(e);
		}
	}

	public MdekAddressBean saveAddressData(MdekAddressBean data, Boolean useWorkingCopy) {
		log.debug("saveAddressData()");

		if (useWorkingCopy) {
			log.debug("Saving address with ID: "+data.getUuid());

			try { return addressRequestHandler.saveAddress(data); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.debug("MdekException while saving address.", e);
				throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
			}
			catch (Exception e) {
				log.debug("Error while saving node", e);
				throw new RuntimeException("Error while saving address.");
			}
		} else {
			log.debug("Publishing address with ID: "+data.getUuid());

			try { return addressRequestHandler.publishAddress(data); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.debug("MdekException while publishing address.", e);
				throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
			}
			catch (Exception e) {
				log.debug("Error while publishing address", e);
				throw new RuntimeException("Error while publishing address.");
			}
		}
	}

	public AddressRequestHandler getAddressRequestHandler() {
		return addressRequestHandler;
	}

	public void setAddressRequestHandler(AddressRequestHandler addressRequestHandler) {
		this.addressRequestHandler = addressRequestHandler;
	}

}
