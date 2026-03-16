package io.kovin.dispatch.management.system.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.kovin.dispatch.management.system.model.persistence.LoadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoadRepository extends JpaRepository<LoadEntity, Long> {

    @Query("""
        SELECT l
        FROM LoadEntity l
        WHERE l.driverDispatcherRelation.uuid = :driverDispatcherRelationUuid
          AND l.endDate >= :startDate
          AND :endDate >= l.startDate
    """)
    List<LoadEntity> findOverlappingLoadsForRelation(
        String driverDispatcherRelationUuid,
        LocalDate startDate,
        LocalDate endDate
    );

    @Query("""
        SELECT l
        FROM LoadEntity l
        WHERE l.driverDispatcherRelation.uuid = :relationUuid
            AND l.endDate <= :date
        ORDER BY l.endDate DESC
        LIMIT 1
    """)
    Optional<LoadEntity> findByRelationUuidAndDateBetween(String relationUuid, LocalDate date);

    void deleteByUuid(String uuid);
}
