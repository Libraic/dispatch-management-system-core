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
import static io.kovin.dispatch.management.system.utils.ErrorMessage.BIRTH_DATE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_COMPANY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IN_USE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMAIL_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.EMPLOYMENT_DATE_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.FIRST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LAST_NAME_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.NEGATIVE_COMMISSION;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.PASSWORD_IS_MANDATORY;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.SUPERVISOR_NOT_FOUND;
import static io.kovin.dispatch.management.system.utils.ErrorUtils.getItemsGroupFromImpactedGroupAndErrorMessage;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.qos.logback.core.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.exception.ImpactedGroup;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.request.CreateSupervisorRequest;
import io.kovin.dispatch.management.system.model.request.CreateUserRequest;
import io.kovin.dispatch.management.system.model.request.CreateWorkloadRequest;
import io.kovin.dispatch.management.system.exception.ItemError;
import io.kovin.dispatch.management.system.exception.ItemsGroup;
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
        List<ItemsGroup> itemsGroups = new ArrayList<>();
        if (StringUtil.isNullOrEmpty(request.firstName())) {
            itemsGroups.add(getItemsGroup(FIRST_NAME, FIRST_NAME_IS_MANDATORY));
        }

        if (StringUtil.isNullOrEmpty(request.lastName())) {
            itemsGroups.add(getItemsGroup(LAST_NAME, LAST_NAME_IS_MANDATORY));
        }

        if (StringUtil.isNullOrEmpty(request.password())) {
            itemsGroups.add(getItemsGroup(PASSWORD, PASSWORD_IS_MANDATORY));
        }

        if (StringUtil.isNullOrEmpty(request.birthDate())) {
            itemsGroups.add(getItemsGroup(BIRTH_DATE, BIRTH_DATE_IS_MANDATORY));
        }

        if (StringUtil.isNullOrEmpty(request.employmentDate())) {
            itemsGroups.add(getItemsGroup(EMPLOYMENT_DATE, EMPLOYMENT_DATE_IS_MANDATORY));
        }

        ItemsGroup workloadsItemsGroup = getWorkloadsValidationResult(request.workloads());
        if (workloadsItemsGroup.hasErrors()) {
            itemsGroups.add(workloadsItemsGroup);
        }

        ItemsGroup emailItemsGroup = getEmailValidationResult(request.email());
        if (emailItemsGroup.hasErrors()) {
            itemsGroups.add(emailItemsGroup);
        }

        ItemsGroup supervisorItemsGroup = getSupervisorValidationResult(request.supervisor());
        if (supervisorItemsGroup.hasErrors()) {
            itemsGroups.add(supervisorItemsGroup);
        }

        if (!itemsGroups.isEmpty()) {
            throw DispatchManagementSystemGroupException.of(itemsGroups, BAD_REQUEST);
        }
    }

    private ItemsGroup getWorkloadsValidationResult(List<CreateWorkloadRequest> workloads) {
        ItemsGroup itemsGroup = ItemsGroup.ofGroupName(WORKLOADS);
        if (workloads != null) {
            for (CreateWorkloadRequest createWorkloadRequest : workloads) {
                ItemError itemError = ItemError.ofItemIdentifier(createWorkloadRequest.itemIdentifier());
                if (StringUtil.isNullOrEmpty(createWorkloadRequest.companyUuid())) {
                    String message = String.format(INVALID_COMPANY, createWorkloadRequest.companyName());
                    log.error(message);
                    itemError.addFieldError(COMPANY, message);
                }

                if (createWorkloadRequest.commission() < 0) {
                    log.error(NEGATIVE_COMMISSION);
                    itemError.addFieldError(COMMISSION, NEGATIVE_COMMISSION);
                }

                if (itemError.hasErrors()) {
                    itemsGroup.addItemError(itemError);
                }
            }
        }

        return itemsGroup;
    }

    private ItemsGroup getEmailValidationResult(String email) {
        ItemsGroup emailItemsGroup = ItemsGroup.ofGroupName(EMAIL);
        if (StringUtil.isNullOrEmpty(email)) {
            log.error(EMAIL_IS_MANDATORY);
            emailItemsGroup.addItemError(ItemError.ofError(EMAIL_IS_MANDATORY));
        }

        boolean isTheEmailAlreadyInUse = userRepository.existsByEmail(email);
        if (isTheEmailAlreadyInUse) {
            log.error(EMAIL_IN_USE);
            emailItemsGroup.addItemError(ItemError.ofError(EMAIL_IN_USE));
        }

        return emailItemsGroup;
    }

    private ItemsGroup getSupervisorValidationResult(CreateSupervisorRequest supervisor) {
        ItemsGroup emailItemsGroup = ItemsGroup.ofGroupName(SUPERVISOR);
        if (supervisor == null) {
            return emailItemsGroup;
        }

        String supervisorUuid = supervisor.uuid();
        if (supervisorUuid == null) {
            return emailItemsGroup;
        }

        Optional<UserEntity> supervisorOptional = userRepository.findByUuid(supervisorUuid);
        if (supervisorOptional.isEmpty()) {
            String errorMessage = String.format(SUPERVISOR_NOT_FOUND, supervisor.fullName());
            log.error(errorMessage);
            emailItemsGroup.addItemError(ItemError.ofError(errorMessage));
        }

        return emailItemsGroup;
    }

    private ItemsGroup getItemsGroup(ImpactedGroup impactedGroup, String message) {
        log.error(message);
        return getItemsGroupFromImpactedGroupAndErrorMessage(impactedGroup, message);
    }
}
