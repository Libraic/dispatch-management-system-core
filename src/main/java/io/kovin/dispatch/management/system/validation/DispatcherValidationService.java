package io.kovin.dispatch.management.system.validation;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.exception.ImpactedField;
import io.kovin.dispatch.management.system.model.request.CreateDispatcherRequest;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DispatcherValidationService {

    public void validateDispatcherCreation(CreateDispatcherRequest request) {
        log.info("Validating the request to create the dispatcher.");
        GroupsErrors groupsErrors = new GroupsErrors();
        if (StringUtil.isNullOrEmpty(request.name())) {
            addError(groupsErrors, ImpactedField.NAME, ErrorMessage.NAME_NOT_PROVIDED);
        }

        if (StringUtil.isNullOrEmpty(request.phoneNumber())) {
            addError(groupsErrors, ImpactedField.PHONE_NUMBER, ErrorMessage.PHONE_NUMBER_IS_MANDATORY);
        }

        if (groupsErrors.hasErrors()) {
            throw DispatchManagementSystemGroupException.of(groupsErrors, BAD_REQUEST);
        }
    }

    private void addError(GroupsErrors groupsErrors, ImpactedField impactedField, String errorMessage) {
        log.error(errorMessage);
        groupsErrors.addError(impactedField, errorMessage);
    }
}
