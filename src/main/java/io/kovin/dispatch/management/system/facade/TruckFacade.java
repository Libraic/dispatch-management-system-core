package io.kovin.dispatch.management.system.facade;

import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.mapper.TruckMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.persistence.TruckEntity;
import io.kovin.dispatch.management.system.model.request.CreateTruckRequest;
import io.kovin.dispatch.management.system.model.response.GetTruckResponse;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.service.TruckService;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import io.kovin.dispatch.management.system.validation.TruckValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.DEFAULT_SORTING_FIELD;

@Component
@RequiredArgsConstructor
public class TruckFacade {

    private final TruckValidationService truckValidationService;
    private final TruckService truckService;
    private final CompanyService companyService;
    private final CriteriaService criteriaService;

    private final TruckMapper truckMapper;

    @Transactional
    public GetTruckResponse createTruck(CreateTruckRequest request) {
        if (request == null) {
            return GetTruckResponse.builder().build();
        }

        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        truckValidationService.validateTruckCreation(request);
        TruckEntity truckEntity = truckMapper.fromCreateTruckRequestToTruckEntity(request, company);
        truckService.saveTruckEntity(truckEntity);
        return truckMapper.fromTruckEntityToTruckData(truckEntity);
    }

    public Page<GetTruckResponse> getTrucksByCriteria(Map<String, String> queryParams, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, DEFAULT_SORTING_FIELD));
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        return criteriaService.getCollection(
            searchCriteria,
            TruckEntity.class,
            pageable,
            truckMapper::fromTruckEntityToTruckData
        );
    }
}
