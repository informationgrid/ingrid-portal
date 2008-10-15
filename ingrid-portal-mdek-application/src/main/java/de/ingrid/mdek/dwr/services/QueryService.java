package de.ingrid.mdek.dwr.services;

import java.util.ArrayList;

import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.query.AddressExtSearchParamsBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.AddressWorkflowResultBean;
import de.ingrid.mdek.beans.query.ObjectExtSearchParamsBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectWorkflowResultBean;
import de.ingrid.mdek.beans.query.SearchResultBean;

public interface QueryService {

	public AddressSearchResultBean searchAddresses(MdekAddressBean adr, int startHit, int numHits);

	public AddressSearchResultBean queryAddressesExtended(AddressExtSearchParamsBean params, int startHit, int numHits);
	public ObjectSearchResultBean queryObjectsExtended(ObjectExtSearchParamsBean params, int startHit, int numHits);

	public AddressSearchResultBean queryAddressesFullText(String searchTerm, int startHit, int numHits);
	public ObjectSearchResultBean queryObjectsFullText(String searchTerm, int startHit, int numHits);

	public AddressSearchResultBean queryAddressesThesaurusTerm(String topicId, int startHit, int numHits);
	public ObjectSearchResultBean queryObjectsThesaurusTerm(String topicId, int startHit, int numHits);

	public ArrayList<AddressWorkflowResultBean> getExpiredAddresses(int numHits);
	public ArrayList<ObjectWorkflowResultBean> getExpiredObjects(int numHits);

	public ArrayList<AddressWorkflowResultBean> getModifiedAddresses(int numHits);
	public ArrayList<ObjectWorkflowResultBean> getModifiedObjects(int numHits);

	public ArrayList<AddressWorkflowResultBean> getQAAddresses(int numHits);
	public ArrayList<ObjectWorkflowResultBean> getQAObjects(int numHits);

	public FileTransfer queryHQLToCSV(String hqlQuery);
	public SearchResultBean queryHQL(String hqlQuery, int startHit, int numHits);
}