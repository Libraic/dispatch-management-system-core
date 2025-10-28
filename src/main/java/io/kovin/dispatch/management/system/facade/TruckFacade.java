package io.kovin.dispatch.management.system.facade;

import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.mapper.TruckMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.TruckEntity;
import io.kovin.dispatch.management.system.model.request.CreateTruckRequest;
import io.kovin.dispatch.management.system.model.response.TruckData;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.service.TruckService;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
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
    private final CriteriaService criteriaService;

    private final TruckMapper truckMapper;

    @Transactional
    public TruckData createTruck(CreateTruckRequest request) {
        if (request == null) {
            return TruckData.builder().build();
        }

        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        truckValidationService.validateTruckCreation(request);
        TruckEntity truckEntity = truckMapper.fromCreateTruckRequestToTruckEntity(request, company);
        truckService.saveTruckEntity(truckEntity);
        return truckMapper.fromTruckEntityToTruckData(truckEntity);
    }

    public List<TruckData> getTrucksByCriteria(Map<String, String> queryParams, int page, int size) {
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        List<TruckEntity> trucks = criteriaService.getCollection(searchCriteria, TruckEntity.class, page, size);
        return trucks.stream().map(truckMapper::fromTruckEntityToTruckData).toList();
    }
}
