package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.exception.ImpactedField.COMPANY_NAME;
import static io.kovin.dispatch.management.system.exception.ImpactedField.COMPANY_START_DATE;
import static io.kovin.dispatch.management.system.exception.ImpactedField.EMAIL;
import static io.kovin.dispatch.management.system.exception.ImpactedField.PASSWORD;
import static io.kovin.dispatch.management.system.utils.constants.DispatchManagementSystemConstants.PASSWORD_MIN_LENGTH;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IN_USE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.MISSING_COMPANY_NAME;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.MISSING_COMPANY_START_DATE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.PASSWORD_INVALID_LENGTH;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.PASSWORD_IS_MANDATORY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.model.entity.AccountEntity;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
import io.kovin.dispatch.management.system.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyValidationService {

    private final AccountService accountService;

    public void validateCompanyCreation(CreateCompanyRequest createCompanyRequest) {
        log.info("Validating the request to create the company.");
        GroupsErrors groupsErrors = new GroupsErrors();
        if (StringUtil.isNullOrEmpty(createCompanyRequest.name())) {
            log.error(MISSING_COMPANY_NAME);
            groupsErrors.addError(COMPANY_NAME, MISSING_COMPANY_NAME);
        }

        if (StringUtil.isNullOrEmpty(createCompanyRequest.startDate())) {
            log.error(MISSING_COMPANY_START_DATE);
            groupsErrors.addError(COMPANY_START_DATE, MISSING_COMPANY_START_DATE);
        }

        if (StringUtil.isNullOrEmpty(createCompanyRequest.email())) {
            log.error(EMAIL_IS_MANDATORY);
            groupsErrors.addError(EMAIL, EMAIL_IS_MANDATORY);
        } else {
            Optional<AccountEntity> account = accountService.findByUsername(createCompanyRequest.email());
            if (account.isPresent()) {
                log.error(EMAIL_IN_USE);
                groupsErrors.addError(EMAIL, EMAIL_IN_USE);
            }
        }

        if (StringUtil.isNullOrEmpty(createCompanyRequest.password())) {
            log.error(PASSWORD_IS_MANDATORY);
            groupsErrors.addError(PASSWORD, PASSWORD_IS_MANDATORY);
        }

        if (createCompanyRequest.password().length() < PASSWORD_MIN_LENGTH) {
            log.error(PASSWORD_INVALID_LENGTH);
            groupsErrors.addError(PASSWORD, PASSWORD_INVALID_LENGTH);
        }

        if (groupsErrors.hasErrors()) {
            throw DispatchManagementSystemGroupException.of(groupsErrors, BAD_REQUEST);
        }
    }
}
