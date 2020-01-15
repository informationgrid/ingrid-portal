/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.query.AddressExtSearchParamsBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectExtSearchParamsBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.SearchResultBean;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.util.MdekAddressUtils;
import de.ingrid.mdek.util.MdekObjectUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

@Service("queryRequestHandler")
public class QueryRequestHandlerImpl implements QueryRequestHandler {

	private static final Logger log = Logger.getLogger(QueryRequestHandlerImpl.class);

	// Injected by Spring
	@Autowired
	private ConnectionFacade connectionFacade;

	// Initialized by spring through the init method
	private IMdekCallerQuery mdekCallerQuery;
	private IMdekCallerAddress mdekCallerAddress;

	public void init() {
		mdekCallerQuery = connectionFacade.getMdekCallerQuery();
		mdekCallerAddress = connectionFacade.getMdekCallerAddress();
	}

	
	public AddressSearchResultBean queryAddressesThesaurusTerm(String topicId, int startHit, int numHits) {
		log.debug("Searching for addresses with topicId: "+topicId);

		IngridDocument response = mdekCallerQuery.queryAddressesThesaurusTerm(connectionFacade.getCurrentPlugId(), topicId, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());
		
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public AddressSearchResultBean queryAddressesExtended(AddressExtSearchParamsBean query, int startHit, int numHits) {
		IngridDocument queryParams = MdekUtils.convertAddressExtSearchParamsToIngridDoc(query);

		IngridDocument response = mdekCallerQuery.queryAddressesExtended(connectionFacade.getCurrentPlugId(), queryParams, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public ObjectSearchResultBean queryObjectsExtended(ObjectExtSearchParamsBean query, int startHit, int numHits) {
		IngridDocument queryParams = MdekUtils.convertObjectExtSearchParamsToIngridDoc(query);

		IngridDocument response = mdekCallerQuery.queryObjectsExtended(connectionFacade.getCurrentPlugId(), queryParams, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());
		return MdekObjectUtils.extractObjectSearchResultsFromResponse(response);
	}

	public AddressSearchResultBean queryAddressesFullText(String searchTerm, int startHit, int numHits) {
		IngridDocument response = mdekCallerQuery.queryAddressesFullText(connectionFacade.getCurrentPlugId(), searchTerm, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public ObjectSearchResultBean queryObjectsFullText(String searchTerm, int startHit, int numHits) {
		IngridDocument response = mdekCallerQuery.queryObjectsFullText(connectionFacade.getCurrentPlugId(), searchTerm, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());
		return MdekObjectUtils.extractObjectSearchResultsFromResponse(response);
	}

	public SearchResultBean queryHQL(String hqlQuery, int startHit, int numHits) {
		log.debug("Searching via HQL query: "+hqlQuery);

		IngridDocument response = mdekCallerQuery.queryHQL(connectionFacade.getCurrentPlugId(), hqlQuery, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());

		return MdekUtils.extractSearchResultsFromResponse(response);
	}

	public SearchResultBean queryHQLToCSV(String hqlQuery) {
		log.debug("Searching via HQL to csv query: "+hqlQuery);

		IngridDocument response = mdekCallerQuery.queryHQLToCsv(connectionFacade.getCurrentPlugId(), hqlQuery, MdekSecurityUtils.getCurrentUserUuid());

		return MdekUtils.extractSearchResultsFromResponse(response);
	}

	public ObjectSearchResultBean queryObjectsThesaurusTerm(String topicId, int startHit, int numHits) {
		log.debug("Searching for objects with topicId: "+topicId);

		IngridDocument response = mdekCallerQuery.queryObjectsThesaurusTerm(connectionFacade.getCurrentPlugId(), topicId, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());
		
		return MdekObjectUtils.extractObjectSearchResultsFromResponse(response);
	}

	public AddressSearchResultBean searchAddresses(MdekAddressBean adr, int startHit, int numHits) {
		IngridDocument adrDoc = MdekAddressUtils.convertFromAddressRepresentation(adr);

		log.debug("Sending the following address search:");
		log.debug(adrDoc);

		IngridDocument response = mdekCallerAddress.searchAddresses(connectionFacade.getCurrentPlugId(), adrDoc, startHit, numHits, MdekSecurityUtils.getCurrentUserUuid());
		
		// TODO Convert the response
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public IMdekCallerQuery getMdekCallerQuery() {
		return mdekCallerQuery;
	}

	public void setMdekCallerQuery(IMdekCallerQuery mdekCallerQuery) {
		this.mdekCallerQuery = mdekCallerQuery;
	}


	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}


	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

}
