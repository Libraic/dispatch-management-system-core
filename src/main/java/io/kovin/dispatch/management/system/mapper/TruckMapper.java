package io.kovin.dispatch.management.system.mapper;

import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.TruckEntity;
import io.kovin.dispatch.management.system.model.request.CreateTruckRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
            .build();
    }
}
