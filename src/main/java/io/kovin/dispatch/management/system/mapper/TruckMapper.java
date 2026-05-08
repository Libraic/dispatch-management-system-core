package io.kovin.dispatch.management.system.mapper;

import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.persistence.TruckEntity;
import io.kovin.dispatch.management.system.model.request.CreateTruckRequest;
import io.kovin.dispatch.management.system.model.response.GetTruckResponse;
import org.springframework.stereotype.Component;

@Component
public class TruckMapper {

    public TruckEntity fromCreateTruckRequestToTruckEntity(CreateTruckRequest request, CompanyEntity company) {
        return TruckEntity.builder()
            .uuid(UUID.randomUUID())
            .truckNumber(request.truckNumber())
            .vinNumber(request.vinNumber())
            .model(request.model())
            .truckYear(request.truckYear())
            .truckMake(request.truckMake())
            .fuelType(request.fuelType())
            .color(request.color())
            .weight(request.weight())
            .company(company)
            .build();
    }

    public GetTruckResponse fromTruckEntityToTruckData(TruckEntity truckEntity) {
        return GetTruckResponse.builder()
            .uuid(truckEntity.getUuid().toString())
            .truckNumber(truckEntity.getTruckNumber())
            .vinNumber(truckEntity.getVinNumber())
            .truckMake(truckEntity.getTruckMake())
            .model(truckEntity.getModel())
            .fuelType(truckEntity.getFuelType())
            .createdAt(truckEntity.getCreatedAt())
            .build();
    }
}
