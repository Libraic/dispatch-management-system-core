package io.kovin.dispatch.management.system.repository;

import java.time.LocalDate;
import java.util.Optional;
import io.kovin.dispatch.management.system.model.persistence.LoadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoadRepository extends JpaRepository<LoadEntity, Long> {

    Optional<LoadEntity> findByUuidAndDeletedAtIsNull(String uuid);

    @Query("SELECT le FROM LoadEntity le " +
        "WHERE le.dispatcher.uuid = :dispatcherUuid " +
        "AND le.driver.uuid = :driverUuid " +
        "AND le.company.uuid = :companyUuid " +
        "AND le.startDate >= :startDate " +
        "AND le.endDate <= :endDate " +
        "AND le.deletedAt IS NULL"
    )
    Optional<LoadEntity> findLoadsForTimeframe(
        String companyUuid,
        String dispatcherUuid,
        String driverUuid,
        LocalDate startDate,
        LocalDate endDate
    );

    void deleteByUuid(String uuid);
}
