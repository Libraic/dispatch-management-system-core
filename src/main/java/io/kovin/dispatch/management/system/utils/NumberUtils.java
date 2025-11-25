package io.kovin.dispatch.management.system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberUtils {

    public static Double getOrZero(Double input) {
        if (input == null) {
            return 0.0;
        }

        return input;
    }

    public static Double divide(Double input1, Double input2) {
        if (input1 == 0.0 || input2 == 0.0) {
            return 0.0;
        }

        return input1 / input2;
    }
}
