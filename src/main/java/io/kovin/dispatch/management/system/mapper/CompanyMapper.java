package io.kovin.dispatch.management.system.mapper;

import java.util.UUID;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyEntity fromCreateCompanyRequestToCompanyEntity(CreateCompanyRequest request) {
        return CompanyEntity.builder()
            .name(request.name())
            .uuid(UUID.randomUUID().toString())
            .build();
    }

    public CompanyData fromCompanyEntityToCompanyData(CompanyEntity companyEntity) {
        return CompanyData.builder()
            .name(companyEntity.getName())
            .uuid(companyEntity.getUuid())
            .build();
    }
}
