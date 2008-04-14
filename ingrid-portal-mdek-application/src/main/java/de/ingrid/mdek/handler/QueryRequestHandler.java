package de.ingrid.mdek.handler;

import de.ingrid.mdek.beans.AddressExtSearchParamsBean;
import de.ingrid.mdek.beans.AddressSearchResultBean;
import de.ingrid.mdek.beans.MdekAddressBean;
import de.ingrid.mdek.beans.ObjectExtSearchParamsBean;
import de.ingrid.mdek.beans.ObjectSearchResultBean;
import de.ingrid.mdek.beans.SearchResultBean;

public interface QueryRequestHandler {

	public AddressSearchResultBean searchAddresses(MdekAddressBean adr, int startHit, int numHits);

	public AddressSearchResultBean queryAddressesExtended(AddressExtSearchParamsBean query, int startHit, int numHits);
	public ObjectSearchResultBean queryObjectsExtended(ObjectExtSearchParamsBean query, int startHit, int numHits);

	public AddressSearchResultBean queryAddressesFullText(String searchTerm, int startHit, int numHits);
	public ObjectSearchResultBean queryObjectsFullText(String searchTerm, int startHit, int numHits);

	public AddressSearchResultBean queryAddressesThesaurusTerm(String topicId, int startHit, int numHits);
	public ObjectSearchResultBean queryObjectsThesaurusTerm(String topicId, int startHit, int numHits);
	
	public SearchResultBean queryHQL(String hqlQuery, int startHit, int numHits);
	public SearchResultBean queryHQLToCSV(String hqlQuery);
}
