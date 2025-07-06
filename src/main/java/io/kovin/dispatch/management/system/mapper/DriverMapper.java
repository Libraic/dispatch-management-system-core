package io.kovin.dispatch.management.system.mapper;

import java.util.UUID;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.enums.DocumentStatus;
import io.kovin.dispatch.management.system.model.entity.enums.DriverPosition;
import io.kovin.dispatch.management.system.model.entity.enums.TrailerType;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.DriverData;
import io.kovin.dispatch.management.system.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DriverMapper {

    private final CompanyService companyService;

    public DriverEntity fromCreateDriverRequestToDriverEntity(CreateDriverRequest request) {
        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        return DriverEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .phoneNumber(request.phoneNumber())
            .email(request.email())
            .truckNumber(request.truckNumber())
            .trailerNumber(request.trailerNumber())
            .maxLegalWeightCapacity(request.maxLegalWeightCapacity())
            .trailerType(TrailerType.from(request.trailerType()))
            .trailerLength(request.trailerLength())
            .documentStatus(DocumentStatus.from(request.documentStatus()))
            .position(DriverPosition.from(request.position()))
            .company(company)
            .build();
    }

    public DriverData fromDriverEntityToDriverData(DriverEntity driver) {
        return DriverData.builder()
            .firstName(driver.getFirstName())
            .lastName(driver.getLastName())
            .trailerNumber(driver.getTrailerNumber())
            .truckNumber(driver.getTruckNumber())
            .email(driver.getEmail())
            .phoneNumber(driver.getPhoneNumber())
            .build();
    }
}
