package io.kovin.dispatch.management.system.service;

import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.persistence.TrailerEntity;
import io.kovin.dispatch.management.system.repository.TrailerRepository;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrailerService {

    private final TrailerRepository trailerRepository;

    public Optional<TrailerEntity> findTrailerByUuid(String uuid) {
        log.info("Retrieving the Trailer by UUID=[{}].", uuid);
        return trailerRepository.findByUuidAndDeletedAtIsNull(uuid);
    }

    public TrailerEntity getTrailerByUuid(String uuid) {
        Optional<TrailerEntity> trailerEntityOptional = findTrailerByUuid(uuid);
        if (trailerEntityOptional.isEmpty()) {
            String errorMessage = String.format(ErrorMessage.TRAILER_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, HttpStatus.NOT_FOUND);
        }

        return trailerEntityOptional.get();
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
