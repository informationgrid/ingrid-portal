package de.ingrid.mdek.util;

import java.util.Objects;

public class MarkdownContextHelpItem {

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
     * Title, used in help window bar.
     */
    String title = null;

    public MarkdownContextHelpItem(String guid, String oid, String title) {
        super();
        this.guid = guid;
        this.oid = oid;
        this.title = title;
    }

    public MarkdownContextHelpItem(String guid, String oid) {
        super();
        this.guid = guid;
        this.oid = oid;
    }

    public MarkdownContextHelpItem(String guid) {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "MarkdownContextHelpItem: {guid: " + guid + "; oid: " + oid + "; lang: " + lang + "}";
    }

    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (!(o instanceof MarkdownContextHelpItem)) {
            return false;
        }
        MarkdownContextHelpItem mchi = (MarkdownContextHelpItem) o;
        return Objects.equals( guid, mchi.guid ) && Objects.equals( lang, mchi.lang ) && Objects.equals( oid, mchi.oid );
    }

    @Override
    public int hashCode() {
        return Objects.hash( guid, lang, oid );
    }

}
