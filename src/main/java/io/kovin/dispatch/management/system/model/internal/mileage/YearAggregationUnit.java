package io.kovin.dispatch.management.system.model.internal.mileage;

import io.kovin.dispatch.management.system.model.internal.Pair;

import java.time.LocalDate;

public class YearAggregationUnit implements AggregationUnit {

    private final int year;

    public YearAggregationUnit(int year) {
        this.year = year;
    }

    @Override
    public AggregationUnit cloneUnit() {
        return new YearAggregationUnit(year);
    }

    @Override
    public boolean differentFrom(AggregationUnit aggregationUnit) {
        return !(aggregationUnit instanceof YearAggregationUnit yearAggregationUnit) || yearAggregationUnit.year != year;
    }

    @Override
    public Object getValue() {
        return year;
    }

    @Override
    public Pair<LocalDate, LocalDate> getFirstAndLastDayOfAggregationUnit() {
        LocalDate firstDay = LocalDate.of(year, 1, 1);
        LocalDate lastDay = LocalDate.of(year, 12, 31);
        return new Pair<>(firstDay, lastDay);
    }

    @Override
    public String getLabel() {
        return "Year " + year;
    }
}
