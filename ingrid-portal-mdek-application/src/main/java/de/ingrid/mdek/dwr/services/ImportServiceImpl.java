package de.ingrid.mdek.dwr.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.ProtocolInfoBean;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.handler.ProtocolHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.mapping.DataMapperFactory;
import de.ingrid.mdek.mapping.ProtocolFactory;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;

public class ImportServiceImpl {

	private final static Logger log = Logger.getLogger(ImportServiceImpl.class);	
	private static final int buffer = 4096;
	
	private enum FileType { GZIP, ZIP, XML, UNKNOWN }

	// Injected by Spring
	private CatalogRequestHandler catalogRequestHandler;
	private DataMapperFactory dataMapperFactory;
	private ProtocolFactory protocolFactory;
	
	private static Map<String,ProtocolInfoBean> controlMap = new ConcurrentHashMap<String, ProtocolInfoBean>();
	
	public void importEntities(FileTransfer fileTransfer, String fileType, String targetObjectUuid, String targetAddressUuid,
			boolean publishImmediately, boolean doSeparateImport) {

		// Define bean for protocol
		ProtocolInfoBean protocolBean = new ProtocolInfoBean();
		setProtocolStatus(protocolBean, false);
		setProtocolInputType(protocolBean, fileType);
		
		//Start protocol
		ProtocolHandler protocolHandler = protocolFactory.getProtocolHandler();
		setProtocolHandler(protocolBean, protocolHandler);
		controlMap.get(getCurrentUserId()).getProtocolHandler().startProtocol();
		
		// Initiate a list of bytes for each file
		ArrayList<byte[]> importData = new ArrayList<byte[]>();
		try {
			// map source file into icg-target format
			InputStream preparedImportData;
			
			switch (getFileType(fileTransfer.getMimeType())) {
			case GZIP:
				File dirGZIP = createFileDirectory("gzip");
				String rootDirGZIP = dirGZIP.getAbsolutePath();
				
				preparedImportData = fileTransfer.getInputStream();
				
				// Create tmp file for GZIP 
				File tmpFile = createTemporaryFile(preparedImportData, dirGZIP); 
				
				// Save GZIP file on temp directory
				FileInputStream gzipInputStream = new FileInputStream(tmpFile);
				File fileGZIP = createFilebyInputStream(gzipInputStream, rootDirGZIP + "\\" + fileTransfer.getFilename());
				gzipInputStream.close();
				
				// Extract GZIP file
				FileInputStream inXML = new FileInputStream(fileGZIP);
			    GZIPInputStream gzipInXML = new GZIPInputStream(inXML);
			    File outXML = createFilebyInputStream(gzipInXML, rootDirGZIP + "\\" + fileTransfer.getFilename()+".xml");
			    gzipInXML.close();
				
			    // Import file data
			    importData.add(compress(importXMLData(new FileInputStream(outXML), outXML.getName(),fileType)).toByteArray());
			    deleteUserImportDirectory(dirGZIP);
			    break;

			case ZIP:
				preparedImportData = fileTransfer.getInputStream();
				File dirZIP = createFileDirectory("zip");
				String rootDirZIP = dirZIP.getAbsolutePath();

				ZipInputStream zipIn = new ZipInputStream(preparedImportData);
				while (true){
					ZipEntry entry = zipIn.getNextEntry();
					if (entry == null){
						break;
					}
					File file = createFilebyInputStream(zipIn, rootDirZIP + "\\" + entry.getName());
					zipIn.closeEntry();
					
					importData.add(compress(importXMLData(new FileInputStream(file),file.getName(), fileType)).toByteArray());
				}
				zipIn.close();
				deleteUserImportDirectory(dirZIP);
				break;

			case UNKNOWN:
				log.debug("Unknown file type. Assuming uncompressed xml data.");
				// Fall through
			case XML:
				preparedImportData = importXMLData(fileTransfer.getInputStream(), fileTransfer.getFilename(), fileType);
				importData.add(compress(preparedImportData).toByteArray());
				break;

			default:
				throw new IllegalArgumentException("Error checking input file type. Supported types: GZIP, ZIP, XML");
			}
			
			if(controlMap.get(getCurrentUserId()).getProtocolHandler().getProtocol() != null){
				setProtocolMessage(protocolBean, controlMap.get(getCurrentUserId()).getProtocolHandler().getProtocol());
			}
		} catch (IOException ex) {
			log.error("Error creating input data.", ex);
		} catch (MdekException ex) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while starting import job.", ex);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(ex));
		}finally{
			setProtocolImportData(protocolBean, importData);
			setProtocolStatus(protocolBean, true);
		}
		
	}

	public void startImportThread(FileTransfer fileTransfer, String fileType, String targetObjectUuid, String targetAddressUuid,
			boolean publishImmediately, boolean doSeparateImport){
		// Start the import process in a separate thread
		// After the thread is started, we wait on it for three seconds and check if it has finished afterwards
		// If the thread ended with an exception (probably because another job is already running),
		// we throw a new MdekException to notify the user
		UserData currentUser = MdekSecurityUtils.getCurrentPortalUserData();
		
		ImportEntitiesThread importThread;
		importThread = new ImportEntitiesThread(catalogRequestHandler, currentUser, controlMap.get(getCurrentUserId()).getImportData(), fileType, targetObjectUuid, targetAddressUuid, publishImmediately, doSeparateImport);
		importThread.start();
		try {
			importThread.join(3000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		if (!importThread.isAlive() && importThread.getException() != null) {
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(importThread.getException()));
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

	private InputStream importXMLData (InputStream inputFileStream, String inputFileName, String inputFileType) throws FileNotFoundException, IOException{
		if(inputFileType.equals("igc")){
			return inputFileStream;
		}else{
			controlMap.get(getCurrentUserId()).getProtocolHandler().setCurrentFilename(inputFileName);
			return dataMapperFactory.getMapper(inputFileType).convert(inputFileStream, controlMap.get(getCurrentUserId()).getProtocolHandler());
		}
	}
	
	private File createFileDirectory(String dirname){
		File dirGZIP = new File(System.getProperty("java.io.tmpdir") + "\\ingrid-ige\\import\\" + dirname + "\\" + getCurrentUserId());
		dirGZIP.mkdirs();
		
		return dirGZIP;
	}
	
	private void deleteUserImportDirectory(File file){
		File[] listFiles = file.listFiles();
		for (int i=0; i < listFiles.length; i++){
			if(listFiles[i].listFiles() != null && listFiles[i].listFiles().length > 1){
				deleteUserImportDirectory(listFiles[i]);
			}else{
				listFiles[i].delete();	
			}
		}
		file.delete();
	}
	
	private File createFilebyInputStream(InputStream input, String filePath) throws IOException{
		File file = new File(filePath);
		FileOutputStream out = new FileOutputStream(file);
		int len;
		byte[] buf = new byte[buffer];
		while ((len = input.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		
		return file;
	}

	private File createTemporaryFile(InputStream inputStream, File dir) throws IOException{
		File tmpFile = File.createTempFile(getCurrentUserId(), ".txt", dir);
		OutputStream src = new FileOutputStream(tmpFile);
		src.write(createByteArrayFromInputStream(inputStream));
		src.close();
		
		return tmpFile;
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

	public void setProtocolFactory(ProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	
	public void cancelRunningJob() {
		catalogRequestHandler.cancelRunningJob();
	}
	
	public void setDataMapperFactory(DataMapperFactory mapperFactory) {
		this.dataMapperFactory = mapperFactory;
	}

	public ProtocolInfoBean getControlMap() {
		return controlMap.get(getCurrentUserId());
	}
	
	private String getCurrentUserId(){
		UserData user = MdekSecurityUtils.getCurrentPortalUserData();
		return user.getAddressUuid();
	}
	
	private void setProtocolInputType(ProtocolInfoBean protocolBean, String type) {
		protocolBean.setInputType(type);
		controlMap.put(getCurrentUserId(), protocolBean);
	}

	private void setProtocolStatus(ProtocolInfoBean protocolBean, boolean status) {
		protocolBean.setStatus(status);
		controlMap.put(getCurrentUserId(), protocolBean);
	}
		
	private void setProtocolMessage(ProtocolInfoBean protocolBean, String protocol) {
		protocolBean.setProtocol(protocol);
		controlMap.put(getCurrentUserId(), protocolBean);
	}
	
	private void setProtocolImportData(ProtocolInfoBean protocolBean, ArrayList <byte[]> importData) {
		protocolBean.setImportData(importData);
		controlMap.put(getCurrentUserId(), protocolBean);
	}
	
	private void setProtocolHandler(ProtocolInfoBean protocolBean, ProtocolHandler protocolHandler) {
		protocolBean.setProtocolHandler(protocolHandler);
		controlMap.put(getCurrentUserId(), protocolBean);
	}
}

// Helper thread which starts an import process
class ImportEntitiesThread extends Thread {

	private final static Logger log = Logger.getLogger(ImportEntitiesThread.class);	

	private final CatalogRequestHandler catalogRequestHandler;
	private final UserData currentUser;
	private final ArrayList<byte[]> importData;
	private final String targetObjectUuid;
	private final String targetAddressUuid;
	private final boolean publishImmediately;
	private final boolean doSeparateImport;
	private final String fileDataType;

	private volatile MdekException exception;
	
	public ImportEntitiesThread(CatalogRequestHandler catalogRequestHandler, UserData currentUser, ArrayList<byte[]> importData, String fileDataType, String targetObjectUuid, String targetAddressUuid, boolean publishImmediately, boolean doSeparateImport) {
		super();
		this.catalogRequestHandler = catalogRequestHandler;
		this.currentUser = currentUser;
		this.importData = importData;
		this.targetObjectUuid = targetObjectUuid;
		this.targetAddressUuid = targetAddressUuid;
		this.publishImmediately = publishImmediately;
		this.doSeparateImport = doSeparateImport;
		this.fileDataType = fileDataType;
	}

	@Override
	public void run() {
		try {
			catalogRequestHandler.importEntities(currentUser, importData, targetObjectUuid, targetAddressUuid, publishImmediately, doSeparateImport);
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