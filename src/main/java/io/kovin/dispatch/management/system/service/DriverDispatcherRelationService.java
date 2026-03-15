package io.kovin.dispatch.management.system.service;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.DRIVER_DISPATCHER_RELATION_NOT_FOUND;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import io.kovin.dispatch.management.system.model.persistence.DriverDispatcherRelationEntity;
import io.kovin.dispatch.management.system.repository.DriverDispatcherRelationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverDispatcherRelationService {

    private final DriverDispatcherRelationRepository driverDispatcherRelationRepository;

    /**
     * Persists the relationship between a Driver and a Dispatcher within the system.
     * Logs the details of the Driver and Dispatcher involved in the relationship.
     *
     * @param driverDispatcherRelation the entity representing the relationship
     *                                 between the Driver and the Dispatcher to be persisted
     */
    public void persistRelation(DriverDispatcherRelationEntity driverDispatcherRelation) {
        log.info(
            "Persisting the relation between Driver=[{}] and Dispatcher=[{}].",
            driverDispatcherRelation.getDriver().getUuid(),
            driverDispatcherRelation.getDispatcher().getUuid()
        );
        driverDispatcherRelationRepository.save(driverDispatcherRelation);
    }

    /**
     * Retrieves the relation between a driver and a dispatcher for a specific company.
     * Throws an exception if the relation is not found or has been marked as deleted.
     *
     * @param companyUuid      the UUID of the company to which the driver and dispatcher belong
     * @param dispatcherUuid   the UUID of the dispatcher
     * @param driverUuid       the UUID of the driver
     *
     * @return the DriverDispatcherRelationEntity representing the relation between the driver and dispatcher
     *         for the specified company
     *
     * @throws DispatchManagementSystemException if the relation is not found or has been marked as deleted
     */
    public DriverDispatcherRelationEntity findRelationByDriverAndDispatcher(
        String companyUuid,
        String dispatcherUuid,
        String driverUuid
    ) {
        log.info(
            "Retrieving the relation between Driver=[{}] and Dispatcher=[{}] for Company=[{}].",
            driverUuid,
            dispatcherUuid,
            companyUuid
        );
        var relationOptional = driverDispatcherRelationRepository.findByCompanyUuidAndDriverUuidAndDispatcherUuidAndDeletedAtIsNull(
            companyUuid,
            driverUuid,
            dispatcherUuid
        );
        if (relationOptional.isEmpty()) {
            log.error(DRIVER_DISPATCHER_RELATION_NOT_FOUND);
            throw DispatchManagementSystemException.of(DRIVER_DISPATCHER_RELATION_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return relationOptional.get();
    }

    /**
     * Retrieves all Driver-Dispatcher relationships for a specific company and groups them by dispatcher.
     *
     * @param companyUuid the UUID of the company whose driver-dispatcher relationships are to be retrieved
     * @return a map where the keys are DispatcherEntity objects representing dispatchers,
     *         and the values are lists of DriverDispatcherRelationEntity objects representing the driver-dispatcher
     *         relationships associated with each dispatcher
     */
    public Map<DispatcherEntity, List<DriverDispatcherRelationEntity>> findRelationsByCompanyGroupedByDispatcher(String companyUuid) {
        log.info("Retrieving all relations for Company=[{}].", companyUuid);
        List<DriverDispatcherRelationEntity> relations = driverDispatcherRelationRepository.findAllByCompanyUuidAndDeletedAtIsNull(companyUuid);
        return relations.stream().collect(Collectors.groupingBy(DriverDispatcherRelationEntity::getDispatcher));
    }
}
