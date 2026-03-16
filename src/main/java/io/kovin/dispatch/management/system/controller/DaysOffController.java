package io.kovin.dispatch.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.facade.DaysOffFacade;
import io.kovin.dispatch.management.system.model.request.UpsertDayOffPeriodRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.GetDayOffPeriodResponse;
import io.kovin.dispatch.management.system.model.response.UpsertDayOffPeriodResponse;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.service.DaysOffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/days-off")
@Slf4j
public class DaysOffController {

    private final DaysOffFacade daysOffFacade;
    private final DaysOffService daysOffService;

    @PutMapping
    public ResponseEntity<ApiResponse<UpsertDayOffPeriodResponse, ErrorResponse>> upsertDayOffPeriod(
        @RequestBody UpsertDayOffPeriodRequest upsertDayOffPeriodRequest
    ) {
        log.info("A request to upsert a day off period was received.");
        UpsertDayOffPeriodResponse upsertDayOffPeriodResponse = daysOffFacade.upsertDayOffPeriod(upsertDayOffPeriodRequest);
        return ResponseEntity.ok(ApiResponse.fromData(upsertDayOffPeriodResponse));
    }

    @GetMapping("/relations/{relationUuid}")
    public ResponseEntity<ApiResponse<List<GetDayOffPeriodResponse>, ErrorResponse>> getDaysOffPeriodsForTimeframe(
        @PathVariable(name = "relationUuid") String relationUuid,
        @RequestParam(name = "startDate") LocalDate startDate,
        @RequestParam(name = "endDate") LocalDate endDate
    ) {
        log.info(
            "A request to retrieve the Days Off periods for the Driver-Dispatcher Relation=[{}], between [{} - {}], was received.",
            relationUuid,
            startDate,
            endDate
        );
        var responses = daysOffFacade.getDaysOffPeriodsResponseList(relationUuid, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.fromData(responses));
    }

    @DeleteMapping("/{daysOffPeriodUuid}")
    public ResponseEntity<ApiResponse<Void, ErrorResponse>> deleteDaysOffPeriod(@PathVariable String daysOffPeriodUuid) {
        log.info("A request to remove the Days Off period with the UUID=[{}} was received.", daysOffPeriodUuid);
        daysOffService.deleteDaysOffPeriodByUuid(daysOffPeriodUuid);
        return ResponseEntity.noContent().build();
    }
}
