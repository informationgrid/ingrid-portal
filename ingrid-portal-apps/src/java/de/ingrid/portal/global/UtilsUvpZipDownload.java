/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
package de.ingrid.portal.global;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.xpath.XPathUtils;

public class UtilsUvpZipDownload {

    private static final Logger log = LoggerFactory.getLogger(UtilsUvpZipDownload.class);

    public static File searchFilesToCreateZip(String uuid, String plugid, IngridResourceBundle messages, XPathUtils xPathUtils) {
        File zip = null;
        IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
        String query = Settings.HIT_KEY_OBJ_ID + ":" + uuid + " iplugs:\"" + plugid + "\" ranking:score";
        try {
            IngridQuery q = QueryStringParser.parse(query);
            IngridHits hits = ibus.search(q, 1, 1, 0, 3000);
            IngridHit hit = null;
            if(hits.getHits().length > 0) {
                hit = hits.getHits()[0];
            }
            if(hit != null) { 
                Record record = ibus.getRecord(hit);
                String idfString = IdfTool.getIdfDataFromRecord(record);

                if(idfString != null){
                    if(log.isDebugEnabled()){
                        log.debug(idfString);
                    } 
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    dbf.setNamespaceAware(true);
                    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                    dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                    dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                    dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
                    dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document idfDoc = db.parse(new InputSource(new StringReader(idfString)));
                    Element root = idfDoc.getDocumentElement();
                    String title = plugid.replaceAll(":", "#") + uuid;
                    String xpathExpression = "//idf:idfMdMetadata/name";
                    if(xPathUtils.nodeExists(root, xpathExpression)) {
                        title = xPathUtils.getString(root, xpathExpression);
                    }
                    String downloadPath = UtilsUvpZipDownload.createDownloadPath(PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_ZIP_PATH, "./data"), plugid, uuid);
                    xpathExpression = "//idf:idfMdMetadata/steps/step[docs/doc]";
                    if(xPathUtils.nodeExists(root, xpathExpression)) {
                        zip = createDownload(uuid, title, downloadPath, xPathUtils.getNodeList(root, xpathExpression), messages, xPathUtils);
                    }
                    xpathExpression = "//idf:idfMdMetadata[docs/doc]";
                    if(xPathUtils.nodeExists(root, xpathExpression)) {
                        zip = createDownload(uuid, title, downloadPath, xPathUtils.getNodeList(root, xpathExpression), messages, xPathUtils);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error create download file for iPlug '" + plugid + "' and docuuid '" + uuid + "':", e);
        }
        return zip;
    }

    public static File createDownload(String uuid, String title, String downloadPath, NodeList docParentNode, IngridResourceBundle messages, XPathUtils xPathUtils) {
        String zipName = downloadPath + "/" + title.replaceAll("[\\\\/:*?\"<>|]", "_") + ".zip";
        File zipFile = new File(zipName);
        File processFile = new File(downloadPath + "/PROCESS_RUNNING");
        File statsJsonFile = new File(downloadPath + "/stats.json");

        try {
            // Create download status file
            if(!processFile.exists() && processFile.createNewFile()) {
                log.debug("Create download process file.");
            }
            if(!zipFile.exists()) {
                // Delete old files
                for(File file: processFile.getParentFile().listFiles()) {
                    if(!file.equals(processFile)) {
                        file.delete();
                    }
                }
                if(docParentNode.getLength() > 0) {
                    createNewZipFile(uuid, title, zipName, zipFile, downloadPath, docParentNode, xPathUtils, messages);
                }
            } else if (statsJsonFile.exists()) {
                String json = new String(Files.readAllBytes(statsJsonFile.toPath()));
                JSONObject statsJson = new JSONObject(json);
                // Update new ZIP file
                createNewOrReplaceZipFile(uuid, title, zipName, zipFile, downloadPath, docParentNode, statsJsonFile, statsJson, xPathUtils, messages, true);
            }
        } catch (JSONException | IOException e) {
            log.error("Error create download.", e);
        } finally {
            // Update zip file
            File tmpFile = new File(zipFile.getAbsoluteFile() + ".tmp");
            if(tmpFile.exists() && zipFile.exists()) {
                try {
                    Files.move(tmpFile.getAbsoluteFile().toPath(), zipFile.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.error("Error rename to old zip file.", e);
                }
            }
            // Delete process file
            if(processFile.exists()) {
                if(processFile.delete()) {
                    log.debug("Delete download process file!");
                }
            }
        }
        return zipFile;
    }

    private static void createNewZipFile(String uuid, String title, String zipName, File zipFile, String downloadPath, NodeList docParentNode, XPathUtils xPathUtils, IngridResourceBundle messages) {
        createNewOrReplaceZipFile(uuid, title, zipName, zipFile, downloadPath, docParentNode, null, null, xPathUtils, messages, false);
    }

    private static void createNewOrReplaceZipFile(String uuid, String title, String zipName, File zipFile, String downloadPath, NodeList docParentNode, File statsJsonFile, JSONObject statsJson, XPathUtils xPathUtils, IngridResourceBundle messages, boolean checkHead) {
        JSONObject newStatsJson = new JSONObject(); 
        ArrayList<String> stepFoldernames = new ArrayList<String>(); 
        for (int i = 0; i < docParentNode.getLength(); i++) {
            Node stepNode = docParentNode.item(i);
            String stepType = "";
            String stepDate = "";
            if(xPathUtils.nodeExists(stepNode, "./@type")) {
                stepType = xPathUtils.getString(stepNode, "./@type");
            }
            if(xPathUtils.nodeExists(stepNode, "./datePeriod/from | ./date/from")) {
                stepDate = xPathUtils.getString(stepNode, "./datePeriod/from | ./date/from");
                stepDate = stepDate.split("T")[0];
                stepDate = stepDate.replace("-", "");
            }
            if(xPathUtils.nodeExists(stepNode, "./docs/doc")) {
                String stepFoldername = stepDate;
                if(!stepType.isEmpty()) {
                    stepFoldername = stepFoldername + "_" + messages.getString("common.steps.uvp." + stepType);
                } else {
                    stepType = "neg";
                }
                // Create ZIP step folder
                if(stepFoldernames.indexOf(stepFoldername) > -1) {
                    int count = 1;
                    for (int j = 0; j < stepFoldernames.size(); j++) {
                        if(stepFoldernames.get(j).equals(stepFoldername)) {
                            count++;
                        }
                    }
                    stepFoldernames.add(stepFoldername);
                    stepFoldername = stepFoldername + " (" + count + ")";
                } else {
                    stepFoldernames.add(stepFoldername);
                }
                if(xPathUtils.nodeExists(stepNode, "./docs")) {
                    NodeList docsList = xPathUtils.getNodeList(stepNode, "./docs");
                    for (int j = 0; j < docsList.getLength(); j++) {
                        Node docsNode = docsList.item(j);
                        String docType = "";
                        if(xPathUtils.nodeExists(docsNode, "./@type")) {
                            docType = xPathUtils.getString(docsNode, "./@type");
                        }
                        if(!docType.isEmpty()) {
                            if(!stepType.isEmpty()) {
                                docType = messages.getString("search.detail.uvp." + stepType + ".doc." + docType);
                            }
                        }
                        // Create ZIP doc folder into step folder
                        String docFoldername = stepFoldername + "/" + docType;
                        if(xPathUtils.nodeExists(docsNode, "./doc")) {
                            if(docsNode.hasChildNodes()) {
                                if(xPathUtils.nodeExists(docsNode, "./doc")) {
                                    NodeList docList = xPathUtils.getNodeList(docsNode, "./doc");
                                    ArrayList<String> docFilenames = new ArrayList<String>(); 
                                    for (int k = 0; k < docList.getLength(); k++) {
                                        Node doc = docList.item(k);
                                        // Download links and add to ZIP
                                        if(xPathUtils.nodeExists(doc, "./link")) {
                                            String link = xPathUtils.getString(doc, "./link");
                                            String label = xPathUtils.getString(doc, "./label").replaceAll("[\\\\/:*?\"<>|]", "_");
                                            if(docFilenames.indexOf(label) > -1) {
                                                int count = 1;
                                                for (int l = 0; l < docFilenames.size(); l++) {
                                                    if(docFilenames.get(l).equals(label)) {
                                                        count++;
                                                    }
                                                }
                                                docFilenames.add(label);
                                                label = label + " (" + count + ")";
                                            } else {
                                                docFilenames.add(label);
                                            }
                                            try {
                                                URL url = new URL(link);
                                                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                                String login = PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_LOGIN);
                                                String password = PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_PASSWORD);
                                                if(StringUtils.isNotEmpty(login) && StringUtils.isNotEmpty(password)) {
                                                    UtilsHttpConnection.urlConnectionAuth(con, login, password);
                                                }
                                                con.setRequestMethod("HEAD");
                                                String zipEntryName = docFoldername + "/" + URLDecoder.decode(label, "UTF-8");
                                                JSONObject statsEntry = new JSONObject();
                                                int length = con.getContentLength();
                                                String contentType = con.getContentType();
                                                long lastModified = con.getLastModified();
                                                if(length != -1) {
                                                    statsEntry.put("length", length);
                                                    statsEntry.put("type", contentType);
                                                    statsEntry.put("modified", lastModified);
                                                    if(con.getContentType().indexOf("text/html") > -1) {
                                                        if(!zipEntryName.endsWith(".html")) {
                                                            if(!link.isEmpty()) {
                                                                zipEntryName += "-" + link.replaceAll("[\\\\/:*?\"<>|]", "_");
                                                            }
                                                            zipEntryName += ".html";
                                                        }
                                                    }
                                                    statsEntry.put("name", zipEntryName);
                                                    newStatsJson.put(link, statsEntry);
                                                }
                                            } catch (IOException e) {
                                                log.error("Error download file for ZIP: '" + title + "'", e);
                                            } catch (JSONException e) {
                                                log.error("Error put stats entry.", e);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(statsJsonFile == null || !statsJsonFile.exists()) {
            // Create new zip
            if(newStatsJson.length() != 0) {
                File newStatsJsonFile = new File(downloadPath + "/stats-update.json");
                try (
                    FileWriter statsJsonFileWriter = new FileWriter(newStatsJsonFile.getAbsolutePath())
                ){
                    statsJsonFileWriter.write(newStatsJson.toString());

                    createZipFromJSON(zipName, zipFile, newStatsJson);

                } catch (Exception e) {
                    log.error("Error write stats file!", e);
                } finally {
                    File stats = new File(downloadPath + "/stats.json");
                    newStatsJsonFile.renameTo(stats);
                }
            }
        } else {
            // Check last update time
            Date now = new Date();
            long nowTime = now.getTime();
            long lastUpdateTime = statsJsonFile.lastModified();
            long lastUpdateTimeInMin = (nowTime - lastUpdateTime) / 60000;
            long updateTime = PortalConfig.getInstance().getLong(PortalConfig.PORTAL_DETAIL_UVP_ZIP_UPDATE_MIN, 0);
                    
            // Update zip
            if(lastUpdateTimeInMin > updateTime) {
                if(newStatsJson.length() != 0) {
                    if(statsJson != null) {
                        File newStatsJsonFile = new File(downloadPath + "/stats-update.json");
                        try(
                            FileWriter statsJsonFileWriter = new FileWriter(newStatsJsonFile.getAbsolutePath())
                        ){
                            statsJsonFileWriter.write(newStatsJson.toString());
    
                            Iterator<String> newkeys = newStatsJson.keys();
                            while(newkeys.hasNext()) {
                                String newKey = newkeys.next();
                                JSONObject newStatsEntry = newStatsJson.getJSONObject(newKey);
                                if(statsJson.has(newKey)) {
                                    JSONObject statsEntry = statsJson.getJSONObject(newKey);
                                    if(newStatsEntry.getString("name").equals(statsEntry.getString("name")) &&
                                        newStatsEntry.getString("length").equals(statsEntry.getString("length")) &&
                                        newStatsEntry.getString("type").equals(statsEntry.getString("type")) &&
                                        newStatsEntry.getString("modified").equals(statsEntry.getString("modified"))) {
                                        log.debug("Entry unchanged.");
                                        statsJson.remove(newKey);
                                        newkeys.remove();
                                        newStatsJson.remove(newKey);
                                    }
                                }
                            }
                            updateZipFromJSON(zipName, zipFile, statsJson, newStatsJson);
    
                        } catch (Exception e) {
                            log.error("Error on changes check.", e);
                        } finally {
                            File stats = new File(downloadPath + "/stats.json");
                            if(stats.delete()) {
                                newStatsJsonFile.renameTo(stats);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createZipFromJSON(String zipName, File zipFile, JSONObject statsJson) {
        if(statsJson != null && statsJson.length() > 0) {
            try (
                FileOutputStream f = new FileOutputStream(zipName);
                ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(f));
            ){
                Iterator<String> keys = statsJson.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    try {
                        JSONObject statsEntry = statsJson.getJSONObject(key);
                        addFileInZip(key, zip, statsEntry);
                    } catch (IOException | JSONException e) {
                        log.error("Error download file for ZIP: '" + key + "'", e);
                    }
                }
            } catch (IOException e) {
                log.error("Error create download file: " + zipName, e);
            }
        }
    }

    public static void addFileInZip(String link, ZipOutputStream zip, JSONObject statsEntry) throws IOException, JSONException {
        URL url = new URL(link);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        String login = PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_LOGIN);
        String password = PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_PASSWORD);
        if(StringUtils.isNotEmpty(login) && StringUtils.isNotEmpty(password)) {
            UtilsHttpConnection.urlConnectionAuth(con, login, password);
        }
        InputStream in = con.getInputStream();
        ZipEntry zipEntry = new ZipEntry(statsEntry.getString("name"));
        zip.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = in.read(bytes)) >= 0) {
            zip.write(bytes, 0, length);
        }
    }

    private static void updateZipFromJSON(String zipName, File zipFile, JSONObject delStatsJson, JSONObject newStatsJson) {
        if(delStatsJson.length() > 0 || newStatsJson.length() > 0) {
            File newFile = new File(zipName + ".tmp");
            try (
                ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(newFile));
                ZipFile oldZipFile = new ZipFile(zipFile);
            ){
                // Delete non existing files
                for(Enumeration zipEntries = oldZipFile.entries(); zipEntries.hasMoreElements(); ) {
                    ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();
                    Iterator<String> delKeys = delStatsJson.keys();
                    boolean hasToIgnore = false;
                    while(delKeys.hasNext()) {
                        String delKey = delKeys.next();
                        try {
                            JSONObject statsEntry = delStatsJson.getJSONObject(delKey);
                            String name = statsEntry.getString("name");
                            if(zipEntry.getName().equalsIgnoreCase(name)) {
                                hasToIgnore = true;
                                break;
                            }
                        } catch (JSONException ex) {
                            log.error("Error get delete entry: '" + delKey + "'", ex);
                        }
                    }
                    if (!hasToIgnore) {
                        zip.putNextEntry(zipEntry);
                        InputStream is = oldZipFile.getInputStream(zipEntry);
                        byte[] buf = new byte[1024];
                        int len;
                        while((len = is.read(buf)) > 0) {
                            zip.write(buf, 0, len);
                        }
                    }
                }
                if(newStatsJson != null && newStatsJson.length() > 0) {
                    Iterator<String> keys = newStatsJson.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        try {
                            JSONObject newStatsEntry = newStatsJson.getJSONObject(key);
                            addFileInZip(key, zip, newStatsEntry);
                        } catch (IOException | JSONException e) {
                            log.error("Error download file for ZIP: '" + key + "'", e);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Error create download file: " + zipName, e);
            }
        }
    }

    public static void updateFileInZip(String link, ZipOutputStream zip, JSONObject statsEntry) throws IOException, JSONException {
        URL url = new URL(link);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        String login = PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_LOGIN);
        String password = PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_PASSWORD);
        if(StringUtils.isNotEmpty(login) && StringUtils.isNotEmpty(password)) {
            UtilsHttpConnection.urlConnectionAuth(con, login, password);
        }
        InputStream in = con.getInputStream();
        ZipEntry zipEntry = new ZipEntry(statsEntry.getString("name"));
        zip.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = in.read(bytes)) >= 0) {
            zip.write(bytes, 0, length);
        }
    }

    public static String createDownloadPath(String configPath, String plugid, String uuid) {
        File dirData = new File(configPath);
        if(!dirData.exists()) {
            dirData.mkdirs();
        }
        File dirDownload = new File(dirData.getPath() + "/downloads");
        if(!dirDownload.exists()) {
            dirDownload.mkdirs();
        }
        File dirPlugId = new File(dirDownload.getPath() + "/" + plugid.replace(":", "#"));
        if(!dirPlugId.exists()) {
            dirPlugId.mkdirs();
        }
        File dirUuid = new File(dirPlugId.getPath() + "/" + uuid);
        if(!dirUuid.exists()) {
            dirUuid.mkdirs();
        }
        return dirUuid.getPath();
    }

}
