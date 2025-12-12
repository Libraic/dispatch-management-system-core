package io.kovin.dispatch.management.system.utils.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class that defines constants used for labels in reports within the
 * Dispatch Management System.
 * <p>
 * This class contains a set of constants representing various labels used in different metrics
 * The constants are primarily used for identifying specific data points in reporting functionality.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportConstants {

    /**
     * Represents the label used to denote a Dispatcher in the load-by-load reports.
     */
    public static final String BROKER_LABEL = "Broker";

    /**
     * Represents the label used to denote the Dispatcher signature in the load-by-load reports.
     */
    public static final String DISPATCHER_SIGNATURE_LABEL = "Signature";

    /**
     * Represents the label used to denote the Gross in the load-by-load reports.
     */
    public static final String REVENUE_LABEL = "Gross";

    /**
     * Represents the label used to denote the Miles in the load-by-load reports.
     */
    public static final String MILES_LABEL = "Miles";

    /**
     * Represents the label used to denote the Revenue/Mile in the load-by-load reports.
     */
    public static final String REVENUE_PER_MILE_LABEL = "RPM";

    /**
     * Represents the label used to denote the Gross/Week in the load-by-load reports.
     */
    public static final String GROSS_PER_WEEK_LABEL = "Gross/Week";

    /**
     * Represents the label used to denote the Miles/Week in the load-by-load reports.
     */
    public static final String MILES_PER_WEEK_LABEL = "Miles/Week";

    /**
     * Represents the label used to denote the RPM/Week in the load-by-load reports.
     */
    public static final String RPM_PER_WEEK_LABEL = "RPM/WEEK";
}
