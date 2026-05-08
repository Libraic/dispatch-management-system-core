package io.kovin.dispatch.management.system.repository;

import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<DriverEntity, Long> {

    Optional<DriverEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
