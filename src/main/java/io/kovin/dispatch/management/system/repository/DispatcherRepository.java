package io.kovin.dispatch.management.system.repository;

import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DispatcherRepository extends JpaRepository<DispatcherEntity, Long> {

    Optional<DispatcherEntity> findByUuidAndDeletedAtIsNull(String uuid);

    @EntityGraph(attributePaths = "drivers")
    @Query("""
        SELECT d
        FROM DispatcherEntity d
        WHERE d.company.uuid = :companyUuid
          AND EXISTS (
              SELECT dr.id
              FROM DriverEntity dr
              WHERE dr.dispatcher = d
          )
        ORDER BY d.createdAt ASC
    """)
    List<DispatcherEntity> getDispatchersWithDrivers(String companyUuid);
}
