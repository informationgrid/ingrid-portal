package de.ingrid.mdek.dwr.services;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.MdekUtils.MdekSysList;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.beans.ProfileBean;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.profile.ProfileMapper;
import de.ingrid.mdek.util.MdekErrorUtils;

public class CatalogServiceImpl implements CatalogService {

	private final static Logger log = Logger.getLogger(CatalogServiceImpl.class);	

	// Injected by Spring
	private CatalogRequestHandler catalogRequestHandler;

	
	public Integer[] getAllSysListIds() {
		return catalogRequestHandler.getAllSysListIds();
	}

	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String language) {
		return catalogRequestHandler.getSysLists(listIds, language);
	}

	public String[] getFreeListEntries(MdekSysList sysList) {
		return catalogRequestHandler.getFreeListEntries(sysList);
	}

	public void storeSysList(Integer listId, boolean maintainable, Integer defaultEntryIndex, Integer[] entryIds,
			String[] entriesGerman, String[] entriesEnglish) {
		catalogRequestHandler.storeSysList(listId, maintainable, defaultEntryIndex, entryIds, entriesGerman, entriesEnglish);
	}

	public void replaceFreeEntryWithSysListEntry(String freeEntry, MdekSysList sysList, Integer sysListEntryId, String sysListEntryName) {
		catalogRequestHandler.replaceFreeEntryWithSysListEntry(freeEntry, sysList, sysListEntryId, sysListEntryName);
	}

	public FileTransfer exportSysLists(Integer[] listIds) throws UnsupportedEncodingException {
		String xmlDoc = catalogRequestHandler.exportSysLists(listIds);
		return new FileTransfer("sysLists.xml", "text/xml", xmlDoc.getBytes("UTF-8"));
	}

	public void importSysLists(byte[] data) throws UnsupportedEncodingException {
		catalogRequestHandler.importSysLists(new String(data, "UTF-8"));
	}

	/*
	public List<Map<String, String>> getSysGuis(String[] guiIds) {
		return catalogRequestHandler.getSysGuis(guiIds);
	}

	public List<Map<String, String>> storeSysGuis(List<Map<String, String>> sysGuis, boolean refetchAfterStore) {
		try {
			return catalogRequestHandler.storeSysGuis(sysGuis, refetchAfterStore);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while storing sysGui data.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}*/

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
