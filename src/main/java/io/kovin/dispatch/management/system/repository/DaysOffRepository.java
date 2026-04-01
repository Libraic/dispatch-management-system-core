package io.kovin.dispatch.management.system.repository;

import java.util.Optional;
import io.kovin.dispatch.management.system.model.persistence.DaysOffPeriodEntity;

public interface DaysOffRepository extends BaseSchedulableRepository<DaysOffPeriodEntity> {

    Optional<DaysOffPeriodEntity> findByUuid(String uuid);

    void deleteByUuid(String uuid);
}
