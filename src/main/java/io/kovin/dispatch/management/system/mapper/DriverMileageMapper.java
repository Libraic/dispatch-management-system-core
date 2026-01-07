package io.kovin.dispatch.management.system.mapper;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.model.entity.DispatcherEntity;
import io.kovin.dispatch.management.system.model.internal.mileage.DispatcherDto;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverDto;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverMileageDto;
import io.kovin.dispatch.management.system.model.internal.mileage.MileageDto;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.MileageData;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.model.global.Mileage;
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

    private List<Mileage> fromMileageDataListToMileageList(Map<String, MileageData> mileageDataMap) {
        return mileageDataMap.entrySet()
            .stream()
            .map(entry -> {
                MileageData mileageData = entry.getValue();
                return new Mileage(
                    entry.getKey(),
                    mileageData.getRevenue(),
                    mileageData.getMiles(),
                    mileageData.getBroker()
                );
            })
            .sorted(Comparator.comparing(Mileage::date))
            .toList();
    }
}
