package io.kovin.dispatch.management.system.validation;

import ch.qos.logback.core.util.StringUtil;
import java.util.List;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.request.CreateLoadLocationRequest;
import io.kovin.dispatch.management.system.model.request.UpsertLoadRequest;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;
import io.kovin.dispatch.management.system.utils.CollectionUtils;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadValidationService {

    public void validateLoadUpsertion(UpsertLoadRequest request) {
        log.info("Validating the request to create the load.");
        if (StringUtil.isNullOrEmpty(request.companyUuid())) {
            throw DispatchManagementSystemException.of(ErrorMessage.COMPANY_IS_MANDATORY, HttpStatus.BAD_REQUEST);
        }

        // These validations are only for the creation of the load.
        if (StringUtil.isNullOrEmpty(request.loadUuid())) {
            if (StringUtil.isNullOrEmpty(request.dispatcherUuid())) {
                throw DispatchManagementSystemException.of(ErrorMessage.DISPATCHER_IS_MANDATORY, HttpStatus.BAD_REQUEST);
            }

            if (StringUtil.isNullOrEmpty(request.driverUuid())) {
                throw DispatchManagementSystemException.of(ErrorMessage.DRIVER_IS_MANDATORY, HttpStatus.BAD_REQUEST);
            }

            if (request.miles() == null) {
                throw DispatchManagementSystemException.of(ErrorMessage.MILES_ARE_MANDATORY, HttpStatus.BAD_REQUEST);
            }

            if (request.revenue() == null) {
                throw DispatchManagementSystemException.of(ErrorMessage.REVENUE_IS_MANDATORY, HttpStatus.BAD_REQUEST);
            }

            if (StringUtil.isNullOrEmpty(request.broker())) {
                throw DispatchManagementSystemException.of(ErrorMessage.BROKER_IS_MANDATORY, HttpStatus.BAD_REQUEST);
            }

            validateLocations(request.locations());
        }

        if (BigDecimalUtils.isNegative(request.miles())) {
            throw DispatchManagementSystemException.of(ErrorMessage.NEGATIVE_MILES, HttpStatus.BAD_REQUEST);
        }

        if (BigDecimalUtils.isNegative(request.revenue())) {
            throw DispatchManagementSystemException.of(ErrorMessage.NEGATIVE_REVENUE, HttpStatus.BAD_REQUEST);
        }
    }

    private void validateLocations(List<CreateLoadLocationRequest> locations) {
        if (CollectionUtils.isEmpty(locations)) {
            throw DispatchManagementSystemException.of(ErrorMessage.LOCATIONS_ARE_MANDATORY, HttpStatus.BAD_REQUEST);
        }

        for (int i = 0; i < locations.size(); i++) {
            CreateLoadLocationRequest current = locations.get(i);
            if (current.date() == null) {
                throw DispatchManagementSystemException.of(ErrorMessage.LOAD_DATE_IS_MANDATORY, HttpStatus.BAD_REQUEST);
            }

            if (i > 0 && locations.get(i - 1).date().isAfter(current.date())) {
                throw DispatchManagementSystemException.of(ErrorMessage.LOCATIONS_CHRONOLOGICAL_ORDER, HttpStatus.BAD_REQUEST);
            }

            if (current.location() == null) {
                throw DispatchManagementSystemException.of(ErrorMessage.LOCATION_MANDATORY, HttpStatus.BAD_REQUEST);
            }

            if (current.label() == null) {
                throw DispatchManagementSystemException.of(ErrorMessage.LOCATION_LABEL_IS_MANDATORY, HttpStatus.BAD_REQUEST);
            }
        }
    }
}
