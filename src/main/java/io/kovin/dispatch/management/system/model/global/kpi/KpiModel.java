package io.kovin.dispatch.management.system.model.global.kpi;

import java.util.List;

public record KpiModel(
    KpiSubject subject,
    List<Kpi> kpis
) {
}
