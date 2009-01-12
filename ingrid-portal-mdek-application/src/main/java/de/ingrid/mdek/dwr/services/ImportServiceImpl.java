package de.ingrid.mdek.dwr.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.handler.CatalogRequestHandler;

public class ImportServiceImpl {

	private final static Logger log = Logger.getLogger(ImportServiceImpl.class);	

	// Injected by Spring
	private CatalogRequestHandler catalogRequestHandler;

	public void importEntities(FileTransfer fileTransfer, String targetObjectUuid, String targetAddressUuid,
			boolean publishImmediately, boolean doSeparateImport) {

		log.debug("File transfer mime type: "+fileTransfer.getMimeType());

		try {
			byte[] gzippedData = null;
			if (fileTransfer.getMimeType().equals("application/x-gzip")) {
				gzippedData = createByteArrayFromInputStream(fileTransfer.getInputStream());

			} else if (fileTransfer.getMimeType().equals("application/zip")) {
				ZipInputStream zipIn = new ZipInputStream(fileTransfer.getInputStream());
				zipIn.getNextEntry();

				gzippedData = compress(zipIn).toByteArray();

			} else if (fileTransfer.getMimeType().equals("text/xml")) {
				gzippedData = compress(fileTransfer.getInputStream()).toByteArray();
			}

			catalogRequestHandler.importEntities(gzippedData, targetObjectUuid, targetAddressUuid, publishImmediately, doSeparateImport);

		} catch (IOException ex) {
			log.error("Error creating input data.", ex);
		}
		// TODO catch and throw mdek exception (e.g. Parent not set, ...)?
	}

	public JobInfoBean getImportInfo() {
		return catalogRequestHandler.getImportInfo();
	}

	public FileTransfer getLastImportLog() {
		JobInfoBean jobInfo = catalogRequestHandler.getImportInfo();
		String log = jobInfo.getDescription();

		return new FileTransfer("log.txt", "text/plain", log.getBytes()); 
	}

	private byte[] createByteArrayFromInputStream(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		final int BUFFER = 2048;
		int count;
		byte data[] = new byte[BUFFER];
		while((count = in.read(data, 0, BUFFER)) != -1) {
			   out.write(data, 0, count);
		}

		return out.toByteArray();
	}

	// Compress (zip) any data on InputStream and write it to a ByteArrayOutputStream
	private static ByteArrayOutputStream compress(InputStream is) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(is);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzout = new GZIPOutputStream(new BufferedOutputStream(out));

		final int BUFFER = 2048;
		int count;
		byte data[] = new byte[BUFFER];
		while((count = bin.read(data, 0, BUFFER)) != -1) {
		   gzout.write(data, 0, count);
		}

		gzout.close();
		return out;
	}

	public void setCatalogRequestHandler(CatalogRequestHandler catalogRequestHandler) {
		this.catalogRequestHandler = catalogRequestHandler;
	}

	public void cancelRunningJob() {
		catalogRequestHandler.cancelRunningJob();
	}
}
