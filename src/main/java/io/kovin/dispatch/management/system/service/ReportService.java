package io.kovin.dispatch.management.system.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.Kpiable;
import io.kovin.dispatch.management.system.model.global.reports.loadbyload.LoadByLoadAggregationModel;
import io.kovin.dispatch.management.system.model.global.reports.loadbyload.LoadByLoadModel;
import io.kovin.dispatch.management.system.model.global.reports.financial.FinancialAggregationModel;
import io.kovin.dispatch.management.system.model.global.reports.financial.FinancialModel;
import io.kovin.dispatch.management.system.model.enums.KpiTargetEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CriteriaService criteriaService;
    private final FinancialService financialService;
    private final LoadByLoadService loadByLoadService;

    /**
     * Retrieves financial data based on the provided aggregation model. The method first identifies
     * the target entity class based on the `target` specified in the aggregation model. Then, it
     * fetches all relevant target entities for the specified company UUID and calculates financial
     * models based on the provided data.
     *
     * @param aggregationModel the financial aggregation model containing the necessary details for
     *                         retrieving and calculating financial data, such as the company UUID,
     *                         target entity type, KPI names, and loadDate range.
     * @return a list of FinancialModel objects containing calculated financial data for the selected target entities.
     */
    public List<FinancialModel> getFinancialData(FinancialAggregationModel aggregationModel) {
        Class<Kpiable> clazz = getTargetEntityClass(aggregationModel.target());
        List<Kpiable> targetEntities = criteriaService.getTargetEntitiesForKpis(clazz, aggregationModel.companyUuid());
        return financialService.getFinancialModels(targetEntities, aggregationModel);
    }

    /**
     * Retrieves a list of LoadByLoadModel data based on the provided aggregation model.
     * This method calculates the first and last day of the specified month and year,
     * retrieves the driver entities belonging to the specified company, and then
     * computes the load-by-load models for the provided time range.
     *
     * @param loadByLoadAggregationModel an aggregation model containing the company UUID, year,
     *                                    and month for which the data is being requested.
     * @return a list of LoadByLoadModel objects representing the calculated load-by-load data
     *         for the specified company and time period.
     */
    public List<LoadByLoadModel> getLoadByLoadData(LoadByLoadAggregationModel loadByLoadAggregationModel) {
        Month monthEnum = Month.valueOf(loadByLoadAggregationModel.month().toUpperCase());
        YearMonth yearMonth = YearMonth.of(loadByLoadAggregationModel.year(), monthEnum);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        List<DriverEntity> driverEntities = criteriaService.getTargetEntitiesForKpis(
            DriverEntity.class,
            loadByLoadAggregationModel.companyUuid()
        );
        return loadByLoadService.getLoadByLoadModels(driverEntities, start, end);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> getTargetEntityClass(KpiTargetEntity kpiTargetEntity) {
        return (Class<T>) DriverEntity.class;
    }
}
