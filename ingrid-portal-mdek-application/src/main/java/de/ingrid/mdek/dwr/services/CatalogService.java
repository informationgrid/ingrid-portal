/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.dwr.services;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.beans.SysList;
import de.ingrid.utils.ige.profile.beans.ProfileBean;

public interface CatalogService {

	public List<SysList> getAllSysListInfos();
	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode);
	/** Removes metadata encoded in syslist values, e.g. ", yyyy-MM-dd" at end */
	public Map<Integer, List<String[]>> getSysListsRemoveMetadata(Integer[] listIds, String languageCode);
	public void storeSysList(Integer listId, boolean maintainable, Integer defaultEntryIndex, Integer[] entryIds,
			String[] entriesGerman, String[] entriesEnglish, String[] data);
	public String[] getFreeListEntries(Integer listId);
	public void replaceFreeEntryWithSysListEntry(String freeEntry, Integer listId, Integer sysListEntryId, String sysListEntryName);
	public FileTransfer exportSysLists(Integer[] listIds) throws UnsupportedEncodingException;
	public void importSysLists(byte[] data) throws UnsupportedEncodingException;

	//public List<Map<String, String>> getSysGuis(String[] guiIds);
	//public List<Map<String, String>> storeSysGuis(List<Map<String, String>> sysGuis, boolean refetchAfterStore);

	public List<GenericValueBean> getSysGenericValues(String[] keyNames);
	public void storeSysGenericValues(List<GenericValueBean> genericValues);
	
	public CatalogBean getCatalogData();
	public CatalogBean storeCatalogData(CatalogBean cat);
	
	public ProfileBean getProfileData();
	public void saveProfileData(ProfileBean bean);
}