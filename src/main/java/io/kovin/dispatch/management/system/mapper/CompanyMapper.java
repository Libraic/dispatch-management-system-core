package io.kovin.dispatch.management.system.mapper;

import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.utils.LocalDateUtils;
import io.kovin.dispatch.management.system.utils.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyEntity fromCreateCompanyRequestToCompanyEntity(CreateCompanyRequest request) {
        return CompanyEntity.builder()
            .name(request.name())
            .mcNumber(StringUtils.parseEmptyString(request.mcNumber()))
            .address(StringUtils.parseEmptyString(request.address()))
            .serviceDate(LocalDateUtils.parseLocalDate(request.serviceDate()))
            .startDate(LocalDateUtils.parseLocalDate(request.startDate()))
            .uuid(UUID.randomUUID().toString())
            .build();
    }

    public CompanyData fromCompanyEntityToCompanyData(CompanyEntity companyEntity) {
        return CompanyData.builder()
            .name(companyEntity.getName())
            .uuid(companyEntity.getUuid())
            .mcNumber(companyEntity.getMcNumber())
            .address(companyEntity.getAddress())
            .startDate(companyEntity.getStartDate().toString())
            .build();
    }

    public Page<CompanyData> fromCompanyEntityPageToCompanyDataPage(Page<CompanyEntity> companyEntities) {
        return companyEntities.map(this::fromCompanyEntityToCompanyData);
    }
}
