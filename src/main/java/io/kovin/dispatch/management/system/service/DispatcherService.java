package io.kovin.dispatch.management.system.service;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.DISPATCHER_NOT_FOUND;

import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import io.kovin.dispatch.management.system.repository.DispatcherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DispatcherService {

    private final DispatcherRepository dispatcherRepository;

    public void saveDispatcher(DispatcherEntity dispatcherEntity) {
        log.info("Saving dispatcher with UUID=[{}].", dispatcherEntity.getUuid());
        dispatcherRepository.save(dispatcherEntity);
    }

    public DispatcherEntity getByUuid(String uuid) {
        Optional<DispatcherEntity> dispatcherEntityOptional = dispatcherRepository.findByUuidAndDeletedAtIsNull(uuid);
        if (dispatcherEntityOptional.isEmpty()) {
            String message = String.format(DISPATCHER_NOT_FOUND, uuid);
            log.error(message);
            throw DispatchManagementSystemException.of(message, HttpStatus.NOT_FOUND);
        }

        return dispatcherEntityOptional.get();
    }

    public List<DispatcherEntity> getAllDispatchersWithDriversByCompany(String companyUuid) {
        return dispatcherRepository.getDispatchersWithDrivers(companyUuid);
    }
}
