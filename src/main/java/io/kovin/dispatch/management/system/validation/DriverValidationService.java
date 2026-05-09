package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.FIRST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_DOCUMENT_STATUS;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_DRIVER_POSITION;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LAST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.PHONE_NUMBER_IS_MANDATORY;

import ch.qos.logback.core.util.StringUtil;
import java.util.HashMap;
import java.util.Map;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemValidationException;
import io.kovin.dispatch.management.system.model.persistence.enums.DocumentStatus;
import io.kovin.dispatch.management.system.model.persistence.enums.DriverPosition;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.validation.fields.DriverField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DriverValidationService {

    public void validateDriverCreation(CreateDriverRequest request) {
        log.info("Validating the request to create the driver.");
        Map<String, String> errorFields = new HashMap<>();
        if (StringUtil.isNullOrEmpty(request.firstName())) {
            errorFields.put(DriverField.FIRST_NAME.getFieldName(), FIRST_NAME_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.lastName())) {
            errorFields.put(DriverField.LAST_NAME.getFieldName(), LAST_NAME_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.email())) {
            errorFields.put(DriverField.EMAIL.getFieldName(), EMAIL_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.phoneNumber())) {
            errorFields.put(DriverField.PHONE_NUMBER.getFieldName(), PHONE_NUMBER_IS_MANDATORY);
        }

        if (DocumentStatus.from(request.documentsStatus()) == null) {
            errorFields.put(DriverField.DOCUMENT_STATUS.getFieldName(), INVALID_DOCUMENT_STATUS);
        }

        if (DriverPosition.from(request.position()) == null) {
            errorFields.put(DriverField.POSITION.getFieldName(), INVALID_DRIVER_POSITION);
        }

        if (!errorFields.isEmpty()) {
            throw DispatchManagementSystemValidationException.ofBadRequest(errorFields);
        }
    }
}
