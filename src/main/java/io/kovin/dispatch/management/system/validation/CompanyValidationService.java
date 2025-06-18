package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.exception.ImpactedField.COMPANY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.MISSING_COMPANY_NAME;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CompanyValidationService {

    public void validateCompanyCreation(CreateCompanyRequest createCompanyRequest) {
        if (createCompanyRequest.name() == null) {
            log.error(MISSING_COMPANY_NAME);
            throw DispatchManagementSystemException.of(MISSING_COMPANY_NAME, HttpStatus.BAD_REQUEST);
        }
    }
}
