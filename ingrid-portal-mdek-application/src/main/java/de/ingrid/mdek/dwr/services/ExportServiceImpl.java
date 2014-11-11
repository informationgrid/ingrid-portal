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

import java.util.Date;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.MdekUtils;
import de.ingrid.mdek.beans.ExportJobInfoBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekErrorUtils;

public class ExportServiceImpl {

	private final static Logger log = Logger.getLogger(ExportServiceImpl.class);	

	// Injected by Spring
	private CatalogRequestHandler catalogRequestHandler;

	public void setCatalogRequestHandler(CatalogRequestHandler catalogRequestHandler) {
		this.catalogRequestHandler = catalogRequestHandler;
	}

	public void exportObjectBranch(String rootUuid, boolean exportChildren, boolean includeWorkingCopies) {
		try {
			catalogRequestHandler.exportObjectBranch(rootUuid, exportChildren, includeWorkingCopies);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while starting export job.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public void exportObjectsWithCriteria(String exportCriteria, boolean includeWorkingCopies) {
		try {
			catalogRequestHandler.exportObjectsWithCriteria(exportCriteria, includeWorkingCopies);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while starting export job.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public void exportAddressBranch(String rootUuid, boolean exportChildren, boolean includeWorkingCopies) {
		try {
			catalogRequestHandler.exportAddressBranch(rootUuid, exportChildren, includeWorkingCopies);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while starting export job.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public void exportFreeAddresses(boolean includeWorkingCopies) {
		try {
			catalogRequestHandler.exportFreeAddresses(includeWorkingCopies);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while starting export job.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public void exportTopAddresses(boolean exportChildren, boolean includeWorkingCopies) {
		try {
			catalogRequestHandler.exportTopAddresses(exportChildren, includeWorkingCopies);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while starting export job.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public JobInfoBean getExportInfo(boolean includeExportData) {
		return catalogRequestHandler.getExportInfo(includeExportData);
	}

	public FileTransfer getLastExportFile() {
		ExportJobInfoBean exportInfo = catalogRequestHandler.getExportInfo(true);
		return new FileTransfer(createFilename(exportInfo), "x-gzip", exportInfo.getResult());
	}

	private static String createFilename(JobInfoBean jobInfo) {
		Date startTime = jobInfo.getStartTime();
		String timestamp = MdekUtils.dateToTimestamp(startTime);
		return (timestamp != null) ? "export"+timestamp+".xml.gz" : "export.xml.gz";
	}

	public void cancelRunningJob() {
		catalogRequestHandler.cancelRunningJob();
	}
}
