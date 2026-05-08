package io.kovin.dispatch.management.system.facade;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.mapper.CompanyMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.request.company.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.request.company.request.UpdateCompanySettingsRequest;
import io.kovin.dispatch.management.system.model.request.company.response.GetCompanySettingsResponse;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import io.kovin.dispatch.management.system.utils.TimeUtils;
import io.kovin.dispatch.management.system.validation.CompanyValidationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_TIMEZONE;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.DEFAULT_SORTING_FIELD;

@Component
@AllArgsConstructor
public class CompanyFacade {

    private final CompanyService companyService;
    private final CriteriaService criteriaService;
    private final CompanyValidationService companyValidationService;

    private final CompanyMapper companyMapper;

    @Transactional
    public CompanyData saveCompany(CreateCompanyRequest createCompanyRequest) {
        if (createCompanyRequest == null) {
            return null;
        }

        companyValidationService.validateCompanyCreation(createCompanyRequest);
        CompanyEntity companyEntity = companyMapper.fromCreateCompanyRequestToCompanyEntity(createCompanyRequest);
        companyService.saveCompany(companyEntity);
        return companyMapper.fromCompanyEntityToCompanyData(companyEntity);
    }

    public void updateCompanySettings(UUID companyUuid, UpdateCompanySettingsRequest updateCompanySettingsRequest) {
        CompanyEntity company = companyService.getByUuid(companyUuid);
        String timezone = updateCompanySettingsRequest.timezone();
        if (!TimeUtils.isValidTimezone(timezone)) {
            throw DispatchManagementSystemException.ofBadRequest(INVALID_TIMEZONE);
        }

        company.setTimezone(timezone);
        companyService.saveCompany(company);
    }

    public GetCompanySettingsResponse getSettings(UUID companyUuid) {
        CompanyEntity company = companyService.getByUuid(companyUuid);
        return GetCompanySettingsResponse.builder()
            .timezone(company.getTimezone())
            .build();
    }

    public Page<CompanyData> getCompaniesByCriteria(Map<String, String> queryParams, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, DEFAULT_SORTING_FIELD));
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        return criteriaService.getCollection(
            searchCriteria,
            CompanyEntity.class,
            pageable,
            companyMapper::fromCompanyEntityToCompanyData
        );
    }
}
