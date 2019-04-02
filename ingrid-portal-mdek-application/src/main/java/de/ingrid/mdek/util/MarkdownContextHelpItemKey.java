package de.ingrid.mdek.util;

import java.util.Objects;

public class MarkdownContextHelpItemKey {

    /**
     * GUID fo the ingrid form.
     */
    String guid = null;

    /**
     * Object class ID, since help messages for one GUID can be dependent on the
     * object class.
     */
    String oid = null;

    /**
     * Language ISO 639-1
     */
    String lang = null;

    /**
     * Profile string
     */
    String profile = null;
    
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
