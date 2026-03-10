package io.kovin.dispatch.management.system.model.internal.load;

import java.time.LocalDate;
import io.kovin.dispatch.management.system.model.internal.Pair;

public interface AggregationUnit {

    AggregationUnit cloneUnit();

    boolean differentFrom(AggregationUnit aggregationUnit);

    Object getValue();

    Pair<LocalDate, LocalDate> getFirstAndLastDayOfAggregationUnit();

    String getLabel();
}
