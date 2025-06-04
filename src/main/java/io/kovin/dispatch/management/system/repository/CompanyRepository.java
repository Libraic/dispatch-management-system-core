package io.kovin.dispatch.management.system.repository;

import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    Optional<CompanyEntity> findByUuidAndDeletedAtIsNull(String uuid);

    List<CompanyEntity> findByUuidIn(List<String> uuids);
}
