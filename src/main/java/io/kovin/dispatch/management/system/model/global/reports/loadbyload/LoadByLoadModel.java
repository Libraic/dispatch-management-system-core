package io.kovin.dispatch.management.system.model.global.reports.loadbyload;

import java.util.List;
import io.kovin.dispatch.management.system.model.global.reports.financial.KpiSubject;

public record LoadByLoadModel(
    KpiSubject subject,
    List<LoadByLoadData> loadByLoadData
) {

}
