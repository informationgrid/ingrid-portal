/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.mdek.beans;

import java.util.ArrayList;
import java.util.List;

public class Record {
    private String identifier;
    private String title;
    private String uuid;
    private List<String> downloadData = new ArrayList<>();
    private boolean hasDownloadData = false;
    
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public boolean isHasDownloadData() {
        return hasDownloadData;
    }
    
    public void setHasDownloadData(boolean hasDownloadData) {
        this.hasDownloadData = hasDownloadData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getDownloadData() {
        return downloadData;
    }

    public void setDownloadData(List<String> downloadData) {
        this.downloadData = downloadData;
        if (!downloadData.isEmpty()) this.hasDownloadData = true;
    }
    
    public void addDownloadData(String data) {
        this.downloadData.add( data );
        this.hasDownloadData = true;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
