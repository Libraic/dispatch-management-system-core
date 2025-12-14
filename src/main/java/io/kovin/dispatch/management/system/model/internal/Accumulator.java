package io.kovin.dispatch.management.system.model.internal;

import java.math.BigDecimal;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;

public class Accumulator {

    private BigDecimal accumulator;

    public Accumulator() {
        accumulator = BigDecimal.ZERO;
    }

    public void reset() {
        accumulator = BigDecimal.ZERO;
    }

    public void add(BigDecimal value) {
        if (value != null) {
            accumulator = accumulator.add(value);
        }
    }

    public void add(Double value) {
        if (Double.isFinite(value)) {
            accumulator = accumulator.add(BigDecimalUtils.getBigDecimalFromDouble(value));
        }
    }

    public BigDecimal getValue() {
        return BigDecimalUtils.copy(accumulator);
    }
}
