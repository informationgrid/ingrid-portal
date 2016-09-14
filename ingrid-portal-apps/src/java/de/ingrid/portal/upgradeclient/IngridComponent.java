/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.portal.upgradeclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ingrid.portal.scheduler.jobs.IngridAbstractStateJob;

public class IngridComponent implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 6373648235772800709L;
    
    public static final String PARTNER_INFO  = "PARTNER_INFO";
    
    public static final String PROVIDER_INFO = "PROVIDER_INFO";
    
    private String id;
    private String name;
    private String type;
    private String version;
    private String versionAvailable;
    // Build extracted only in components from Server
    // components managed by the client have build information inside version already
    private String versionAvailableBuild;
    private String downloadLink;
    private String changelogLink;
    
    private Date emailSent;
    private String infoText;
    private List<String> emails;
    private boolean hasBeenSent;
    private boolean isIPlug;
    private boolean wasUnknown;
    private String status;
    private String errorStatus;
    private String connected;
    
    private Map<String, Object> extraInfo = new HashMap<String, Object>();
    
    public IngridComponent(String id, String type) {
        this.setId(id);
        this.setName(id);
        this.setType(type);
        
        this.emails = new ArrayList<String>();
        setConnected(IngridAbstractStateJob.STATUS_IS_MANUALLY_CONNECTED);
        setIPlug(false);
        setHasBeenSent(false);
        setWasUnknown(true);
        setStatus(IngridAbstractStateJob.STATUS_NO_UPDATE_AVAILABLE);
        //this.setVersion(version);
        //this.setVersionAvailable(versionAvailable);
        //this.setDate(date);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setEmailSentDate(Date date) {
        this.emailSent = date;
    }

    public Date getEmailSentDate() {
        return emailSent;
    }
/*
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }
*/
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setVersionAvailable(String versionAvailable) {
        this.versionAvailable = versionAvailable;
    }

    public String getVersionAvailable() {
        return versionAvailable;
    }

    public void setIPlug(boolean isIPlug) {
        this.isIPlug = isIPlug;
    }

    public boolean isIPlug() {
        return isIPlug;
    }

    public void addEmail(String email) {
        this.emails.add(email);
    }

    public List<String> getEmails() {
        return emails;
    }
    
    public void removeEmail(String[] emails) {
        for (String email : emails) {
            this.emails.remove(email);
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setWasUnknown(boolean wasUnknown) {
        this.wasUnknown = wasUnknown;
    }

    public boolean wasUnknown() {
        return wasUnknown;
    }

    public void setInfo(String text) {
        infoText = text;
    }
    
    public String getInfo() {
        return infoText;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }

    public void setHasBeenSent(boolean hasBeenSent) {
        this.hasBeenSent = hasBeenSent;
    }

    public boolean hasBeenSent() {
        return hasBeenSent;
    }
    
    public void addExtraInfo(String key, Object value) {
        extraInfo.put(key, value);
    }
    
    public Object getExtraInfo(String key) {
        return extraInfo.get(key);
    }
    
    public void removeExtraInfo(String key) {
        extraInfo.remove(key);
    }

    public void setChangelogLink(String changelogLink) {
        this.changelogLink = changelogLink;
    }

    public String getChangelogLink() {
        return changelogLink;
    }

    public void setConnected(String connected) {
        this.connected = connected;
    }

    public String getConnected() {
        return connected;
    }

    public void setErrorStatus(String status) {
        this.errorStatus = status;        
    }
    
    public String getErrorStatus() {
        return errorStatus;
    }

    public void setVersionAvailableBuild(String versionAvailableBuild) {
        this.versionAvailableBuild = versionAvailableBuild;
    }

    public String getVersionAvailableBuild() {
        return versionAvailableBuild;
    }

    public void reset() {
        setVersionAvailable(null);
        setVersionAvailableBuild(null);
        setChangelogLink(null);
        setDownloadLink(null);
    }
}
