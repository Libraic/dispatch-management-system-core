package io.kovin.dispatch.management.system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DispatchManagementSystemConstants {

    public static final String BLANK_STRING = "";
    public static final String BLANK_SPACE = " ";

    /**
     * The maximum value a long variable can take, namely 2^63 - 1, which is equal to 9,223,372,036,854,775,807.
     */
    public static final String LONG_MAX_VALUE = "9223372036854775807";

    /**
     * The batch size of a page.
     */
    public static final String PAGE_BATCH_SIZE = "10";
}
