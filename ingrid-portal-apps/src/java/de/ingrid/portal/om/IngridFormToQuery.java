package de.ingrid.portal.om;

public abstract class IngridFormToQuery {

    private Long id;

    private String formValue;

    private String queryValue;

    private int sortkey;

    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Returns the formValue.
     */
    public String getFormValue() {
        return formValue;
    }

    /**
     * @param formValue The formValue to set.
     */
    public void setFormValue(String formValue) {
        this.formValue = formValue;
    }

    /**
     * @return Returns the queryValue.
     */
    public String getQueryValue() {
        return queryValue;
    }

    /**
     * @param queryValue The queryValue to set.
     */
    public void setQueryValue(String queryValue) {
        this.queryValue = queryValue;
    }

    /**
     * @return Returns the sortkey.
     */
    public int getSortkey() {
        return sortkey;
    }

    /**
     * @param sortkey The sortkey to set.
     */
    public void setSortkey(int sortkey) {
        this.sortkey = sortkey;
    }

}
