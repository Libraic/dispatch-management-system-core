package io.kovin.dispatch.management.system.facade;

import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.mapper.CompanyMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class CompanyFacade {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final CriteriaService criteriaService;

    @Transactional
    public CompanyData saveCompany(CreateCompanyRequest createCompanyRequest) {
        CompanyEntity companyEntity = companyService.createCompany(createCompanyRequest);
        return companyMapper.fromCompanyEntityToCompanyData(companyEntity);
    }

    public List<CompanyData> getCompaniesByCriteria(Map<String, String> queryParams, int page, int size) {
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        List<CompanyEntity> companies = criteriaService.getCollection(searchCriteria, CompanyEntity.class, page, size);
        return companies.stream().map(companyMapper::fromCompanyEntityToCompanyData).toList();
    }
}
