package io.kovin.dispatch.management.system.repository;

import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUuid(String uuid);

    boolean existsByEmail(String email);

    @Query(
        "SELECT u " +
        "FROM UserEntity u " +
        "WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) " +
            "LIKE LOWER(CONCAT('%', :fullName, '%'))"
    )
    List<UserEntity> searchByFullName(@Param("fullName") String fullName);

    List<UserEntity> findByUuidIn(List<String> uuids);
}
