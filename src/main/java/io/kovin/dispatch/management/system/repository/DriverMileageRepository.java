package io.kovin.dispatch.management.system.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DriverMileageRepository extends JpaRepository<DriverMileageEntity, Long> {

    @Modifying
    @Query("DELETE FROM DriverMileageEntity dme WHERE dme.uuid IN :uuids")
    void deleteAllByUuidIn(@Param("uuids") List<String> uuids);

    Optional<DriverMileageEntity> findByUuidAndDeletedAtIsNull(String uuid);

    Optional<DriverMileageEntity> findByStartDateAndEndDateAndCompanyUuidAndDispatcherUuidAndDeletedAtIsNull(
        LocalDate startDate,
        LocalDate endDate,
        String companyUuid,
        String dispatcherUuid
    );

    List<DriverMileageEntity> findByCompanyUuidAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndDeletedAtIsNullOrderByCreatedAtAsc(
        String companyUuid,
        LocalDate startDate,
        LocalDate endDate
    );

    @Query("SELECT dme FROM DriverMileageEntity dme " +
        "WHERE dme.dispatcher.uuid = :dispatcherUuid " +
        "AND dme.driver.uuid = :driverUuid " +
        "AND dme.startDate >= :startDate " +
        "AND dme.endDate <= :endDate " +
        "AND dme.deletedAt IS NULL"
    )
    Optional<DriverMileageEntity> findDriversMileageForTimeframe(
        String dispatcherUuid,
        String driverUuid,
        LocalDate startDate,
        LocalDate endDate
    );
}
