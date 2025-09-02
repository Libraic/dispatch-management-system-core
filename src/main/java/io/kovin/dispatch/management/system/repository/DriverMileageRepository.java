package io.kovin.dispatch.management.system.repository;

import java.util.List;
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
}
