package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.exception.ImpactedArea.BASIC_INFORMATION;
import static io.kovin.dispatch.management.system.exception.ImpactedField.EMAIL;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.BIRTH_DATE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.COMPANY_UUID_MISSING;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IN_USE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMPLOYMENT_DATE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.FIRST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LAST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.NEGATIVE_COMMISSION;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.PASSWORD_IS_MANDATORY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.request.CreateUserRequest;
import io.kovin.dispatch.management.system.model.request.CreateWorkloadRequest;
import io.kovin.dispatch.management.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidation {

    private final UserRepository userRepository;

    public void validateUserCreation(CreateUserRequest request) {
        if (StringUtil.isNullOrEmpty(request.firstName())) {
            throw DispatchManagementSystemException.of(FIRST_NAME_IS_MANDATORY, BAD_REQUEST);
        }

        if (StringUtil.isNullOrEmpty(request.lastName())) {
            throw DispatchManagementSystemException.of(LAST_NAME_IS_MANDATORY, BAD_REQUEST);
        }

        if (StringUtil.isNullOrEmpty(request.email())) {
            throw DispatchManagementSystemException.of(EMAIL_IS_MANDATORY, BAD_REQUEST);
        }

        if (StringUtil.isNullOrEmpty(request.password())) {
            throw DispatchManagementSystemException.of(PASSWORD_IS_MANDATORY, BAD_REQUEST);
        }

        if (StringUtil.isNullOrEmpty(request.birthDate())) {
            throw DispatchManagementSystemException.of(BIRTH_DATE_IS_MANDATORY, BAD_REQUEST);
        }

        if (StringUtil.isNullOrEmpty(request.employmentDate())) {
            throw DispatchManagementSystemException.of(EMPLOYMENT_DATE_IS_MANDATORY, BAD_REQUEST);
        }

        if (request.workloads() != null) {
            for (CreateWorkloadRequest createWorkloadRequest : request.workloads()) {
                if (StringUtil.isNullOrEmpty(createWorkloadRequest.companyUuid())) {
                    throw DispatchManagementSystemException.of(COMPANY_UUID_MISSING, BAD_REQUEST);
                }

                if (createWorkloadRequest.commission() < 0) {
                    throw DispatchManagementSystemException.of(NEGATIVE_COMMISSION, BAD_REQUEST);
                }
            }
        }

        boolean isTheEmailAlreadyInUse = userRepository.existsByEmail(request.email());
        if (isTheEmailAlreadyInUse) {
            log.error(EMAIL_IN_USE);
            throw DispatchManagementSystemException.of(EMAIL_IN_USE, BAD_REQUEST, EMAIL, BASIC_INFORMATION);
        }
    }
}
