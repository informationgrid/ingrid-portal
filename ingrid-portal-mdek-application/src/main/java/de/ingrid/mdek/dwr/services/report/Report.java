package de.ingrid.mdek.dwr.services.report;

import java.util.Map;

public class Report {
    private ReportType type;

    private Map<String, Object> values;

    public Report(ReportType type) {
        this.type = type;
    }

    public ReportType getType() {
        return type;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }
}
