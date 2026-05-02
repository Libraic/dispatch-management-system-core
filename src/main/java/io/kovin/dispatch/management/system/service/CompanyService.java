package io.kovin.dispatch.management.system.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.repository.CompanyRepository;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;

    public void saveCompany(CompanyEntity companyEntity) {
        log.trace("Persisting company with UUID=[{}].", companyEntity.getUuid());
        companyRepository.save(companyEntity);
    }

    public void deleteCompany(UUID uuid) {
        CompanyEntity companyEntity = getByUuid(uuid);
        companyEntity.setDeletedAt(LocalDateTime.now());
        companyRepository.save(companyEntity);
    }

    /**
     * Retrieves a non-deleted {@link CompanyEntity} by its UUID.
     * If no such company is found, an exception is thrown with an appropriate error message.
     *
     * @param uuid the unique identifier of the company to be retrieved; must not be null or empty
     * @return the {@link CompanyEntity} associated with the given UUID
     * @throws DispatchManagementSystemException if no company is found with the given UUID
     */
    public CompanyEntity getByUuid(UUID uuid) {
        log.info("Retrieving the company with UUID=[{}].", uuid);
        Optional<CompanyEntity> companyEntityOptional = companyRepository.findByUuidAndDeletedAtIsNull(uuid);
        if (companyEntityOptional.isEmpty()) {
            String errorMessage = String.format(ErrorMessage.COMPANY_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, HttpStatus.NOT_FOUND);
        }

        return companyEntityOptional.get();
    }

    /**
     * Validates whether a company with the specified UUID is registered in the system.
     * <br> If the company is not found, an exception is thrown with an appropriate error message.
     *
     * @param uuid the unique identifier of the company to be validated
     *             (must not be null or empty)
     * @throws DispatchManagementSystemException if the company with the given UUID is not found
     */
    public void validateTheCompanyIsRegistered(UUID uuid) {
        log.info("Checking if the company with UUID=[{}] exists.", uuid);
        boolean isCompanyRegistered = companyRepository.existsByUuid(uuid);
        if (!isCompanyRegistered) {
            String errorMessage = String.format(ErrorMessage.COMPANY_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, HttpStatus.NOT_FOUND);
        }
    }
}
