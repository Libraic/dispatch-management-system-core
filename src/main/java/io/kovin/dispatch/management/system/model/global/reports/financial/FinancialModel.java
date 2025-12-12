package io.kovin.dispatch.management.system.model.global.reports.financial;

import java.util.List;

public record FinancialModel(
    KpiSubject subject,
    List<Kpi> kpis
) {
}
