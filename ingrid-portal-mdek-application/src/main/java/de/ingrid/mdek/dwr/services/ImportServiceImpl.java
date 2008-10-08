package de.ingrid.mdek.dwr.services;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

public class ImportServiceImpl {

	private final static Logger log = Logger.getLogger(ImportServiceImpl.class);	

	// Echoes the uploaded file back to the client
	public FileTransfer uploadFile(FileTransfer fileTransfer) throws IOException {
		log.debug("uploadFile called.");
		log.debug("fileName: "+fileTransfer.getName());
		log.debug("mimeType: "+fileTransfer.getMimeType());
//      return new FileTransfer(fileTransfer.getName(), fileTransfer.getMimeType(), fileTransfer.getInputStream());
		return fileTransfer;
	}
}
