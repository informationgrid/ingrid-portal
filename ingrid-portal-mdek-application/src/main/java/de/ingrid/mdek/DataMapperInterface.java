package de.ingrid.mdek;

import java.util.HashMap;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;

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

	// Sets the initial values for the address addr. Initial values are loaded from the backend.
	public void setInitialValues(MdekAddressBean addr);
	// Sets the initial values for the object obj. Initial values are loaded from the backend.
	public void setInitialValues(MdekDataBean obj);

	// List IDs for key/value lookup
	public final static int MDEK_ADDRESS_REF_ID = 505;
	public final static int MDEK_ADDRESS_REF_SPECIAL_ID = 2010;

	// List IDs for initial value lookup.
	public final static int VERTICAL_EXTENT_VDATUM_ID = 101;
	public final static int VERTICAL_EXTENT_UNIT_ID = 102;
	public final static int TIME_PERIOD_ID = 518;
	public final static int TIME_STATUS_ID = 523;
	public final static int HIERARCHY_LEVEL_ID = 525;
	public final static int VECTOR_TOPOLOGY_LEVEL_ID = 528;
	public final static int TIME_SCALE_ID = 1230;
	public final static int SERVICE_TYPE_ID = 5100;
	public final static int DATA_LANGUAGE_ID = 99999999;
	public final static int PUBLICATION_CONDITION_ID = 3571;

	// Miscellaneous
	public final static String MDEK_ID = "id";
	public final static String MDEK_HAS_CHILDREN = "isFolder";
	public final static String MDEK_IS_PUBLISHED = "isPublished";
	public final static String MDEK_USER_WRITE_PERMISSION = "userWritePermission";
	public final static String MDEK_USER_WRITE_SINGLE_PERMISSION = "userWriteSinglePermission";
	public final static String MDEK_USER_WRITE_TREE_PERMISSION = "userWriteTreePermission";

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
