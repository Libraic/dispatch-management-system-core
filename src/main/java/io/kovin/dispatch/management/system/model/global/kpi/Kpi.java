package io.kovin.dispatch.management.system.model.global.kpi;

import java.util.List;

public record Kpi(
    String type,
    List<KpiDiscriminator> discriminators
) {
}
