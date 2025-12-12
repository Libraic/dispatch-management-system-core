package io.kovin.dispatch.management.system.model.global.reports.loadbyload;

public record LoadByLoadItem<T>(
    String label,
    T value
) {
}
