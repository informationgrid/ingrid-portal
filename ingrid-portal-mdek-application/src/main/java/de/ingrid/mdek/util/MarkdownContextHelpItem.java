package de.ingrid.mdek.util;

import java.nio.file.Path;

public class MarkdownContextHelpItem {

    /**
     * Path to of the markdown file.
     */
    private Path markDownFilename = null;

    /**
     * Title, used in help window bar.
     */
    private String title = null;

    public MarkdownContextHelpItem(Path markDownFilename) {
        super();
        this.markDownFilename = markDownFilename;
    }

    public Path getMarkDownFilename() {
        return markDownFilename;
    }

    public void setMarkDownFilename(Path markDownFilename) {
        this.markDownFilename = markDownFilename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "MarkdownContextHelpItem: {markDownFilename: " + markDownFilename + "; title: " + title + "}";
    }
}
