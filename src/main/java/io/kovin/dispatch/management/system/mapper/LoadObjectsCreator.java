package io.kovin.dispatch.management.system.mapper;

import java.time.LocalDate;
import java.util.List;

import io.kovin.dispatch.management.system.model.persistence.LoadLocationEntity;
import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import io.kovin.dispatch.management.system.model.persistence.LoadEntity;
import io.kovin.dispatch.management.system.model.persistence.DriverEntity;
import io.kovin.dispatch.management.system.model.persistence.enums.LoadStatus;
import io.kovin.dispatch.management.system.model.response.GetDispatcherResponse;
import io.kovin.dispatch.management.system.model.response.GetDriverResponse;
import io.kovin.dispatch.management.system.model.response.load.GetLocationResponse;
import io.kovin.dispatch.management.system.model.response.load.GenericLoadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LoadObjectsCreator {

    public GetDispatcherResponse createGetDispatcherResponse(DispatcherEntity dispatcher) {
        return GetDispatcherResponse.builder()
            .uuid(dispatcher.getUuid())
            .name(dispatcher.getName())
            .phoneNumber(dispatcher.getPhoneNumber())
            .build();
    }

    public GetDriverResponse createGetDriverResponse(DriverEntity driver) {
        return GetDriverResponse.builder()
            .fullName(driver.getFullName())
            .uuid(driver.getUuid())
            .phoneNumber(driver.getPhoneNumber())
            .build();
    }

    public GenericLoadResponse createGetLoadResponse(LoadEntity load, List<LoadLocationEntity> loadLocations) {
        return GenericLoadResponse.builder()
            .loadUuid(load.getUuid())
            .miles(load.getMiles())
            .revenue(load.getRevenue())
            .broker(load.getBroker())
            .representative(load.getRepresentative())
            .loadStatus(load.getLoadStatus().getStatus())
            .representativeContactNumber(load.getRepresentativeContactNumber())
            .startDate(load.getStartDate())
            .endDate(load.getEndDate())
            .locations(loadLocations.stream()
                .map(locationData -> new GetLocationResponse(
                    locationData.getLocation(),
                    locationData.getDate(),
                    locationData.getTime(),
                    locationData.getLocationType().getType(),
                    locationData.getLocationOrder()
                )).toList()
            ).build();
    }
}
