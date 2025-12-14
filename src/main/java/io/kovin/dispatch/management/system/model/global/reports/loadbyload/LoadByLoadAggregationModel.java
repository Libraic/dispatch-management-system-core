package io.kovin.dispatch.management.system.model.global.reports.loadbyload;

public record LoadByLoadAggregationModel(
    String companyUuid,
    int year,
    String month
) {
}
