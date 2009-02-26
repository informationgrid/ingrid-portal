package de.ingrid.mdek.beans;

import java.util.List;

import de.ingrid.mdek.services.catalog.dbconsistency.ErrorReport;

public class AnalyzeJobInfoBean extends JobInfoBean {
	private List<ErrorReport> errorReports;

	public void setErrorReports(List<ErrorReport> errorReports) {
		this.errorReports = errorReports;
	}

	public List<ErrorReport> getErrorReports() {
		return errorReports;
	}
}
