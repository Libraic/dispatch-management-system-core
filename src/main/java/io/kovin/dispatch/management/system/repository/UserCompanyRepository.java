package io.kovin.dispatch.management.system.repository;

import io.kovin.dispatch.management.system.model.entity.UserCompanyEntity;
import io.kovin.dispatch.management.system.model.entity.UserCompanyId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCompanyRepository extends JpaRepository<UserCompanyEntity, UserCompanyId> {
}
