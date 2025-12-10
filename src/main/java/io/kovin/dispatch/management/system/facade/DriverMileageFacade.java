package io.kovin.dispatch.management.system.facade;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.COMPANY_IS_MANDATORY;

import ch.qos.logback.core.util.StringUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.mapper.DriverMileageMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.model.entity.MileageData;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.global.Mileage;
import io.kovin.dispatch.management.system.model.internal.Tuple;
import io.kovin.dispatch.management.system.model.request.DriverMileage;
import io.kovin.dispatch.management.system.model.request.UpsertDriverMileageRequest;
import io.kovin.dispatch.management.system.model.response.DriverMileageData;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.service.DriverService;
import io.kovin.dispatch.management.system.service.DriverMileageService;
import io.kovin.dispatch.management.system.service.UserService;
import io.kovin.dispatch.management.system.utils.CollectionUtils;
import io.kovin.dispatch.management.system.utils.DriverMileageUtils;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import io.kovin.dispatch.management.system.validation.DriverMileageValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverMileageFacade {

    private final DriverMileageValidationService driverMileageValidationService;

    private final CompanyService companyService;
    private final UserService userService;
    private final DriverService driverService;
    private final DriverMileageService driverMileageService;
    private final CriteriaService criteriaService;

    private final DriverMileageMapper driverMileageMapper;

    @Transactional
    public List<DriverMileageData> upsertMileage(UpsertDriverMileageRequest upsertDriverMileageRequest) {
        String companyUuid = upsertDriverMileageRequest.companyUuid();
        if (StringUtil.isNullOrEmpty(companyUuid)) {
            throw DispatchManagementSystemException.of(COMPANY_IS_MANDATORY, HttpStatus.BAD_REQUEST);
        }

        CompanyEntity company = companyService.getByUuid(companyUuid);
        List<DriverMileage> driverMileageList = upsertDriverMileageRequest.driverMileageData();
        if (CollectionUtils.isEmpty(driverMileageList)) {
            return List.of();
        }

        // Collect all the required UUIDs in a single iteration over the Drivers Mileage list.
        // Extract the required entities in a single transaction for each type of entity, so that we do not have this
        // while validating the request and during the mapping process to DriverMileageEntity objects as well.
        Tuple<List<String>, List<String>, List<String>> uuids = getAffiliatedEntitiesUuids(driverMileageList);
        Map<String, UserEntity> dispatchersMap = userService.getUsersMapByUuids(uuids.left());
        Map<String, DriverMileageEntity> mileageMap = driverMileageService.getMileageMapByUuids(uuids.middle());
        Map<String, DriverEntity> driversMap = driverService.getDriversMapByUuids(uuids.right());

        driverMileageValidationService.validateDriversMileageUpsertion(driverMileageList, dispatchersMap, driversMap);

        List<DriverMileageEntity> driverMileageEntities = getDriverMileageEntitiesFromUpsertRequest(
            driverMileageList,
            company,
            dispatchersMap,
            driversMap,
            mileageMap
        );
        List<DriverMileageEntity> savedMileageEntities = driverMileageService.saveMileageEntities(driverMileageEntities);
        LocalDate startDate = upsertDriverMileageRequest.driverMileageData().getFirst().startDate();
        List<DriverMileageEntity> currentWeekEntities = savedMileageEntities.stream()
            .filter(entity -> entity.getStartDate().equals(startDate))
            .toList();
        return driverMileageMapper.fromDriverMileageEntitiesToDriverMileageDataList(currentWeekEntities);
    }

    public List<DriverMileageData> getDriversMileageByCriteria(Map<String, String> queryParams, int page, int size) {
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        List<DriverMileageEntity> driverMileageEntities = criteriaService.getCollection(
            searchCriteria,
            DriverMileageEntity.class,
            page,
            size
        );
        log.info("Found [{}] drivers that match the search criteria.", driverMileageEntities.size());
        return driverMileageMapper.fromDriverMileageEntitiesToDriverMileageDataList(driverMileageEntities);
    }

    private Tuple<List<String>, List<String>, List<String>> getAffiliatedEntitiesUuids(List<DriverMileage> driverMileageList) {
        List<String> dispatchersUuids = new ArrayList<>();
        List<String> mileageUuids = new ArrayList<>();
        List<String> driversUuids = new ArrayList<>();
        for (DriverMileage driverMileage : driverMileageList) {
            String dispatcherUuid = driverMileage.dispatcherUuid();
            String mileageUuid = driverMileage.mileageUuid();
            String driverUuid = driverMileage.driverUuid();
            if (!StringUtil.isNullOrEmpty(dispatcherUuid)) {
                dispatchersUuids.add(dispatcherUuid);
            }

            if (!StringUtil.isNullOrEmpty(driverMileage.mileageUuid())) {
                mileageUuids.add(mileageUuid);
            }

            if (!StringUtil.isNullOrEmpty(dispatcherUuid)) {
                driversUuids.add(driverUuid);
            }
        }

        return new Tuple<>(dispatchersUuids, mileageUuids, driversUuids);
    }

    private List<DriverMileageEntity> getDriverMileageEntitiesFromUpsertRequest(
        List<DriverMileage> driverMileageList,
        CompanyEntity company,
        Map<String, UserEntity> dispatchersMap,
        Map<String, DriverEntity> driversMap,
        Map<String, DriverMileageEntity> mileageMap
    ) {
        List<DriverMileageEntity> driverMileageEntities = new ArrayList<>();
        for (DriverMileage driverMileage : driverMileageList) {
            List<Mileage> pivotedMileageList = driverMileage.mileage().subList(7, driverMileage.mileage().size());
            if (doesMileageContainData(pivotedMileageList)) {
                DriverMileageEntity nextWeekDriverMileageEntity = getNextWeekDriverMileageEntity(
                    pivotedMileageList,
                    driverMileage,
                    company,
                    dispatchersMap,
                    driversMap
                );
                driverMileageEntities.add(nextWeekDriverMileageEntity);
            }

            DriverMileageEntity entity = driverMileageMapper.createDriverMileageEntity(
                mileageMap.get(driverMileage.mileageUuid()),
                driverMileage,
                dispatchersMap.get(driverMileage.dispatcherUuid()),
                driversMap.get(driverMileage.driverUuid()),
                company
            );
            driverMileageEntities.add(entity);
        }

        return driverMileageEntities;
    }

    private DriverMileageEntity getNextWeekDriverMileageEntity(
        List<Mileage> mileageList,
        DriverMileage driverMileage,
        CompanyEntity company,
        Map<String, UserEntity> dispatchersMap,
        Map<String, DriverEntity> driversMap
    ) {
        Map<String, MileageData> nextWeekMileageData = driverMileageMapper.fromMileageDataRequestToMileageDataEntity(mileageList);
        Optional<DriverMileageEntity> driverMileageEntityOptional = driverMileageService.getByDispatcherAndDriverAndStartDateAndEndDate(
            driverMileage.dispatcherUuid(),
            driverMileage.driverUuid(),
            driverMileage.startDate().plusWeeks(1),
            driverMileage.endDate().plusWeeks(1)
        );
        if (driverMileageEntityOptional.isPresent()) {
            DriverMileageEntity driverMileageEntity = driverMileageEntityOptional.get();
            driverMileageEntity.getMileageData().putAll(nextWeekMileageData);
            return driverMileageEntity;
        }

        Map<String, MileageData> mileageDataMap = DriverMileageUtils.createTimelineFromStartDate(driverMileage.startDate().plusWeeks(2));
        mileageDataMap.putAll(nextWeekMileageData);
        return driverMileageMapper.createTransientDriverMileageEntity(
            driverMileage,
            mileageDataMap,
            company,
            dispatchersMap,
            driversMap,
            driverMileage.startDate().plusWeeks(1),
            driverMileage.endDate().plusWeeks(1)
        );
    }

    private boolean doesMileageContainData(List<Mileage> mileageList) {
        for (Mileage mileage : mileageList) {
            if (!StringUtil.isNullOrEmpty(mileage.destinationNote()) ||
                !StringUtil.isNullOrEmpty(mileage.note()) ||
                !StringUtil.isNullOrEmpty(mileage.broker()) ||
                mileage.revenue() != null ||
                mileage.miles() != null
            ) {
                return true;
            }
        }

        return false;
    }
}
