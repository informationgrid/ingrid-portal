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
