package io.kovin.dispatch.management.system.repository;

import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.model.persistence.DriverDispatcherRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverDispatcherRelationRepository extends JpaRepository<DriverDispatcherRelationEntity, Long> {

    Optional<DriverDispatcherRelationEntity> findByCompanyUuidAndDriverUuidAndDispatcherUuidAndDeletedAtIsNull(
        String companyUuid,
        String driverUuid,
        String dispatcherUuid
    );

    List<DriverDispatcherRelationEntity> findAllByCompanyUuidAndDeletedAtIsNull(String companyUuid);
}
