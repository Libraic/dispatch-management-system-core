package io.kovin.dispatch.management.system.mapper;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import io.kovin.dispatch.management.system.model.internal.mileage.DispatcherDto;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverDto;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverMileageDto;
import io.kovin.dispatch.management.system.model.internal.mileage.MileageDto;
import io.kovin.dispatch.management.system.model.entity.Auditable;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.MileageData;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.global.Mileage;
import io.kovin.dispatch.management.system.model.request.DriverMileage;
import io.kovin.dispatch.management.system.model.response.DriverMileageData;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;
import io.kovin.dispatch.management.system.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DriverMileageMapper {

    private final DriverMapper driverMapper;
    private final UserMapper userMapper;

    public DriverMileageEntity createTransientDriverMileageEntity(
        DriverMileage driverMileage,
        Map<String, MileageData> mileageDataMap,
        CompanyEntity company,
        Map<String, UserEntity> dispatchersMap,
        Map<String, DriverEntity> driversMap,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return DriverMileageEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .company(company)
            .dispatcher(dispatchersMap.get(driverMileage.dispatcherUuid()))
            .driver(driversMap.get(driverMileage.driverUuid()))
            .mileageData(mileageDataMap)
            .itemIdentifier(driverMileage.itemIdentifier())
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

    /**
     * Maps the Driver Mileage Entity objects to Driver Mileage Data.
     *
     * @param entities the Driver Mileage Entity objects.
     * @return a list of Driver Mileage Data objects.
     */
    public List<DriverMileageData> fromDriverMileageEntitiesToDriverMileageDataList(List<DriverMileageEntity> entities) {
        return entities.stream()
            .sorted(Comparator.comparing(Auditable::getCreatedAt))
            .map(driverMileageEntity -> DriverMileageData.builder()
                .uuid(driverMileageEntity.getUuid())
                .driver(driverMapper.fromDriverEntityToDriverData(driverMileageEntity.getDriver()))
                .dispatcher(userMapper.fromUserEntityToUserData(driverMileageEntity.getDispatcher()))
                .itemIdentifier(driverMileageEntity.getItemIdentifier())
                .startDate(driverMileageEntity.getStartDate())
                .endDate(driverMileageEntity.getEndDate())
                .mileageData(fromMileageDataListToMileageList(driverMileageEntity.getMileageData()))
                .build()
            )
            .toList();
    }

    public DriverMileageEntity createDriverMileageEntity(
        DriverMileageEntity current,
        DriverMileage driverMileage,
        UserEntity dispatcher,
        DriverEntity driver,
        CompanyEntity company
    ) {
        if (current != null) {
            return current.toBuilder()
                .dispatcher(dispatcher)
                .driver(driver)
                .mileageData(fromMileageDataRequestToMileageDataEntity(driverMileage.mileage()))
                .itemIdentifier(driverMileage.itemIdentifier())
                .build();
        }

        return DriverMileageEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .company(company)
            .dispatcher(dispatcher)
            .driver(driver)
            .mileageData(fromMileageDataRequestToMileageDataEntity(driverMileage.mileage()))
            .itemIdentifier(driverMileage.itemIdentifier())
            .startDate(driverMileage.startDate())
            .endDate(driverMileage.endDate())
            .build();
    }

    public Map<String, MileageData> fromMileageDataRequestToMileageDataEntity(
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
                    .broker(mileage.broker())
                    .build()
            ));
    }

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

    private DispatcherDto fromUserEntityToDispatcherDto(UserEntity dispatcher) {
        return new DispatcherDto(dispatcher.getUuid(), dispatcher.getFullName());
    }

    private List<MileageDto> fromMileageDataToMileageDto(Map<String, MileageData> mileageDataMap) {
        return mileageDataMap.entrySet()
            .stream()
            .map(mileageDataEntry -> new MileageDto(
                LocalDate.parse(mileageDataEntry.getKey()),
                NumberUtils.getOrZero(mileageDataEntry.getValue().getRevenue()),
                NumberUtils.getOrZero(mileageDataEntry.getValue().getMiles())
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
                    mileageData.getDestinationNote(),
                    BigDecimalUtils.getBigDecimalFromDouble(mileageData.getRevenue()),
                    BigDecimalUtils.getBigDecimalFromDouble(mileageData.getMiles()),
                    mileageData.getNote(),
                    mileageData.getBroker()
                );
            })
            .sorted(Comparator.comparing(Mileage::date))
            .toList();
    }
}
