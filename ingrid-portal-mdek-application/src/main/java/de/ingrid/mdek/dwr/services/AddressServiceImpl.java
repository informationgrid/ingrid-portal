/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.dwr.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekUtils.IdcEntityOrderBy;
import de.ingrid.mdek.MdekUtils.IdcQAEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.IdcWorkEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.beans.TreeNodeBean;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.address.MdekAddressSimpleBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.AddressSearchResultSimpleBean;
import de.ingrid.mdek.beans.query.AddressStatisticsResultBean;
import de.ingrid.mdek.beans.query.ThesaurusStatisticsResultBean;
import de.ingrid.mdek.handler.AddressRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekAddressUtils;
import de.ingrid.mdek.util.MdekErrorUtils;

public class AddressServiceImpl implements AddressService {

	private static final Logger log = Logger.getLogger(AddressServiceImpl.class);	

	// Injected by Spring
	private AddressRequestHandler addressRequestHandler;

	private static final String ADDRESS_APPTYPE = "A";

	public boolean canCopyAddress(String uuid) {
		log.debug("Query if address can be copied: "+uuid);

		try {
			addressRequestHandler.canCopyAddress(uuid);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while checking if address can be cut.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error checking if address can be copied.", e);
		}
		return true;
	}
	
	public boolean canCopyAddresses(String[] uuids) {
        boolean success = true;
        try {
            for (String uuid : uuids) {
                success = success && canCopyAddress(uuid);
            }
        } catch (MdekException e) {
            // Wrap the MdekException in a RuntimeException so dwr can convert it
            log.error("MdekException while checking if one of several address can be cut.", e);
            throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
        } catch (Exception e) {
            log.error("Error checking if one of several address can be copied.", e);
        }
        return success;
    }

	public boolean canCutAddress(String uuid) {
		log.debug("Query if address can be cut: "+uuid);

		try {
			addressRequestHandler.canCutAddress(uuid);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while checking if address can be cut.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error checking if address can be cut.", e);
		}
		return true;
	}

	
	public TreeNodeBean copyAddress(String nodeUuid, String dstNodeUuid,
			Boolean includeChildren, Boolean copyToFreeAddress) {
		log.debug("Copying address with ID: "+nodeUuid+" to ID: "+dstNodeUuid);

		try {
			TreeNodeBean copyResult = addressRequestHandler.copyAddress(nodeUuid, dstNodeUuid, includeChildren, copyToFreeAddress);
			if (copyResult != null) {
				TreeServiceImpl.addTreeNodeAddressInfo(copyResult);
			}
			return copyResult;
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while copying address.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.error("Error copying address.", e);
			throw new RuntimeException(e);
		}
	}
	
	public TreeNodeBean[] copyAddresses(String[] nodeUuids, String dstNodeUuid, Boolean includeChildren, Boolean copyToFreeAddress) {
        TreeNodeBean[] result = new TreeNodeBean[nodeUuids.length];
        int i = 0;
        for (String uuid : nodeUuids) {
            result[i++] = copyAddress(uuid, dstNodeUuid, includeChildren, copyToFreeAddress);
        }
        return result;
    }

	public MdekAddressBean createNewAddress(String parentUuid) {
		log.debug("creating new address with parent id = "+parentUuid);		
		MdekAddressBean data = null;
		try {
			data = addressRequestHandler.getInitialAddress(parentUuid);
		} catch (MdekException e) {
            // Wrap the MdekException in a RuntimeException so dwr can convert it
            log.error("Error creating address.", e);
            throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (RuntimeException e) {
            log.error("Error creating address", e);
            throw e;
        }

		if (data != null) {
    		MdekAddressUtils.setInitialValues(data);
    
    		data.setNodeAppType(ADDRESS_APPTYPE);
    		data.setUuid("newNode");
		}
		return data;
	}

	
	public void deleteAddress(String uuid, Boolean forceDeleteReferences, Boolean markOnly) {
		try {
			addressRequestHandler.deleteAddress(uuid, forceDeleteReferences);
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("Error deleting address.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (RuntimeException e) {
			log.error("Error deleting address", e);
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
			log.error("Error deleting address working Copy.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.error("Error deleting address working Copy.", e);
			throw new RuntimeException(e);
		}
		return null;
	}

	public MdekAddressBean getAddressData(String nodeUuid, Boolean useWorkingCopy) {
		MdekAddressBean address = null; 

		try {
			address = addressRequestHandler.getAddressDetail(nodeUuid);
		} catch (RuntimeException e) {
			log.error("Error while getting address data.", e);
			throw e;
		}

		return address;
	}
	
	public List<String> getAddressInstitutions(List<String> uuidList) {
		MdekAddressBean address 	= null;
		List<String> institutions 	= new ArrayList<>();
		
		for (String uuid : uuidList) {
			address = getAddressData(uuid, false);
			
			institutions.add(MdekAddressUtils.extractInstitutions(address));
		}		
		
		return institutions;
	}
	
	public MdekAddressBean getPublishedAddressData(String nodeUuid) {
		MdekAddressBean address = null; 

		try {
			address = addressRequestHandler.getPublishedAddressDetail(nodeUuid);
		} catch (RuntimeException e) {
			log.error("Error while getting published address data.", e);
			throw e;
		}

		return address;
	}

	public List<String> getPathToAddress(String uuid) {
		return addressRequestHandler.getPathToAddress(uuid);
	}


	public void moveAddress(String nodeUuid, String oldParentUuid, String newParentUuid, boolean moveToFreeAddress, boolean forcePublicationCondition) {
		log.debug("Moving address with ID: "+nodeUuid+" and parent "+oldParentUuid+" to ID: "+newParentUuid);

		try {
			addressRequestHandler.moveAddressSubTree(nodeUuid, oldParentUuid, newParentUuid, moveToFreeAddress, forcePublicationCondition);
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while moving address.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.error("Error moving address.", e);
			throw new RuntimeException(e);
		}
	}
	
	public void moveAddresses(String[] nodeUuids, String[] oldParentUuids, String newParentUuid, boolean moveToFreeAddress, boolean forcePublicationCondition) {
	    int i = 0;
        for (String uuid : nodeUuids) {
            moveAddress(uuid, oldParentUuids[i++], newParentUuid, moveToFreeAddress, forcePublicationCondition);
        }
	}

	public MdekAddressBean saveAddressData(MdekAddressBean data, Boolean useWorkingCopy, boolean forcePublicationCondition) {
		log.debug("saveAddressData()");

		if (useWorkingCopy) {
			log.debug("Saving address with ID: "+data.getUuid());

			try { return addressRequestHandler.saveAddress(data); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.error("MdekException while saving address.", e);
				throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
			}
			catch (RuntimeException e) {
				log.error("Error while saving node", e);
				throw e;
			}
		} else {
			log.debug("Publishing address with ID: "+data.getUuid());

			try { return addressRequestHandler.publishAddress(data, forcePublicationCondition); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.error("MdekException while publishing address.", e);
				throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
			}
			catch (RuntimeException e) {
				log.error("Error while publishing address", e);
				throw e;
			}
		}
	}

	public MdekAddressBean assignAddressToQA(MdekAddressBean data) {
		log.debug("Forwarding address with ID to QA: "+data.getUuid());

		try { return addressRequestHandler.assignAddressToQA(data); }
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while forwarding address to QA.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (RuntimeException e) {
			log.error("Error while forwarding address to QA", e);
			throw e;
		}
	}		

	public MdekAddressBean reassignAddressToAuthor(MdekAddressBean data) {
		log.debug("Forwarding address with ID to Author: "+data.getUuid());

		try { return addressRequestHandler.reassignAddressToAuthor(data); }
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while forwarding address to Author.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (RuntimeException e) {
			log.error("Error while forwarding address to Author", e);
			throw e;
		}
	}		

	public MdekAddressBean fetchAddressObjectReferences(String addrUuid, int startIndex, int numRefs) {
		try { return addressRequestHandler.fetchAddressObjectReferences(addrUuid, startIndex, numRefs); }
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while fetching address AORs.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (RuntimeException e) {
			log.error("Error while fetching address AORs.", e);
			throw e;
		}		
	}

	public AddressSearchResultSimpleBean getWorkAddresses(IdcWorkEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits) {
		try {
			AddressSearchResultBean workAddresses = addressRequestHandler.getWorkAddresses(selectionType, orderBy, orderAsc, startHit, numHits);
            Iterator<MdekAddressBean> iterator = workAddresses.getResultList().iterator();
            AddressSearchResultSimpleBean workAddressesSimple = new AddressSearchResultSimpleBean();
            workAddressesSimple.setTotalNumHits( workAddresses.getTotalNumHits() );
            workAddressesSimple.setNumHits( workAddresses.getNumHits() );
            while (iterator.hasNext()) {
                MdekAddressBean bean = iterator.next();
                MdekAddressSimpleBean simpleBean = new MdekAddressSimpleBean();
                simpleBean.setUuid( bean.getUuid() );
                simpleBean.setModificationTime( bean.getModificationTime() );
                simpleBean.setNodeDocType( bean.getNodeDocType() );
                simpleBean.setAddressClass( bean.getAddressClass() );
                simpleBean.setGivenName( bean.getGivenName() );
                simpleBean.setName( bean.getName() );
                simpleBean.setOrganisation( bean.getOrganisation() );
                simpleBean.setWorkState( bean.getWorkState() );
                simpleBean.setWritePermission( bean.getWritePermission() );
                
                workAddressesSimple.getResultList().add( simpleBean );
            }
            return workAddressesSimple;

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while fetching Work addresses.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
	
		} catch (RuntimeException e) {
			log.error("Error while fetching Work addresses", e);
			throw e;
		}
	}
	
	public AddressSearchResultBean getWorkAddressesQA(IdcWorkEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits) {
	    try {
	        return addressRequestHandler.getWorkAddresses(selectionType, orderBy, orderAsc, startHit, numHits);
	        
	    } catch (MdekException e) {
	        // Wrap the MdekException in a RuntimeException so dwr can convert it
	        log.error("MdekException while fetching Work addresses.", e);
	        throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
	        
	    } catch (RuntimeException e) {
	        log.error("Error while fetching Work addresses", e);
	        throw e;
	    }
	}

	public AddressSearchResultBean getQAAddresses(WorkState workState, IdcQAEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits) {
		try {
			return addressRequestHandler.getQAAddresses(workState, selectionType, orderBy, orderAsc, startHit, numHits);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while fetching QA addresses.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));

		} catch (RuntimeException e) {
			log.error("Error while fetching QA addresses", e);
			throw e;
		}
	}

	public AddressStatisticsResultBean getAddressStatistics(String adrUuid, boolean freeAddressesOnly) {
		try {
			return addressRequestHandler.getAddressStatistics(adrUuid, freeAddressesOnly);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while fetching addresses statistics.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));

		} catch (RuntimeException e) {
			log.error("Error while fetching addresses statistics", e);
			throw e;
		}		
	}

	public ThesaurusStatisticsResultBean getAddressThesaurusStatistics(String adrUuid, boolean freeAddressesOnly, boolean thesaurusTerms, int startHit, int numHits) {
		try {
			return addressRequestHandler.getAddressThesaurusStatistics(adrUuid, freeAddressesOnly, thesaurusTerms, startHit, numHits);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while fetching addresses thesaurus statistics.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));

		} catch (RuntimeException e) {
			log.error("Error while fetching addresses thesaurus statistics", e);
			throw e;
		}
	}
	
	public String inheritAddressToChildren(String adrUuid) {
	    return addressRequestHandler.mergeAddressToSubAddresses(adrUuid);
	}

	public AddressRequestHandler getAddressRequestHandler() {
		return addressRequestHandler;
	}

	public void setAddressRequestHandler(AddressRequestHandler addressRequestHandler) {
		this.addressRequestHandler = addressRequestHandler;
	}

	public String getIsoXml(String id, boolean publishedVersion) {
        return addressRequestHandler.getIsoXml( id, publishedVersion );
    }
}
