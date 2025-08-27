package io.kovin.dispatch.management.system.repository;

import java.util.List;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverMileageRepository extends JpaRepository<DriverMileageEntity, Long> {

    List<DriverMileageEntity> findByUuidIn(List<String> uuids);
}
