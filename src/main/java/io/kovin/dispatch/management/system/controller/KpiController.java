package io.kovin.dispatch.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.model.global.kpi.KpiAggregationModel;
import io.kovin.dispatch.management.system.model.global.kpi.KpiModel;
import io.kovin.dispatch.management.system.model.enums.KpiAggregationPeriod;
import io.kovin.dispatch.management.system.model.enums.KpiTargetEntity;
import io.kovin.dispatch.management.system.model.enums.KpiName;
import io.kovin.dispatch.management.system.service.KpiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/kpis")
public class KpiController {

    private final KpiService kpiService;

    /**
     * The main endpoint for getting a certain KPI for a target entity.
     *
     * @param companyUuid       the UUID of the Company the target entity pertains to.
     * @param target            the target entity the KPIs are calculated for.
     * @param startDate         the start date of the time interval the KPIs are calculated for.
     * @param endDate           the end date of the time interval the KPIs are calculated for.
     * @param aggregationPeriod the timeframe the KPIs will be aggregated for.
     * @param kpiNames          a list of the KPIs that should be calculated.
     * @return                  a list of KPI data.
     */
    @GetMapping
    public ResponseEntity<List<KpiModel>> getKpis(
        @RequestParam(name = "companyUuid") String companyUuid,
        @RequestParam(name = "target") String target,
        @RequestParam(name = "start") LocalDate startDate,
        @RequestParam(name = "end") LocalDate endDate,
        @RequestParam(name = "window") String aggregationPeriod,
        @RequestParam(name = "kpis") List<String> kpiNames
        ) {
        log.info(
            "A request to get the KPIs for the [{}] entity for [{} - {}] time interval was received .",
            target,
            startDate,
            endDate
        );
        KpiAggregationModel aggregationModel = new KpiAggregationModel(
            companyUuid,
            KpiTargetEntity.from(target),
            KpiAggregationPeriod.from(aggregationPeriod),
            KpiName.from(kpiNames),
            startDate,
            endDate
        );
        List<KpiModel> kpiModels = kpiService.getKpis(aggregationModel);
        return ResponseEntity.status(HttpStatus.OK).body(kpiModels);
    }
}
