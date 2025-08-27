package io.kovin.dispatch.management.system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BigDecimalUtils {

    public static boolean isLessOrEqualToZeroSafe(BigDecimal value) {
        if (value == null) {
            return false;
        }

        return value.compareTo(BigDecimal.ZERO) <= 0;
    }

    public static Double getDoubleFromBigDecimal(BigDecimal input) {
        return input == null ? null : input.doubleValue();
    }

    public static BigDecimal getBigDecimalFromDouble(Double input) {
        return input == null ? null : BigDecimal.valueOf(input);
    }

    public static boolean isNegative(BigDecimal input) {
        return input != null && input.signum() < 0;
    }
}
