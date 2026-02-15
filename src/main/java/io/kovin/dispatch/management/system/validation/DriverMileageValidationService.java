package io.kovin.dispatch.management.system.validation;

import ch.qos.logback.core.util.StringUtil;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.request.UpsertDriverMileageRequest;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverMileageValidationService {

    public void validateDriversMileageUpsertion(UpsertDriverMileageRequest request) {
        log.info("Validating the request to create the mileage.");
        if (StringUtil.isNullOrEmpty(request.companyUuid())) {
            throw DispatchManagementSystemException.of(ErrorMessage.COMPANY_IS_MANDATORY, HttpStatus.BAD_REQUEST);
        }

        // These validations are only for the creation of mileage.
        if (StringUtil.isNullOrEmpty(request.driverMileageUuid())) {
            if (StringUtil.isNullOrEmpty(request.dispatcherUuid())) {
                throw DispatchManagementSystemException.of(ErrorMessage.DISPATCHER_IS_MANDATORY, HttpStatus.BAD_REQUEST);
            }

            if (StringUtil.isNullOrEmpty(request.driverUuid())) {
                throw DispatchManagementSystemException.of(ErrorMessage.DRIVER_IS_MANDATORY, HttpStatus.BAD_REQUEST);
            }

            if (request.startDate() == null) {
                throw DispatchManagementSystemException.of(ErrorMessage.START_DATE_IS_MANDATORY, HttpStatus.BAD_REQUEST);
            }

            if (request.endDate() == null) {
                throw DispatchManagementSystemException.of(ErrorMessage.END_DATE_IS_MANDATORY, HttpStatus.BAD_REQUEST);
            }

            if (request.mileageDate() == null) {
                throw DispatchManagementSystemException.of(ErrorMessage.MILEAGE_DATE_IS_MANDATORY, HttpStatus.BAD_REQUEST);
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
        }

        if (BigDecimalUtils.isNegative(request.miles())) {
            throw DispatchManagementSystemException.of(ErrorMessage.NEGATIVE_MILES, HttpStatus.BAD_REQUEST);
        }

        if (BigDecimalUtils.isNegative(request.revenue())) {
            throw DispatchManagementSystemException.of(ErrorMessage.NEGATIVE_REVENUE, HttpStatus.BAD_REQUEST);
        }
    }
}
