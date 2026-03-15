package io.kovin.dispatch.management.system.repository;

import java.util.Optional;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    Optional<CompanyEntity> findByUuidAndDeletedAtIsNull(String uuid);

    boolean existsByUuid(String uuid);
}
