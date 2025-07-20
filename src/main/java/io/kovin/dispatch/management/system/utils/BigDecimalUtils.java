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
}
