package io.kovin.dispatch.management.system.repository;

import java.util.Optional;
import io.kovin.dispatch.management.system.model.entity.TruckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckRepository extends JpaRepository<TruckEntity, Long> {

    Optional<TruckEntity> findByVinNumberAndDeletedAtIsNull(String vinNumber);

    Optional<TruckEntity> findByUuidAndDeletedAtIsNull(String uuid);
}
