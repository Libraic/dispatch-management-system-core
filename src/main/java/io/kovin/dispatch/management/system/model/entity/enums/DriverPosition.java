package io.kovin.dispatch.management.system.model.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DriverPosition {
    OWNER_OPERATOR("Owner Operator"),
    COMPANY_DRIVER("Company Driver");

    private final String position;

    public static DriverPosition from(String position) {
        if (position == null) {
            return null;
        }

        for (DriverPosition dp : values()) {
            if (dp.position.equals(position)) {
                return dp;
            }
        }

        return null;
    }
}
