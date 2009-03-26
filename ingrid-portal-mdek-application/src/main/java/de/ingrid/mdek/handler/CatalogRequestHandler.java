package de.ingrid.mdek.handler;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.MdekUtils.CsvRequestType;
import de.ingrid.mdek.MdekUtils.MdekSysList;
import de.ingrid.mdek.beans.AdditionalFieldBean;
import de.ingrid.mdek.beans.AnalyzeJobInfoBean;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.ExportJobInfoBean;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.utils.IngridDocument;

public interface CatalogRequestHandler {

	public Integer[] getAllSysListIds();
	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode);
	public void storeSysList(Integer listId, boolean maintainable, Integer defaultEntryIndex, Integer[] entryIds,
			String[] entriesGerman, String[] entriesEnglish);
	public String[] getFreeListEntries(MdekSysList sysList);
	public void replaceFreeEntryWithSysListEntry(String freeEntry, MdekSysList sysList, Integer sysListEntryId, String sysListEntryName);
	public String exportSysLists(Integer[] listIds);
	public void importSysLists(String xmlDoc);

	public List<AdditionalFieldBean> getSysAdditionalFields(Long[] fieldIds, String language);
	public List<AdditionalFieldBean> storeAllSysAdditionalFields(List<AdditionalFieldBean> additionalFields);

	public List<Map<String, String>> getSysGuis(String[] guiIds);
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
	
	// from CatalogManagementService
	public AnalyzeJobInfoBean analyze();
	public List<MdekDataBean> getObjectsOfAuskunftAddress(String auskunftAddressUuid, int maxNumHits);
	public List<MdekDataBean> getObjectsOfResponsibleUser(String responsibleUserUuid, int maxNumHits);
	public List<MdekAddressBean> getAddressesOfResponsibleUser(String responsibleUserUuid, int maxNumHits);
	public byte[] getCsvData(String uuid, CsvRequestType type);
	public IngridDocument replaceAddress(String oldUuid, String newUuid);

	public void rebuildSysListData();
	public JobInfoBean getRebuildJobInfo();
}