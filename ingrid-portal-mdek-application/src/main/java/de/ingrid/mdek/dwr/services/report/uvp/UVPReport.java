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
package de.ingrid.mdek.dwr.services.report.uvp;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.model.CodeListEntry;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.dwr.services.QueryService;
import de.ingrid.mdek.dwr.services.report.Report;
import de.ingrid.mdek.dwr.services.report.ReportType;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.directwebremoting.io.FileTransfer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class UVPReport {

    private static final Logger log = LogManager.getLogger(UVPReport.class);

    private static final String TOTAL_GROUPED = "totalGrouped";
    private static final String TOTAL_NEGATIVE = "totalNegative";
    private static final String TOTAL_POSITIVE = "totalPositive";
    private static final String AVERAGE_DURATION = "averageDuration";

    private final CatalogRequestHandler catalogRequestHandler;

    private final long startDate;
    private final long endDate;
    private final CodeListService codelistService;
    private final QueryService queryService;

    public UVPReport(CatalogRequestHandler catalogRequestHandler, QueryService queryService, CodeListService codelistService, Map parameter) {
        this.catalogRequestHandler = catalogRequestHandler;
        this.queryService = queryService;
        this.codelistService = codelistService;
        this.startDate = Long.parseLong((String) parameter.get("startDate"));
        this.endDate = Long.parseLong((String) parameter.get("endDate"));
    }

    public Report create() throws IOException {
        Report report = new Report(ReportType.UVP);
        Map<String, Object> values = new HashMap<>();

        values.put(TOTAL_GROUPED, getTotalCountGroupedByUvpNumber());
        values.put(TOTAL_NEGATIVE, getNegativCount());
        values.put(TOTAL_POSITIVE, getPositiveCount());
        values.put(AVERAGE_DURATION, getAverageDuration());

        report.setValues(values);

        return report;
    }

    private Float getAverageDuration() throws IOException {
        FileTransfer fileTransfer = queryService.queryHQLToCSV(getAverageDurationQuery());
        List<Map<String, Long>> dateList = getDatesFromCSV(fileTransfer);
        return calculateAverageDuration(dateList);
    }

    private List<Map<String, Long>> getDatesFromCSV(FileTransfer fileTransfer) throws IOException {
        ArrayList<Map<String, Long>> list = new ArrayList<>();

        String csv = gzipFileToString(fileTransfer.getInputStream());
        String[] lines = csv.split("\r\n");
        for (int i = 1; i < lines.length; i++) {
            String[] dateLine = lines[i].split(";");
            Map<String, Long> result = new HashMap<>();
            result.put("start", Long.valueOf(dateLine[1]));
            result.put("end", Long.valueOf(dateLine[2]));
            list.add(result);
        }
        return list;
    }

    private Float calculateAverageDuration(List<Map<String, Long>> dateList) {
        return dateList.stream()
                .map(item -> item.get("end") - item.get("start"))
                .reduce(Long::sum)
                .map(val -> (float) val / dateList.size()) //calculate average value
                .map(val -> val / 1000 / 60 / 60 / 24)
                .orElse(null);
    }

    private int getPositiveCount() throws IOException {
        FileTransfer fileTransfer = queryService.queryHQLToCSV(getPositiveCountQuery());
        Integer catIdAndCount = getCountFromCSV(fileTransfer);
        if (catIdAndCount != null) return catIdAndCount;
        return 0;
    }

    private int getNegativCount() throws IOException {
        FileTransfer fileTransfer = queryService.queryHQLToCSV(getNegativCountQuery());
        Integer catIdAndCount = getCountFromCSV(fileTransfer);
        if (catIdAndCount != null) return catIdAndCount;
        return 0;
    }

    private Map<String, Object> getTotalCountGroupedByUvpNumber() throws IOException {
        FileTransfer fileTransfer = queryService.queryHQLToCSV(getCategoryQuery());
        Map<String, Object> mapFromCSV = getMapFromCSV(fileTransfer);
        mapUvpNumbers(mapFromCSV);
        return mapFromCSV;
    }

    private Map<String, Object> getMapFromCSV(FileTransfer fileTransfer) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String csv = gzipFileToString(fileTransfer.getInputStream());
        String[] lines = csv.split("\r\n");
        for (int i = 1; i < lines.length; i++) {
            String[] catIdAndCount = lines[i].split(";");
            result.put(catIdAndCount[0], Integer.valueOf(catIdAndCount[1]));
        }
        return result;
    }

    private Integer getCountFromCSV(FileTransfer fileTransfer) throws IOException {
        String csv = gzipFileToString(fileTransfer.getInputStream());
        String[] lines = csv.split("\r\n");
        if (lines.length > 1) {
            String[] catIdAndCount = lines[1].split(";");
            return Integer.parseInt(catIdAndCount[1]);
        }
        return null;
    }

    public static String gzipFileToString(InputStream inputStream) throws IOException {
        try (GZIPInputStream gzipIn = new GZIPInputStream(inputStream)) {
            String result = IOUtils.toString(gzipIn);
            if (log.isDebugEnabled()) {
                log.debug("STATISTIC: " + result);
            }
            return result;
        }
    }

    /**
     * Get Categories grouped by the total number in the given time interval.
     * Since multiple decision phases are possible we need to specify the maximum date in the given interval, which we
     * do here by sub querying.
     */
    private String getCategoryQuery() {
        return "select CATEGORYITEMS.data as Category, count(CATEGORYITEMS.data)\n" +
                "FROM ObjectNode oNode \n" +
                "JOIN oNode.t01ObjectPublished PUB \n" +
                "JOIN PUB.additionalFieldDatas CATEGORY \n" +
                "JOIN CATEGORY.additionalFieldDatas CATEGORYITEMS\n" +
                "JOIN PUB.additionalFieldDatas ADDITIONAL \n" +
                "JOIN ADDITIONAL.additionalFieldDatas ADDITIONAL2  \n" +
                "JOIN ADDITIONAL2.additionalFieldDatas DATE\n" +
                "WHERE\n" +
                "(PUB.objClass=10 OR PUB.objClass=13 OR PUB.objClass=14)\n" +
                "AND ADDITIONAL.fieldKey ='UVPPhases'\n" +
                "AND CATEGORY.fieldKey = 'uvpgCategory'\n" +
                "AND ADDITIONAL2.fieldKey = 'phase3'\n" +
                "AND DATE.fieldKey = 'approvalDate'\n" +
                "AND DATE.data >= " + this.startDate + "\n" +
                "AND DATE.data <= " + this.endDate + "\n" +
                "AND DATE.data = (SELECT max(add3.data) FROM AdditionalFieldData add1 JOIN add1.additionalFieldDatas add2 JOIN add2.additionalFieldDatas add3 WHERE ADDITIONAL.id=add1.id AND add3.data <= " + this.endDate + ")\n" +
                "GROUP BY CATEGORYITEMS.data";
    }

    /**
     * Get number of negativ documents in the given time interval.
     */
    private String getNegativCountQuery() {
        return "select PUB.objName, count(distinct NEGATIV.id)\n" +
                "FROM ObjectNode oNode \n" +
                "JOIN oNode.t01ObjectPublished PUB \n" +
                "JOIN PUB.additionalFieldDatas NEGATIV\n" +
                "WHERE\n" +
                "(PUB.objClass=12)\n" +
                "AND NEGATIV.fieldKey = 'uvpNegativeApprovalDate'\n" +
                "AND NEGATIV.data >= " + this.startDate + "\n" +
                "AND NEGATIV.data <= " + this.endDate;
    }

    /**
     * Get number of all documents which have "uvpPreExaminationAccomplished" set to true in the given time interval.
     */
    private String getPositiveCountQuery() {
        return "select PUB.objName, count(distinct POSITIV.id)\n" +
                "FROM ObjectNode oNode \n" +
                "JOIN oNode.t01ObjectPublished PUB\n" +
                "JOIN PUB.additionalFieldDatas ADDITIONAL\n" +
                "JOIN PUB.additionalFieldDatas POSITIV\n" +
                "JOIN ADDITIONAL.additionalFieldDatas PHASE\n" +
                "JOIN PHASE.additionalFieldDatas DATE\n" +
                "WHERE\n" +
                "ADDITIONAL.fieldKey = 'UVPPhases'\n" +
                "AND POSITIV.fieldKey = 'uvpPreExaminationAccomplished'\n" +
                "AND POSITIV.data= 'true'\n" +
                "AND PHASE.fieldKey = 'phase3'\n" +
                "AND DATE.fieldKey = 'approvalDate'\n" +
                "AND DATE.data >= " + this.startDate + "\n" +
                "AND DATE.data <= " + this.endDate;
    }

    /**
     * Get start and end date from a document in the given time interval. Use latest end date if there are more
     * than one.
     */
    private String getAverageDurationQuery() {
        return "select PUB.objName, ADDITIONALROOT.data, max(DATE.data)\n" +
                "FROM ObjectNode oNode \n" +
                "JOIN oNode.t01ObjectPublished PUB\n" +
                "JOIN PUB.additionalFieldDatas ADDITIONAL\n" +
                "JOIN PUB.additionalFieldDatas ADDITIONALROOT\n" +
                "JOIN ADDITIONAL.additionalFieldDatas PHASE\n" +
                "JOIN PHASE.additionalFieldDatas DATE\n" +
                "WHERE\n" +
                "ADDITIONAL.fieldKey = 'UVPPhases'\n" +
                "AND ADDITIONALROOT.fieldKey = 'uvpApplicationReceipt'\n" +
                "AND PHASE.fieldKey = 'phase3'\n" +
                "AND DATE.fieldKey = 'approvalDate'\n" +
                "AND DATE.data >= " + this.startDate + "\n" +
                "AND DATE.data <= " + this.endDate + "\n" +
                "GROUP BY PUB.id";
    }

    private void mapUvpNumbers(Map<String, Object> grouped) {
        Map<String, Object> mappedNumberResults = new HashMap<>();
        Iterator<String> iterator = grouped.keySet().iterator();
        JSONParser jsonParser = new JSONParser();

        String codelistId = getUvpCodelistId(jsonParser);

        List<CodeListEntry> uvpEntries = codelistService.getCodeList(codelistId).getEntries();

        while (iterator.hasNext()) {
            String key = iterator.next();
            // get full UVP number
            String keyValue = codelistService.getCodeListValue(codelistId, key, "de");

            // get according category to UVP id
            String data = getDataForCodelist(uvpEntries, key);
            String uvpCategory = null;
            if (data != null) {
                try {
                    uvpCategory = getUvpCategory((JSONObject) jsonParser.parse(data));
                } catch (ParseException ex) {
                    log.error("Could not parse json data from codelist", ex);
                }
            }

            mappedNumberResults.put(keyValue, new Object[]{grouped.get(key), uvpCategory});
        }
        grouped.clear();
        grouped.putAll(mappedNumberResults);
    }

    @SuppressWarnings("unchecked")
    private String getUvpCodelistId(JSONParser jsonParser) {

        List<GenericValueBean> behaviours = catalogRequestHandler.getSysGenericValues(new String[]{"BEHAVIOURS"});
        try {
            JSONArray json = (JSONArray) jsonParser.parse(behaviours.get(0).getValue());
            // get the defined category id or return default id (9000)
            return (String) json.stream()
                    .filter(item -> "uvpPhaseField".equals(((JSONObject) item).get("id")))
                    .findFirst()
                    .map(uvpPhaseField -> {
                        JSONArray params = (JSONArray) ((JSONObject) uvpPhaseField).get("params");
                        return params.stream()
                                .filter(param -> "categoryCodelist".equals(((JSONObject) param).get("id")))
                                .findFirst().map(categoryCodelist -> ((JSONObject) categoryCodelist).get("value"))
                                .orElse("9000");
                    })
                    .orElse("9000"); // default codelist for UVP

        } catch (ParseException e) {
            log.error("Error getting behaviour settings from DB", e);
        }
        return null;
    }

    private String getUvpCategory(JSONObject json) {
        return (String) json.get("cat");
    }

    private String getDataForCodelist(List<CodeListEntry> uvpEntries, String codelistName) {
        CodeListEntry uvpItem = uvpEntries.stream()
                .filter(entry -> codelistName.equals(entry.getId()))
                .findFirst()
                .orElse(null);

        if (uvpItem != null) {
            return uvpItem.getData();
        }
        return null;
    }

}
