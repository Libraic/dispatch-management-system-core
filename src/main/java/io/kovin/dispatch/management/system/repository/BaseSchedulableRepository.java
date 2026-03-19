package io.kovin.dispatch.management.system.repository;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.model.persistence.SchedulableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseSchedulableRepository<T extends SchedulableEntity> extends JpaRepository<T, Long> {

    @Query("""
        SELECT e
        FROM #{#entityName} e
        WHERE e.driverDispatcherRelation.uuid = :driverDispatcherRelationUuid
          AND e.endDate >= :startDate
          AND :endDate >= e.startDate
    """)
    List<T> findOverlappingRecordsForRelation(
        String driverDispatcherRelationUuid,
        LocalDate startDate,
        LocalDate endDate
    );
}
