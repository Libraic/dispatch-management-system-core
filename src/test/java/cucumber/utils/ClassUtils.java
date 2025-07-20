package cucumber.utils;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static cucumber.utils.ITConstants.DOT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassUtils {

    private static final String BASE_PACKAGE = "io.kovin.dispatch.management.system";
    private static final String MODEL_PACKAGE = BASE_PACKAGE + DOT + "model";
    private static final String RESPONSE_MODEL_PACKAGE = MODEL_PACKAGE + DOT + "response";
    private static final String ERROR_RESPONSE_MODEL_PACKAGE = RESPONSE_MODEL_PACKAGE + DOT + "error";

    private static final String DRIVER_DATA_CLASS = "DriverData";
    private static final String GROUP_ERROR_RESPONSE_CLASS = "GroupsErrorResponse";
    private static final String ERROR_RESPONSE_CLASS = "ErrorResponse";

    private static final String DRIVER_DATA_CLASS_PLAIN_NAME = "Driver Data";

    private static final String GROUPS_ERROR_RESPONSE_CLASS_PLAIN_NAME = "Groups Error Response";
    private static final String ERROR_RESPONSE_CLASS_PLAIN_NAME = "Error Response";

    /**
     * A dictionary that stores the correspondence between a plain class name and the complete Java class name in
     * the following map.
     * Add any new class that needs to be used during the execution of Cucumber tests here.
     */
    private static final Map<String, String> FULL_CLASSES_NAMES;

    static {
        FULL_CLASSES_NAMES = new HashMap<>();
        FULL_CLASSES_NAMES.put(DRIVER_DATA_CLASS_PLAIN_NAME, RESPONSE_MODEL_PACKAGE + DOT + DRIVER_DATA_CLASS);
        FULL_CLASSES_NAMES.put(GROUPS_ERROR_RESPONSE_CLASS_PLAIN_NAME, ERROR_RESPONSE_MODEL_PACKAGE + DOT + GROUP_ERROR_RESPONSE_CLASS);
        FULL_CLASSES_NAMES.put(ERROR_RESPONSE_CLASS_PLAIN_NAME, ERROR_RESPONSE_MODEL_PACKAGE + DOT + ERROR_RESPONSE_CLASS);
    }

    public static String getFullClassName(String plainName) {
        return FULL_CLASSES_NAMES.get(plainName);
    }
}
