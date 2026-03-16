package io.kovin.dispatch.management.system.validation;

import ch.qos.logback.core.util.StringUtil;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.request.UpsertVehicleMaintenanceRecordRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.END_DATE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LOCATION_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.START_DATE_BEFORE_END_DATE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.START_DATE_IS_MANDATORY;

@Service
@Slf4j
public class VehicleMaintenanceValidationService {

    public void validateVehicleMaintenanceUpsertion(UpsertVehicleMaintenanceRecordRequest request) {
        log.info("Validating the request to upsert vehicle maintenance record.");
        if (StringUtil.isNullOrEmpty(request.location())) {
            log.error(LOCATION_MANDATORY);
            throw DispatchManagementSystemException.ofBadRequest(LOCATION_MANDATORY);
        }

        if (request.startDate() == null) {
            log.error(START_DATE_IS_MANDATORY);
            throw DispatchManagementSystemException.ofBadRequest(START_DATE_IS_MANDATORY);
        }

        if (request.endDate() == null) {
            log.error(END_DATE_IS_MANDATORY);
            throw DispatchManagementSystemException.ofBadRequest(END_DATE_IS_MANDATORY);
        }

        if (request.startDate().isAfter(request.endDate())) {
            log.error(START_DATE_BEFORE_END_DATE);
            throw DispatchManagementSystemException.ofBadRequest(START_DATE_BEFORE_END_DATE);
        }
    }
}
