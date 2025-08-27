package io.kovin.dispatch.management.system.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.repository.DriverMileageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DriverMileageService {

    private final DriverMileageRepository driverMileageRepository;

    public List<DriverMileageEntity> saveMileageEntities(List<DriverMileageEntity> mileageEntities) {
        log.info("Persisting [{}] Mileage entities.", mileageEntities.size());
        return driverMileageRepository.saveAll(mileageEntities);
    }

    public Map<String, DriverMileageEntity> getMileageMapByUuids(List<String> uuids) {
        return driverMileageRepository.findByUuidIn(uuids)
            .stream()
            .collect(Collectors.toMap(DriverMileageEntity::getUuid, Function.identity()));
    }
}
