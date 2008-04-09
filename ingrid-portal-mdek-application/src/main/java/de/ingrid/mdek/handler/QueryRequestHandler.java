package de.ingrid.mdek.handler;

import java.util.Map;

import de.ingrid.mdek.beans.AddressSearchResultBean;
import de.ingrid.mdek.beans.MdekAddressBean;
import de.ingrid.mdek.beans.ObjectSearchResultBean;
import de.ingrid.mdek.beans.SearchResultBean;

public interface QueryRequestHandler {

	public AddressSearchResultBean searchAddresses(MdekAddressBean adr, int startHit, int numHits);
	public AddressSearchResultBean queryExtAddresses(Map<String, String> params, int startHit, int numHits);
	public ObjectSearchResultBean queryExtObjects(Map<String, String> params, int startHit, int numHits);
	public AddressSearchResultBean queryAddressesThesaurusTerm(String topicId, int startHit, int numHits);
	public ObjectSearchResultBean queryObjectsThesaurusTerm(String topicId, int startHit, int numHits);
	public SearchResultBean queryHQL(String hqlQuery, int startHit, int numHits);
	public SearchResultBean queryHQLToCSV(String hqlQuery);
}
