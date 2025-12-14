package io.kovin.dispatch.management.system.model.global.reports.financial;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.model.enums.KpiAggregationPeriod;
import io.kovin.dispatch.management.system.model.enums.KpiTargetEntity;
import io.kovin.dispatch.management.system.model.enums.KpiName;

/**
 * The FinancialAggregationModel data transfer object.
 *
 * @param companyUuid       the UUID of the company the targeted entity belongs to.
 * @param target            the entity the KPIs are calculation for.
 * @param aggregationPeriod the period the data will be aggregated for.
 * @param kpiNames          the name of the KPIs that will be calculated.
 * @param startDate         the start date of the timeframe the KPIs will be calculated for.
 * @param endDate           the end date of the timeframe the KPIs will be calculated for.
 */
public record FinancialAggregationModel(
    String companyUuid,
    KpiTargetEntity target,
    KpiAggregationPeriod aggregationPeriod,
    List<KpiName> kpiNames,
    LocalDate startDate,
    LocalDate endDate
) {
}
