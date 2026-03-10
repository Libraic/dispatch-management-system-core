package io.kovin.dispatch.management.system.mapper;

import static io.kovin.dispatch.management.system.utils.constants.DispatchManagementSystemConstants.BLANK_SPACE;

import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import io.kovin.dispatch.management.system.model.persistence.DriverEntity;
import io.kovin.dispatch.management.system.model.persistence.TrailerEntity;
import io.kovin.dispatch.management.system.model.persistence.TruckEntity;
import io.kovin.dispatch.management.system.model.persistence.enums.DocumentStatus;
import io.kovin.dispatch.management.system.model.persistence.enums.DriverPosition;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.DriverData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DriverMapper {

    public DriverEntity fromCreateDriverRequestToDriverEntity(
        CreateDriverRequest request,
        CompanyEntity company,
        DispatcherEntity dispatcher,
        TrailerEntity trailer,
        TruckEntity truck
    ) {
        return DriverEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .fullName(request.firstName() + BLANK_SPACE + request.lastName())
            .phoneNumber(request.phoneNumber())
            .email(request.email())
            .documentStatus(DocumentStatus.from(request.documentsStatus()))
            .position(DriverPosition.from(request.position()))
            .state(request.state())
            .city(request.city())
            .company(company)
            .trailer(trailer)
            .truck(truck)
            .dispatcher(dispatcher)
            .build();
    }

    public DriverData fromDriverEntityToDriverData(DriverEntity driver) {
        return DriverData.builder()
            .uuid(driver.getUuid())
            .firstName(driver.getFirstName())
            .lastName(driver.getLastName())
            .trailerNumber(Optional.ofNullable(driver.getTrailer()).map(TrailerEntity::getTrailerNumber).orElse(null))
            .truckNumber(Optional.ofNullable(driver.getTruck()).map(TruckEntity::getTruckNumber).orElse(null))
            .email(driver.getEmail())
            .phoneNumber(driver.getPhoneNumber())
            .documentsStatus(driver.getDocumentStatus().getType())
            .state(driver.getState())
            .city(driver.getCity())
            .createdAt(driver.getCreatedAt())
            .build();
    }
}
