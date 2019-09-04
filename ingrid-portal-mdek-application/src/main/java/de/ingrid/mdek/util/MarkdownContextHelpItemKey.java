/*-
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
package de.ingrid.mdek.util;

import java.util.Objects;

public class MarkdownContextHelpItemKey {

    /**
     * GUID fo the ingrid form.
     */
    private String guid = null;

    /**
     * Object class ID, since help messages for one GUID can be dependent on the
     * object class.
     */
    private String oid = null;

    /**
     * Language ISO 639-1
     */
    private String lang = null;

    /**
     * Profile string
     */
    private String profile = null;
    
    public MarkdownContextHelpItemKey(String guid) {
        super();
        this.guid = guid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "MarkdownContextHelpItem: {guid: " + guid + "; oid: " + oid + "; lang: " + lang + "; profile: " + profile + "}";
    }

    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (!(o instanceof MarkdownContextHelpItemKey)) {
            return false;
        }
        MarkdownContextHelpItemKey mchi = (MarkdownContextHelpItemKey) o;
        return Objects.equals( guid, mchi.guid ) && Objects.equals( lang, mchi.lang ) && Objects.equals( oid, mchi.oid ) && Objects.equals( profile, mchi.profile );
    }

    @Override
    public int hashCode() {
        return Objects.hash( guid, lang, oid, profile);
    }

}
