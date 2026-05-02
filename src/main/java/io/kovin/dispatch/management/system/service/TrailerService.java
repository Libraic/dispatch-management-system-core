package io.kovin.dispatch.management.system.service;

import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.TrailerEntity;
import io.kovin.dispatch.management.system.repository.TrailerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrailerService {

    private final TrailerRepository trailerRepository;

    public Optional<TrailerEntity> findTrailerByUuid(UUID uuid) {
        log.info("Retrieving the Trailer by UUID=[{}].", uuid);
        return trailerRepository.findByUuidAndDeletedAtIsNull(uuid);
    }

    public TrailerEntity getTrailerByVinNumber(String vinNumber) {
        log.info("Retrieving the Trailer by VIN Number=[{}].", vinNumber);
        return trailerRepository.findByVinNumberAndDeletedAtIsNull(vinNumber).orElse(null);
    }

    public void saveTrailerEntity(TrailerEntity trailerEntity) {
        if (trailerEntity != null) {
            log.info("Saving Trailer entity.");
            trailerRepository.save(trailerEntity);
        }
    }
}
