package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

public class MapForm extends ActionForm {


	private static final long serialVersionUID = 5284771128504510508L;

	/** attribute name of action form in session */
    public static final String SESSION_KEY = "map_form";

    public static final String FIELD_MAP_NAME = "mapurl";
    
    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        // no default data to set
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_MAP_NAME, request.getParameter(FIELD_MAP_NAME));
       }

    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        if (!hasInput(FIELD_MAP_NAME)) {
            setError(FIELD_MAP_NAME, "maps.error.noName");
            allOk = false;
        }
        
        return allOk;
    }
}
