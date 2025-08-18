package io.kovin.dispatch.management.system.validation;

import static io.kovin.dispatch.management.system.exception.ImpactedField.COMMISSION;
import static io.kovin.dispatch.management.system.exception.ImpactedField.COMPANY;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.BIRTH_DATE;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.EMAIL;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.EMPLOYMENT_DATE;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.FIRST_NAME;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.LAST_NAME;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.PASSWORD;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.SUPERVISOR;
import static io.kovin.dispatch.management.system.exception.ImpactedGroup.WORKLOADS;
import static io.kovin.dispatch.management.system.utils.DispatchManagementSystemConstants.BLANK_STRING;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.BIRTH_DATE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.BLANK_COMPANY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_COMPANY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IN_USE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMPLOYMENT_DATE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.FIRST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LAST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.NEGATIVE_COMMISSION;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.PASSWORD_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.SUPERVISOR_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import java.util.List;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.exception.ImpactedGroup;
import io.kovin.dispatch.management.system.model.request.CreateSupervisorRequest;
import io.kovin.dispatch.management.system.model.request.CreateUserRequest;
import io.kovin.dispatch.management.system.model.request.CreateWorkloadRequest;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
import io.kovin.dispatch.management.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {

    private final UserRepository userRepository;

    public void validateUserCreation(CreateUserRequest request) {
        log.info("Validating the request to create the user.");
        GroupsErrors groupsErrors = new GroupsErrors();
        if (StringUtil.isNullOrEmpty(request.firstName())) {
            addError(groupsErrors, FIRST_NAME, FIRST_NAME_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.lastName())) {
            addError(groupsErrors, LAST_NAME, LAST_NAME_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.password())) {
            addError(groupsErrors, PASSWORD, PASSWORD_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.birthDate())) {
            addError(groupsErrors, BIRTH_DATE, BIRTH_DATE_IS_MANDATORY);
        }

        if (StringUtil.isNullOrEmpty(request.employmentDate())) {
            addError(groupsErrors, EMPLOYMENT_DATE, EMPLOYMENT_DATE_IS_MANDATORY);
        }

        validateWorkloads(request.workloads(), groupsErrors);
        validateEmail(request.email(), groupsErrors);
        validateSupervisor(request.supervisor(), groupsErrors);

        if (groupsErrors.hasErrors()) {
            throw DispatchManagementSystemGroupException.of(groupsErrors, BAD_REQUEST);
        }
    }

    private void validateWorkloads(List<CreateWorkloadRequest> workloads, GroupsErrors groupsErrors) {
        if (workloads != null) {
            for (CreateWorkloadRequest createWorkloadRequest : workloads) {
                String groupIdentifier = createWorkloadRequest.itemIdentifier();
                if (StringUtil.isNullOrEmpty(createWorkloadRequest.companyUuid())) {
                    String message = StringUtil.isNullOrEmpty(createWorkloadRequest.companyName())
                        ? BLANK_COMPANY
                        : String.format(INVALID_COMPANY, createWorkloadRequest.companyName());
                    log.error(message);
                    groupsErrors.addError(WORKLOADS, COMPANY, message, groupIdentifier);
                }

                if (createWorkloadRequest.commission() < 0) {
                    log.error(NEGATIVE_COMMISSION);
                    groupsErrors.addError(WORKLOADS, COMMISSION, NEGATIVE_COMMISSION, groupIdentifier);
                }
            }
        }
    }

    private void validateEmail(String email, GroupsErrors groupsErrors) {
        if (StringUtil.isNullOrEmpty(email)) {
            log.error(EMAIL_IS_MANDATORY);
            groupsErrors.addError(EMAIL, EMAIL_IS_MANDATORY);
        } else if (userRepository.existsByEmail(email)) {
            log.error(EMAIL_IN_USE);
            groupsErrors.addError(EMAIL, EMAIL_IN_USE);
        }
    }

    private void validateSupervisor(CreateSupervisorRequest supervisor, GroupsErrors groupsErrors) {
        if (supervisor != null) {
            String supervisorUuid = supervisor.uuid();
            String supervisorFullName = supervisor.fullName() != null ? supervisor.fullName() : BLANK_STRING;
            if (supervisorUuid == null && supervisorFullName.equals(BLANK_STRING)) {
                return;
            }

            if (supervisorUuid == null || userRepository.findByUuid(supervisorUuid).isEmpty()) {
                String errorMessage = String.format(SUPERVISOR_NOT_FOUND, supervisorFullName);
                log.error(errorMessage);
                groupsErrors.addError(SUPERVISOR, errorMessage);
            }
        }
    }

    private void addError(GroupsErrors groupsErrors, ImpactedGroup impactedGroup, String errorMessage) {
        log.error(errorMessage);
        groupsErrors.addError(impactedGroup, errorMessage);
    }
}
