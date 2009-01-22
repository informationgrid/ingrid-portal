package de.ingrid.mdek.handler;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCaller.AddressArea;
import de.ingrid.mdek.dwr.util.HTTPSessionHelper;
import de.ingrid.mdek.persistence.db.model.UserData;
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
		IngridDocument response = mdekCallerCatalog.getSysLists(connectionFacade.getCurrentPlugId(), listIds, languageCode, HTTPSessionHelper.getCurrentUserUuid());
		return MdekCatalogUtils.extractSysListFromResponse(response);
	}

	public List<Map<String, String>> getSysGuis(String[] guiIds) {
		IngridDocument response = mdekCallerCatalog.getSysGuis(connectionFacade.getCurrentPlugId(), guiIds, HTTPSessionHelper.getCurrentUserUuid());
		return MdekCatalogUtils.extractSysGuisFromResponse(response);
	}

	public List<Map<String, String>> storeSysGuis(List<Map<String, String>> sysGuis, boolean refetchAfterStore) {
		List<IngridDocument> sysGuiDoc = MdekCatalogUtils.convertFromSysGuiRepresentation(sysGuis);

		IngridDocument response = mdekCallerCatalog.storeSysGuis(connectionFacade.getCurrentPlugId(), sysGuiDoc, refetchAfterStore, HTTPSessionHelper.getCurrentUserUuid());
		return MdekCatalogUtils.extractSysGuisFromResponse(response);
	}

	public CatalogBean getCatalogData() {
		IngridDocument response = mdekCallerCatalog.fetchCatalog(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentUserUuid());
		return MdekCatalogUtils.extractCatalogFromResponse(response);
	}

	public CatalogBean storeCatalogData(CatalogBean cat) {
		IngridDocument catDoc = (IngridDocument) MdekCatalogUtils.convertFromCatalogRepresentation(cat);

		log.debug("Sending the following catalog for storage:");
		log.debug(catDoc);

		IngridDocument response = mdekCallerCatalog.storeCatalog(connectionFacade.getCurrentPlugId(), catDoc, true, HTTPSessionHelper.getCurrentUserUuid());
		return MdekCatalogUtils.extractCatalogFromResponse(response);
	}

	public void exportFreeAddresses() {
		IngridDocument response = exportAddressBranch(null, false, AddressArea.ALL_FREE_ADDRESSES);
		MdekCatalogUtils.extractJobInfoFromResponse(response);
	}

	public void exportTopAddresses(boolean exportChildren) {
		IngridDocument response = exportAddressBranch(null, exportChildren, AddressArea.ALL_ADDRESSES);
		MdekCatalogUtils.extractJobInfoFromResponse(response);
	}

	public void exportAddressBranch(String rootUuid, boolean exportChildren) {
		IngridDocument response = exportAddressBranch(rootUuid, exportChildren, null);
		MdekCatalogUtils.extractJobInfoFromResponse(response);
	}

	private IngridDocument exportAddressBranch(String rootUuid, boolean exportChildren, AddressArea addressArea) {
		return mdekCallerCatalog.exportAddressBranch(
				connectionFacade.getCurrentPlugId(),
				rootUuid, !exportChildren,
				addressArea, HTTPSessionHelper.getCurrentUserUuid());
	}

	public void exportObjectBranch(String rootUuid, boolean exportChildren) {
		IngridDocument response = mdekCallerCatalog.exportObjectBranch(
				connectionFacade.getCurrentPlugId(),
				rootUuid,
				!exportChildren, HTTPSessionHelper.getCurrentUserUuid());

		MdekCatalogUtils.extractJobInfoFromResponse(response);
	}

	public void exportObjectsWithCriteria(String exportCriteria) {
		IngridDocument response = mdekCallerCatalog.exportObjects(
				connectionFacade.getCurrentPlugId(),
				exportCriteria,
				HTTPSessionHelper.getCurrentUserUuid());
		MdekCatalogUtils.extractJobInfoFromResponse(response);
	}

	public JobInfoBean getExportInfo(boolean includeExportData) {
		IngridDocument response = mdekCallerCatalog.getExportInfo(connectionFacade.getCurrentPlugId(), includeExportData, HTTPSessionHelper.getCurrentUserUuid());
		return MdekCatalogUtils.extractJobInfoFromResponse(response);
	}

	public void importEntities(UserData currentUser, byte[] importData, String targetObjectUuid, String targetAddressUuid,
			boolean publishImmediately, boolean doSeparateImport) {
		IngridDocument response = mdekCallerCatalog.importEntities(
				currentUser.getPlugId(),
				importData,
				targetObjectUuid, targetAddressUuid,
				publishImmediately, doSeparateImport,
				currentUser.getAddressUuid());
		MdekCatalogUtils.extractJobInfoFromResponse(response);
	}

	public JobInfoBean getImportInfo() {
		IngridDocument response = mdekCallerCatalog.getImportInfo(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentUserUuid());
		return MdekCatalogUtils.extractJobInfoFromResponse(response);
	}

	public void cancelRunningJob() {
		mdekCallerCatalog.cancelRunningJob(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentUserUuid());
	}

	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}


	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
}
