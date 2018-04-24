package de.ingrid.mdek.dwr.services.report.uvp;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.model.CodeListEntry;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.beans.object.AdditionalFieldBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.dwr.services.report.Report;
import de.ingrid.mdek.dwr.services.report.ReportType;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.handler.ObjectRequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.ingrid.mdek.MdekUtils.IdcEntityOrderBy.DATE;
import static de.ingrid.mdek.MdekUtils.IdcWorkEntitiesSelectionType.PORTAL_QUICKLIST_ALL_USERS_PUBLISHED;

public class UVPReport {

    private final static Logger log = LogManager.getLogger(UVPReport.class);

    private static final String TOTAL_GROUPED = "totalGrouped";
    private static final String TOTAL_NEGATIVE = "totalNegative";
    private static final String TOTAL_POSITIVE = "totalPositive";

    private final ObjectRequestHandler objectRequestHandler;
    private final CatalogRequestHandler catalogRequestHandler;

    private final long startDate;
    private final CodeListService codelistService;

    public UVPReport(ObjectRequestHandler objectRequestHandler, CatalogRequestHandler catalogRequestHandler, CodeListService codelistService, Map parameter) {
        this.objectRequestHandler = objectRequestHandler;
        this.catalogRequestHandler = catalogRequestHandler;
        this.codelistService = codelistService;
        this.startDate = Long.valueOf((String) parameter.get("startDate"));
    }

    public Report create() {
        Report report = new Report(ReportType.UVP);
        Map<String, Object> values = new HashMap<>();

        // initialize report data
        values.put(TOTAL_GROUPED, new HashMap<>());
        values.put(TOTAL_NEGATIVE, 0);
        values.put(TOTAL_POSITIVE, 0);


        ObjectSearchResultBean workObjects = objectRequestHandler.getWorkObjects(PORTAL_QUICKLIST_ALL_USERS_PUBLISHED, DATE, true, 0, 10);

        for (MdekDataBean doc : workObjects.getResultList()) {
            MdekDataBean docDetail = objectRequestHandler.getObjectDetail(doc.getUuid());

            // check if document is not older than given date
            if (datasetIsInDateRange(docDetail)) {
                // categorize datasets by their UVP number and count them
                handleTotalCountGroupedByUvpNumber(docDetail, values);

                // count all positive pre-examinations
                handlePositiveCount(docDetail, values);

                // count all negative pre-examinations
                handleNegativeCount(docDetail, values);
            }

        }

        mapUvpNumbers((Map) values.get(TOTAL_GROUPED));

        report.setValues(values);

        return report;
    }

    private boolean datasetIsInDateRange(MdekDataBean doc) {

        // collect all approval dates from repeatable phase 3 ("Entscheidung über die Zulassung")
        List<AdditionalFieldBean> approvalDates = doc.getAdditionalFields().stream()
                .filter(field -> "UVPPhases".equals(field.getIdentifier()))
                .flatMap(phases -> {
                    if (phases.getTableRows() == null) return Stream.empty();
                    return phases.getTableRows().stream()
                            .filter(p -> "phase3".equals(p.get(0).getIdentifier()))
                            .map(i -> i.get(0));
                })
                .flatMap(p3 -> {
                    return p3.getTableRows().stream()
                            .filter(f -> "approvalDate".equals(f.get(0).getIdentifier()))
                            .map(i -> i.get(0));
                })
                .collect(Collectors.toList());

        // add approval date from negative examinations
        doc.getAdditionalFields().stream()
                .filter(field -> "uvpNegativeApprovalDate".equals(field.getIdentifier()))
                .findFirst()
                .ifPresent(approvalDates::add);

        // check if one of the approval dates is younger than the given date
        for (AdditionalFieldBean approvalDate : approvalDates) {
            if (Long.valueOf(approvalDate.getValue()) >= startDate) {
                return true;
            }
        }
        return false;
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

            mappedNumberResults.put(keyValue, new Object[] { grouped.get(key), uvpCategory });
        }
        grouped.clear();
        grouped.putAll(mappedNumberResults);
    }

    @SuppressWarnings("unchecked")
    private String getUvpCodelistId(JSONParser jsonParser) {

        List<GenericValueBean> behaviours = catalogRequestHandler.getSysGenericValues(new String[] { "BEHAVIOURS" });
        try {
            JSONArray json = (JSONArray) jsonParser.parse(behaviours.get(0).getValue().toString());
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


    private void handleTotalCountGroupedByUvpNumber(MdekDataBean doc, Map<String, Object> values) {

        // only count for datasets of type "Zulassungsverfahren" and "Vorgelagerte Verfahren"
        if (doc.getObjectClass() == 10 || doc.getObjectClass() == 13 || doc.getObjectClass() == 14) {

            Map grouped = (Map) values.get(TOTAL_GROUPED);

            // get all UVP numbers
            AdditionalFieldBean uvpCategory = doc.getAdditionalFields().stream()
                    .filter(addField -> "uvpgCategory".equals(addField.getIdentifier()))
                    .findFirst()
                    .orElse(null);

            if (uvpCategory != null && uvpCategory.getTableRows() != null) {
                List<AdditionalFieldBean> uvpNumvbers = new ArrayList<>();

                for (List<AdditionalFieldBean> catTable : uvpCategory.getTableRows()) {
                    catTable.stream()
                            .filter(cat -> "categoryId".equals(cat.getIdentifier()))
                            .findFirst()
                            .ifPresent(uvpNumvbers::add);
                }

                for (AdditionalFieldBean uvpNumber : uvpNumvbers) {

                    String categoryValue = uvpNumber.getValue();

                    // count numbers according to their category
                    if (grouped.containsKey(categoryValue)) {
                        int num = (int) grouped.get(categoryValue);
                        grouped.put(categoryValue, ++num);
                    } else {
                        grouped.put(categoryValue, 1);
                    }
                }
            }
        }
    }

    private void handlePositiveCount(MdekDataBean doc, Map<String, Object> values) {
        // only count those who have checked "Vorprüfung durchgeführt"
        AdditionalFieldBean preExaminationAccomplished = doc.getAdditionalFields().stream()
                .filter(fields -> "".equals(fields.getIdentifier()))
                .findFirst()
                .orElse(null);

        if (preExaminationAccomplished != null && "true".equals(preExaminationAccomplished.getValue())) {
            values.put(TOTAL_POSITIVE, (int) values.get(TOTAL_POSITIVE) + 1);
        }

    }

    private void handleNegativeCount(MdekDataBean doc, Map<String, Object> values) {
        // only count those of type "negative Vorprüfung"
        if (doc.getObjectClass() == 12) {
            values.put(TOTAL_NEGATIVE, (int) values.get(TOTAL_NEGATIVE) + 1);
        }
    }
}
