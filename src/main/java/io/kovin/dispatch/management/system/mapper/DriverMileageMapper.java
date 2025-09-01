package io.kovin.dispatch.management.system.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.MileageData;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.global.Mileage;
import io.kovin.dispatch.management.system.model.request.DriverMileage;
import io.kovin.dispatch.management.system.model.response.DriverMileageData;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DriverMileageMapper {

    private final DriverMapper driverMapper;
    private final UserMapper userMapper;

    public List<DriverMileageEntity> fromMileageDataListToMileageEntityList(
        List<DriverMileage> driverMileageList,
        CompanyEntity company,
        Map<String, UserEntity> dispatchersMap,
        Map<String, DriverMileageEntity> mileageMap,
        Map<String, DriverEntity> driversMap
    ) {
        List<DriverMileageEntity> mileageEntities = new ArrayList<>();
        for (DriverMileage driverMileage : driverMileageList) {
            DriverMileageEntity current = mileageMap.get(driverMileage.mileageUuid());
            if (current != null) {
                DriverMileageEntity updated = current.toBuilder()
                    .dispatcher(dispatchersMap.get(driverMileage.dispatcherUuid()))
                    .driver(driversMap.get(driverMileage.driverUuid()))
                    .mileageData(fromMileageDataRequestToMileageDataEntity(driverMileage.mileage()))
                    .itemIdentifier(driverMileage.itemIdentifier())
                    .build();
                mileageEntities.add(updated);
            } else {
                DriverMileageEntity newEntity = DriverMileageEntity.builder()
                    .uuid(UUID.randomUUID().toString())
                    .company(company)
                    .dispatcher(dispatchersMap.get(driverMileage.dispatcherUuid()))
                    .driver(driversMap.get(driverMileage.driverUuid()))
                    .itemIdentifier(driverMileage.itemIdentifier())
                    .mileageData(fromMileageDataRequestToMileageDataEntity(driverMileage.mileage()))
                    .build();
                mileageEntities.add(newEntity);
            }
        }

        return mileageEntities;
    }

    /**
     * Maps the Driver Mileage Entity objects to Driver Mileage Data.
     *
     * @param entities the Driver Mileage Entity objects.
     * @return a list of Driver Mileage Data objects.
     */
    public List<DriverMileageData> fromDriverMileageEntitiesToDriverMileageDataList(List<DriverMileageEntity> entities) {
        return entities.stream()
            .map(driverMileageEntity -> DriverMileageData.builder()
                .uuid(driverMileageEntity.getUuid())
                .driver(driverMapper.fromDriverEntityToDriverData(driverMileageEntity.getDriver()))
                .dispatcher(userMapper.fromUserEntityToUserData(driverMileageEntity.getDispatcher()))
                .itemIdentifier(driverMileageEntity.getItemIdentifier())
                .mileageData(fromMileageDataListToMileageList(driverMileageEntity.getMileageData()))
                .build()
            ).toList();
    }

    private Map<String, MileageData> fromMileageDataRequestToMileageDataEntity(
        List<Mileage> mileageList
    ) {
        return mileageList.stream()
            .collect(Collectors.toMap(
                Mileage::date,
                mileage -> MileageData.builder()
                    .note(mileage.note())
                    .miles(BigDecimalUtils.getDoubleFromBigDecimal(mileage.miles()))
                    .revenue(BigDecimalUtils.getDoubleFromBigDecimal(mileage.revenue()))
                    .destinationNote(mileage.destinationNote())
                    .build()
            ));
    }

    private List<Mileage> fromMileageDataListToMileageList(Map<String, MileageData> mileageDataMap) {
        return mileageDataMap.entrySet()
            .stream()
            .map(entry -> new Mileage(
                entry.getKey(),
                entry.getValue().getDestinationNote(),
                BigDecimalUtils.getBigDecimalFromDouble(entry.getValue().getRevenue()),
                BigDecimalUtils.getBigDecimalFromDouble(entry.getValue().getMiles()),
                entry.getValue().getNote()
            )).toList();
    }

    private final Predicate<Mileage> NON_EMPTY_MILEAGE = value -> value.destinationNote() != null
        || value.note() != null
        || value.revenue() != null
        || value.miles() != null;
}
