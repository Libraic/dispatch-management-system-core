package io.kovin.dispatch.management.system.model.global.reports.financial;

import java.util.List;

public record Kpi(
    String type,
    List<KpiDiscriminator> discriminators
) {
}
