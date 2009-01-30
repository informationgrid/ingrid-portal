package de.ingrid.mdek.dwr.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.quartz.MdekJobHandler;
import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class CatalogManagementServiceImpl {

	private final static Logger log = Logger
			.getLogger(CatalogManagementServiceImpl.class);

	private MdekJobHandler mdekJobHandler;
	private ConnectionFacade connectionFacade;

	public void startUrlValidatorJob() {
		mdekJobHandler.startUrlValidatorJob();
	}

	public void stopUrlValidatorJob() {
		mdekJobHandler.stopUrlValidatorJob();
	}

	public JobInfoBean getUrlValidatorJobInfo() {
		return mdekJobHandler.getUrlValidatorJobInfo();
	}

	public List<URLObjectReference> getUrlValidatorJobResult() {
		return mdekJobHandler.getUrlValidatorJobResult();
	}

	public List<MdekDataBean> getDuplicateObjects() {
		// TODO Use function from backend when it's done
		IMdekCallerQuery mdekCallerQuery = connectionFacade
				.getMdekCallerQuery();

		String qString = "select obj.objUuid, obj.objClass, obj.objName, obj.objDescr "
			+ "from ObjectNode oNode "
				+ "inner join oNode.t01ObjectPublished obj "
			+ "where oNode.objIdPublished = oNode.objId "
			+ "order by obj.objName";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(
				connectionFacade.getCurrentPlugId(), qString, null, "");
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		List<MdekDataBean> resultList = new ArrayList<MdekDataBean>();
		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				for (IngridDocument objEntity : objs) {
					if (isDuplicate(objEntity, objs)) {
						MdekDataBean obj = new MdekDataBean();
						obj.setUuid(objEntity.getString("obj.objUuid"));
						obj.setObjectClass(objEntity.getInt("obj.objClass"));
						obj.setTitle(objEntity.getString("obj.objName"));
						obj.setGeneralDescription(objEntity.getString("obj.objDescr"));
						resultList.add(obj);
					}
				}
			}
		}
		return resultList;
	}

	private static boolean isDuplicate(IngridDocument objEntity, List<IngridDocument> list) {
		String objName = objEntity.getString("obj.objName");
		int count = 0;
		for (IngridDocument item : list) {
			if (item.getString("obj.objName").equals(objName))
				count++;
			if (count > 1) {
				return true;
			}
		}
		return false;
	}

	public void setMdekJobHandler(MdekJobHandler mdekJobHandler) {
		this.mdekJobHandler = mdekJobHandler;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

}