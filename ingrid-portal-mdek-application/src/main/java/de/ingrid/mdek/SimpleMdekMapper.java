package de.ingrid.mdek;

import java.util.HashMap;

import de.ingrid.mdek.dwr.MdekDataBean;

public class SimpleMdekMapper implements DataMapperInterface {

	// -- Dispatch to the local private methods --
	public MdekDataBean getDetailedMdekRepresentation(Object obj) {
		if (obj instanceof HashMap)
			return getDetailedMdekRepresentation((HashMap<String, Object>) obj);
		else
			return null;		
	}
	public HashMap<String, Object> getSimpleMdekRepresentation(Object obj) {
		if (obj instanceof HashMap)
			return getSimpleMdekRepresentation((HashMap<String, Object>) obj);
		else
			return null;
	}
	// --


	private MdekDataBean getDetailedMdekRepresentation(
			HashMap<String, Object> obj) {

		// by default, assertions are disabled at runtime.
		// Start the server with -ea to enable this method call
		try { assert testInputConformity(obj); }
		catch (AssertionError e) { e.printStackTrace(); }

		MdekDataBean mdekObj = new MdekDataBean();

		mdekObj.setGeneralDescription((String) obj.get(MdekKeys.ABSTRACT));
		mdekObj.setId((String) obj.get(MdekKeys.UUID));
		mdekObj.setTitle((String) obj.get(MdekKeys.TITLE));
		mdekObj.setObjectClass((Integer) obj.get(MdekKeys.CLASS));
		mdekObj.setNodeDocType("Class"+((Integer) obj.get(MdekKeys.CLASS) + 1));
		mdekObj.setHasChildren((Boolean) obj.get(MdekKeys.HAS_CHILD));
		mdekObj.setObjectName((String) obj.get(MdekKeys.TITLE));

		return mdekObj;
	}

	private HashMap<String, Object> getSimpleMdekRepresentation(
			HashMap<String, Object> obj) {
		
		// by default, assertions are disabled at runtime.
		// Start the server with -ea to enable this method call
		try { assert testInputConformity(obj); }
		catch (AssertionError e) { e.printStackTrace(); }

		HashMap<String, Object> mdekObj = new HashMap<String, Object>();

		mdekObj.put(MDEK_OBJECT_UUID, obj.get(MdekKeys.UUID));
		mdekObj.put(MDEK_OBJECT_TITLE, obj.get(MdekKeys.TITLE));
		mdekObj.put(MDEK_OBJECT_DOCTYPE, "Class"+((Integer) obj.get(MdekKeys.CLASS) + 1));
		mdekObj.put(MDEK_OBJECT_HAS_CHILDREN, obj.get(MdekKeys.HAS_CHILD));

		return mdekObj;
	}

	public Object convertFromMdekRepresentation(MdekDataBean data){
		HashMap<String, Object> udkObj = new HashMap<String, Object>();

		udkObj.put(MdekKeys.ABSTRACT, data.getGeneralDescription());
		udkObj.put(MdekKeys.UUID, data.getId());
		udkObj.put(MdekKeys.TITLE, data.getTitle());
		udkObj.put(MdekKeys.CLASS, data.getObjectClass());
		udkObj.put(MdekKeys.HAS_CHILD, data.getHasChildren());

		return udkObj;
	}

	
	
	// ------------------------ Helper Methods ---------------------------------------	

	private static boolean testInputConformity(HashMap<String, Object> obj)
	{
		Object val = null;
		Integer valInt = null;
//		val = obj.get(MdekKeys.ABSTRACT);
//      assert val != null : "Input HashMap does not contain a key: MdekKeys.ABSTRACT";
//		assert val instanceof String : "MdekKeys.ABSTRACT";

		val = obj.get(MdekKeys.UUID);
		assert val != null : "Input HashMap does not contain a key: MdekKeys.UUID";
		assert val instanceof String : "Input HashMap value for key MdekKeys.UUID is not of type String";

		val = obj.get(MdekKeys.TITLE);
		assert val != null : "Input HashMap does not contain a key: MdekKeys.TITLE";
		assert val instanceof String : "Input HashMap value for key MdekKeys.TITLE is not of type String";

		val = obj.get(MdekKeys.CLASS);
		assert val != null : "Input HashMap does not contain a key: MdekKeys.CLASS";
		assert val instanceof Integer : "Input HashMap value for key MdekKeys.CLASS is not of type Integer";

		valInt = (Integer) val;
		assert (valInt >= 0 && valInt <= 5) : "Input HashMap value for key MdekKeys.CLASS is not in the range of 0 and 5";

		val = obj.get(MdekKeys.HAS_CHILD);
		assert val != null : "Input HashMap does not contain a key: MdekKeys.HAS_CHILD";
		assert val instanceof Boolean : "Input HashMap value for key MdekKeys.CLASS is not of type Boolean";

		return true;
	}

}