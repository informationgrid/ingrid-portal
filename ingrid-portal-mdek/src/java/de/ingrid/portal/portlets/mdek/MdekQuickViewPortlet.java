package de.ingrid.portal.portlets.mdek;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import de.ingrid.mdek.EnumUtil;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekKeysSecurity;
import de.ingrid.mdek.MdekUtils.AddressType;
import de.ingrid.mdek.MdekUtils.IdcEntityOrderBy;
import de.ingrid.mdek.MdekUtils.IdcStatisticsSelectionType;
import de.ingrid.mdek.MdekUtils.IdcWorkEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.ObjectType;
import de.ingrid.mdek.MdekUtils.UserOperation;
import de.ingrid.mdek.MdekUtilsSecurity.IdcRole;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerObject;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.MdekCallerAddress;
import de.ingrid.mdek.caller.MdekCallerCatalog;
import de.ingrid.mdek.caller.MdekCallerObject;
import de.ingrid.mdek.caller.MdekCallerSecurity;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.util.MdekAddressUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.utils.IngridDocument;

public class MdekQuickViewPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(MdekQuickViewPortlet.class);

    // VIEW TEMPLATES
    private final static String VIEW_TEMPLATE = "/WEB-INF/templates/mdek/mdek_quick_view.vm";

    // Parameters set on init
    private RoleManager roleManager;
    private UserManager userManager;
    private IMdekCallerCatalog mdekCallerCatalog;
    private IMdekCallerObject mdekCallerObject;
    private IMdekCallerAddress mdekCallerAddress;
    private IMdekCallerSecurity mdekCallerSecurity;

    // maximum number of datasets displayed per table
    private static final int MAX_NUM_DISPLAYED_DATASETS = 20;


    public void init(PortletConfig config) throws PortletException {
		super.init(config);

		mdekCallerCatalog = MdekCallerCatalog.getInstance();
		mdekCallerObject = MdekCallerObject.getInstance();
		mdekCallerAddress = MdekCallerAddress.getInstance();
		mdekCallerSecurity = MdekCallerSecurity.getInstance();

		userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        roleManager = (RoleManager) getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager) {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
    throws PortletException, IOException {
    	setDefaultViewPage(VIEW_TEMPLATE);
    	Context context = getContext(request);
    	ResourceBundle resourceBundle = getPortletConfig().getResourceBundle(request.getLocale());
    	context.put("MESSAGES", resourceBundle);

    	UserData userData = getUserData(request);
    	try {
    		IdcRole role = getRole(userData);
    		context.put("userRole", role.name());

    		Catalog cat = fetchCatalog(userData);
	    	context.put("catalogName", cat.getName());
	    	context.put("catalogLocation", cat.getLocation());
	    	context.put("numObjects", fetchNumObjects(userData));
	    	context.put("numAddresses", fetchNumAddresses(userData));
	    	context.put("numUsers", fetchNumUsers(userData));

	    	TableData objectTableData = fetchObjectQuicklist(userData);
	    	context.put("objectList", objectTableData.getEntries());
	    	context.put("totalNumObjects", objectTableData.getTotalNumEntries());

	    	TableData addressTableData = fetchAddressQuicklist(userData);
	    	context.put("addressList", addressTableData.getEntries());
	    	context.put("totalNumAddresses", addressTableData.getTotalNumEntries());

    	} catch (Exception e) {
			throw new PortletException ("Error fetching values from the iPlug with id '"+userData.getPlugId()+"'", e);
    	}

    	super.doView(request, response);
    }


    public void processAction(ActionRequest request, ActionResponse actionResponse) {
    }


    private static UserData getUserData(javax.portlet.RenderRequest request) {
    	String userName = request.getRemoteUser();
    	Session session = HibernateUtil.currentSession();
    	session.beginTransaction();
    	UserData userData = (UserData) session.createCriteria(UserData.class).add(Restrictions.eq("portalLogin", userName)).uniqueResult();
    	HibernateUtil.closeSession();
    	return userData;
    }

    private Catalog fetchCatalog(UserData userData) {
    	Catalog catalog = new Catalog();
    	IngridDocument response = mdekCallerCatalog.fetchCatalog(userData.getPlugId(), userData.getAddressUuid());
    	IngridDocument catalogDoc = MdekUtils.getResultFromResponse(response);

    	if (catalogDoc != null) {
        	catalog.setName(catalogDoc.getString(MdekKeys.CATALOG_NAME));
        	IngridDocument locationDoc = (IngridDocument) catalogDoc.get(MdekKeys.CATALOG_LOCATION);
        	if (locationDoc != null) {
        		catalog.setLocation(locationDoc.getString(MdekKeys.LOCATION_NAME));
        	}
    	}

    	return catalog;
    }

    private long fetchNumObjects(UserData userData) {
    	long totalNumObjects = 0;
    	IngridDocument response = mdekCallerObject.getObjectStatistics(userData.getPlugId(),
    			null, IdcStatisticsSelectionType.CLASSES_AND_STATES,
    			0, 0, userData.getAddressUuid());
    	IngridDocument statisticsDoc = MdekUtils.getResultFromResponse(response);

    	// Count the total number of objects by iterating over all 'objClass' entries
    	Object[] objClasses = EnumUtil.getDbValues(ObjectType.class);
		for (Object objClass : objClasses) {
			IngridDocument classMap = (IngridDocument) statisticsDoc.get(objClass);
			totalNumObjects += (classMap.get(MdekKeys.TOTAL_NUM) != null) ? (Long) classMap.get(MdekKeys.TOTAL_NUM) : 0;
		}

    	return totalNumObjects;
    }

    private long fetchNumAddresses(UserData userData) {
    	long totalNumAddresses = 0;
    	IngridDocument response = mdekCallerAddress.getAddressStatistics(userData.getPlugId(),
    			null, false, IdcStatisticsSelectionType.CLASSES_AND_STATES,
    			0, 0, userData.getAddressUuid());
    	IngridDocument statisticsDoc = MdekUtils.getResultFromResponse(response);

    	// Count the total number of addresses by iterating over all 'adrClass' entries
		Object[] adrClasses = EnumUtil.getDbValues(AddressType.class);
		for (Object adrClass : adrClasses) {
			IngridDocument classMap = (IngridDocument) statisticsDoc.get(adrClass);
			totalNumAddresses += (classMap.get(MdekKeys.TOTAL_NUM) != null) ? (Long) classMap.get(MdekKeys.TOTAL_NUM) : 0;
		}

    	return totalNumAddresses;
    }

    private int fetchNumUsers(UserData userData) {
    	Session session = HibernateUtil.currentSession();
    	session.beginTransaction();
//    	session.createCriteria(UserData.class).add(Restrictions.eq("portalLogin", userData.getPortalLogin())).
    	Integer numUsers = (Integer) session.createCriteria(UserData.class)
    		.add(Restrictions.eq("plugId", userData.getPlugId()))
    		.setProjection(Projections.rowCount()).uniqueResult();
    	HibernateUtil.closeSession();
    	return (numUsers != null)? numUsers : 0;
    }

    private TableData fetchAddressQuicklist(UserData userData) {
    	TableData tableData = new TableData();
    	List<HashMap<String, String>> entries = new ArrayList<HashMap<String,String>>();

    	IngridDocument response = mdekCallerAddress.getWorkAddresses(userData.getPlugId(),
    			IdcWorkEntitiesSelectionType.MODIFIED, IdcEntityOrderBy.DATE, false,
    			0, MAX_NUM_DISPLAYED_DATASETS, userData.getAddressUuid());
    	IngridDocument resultDoc = MdekUtils.getResultFromResponse(response);

    	if (resultDoc != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) resultDoc.get(MdekKeys.ADR_ENTITIES);
			if (adrs != null) {
				for (IngridDocument adrEntity : adrs) {
					HashMap<String, String> entry = new HashMap<String, String>();
					entry.put("udkClass", ((Integer) adrEntity.get(MdekKeys.CLASS)).toString());
					entry.put("uuid", adrEntity.getString(MdekKeys.UUID));
					entry.put("modDate", de.ingrid.mdek.MdekUtils.timestampToDisplayDate(adrEntity.getString(MdekKeys.DATE_OF_LAST_MODIFICATION)));
					entry.put("name", MdekAddressUtils.createAddressTitle(
							adrEntity.getString(MdekKeys.ORGANISATION),
							adrEntity.getString(MdekKeys.NAME),
							adrEntity.getString(MdekKeys.GIVEN_NAME)));

					UserOperation u = (UserOperation) adrEntity.get(MdekKeys.RESULTINFO_USER_OPERATION);
					if (u != null) {
						entry.put("status", u.toString());
					}
					entries.add(entry);
				}

				if (resultDoc.get(MdekKeys.TOTAL_NUM_PAGING) != null) {
					tableData.setTotalNumEntries((Long) resultDoc.get(MdekKeys.TOTAL_NUM_PAGING));

				} else {
			    	tableData.setTotalNumEntries(0);
				}
			}
		}

    	tableData.setEntries(entries);
    	return tableData;
    }

    private TableData fetchObjectQuicklist(UserData userData) {
    	TableData tableData = new TableData();
    	List<HashMap<String, String>> entries = new ArrayList<HashMap<String,String>>();

    	IngridDocument response = mdekCallerObject.getWorkObjects(userData.getPlugId(),
    			IdcWorkEntitiesSelectionType.MODIFIED, IdcEntityOrderBy.DATE, false,
    			0, MAX_NUM_DISPLAYED_DATASETS, userData.getAddressUuid());
    	IngridDocument resultDoc = MdekUtils.getResultFromResponse(response);

    	if (resultDoc != null) {
			List<IngridDocument> objs = (List<IngridDocument>) resultDoc.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				for (IngridDocument objEntity : objs) {
					HashMap<String, String> entry = new HashMap<String, String>();
					entry.put("udkClass", ((Integer) objEntity.get(MdekKeys.CLASS)).toString());
					entry.put("uuid", objEntity.getString(MdekKeys.UUID));
					entry.put("modDate", de.ingrid.mdek.MdekUtils.timestampToDisplayDate(objEntity.getString(MdekKeys.DATE_OF_LAST_MODIFICATION)));
					entry.put("name", objEntity.getString(MdekKeys.TITLE));

					UserOperation u = (UserOperation) objEntity.get(MdekKeys.RESULTINFO_USER_OPERATION);
					if (u != null) {
						entry.put("status", u.toString());
					}
					entries.add(entry);
				}

				if (resultDoc.get(MdekKeys.TOTAL_NUM_PAGING) != null) {
					tableData.setTotalNumEntries((Long) resultDoc.get(MdekKeys.TOTAL_NUM_PAGING));

				} else {
			    	tableData.setTotalNumEntries(0);
				}
			}
		}

    	tableData.setEntries(entries);
    	return tableData;
    }

    private IdcRole getRole(UserData userData) throws PortletException, SecurityException {
    	if (!roleManager.isUserInRole(userData.getPortalLogin(), "mdek")) {
    		throw new PortletException("User with name "+userData.getPortalLogin()+" has to be associated with role mdek.");
    	}

    	// Check for the idcRole of the user
    	IngridDocument response = null;
    	try {
    		response = mdekCallerSecurity.getUserDetails(userData.getPlugId(), userData.getAddressUuid(), userData.getAddressUuid());

    	} catch (Exception e) {
			throw new PortletException ("The connection to the iPlug with id '"+userData.getPlugId()+"' could not be established.", e);    		
    	}

    	IngridDocument userDoc = MdekUtils.getResultFromResponse(response);
		Integer role = (Integer) userDoc.get(MdekKeysSecurity.IDC_ROLE);
		return EnumUtil.mapDatabaseToEnumConst(IdcRole.class, role);
    }
}


// Simple Container class holding the catalog fields needed by the quickview portlet
class Catalog {
	private String name;
	private String location;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}

//Simple Container class holding the data required for the displayed tables
class TableData {
	private List<HashMap<String, String>> entries;
	private long totalNumEntries;

	public List<HashMap<String, String>> getEntries() {
		return entries;
	}
	public void setEntries(List<HashMap<String, String>> entries) {
		this.entries = entries;
	}
	public long getTotalNumEntries() {
		return totalNumEntries;
	}
	public void setTotalNumEntries(long totalNumEntries) {
		this.totalNumEntries = totalNumEntries;
	}
}