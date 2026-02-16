package io.kovin.dispatch.management.system.mapper;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.model.entity.DispatcherEntity;
import io.kovin.dispatch.management.system.model.entity.enums.LoadStatus;
import io.kovin.dispatch.management.system.model.internal.mileage.DispatcherDto;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverDto;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverMileageDto;
import io.kovin.dispatch.management.system.model.internal.mileage.MileageDto;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.MileageData;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.model.request.UpsertDriverMileageRequest;
import io.kovin.dispatch.management.system.model.response.mileage.GetMileageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DriverMileageMapper {

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
                .deliveryLocation(mileageData.getValue().getDeliveryLocation())
                .pickUpLocation(mileageData.getValue().getPickUpLocation())
                .pickUpDate(mileageData.getValue().getPickUpDate())
                .deliveryDate(mileageData.getValue().getDeliveryDate())
                .loadStatus(mileageData.getValue().getLoadStatus().getStatus())
                .build()
            ).toList();
    }

    public MileageData createCoveredMileageDatum(UpsertDriverMileageRequest request) {
        return MileageData.builder()
            .revenue(request.revenue())
            .miles(request.miles())
            .broker(request.broker())
            .representative(request.representative())
            .deliveryLocation(request.deliveryLocation())
            .pickUpLocation(request.pickUpLocation())
            .pickUpDate(request.pickUpDate())
            .deliveryDate(request.deliveryDate())
            .loadStatus(LoadStatus.COVERED)
            .build();
    }

    public MileageData createTransitMileageDatum() {
        return MileageData.builder().loadStatus(LoadStatus.TRANSIT).build();
    }

    public MileageData createEmptyMileageDatum(LocalDate deliveryDate, String pickUpLocation) {
        return MileageData.builder()
            .pickUpDate(deliveryDate)
            .pickUpLocation(pickUpLocation)
            .loadStatus(LoadStatus.EMPTY)
            .build();
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
