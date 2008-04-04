package de.ingrid.mdek;

import java.util.HashMap;

import de.ingrid.mdek.beans.MdekAddressBean;
import de.ingrid.mdek.beans.MdekDataBean;

/**
 * @author mbenz
 * 
 * The Data Mapper Interface defines the methods needed to map external data to the internal
 * UDK data representation of the Mdek and the other way round.
 * 
 * The static Strings defined in this interface represent the Mdek view of an Udk object
 * 
 */
public interface DataMapperInterface {

	// TODO Change return type to MdekDataBean?
	public HashMap<String, Object> getSimpleObjectRepresentation(Object obj);
	public MdekDataBean getDetailedObjectRepresentation(Object obj);

	// We return an Object since we don't know all the possible target types in advance 
	public Object convertFromObjectRepresentation(MdekDataBean data);
	public HashMap<String, Object> getSimpleAddressRepresentation(Object obj);
	public MdekAddressBean getDetailedAddressRepresentation(Object obj);
	public Object convertFromAddressRepresentation(MdekAddressBean data);

	// List IDs for key/value lookup
	public final static int MDEK_ADDRESS_REF_ID = 505;
	public final static int MDEK_ADDRESS_REF_SPECIAL_ID = 2010;
	
	
	// Miscellaneous
	public final static String MDEK_ID = "id";
	public final static String MDEK_HAS_CHILDREN = "isFolder";
	public final static String MDEK_IS_PUBLISHED = "isPublished";
	// TODO: Some values are duplicates. Merge them!
	public final static String MDEK_TITLE = "title";
	public final static String MDEK_DOCTYPE = "nodeDocType";
	public final static String MDEK_CLASS = "objectClass";

	// Communication
	public final static String MDEK_ADDRESS_COMM = "communication";
	public final static String MDEK_ADDRESS_COMM_MEDIUM = "communicationMedium";
	public final static String MDEK_ADDRESS_COMM_VALUE = "communicationValue";
	public final static String MDEK_ADDRESS_COMM_DESCRIPTION = "communicationDescription";

	// Language codes
	//   Codes are stored in the database
	public final static String LANGUAGE_CODE_GERMAN = "de";
	public final static String LANGUAGE_CODE_ENGLISH = "en";
	//   Identifiers are used by the frontend (sysList lookup, etc.)
	public final static String LANGUAGE_ID_GERMAN = "121";
	public final static String LANGUAGE_ID_ENGLISH = "94";
}
