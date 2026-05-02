package io.kovin.dispatch.management.system.facade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.kovin.dispatch.management.system.model.persistence.DaysOffPeriodEntity;
import io.kovin.dispatch.management.system.model.persistence.DriverDispatcherRelationEntity;
import io.kovin.dispatch.management.system.model.request.UpsertDayOffPeriodRequest;
import io.kovin.dispatch.management.system.model.response.GetDayOffPeriodResponse;
import io.kovin.dispatch.management.system.model.response.UpsertDayOffPeriodResponse;
import io.kovin.dispatch.management.system.service.DaysOffService;
import io.kovin.dispatch.management.system.service.DriverDispatcherRelationService;
import io.kovin.dispatch.management.system.validation.DaysOffValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DaysOffFacade {

    private final DriverDispatcherRelationService driverDispatcherRelationService;
    private final DaysOffValidationService daysOffValidationService;
    private final DaysOffService daysOffService;

    /**
     * Inserts or updates a day-off period for a given driver-dispatcher relation.
     *
     * @param request the {@link UpsertDayOffPeriodRequest} containing the details of the day-off period,
     *                including the relation ID, start date, and end date. If a day-off ID is provided,
     *                it indicates an update; otherwise, a new day-off period is created.
     * @return the {@link UpsertDayOffPeriodResponse} containing the unique identifier of the created
     *         or updated day-off period, along with its start and end dates.
     */
    public UpsertDayOffPeriodResponse upsertDayOffPeriod(UpsertDayOffPeriodRequest request) {
        daysOffValidationService.validateDaysOffPeriodUpsertion(request);

        var relation = driverDispatcherRelationService.getRelationByUuid(request.relationId());
        DaysOffPeriodEntity daysOffPeriodEntity = getDaysOffPeriodEntity(request, relation);
        daysOffService.persistDayOffPeriod(daysOffPeriodEntity);

        return UpsertDayOffPeriodResponse.builder()
            .daysOffPeriodId(daysOffPeriodEntity.getUuid().toString())
            .startDate(daysOffPeriodEntity.getStartDate())
            .endDate(daysOffPeriodEntity.getEndDate())
            .build();
    }

    /**
     * Retrieves a list of day-off responses for a specific Driver-Dispatcher relation
     * within the given start and end dates. It fetches the overlapping days off corresponding
     * to the provided relation ID and timeframe, and maps them into response objects.
     *
     * @param relationId the unique identifier of the Driver-Dispatcher relation for which
     *                   the day-off responses are to be retrieved.
     * @param startDate  the beginning of the date range for which the day-off responses
     *                   are to be retrieved.
     * @param endDate    the end of the date range for which the day-off responses
     *                   are to be retrieved.
     * @return a list of {@code GetDayOffResponse} objects representing the day-off periods
     *         within the specified date range for the given relation ID.
     */
    public List<GetDayOffPeriodResponse> getDaysOffPeriodsResponseList(
        UUID relationId,
        LocalDate startDate,
        LocalDate endDate
    ) {
        var daysOff = daysOffService.getOverlappingDaysOffPeriodsForRelation(relationId, startDate, endDate);
        List<GetDayOffPeriodResponse> responses = new ArrayList<>();
        for (var dayOff : daysOff) {
            GetDayOffPeriodResponse response = GetDayOffPeriodResponse.builder()
                .daysOffPeriodId(dayOff.getUuid().toString())
                .startDate(dayOff.getStartDate())
                .endDate(dayOff.getEndDate())
                .build();
            responses.add(response);
        }

        return responses;
    }

    private DaysOffPeriodEntity getDaysOffPeriodEntity(UpsertDayOffPeriodRequest request, DriverDispatcherRelationEntity relation) {
        DaysOffPeriodEntity daysOffPeriod = getOrCreateDaysOffPeriod(request.daysOffPeriodId());
        return daysOffPeriod.toBuilder()
            .startDate(request.startDate())
            .endDate(request.endDate())
            .driverDispatcherRelation(relation)
            .build();
    }

    private DaysOffPeriodEntity getOrCreateDaysOffPeriod(UUID daysOffPeriodId) {
        if (daysOffPeriodId != null) {
            return daysOffService.getDaysOffPeriodByUuid(daysOffPeriodId);
        }

        return DaysOffPeriodEntity.builder().uuid(UUID.randomUUID()).build();
    }
}
