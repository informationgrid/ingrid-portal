package de.ingrid.mdek.handler;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.dwr.util.HTTPSessionHelper;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.utils.IngridDocument;

public class CatalogRequestHandlerImpl implements CatalogRequestHandler {

//	private final static Logger log = Logger.getLogger(CatalogRequestHandlerImpl.class);

	// Injected by Spring
	private ConnectionFacade connectionFacade;

	// Initialized by spring through the init method
	private IMdekCallerCatalog mdekCallerCatalog;

	public void init() {
		mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
	}
	
	
	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode) {
		IngridDocument response = mdekCallerCatalog.getSysLists(connectionFacade.getCurrentPlugId(), listIds, languageCode, HTTPSessionHelper.getCurrentSessionId());
		return MdekCatalogUtils.extractSysListFromResponse(response);
	}

	public CatalogBean getCatalogData() {
		IngridDocument response = mdekCallerCatalog.fetchCatalog(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentSessionId());
		return MdekCatalogUtils.extractCatalogFromResponse(response);
	}


	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}


	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
}
