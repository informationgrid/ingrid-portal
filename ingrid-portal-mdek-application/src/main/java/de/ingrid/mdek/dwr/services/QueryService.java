package de.ingrid.mdek.dwr.services;

import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.beans.AddressExtSearchParamsBean;
import de.ingrid.mdek.beans.AddressSearchResultBean;
import de.ingrid.mdek.beans.MdekAddressBean;
import de.ingrid.mdek.beans.ObjectExtSearchParamsBean;
import de.ingrid.mdek.beans.ObjectSearchResultBean;
import de.ingrid.mdek.beans.SearchResultBean;

public interface QueryService {

	public AddressSearchResultBean searchAddresses(MdekAddressBean adr, int startHit, int numHits);

	public AddressSearchResultBean queryAddressesExtended(AddressExtSearchParamsBean params, int startHit, int numHits);
	public ObjectSearchResultBean queryObjectsExtended(ObjectExtSearchParamsBean params, int startHit, int numHits);

	public AddressSearchResultBean queryAddressesFullText(String searchTerm, int startHit, int numHits);
	public ObjectSearchResultBean queryObjectsFullText(String searchTerm, int startHit, int numHits);

	public AddressSearchResultBean queryAddressesThesaurusTerm(String topicId, int startHit, int numHits);
	public ObjectSearchResultBean queryObjectsThesaurusTerm(String topicId, int startHit, int numHits);

	public FileTransfer queryHQLToCSV(String hqlQuery);
	public SearchResultBean queryHQL(String hqlQuery, int startHit, int numHits);
}