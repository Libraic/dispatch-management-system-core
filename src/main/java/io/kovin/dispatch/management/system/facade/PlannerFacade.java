package io.kovin.dispatch.management.system.facade;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.START_DATE_BEFORE_END_DATE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.mapper.LoadObjectsCreator;
import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import io.kovin.dispatch.management.system.model.persistence.DriverDispatcherRelationEntity;
import io.kovin.dispatch.management.system.model.response.GetDayOffPeriodResponse;
import io.kovin.dispatch.management.system.model.response.GetDispatcherResponse;
import io.kovin.dispatch.management.system.model.response.GetDriverResponse;
import io.kovin.dispatch.management.system.model.response.GetVehicleMaintenanceResponse;
import io.kovin.dispatch.management.system.model.response.load.GetWorkforceDataResponse;
import io.kovin.dispatch.management.system.model.response.load.GetLoadResponse;
import io.kovin.dispatch.management.system.model.response.load.GetDispatchingDataResponse;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.DriverDispatcherRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlannerFacade {

    private final CompanyService companyService;
    private final DriverDispatcherRelationService driverDispatcherRelationService;
    private final LoadObjectsCreator loadObjectsCreator;

    private final VehicleMaintenanceFacade vehicleMaintenanceFacade;
    private final LoadFacade loadFacade;
    private final DaysOffFacade daysOffFacade;

    /**
     * Retrieves the driver loads and related planning data for a specified company within a given timeframe.
     * The method validates the company's registration status and retrieves driver-dispatcher relationships to
     * generate workforce planning data for the specified timeframe.
     *
     * @param companyUuid the unique identifier of the company whose driver loads need to be retrieved
     *                    (must not be null or empty)
     * @param startDate   the starting date of the timeframe for which the data is to be retrieved
     *                    (must not be null and should not be after {@code endDate})
     * @param endDate     the ending date of the timeframe for which the data is to be retrieved
     *                    (must not be null and should not be before {@code startDate})
     * @return a list of {@code GetWorkforcePlanningDataResponse} objects, each representing workforce planning data
     *         including dispatcher details and driver-specific planning data for the given timeframe
     * @throws DispatchManagementSystemException if the company with the given UUID is not registered or if {@code startDate} is after {@code endDate}
     */
    public List<GetDispatchingDataResponse> getDriverLoadsForTimeframe(
        String companyUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        companyService.validateTheCompanyIsRegistered(companyUuid);
        if (startDate.isAfter(endDate)) {
            throw DispatchManagementSystemException.of(START_DATE_BEFORE_END_DATE, HttpStatus.BAD_REQUEST);
        }

        List<GetDispatchingDataResponse> responses = new ArrayList<>();

        Map<DispatcherEntity, List<DriverDispatcherRelationEntity>> relations =
            driverDispatcherRelationService.findRelationsByCompanyGroupedByDispatcher(companyUuid);

        for (var entry : relations.entrySet()) {
            GetDispatcherResponse getDispatcherResponse = loadObjectsCreator.createGetDispatcherResponse(entry.getKey());
            List<GetWorkforceDataResponse> getDriverLoadResponses = new ArrayList<>();
            List<DriverDispatcherRelationEntity> sortedRelations = entry.getValue()
                .stream()
                .sorted(Comparator.comparing(relation -> relation.getDriver().getCreatedAt()))
                .toList();
            for (DriverDispatcherRelationEntity relation : sortedRelations) {
                List<GetLoadResponse> getLoadResponses = loadFacade.getLoadResponses(relation.getUuid(), startDate, endDate);
                List<GetVehicleMaintenanceResponse> getVehicleMaintenanceResponses = vehicleMaintenanceFacade.getVehicleMaintenanceResponseList(
                    relation.getUuid(),
                    startDate,
                    endDate
                );
                List<GetDayOffPeriodResponse> getDayOffPeriodResponses = daysOffFacade.getDaysOffPeriodsResponseList(
                    relation.getUuid(),
                    startDate,
                    endDate
                );
                GetDriverResponse getDriverResponse = loadObjectsCreator.createGetDriverResponse(relation.getDriver());
                GetWorkforceDataResponse getWorkforceDataResponses = GetWorkforceDataResponse.builder()
                    .relationUuid(relation.getUuid())
                    .driver(getDriverResponse)
                    .loads(getLoadResponses)
                    .vehicleMaintenanceRecords(getVehicleMaintenanceResponses)
                    .daysOffPeriods(getDayOffPeriodResponses)
                    .build();
                getDriverLoadResponses.add(getWorkforceDataResponses);
            }

            GetDispatchingDataResponse getDispatchingDataResponse = GetDispatchingDataResponse.builder()
                .dispatcher(getDispatcherResponse)
                .workforceData(getDriverLoadResponses)
                .build();
            responses.add(getDispatchingDataResponse);
        }

        return responses;
    }
}
