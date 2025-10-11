package io.kovin.dispatch.management.system.mapper;

import java.util.UUID;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.TrailerEntity;
import io.kovin.dispatch.management.system.model.request.CreateTrailerRequest;
import io.kovin.dispatch.management.system.model.response.TrailerData;
import org.springframework.stereotype.Component;

@Component
public class TrailerMapper {

    public TrailerEntity fromCreateTrailerRequestToTrailerEntity(CreateTrailerRequest request, CompanyEntity company) {
        return TrailerEntity.builder()
            .uuid(UUID.randomUUID().toString())
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

    public TrailerData fromTrailerEntityToTrailerData(TrailerEntity trailerEntity) {
        return TrailerData.builder()
            .uuid(trailerEntity.getUuid())
            .trailerNumber(trailerEntity.getTrailerNumber())
            .createdAt(trailerEntity.getCreatedAt())
            .build();
    }
}
