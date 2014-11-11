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

import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.query.AddressExtSearchParamsBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectExtSearchParamsBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.SearchResultBean;

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