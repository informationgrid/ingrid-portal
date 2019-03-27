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

    private static final Logger log = LogManager.getLogger(UVPReport.class);

    private static final String TOTAL_GROUPED = "totalGrouped";
    private static final String TOTAL_NEGATIVE = "totalNegative";
    private static final String TOTAL_POSITIVE = "totalPositive";
    private static final String AVERAGE_DURATION = "averageDuration";

    private final ObjectRequestHandler objectRequestHandler;
    private final CatalogRequestHandler catalogRequestHandler;

    private final long startDate;
    private final long endDate;
    private final CodeListService codelistService;

    public UVPReport(ObjectRequestHandler objectRequestHandler, CatalogRequestHandler catalogRequestHandler, CodeListService codelistService, Map parameter) {
        this.objectRequestHandler = objectRequestHandler;
        this.catalogRequestHandler = catalogRequestHandler;
        this.codelistService = codelistService;
        this.startDate = Long.valueOf((String) parameter.get("startDate"));
        this.endDate = Long.valueOf((String) parameter.get("endDate"));
    }

    public Report create() {
        Report report = new Report(ReportType.UVP);
        Map<String, Object> values = new HashMap<>();

        // initialize report data
        values.put(TOTAL_GROUPED, new HashMap<>());
        values.put(TOTAL_NEGATIVE, 0);
        values.put(TOTAL_POSITIVE, 0);

        List<Long> durations = new ArrayList<>();

        int pageSize = 10;
        int page = 0;
        while (true) {
            ObjectSearchResultBean workObjects = objectRequestHandler.getWorkObjects(PORTAL_QUICKLIST_ALL_USERS_PUBLISHED, DATE, true, page*pageSize, pageSize);

            if (workObjects.getResultList().size() == 0) {
                break;
            }

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

                    // get duration of this dataset
                    Long duration = getDuration(docDetail);
                    if (duration != null) {
                        durations.add(duration);
                    }
                }

            }

            page++;
        }

        mapUvpNumbers((Map) values.get(TOTAL_GROUPED));

        Optional<Float> avgDuration = calculateAverageValue(durations);
        values.put(AVERAGE_DURATION, avgDuration.orElse(null));

        report.setValues(values);

        return report;
    }

    private Long getDuration(MdekDataBean docDetail) {
        AdditionalFieldBean applicationReceipt = docDetail.getAdditionalFields().stream()
                .filter(field -> "uvpApplicationReceipt".equals(field.getIdentifier()))
                .findFirst().orElse(null);

        List<AdditionalFieldBean> publicationDates = docDetail.getAdditionalFields().stream()
                .filter(field -> "UVPPhases".equals(field.getIdentifier()))
                .flatMap(phases -> {
                    if (phases.getTableRows() == null) return Stream.empty();
                    return phases.getTableRows().stream()
                            .filter(p -> "phase3".equals(p.get(0).getIdentifier()))
                            .map(i -> i.get(0));
                })
                .flatMap(p1 -> p1.getTableRows().stream()
                        .filter(f -> "approvalDate".equals(f.get(0).getIdentifier()))
                        .map(i -> i.get(0)))
                .sorted((a,b) -> {
                    if (a == null) return 1;
                    if (b == null) return -1;
                    if (Long.valueOf(a.getValue()) < Long.valueOf(b.getValue())) {
                        return -1;
                    } else {
                        return 1;
                    }
                })
                .collect(Collectors.toList());

        if (publicationDates.size() > 0 && applicationReceipt != null) {
            Long applicationReceiptValue = Long.valueOf(applicationReceipt.getValue());
            Long publicationDate = Long.valueOf(publicationDates.get(0).getValue());

            return publicationDate - applicationReceiptValue;
        }

        return null;
    }

    private Optional<Float> calculateAverageValue(List<Long> list) {
        return list.stream()
                .reduce(Long::sum)
                .map(val -> (float)val / list.size()) //calculate average value
                .map(val -> val/1000/60/60/24); // convert to days
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

        // check if one of the approval dates is in the given date range
        for (AdditionalFieldBean approvalDate : approvalDates) {
            Long approvalDateLong = Long.valueOf(approvalDate.getValue());
            if (approvalDateLong >= startDate && approvalDateLong <= endDate) {
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
        doc.getAdditionalFields().stream()
                .filter(field -> "uvpPreExaminationAccomplished".equals(field.getIdentifier()) && "true".equals(field.getValue()))
                .findFirst()
                .ifPresent(item -> values.put(TOTAL_POSITIVE, (int) values.get(TOTAL_POSITIVE) + 1));
    }

    private void handleNegativeCount(MdekDataBean doc, Map<String, Object> values) {
        // only count those of type "negative Vorprüfung"
        if (doc.getObjectClass() == 12) {
            values.put(TOTAL_NEGATIVE, (int) values.get(TOTAL_NEGATIVE) + 1);
        }
    }
}
