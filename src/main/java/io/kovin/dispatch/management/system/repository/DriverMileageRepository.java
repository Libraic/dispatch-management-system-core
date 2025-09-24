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

    List<DriverMileageEntity> findByUuidIn(List<String> uuids);

    @Modifying
    @Query("DELETE FROM DriverMileageEntity dme WHERE dme.uuid IN :uuids")
    void deleteAllByUuidIn(@Param("uuids") List<String> uuids);

    Optional<DriverMileageEntity> findByDispatcher_UuidAndDriver_UuidAndStartDateAndEndDate(
        String dispatcherUuid,
        String driverUuid,
        LocalDate startDate,
        LocalDate endDate
    );

}
