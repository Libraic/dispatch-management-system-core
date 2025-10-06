package io.kovin.dispatch.management.system.facade;

import io.kovin.dispatch.management.system.mapper.TruckMapper;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.TruckEntity;
import io.kovin.dispatch.management.system.model.request.CreateTruckRequest;
import io.kovin.dispatch.management.system.model.response.TruckData;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.TruckService;
import io.kovin.dispatch.management.system.validation.TruckValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TruckFacade {

    private final TruckValidationService truckValidationService;
    private final TruckService truckService;
    private final CompanyService companyService;

    private final TruckMapper truckMapper;

    @Transactional
    public TruckData createTruck(CreateTruckRequest request) {
        if (request == null) {
            return new TruckData(null);
        }

        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        truckValidationService.validateTruckCreation(request);
        TruckEntity truckEntity = truckMapper.fromCreateTruckRequestToTruckEntity(request, company);
        truckService.saveTruckEntity(truckEntity);
        return new TruckData(truckEntity.getUuid());
    }
}
