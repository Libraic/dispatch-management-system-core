package io.kovin.dispatch.management.system.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import io.kovin.dispatch.management.system.model.persistence.jsonb.LocationData;
import io.kovin.dispatch.management.system.model.persistence.enums.LoadStatus;
import io.kovin.dispatch.management.system.model.persistence.enums.LocationType;
import io.kovin.dispatch.management.system.model.persistence.DriverEntity;
import io.kovin.dispatch.management.system.model.persistence.jsonb.LoadData;
import io.kovin.dispatch.management.system.model.persistence.LoadEntity;
import io.kovin.dispatch.management.system.model.request.CreateLoadLocationRequest;
import io.kovin.dispatch.management.system.model.request.UpsertLoadRequest;
import io.kovin.dispatch.management.system.model.response.GetDriverResponse;
import io.kovin.dispatch.management.system.model.response.load.GetDriverLoadsResponse;
import io.kovin.dispatch.management.system.model.response.load.GetLocationResponse;
import io.kovin.dispatch.management.system.model.response.load.GetLoadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LoadObjectsCreator {

    public List<GetLoadResponse> fromLoadEntityToGetLoadResponse(LoadEntity loadEntity) {
        if (loadEntity == null || loadEntity.getLoadData() == null) {
            return List.of();
        }

        return loadEntity.getLoadData()
            .entrySet()
            .stream()
            .map(loadData -> GetLoadResponse.builder()
                .date(LocalDate.parse(loadData.getKey()))
                .miles(loadData.getValue().getMiles())
                .revenue(loadData.getValue().getRevenue())
                .broker(loadData.getValue().getBroker())
                .representative(loadData.getValue().getRepresentative())
                .loadStatus(loadData.getValue().getCurrentLoadStatus().getStatus())
                .representativeContactNumber(loadData.getValue().getRepresentativeContactNumber())
                .idAcrossTimeframe(loadData.getValue().getIdAcrossTimeframe())
                .locations(loadData.getValue()
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

    public LoadData createCoveredLoadDatum(UpsertLoadRequest request, String idAcrossTimeframe) {
        return LoadData.builder()
            .revenue(request.revenue())
            .miles(request.miles())
            .broker(request.broker())
            .representative(request.representative())
            .representativeContactNumber(request.representativeContactNumber())
            .currentLoadStatus(LoadStatus.COVERED)
            .idAcrossTimeframe(idAcrossTimeframe)
            .build();
    }

    public LocationData fromCreateLoadLocationRequestToLocationData(CreateLoadLocationRequest request, int order) {
        return LocationData.builder()
            .date(request.date())
            .location(request.location())
            .locationType(LocationType.from(request.label()))
            .order(order)
            .build();
    }

    public LoadData createTransitLoadDatum(String idAcrossTimeframe) {
        return LoadData.builder()
            .currentLoadStatus(LoadStatus.TRANSIT)
            .idAcrossTimeframe(idAcrossTimeframe)
            .build();
    }

    public LoadData createEmptyLoadDatum(String idAcrossTimeframe) {
        return LoadData.builder()
            .currentLoadStatus(LoadStatus.EMPTY)
            .revenue(BigDecimal.ZERO)
            .miles(BigDecimal.ZERO)
            .idAcrossTimeframe(idAcrossTimeframe)
            .build();
    }

    public GetDriverLoadsResponse createGetDriverLoadsResponse(
        LoadEntity loadEntity,
        DriverEntity driver
    ) {
        GetDriverResponse getDriverResponse = GetDriverResponse.builder()
            .fullName(driver.getFullName())
            .uuid(driver.getUuid())
            .build();

        List<GetLoadResponse> getLoadResponse = fromLoadEntityToGetLoadResponse(loadEntity);
        return GetDriverLoadsResponse.builder()
            .loadUuid(loadEntity != null ? loadEntity.getUuid() : null)
            .driver(getDriverResponse)
            .loads(getLoadResponse)
            .build();
    }

    public List<GetDriverLoadsResponse> createGetDriverLoadsResponses(List<DriverEntity> driversWithoutDispatchers) {
        List<GetDriverLoadsResponse> driversWithoutDispatchersLoadsResponses = new ArrayList<>();
        for (DriverEntity driver : driversWithoutDispatchers) {
            driversWithoutDispatchersLoadsResponses.add(GetDriverLoadsResponse.builder()
                .driver(GetDriverResponse.builder().uuid(driver.getUuid()).fullName(driver.getFullName()).build())
                .loads(List.of())
                .build()
            );
        }
        return driversWithoutDispatchersLoadsResponses;
    }
}
