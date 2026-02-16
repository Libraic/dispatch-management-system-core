package io.kovin.dispatch.management.system.repository;

import java.time.LocalDate;
import java.util.Optional;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DriverMileageRepository extends JpaRepository<DriverMileageEntity, Long> {

    Optional<DriverMileageEntity> findByUuidAndDeletedAtIsNull(String uuid);

    @Query("SELECT dme FROM DriverMileageEntity dme " +
        "WHERE dme.dispatcher.uuid = :dispatcherUuid " +
        "AND dme.driver.uuid = :driverUuid " +
        "AND dme.company.uuid = :companyUuid " +
        "AND dme.startDate >= :startDate " +
        "AND dme.endDate <= :endDate " +
        "AND dme.deletedAt IS NULL"
    )
    Optional<DriverMileageEntity> findDriversMileageForTimeframe(
        String companyUuid,
        String dispatcherUuid,
        String driverUuid,
        LocalDate startDate,
        LocalDate endDate
    );
}
