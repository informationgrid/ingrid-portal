package de.ingrid.mdek.dwr.services;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.quartz.MdekJobHandler;
import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;

public class CatalogManagementServiceImpl {

	private final static Logger log = Logger.getLogger(CatalogManagementServiceImpl.class);	

	private MdekJobHandler mdekJobHandler;

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

	public void setMdekJobHandler(MdekJobHandler mdekJobHandler) {
		this.mdekJobHandler = mdekJobHandler;
	}

}