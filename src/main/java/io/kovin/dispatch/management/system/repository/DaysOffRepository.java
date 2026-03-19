package io.kovin.dispatch.management.system.repository;

import io.kovin.dispatch.management.system.model.persistence.DaysOffPeriodEntity;

public interface DaysOffRepository extends BaseSchedulableRepository<DaysOffPeriodEntity> {

    void deleteByUuid(String uuid);
}
