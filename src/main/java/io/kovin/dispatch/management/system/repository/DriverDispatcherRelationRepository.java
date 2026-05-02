package io.kovin.dispatch.management.system.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.DriverDispatcherRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverDispatcherRelationRepository extends JpaRepository<DriverDispatcherRelationEntity, Long> {

    Optional<DriverDispatcherRelationEntity> findByCompanyUuidAndDriverUuidAndDispatcherUuidAndDeletedAtIsNull(
        UUID companyUuid,
        UUID driverUuid,
        UUID dispatcherUuid
    );

    List<DriverDispatcherRelationEntity> findAllByCompanyUuidAndDeletedAtIsNull(UUID companyUuid);

    Optional<DriverDispatcherRelationEntity> findByUuidAndDeletedAtIsNull(UUID uuid);
}
