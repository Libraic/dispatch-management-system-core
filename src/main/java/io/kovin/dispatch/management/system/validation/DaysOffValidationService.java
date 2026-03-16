package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.START_DATE_IS_MANDATORY;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.request.UpsertDayOffPeriodRequest;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DaysOffValidationService {

    public void validateDaysOffPeriodUpsertion(UpsertDayOffPeriodRequest request) {
        log.info("Validating the request to upsert the days off period.");
        if (request.startDate() == null) {
            log.error(START_DATE_IS_MANDATORY);
            throw DispatchManagementSystemException.ofBadRequest(START_DATE_IS_MANDATORY);
        }

        if (request.endDate() == null) {
            log.error(ErrorMessage.END_DATE_IS_MANDATORY);
            throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.END_DATE_IS_MANDATORY);
        }

        if (request.startDate().isAfter(request.endDate())) {
            log.error(ErrorMessage.START_DATE_BEFORE_END_DATE);
            throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.START_DATE_BEFORE_END_DATE);
        }
    }
}
