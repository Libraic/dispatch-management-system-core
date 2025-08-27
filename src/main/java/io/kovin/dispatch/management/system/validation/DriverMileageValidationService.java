package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.exception.ImpactedField.DISPATCHER;
import static io.kovin.dispatch.management.system.exception.ImpactedField.DRIVER;
import static io.kovin.dispatch.management.system.exception.ImpactedField.MILES;
import static io.kovin.dispatch.management.system.exception.ImpactedField.REVENUE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.DISPATCHER_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.DISPATCHER_NOT_FOUND;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.DRIVER_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.DRIVER_NOT_FOUND;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.NEGATIVE_MILES;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.NEGATIVE_REVENUE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.global.Mileage;
import io.kovin.dispatch.management.system.model.request.DriverMileage;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverMileageValidationService {

    public void validateDriversMileageUpsertion(
        List<DriverMileage> driverMileageList,
        Map<String, UserEntity> dispatchersMap,
        Map<String, DriverEntity> driversMap
    ) {
        log.info("Validating the request to create the mileage.");
        GroupsErrors groupsErrors = new GroupsErrors();
        for (DriverMileage driverMileage : driverMileageList) {
            String itemIdentifier = driverMileage.itemIdentifier();
            validateDriver(driverMileage.driverUuid(), itemIdentifier, driversMap, groupsErrors);
            validateDispatcher(driverMileage.dispatcherUuid(), itemIdentifier, dispatchersMap, groupsErrors);
            validateMileage(driverMileage.mileage(), itemIdentifier, groupsErrors);
        }

        if (groupsErrors.hasErrors()) {
            throw DispatchManagementSystemGroupException.of(groupsErrors, BAD_REQUEST);
        }
    }

    private void validateDriver(
        String driverUuid,
        String itemIdentifier,
        Map<String, DriverEntity> driversMap,
        GroupsErrors groupsErrors
    ) {
        if (StringUtil.isNullOrEmpty(driverUuid)) {
            log.error(DRIVER_IS_MANDATORY);
            groupsErrors.addError(itemIdentifier, DRIVER, DRIVER_IS_MANDATORY, null);
        } else if (driversMap.get(driverUuid) == null) {
            log.error(DRIVER_NOT_FOUND);
            groupsErrors.addError(itemIdentifier, DRIVER, DRIVER_NOT_FOUND, null);
        }
    }

    private void validateDispatcher(
        String dispatcherUuid,
        String itemIdentifier,
        Map<String, UserEntity> dispatchersMap,
        GroupsErrors groupsErrors
    ) {
        if (StringUtil.isNullOrEmpty(dispatcherUuid)) {
            log.error(DISPATCHER_IS_MANDATORY);
            groupsErrors.addError(itemIdentifier, DISPATCHER, DISPATCHER_IS_MANDATORY, null);
        } else if (dispatchersMap.get(dispatcherUuid) == null) {
            log.error(DISPATCHER_NOT_FOUND);
            groupsErrors.addError(itemIdentifier, DISPATCHER, DISPATCHER_NOT_FOUND, null);
        }
    }

    private void validateMileage(List<Mileage> mileageList, String itemIdentifier, GroupsErrors groupsErrors) {
        for (Mileage mileage : mileageList) {
            if (BigDecimalUtils.isNegative(mileage.revenue())) {
                log.error(NEGATIVE_REVENUE);
                groupsErrors.addError(itemIdentifier, REVENUE, NEGATIVE_REVENUE, mileage.date());
            }
            if (BigDecimalUtils.isNegative(mileage.miles())) {
                log.error(NEGATIVE_MILES);
                groupsErrors.addError(itemIdentifier, MILES, NEGATIVE_MILES, mileage.date());
            }
        }
    }
}
