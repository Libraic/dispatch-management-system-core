package io.kovin.dispatch.management.system.model.internal.load;

import io.kovin.dispatch.management.system.model.internal.Pair;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class WeekAggregationUnit implements AggregationUnit {

    private final LocalDate date;

    public WeekAggregationUnit(LocalDate date) {
        this.date = date;
    }

    @Override
    public AggregationUnit cloneUnit() {
        return new WeekAggregationUnit(date);
    }

    @Override
    public boolean differentFrom(AggregationUnit aggregationUnit) {
        if (!(aggregationUnit instanceof WeekAggregationUnit weekAggregationUnit)) {
            return false;
        }

        WeekFields weekFields = WeekFields.ISO;

        int week1 = date.get(weekFields.weekOfWeekBasedYear());
        int week2 = ((LocalDate) weekAggregationUnit.getValue()).get(weekFields.weekOfWeekBasedYear());
        int year1 = date.get(weekFields.weekBasedYear());
        int year2 = ((LocalDate) weekAggregationUnit.getValue()).get(weekFields.weekBasedYear());

        return (week1 != week2) || (year1 != year2);
    }

    @Override
    public Object getValue() {
        return date;
    }

    @Override
    public Pair<LocalDate, LocalDate> getFirstAndLastDayOfAggregationUnit() {
        LocalDate first = date.with(DayOfWeek.MONDAY);
        LocalDate last = date.with(DayOfWeek.SUNDAY);
        return new Pair<>(first, last);
    }

    @Override
    public String getLabel() {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return "Week " + date.get(weekFields.weekOfWeekBasedYear());
    }
}
