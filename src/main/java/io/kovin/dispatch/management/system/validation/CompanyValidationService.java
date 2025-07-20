package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.exception.ImpactedGroup.COMPANY_NAME;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.COMPANY_START_DATE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.MISSING_COMPANY_NAME;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.MISSING_COMPANY_START_DATE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CompanyValidationService {

    public void validateCompanyCreation(CreateCompanyRequest createCompanyRequest) {
        log.info("Validating the request to create the company.");
        GroupsErrors groupsErrors = new GroupsErrors();
        if (StringUtil.isNullOrEmpty(createCompanyRequest.name())) {
            log.error(MISSING_COMPANY_NAME);
            groupsErrors.addError(COMPANY_NAME, MISSING_COMPANY_NAME);
        }

        if (StringUtil.isNullOrEmpty(createCompanyRequest.startDate())) {
            String message = String.format(MISSING_COMPANY_START_DATE, createCompanyRequest.name());
            log.error(message);
            groupsErrors.addError(COMPANY_START_DATE, message);
        }

        if (groupsErrors.hasErrors()) {
            throw DispatchManagementSystemGroupException.of(groupsErrors, BAD_REQUEST);
        }
    }
}
