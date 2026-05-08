package io.kovin.dispatch.management.system.facade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import io.kovin.dispatch.management.system.mapper.DriverMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import io.kovin.dispatch.management.system.model.persistence.DriverDispatcherRelationEntity;
import io.kovin.dispatch.management.system.model.persistence.DriverEntity;
import io.kovin.dispatch.management.system.model.persistence.TrailerEntity;
import io.kovin.dispatch.management.system.model.persistence.TruckEntity;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.DriverData;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.service.DispatcherService;
import io.kovin.dispatch.management.system.service.DriverDispatcherRelationService;
import io.kovin.dispatch.management.system.service.DriverService;
import io.kovin.dispatch.management.system.service.TrailerService;
import io.kovin.dispatch.management.system.service.TruckService;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import io.kovin.dispatch.management.system.validation.DriverValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.DEFAULT_SORTING_FIELD;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverFacade {

    private final DispatcherService dispatcherService;
    private final DriverValidationService driverValidationService;
    private final DriverService driverService;
    private final DriverMapper driverMapper;
    private final CompanyService companyService;
    private final TruckService truckService;
    private final TrailerService trailerService;
    private final CriteriaService criteriaService;
    private final DriverDispatcherRelationService driverDispatcherRelationService;

    @Transactional
    public DriverData createDriver(CreateDriverRequest request) {
        driverValidationService.validateDriverCreation(request);
        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        DispatcherEntity dispatcher = getDispatcher(request.dispatcherUuid());
        TruckEntity truck = getTruck(request.truckUuid());
        TrailerEntity trailer = getTrailer(request.trailerUuid());
        DriverEntity driverEntity = driverMapper.fromCreateDriverRequestToDriverEntity(request, company, dispatcher, trailer, truck);
        DriverEntity savedDriverEntity = driverService.saveDriver(driverEntity);

        DriverDispatcherRelationEntity driverDispatcherRelationEntity = DriverDispatcherRelationEntity.builder()
            .uuid(UUID.randomUUID())
            .company(company)
            .dispatcher(dispatcher)
            .driver(savedDriverEntity)
            .build();
        driverDispatcherRelationService.persistRelation(driverDispatcherRelationEntity);

        return driverMapper.fromDriverEntityToDriverData(savedDriverEntity);
    }

    @Transactional
    public void deleteDriver(UUID driverUuid) {
        DriverEntity driver = driverService.getByUuid(driverUuid);
        if (driver == null) {
            return;
        }

        DriverDispatcherRelationEntity relation = driverDispatcherRelationService.getRelationByDriver(driverUuid);

        LocalDateTime deletedAt = LocalDateTime.now();
        relation.setDeletedAt(deletedAt);
        driver.setDeletedAt(deletedAt);
    }

    public Page<DriverData> getDriversByCriteria(Map<String, String> queryParams, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, DEFAULT_SORTING_FIELD));
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        return criteriaService.getCollection(
            searchCriteria,
            DriverEntity.class,
            pageable,
            driverMapper::fromDriverEntityToDriverData
        );
    }

    private TruckEntity getTruck(UUID truckUuid) {
        if (truckUuid == null) {
            return null;
        }

        return truckService.findTruckByUuid(truckUuid).orElse(null);
    }

    private TrailerEntity getTrailer(UUID trailerUuid) {
        if (trailerUuid == null) {
            return null;
        }

        return trailerService.findTrailerByUuid(trailerUuid).orElse(null);
    }

    private DispatcherEntity getDispatcher(UUID dispatcherUuid) {
        if (dispatcherUuid == null) {
            return null;
        }

        return dispatcherService.getByUuid(dispatcherUuid);
    }
}
