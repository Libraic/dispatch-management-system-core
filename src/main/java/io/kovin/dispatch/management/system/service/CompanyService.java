package io.kovin.dispatch.management.system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
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

    public void deleteCompany(String uuid) {
        CompanyEntity companyEntity = getByUuid(uuid);
        companyEntity.setDeletedAt(LocalDateTime.now());
        companyRepository.save(companyEntity);
    }

    public CompanyEntity getByUuid(String uuid) {
        log.info("Retrieving the company with UUID=[{}].", uuid);
        Optional<CompanyEntity> companyEntityOptional = companyRepository.findByUuidAndDeletedAtIsNull(uuid);
        if (companyEntityOptional.isEmpty()) {
            String errorMessage = String.format(ErrorMessage.COMPANY_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, HttpStatus.NOT_FOUND);
        }

        return companyEntityOptional.get();
    }

    public List<CompanyEntity> findByUuids(List<String> uuids) {
        if (uuids == null) {
            return List.of();
        }

        return companyRepository.findByUuidIn(uuids);
    }
}
