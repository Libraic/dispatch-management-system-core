package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.exception.ImpactedGroup.CITY;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.DOCUMENT_STATUS;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.EMAIL;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.FIRST_NAME;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.TRAILER_HEIGHT;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.LAST_NAME;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.MAX_LEGAL_WEIGHT_CAPACITY;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.PHONE_NUMBER;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.POSITION;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.STATE;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.TRAILER_LENGTH;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.TRAILER_NUMBER;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.TRAILER_TYPE;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.TRUCK_NUMBER;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.CITY_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.FIRST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_DOCUMENT_STATUS;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_DRIVER_POSITION;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_TRAILER_HEIGHT;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_MAX_LEGAL_WEIGHT_CAPACITY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_TRAILER_LENGTH;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_TRAILER_TYPE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LAST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.PHONE_NUMBER_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.STATE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.TRAILER_NUMBER_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.TRUCK_NUMBER_IS_MANDATORY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.exception.ImpactedGroup;
import io.kovin.dispatch.management.system.model.entity.enums.DocumentStatus;
import io.kovin.dispatch.management.system.model.entity.enums.DriverPosition;
import io.kovin.dispatch.management.system.model.entity.enums.TrailerType;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DriverValidationService {

    public void validateDriverCreation(CreateDriverRequest request) {
        log.info("Validating the request to create the driver.");
        GroupsErrors groupsErrors = new GroupsErrors();
        if (StringUtil.isNullOrEmpty(request.firstName())) {
            addError(groupsErrors, FIRST_NAME, FIRST_NAME_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.lastName())) {
            addError(groupsErrors, LAST_NAME, LAST_NAME_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.email())) {
            addError(groupsErrors, EMAIL, EMAIL_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.phoneNumber())) {
            addError(groupsErrors, PHONE_NUMBER, PHONE_NUMBER_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.trailerNumber())) {
            addError(groupsErrors, TRAILER_NUMBER, TRAILER_NUMBER_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.truckNumber())) {
            addError(groupsErrors, TRUCK_NUMBER, TRUCK_NUMBER_IS_MANDATORY);
        }

        if (request.trailerHeight() == null || BigDecimalUtils.isLessOrEqualToZeroSafe(request.trailerHeight())) {
            addError(groupsErrors, TRAILER_HEIGHT, INVALID_TRAILER_HEIGHT);
        }

        if (request.maxLegalWeightCapacity() == null || BigDecimalUtils.isLessOrEqualToZeroSafe(request.maxLegalWeightCapacity())) {
            addError(groupsErrors, MAX_LEGAL_WEIGHT_CAPACITY, INVALID_MAX_LEGAL_WEIGHT_CAPACITY);
        }

        if (TrailerType.from(request.trailerType()) == null) {
            addError(groupsErrors, TRAILER_TYPE, INVALID_TRAILER_TYPE);
        }

        if (request.trailerLength() == null || BigDecimalUtils.isLessOrEqualToZeroSafe(request.trailerLength())) {
            addError(groupsErrors, TRAILER_LENGTH, INVALID_TRAILER_LENGTH);
        }

        if (DocumentStatus.from(request.documentsStatus()) == null) {
            addError(groupsErrors, DOCUMENT_STATUS, INVALID_DOCUMENT_STATUS);
        }

        if (DriverPosition.from(request.position()) == null) {
            addError(groupsErrors, POSITION, INVALID_DRIVER_POSITION);
        }

        if (StringUtil.isNullOrEmpty(request.state())) {
            addError(groupsErrors, STATE, STATE_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.city())) {
            addError(groupsErrors, CITY, CITY_IS_MANDATORY);
        }

        if (groupsErrors.hasErrors()) {
            throw DispatchManagementSystemGroupException.of(groupsErrors, BAD_REQUEST);
        }
    }

    private void addError(GroupsErrors groupsErrors, ImpactedGroup impactedGroup, String errorMessage) {
        log.error(errorMessage);
        groupsErrors.addError(impactedGroup, errorMessage);
    }
}
