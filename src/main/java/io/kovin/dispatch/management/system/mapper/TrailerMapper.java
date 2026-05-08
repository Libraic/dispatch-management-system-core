package io.kovin.dispatch.management.system.mapper;

import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.persistence.TrailerEntity;
import io.kovin.dispatch.management.system.model.request.CreateTrailerRequest;
import io.kovin.dispatch.management.system.model.response.GetTrailerResponse;
import org.springframework.stereotype.Component;

@Component
public class TrailerMapper {

    public TrailerEntity fromCreateTrailerRequestToTrailerEntity(CreateTrailerRequest request, CompanyEntity company) {
        return TrailerEntity.builder()
            .uuid(UUID.randomUUID())
            .trailerNumber(request.trailerNumber())
            .vinNumber(request.vinNumber())
            .trailerYear(request.trailerYear())
            .trailerMake(request.trailerMake())
            .equipmentType(request.equipmentType())
            .equipmentSize(request.equipmentSize())
            .palletCapacity(request.palletCapacity())
            .maxWeight(request.maxWeight())
            .tireSize(request.tireSize())
            .company(company)
            .build();
    }

    public GetTrailerResponse fromTrailerEntityToTrailerData(TrailerEntity trailerEntity) {
        return GetTrailerResponse.builder()
            .uuid(trailerEntity.getUuid().toString())
            .trailerNumber(trailerEntity.getTrailerNumber())
            .vinNumber(trailerEntity.getVinNumber())
            .trailerYear(trailerEntity.getTrailerYear())
            .trailerMake(trailerEntity.getTrailerMake())
            .equipmentType(trailerEntity.getEquipmentType())
            .createdAt(trailerEntity.getCreatedAt())
            .build();
    }
}
