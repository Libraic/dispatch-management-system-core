package io.kovin.dispatch.management.system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.mapper.CompanyMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.repository.CompanyRepository;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import io.kovin.dispatch.management.system.validation.CompanyValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CompanyValidationService companyValidationService;
    private final CriteriaService<CompanyEntity> criteriaService;

    public CompanyEntity createCompany(CreateCompanyRequest createCompanyRequest) {
        companyValidationService.validateCompanyCreation(createCompanyRequest);
        CompanyEntity companyEntity = companyMapper.fromCreateCompanyRequestToCompanyEntity(createCompanyRequest);
        return companyRepository.save(companyEntity);
    }


    public void deleteCompany(String uuid) {
        CompanyEntity companyEntity = getByUuid(uuid);
        companyEntity.setDeletedAt(LocalDateTime.now());
        companyRepository.save(companyEntity);
    }

    public Optional<CompanyEntity> findByUuid(String uuid) {
        return companyRepository.findByUuidAndDeletedAtIsNull(uuid);
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

    public Page<CompanyEntity> getCompanies(int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return companyRepository.findAll(page);
    }

    public List<CompanyEntity> getCompanies(Map<String, String> queryParams) {
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        return criteriaService.getCollection(searchCriteria, CompanyEntity.class);
    }
}
