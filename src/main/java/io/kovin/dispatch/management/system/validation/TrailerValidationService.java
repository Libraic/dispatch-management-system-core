package io.kovin.dispatch.management.system.validation;

import ch.qos.logback.core.util.StringUtil;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.model.persistence.TrailerEntity;
import io.kovin.dispatch.management.system.model.request.CreateTrailerRequest;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
import io.kovin.dispatch.management.system.service.TrailerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static io.kovin.dispatch.management.system.exception.ImpactedField.EQUIPMENT_SIZE;
import static io.kovin.dispatch.management.system.exception.ImpactedField.EQUIPMENT_TYPE;
import static io.kovin.dispatch.management.system.exception.ImpactedField.MAX_WEIGHT;
import static io.kovin.dispatch.management.system.exception.ImpactedField.PALLET_CAPACITY;
import static io.kovin.dispatch.management.system.exception.ImpactedField.TRAILER_NUMBER;
import static io.kovin.dispatch.management.system.exception.ImpactedField.TRAILER_YEAR;
import static io.kovin.dispatch.management.system.exception.ImpactedField.VIN_NUMBER;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EQUIPMENT_SIZE_INVALID;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EQUIPMENT_TYPE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.MAX_WEIGHT_INVALID;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.PALLET_CAPACITY_INVALID;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.TRAILER_NUMBER_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.TRAILER_YEAR_INVALID;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.VIN_NUMBER_EXISTS;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.VIN_NUMBER_IS_MANDATORY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrailerValidationService {

    private final TrailerService trailerService;

    public void validateTrailerCreation(CreateTrailerRequest request) {
        log.info("Validating Trailer creation.");
        GroupsErrors groupsErrors = new GroupsErrors();
        if (StringUtil.isNullOrEmpty(request.trailerNumber())) {
            log.error(TRAILER_NUMBER_IS_MANDATORY);
            groupsErrors.addError(TRAILER_NUMBER, TRAILER_NUMBER_IS_MANDATORY);
        }

        String vinNumber = request.vinNumber();
        if (StringUtil.isNullOrEmpty(vinNumber)) {
            log.error(VIN_NUMBER_IS_MANDATORY);
            groupsErrors.addError(VIN_NUMBER, VIN_NUMBER_IS_MANDATORY);
        } else {
            TrailerEntity trailerEntity = trailerService.getTrailerByVinNumber(vinNumber);
            if (trailerEntity != null) {
                log.error(VIN_NUMBER_EXISTS);
                groupsErrors.addError(VIN_NUMBER, VIN_NUMBER_EXISTS);
            }
        }

        if (request.trailerYear() <= 0) {
            log.error(TRAILER_YEAR_INVALID);
            groupsErrors.addError(TRAILER_YEAR, TRAILER_YEAR_INVALID);
        }

        if (StringUtil.isNullOrEmpty(request.equipmentType())) {
            log.error(EQUIPMENT_TYPE_IS_MANDATORY);
            groupsErrors.addError(EQUIPMENT_TYPE, EQUIPMENT_TYPE_IS_MANDATORY);
        }

        if (request.equipmentSize() <= 0) {
            log.error(EQUIPMENT_SIZE_INVALID);
            groupsErrors.addError(EQUIPMENT_SIZE, EQUIPMENT_SIZE_INVALID);
        }

        if (request.palletCapacity() <= 0) {
            log.error(PALLET_CAPACITY_INVALID);
            groupsErrors.addError(PALLET_CAPACITY, PALLET_CAPACITY_INVALID);
        }

        if (request.maxWeight() <= 0) {
            log.error(EQUIPMENT_SIZE_INVALID);
            groupsErrors.addError(MAX_WEIGHT, MAX_WEIGHT_INVALID);
        }

        if (groupsErrors.hasErrors()) {
            throw DispatchManagementSystemGroupException.of(groupsErrors, BAD_REQUEST);
        }
    }
}
