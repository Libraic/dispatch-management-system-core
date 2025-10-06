package io.kovin.dispatch.management.system.service;

import io.kovin.dispatch.management.system.model.entity.TruckEntity;
import io.kovin.dispatch.management.system.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;

    public TruckEntity getTruckByVinNumber(String vinNumber) {
        log.info("Retrieving the Truck by VIN Number=[{}].", vinNumber);
        return truckRepository.findByVinNumber(vinNumber).orElse(null);
    }

    public void saveTruckEntity(TruckEntity truckEntity) {
        if (truckEntity != null) {
            log.info("Saving the Truck Entity.");
            truckRepository.save(truckEntity);
        }
    }
}
