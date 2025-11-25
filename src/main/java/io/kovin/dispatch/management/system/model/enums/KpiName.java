package io.kovin.dispatch.management.system.model.enums;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_KPI_TYPE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.MISSING_KPIS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum KpiName {
    REVENUE("Gross", KpiType.NATIVE),
    MILES("Miles", KpiType.NATIVE),
    APM("APM", KpiType.CALCULATED);

    private final String name;
    private final KpiType kpiType;

    /**
     * Converts a list of strings, that should represent the names of the KPIs, into a list of KpiName enums.
     * @param inputs the list of KPI Names in String format.
     * @return a list of KpiName enums.
     */
    public static List<KpiName> from(List<String> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            throw DispatchManagementSystemException.of(MISSING_KPIS, BAD_REQUEST);
        }

        return inputs.stream().map(KpiName::from).toList();
    }

    private static KpiName from(String input) {
        for (KpiName kpiName : KpiName.values()) {
            if (kpiName.name.equalsIgnoreCase(input.toLowerCase())) {
                return kpiName;
            }
        }

        throw DispatchManagementSystemException.of(String.format(INVALID_KPI_TYPE, input), BAD_REQUEST);
    }
}
