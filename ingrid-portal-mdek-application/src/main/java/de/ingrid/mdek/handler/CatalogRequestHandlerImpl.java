package de.ingrid.mdek.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.AdditionalFieldBean;
import de.ingrid.mdek.beans.AnalyzeJobInfoBean;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.CodeListJobInfoBean;
import de.ingrid.mdek.beans.ExportJobInfoBean;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCaller.AddressArea;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
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
		IngridDocument response = mdekCallerCatalog.getSysLists(connectionFacade.getCurrentPlugId(), listIds, languageCode, MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractSysListFromResponse(response);
	}

	public List<Map<String, String>> getSysGuis(String[] guiIds) {
		IngridDocument response = mdekCallerCatalog.getSysGuis(connectionFacade.getCurrentPlugId(), guiIds, MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractSysGuisFromResponse(response);
	}

	public List<Map<String, String>> storeSysGuis(List<Map<String, String>> sysGuis, boolean refetchAfterStore) {
		List<IngridDocument> sysGuiDoc = MdekCatalogUtils.convertFromSysGuiRepresentation(sysGuis);

		IngridDocument response = mdekCallerCatalog.storeSysGuis(connectionFacade.getCurrentPlugId(), sysGuiDoc, refetchAfterStore, MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractSysGuisFromResponse(response);
	}

	public List<AdditionalFieldBean> getSysAdditionalFields(Long[] fieldIds, String language) {
		IngridDocument response = mdekCallerCatalog.getSysAdditionalFields(connectionFacade.getCurrentPlugId(), fieldIds, language, MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractSysAdditionalFieldsFromResponse(response);
	}

	public List<GenericValueBean> getSysGenericValues(String[] keyNames) {
		IngridDocument response = mdekCallerCatalog.getSysGenericKeys(connectionFacade.getCurrentPlugId(), keyNames, MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractSysGenericKeysFromResponse(response);
	}

	public void storeSysGenericValues(List<GenericValueBean> genericValues) {
		if (genericValues != null) {
			List<String> keyNames = new ArrayList<String>();
			List<String> keyValues = new ArrayList<String>();
	
			for (GenericValueBean genericValue : genericValues) {
				keyNames.add(genericValue.getKey());
				keyValues.add(genericValue.getValue());
			}
	
			IngridDocument response = mdekCallerCatalog.storeSysGenericKeys(
					connectionFacade.getCurrentPlugId(),
					keyNames.toArray(new String[]{}),
					keyValues.toArray(new String[]{}),
					MdekSecurityUtils.getCurrentUserUuid());
		}
	}

	public CatalogBean getCatalogData() {
		IngridDocument response = mdekCallerCatalog.fetchCatalog(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractCatalogFromResponse(response);
	}

	public CatalogBean storeCatalogData(CatalogBean cat) {
		IngridDocument catDoc = (IngridDocument) MdekCatalogUtils.convertFromCatalogRepresentation(cat);

		log.debug("Sending the following catalog for storage:");
		log.debug(catDoc);

		IngridDocument response = mdekCallerCatalog.storeCatalog(connectionFacade.getCurrentPlugId(), catDoc, true, MdekSecurityUtils.getCurrentUserUuid());
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
				addressArea, MdekSecurityUtils.getCurrentUserUuid());
	}

	public void exportObjectBranch(String rootUuid, boolean exportChildren) {
		IngridDocument response = mdekCallerCatalog.exportObjectBranch(
				connectionFacade.getCurrentPlugId(),
				rootUuid,
				!exportChildren, MdekSecurityUtils.getCurrentUserUuid());

		MdekCatalogUtils.extractJobInfoFromResponse(response);
	}

	public void exportObjectsWithCriteria(String exportCriteria) {
		IngridDocument response = mdekCallerCatalog.exportObjects(
				connectionFacade.getCurrentPlugId(),
				exportCriteria,
				MdekSecurityUtils.getCurrentUserUuid());
		MdekCatalogUtils.extractJobInfoFromResponse(response);
	}

	public ExportJobInfoBean getExportInfo(boolean includeExportData) {
		IngridDocument response = mdekCallerCatalog.getExportInfo(connectionFacade.getCurrentPlugId(), includeExportData, MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractExportJobInfoFromResponse(response);
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
		IngridDocument response = mdekCallerCatalog.getImportInfo(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractJobInfoFromResponse(response);
	}

	public void cancelRunningJob() {
		mdekCallerCatalog.cancelRunningJob(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
	}

	public AnalyzeJobInfoBean analyze() {
		IngridDocument response = mdekCallerCatalog.analyze(
				connectionFacade.getCurrentPlugId(),
				MdekSecurityUtils.getCurrentUserUuid());

		return MdekCatalogUtils.extractAnalyzeJobInfoFromResponse(response);
	}

	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}


	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
	
	public CodeListJobInfoBean getObjectsOfAuskunftAddress(String auskunftAddressUuid) {
		IngridDocument response = mdekCallerCatalog.getObjectsOfAuskunftAddress(
				connectionFacade.getCurrentPlugId(),
				auskunftAddressUuid,
				MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractCodeListInfoFromResponse(response);
	}
	
	public CodeListJobInfoBean getObjectsOfResponsibleUser(String responsibleUserUuid) {
		IngridDocument response = mdekCallerCatalog.getObjectsOfResponsibleUser(
				connectionFacade.getCurrentPlugId(),
				responsibleUserUuid,
				MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractCodeListInfoFromResponse(response);
	}
	
	public CodeListJobInfoBean getAddressesOfResponsibleUser(String responsibleUserUuid) {
		IngridDocument response = mdekCallerCatalog.getAddressesOfResponsibleUser(
				connectionFacade.getCurrentPlugId(),
				responsibleUserUuid,
				MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractCodeListInfoFromResponse(response);
	}
}
