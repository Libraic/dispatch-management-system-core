package io.kovin.dispatch.management.system.model.internal.mileage;

import java.time.LocalDate;
import io.kovin.dispatch.management.system.model.internal.Pair;

public class DayAggregationUnit implements AggregationUnit {

    private final LocalDate day;

    public DayAggregationUnit(LocalDate day) {
        this.day = LocalDate.from(day);
    }

    @Override
    public AggregationUnit cloneUnit() {
        return new DayAggregationUnit(LocalDate.from(day));
    }

    @Override
    public boolean differentFrom(AggregationUnit aggregationUnit) {
        return !(aggregationUnit.getValue() instanceof LocalDate localDate) || !day.equals(localDate);
    }

    @Override
    public Object getValue() {
        return day;
    }

    @Override
    public Pair<LocalDate, LocalDate> getFirstAndLastDayOfAggregationUnit() {
        return new Pair<>(day, day);
    }

    @Override
    public String getLabel() {
        String dayOfWeek = day.getDayOfWeek().name();
        return dayOfWeek.charAt(0) + dayOfWeek.substring(1, 3).toLowerCase();
    }
}
