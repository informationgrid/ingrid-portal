package de.ingrid.mdek.beans.controls;

public class TextControl extends ExtendedControls {
    private int numLines;
    
    public TextControl() {
        this.setType(TEXT_CONTROL);
    }

    public void setNumLines(int numLines) {
        this.numLines = numLines;
    }

    public int getNumLines() {
        return numLines;
    }
}
