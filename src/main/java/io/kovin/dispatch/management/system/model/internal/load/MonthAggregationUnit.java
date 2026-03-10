package io.kovin.dispatch.management.system.model.internal.load;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import io.kovin.dispatch.management.system.model.internal.Pair;

public class MonthAggregationUnit implements AggregationUnit {

    private final int year;
    private final Month month;

    public MonthAggregationUnit(LocalDate date) {
        year = date.getYear();
        month = date.getMonth();
    }

    private MonthAggregationUnit(int year, Month month) {
        this.year = year;
        this.month = month;
    }

    @Override
    public AggregationUnit cloneUnit() {
        return new MonthAggregationUnit(year, month);
    }

    @Override
    public boolean differentFrom(AggregationUnit aggregationUnit) {
        return !(aggregationUnit instanceof MonthAggregationUnit monthAggregationUnit)
            || month != monthAggregationUnit.month
            || year != monthAggregationUnit.year;
    }

    @Override
    public Month getValue() {
        return month;
    }

    @Override
    public Pair<LocalDate, LocalDate> getFirstAndLastDayOfAggregationUnit() {
        YearMonth yearMonth = YearMonth.of(year, month.getValue());
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        return new Pair<>(firstDay, lastDay);
    }

    @Override
    public String getLabel() {
        return month.name().charAt(0) + month.name().substring(1, 3).toLowerCase();
    }
}
