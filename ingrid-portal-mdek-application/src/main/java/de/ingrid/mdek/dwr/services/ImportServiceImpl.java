package de.ingrid.mdek.dwr.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekErrorUtils;

public class ImportServiceImpl {

	private final static Logger log = Logger.getLogger(ImportServiceImpl.class);	

	private enum FileType { GZIP, ZIP, XML, UNKNOWN }

	// Injected by Spring
	private CatalogRequestHandler catalogRequestHandler;

	public void importEntities(FileTransfer fileTransfer, String targetObjectUuid, String targetAddressUuid,
			boolean publishImmediately, boolean doSeparateImport) {

//		log.debug("File transfer mime type: "+fileTransfer.getMimeType());

		try {
			byte[] gzippedData = null;
			switch (getFileType(fileTransfer.getMimeType())) {
			case GZIP:
				gzippedData = createByteArrayFromInputStream(fileTransfer.getInputStream());
				break;

			case ZIP:
				ZipInputStream zipIn = new ZipInputStream(fileTransfer.getInputStream());
				zipIn.getNextEntry();
				gzippedData = compress(zipIn).toByteArray();
				break;

			case UNKNOWN:
				log.debug("Unknown file type. Assuming uncompressed xml data.");
				// Fall through
			case XML:
				gzippedData = compress(fileTransfer.getInputStream()).toByteArray();
				break;

			default:
				throw new IllegalArgumentException("Error checking input file type. Supported types: GZIP, ZIP, XML");
			}

			// Start the import process in a separate thread
			// After the thread is started, we wait on it for three seconds and check if it has finished afterwards
			// If the thread ended with an exception (probably because another job is already running),
			// we throw a new MdekException to notify the user
			ImportEntitiesThread importThread = new ImportEntitiesThread(catalogRequestHandler, gzippedData, targetObjectUuid, targetAddressUuid, publishImmediately, doSeparateImport);
			importThread.start();
			try {
				importThread.join(3000);

			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}

			if (!importThread.isAlive() && importThread.getException() != null) {
				throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(importThread.getException()));
			}

		} catch (IOException ex) {
			log.error("Error creating input data.", ex);

		} catch (MdekException ex) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while starting import job.", ex);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(ex));
		}
	}

	private FileType getFileType(String mimeType) {
		if ("application/x-gzip".equals(mimeType) || "application/gzip".equals(mimeType)) {
			return FileType.GZIP;

		} else if ("application/zip".equals(mimeType)) {
			return FileType.ZIP;

		} else if ("text/xml".equals(mimeType)) {
			return FileType.XML;

		} else {
			log.debug("Could not determine import file type from mime type: '"+mimeType+"'");
			return FileType.UNKNOWN;
		}
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

// Helper thread which starts an import process
class ImportEntitiesThread extends Thread {

	private final static Logger log = Logger.getLogger(ImportEntitiesThread.class);	

	private final CatalogRequestHandler catalogRequestHandler;
	private final byte[] importData;
	private final String targetObjectUuid;
	private final String targetAddressUuid;
	private final boolean publishImmediately;
	private final boolean doSeparateImport;

	private volatile MdekException exception;


	public ImportEntitiesThread(CatalogRequestHandler catalogRequestHandler, byte[] importData, String targetObjectUuid, String targetAddressUuid, boolean publishImmediately, boolean doSeparateImport) {
		super();
		this.catalogRequestHandler = catalogRequestHandler;
		this.importData = importData;
		this.targetObjectUuid = targetObjectUuid;
		this.targetAddressUuid = targetAddressUuid;
		this.publishImmediately = publishImmediately;
		this.doSeparateImport = doSeparateImport;
	}

	@Override
	public void run() {
		try {
			catalogRequestHandler.importEntities(importData, targetObjectUuid, targetAddressUuid, publishImmediately, doSeparateImport);

		} catch(MdekException ex) {
			log.debug("Exception while importing entities.", ex);
			setException(ex);
		}
	}

	public MdekException getException() {
		return exception;
	}

	private void setException(MdekException exception) {
		this.exception = exception;
	}
}