package io.kovin.dispatch.management.system.repository;

import io.kovin.dispatch.management.system.model.persistence.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
}
