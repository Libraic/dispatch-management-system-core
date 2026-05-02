package io.kovin.dispatch.management.system.repository;

import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.DaysOffPeriodEntity;

public interface DaysOffRepository extends BaseSchedulableRepository<DaysOffPeriodEntity> {

    Optional<DaysOffPeriodEntity> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
