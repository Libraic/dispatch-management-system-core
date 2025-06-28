package io.kovin.dispatch.management.system.facade;

import io.kovin.dispatch.management.system.mapper.CompanyMapper;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.request.GetCompaniesRequest;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class CompanyFacade {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final PagedResourcesAssembler<CompanyData> assembler;

    @Transactional
    public CompanyData saveCompany(CreateCompanyRequest createCompanyRequest) {
        CompanyEntity companyEntity = companyService.createCompany(createCompanyRequest);
        return companyMapper.fromCompanyEntityToCompanyData(companyEntity);
    }

    public PagedModel<EntityModel<CompanyData>> getCompanies(GetCompaniesRequest request) {
        Page<CompanyEntity> companyEntities = companyService.getCompanies(request.getPage(), request.getSize());
        Page<CompanyData> companyDataPage = companyMapper.fromCompanyEntityPageToCompanyDataPage(companyEntities);
        return assembler.toModel(companyDataPage);
    }
}
