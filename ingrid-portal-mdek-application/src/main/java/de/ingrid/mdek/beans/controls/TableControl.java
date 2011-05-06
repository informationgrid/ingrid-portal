package de.ingrid.mdek.beans.controls;

import java.util.ArrayList;
import java.util.List;

public class TableControl extends ExtendedControls {
    
    private List<TableColumn> columns = new ArrayList<TableColumn>();
    private int numTableRows;
    
    public TableControl() {
        this.setType(GRID_CONTROL);
    }

    public void setColumns(List<TableColumn> columns) {this.columns = columns;}
    public List<TableColumn> getColumns() {return columns;}

    public void setNumTableRows(int numRows) {
        this.numTableRows = numRows;
    }

    public int getNumTableRows() {
        return numTableRows;
    }
    
    /**
     * A table has no index name since only its columns can have one!
     */
    @Override
    public String getIndexName() {
        return null;
    }
}
