package io.kovin.dispatch.management.system.model.internal.load;

import java.time.LocalDate;

public record LoadDto(
    LocalDate date,
    String broker,
    double revenue,
    double miles
) {

    public static LoadDto creteEmptyLoad(LocalDate date) {
        return new LoadDto(date, null, 0.0, 0.0);
    }
}
