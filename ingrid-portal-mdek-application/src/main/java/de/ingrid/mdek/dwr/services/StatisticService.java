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
package de.ingrid.mdek.dwr.services;

import de.ingrid.codelists.CodeListService;
import de.ingrid.mdek.dwr.services.report.Report;
import de.ingrid.mdek.dwr.services.report.ReportType;
import de.ingrid.mdek.dwr.services.report.uvp.UVPReport;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.handler.ObjectRequestHandler;
import de.ingrid.mdek.job.MdekException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class StatisticService {

    private final static Logger log = LogManager.getLogger(StatisticService.class);

    // Injected by Spring
    private ObjectRequestHandler objectRequestHandler;

    private CatalogRequestHandler catalogRequestHandler;

    private CodeListService codelistService;

    public Report createReport(ReportType type, Map parameter) {

        try {
            if (type == ReportType.UVP) {

                return new UVPReport(objectRequestHandler, catalogRequestHandler, codelistService, parameter).create();

            } else {
                log.error("No such report type: " + type);
            }
        } catch(Exception e) {
            log.error("Error occurred during report generation", e);
            throw new MdekException(e.getMessage());
        }

        return null;
    }

    public void setObjectRequestHandler(ObjectRequestHandler objectRequestHandler) {
        this.objectRequestHandler = objectRequestHandler;
    }

    public void setCodelistService(CodeListService codelistService) {
        this.codelistService = codelistService;
    }

    public void setCatalogRequestHandler(CatalogRequestHandler catalogRequestHandler) {
        this.catalogRequestHandler = catalogRequestHandler;
    }
}
