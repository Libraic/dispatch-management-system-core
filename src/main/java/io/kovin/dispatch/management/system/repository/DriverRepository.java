package io.kovin.dispatch.management.system.repository;

import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<DriverEntity, Long> {
}
