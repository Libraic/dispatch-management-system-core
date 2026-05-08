package io.kovin.dispatch.management.system.repository;

import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatcherRepository extends JpaRepository<DispatcherEntity, Long> {

    Optional<DispatcherEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
