package io.kovin.dispatch.management.system.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.kovin.dispatch.management.system.model.entity.DispatcherEntity;
import io.kovin.dispatch.management.system.model.entity.LocationData;
import io.kovin.dispatch.management.system.model.entity.enums.LoadStatus;
import io.kovin.dispatch.management.system.model.entity.enums.LocationType;
import io.kovin.dispatch.management.system.model.internal.mileage.DispatcherDto;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverDto;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverMileageDto;
import io.kovin.dispatch.management.system.model.internal.mileage.MileageDto;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.MileageData;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.model.request.CreateMileageLocationRequest;
import io.kovin.dispatch.management.system.model.request.UpsertDriverMileageRequest;
import io.kovin.dispatch.management.system.model.response.GetDriverResponse;
import io.kovin.dispatch.management.system.model.response.mileage.GetDriverMileageDataResponse;
import io.kovin.dispatch.management.system.model.response.mileage.GetLocationResponse;
import io.kovin.dispatch.management.system.model.response.mileage.GetMileageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DriverMileageObjectsCreator {

    public List<DriverMileageDto> fromDriverMileageEntitiesToDriverMileageDtos(List<DriverMileageEntity> entities) {
        return entities.stream()
            .map(driverMileageEntity -> new DriverMileageDto(
                fromDriverEntityToDriverDto(driverMileageEntity.getDriver()),
                fromUserEntityToDispatcherDto(driverMileageEntity.getDispatcher()),
                fromMileageDataToMileageDto(driverMileageEntity.getMileageData())
            )).toList();
    }

    public List<GetMileageResponse> fromDriverMileageEntityToGetMileageResponse(DriverMileageEntity driverMileageEntity) {
        if (driverMileageEntity == null || driverMileageEntity.getMileageData() == null) {
            return List.of();
        }

        return driverMileageEntity.getMileageData()
            .entrySet()
            .stream()
            .map(mileageData -> GetMileageResponse.builder()
                .date(LocalDate.parse(mileageData.getKey()))
                .miles(mileageData.getValue().getMiles())
                .revenue(mileageData.getValue().getRevenue())
                .broker(mileageData.getValue().getBroker())
                .representative(mileageData.getValue().getRepresentative())
                .loadStatus(mileageData.getValue().getCurrentLoadStatus().getStatus())
                .representativeContactNumber(mileageData.getValue().getRepresentativeContactNumber())
                .idAcrossTimeframe(mileageData.getValue().getIdAcrossTimeframe())
                .locations(mileageData.getValue()
                    .getLocations()
                    .stream()
                    .map(locationData -> new GetLocationResponse(
                        locationData.getLocation(),
                        locationData.getDate(),
                        locationData.getLocationType().getType(),
                        locationData.getOrder()
                    )).toList()
                ).build()
            ).toList();
    }

    public MileageData createCoveredMileageDatum(UpsertDriverMileageRequest request, String idAcrossTimeframe) {
        return MileageData.builder()
            .revenue(request.revenue())
            .miles(request.miles())
            .broker(request.broker())
            .representative(request.representative())
            .representativeContactNumber(request.representativeContactNumber())
            .currentLoadStatus(LoadStatus.COVERED)
            .idAcrossTimeframe(idAcrossTimeframe)
            .build();
    }

    public LocationData fromCreateMileageLocationRequestToLocationData(CreateMileageLocationRequest request, int order) {
        return LocationData.builder()
            .date(request.date())
            .location(request.location())
            .locationType(LocationType.from(request.label()))
            .order(order)
            .build();
    }

    public MileageData createTransitMileageDatum(String idAcrossTimeframe) {
        return MileageData.builder()
            .currentLoadStatus(LoadStatus.TRANSIT)
            .idAcrossTimeframe(idAcrossTimeframe)
            .build();
    }

    public MileageData createEmptyMileageDatum(String idAcrossTimeframe) {
        return MileageData.builder()
            .currentLoadStatus(LoadStatus.EMPTY)
            .revenue(BigDecimal.ZERO)
            .miles(BigDecimal.ZERO)
            .idAcrossTimeframe(idAcrossTimeframe)
            .build();
    }

    public GetDriverMileageDataResponse createGetDriverMileageDataResponse(
        DriverMileageEntity driverMileageEntity,
        DriverEntity driver
    ) {
        GetDriverResponse getDriverResponse = GetDriverResponse.builder()
            .fullName(driver.getFullName())
            .uuid(driver.getUuid())
            .build();

        List<GetMileageResponse> getMileageResponses = fromDriverMileageEntityToGetMileageResponse(
            driverMileageEntity
        );
        return GetDriverMileageDataResponse.builder()
            .driverMileageUuid(driverMileageEntity != null ? driverMileageEntity.getUuid() : null)
            .driver(getDriverResponse)
            .mileage(getMileageResponses)
            .build();
    }

    public List<GetDriverMileageDataResponse> createGetDriverMileageDataResponses(List<DriverEntity> driversWithoutDispatchers) {
        List<GetDriverMileageDataResponse> driversWithoutDispatchersMileageDataResponses = new ArrayList<>();
        for (DriverEntity driver : driversWithoutDispatchers) {
            driversWithoutDispatchersMileageDataResponses.add(GetDriverMileageDataResponse.builder()
                .driver(GetDriverResponse.builder().uuid(driver.getUuid()).fullName(driver.getFullName()).build())
                .mileage(List.of())
                .build()
            );
        }
        return driversWithoutDispatchersMileageDataResponses;
    }

    private DriverDto fromDriverEntityToDriverDto(DriverEntity driver) {
        return new DriverDto(driver.getUuid(), driver.getFullName());
    }

    private DispatcherDto fromUserEntityToDispatcherDto(DispatcherEntity dispatcher) {
        return new DispatcherDto(dispatcher.getUuid(), dispatcher.getName());
    }

    private List<MileageDto> fromMileageDataToMileageDto(Map<String, MileageData> mileageDataMap) {
        return mileageDataMap.entrySet()
            .stream()
            .map(mileageDataEntry -> new MileageDto(
                LocalDate.parse(mileageDataEntry.getKey()),
                mileageDataEntry.getValue().getBroker(),
                mileageDataEntry.getValue().getRevenue().doubleValue(),
                mileageDataEntry.getValue().getMiles().doubleValue()
            )).sorted(Comparator.comparing(MileageDto::date))
            .toList();
    }
}
