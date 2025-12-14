package io.kovin.dispatch.management.system.model.global.reports.loadbyload;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record LoadByLoadData(
    LocalDate start,
    LocalDate end,
    List<LoadByLoadItem<?>> loadByLoadItemsPerWindow,
    List<List<LoadByLoadItem<?>>> loadByLoadItemsPerDay
) {
}
