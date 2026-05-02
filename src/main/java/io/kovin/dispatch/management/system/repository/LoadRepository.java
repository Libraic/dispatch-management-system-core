package io.kovin.dispatch.management.system.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.LoadEntity;
import org.springframework.data.jpa.repository.Query;

public interface LoadRepository extends BaseSchedulableRepository<LoadEntity> {

    Optional<LoadEntity> findByUuid(UUID uuid);

    @Query("""
        SELECT l
        FROM LoadEntity l
        WHERE l.driverDispatcherRelation.uuid = :relationUuid
            AND l.endDate <= :date
        ORDER BY l.endDate DESC
        LIMIT 1
    """)
    Optional<LoadEntity> findByRelationUuidAndDateBetween(UUID relationUuid, LocalDate date);

    void deleteByUuid(UUID uuid);
}
