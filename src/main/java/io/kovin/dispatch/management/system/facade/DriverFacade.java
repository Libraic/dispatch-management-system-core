package io.kovin.dispatch.management.system.facade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.mapper.DriverMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.TrailerEntity;
import io.kovin.dispatch.management.system.model.entity.TruckEntity;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.DriverData;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.service.DriverService;
import io.kovin.dispatch.management.system.service.TrailerService;
import io.kovin.dispatch.management.system.service.TruckService;
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
    private final CompanyService companyService;
    private final TruckService truckService;
    private final TrailerService trailerService;
    private final CriteriaService<DriverEntity> criteriaService;

    public DriverData createDriver(CreateDriverRequest request) {
        driverValidationService.validateDriverCreation(request);
        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        TruckEntity truck = getTruck(request.truckUuid());
        TrailerEntity trailer = getTrailer(request.trailerUuid());
        DriverEntity driverEntity = driverMapper.fromCreateDriverRequestToDriverEntity(request, company, trailer, truck);
        DriverEntity savedDriverEntity = driverService.saveDriver(driverEntity);
        return driverMapper.fromDriverEntityToDriverData(savedDriverEntity);
    }

    public List<DriverData> getDriversByCriteria(Map<String, String> queryParams, int page, int size) {
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        List<DriverEntity> drivers = criteriaService.getCollection(searchCriteria, DriverEntity.class, page, size);
        log.info("Found [{}] drivers that match the search criteria.", drivers.size());
        return drivers.stream().map(driverMapper::fromDriverEntityToDriverData).toList();
    }

    private TruckEntity getTruck(String truckUuid) {
        if (truckUuid == null) {
            return null;
        }

        return truckService.findTruckByUuid(truckUuid).orElse(null);
    }

    private TrailerEntity getTrailer(String trailerUuid) {
        if (trailerUuid == null) {
            return null;
        }

        return trailerService.findTrailerByUuid(trailerUuid).orElse(null);
    }
}
