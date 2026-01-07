package io.kovin.dispatch.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.model.global.reports.financial.FinancialAggregationModel;
import io.kovin.dispatch.management.system.model.global.reports.financial.FinancialModel;
import io.kovin.dispatch.management.system.model.enums.KpiAggregationPeriod;
import io.kovin.dispatch.management.system.model.enums.KpiTargetEntity;
import io.kovin.dispatch.management.system.model.enums.KpiName;
import io.kovin.dispatch.management.system.model.global.reports.loadbyload.LoadByLoadAggregationModel;
import io.kovin.dispatch.management.system.model.global.reports.loadbyload.LoadByLoadModel;
import io.kovin.dispatch.management.system.service.ReportService;
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
@RequestMapping("api/reports")
public class ReportController {

    private final ReportService reportService;

    /**
     * The endpoint for retrieving the data required to present the Financial Reports, targeting the specified entities
     * alongside the required KPIs.
     *
     * @param companyUuid       the UUID of the Company the target entity pertains to.
     * @param target            the target entity the KPIs are calculated for.
     * @param startDate         the start mileageDate of the time interval the KPIs are calculated for.
     * @param endDate           the end mileageDate of the time interval the KPIs are calculated for.
     * @param aggregationPeriod the timeframe the KPIs will be aggregated for.
     * @param kpiNames          a list of the KPIs that should be calculated.
     * @return                  a list of Financial data.
     */
    @GetMapping("/financial")
    public ResponseEntity<List<FinancialModel>> getFinancialData(
        @RequestParam(name = "companyUuid") String companyUuid,
        @RequestParam(name = "target") String target,
        @RequestParam(name = "start") LocalDate startDate,
        @RequestParam(name = "end") LocalDate endDate,
        @RequestParam(name = "window") String aggregationPeriod,
        @RequestParam(name = "kpis") List<String> kpiNames
        ) {
        log.info(
            "A request to get the Financial data for the [{}] entity for [{} - {}] time interval was received .",
            target,
            startDate,
            endDate
        );
        FinancialAggregationModel aggregationModel = new FinancialAggregationModel(
            companyUuid,
            KpiTargetEntity.from(target),
            KpiAggregationPeriod.from(aggregationPeriod),
            KpiName.from(kpiNames),
            startDate,
            endDate
        );
        List<FinancialModel> financialModels = reportService.getFinancialData(aggregationModel);
        return ResponseEntity.status(HttpStatus.OK).body(financialModels);
    }

    /**
     * The endpoint used for retrieving the data for the loads.
     *
     * @param companyUuid the UUID of the Company the data is retrieved for.
     * @param year        the year the data is retrieved for.
     * @param month       the month the data is retrieved for.
     * @return            a list of Load-by-Load data.
     */
    @GetMapping("/load-by-load")
    public ResponseEntity<List<LoadByLoadModel>> getLoadByLoadData(
        @RequestParam(name = "companyUuid") String companyUuid,
        @RequestParam(name = "year") int year,
        @RequestParam(name = "month") String month
    ) {
        log.info("A request to get Load-by-Load data was received for year=[{}] and month=[{}].", year, month);
        LoadByLoadAggregationModel loadByLoadAggregationModel = new LoadByLoadAggregationModel(
            companyUuid,
            year,
            month
        );
        List<LoadByLoadModel> loadByLoadModels = reportService.getLoadByLoadData(loadByLoadAggregationModel);
        return ResponseEntity.status(HttpStatus.OK).body(loadByLoadModels);
    }
}
