package io.kovin.dispatch.management.system.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.DriverDispatcherRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverDispatcherRelationRepository extends JpaRepository<DriverDispatcherRelationEntity, Long> {

    List<DriverDispatcherRelationEntity> findAllByCompanyUuidAndDeletedAtIsNull(UUID companyUuid);

    Optional<DriverDispatcherRelationEntity> findByUuidAndDeletedAtIsNull(UUID uuid);

    Optional<DriverDispatcherRelationEntity> findByDriverUuidAndDeletedAtIsNull(UUID driverUuid);
}
