package de.ingrid.mdek.handler;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.ExportInfoBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCaller.AddressArea;
import de.ingrid.mdek.dwr.util.HTTPSessionHelper;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.utils.IngridDocument;

public class CatalogRequestHandlerImpl implements CatalogRequestHandler {

	private final static Logger log = Logger.getLogger(CatalogRequestHandlerImpl.class);

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

	public List<Map<String, String>> getSysGuis(String[] guiIds) {
		IngridDocument response = mdekCallerCatalog.getSysGuis(connectionFacade.getCurrentPlugId(), guiIds, HTTPSessionHelper.getCurrentSessionId());
		return MdekCatalogUtils.extractSysGuisFromResponse(response);
	}

	public List<Map<String, String>> storeSysGuis(List<Map<String, String>> sysGuis, boolean refetchAfterStore) {
		List<IngridDocument> sysGuiDoc = MdekCatalogUtils.convertFromSysGuiRepresentation(sysGuis);

		IngridDocument response = mdekCallerCatalog.storeSysGuis(connectionFacade.getCurrentPlugId(), sysGuiDoc, refetchAfterStore, HTTPSessionHelper.getCurrentSessionId());
		return MdekCatalogUtils.extractSysGuisFromResponse(response);
	}

	public CatalogBean getCatalogData() {
		IngridDocument response = mdekCallerCatalog.fetchCatalog(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentSessionId());
		return MdekCatalogUtils.extractCatalogFromResponse(response);
	}

	public CatalogBean storeCatalogData(CatalogBean cat) {
		IngridDocument catDoc = (IngridDocument) MdekCatalogUtils.convertFromCatalogRepresentation(cat);

		log.debug("Sending the following catalog for storage:");
		log.debug(catDoc);

		IngridDocument response = mdekCallerCatalog.storeCatalog(connectionFacade.getCurrentPlugId(), catDoc, true, HTTPSessionHelper.getCurrentSessionId());
		return MdekCatalogUtils.extractCatalogFromResponse(response);
	}

	public void exportFreeAddresses() {
		exportAddressBranch(null, false, AddressArea.ALL_FREE_ADDRESSES);
	}

	public void exportTopAddresses(boolean exportChildren) {
		exportAddressBranch(null, exportChildren, AddressArea.ALL_ADDRESSES);
	}

	public void exportAddressBranch(String rootUuid, boolean exportChildren) {
		exportAddressBranch(rootUuid, exportChildren, null);
	}

	private void exportAddressBranch(String rootUuid, boolean exportChildren, AddressArea addressArea) {
		mdekCallerCatalog.exportAddressBranch(
				connectionFacade.getCurrentPlugId(),
				rootUuid, !exportChildren,
				addressArea, HTTPSessionHelper.getCurrentSessionId());
	}

	public void exportObjectBranch(String rootUuid, boolean exportChildren) {
		mdekCallerCatalog.exportObjectBranch(
				connectionFacade.getCurrentPlugId(),
				rootUuid,
				!exportChildren, HTTPSessionHelper.getCurrentSessionId());
	}

	public ExportInfoBean getExportInfo() {
		IngridDocument response = mdekCallerCatalog.getExportInfo(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentSessionId());
		return MdekCatalogUtils.extractExportInfoFromResponse(response);
	}

	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}


	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
}
