package de.ingrid.mdek.dwr.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;
import org.directwebremoting.io.OutputStreamLoader;

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

		FileTransfer fileTransfer = new FileTransfer("test.xml", "application/xml", new SimpleOutputStreamLoader(exportInfo.getResult()));
//		FileTransfer fileTransfer = new FileTransfer("export.zip", "application/zip", exportInfo.getResult());
		return fileTransfer;
	}
}

class SimpleOutputStreamLoader implements OutputStreamLoader {

	private final byte[] content;
	
	public SimpleOutputStreamLoader(byte[] content) {
		this.content = content;
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub
	}

	public void load(OutputStream out) throws IOException {
		InputStream in = new GZIPInputStream(new ByteArrayInputStream(content));
		int c;
		while ((c = in.read()) != -1) {
			out.write(c);
		}
		in.close();
	}
}
