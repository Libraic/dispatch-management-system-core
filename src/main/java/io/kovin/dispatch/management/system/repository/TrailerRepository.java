package io.kovin.dispatch.management.system.repository;

import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.TrailerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrailerRepository extends JpaRepository<TrailerEntity, Long> {

    Optional<TrailerEntity> findByVinNumberAndDeletedAtIsNull(String vinNumber);

    Optional<TrailerEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
