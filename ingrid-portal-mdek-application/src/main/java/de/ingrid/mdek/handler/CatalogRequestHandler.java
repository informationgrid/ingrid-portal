package de.ingrid.mdek.handler;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.beans.AdditionalFieldBean;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.ExportJobInfoBean;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.persistence.db.model.UserData;

public interface CatalogRequestHandler {

	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode);
	public List<Map<String, String>> getSysGuis(String[] guiIds);
	public List<AdditionalFieldBean> getSysAdditionalFields(Long[] fieldIds, String language);
	public List<Map<String, String>> storeSysGuis(List<Map<String, String>> sysGuis, boolean refetchAfterStore);
	public List<GenericValueBean> getSysGenericValues(String[] keyNames);
	public void storeSysGenericValues(List<GenericValueBean> genericValues);
	public CatalogBean getCatalogData();
	public CatalogBean storeCatalogData(CatalogBean cat);
	public void exportFreeAddresses();
	public void exportTopAddresses(boolean exportChildren);
	public void exportAddressBranch(String rootUuid, boolean exportChildren);
	public void exportObjectBranch(String rootUuid, boolean exportChildren);
	public void exportObjectsWithCriteria(String exportCriteria);
	public ExportJobInfoBean getExportInfo(boolean includeExportData);
	public void importEntities(UserData currentUser, byte[] importData, String targetObjectUuid, String targetAddressUuid, boolean publishImmediately, boolean doSeparateImport);
	public JobInfoBean getImportInfo();
	public void cancelRunningJob();
}
