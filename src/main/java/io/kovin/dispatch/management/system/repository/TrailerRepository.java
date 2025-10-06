package io.kovin.dispatch.management.system.repository;

import java.util.Optional;
import io.kovin.dispatch.management.system.model.entity.TrailerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrailerRepository extends JpaRepository<TrailerEntity, Long> {

    Optional<TrailerEntity> findByVinNumber(String vinNumber);
}
