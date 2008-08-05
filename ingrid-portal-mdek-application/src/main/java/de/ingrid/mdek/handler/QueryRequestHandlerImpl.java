package de.ingrid.mdek.handler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.query.AddressExtSearchParamsBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.AddressWorkflowResultBean;
import de.ingrid.mdek.beans.query.ObjectExtSearchParamsBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectWorkflowResultBean;
import de.ingrid.mdek.beans.query.SearchResultBean;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.dwr.util.HTTPSessionHelper;
import de.ingrid.mdek.util.MdekAddressUtils;
import de.ingrid.mdek.util.MdekObjectUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class QueryRequestHandlerImpl implements QueryRequestHandler {

	private final static Logger log = Logger.getLogger(QueryRequestHandlerImpl.class);

	// Injected by Spring
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

		IngridDocument response = mdekCallerQuery.queryAddressesThesaurusTerm(connectionFacade.getCurrentPlugId(), topicId, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public AddressSearchResultBean queryAddressesExtended(AddressExtSearchParamsBean query, int startHit, int numHits) {
		IngridDocument queryParams = MdekUtils.convertAddressExtSearchParamsToIngridDoc(query);

		IngridDocument response = mdekCallerQuery.queryAddressesExtended(connectionFacade.getCurrentPlugId(), queryParams, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public ObjectSearchResultBean queryObjectsExtended(ObjectExtSearchParamsBean query, int startHit, int numHits) {
		IngridDocument queryParams = MdekUtils.convertObjectExtSearchParamsToIngridDoc(query);

		IngridDocument response = mdekCallerQuery.queryObjectsExtended(connectionFacade.getCurrentPlugId(), queryParams, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		return MdekObjectUtils.extractObjectSearchResultsFromResponse(response);
	}

	public AddressSearchResultBean queryAddressesFullText(String searchTerm, int startHit, int numHits) {
		IngridDocument response = mdekCallerQuery.queryAddressesFullText(connectionFacade.getCurrentPlugId(), searchTerm, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public ObjectSearchResultBean queryObjectsFullText(String searchTerm, int startHit, int numHits) {
		IngridDocument response = mdekCallerQuery.queryObjectsFullText(connectionFacade.getCurrentPlugId(), searchTerm, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		return MdekObjectUtils.extractObjectSearchResultsFromResponse(response);
	}

	public SearchResultBean queryHQL(String hqlQuery, int startHit, int numHits) {
		log.debug("Searching via HQL query: "+hqlQuery);

		IngridDocument response = mdekCallerQuery.queryHQL(connectionFacade.getCurrentPlugId(), hqlQuery, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());

		return MdekUtils.extractSearchResultsFromResponse(response);
	}

	public SearchResultBean queryHQLToCSV(String hqlQuery) {
		log.debug("Searching via HQL to csv query: "+hqlQuery);

		IngridDocument response = mdekCallerQuery.queryHQLToCsv(connectionFacade.getCurrentPlugId(), hqlQuery, HTTPSessionHelper.getCurrentSessionId());

		return MdekUtils.extractSearchResultsFromResponse(response);
	}

	public ObjectSearchResultBean queryObjectsThesaurusTerm(String topicId, int startHit, int numHits) {
		log.debug("Searching for objects with topicId: "+topicId);

		IngridDocument response = mdekCallerQuery.queryObjectsThesaurusTerm(connectionFacade.getCurrentPlugId(), topicId, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		
		return MdekObjectUtils.extractObjectSearchResultsFromResponse(response);
	}

	public AddressSearchResultBean searchAddresses(MdekAddressBean adr, int startHit, int numHits) {
		IngridDocument adrDoc = (IngridDocument) MdekAddressUtils.convertFromAddressRepresentation(adr);

		log.debug("Sending the following address search:");
		log.debug(adrDoc);

		IngridDocument response = mdekCallerAddress.searchAddresses(connectionFacade.getCurrentPlugId(), adrDoc, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		
		// TODO Convert the response
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public ArrayList<AddressWorkflowResultBean> searchAddressesForWorkflowManagement() {
		// TODO Implement!
		AddressSearchResultBean adrResults = queryAddressesFullText("a", 0, 10);
		ArrayList<AddressWorkflowResultBean> result = new ArrayList<AddressWorkflowResultBean>();
		for (MdekAddressBean adr : adrResults.getResultList()) {
			AddressWorkflowResultBean adrWf = new AddressWorkflowResultBean();
			adrWf.setAddress(adr);
			double d = Math.random() * (new Date().getTime() - new Date(0).getTime());
			adrWf.setDate(new Date((long) d));

			int idx = (int) ((double) adrResults.getResultList().size() * Math.random());
			adrWf.setAssignedUser(adrResults.getResultList().get(idx));
			adrWf.setState("State");

			adrWf.setType(getRandomType());
			result.add(adrWf);
		}
		return result;
	}

	private String getRandomType() {
		int x = (int) (Math.random() * 4.0d);
		switch (x) {
		case 0: return "A";
		case 1: return "B";
		case 2: return "C";
		case 3: return "D";
		default: return "D";
		}
	}

	public ArrayList<ObjectWorkflowResultBean> searchObjectsForWorkflowManagement() {
		// TODO Implement!
		ObjectSearchResultBean objResults = queryObjectsFullText("g", 0, 10);
		ArrayList<ObjectWorkflowResultBean> result = new ArrayList<ObjectWorkflowResultBean>();
		for (MdekDataBean obj : objResults.getResultList()) {
			ObjectWorkflowResultBean objWf = new ObjectWorkflowResultBean();
			objWf.setObject(obj);
			double d = Math.random() * (new Date().getTime() - new Date(0).getTime());
			objWf.setDate(new Date((long) d));

//			objWf.setAssignedUser(null);
			objWf.setState("State");
			objWf.setType(getRandomType());
			result.add(objWf);
		}
		return result;
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
