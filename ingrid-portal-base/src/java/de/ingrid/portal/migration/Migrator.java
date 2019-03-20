/*-
 * **************************************************-
 * InGrid Portal Base
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
package de.ingrid.portal.migration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Migrator {
    private static Logger log = LogManager.getLogger(Migrator.class);

    private final String version;
    private final DataSource ds;

    public Migrator(DataSource ds, String version) {
        this.ds = ds;
        this.version = version;
    }

    public void run() {

        boolean backupPartnerProvider = needsPartnerProviderMigration();

        if (backupPartnerProvider) {
            log.info("Backup partner and provider table.");

            backupPartner();

            backupProvider();

            log.info("Patch files for Codelist-Repository have been created. Check out root dir of portal installation.");
        }

    }

    private boolean needsPartnerProviderMigration() {
        try {
            String[] splitVersion = this.version.split("\\.");
            int major = Integer.parseInt(splitVersion[0]);
            int minor = Integer.parseInt(splitVersion[1]);
            int patch = Integer.parseInt(splitVersion[2]);

            return major < 4 || (major == 4 && minor < 4) || (major == 4 && minor == 4 && patch < 1);

        } catch (Exception e) {
            log.warn("Version could not be extracted from database", e);
            // in case version was not set like on an empty database
            return true;
        }
    }

    private void backupPartner() {
        try (ResultSet resultSet = getPartner()) {

            List<Element> partners = new ArrayList<>();
            while (resultSet.next()) {
                partners.add(getPartnerEntry(resultSet));
            }

            Document doc = new Document();
            doc.addContent(new Element("list")
                    .addContent(new Element("de.ingrid.codelistHandler.model.CodeListUpdate")
                            .addContent(new Element("id").addContent("110"))
                            .addContent(new Element("type").addContent("ADD"))
                            .addContent(new Element("codelist")
                                    .addContent(new Element("id").addContent("110"))
                                    .addContent(new Element("name").addContent("partner"))
                                    .addContent(new Element("entries").addContent(partners))
                            )
                    )
            );

            XMLOutputter outer = new XMLOutputter();
            outer.setFormat(Format.getPrettyFormat());
            outer.output(doc, new OutputStreamWriter(new FileOutputStream(new File("441_partner_backup_codelist_patch.xml")), StandardCharsets.UTF_8));

        } catch (Exception e) {
            log.error("Error during migration", e);
        }
    }

    private Element getPartnerEntry(ResultSet resultSet) throws SQLException {
        return new Element("de.ingrid.codelists.model.CodeListEntry")
                .addContent(new Element("id").addContent(resultSet.getString("id")))
                .addContent(new Element("localisations")
                        .addContent(new Element("entry")
                                .addContent(new Element("string").addContent("sortkey"))
                                .addContent(new Element("string").addContent(resultSet.getString("sortkey")))
                        )
                        .addContent(new Element("entry")
                                .addContent(new Element("string").addContent("ident"))
                                .addContent(new Element("string").addContent(resultSet.getString("ident")))
                        )
                        .addContent(new Element("entry")
                                .addContent(new Element("string").addContent("name"))
                                .addContent(new Element("string").addContent(resultSet.getString("name")))
                        )
                );
    }

    private ResultSet getPartner() throws SQLException {
        try (Connection con = ds.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM ingrid_partner")) {
                return ps.executeQuery();
            }
        }
    }

    private void backupProvider() {
        try (ResultSet resultSet = getProvider()) {

            List<Element> providers = new ArrayList<>();
            while (resultSet.next()) {
                providers.add(getProviderEntry(resultSet));
            }

            Document doc = new Document();
            doc.addContent(new Element("list")
                    .addContent(new Element("de.ingrid.codelistHandler.model.CodeListUpdate")
                            .addContent(new Element("id").addContent("111"))
                            .addContent(new Element("type").addContent("ADD"))
                            .addContent(new Element("codelist")
                                    .addContent(new Element("id").addContent("111"))
                                    .addContent(new Element("name").addContent("provider"))
                                    .addContent(new Element("entries").addContent(providers))
                            )
                    )
            );

            XMLOutputter outer = new XMLOutputter();
            outer.setFormat(Format.getPrettyFormat());
            outer.output(doc, new OutputStreamWriter(new FileOutputStream(new File("441_provider_backup_codelist_patch.xml")), StandardCharsets.UTF_8));

        } catch (Exception e) {
            log.error("Error during migration", e);
        }

    }

    private ResultSet getProvider() throws SQLException {
        try (Connection con = ds.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM ingrid_provider")) {
                return ps.executeQuery();
            }
        }
    }

    private Element getProviderEntry(ResultSet resultSet) throws SQLException {
        return new Element("de.ingrid.codelists.model.CodeListEntry")
                .addContent(new Element("id").addContent(resultSet.getString("id")))
                .addContent(new Element("localisations")
                        .addContent(new Element("entry")
                                .addContent(new Element("string").addContent("sortkey"))
                                .addContent(new Element("string").addContent(resultSet.getString("sortkey")))
                        )
                        .addContent(new Element("entry")
                                .addContent(new Element("string").addContent("sortkey_partner"))
                                .addContent(new Element("string").addContent(resultSet.getString("sortkey_partner")))
                        )
                        .addContent(new Element("entry")
                                .addContent(new Element("string").addContent("ident"))
                                .addContent(new Element("string").addContent(resultSet.getString("ident")))
                        )
                        .addContent(new Element("entry")
                                .addContent(new Element("string").addContent("name"))
                                .addContent(new Element("string").addContent(resultSet.getString("name")))
                        )
                        .addContent(new Element("entry")
                                .addContent(new Element("string").addContent("url"))
                                .addContent(new Element("string").addContent(resultSet.getString("url")))
                        )
                );
    }
}
