package io.kovin.dispatch.management.system.validation;

import ch.qos.logback.core.util.StringUtil;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.model.entity.TruckEntity;
import io.kovin.dispatch.management.system.model.request.CreateTruckRequest;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
import io.kovin.dispatch.management.system.service.TruckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static io.kovin.dispatch.management.system.exception.ImpactedField.TRUCK_NUMBER;
import static io.kovin.dispatch.management.system.exception.ImpactedField.TRUCK_WEIGHT;
import static io.kovin.dispatch.management.system.exception.ImpactedField.TRUCK_YEAR;
import static io.kovin.dispatch.management.system.exception.ImpactedField.VIN_NUMBER;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.TRUCK_NUMBER_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.TRUCK_WEIGHT_INVALID;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.TRUCK_YEAR_INVALID;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.VIN_NUMBER_EXISTS;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.VIN_NUMBER_IS_MANDATORY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class TruckValidationService {

    private final TruckService truckService;

    public void validateTruckCreation(CreateTruckRequest request) {
        GroupsErrors groupsErrors = new GroupsErrors();
        if (StringUtil.isNullOrEmpty(request.truckNumber())) {
            log.error(TRUCK_NUMBER_IS_MANDATORY);
            groupsErrors.addError(TRUCK_NUMBER, TRUCK_NUMBER_IS_MANDATORY);
        }

        String vinNumber = request.vinNumber();
        if (StringUtil.isNullOrEmpty(vinNumber)) {
            log.error(VIN_NUMBER_IS_MANDATORY);
            groupsErrors.addError(VIN_NUMBER, VIN_NUMBER_IS_MANDATORY);
        } else {
            TruckEntity truckEntity = truckService.getTruckByVinNumber(vinNumber);
            if (truckEntity != null) {
                log.error(VIN_NUMBER_EXISTS);
                groupsErrors.addError(VIN_NUMBER, VIN_NUMBER_EXISTS);
            }
        }

        if (request.truckYear() <= 0) {
            log.error(TRUCK_YEAR_INVALID);
            groupsErrors.addError(TRUCK_YEAR, TRUCK_YEAR_INVALID);
        }

        if (!Double.isFinite(request.weight()) || request.weight() <= 0) {
            log.error(TRUCK_WEIGHT_INVALID);
            groupsErrors.addError(TRUCK_WEIGHT, TRUCK_WEIGHT_INVALID);
        }

        if (groupsErrors.hasErrors()) {
            throw DispatchManagementSystemGroupException.of(groupsErrors, BAD_REQUEST);
        }
    }
}
