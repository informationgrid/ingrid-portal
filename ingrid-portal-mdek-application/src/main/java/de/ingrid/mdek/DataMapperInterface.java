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
package de.ingrid.mdek;

import de.ingrid.mdek.beans.TreeNodeBean;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.utils.IngridDocument;

/**
 * @author mbenz
 * 
 * The Data Mapper Interface defines the methods needed to map external data to the internal
 * UDK data representation of the Mdek and the other way round.
 * 
 */
public interface DataMapperInterface {

	public TreeNodeBean getSimpleObjectRepresentation(IngridDocument doc);
	public MdekDataBean getDetailedObjectRepresentation(IngridDocument doc);
	public IngridDocument convertFromObjectRepresentation(MdekDataBean data);

	public TreeNodeBean getSimpleAddressRepresentation(IngridDocument doc);
	public MdekAddressBean getDetailedAddressRepresentation(IngridDocument doc);
	public IngridDocument convertFromAddressRepresentation(MdekAddressBean data);

	// Initialize an adr/obj with default values
	public void setInitialValues(MdekAddressBean addr);
	public void setInitialValues(MdekDataBean obj);


	// List IDs for key/value lookup
	public final static int MDEK_ADDRESS_REF_ID = 505;
	public final static int MDEK_ADDRESS_REF_SPECIAL_ID = 2010;

	// Language codes
	//   Codes are stored in the database
//	public final static String LANGUAGE_CODE_GERMAN = "de";
//	public final static String LANGUAGE_CODE_ENGLISH = "en";
	//   Identifiers are used by the frontend (sysList lookup, etc.)
//	public final static String LANGUAGE_ID_GERMAN = "121";
//	public final static String LANGUAGE_ID_ENGLISH = "94";
}
