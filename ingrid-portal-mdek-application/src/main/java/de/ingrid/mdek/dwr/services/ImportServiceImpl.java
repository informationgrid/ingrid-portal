/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.ProtocolInfoBean;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.job.protocol.ProtocolFactory;
import de.ingrid.mdek.job.protocol.ProtocolHandler;
import de.ingrid.mdek.job.protocol.ProtocolHandler.Type;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.utils.IngridDocument;

@Service
public class ImportServiceImpl {

	private static final Logger log = Logger.getLogger(ImportServiceImpl.class);	

	private enum FileType { GZIP, ZIP, XML, UNKNOWN }
    
    byte[] buffer = new byte[2048];
    
	// Injected by Spring
    @Autowired
	private CatalogRequestHandler catalogRequestHandler;
    
    @Autowired
	private ProtocolFactory protocolFactory;
	
	private static Map<String,ProtocolInfoBean> controlMap = new ConcurrentHashMap<>();
	
	public void analyzeImportData(FileTransfer fileTransfer, String fileType, String targetObjectUuid, String targetAddressUuid,
            boolean publishImmediately, boolean doSeparateImport, boolean copyNodeIfPresent) {

	    String userId = getCurrentUserId();
	    boolean startNewAnalysis = true;
	    List<Map<Type, List<String>>> allProtocols = new ArrayList<>();
	    
		// Define bean for protocol
		ProtocolInfoBean protocolBean = new ProtocolInfoBean(protocolFactory.getProtocolHandler());
		protocolBean.setInputType(fileType);
		controlMap.put(userId, protocolBean);
		
		//Start protocol
		protocolBean.getProtocolHandler().startProtocol();
		protocolBean.setDataProcessed(0);
		
		List<byte[]> importData = new ArrayList<>();
        try (InputStream importDataStream = fileTransfer.getInputStream()){
            // map source file into icg-target format
            
            switch (getFileType(fileTransfer.getMimeType())) {
            case GZIP:
                File dirGZIP = createFileDirectory("gzip/" + userId);
                String rootDirGZIP = dirGZIP.getAbsolutePath();
                
                

                // Extract GZIP file
                GZIPInputStream gzipInXML = new GZIPInputStream(importDataStream);
                
                File outXML = createFilebyInputStream(gzipInXML, rootDirGZIP + "/" + fileTransfer.getFilename()+".xml");
                gzipInXML.close();
                
                // Import file data
                try (FileInputStream fis = new FileInputStream(outXML)){
                    IngridDocument result = analyzeXMLData(fis , targetObjectUuid, targetAddressUuid, publishImmediately, doSeparateImport, copyNodeIfPresent, fileType, startNewAnalysis );
                    if(result != null) {
                        allProtocols.add( ((ProtocolHandler) result.get( "protocol" )).getProtocol() );
                    }
                } catch (Exception ex) {
                	throw ex;
                } finally {
                    deleteUserImportDirectory(dirGZIP);
                }

                break;

            case ZIP:
                File dirZIP = createFileDirectory("zip/" + userId);
                String rootDirZIP = dirZIP.getAbsolutePath();

                ZipInputStream zipIn = new ZipInputStream(importDataStream);
                // It's not so easy to use an InputStream twice since we need
                // it to find out how many files are inside in the first step
                // and afterwards do the conversion (can only be received easily
                // when using ZipFile()!)
                // Instead we use the available bytes to read shown in percent!
                float totalNumQuotient = 100.0F/importDataStream.available();

                ZipEntry entry;
                while ((entry = zipIn.getNextEntry()) != null) {
                    File file = createFilebyInputStream(zipIn, rootDirZIP + "/" + entry.getName());
                    zipIn.closeEntry();
                    
                    try (FileInputStream fis = new FileInputStream(file)){
                       	IngridDocument result = analyzeXMLData( fis, targetObjectUuid, targetAddressUuid, publishImmediately, doSeparateImport, copyNodeIfPresent, fileType, startNewAnalysis );
                       	// since we analyse more than one file we have to set the flag to false, to remember all converted files in the backend (stored in job store)
                       	startNewAnalysis = false;
                       	if(result != null) {
                       	    allProtocols.add( ((ProtocolHandler) result.get( "protocol" )).getProtocol() );
                       	}
                    } catch (Exception ex) {
                    	// DO NOTHING -> CONTINUE with next file, errors already logged in protocol
                    }
                	
                   	protocolBean.setDataProcessed((int) (100-importDataStream.available()*totalNumQuotient));
                }
                zipIn.close();
                deleteUserImportDirectory(dirZIP);                
                break;

            case UNKNOWN:
                log.debug("Unknown file type. Assuming uncompressed xml data.");
                // Fall through
                break;
            case XML:
                
            	IngridDocument result = analyzeXMLData( importDataStream, targetObjectUuid, targetAddressUuid, publishImmediately, doSeparateImport, copyNodeIfPresent, fileType, startNewAnalysis );
            	if (result.get("error") != null) {
                    throw new IllegalArgumentException("Error analyzing input file: " + result.getString("error"));
                }
            	allProtocols.add( ((ProtocolHandler) result.get( "protocol" )).getProtocol() );
                break;

            default:
                throw new IllegalArgumentException("Error checking input file type. Supported types: GZIP, ZIP, XML");
            }
            // conversion finished -> set progress to 100%
            protocolBean.setDataProcessed(100);

        } catch (MdekException ex) {
            // Wrap the MdekException in a RuntimeException so dwr can convert it
            log.error("MdekException while starting import job.", ex);
            throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(ex));
        } catch (Exception ex) {
            log.error("Error creating input data.", ex);
            throw new RuntimeException(ex);
        } finally {
            protocolBean.setProtocol( allProtocols );
            protocolBean.setImportData(importData);
            protocolBean.setFinished(true);
        }
	}
        
        
    public void startImportThread(String fileType, String targetObjectUuid, String targetAddressUuid,
                boolean publishImmediately, boolean doSeparateImport, boolean copyNodeIfPresent){
        // Start the import process in a separate thread
        // After the thread is started, we wait on it for three seconds and check if it has finished afterwards
        // If the thread ended with an exception (probably because another job is already running),
        // we throw a new MdekException to notify the user
        UserData currentUser = MdekSecurityUtils.getCurrentPortalUserData();
        
        ImportEntitiesThread importThread;
        importThread = new ImportEntitiesThread(catalogRequestHandler, currentUser, fileType, targetObjectUuid, targetAddressUuid, publishImmediately, doSeparateImport, copyNodeIfPresent);
        importThread.start();
        try {
            importThread.join(3000);
        } catch (InterruptedException ex) {
            log.error("Error on startImportThread.", ex);
            ImportEntitiesThread.currentThread().interrupt();
        }
        if (!importThread.isAlive() && importThread.getException() != null) {
            throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(importThread.getException()));
        }
    }

	public JobInfoBean getImportInfo() {
		return catalogRequestHandler.getImportInfo();
	}

	public FileTransfer getLastImportLog() {
		JobInfoBean jobInfo = catalogRequestHandler.getImportInfo();
		String importLog = jobInfo.getDescription();
		importLog += "\n\n" + jobInfo.getFrontendMessages();

		return new FileTransfer("log.txt", "text/plain", importLog.getBytes()); 
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
	
	public ProtocolInfoBean getProtocolInfo() {
		return controlMap.get(getCurrentUserId());
	}
	
	private String getCurrentUserId(){
		UserData user = MdekSecurityUtils.getCurrentPortalUserData();
		return user.getAddressUuid();
	}
    
    private FileType getFileType(String mimeType) {
        if ("application/x-gzip".equals(mimeType) || "application/gzip".equals(mimeType)) {
            return FileType.GZIP;

        } else if ("application/zip".equals(mimeType)) {
            return FileType.ZIP;

        } else if ("application/x-zip-compressed".equals(mimeType)) {
            return FileType.ZIP;

        } else if ("text/xml".equals(mimeType)) {
            return FileType.XML;

        } else {
            log.debug("Could not determine import file type from mime type: '"+mimeType+"'");
            return FileType.UNKNOWN;
        }
    }

    private IngridDocument analyzeXMLData(InputStream inputFileStream, String targetObjectUuid, String targetAddressUuid, Boolean publishImmediately, Boolean doSeparateImport, Boolean copyNodeIfPresent, String frontendProtocol, boolean startNewAnalysis) {
        IngridDocument result = null;
        try {
            result = catalogRequestHandler.analyzeImportData( MdekSecurityUtils.getCurrentPortalUserData(), compress( inputFileStream ).toByteArray(), targetObjectUuid, targetAddressUuid, frontendProtocol, publishImmediately, doSeparateImport, copyNodeIfPresent, startNewAnalysis );
            
        } catch (IOException e) {
            log.error("Error on analyzeXMLData.", e);
        }
        
        return result;
    }
    
    private File createFileDirectory(String dirname){
        File dirGZIP = new File(System.getProperty("java.io.tmpdir") + "/ingrid-ige/import/" + dirname);
        dirGZIP.mkdirs();
        
        return dirGZIP;
    }

    private void deleteUserImportDirectory(File file){
        File[] listFiles = file.listFiles();
        for (int i=0; i < listFiles.length; i++){
            if(listFiles[i].listFiles() != null && listFiles[i].listFiles().length > 1){
                deleteUserImportDirectory(listFiles[i]);
            }else{
                if(listFiles[i].delete()) {
                    log.debug(String.format("Delete file: %s", listFiles[i].getName()));
                }
            }
        }
        if(file.delete()) {
            log.debug(String.format("Delete file: %s", file.getName()));
        }
    }
    
    private File createFilebyInputStream(InputStream input, String filePath) throws IOException{
        File file = new File(filePath);
        try (FileOutputStream output = new FileOutputStream(file)){
            int len = 0;
            while ((len = input.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
        }
        
        return file;
    }
    
    // Compress (zip) any data on InputStream and write it to a ByteArrayOutputStream
    private static ByteArrayOutputStream compress(InputStream is) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(is);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzout = new GZIPOutputStream(new BufferedOutputStream(out));

        final int BUFFER = 2048;
        int count;
        byte[] data = new byte[BUFFER];
        while((count = bin.read(data, 0, BUFFER)) != -1) {
           gzout.write(data, 0, count);
        }

        gzout.close();
        return out;
    }
}

// Helper thread which starts an import process
class ImportEntitiesThread extends Thread {

	private static final Logger log = Logger.getLogger(ImportEntitiesThread.class);	

	private final CatalogRequestHandler catalogRequestHandler;
	private final UserData currentUser;
	private final String targetObjectUuid;
	private final String targetAddressUuid;
	private final boolean publishImmediately;
	private final boolean doSeparateImport;
	private final String fileDataType;

	private MdekException exception;

    private final boolean copyNodeIfPresent;
	
	public ImportEntitiesThread(CatalogRequestHandler catalogRequestHandler, 
	        UserData currentUser, String fileDataType, 
	        String targetObjectUuid, String targetAddressUuid, 
	        boolean publishImmediately, boolean doSeparateImport, boolean copyNodeIfPresent) {
		super();
		this.catalogRequestHandler = catalogRequestHandler;
		this.currentUser = currentUser;
		this.targetObjectUuid = targetObjectUuid;
		this.targetAddressUuid = targetAddressUuid;
		this.publishImmediately = publishImmediately;
		this.doSeparateImport = doSeparateImport;
		this.fileDataType = fileDataType;
		this.copyNodeIfPresent = copyNodeIfPresent;
	}

	@Override
	public void run() {
		try {
			catalogRequestHandler.importEntities(currentUser, targetObjectUuid, targetAddressUuid, fileDataType, publishImmediately, doSeparateImport, copyNodeIfPresent);
		} catch(MdekException ex) {
			log.error("Exception while importing entities.", ex);
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
