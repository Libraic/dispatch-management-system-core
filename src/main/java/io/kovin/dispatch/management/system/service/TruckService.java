package io.kovin.dispatch.management.system.service;

import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.persistence.TruckEntity;
import io.kovin.dispatch.management.system.repository.TruckRepository;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;

    public Optional<TruckEntity> findTruckByUuid(String uuid) {
        log.info("Retrieving the Truck by UUID=[{}].", uuid);
        return truckRepository.findByUuidAndDeletedAtIsNull(uuid);
    }

    public TruckEntity getTruckByUuid(String uuid) {
        Optional<TruckEntity> truckEntityOptional = findTruckByUuid(uuid);
        if (truckEntityOptional.isEmpty()) {
            String errorMessage = String.format(ErrorMessage.TRUCK_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, HttpStatus.NOT_FOUND);
        }

        return truckEntityOptional.get();
    }

    public TruckEntity getTruckByVinNumber(String vinNumber) {
        log.info("Retrieving the Truck by VIN Number=[{}].", vinNumber);
        return truckRepository.findByVinNumberAndDeletedAtIsNull(vinNumber).orElse(null);
    }

    public void saveTruckEntity(TruckEntity truckEntity) {
        if (truckEntity != null) {
            log.info("Saving the Truck Entity.");
            truckRepository.save(truckEntity);
        }
    }
}
