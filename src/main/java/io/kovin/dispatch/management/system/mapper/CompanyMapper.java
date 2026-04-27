package io.kovin.dispatch.management.system.mapper;

import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.request.company.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.utils.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyEntity fromCreateCompanyRequestToCompanyEntity(CreateCompanyRequest request) {
        return CompanyEntity.builder()
            .name(request.name())
            .mcNumber(StringUtils.parseEmptyString(request.mcNumber()))
            .address(StringUtils.parseEmptyString(request.address()))
            .serviceDate(request.serviceDate())
            .startDate(request.startDate())
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
}
