package io.kovin.dispatch.management.system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BigDecimalUtils {

    public static Double getDoubleFromBigDecimal(BigDecimal input) {
        return input == null ? null : input.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static BigDecimal getBigDecimalFromDouble(Double input) {
        return input == null ? null : BigDecimal.valueOf(input).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static boolean isNegative(BigDecimal input) {
        return input != null && input.signum() < 0;
    }

    public static boolean isEmpty(BigDecimal input) {
        return input == null || input.signum() == 0;
    }

    public static BigDecimal divide(BigDecimal input1, BigDecimal input2) {
        if (isEmpty(input1)) {
            return BigDecimal.ZERO;
        }

        if (isEmpty(input2)) {
            return BigDecimal.ZERO;
        }

        return input1.divide(input2, 6, RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal copy(BigDecimal input) {
        if (input == null) {
            return null;
        }

        return BigDecimal.valueOf(input.doubleValue());
    }
}
