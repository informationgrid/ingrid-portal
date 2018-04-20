package de.ingrid.mdek.dwr.services.report.uvp;

import de.ingrid.codelists.CodeListService;
import de.ingrid.mdek.beans.object.AdditionalFieldBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.dwr.services.report.Report;
import de.ingrid.mdek.dwr.services.report.ReportType;
import de.ingrid.mdek.handler.ObjectRequestHandler;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.ingrid.mdek.MdekUtils.IdcEntityOrderBy.DATE;
import static de.ingrid.mdek.MdekUtils.IdcWorkEntitiesSelectionType.PORTAL_QUICKLIST_PUBLISHED;

public class UVPReport {
    public static final String TOTAL_GROUPED = "totalGrouped";
    public static final String TOTAL_NEGATIVE = "totalNegative";
    public static final String TOTAL_POSITIVE = "totalPositive";
    private final ObjectRequestHandler objectRequestHandler;
    private final long startDate;
    private final CodeListService codelistService;

    public UVPReport(ObjectRequestHandler objectRequestHandler, CodeListService codelistService, Map parameter) {
        this.objectRequestHandler = objectRequestHandler;
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


        ObjectSearchResultBean workObjects = objectRequestHandler.getWorkObjects(PORTAL_QUICKLIST_PUBLISHED, DATE, true, 0, 10);
        Iterator<MdekDataBean> iterator = workObjects.getResultList().iterator();

        while (iterator.hasNext()) {
            MdekDataBean doc = iterator.next();
            MdekDataBean docDetail = objectRequestHandler.getObjectDetail(doc.getUuid());

            // TODO: check if document is not older than given date
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
        while (iterator.hasNext()) {
            String key = iterator.next();
            String keyValue = codelistService.getCodeListValue("9000", key, "de");
            mappedNumberResults.put(keyValue, grouped.get(key));
        }
        grouped.clear();
        grouped.putAll(mappedNumberResults);
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
                    AdditionalFieldBean additionalFieldBeanStream = catTable.stream()
                            .filter(cat -> "categoryId".equals(cat.getIdentifier()))
                            .findFirst()
                            .orElse(null);
                    if (additionalFieldBeanStream != null) {
                        uvpNumvbers.add(additionalFieldBeanStream);
                    }
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
            values.put(TOTAL_NEGATIVE, (int) values.get(TOTAL_NEGATIVE) + 1);
        }

    }

    private void handleNegativeCount(MdekDataBean doc, Map<String, Object> values) {
        // only count those of type "negative Vorprüfung"
        if (doc.getObjectClass() == 12) {
            values.put(TOTAL_POSITIVE, (int) values.get(TOTAL_POSITIVE) + 1);
        }
    }
}
