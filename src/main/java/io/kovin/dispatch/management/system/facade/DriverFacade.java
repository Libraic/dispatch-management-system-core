package io.kovin.dispatch.management.system.facade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.mapper.DriverMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.DriverData;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.service.DriverService;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import io.kovin.dispatch.management.system.validation.DriverValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverFacade {

    private final DriverValidationService driverValidationService;
    private final DriverService driverService;
    private final DriverMapper driverMapper;
    private final CriteriaService<DriverEntity> criteriaService;

    public DriverData createDriver(CreateDriverRequest request) {
        driverValidationService.validateDriverCreation(request);
        DriverEntity driverEntity = driverMapper.fromCreateDriverRequestToDriverEntity(request);
        DriverEntity savedDriverEntity = driverService.saveDriver(driverEntity);
        return driverMapper.fromDriverEntityToDriverData(savedDriverEntity);
    }

    public List<DriverData> getDriversByCriteria(Map<String, String> queryParams, LocalDateTime cursor, int size) {
        LocalDateTime createdAtCursor = cursor != null ? cursor : LocalDateTime.now();
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(
            queryParams,
            createdAtCursor
        );
        List<DriverEntity> drivers = criteriaService.getCollection(searchCriteria, DriverEntity.class, size);
        log.info("Found [{}] drivers that match the search criteria.", drivers.size());
        return drivers.stream().map(driverMapper::fromDriverEntityToDriverData).toList();
    }
}
