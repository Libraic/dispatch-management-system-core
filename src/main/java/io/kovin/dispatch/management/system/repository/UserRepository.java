package io.kovin.dispatch.management.system.repository;

import java.util.Optional;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUuid(String uuid);

    boolean existsByEmail(String email);
}
