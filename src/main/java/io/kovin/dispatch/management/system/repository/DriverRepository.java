package io.kovin.dispatch.management.system.repository;

import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.model.persistence.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DriverRepository extends JpaRepository<DriverEntity, Long> {

    Optional<DriverEntity> findByUuidAndDeletedAtIsNull(String uuid);

    @Query("SELECT d FROM DriverEntity d WHERE dispatcher IS NULL")
    List<DriverEntity> getDriversWithoutDispatchersByCompanyUuid(String companyUuid);
}
