package io.kovin.dispatch.management.system.utils;

import static io.kovin.dispatch.management.system.utils.constants.DispatchManagementSystemConstants.BLANK_STRING;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    public static String parseEmptyString(String s) {
        return s == null || s.equals(BLANK_STRING) ? null : s;
    }
}
