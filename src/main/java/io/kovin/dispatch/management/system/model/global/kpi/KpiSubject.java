package io.kovin.dispatch.management.system.model.global.kpi;

import io.kovin.dispatch.management.system.model.entity.Kpiable;

public record KpiSubject(String uuid, String name) {

    public static KpiSubject fromKpiable(Kpiable kpiable) {
        return new KpiSubject(kpiable.getUuid(), kpiable.getName());
    }
}
