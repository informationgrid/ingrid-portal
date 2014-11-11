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
package de.ingrid.mdek.dwr.services;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.query.AddressExtSearchParamsBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectExtSearchParamsBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
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

	        return new FileTransfer("values.csv.gz", "x-gzip", res.getCsvSearchResult().getData());

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while searching for objects.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));

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

	public QueryRequestHandler getQueryRequestHandler() {
		return queryRequestHandler;
	}


	public void setQueryRequestHandler(QueryRequestHandler queryRequestHandler) {
		this.queryRequestHandler = queryRequestHandler;
	}

}
