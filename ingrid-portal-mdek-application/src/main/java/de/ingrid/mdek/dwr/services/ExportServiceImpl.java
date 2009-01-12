package de.ingrid.mdek.dwr.services;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.beans.ExportInfoBean;
import de.ingrid.mdek.handler.CatalogRequestHandler;

public class ExportServiceImpl {

	private final static Logger log = Logger.getLogger(ExportServiceImpl.class);	

	// Injected by Spring
	private CatalogRequestHandler catalogRequestHandler;

	public void setCatalogRequestHandler(CatalogRequestHandler catalogRequestHandler) {
		this.catalogRequestHandler = catalogRequestHandler;
	}

	public void exportObjectBranch(String rootUuid, boolean exportChildren) {
		catalogRequestHandler.exportObjectBranch(rootUuid, exportChildren);
	}

	public void exportObjectsWithCriteria(String exportCriteria) {
		catalogRequestHandler.exportObjectsWithCriteria(exportCriteria);
	}

	public void exportAddressBranch(String rootUuid, boolean exportChildren) {
		catalogRequestHandler.exportAddressBranch(rootUuid, exportChildren);
	}

	public void exportFreeAddresses() {
		catalogRequestHandler.exportFreeAddresses();
	}

	public void exportTopAddresses(boolean exportChildren) {
		catalogRequestHandler.exportTopAddresses(exportChildren);
	}

	public ExportInfoBean getExportInfo(boolean includeExportData) {
		return catalogRequestHandler.getExportInfo(includeExportData);
	}

	public FileTransfer getLastExportFile() {
		ExportInfoBean exportInfo = catalogRequestHandler.getExportInfo(true);

		return new FileTransfer("export.xml.gz", "x-gzip", exportInfo.getResult());
	}

	public void cancelRunningJob() {
		catalogRequestHandler.cancelRunningJob();
	}
}
