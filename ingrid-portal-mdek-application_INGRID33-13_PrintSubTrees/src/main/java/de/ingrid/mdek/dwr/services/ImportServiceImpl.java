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

	private enum FileType { GZIP, ZIP, XML, UNKNOWN }
    
    byte[] buffer = new byte[2048];
    
	// Injected by Spring
	private CatalogRequestHandler catalogRequestHandler;
	private DataMapperFactory dataMapperFactory;
	private ProtocolFactory protocolFactory;
	
	private static Map<String,ProtocolInfoBean> controlMap = new ConcurrentHashMap<String, ProtocolInfoBean>();
	
	public void importEntities(FileTransfer fileTransfer, String fileType, String targetObjectUuid, String targetAddressUuid,
			boolean publishImmediately, boolean doSeparateImport) {

	    String userId = getCurrentUserId();
	    
		// Define bean for protocol
		ProtocolInfoBean protocolBean = new ProtocolInfoBean(protocolFactory.getProtocolHandler());
		protocolBean.setInputType(fileType);
		controlMap.put(userId, protocolBean);
		
		//Start protocol
		protocolBean.getProtocolHandler().startProtocol();
		protocolBean.setDataProcessed(0);
		
		List<byte[]> importData = new ArrayList<byte[]>();
        try {
            // map source file into icg-target format
            InputStream importDataStream;
            
            switch (getFileType(fileTransfer.getMimeType())) {
            case GZIP:
                File dirGZIP = createFileDirectory("gzip/" + userId);
                String rootDirGZIP = dirGZIP.getAbsolutePath();
                
                importDataStream = fileTransfer.getInputStream();

                // Extract GZIP file
                //FileInputStream inXML = new FileInputStream(fileGZIP);
                GZIPInputStream gzipInXML = new GZIPInputStream(importDataStream);//inXML);
                
                File outXML = createFilebyInputStream(gzipInXML, rootDirGZIP + "/" + fileTransfer.getFilename()+".xml");
                gzipInXML.close();
                
                // Import file data
                try {
                    addImportData(importData, new FileInputStream(outXML), outXML.getName(), fileType, protocolBean.getProtocolHandler());
                } catch (Exception ex) {
                	throw ex;
                } finally {
                    deleteUserImportDirectory(dirGZIP);                	
                }

                break;

            case ZIP:
                importDataStream = fileTransfer.getInputStream();
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
                    
                    try {
                       	addImportData(importData, new FileInputStream(file), file.getName(), fileType, protocolBean.getProtocolHandler());                    	
                    } catch (Exception ex) {
                    	protocolBean.getProtocolHandler().addMessage("\n\n");
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
            case XML:
                
            	addImportData(importData, fileTransfer.getInputStream(), fileTransfer.getFilename(), fileType, protocolBean.getProtocolHandler());
            	
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
        } finally {
            protocolBean.setImportData(importData);
            protocolBean.setFinished(true);
        }
	}
        
        
    public void startImportThread(FileTransfer fileTransfer, String fileType, String targetObjectUuid, String targetAddressUuid,
                boolean publishImmediately, boolean doSeparateImport, boolean copyNodeIfPresent){
        // Start the import process in a separate thread
        // After the thread is started, we wait on it for three seconds and check if it has finished afterwards
        // If the thread ended with an exception (probably because another job is already running),
        // we throw a new MdekException to notify the user
        UserData currentUser = MdekSecurityUtils.getCurrentPortalUserData();
        
        ImportEntitiesThread importThread;
        ProtocolInfoBean infoBean = controlMap.get(getCurrentUserId());
        importThread = new ImportEntitiesThread(catalogRequestHandler, currentUser, infoBean.getImportData(), fileType, targetObjectUuid, targetAddressUuid, infoBean.getProtocol(), publishImmediately, doSeparateImport, copyNodeIfPresent);
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

	public JobInfoBean getImportInfo() {
		return catalogRequestHandler.getImportInfo();
	}

	public FileTransfer getLastImportLog() {
		JobInfoBean jobInfo = catalogRequestHandler.getImportInfo();
		String log = jobInfo.getDescription();
		log += "\n\n" + jobInfo.getFrontendMessages();

		return new FileTransfer("log.txt", "text/plain", log.getBytes()); 
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

    /**
     * Add a import file as compressed IGC import format to the importdata 
     * list. Transforms the file if necessary. If an error occurs during the 
     * transformation process, the file will not be added to the list. The error 
     * messages are still available through the protocolHandler.
     * 
     * 
     * @param importData The import data list. Contains compressed imput data byte[].
     * @param input The input stream.
     * @param fileName The file name of the source data.
     * @param fileType The file type of the source data.
     * @param protocolHandler The protocol handler.
     */
    private void addImportData(List <byte[]> importData, InputStream input, String fileName, String fileType, ProtocolHandler protocolHandler)
    throws IOException, MdekException {
        try {
			importData.add(compress(importXMLData(input, fileName, fileType, protocolHandler)).toByteArray());
		} catch (IOException e) {
			log.error("Error adding import data from file: " + fileName, e);
			throw e;
		} catch (MdekException e) {
			throw e;
		}
    }
    
    
    private InputStream importXMLData (InputStream inputFileStream, String inputFileName, String inputFileType, ProtocolHandler protocolHandler)
    throws MdekException {
        if(inputFileType.equals("igc")){
            return inputFileStream;
        }else{
            protocolHandler.setCurrentFilename(inputFileName);
            return dataMapperFactory.getMapper(inputFileType).convert(inputFileStream, protocolHandler);
        }
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
                listFiles[i].delete();  
            }
        }
        file.delete();
    }
    
    private File createFilebyInputStream(InputStream input, String filePath) throws IOException{
        File file = new File(filePath);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            int len = 0;
            while ((len = input.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
        } finally {
            // we must always close the output file
            if(output!=null) output.close();
        }
        
        return file;
    }

    /*
    private File createTemporaryFile(InputStream inputStream, File dir, String prefix) throws IOException{
        File tmpFile = File.createTempFile(prefix, ".txt", dir);
        OutputStream src = new FileOutputStream(tmpFile);
        src.write(createByteArrayFromInputStream(inputStream));
        src.close();
        
        return tmpFile;
    }*/
    
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
}

// Helper thread which starts an import process
class ImportEntitiesThread extends Thread {

	private final static Logger log = Logger.getLogger(ImportEntitiesThread.class);	

	private final CatalogRequestHandler catalogRequestHandler;
	private final UserData currentUser;
	private final List<byte[]> importData;
	private final String targetObjectUuid;
	private final String targetAddressUuid;
	private final boolean publishImmediately;
	private final boolean doSeparateImport;
	//private final String fileDataType;
	private final String protocol;

	private volatile MdekException exception;

    private final boolean copyNodeIfPresent;
	
	public ImportEntitiesThread(CatalogRequestHandler catalogRequestHandler, 
	        UserData currentUser, List<byte[]> importData, String fileDataType, 
	        String targetObjectUuid, String targetAddressUuid, String protocol, 
	        boolean publishImmediately, boolean doSeparateImport, boolean copyNodeIfPresent) {
		super();
		this.catalogRequestHandler = catalogRequestHandler;
		this.currentUser = currentUser;
		this.importData = importData;
		this.targetObjectUuid = targetObjectUuid;
		this.targetAddressUuid = targetAddressUuid;
		this.publishImmediately = publishImmediately;
		this.doSeparateImport = doSeparateImport;
		//this.fileDataType = fileDataType;
		this.protocol = protocol;
		this.copyNodeIfPresent = copyNodeIfPresent;
	}

	@Override
	public void run() {
		try {
			catalogRequestHandler.importEntities(currentUser, importData, targetObjectUuid, targetAddressUuid, protocol, publishImmediately, doSeparateImport, copyNodeIfPresent);
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