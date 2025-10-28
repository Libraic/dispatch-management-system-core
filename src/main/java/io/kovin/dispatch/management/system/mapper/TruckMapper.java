package io.kovin.dispatch.management.system.mapper;

import java.util.UUID;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.TruckEntity;
import io.kovin.dispatch.management.system.model.request.CreateTruckRequest;
import io.kovin.dispatch.management.system.model.response.TruckData;
import org.springframework.stereotype.Component;

@Component
public class TruckMapper {

    public TruckEntity fromCreateTruckRequestToTruckEntity(CreateTruckRequest request, CompanyEntity company) {
        return TruckEntity.builder()
            .uuid(UUID.randomUUID().toString())
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

    public TruckData fromTruckEntityToTruckData(TruckEntity truckEntity) {
        return TruckData.builder()
            .uuid(truckEntity.getUuid())
            .truckNumber(truckEntity.getTruckNumber())
            .vinNumber(truckEntity.getVinNumber())
            .truckMake(truckEntity.getTruckMake())
            .model(truckEntity.getModel())
            .fuelType(truckEntity.getFuelType())
            .createdAt(truckEntity.getCreatedAt())
            .build();
    }
}
