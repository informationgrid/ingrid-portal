package de.ingrid.mdek.dwr.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.query.AddressExtSearchParamsBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.AddressWorkflowResultBean;
import de.ingrid.mdek.beans.query.ObjectExtSearchParamsBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectWorkflowResultBean;
import de.ingrid.mdek.beans.query.SearchResultBean;
import de.ingrid.mdek.handler.QueryRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekErrorUtils;

public class QueryServiceImpl implements QueryService {

	private final static Logger log = Logger.getLogger(QueryServiceImpl.class);	

	// Injected by Spring
	private QueryRequestHandler queryRequestHandler;

	
	public AddressSearchResultBean queryAddressesThesaurusTerm(String topicId, int startHit, int numHits) {
		log.debug("Starting address query.");

		try {
			  return queryRequestHandler.queryAddressesThesaurusTerm(topicId, startHit, numHits);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while searching for addresses.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error while searching for addresses.", e);
		}
		return new AddressSearchResultBean();
	}

	
	public AddressSearchResultBean queryAddressesExtended(AddressExtSearchParamsBean query, int startHit, int numHits) {
		log.debug("Starting extended address query.");

		try {
			  return queryRequestHandler.queryAddressesExtended(query, startHit, numHits);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while executing an extended search for addresses.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error while executing an extended search for addresses.", e);
		}
		return new AddressSearchResultBean();
	}

	public ObjectSearchResultBean queryObjectsExtended(ObjectExtSearchParamsBean query, int startHit, int numHits) {
		log.debug("Starting extended object query.");

		try {
			  return queryRequestHandler.queryObjectsExtended(query, startHit, numHits);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while executing an extended search for objects.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error while executing an extended search for objects.", e);
		}
		return new ObjectSearchResultBean();
	}

	public AddressSearchResultBean queryAddressesFullText(String searchTerm, int startHit, int numHits) {
		log.debug("Starting fulltext address query.");

		try {
			  return queryRequestHandler.queryAddressesFullText(searchTerm, startHit, numHits);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while executing a fulltext search for addresses.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error while executing a fulltext search for addresses.", e);
		}
		return new AddressSearchResultBean();
	}

	
	public ObjectSearchResultBean queryObjectsFullText(String searchTerm, int startHit, int numHits) {
		log.debug("Starting fulltext object query.");

		try {
			  return queryRequestHandler.queryObjectsFullText(searchTerm, startHit, numHits);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while executing a fulltext search for objects.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error while executing a fulltext search for objects.", e);
		}
		return new ObjectSearchResultBean();
	}

	public SearchResultBean queryHQL(String hqlQuery, int startHit, int numHits) {
		log.debug("Starting hql query.");

		try {
			  return queryRequestHandler.queryHQL(hqlQuery, startHit, numHits);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while searching for objects.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
//		} catch (Exception e) {
//			log.error("Error while searching for objects.", e);
//			throw e;
		}
//		return new SearchResultBean();
	}

	public FileTransfer queryHQLToCSV(String hqlQuery) {
		log.debug("Starting hql query.");

		try {
			SearchResultBean res = queryRequestHandler.queryHQLToCSV(hqlQuery);

	        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	        buffer.write(res.getCsvSearchResult().getData().getBytes());

	        return new FileTransfer("values.csv", "text/comma-separated-values", buffer.toByteArray());

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while searching for objects.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (IOException e) {
			log.debug("IOException while converting csv.", e);
			return null;
		} catch (RuntimeException e) {
			log.debug("Runtime Exception while loading csv values", e);
			throw new RuntimeException(e.getCause());
		}
	}

	
	public ObjectSearchResultBean queryObjectsThesaurusTerm(String topicId, int startHit, int numHits){
		log.debug("Starting object query.");

		try {
			  return queryRequestHandler.queryObjectsThesaurusTerm(topicId, startHit, numHits);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while searching for objects.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error while searching for objects.", e);
		}
		return new ObjectSearchResultBean();		
	}


	public AddressSearchResultBean searchAddresses(MdekAddressBean adr, int startHit, int numHits) {
		log.debug("Starting address search.");

		try {
			  return queryRequestHandler.searchAddresses(adr, startHit, numHits);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while searching for addresses.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error while searching for addresses.", e);
		}
		return new AddressSearchResultBean();
	}

	public ArrayList<AddressWorkflowResultBean> queryAddressesForWorkflowManagement() {
		log.debug("Starting address search for workflow management.");

		try {
			return queryRequestHandler.searchAddressesForWorkflowManagement();
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while searching for addresses - workflow management.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error while searching for addresses - workflow management.", e);
		}
		return new ArrayList<AddressWorkflowResultBean>();
	}

	public ArrayList<ObjectWorkflowResultBean> queryObjectsForWorkflowManagement() {
		log.debug("Starting object search for workflow management.");

		try {
			return queryRequestHandler.searchObjectsForWorkflowManagement();
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while searching for objects - workflow management.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error while searching for objects - workflow management.", e);
		}
		return new ArrayList<ObjectWorkflowResultBean>();
	}

	public QueryRequestHandler getQueryRequestHandler() {
		return queryRequestHandler;
	}


	public void setQueryRequestHandler(QueryRequestHandler queryRequestHandler) {
		this.queryRequestHandler = queryRequestHandler;
	}

}
