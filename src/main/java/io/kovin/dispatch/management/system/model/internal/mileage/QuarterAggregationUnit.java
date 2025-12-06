package io.kovin.dispatch.management.system.model.internal.mileage;

import io.kovin.dispatch.management.system.model.internal.Pair;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

public class QuarterAggregationUnit implements AggregationUnit {

    private final int year;
    private final int quarter;

    public QuarterAggregationUnit(LocalDate date) {
        year = date.getYear();
        quarter = getQuarterByMonth(date.getMonth());
    }

    private QuarterAggregationUnit(int year, int quarter) {
        this.year = year;
        this.quarter = quarter;
    }

    @Override
    public AggregationUnit cloneUnit() {
        return new QuarterAggregationUnit(year, quarter);
    }

    @Override
    public boolean differentFrom(AggregationUnit aggregationUnit) {
        return !(aggregationUnit instanceof QuarterAggregationUnit quarterAggregationUnit)
            || quarterAggregationUnit.year != year
            || quarterAggregationUnit.quarter != quarter;
    }

    @Override
    public Object getValue() {
        return quarter;
    }

    public Pair<LocalDate, LocalDate> getFirstAndLastDayOfAggregationUnit() {
        Pair<Month, Month> firstAndLastMonthOfQuarter = getFirstAndLastMonthOfQuarter();
        YearMonth firstYearMonth = YearMonth.of(LocalDate.now().getYear(), firstAndLastMonthOfQuarter.left().getValue());
        LocalDate firstDay = firstYearMonth.atDay(1);
        YearMonth lastYearMonth = YearMonth.of(LocalDate.now().getYear(), firstAndLastMonthOfQuarter.right().getValue());
        LocalDate lastDay = lastYearMonth.atEndOfMonth();
        return new Pair<>(firstDay, lastDay);
    }

    @Override
    public String getLabel() {
        return "Quarter " + quarter;
    }

    private Pair<Month, Month> getFirstAndLastMonthOfQuarter() {
        if (quarter == 1) {
            return new Pair<>(Month.JANUARY, Month.MARCH);
        } else if (quarter == 2) {
            return new Pair<>(Month.APRIL, Month.JUNE);
        } else if (quarter == 3) {
            return new Pair<>(Month.JULY, Month.SEPTEMBER);
        } else {
            return new Pair<>(Month.OCTOBER, Month.DECEMBER);
        }
    }

    private int getQuarterByMonth(Month month) {
        if (month == Month.JANUARY || month == Month.FEBRUARY || month == Month.MARCH) {
            return 1;
        } else if (month == Month.APRIL || month == Month.MAY || month == Month.JUNE) {
            return 2;
        } else if (month == Month.JULY || month == Month.AUGUST || month == Month.SEPTEMBER) {
            return 3;
        } else {
            return 4;
        }
    }
}
