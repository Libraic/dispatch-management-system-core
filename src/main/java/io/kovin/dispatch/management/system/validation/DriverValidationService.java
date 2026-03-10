package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.exception.ImpactedGroup.CITY;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.DOCUMENT_STATUS;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.EMAIL;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.FIRST_NAME;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.LAST_NAME;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.PHONE_NUMBER;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.POSITION;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.STATE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.CITY_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.FIRST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_DOCUMENT_STATUS;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_DRIVER_POSITION;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LAST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.PHONE_NUMBER_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.STATE_IS_MANDATORY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.exception.ImpactedGroup;
import io.kovin.dispatch.management.system.model.persistence.enums.DocumentStatus;
import io.kovin.dispatch.management.system.model.persistence.enums.DriverPosition;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
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
