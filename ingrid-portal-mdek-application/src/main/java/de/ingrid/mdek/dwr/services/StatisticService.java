package de.ingrid.mdek.dwr.services;

import de.ingrid.codelists.CodeListService;
import de.ingrid.mdek.dwr.services.report.Report;
import de.ingrid.mdek.dwr.services.report.ReportType;
import de.ingrid.mdek.dwr.services.report.uvp.UVPReport;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.handler.ObjectRequestHandler;
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

        if (type == ReportType.UVP) {

            return new UVPReport(objectRequestHandler, catalogRequestHandler, codelistService, parameter).create();

        } else {
            log.error("No such report type: " + type);
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
