package io.kovin.dispatch.management.system.service;

import io.kovin.dispatch.management.system.model.entity.TrailerEntity;
import io.kovin.dispatch.management.system.repository.TrailerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrailerService {

    private final TrailerRepository trailerRepository;

    public TrailerEntity getTrailerByVinNumber(String vinNumber) {
        log.info("Retrieving the Trailer by VIN Number=[{}].", vinNumber);
        return trailerRepository.findByVinNumber(vinNumber).orElse(null);
    }

    public void saveTrailerEntity(TrailerEntity trailerEntity) {
        if (trailerEntity != null) {
            log.info("Saving Trailer entity.");
            trailerRepository.save(trailerEntity);
        }
    }
}
