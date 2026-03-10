package io.kovin.dispatch.management.system.facade;

import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.mapper.CompanyMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import io.kovin.dispatch.management.system.validation.CompanyValidationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    public List<CompanyData> getCompaniesByCriteria(Map<String, String> queryParams, int page, int size) {
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        List<CompanyEntity> companies = criteriaService.getCollection(searchCriteria, CompanyEntity.class, page, size);
        return companies.stream().map(companyMapper::fromCompanyEntityToCompanyData).toList();
    }
}
