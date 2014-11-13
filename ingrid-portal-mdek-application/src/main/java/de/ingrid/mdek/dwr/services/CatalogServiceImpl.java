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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.EnumUtil;
import de.ingrid.mdek.MdekUtils.MdekSysList;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.beans.SysList;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.utils.ige.profile.ProfileMapper;
import de.ingrid.utils.ige.profile.beans.ProfileBean;

public class CatalogServiceImpl implements CatalogService {

	private final static Logger log = Logger.getLogger(CatalogServiceImpl.class);	

	// Injected by Spring
	private CatalogRequestHandler catalogRequestHandler;

	
	public List<SysList> getAllSysListInfos() {
		return catalogRequestHandler.getAllSysListInfos();
	}

	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String language) {
		return catalogRequestHandler.getSysLists(listIds, language);
	}

	public Map<Integer, List<String[]>> getSysListsRemoveMetadata(Integer[] listIds, String language) {
		Map<Integer, List<String[]>> listMap = catalogRequestHandler.getSysLists(listIds, language);
		
		return MdekCatalogUtils.removeMetadataFromSysLists(listMap);
	}

	public String[] getFreeListEntries(Integer sysListId) {
		MdekSysList mySyslist = EnumUtil.mapDatabaseToEnumConst(MdekSysList.class, sysListId);
		return catalogRequestHandler.getFreeListEntries(mySyslist);
	}

	public void storeSysList(Integer listId, boolean maintainable, Integer defaultEntryIndex, Integer[] entryIds,
			String[] entriesGerman, String[] entriesEnglish, String[] data) {
		catalogRequestHandler.storeSysList(listId, maintainable, defaultEntryIndex, entryIds, entriesGerman, entriesEnglish, data);
	}

	public void replaceFreeEntryWithSysListEntry(String freeEntry, Integer sysListId, Integer sysListEntryId, String sysListEntryName) {
		MdekSysList mySyslist = EnumUtil.mapDatabaseToEnumConst(MdekSysList.class, sysListId);
		catalogRequestHandler.replaceFreeEntryWithSysListEntry(freeEntry, mySyslist, sysListEntryId, sysListEntryName);
	}

	public FileTransfer exportSysLists(Integer[] listIds) throws UnsupportedEncodingException {
		String xmlDoc = catalogRequestHandler.exportSysLists(listIds);
		return new FileTransfer("sysLists.xml", "text/xml", xmlDoc.getBytes("UTF-8"));
	}

	public void importSysLists(byte[] data) throws UnsupportedEncodingException {
		catalogRequestHandler.importSysLists(new String(data, "UTF-8"));

		// Reset timestamp of codelist repo so all syslists will be synchronized after manual import of syslist.
		// This way we guarantee that syslist entries are not lost via import. 
		// see https://dev.wemove.com/jira/browse/INGRID-2184
		if (log.isInfoEnabled()) {
	        log.info("Reset sys_generic_key \"lastModifiedSyslist\" to \"-1\", " +
	        	"so syslists will be reloaded from codelist repo after manual import.");
		}

        List<GenericValueBean> genericValues = new ArrayList<GenericValueBean>();
        GenericValueBean valueBean = new GenericValueBean();
        valueBean.setKey("lastModifiedSyslist");
        valueBean.setValue("-1");
        genericValues.add(valueBean);
        storeSysGenericValues(genericValues);
	}

	public List<GenericValueBean> getSysGenericValues(String[] keyNames) {
	    return getSysGenericValues(keyNames, null);
	}
	
	public List<GenericValueBean> getSysGenericValues(String[] keyNames, HttpServletRequest req) {
		try {
		    if (req == null)
		        return catalogRequestHandler.getSysGenericValues(keyNames);
		    else
		        return catalogRequestHandler.getSysGenericValues(keyNames, req);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching sysGenericKeys data.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public void storeSysGenericValues(List<GenericValueBean> genericValues) {
		try {
			catalogRequestHandler.storeSysGenericValues(genericValues);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while storing sysGenericKeys data.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public CatalogBean getCatalogData() {
		return catalogRequestHandler.getCatalogData();	
	}

	public CatalogBean storeCatalogData(CatalogBean cat) {
		try {
			return catalogRequestHandler.storeCatalogData(cat);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while storing catalog data.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public CatalogRequestHandler getCatalogRequestHandler() {
		return catalogRequestHandler;
	}

	public void setCatalogRequestHandler(CatalogRequestHandler catalogRequestHandler) {
		this.catalogRequestHandler = catalogRequestHandler;
	}

    @Override
    public ProfileBean getProfileData() {
        return getProfileData(null);
    }
    
    public ProfileBean getProfileData(HttpServletRequest req) {
        List<GenericValueBean> data;
        
        if (req == null)
            data = getSysGenericValues(new String[]{"profileXML"});
        else
            data = getSysGenericValues(new String[]{"profileXML"}, req);
        
        if (data.size() == 0) {
            return new ProfileBean();
        }
        
        // convert data to bean
        ProfileMapper pM = new ProfileMapper();
        
        //
        //ProfileBean bean = pM.mapStreamToBean(new InputSource(getClass().getResourceAsStream("/coreProfile.xml")));
        //

        ProfileBean bean = pM.mapStringToBean(data.get(0).getValue());        

        return bean;
    }
    
    @Override
    public void saveProfileData(ProfileBean bean) {
        // get profile xml data from backend (as stream?)
        //if (log.)
        log.debug("saving profile bean: " + bean.getRubrics().size() + " rubrics");
        // convert bean to XML
        ProfileMapper pM = new ProfileMapper();
        String profileString = pM.mapBeanToXmlString(bean);
        List<GenericValueBean> data = new ArrayList<GenericValueBean>();
        GenericValueBean valueBean = new GenericValueBean();
        valueBean.setKey("profileXML");
        valueBean.setValue(profileString);
        data.add(valueBean);
        storeSysGenericValues(data);
    }
}
