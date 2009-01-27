package de.ingrid.mdek.dwr.services;

import org.apache.log4j.Logger;

import de.ingrid.mdek.quartz.MdekJobHandler;

public class CatalogManagementServiceImpl {

	private final static Logger log = Logger.getLogger(CatalogManagementServiceImpl.class);	

	private MdekJobHandler mdekJobHandler;

	public void startUrlValidatorJob() {
		mdekJobHandler.startUrlValidatorJob();
	}

	public void setMdekJobHandler(MdekJobHandler mdekJobHandler) {
		this.mdekJobHandler = mdekJobHandler;
	}

}